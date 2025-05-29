package com.fitgymtrack.services

import com.fitgymtrack.platform.PlatformContext
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError
import com.fitgymtrack.enums.NotificationPriority
import com.fitgymtrack.enums.NotificationType
import com.fitgymtrack.models.Notification
import com.fitgymtrack.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.days

/**
 * Sistema avanzato per pulizia e ottimizzazione notifiche
 */
class NotificationCleanupSystem private constructor(
    private val context: PlatformContext,
    private val repository: NotificationRepository
) {

    private val TAG = "NotificationCleanup"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        @Volatile
        private var INSTANCE: NotificationCleanupSystem? = null

        fun getInstance(context: PlatformContext): NotificationCleanupSystem {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationCleanupSystem(
                    context,
                    NotificationRepository(context)
                ).also { INSTANCE = it }
            }
        }

        // Configurazione cleanup
        private const val MAX_NOTIFICATIONS = 100
        private const val MAX_AGE_DAYS = 30L
        private const val CLEANUP_INTERVAL_HOURS = 6L
        private const val MAX_DUPLICATES_SAME_TYPE = 3
    }

    private var isRunning = false

    /**
     * Avvia il sistema di cleanup periodico
     */
    fun startPeriodicCleanup() {
        if (isRunning) {
            logDebug(TAG, "⚠️ Cleanup già in esecuzione")
            return
        }

        scope.launch {
            isRunning = true
            logDebug(TAG, "🚀 Avvio sistema cleanup periodico")

            try {
                while (isRunning) {
                    performCleanup()
                    delay(CLEANUP_INTERVAL_HOURS.hours)
                }
            } catch (e: Exception) {
                logError(TAG, "❌ Errore cleanup periodico: ${e.message}")
            } finally {
                isRunning = false
            }
        }
    }

    /**
     * Esegue cleanup completo
     */
    suspend fun performCleanup() {
        try {
            logDebug(TAG, "🧹 Inizio cleanup completo")

            val notifications = repository.getAllNotifications().first()
            val initialCount = notifications.size

            logDebug(TAG, "📊 Notifiche iniziali: $initialCount")

            // 1. Rimuovi notifiche scadute
            val afterExpiredCleanup = removeExpiredNotifications(notifications)
            logDebug(TAG, "🗑️ Dopo rimozione scadute: ${afterExpiredCleanup.size}")

            // 2. Rimuovi notifiche troppo vecchie
            val afterAgeCleanup = removeOldNotifications(afterExpiredCleanup)
            logDebug(TAG, "📅 Dopo rimozione vecchie: ${afterAgeCleanup.size}")

            // 3. Limita numero totale
            val afterCountLimit = limitTotalNotifications(afterAgeCleanup)
            logDebug(TAG, "📝 Dopo limite count: ${afterCountLimit.size}")

            // 4. Rimuovi duplicati
            val afterDeduplication = removeDuplicates(afterCountLimit)
            logDebug(TAG, "🔗 Dopo deduplicazione: ${afterDeduplication.size}")

            // 5. Ottimizza per performance
            val optimized = optimizeForPerformance(afterDeduplication)
            logDebug(TAG, "⚡ Dopo ottimizzazione: ${optimized.size}")

            // 6. Salva risultati se ci sono cambiamenti
            if (optimized.size != initialCount) {
                saveCleanedNotifications(optimized)
                logDebug(TAG, "✅ Cleanup completato: $initialCount → ${optimized.size}")
            } else {
                logDebug(TAG, "✅ Nessuna pulizia necessaria")
            }

        } catch (e: Exception) {
            logError(TAG, "❌ Errore durante cleanup: ${e.message}")
        }
    }

    /**
     * Rimuovi notifiche scadute
     */
    private fun removeExpiredNotifications(notifications: List<Notification>): List<Notification> {
        val now = Clock.System.now().toEpochMilliseconds()
        return notifications.filter { notification ->
            notification.expiryDate?.let { expiry ->
                now <= expiry
            } ?: true // Se non ha scadenza, mantieni
        }
    }

    /**
     * Rimuovi notifiche troppo vecchie
     */
    private fun removeOldNotifications(notifications: List<Notification>): List<Notification> {
        val cutoffTime = Clock.System.now().toEpochMilliseconds() - MAX_AGE_DAYS.days.inWholeMilliseconds

        return notifications.filter { notification ->
            // Mantieni se:
            // 1. È più recente del cutoff
            // 2. È URGENT e non letta (anche se vecchia)
            // 3. È SUBSCRIPTION_EXPIRED (importante)
            notification.timestamp > cutoffTime ||
                    (notification.priority == NotificationPriority.URGENT && !notification.isRead) ||
                    notification.type == NotificationType.SUBSCRIPTION_EXPIRED
        }
    }

    /**
     * Limita numero totale di notifiche
     */
    private fun limitTotalNotifications(notifications: List<Notification>): List<Notification> {
        if (notifications.size <= MAX_NOTIFICATIONS) {
            return notifications
        }

        // Ordina per priorità e timestamp
        val sorted = notifications.sortedWith(
            compareByDescending<Notification> { it.priority.ordinal }
                .thenByDescending { it.timestamp }
        )

        return sorted.take(MAX_NOTIFICATIONS)
    }

    /**
     * Rimuovi duplicati dello stesso tipo
     */
    private fun removeDuplicates(notifications: List<Notification>): List<Notification> {
        val result = mutableListOf<Notification>()
        val typeCount = mutableMapOf<NotificationType, Int>()

        // Ordina per timestamp decrescente (più recenti prima)
        val sorted = notifications.sortedByDescending { it.timestamp }

        for (notification in sorted) {
            val currentCount = typeCount[notification.type] ?: 0

            if (currentCount < MAX_DUPLICATES_SAME_TYPE) {
                result.add(notification)
                typeCount[notification.type] = currentCount + 1
            } else {
                logDebug(TAG, "🔗 Rimossa duplicata: ${notification.type}")
            }
        }

        return result
    }

    /**
     * Ottimizza per performance
     */
    private fun optimizeForPerformance(notifications: List<Notification>): List<Notification> {
        // Raggruppa notifiche simili se troppe
        return if (notifications.size > 50) {
            // Mantieni solo le più importanti per performance
            notifications.sortedWith(
                compareByDescending<Notification> { it.priority.ordinal }
                    .thenByDescending { !it.isRead }
                    .thenByDescending { it.timestamp }
            ).take(50)
        } else {
            notifications
        }
    }

    /**
     * Salva notifiche pulite
     */
    private suspend fun saveCleanedNotifications(notifications: List<Notification>) {
        try {
            // Implementazione specifica per salvare le notifiche pulite
            // Per ora, utilizziamo il repository esistente
            repository.clearAllNotifications()

            // Salva una per una (non ottimale, ma funziona)
            notifications.forEach { notification ->
                repository.saveNotification(notification)
            }

        } catch (e: Exception) {
            logError(TAG, "❌ Errore salva notifiche pulite: ${e.message}")
        }
    }

    /**
     * Cleanup mirato per tipo specifico
     */
    suspend fun cleanupByType(type: NotificationType) {
        try {
            logDebug(TAG, "🎯 Cleanup per tipo: $type")

            val notifications = repository.getAllNotifications().first()
            val filtered = notifications.filter { it.type != type }

            if (filtered.size != notifications.size) {
                saveCleanedNotifications(filtered)
                logDebug(TAG, "✅ Rimosse ${notifications.size - filtered.size} notifiche $type")
            }

        } catch (e: Exception) {
            logError(TAG, "❌ Errore cleanup by type: ${e.message}")
        }
    }

    /**
     * Cleanup notifiche lette vecchie
     */
    suspend fun cleanupReadNotifications(olderThanDays: Long = 7) {
        try {
            logDebug(TAG, "📖 Cleanup notifiche lette vecchie")

            val cutoffTime = Clock.System.now().toEpochMilliseconds() - olderThanDays.days.inWholeMilliseconds
            val notifications = repository.getAllNotifications().first()

            val filtered = notifications.filter { notification ->
                // Mantieni se non è letta O è più recente del cutoff
                !notification.isRead || notification.timestamp > cutoffTime
            }

            if (filtered.size != notifications.size) {
                saveCleanedNotifications(filtered)
                logDebug(TAG, "✅ Rimosse ${notifications.size - filtered.size} notifiche lette vecchie")
            }

        } catch (e: Exception) {
            logError(TAG, "❌ Errore cleanup read notifications: ${e.message}")
        }
    }

    /**
     * Statistiche sistema
     */
    suspend fun getCleanupStats(): CleanupStats {
        return try {
            val notifications = repository.getAllNotifications().first()
            val now = Clock.System.now().toEpochMilliseconds()

            CleanupStats(
                totalNotifications = notifications.size,
                readNotifications = notifications.count { it.isRead },
                unreadNotifications = notifications.count { !it.isRead },
                expiredNotifications = notifications.count { it.isExpired() },
                urgentNotifications = notifications.count { it.priority == NotificationPriority.URGENT },
                oldNotifications = notifications.count {
                    (now - it.timestamp) > MAX_AGE_DAYS.days.inWholeMilliseconds
                },
                byType = notifications.groupingBy { it.type }.eachCount(),
                averageAge = if (notifications.isNotEmpty()) {
                    notifications.map { now - it.timestamp }.average().toLong()
                } else 0L,
                lastCleanup = Clock.System.now().toEpochMilliseconds()
            )
        } catch (e: Exception) {
            logError(TAG, "❌ Errore stats: ${e.message}")
            CleanupStats()
        }
    }

    /**
     * Stop cleanup system
     */
    fun stopCleanup() {
        isRunning = false
        logDebug(TAG, "🛑 Sistema cleanup fermato")
    }
}

/**
 * Statistiche del sistema di cleanup
 */
data class CleanupStats(
    val totalNotifications: Int = 0,
    val readNotifications: Int = 0,
    val unreadNotifications: Int = 0,
    val expiredNotifications: Int = 0,
    val urgentNotifications: Int = 0,
    val oldNotifications: Int = 0,
    val byType: Map<NotificationType, Int> = emptyMap(),
    val averageAge: Long = 0L,
    val lastCleanup: Long = 0L
) {
    fun getHealthScore(): Int {
        // Calcola un punteggio di "salute" del sistema notifiche
        val score = when {
            totalNotifications == 0 -> 100
            totalNotifications > 100 -> 20
            expiredNotifications > totalNotifications / 2 -> 30
            oldNotifications > totalNotifications / 3 -> 50
            urgentNotifications > 5 -> 60
            else -> 85
        }
        return score.coerceIn(0, 100)
    }

    fun needsCleanup(): Boolean {
        return getHealthScore() < 70
    }
}