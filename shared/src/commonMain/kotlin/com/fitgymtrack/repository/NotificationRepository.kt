package com.fitgymtrack.repository

import com.fitgymtrack.platform.PlatformContext
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError
import com.fitgymtrack.api.ApiClient
import com.fitgymtrack.api.AppVersionCheckRequest
import com.fitgymtrack.api.NotificationApiService
import com.fitgymtrack.enums.NotificationPriority
import com.fitgymtrack.models.AppUpdateInfo
import com.fitgymtrack.models.AppVersionResponse
import com.fitgymtrack.models.Notification
import com.fitgymtrack.models.NotificationStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository per gestione notifiche con storage + API
 * MIGRATO: Context → PlatformContext, Log → platform logging
 * FIXED: Errori specifici di mutation e type inference
 */
class NotificationRepository(
    private val context: PlatformContext,
    private val apiService: NotificationApiService = ApiClient.notificationApiService
) {

    private val TAG = "NotificationRepository"

    // ✅ FIX: Explicit type parameters for MutableStateFlow
    private val notifications = MutableStateFlow(emptyList<Notification>())
    private val lastVersionCheck = MutableStateFlow(0L)
    private val lastMessagesCheck = MutableStateFlow(0L)
    private val lastCleanup = MutableStateFlow(0L)
    private val dismissedNotifications = MutableStateFlow(emptySet<String>())

    // ✅ FIX: Multiplatform time constants
    private companion object {
        const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L
        const val WEEK_IN_MILLIS = 7L * DAY_IN_MILLIS
        const val MONTH_IN_MILLIS = 30L * DAY_IN_MILLIS
    }

    // === NOTIFICHE LOCALI ===

    /**
     * Salva una notifica nel storage
     * FIX: Corretta la logica di mutation della lista
     */
    suspend fun saveNotification(notification: Notification) {
        withContext(Dispatchers.IO) {
            try {
                logDebug(TAG, "💾 Salvando notifica: ${notification.type} - ${notification.title}")

                // ✅ FIX: Crea una nuova lista invece di modificare quella esistente
                val currentList = notifications.value
                val newList = buildList {
                    // Aggiungi tutte le notifiche esistenti tranne quelle duplicate
                    addAll(currentList.filter { !(it.type == notification.type && it.source == notification.source) })
                    // Aggiungi la nuova notifica
                    add(notification)
                }

                // Ordina per timestamp (più recenti prima)
                val sortedNotifications = newList.sortedByDescending { it.timestamp }

                // Mantieni solo le ultime 100 notifiche per evitare overflow
                val limitedNotifications = sortedNotifications.take(100)

                // ✅ FIX: Assegna la nuova lista
                notifications.value = limitedNotifications

                logDebug(TAG, "✅ Notifica salvata con successo. Totale: ${limitedNotifications.size}")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore salvando notifica: ${e.message}")
            }
        }
    }

    /**
     * Recupera tutte le notifiche
     */
    fun getAllNotifications(): Flow<List<Notification>> {
        return notifications.map { notificationList ->
            try {
                // Filtra notifiche scadute
                val validNotifications = notificationList.filterNot { it.isExpired() }

                logDebug(TAG, "📱 Caricate ${validNotifications.size} notifiche dal storage")
                validNotifications

            } catch (e: Exception) {
                logError(TAG, "❌ Errore caricando notifiche: ${e.message}")
                emptyList()
            }
        }
    }

    /**
     * Recupera solo le notifiche non lette
     */
    fun getUnreadNotifications(): Flow<List<Notification>> {
        return getAllNotifications().map { notificationList ->
            notificationList.filter { !it.isRead }
        }
    }

    /**
     * Conta le notifiche non lette
     */
    fun getUnreadCount(): Flow<Int> {
        return getUnreadNotifications().map { it.size }
    }

    /**
     * Marca una notifica come letta
     * FIX: Corretta la logica di update
     */
    suspend fun markAsRead(notificationId: String) {
        withContext(Dispatchers.IO) {
            try {
                logDebug(TAG, "📖 Marcando come letta: $notificationId")

                // ✅ FIX: Crea nuova lista con modifica
                val currentList = notifications.value
                val updatedList = currentList.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(isRead = true)
                    } else {
                        notification
                    }
                }

                notifications.value = updatedList
                logDebug(TAG, "✅ Notifica marcata come letta")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore marcando come letta: ${e.message}")
            }
        }
    }

    /**
     * Marca tutte le notifiche come lette
     * FIX: Corretta la logica di update
     */
    suspend fun markAllAsRead() {
        withContext(Dispatchers.IO) {
            try {
                logDebug(TAG, "📖 Marcando tutte come lette")

                // ✅ FIX: Crea nuova lista con tutte marcate come lette
                val currentList = notifications.value
                val updatedList = currentList.map { it.copy(isRead = true) }

                notifications.value = updatedList
                logDebug(TAG, "✅ Tutte le notifiche marcate come lette")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore marcando tutte come lette: ${e.message}")
            }
        }
    }

    /**
     * Elimina una notifica
     * FIX: Corretta la logica di deletion
     */
    suspend fun deleteNotification(notificationId: String) {
        withContext(Dispatchers.IO) {
            try {
                logDebug(TAG, "🗑️ Eliminando notifica: $notificationId")

                // ✅ FIX: Filtra per creare nuova lista senza la notifica
                val currentList = notifications.value
                val filteredList = currentList.filter { it.id != notificationId }

                notifications.value = filteredList
                logDebug(TAG, "✅ Notifica eliminata")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore eliminando notifica: ${e.message}")
            }
        }
    }

    // === VERSION CHECK ===

    /**
     * Verifica aggiornamenti app (con cache di 24 ore)
     */
    suspend fun checkAppUpdates(forceCheck: Boolean = false): Result<AppUpdateInfo?> {
        return withContext(Dispatchers.IO) {
            try {
                // Controlla cache
                if (!forceCheck && !shouldCheckVersion()) {
                    logDebug(TAG, "⏰ Version check in cache, skip")
                    return@withContext Result.success(null)
                }

                logDebug(TAG, "🔄 Controllo aggiornamenti app...")

                // TODO: Get platform-specific version info through expect/actual
                val currentVersionName = "2.0.0"
                val currentVersionCode = 20

                val request = AppVersionCheckRequest(
                    current_version = currentVersionName,
                    current_version_code = currentVersionCode,
                    platform = "android",
                    device_info = null
                )

                // Mock response per testing
                val mockResponse = AppVersionResponse(
                    success = true,
                    update_available = true,
                    latest_version = "2.1.0",
                    latest_version_code = currentVersionCode + 1,
                    is_critical = false,
                    changelog = "• Nuove statistiche avanzate\n• Miglioramenti UI\n• Correzioni bug",
                    play_store_url = "https://play.google.com/store/apps/details?id=com.fitgymtrack"
                )

                // Aggiorna cache
                updateVersionCheckTimestamp()

                if (mockResponse.update_available && mockResponse.latest_version_code > currentVersionCode) {
                    val updateInfo = AppUpdateInfo(
                        newVersion = mockResponse.latest_version,
                        newVersionCode = mockResponse.latest_version_code,
                        currentVersion = currentVersionName,
                        currentVersionCode = currentVersionCode,
                        changelog = mockResponse.changelog,
                        isCritical = mockResponse.is_critical,
                        playStoreUrl = mockResponse.play_store_url
                    )

                    logDebug(TAG, "🆕 Aggiornamento disponibile: ${updateInfo.newVersion}")
                    Result.success(updateInfo)
                } else {
                    logDebug(TAG, "✅ App aggiornata")
                    Result.success(null)
                }

            } catch (e: Exception) {
                logError(TAG, "❌ Errore version check: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Verifica se è necessario controllare la versione
     */
    private suspend fun shouldCheckVersion(): Boolean {
        val lastCheck = lastVersionCheck.value
        val now = System.currentTimeMillis()
        return (now - lastCheck) > DAY_IN_MILLIS
    }

    /**
     * Aggiorna timestamp ultimo version check
     */
    private suspend fun updateVersionCheckTimestamp() {
        lastVersionCheck.value = System.currentTimeMillis()
    }

    // === UTILITY METHODS ===

    /**
     * Pulisce notifiche vecchie e scadute
     * FIX: Corretta la logica di filtering
     */
    suspend fun cleanupOldNotifications() {
        withContext(Dispatchers.IO) {
            try {
                logDebug(TAG, "🧹 Pulizia notifiche vecchie...")

                val currentList = notifications.value
                val now = System.currentTimeMillis()
                val thirtyDaysAgo = now - MONTH_IN_MILLIS

                // ✅ FIX: Crea nuova lista filtrata
                val validNotifications = currentList.filter { notification ->
                    !notification.isExpired() &&
                            (notification.timestamp > thirtyDaysAgo ||
                                    (notification.priority == NotificationPriority.URGENT && !notification.isRead))
                }

                if (validNotifications.size != currentList.size) {
                    notifications.value = validNotifications
                    val removed = currentList.size - validNotifications.size
                    logDebug(TAG, "🧹 Rimosse $removed notifiche vecchie")
                }

                // Aggiorna timestamp cleanup
                lastCleanup.value = now

            } catch (e: Exception) {
                logError(TAG, "❌ Errore durante cleanup: ${e.message}")
            }
        }
    }

    /**
     * Ottieni statistiche per debugging
     */
    suspend fun getNotificationStats(): NotificationStats {
        return withContext(Dispatchers.IO) {
            try {
                val notificationList = notifications.value
                val lastCleanupTime = lastCleanup.value

                NotificationStats(
                    totalNotifications = notificationList.size,
                    unreadCount = notificationList.count { !it.isRead },
                    byType = notificationList.groupingBy { it.type }.eachCount(),
                    byPriority = notificationList.groupingBy { it.priority }.eachCount(),
                    expiredCount = notificationList.count { it.isExpired() },
                    lastCleanup = lastCleanupTime
                )
            } catch (e: Exception) {
                logError(TAG, "❌ Errore stats: ${e.message}")
                NotificationStats(0, 0, emptyMap(), emptyMap(), 0, null)
            }
        }
    }

    /**
     * Reset completo (per testing)
     * FIX: Corrette tutte le assegnazioni
     */
    suspend fun clearAllNotifications() {
        withContext(Dispatchers.IO) {
            try {
                logDebug(TAG, "🗑️ RESET: Eliminando tutte le notifiche")
                notifications.value = emptyList()
                lastVersionCheck.value = 0L
                lastMessagesCheck.value = 0L
                lastCleanup.value = 0L
                dismissedNotifications.value = emptySet()
                logDebug(TAG, "✅ Tutte le notifiche eliminate")
            } catch (e: Exception) {
                logError(TAG, "❌ Errore durante reset: ${e.message}")
            }
        }
    }
}