package com.fitgymtrack.services

import com.fitgymtrack.platform.PlatformContext
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError
import com.fitgymtrack.enums.NotificationType
import com.fitgymtrack.models.Subscription
import com.fitgymtrack.models.User
import com.fitgymtrack.utils.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

/**
 * Servizio per l'integrazione automatica delle notifiche
 * Sostituisce i banner esistenti con notifiche centralizzate
 */
class NotificationIntegrationService private constructor(
    private val context: PlatformContext
) {

    private val TAG = "NotificationIntegrationService"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val notificationManager = NotificationManager.getInstance(context)

    companion object {
        @Volatile
        private var INSTANCE: NotificationIntegrationService? = null

        fun getInstance(context: PlatformContext): NotificationIntegrationService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationIntegrationService(context)
                    .also { INSTANCE = it }
            }
        }
    }

    // === SUBSCRIPTION NOTIFICATIONS ===

    /**
     * Controlla e crea notifiche per subscription scadute/in scadenza
     * SOSTITUISCE: SubscriptionExpiredBanner + SubscriptionExpiryWarningBanner
     */
    fun checkSubscriptionStatus(subscription: Subscription?) {
        if (subscription == null) return

        scope.launch {
            try {
                logDebug(TAG, "🔍 Controllo stato subscription: ${subscription.planName}")

                // Solo per piani a pagamento
                if (subscription.price > 0.0 && subscription.end_date != null) {
                    val daysRemaining = calculateDaysRemaining(subscription.end_date)

                    when {
                        daysRemaining < 0 -> {
                            // Subscription SCADUTA
                            logDebug(TAG, "🚨 Subscription scaduta da ${Math.abs(daysRemaining)} giorni")
                            createSubscriptionExpiredNotification(subscription.planName)
                        }
                        daysRemaining == 0 -> {
                            // Scade OGGI
                            logDebug(TAG, "⚠️ Subscription scade oggi")
                            createSubscriptionExpiryNotification(0, subscription.planName)
                        }
                        daysRemaining in 1..7 -> {
                            // Scade tra 1-7 giorni
                            logDebug(TAG, "⚠️ Subscription scade tra $daysRemaining giorni")
                            createSubscriptionExpiryNotification(daysRemaining, subscription.planName)
                        }
                        else -> {
                            logDebug(TAG, "✅ Subscription attiva ($daysRemaining giorni rimanenti)")
                        }
                    }
                }

            } catch (e: Exception) {
                logError(TAG, "❌ Errore controllo subscription: ${e.message}")
            }
        }
    }

    /**
     * Crea notifica per subscription scaduta
     * SOSTITUISCE: SubscriptionExpiredBanner
     */
    private fun createSubscriptionExpiredNotification(planName: String) {
        notificationManager.createNotification(
            type = NotificationType.SUBSCRIPTION_EXPIRED,
            data = mapOf("planName" to planName)
        )
    }

    /**
     * Crea notifica per subscription in scadenza
     * SOSTITUISCE: SubscriptionExpiryWarningBanner
     */
    private fun createSubscriptionExpiryNotification(daysRemaining: Int, planName: String) {
        notificationManager.createNotification(
            type = NotificationType.SUBSCRIPTION_EXPIRY,
            data = mapOf(
                "daysRemaining" to daysRemaining,
                "planName" to planName
            )
        )
    }

    // === LIMIT NOTIFICATIONS ===

    /**
     * Controlla e crea notifiche per limiti raggiunti
     * SOSTITUISCE: SubscriptionLimitBanner
     */
    fun checkResourceLimits(
        resourceType: String,
        currentCount: Int,
        maxAllowed: Int?,
        isLimitReached: Boolean
    ) {
        scope.launch {
            try {
                logDebug(TAG, "🔍 Controllo limiti: $resourceType ($currentCount/${maxAllowed ?: "∞"})")

                if (isLimitReached && maxAllowed != null) {
                    logDebug(TAG, "🚨 Limite raggiunto per $resourceType")
                    createLimitReachedNotification(resourceType, maxAllowed)
                }

            } catch (e: Exception) {
                logError(TAG, "❌ Errore controllo limiti: ${e.message}")
            }
        }
    }

    /**
     * Crea notifica per limite raggiunto
     * SOSTITUISCE: SubscriptionLimitBanner
     */
    private fun createLimitReachedNotification(resourceType: String, maxAllowed: Int) {
        notificationManager.createNotification(
            type = NotificationType.LIMIT_REACHED,
            data = mapOf(
                "resourceType" to resourceType,
                "maxAllowed" to maxAllowed
            )
        )
    }

    // === WORKOUT NOTIFICATIONS ===

    /**
     * Crea notifica per allenamento completato
     */
    fun notifyWorkoutCompleted(
        workoutName: String,
        duration: Long,
        exerciseCount: Int
    ) {
        scope.launch {
            try {
                logDebug(TAG, "💪 Allenamento completato: $workoutName")

                notificationManager.createNotification(
                    type = NotificationType.WORKOUT_COMPLETED,
                    data = mapOf(
                        "workoutName" to workoutName,
                        "duration" to duration,
                        "exerciseCount" to exerciseCount
                    )
                )

            } catch (e: Exception) {
                logError(TAG, "❌ Errore notifica workout: ${e.message}")
            }
        }
    }

    /**
     * Crea notifica per traguardo raggiunto
     */
    fun notifyAchievement(
        achievementTitle: String,
        achievementDescription: String
    ) {
        scope.launch {
            try {
                logDebug(TAG, "🏆 Traguardo raggiunto: $achievementTitle")

                notificationManager.createNotification(
                    type = NotificationType.ACHIEVEMENT,
                    data = mapOf(
                        "title" to achievementTitle,
                        "description" to achievementDescription
                    )
                )

            } catch (e: Exception) {
                logError(TAG, "❌ Errore notifica achievement: ${e.message}")
            }
        }
    }

    // === APP LIFECYCLE NOTIFICATIONS ===

    /**
     * Controlla aggiornamenti app e crea notifica se disponibile
     */
    fun checkAppUpdates() {
        scope.launch {
            try {
                logDebug(TAG, "🔄 Controllo aggiornamenti app")
                notificationManager.startPeriodicChecks()

            } catch (e: Exception) {
                logError(TAG, "❌ Errore controllo app updates: ${e.message}")
            }
        }
    }

    /**
     * Crea notifica di benvenuto per nuovi utenti
     */
    fun notifyWelcomeMessage(user: User) {
        scope.launch {
            try {
                logDebug(TAG, "👋 Benvenuto: ${user.username}")

                notificationManager.createNotification(
                    type = NotificationType.DIRECT_MESSAGE,
                    data = mapOf(
                        "title" to "Benvenuto in FitGymTrack!",
                        "message" to "Ciao ${user.username}! Siamo felici che tu sia qui. Inizia subito a creare la tua prima scheda di allenamento!",
                        "priority" to "NORMAL"
                    )
                )

            } catch (e: Exception) {
                logError(TAG, "❌ Errore notifica benvenuto: ${e.message}")
            }
        }
    }

    // === CLEANUP & MAINTENANCE ===

    /**
     * Avvia pulizia automatica delle notifiche
     */
    fun startPeriodicCleanup() {
        scope.launch {
            try {
                logDebug(TAG, "🧹 Avvio pulizia periodica")
                notificationManager.cleanup()

            } catch (e: Exception) {
                logError(TAG, "❌ Errore cleanup: ${e.message}")
            }
        }
    }

    /**
     * Inizializza il servizio con controlli periodici
     */
    fun initialize() {
        scope.launch {
            try {
                logDebug(TAG, "🚀 Inizializzazione NotificationIntegrationService")

                // Avvia controlli periodici
                checkAppUpdates()
                startPeriodicCleanup()

                logDebug(TAG, "✅ NotificationIntegrationService inizializzato")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore inizializzazione: ${e.message}")
            }
        }
    }

    // === UTILITY METHODS ===

    /**
     * Calcola giorni rimanenti da una data string
     * FIXED: Usa kotlinx.datetime invece di SimpleDateFormat
     */
    private fun calculateDaysRemaining(endDateString: String): Int {
        return try {
            // Prova a parsare diversi formati di data
            val endDate = try {
                // Formato standard: "yyyy-MM-dd HH:mm:ss"
                val dateTime = if (endDateString.contains(" ")) {
                    endDateString.replace(" ", "T") + "Z"
                } else {
                    // Formato solo data: "yyyy-MM-dd"
                    "${endDateString}T00:00:00Z"
                }
                Instant.parse(dateTime)
            } catch (e: Exception) {
                // Fallback: prova a parsare come LocalDate
                try {
                    val localDate = LocalDate.parse(endDateString.split(" ")[0])
                    localDate.atTime(0, 0).toInstant(TimeZone.UTC)
                } catch (e2: Exception) {
                    logError(TAG, "❌ Impossibile parsare data: $endDateString")
                    return Int.MAX_VALUE
                }
            }

            val currentDate = Clock.System.now()
            val diffInDays = (endDate.toEpochMilliseconds() - currentDate.toEpochMilliseconds()).days.inWholeDays

            diffInDays.toInt()

        } catch (e: Exception) {
            logError(TAG, "❌ Errore calcolo giorni rimanenti: ${e.message}")
            Int.MAX_VALUE
        }
    }

    /**
     * Verifica se una notifica di un certo tipo esiste già
     */
    private suspend fun notificationExists(type: NotificationType): Boolean {
        return try {
            // Implementazione per verificare se esiste già una notifica di questo tipo
            // Per ora ritorna false per permettere sempre nuove notifiche
            false
        } catch (e: Exception) {
            logError(TAG, "❌ Errore verifica notifica esistente: ${e.message}")
            false
        }
    }

    /**
     * Logging dettagliato per debugging
     */
    fun logStatus() {
        scope.launch {
            try {
                logDebug(TAG, "📊 Status NotificationIntegrationService:")
                logDebug(TAG, "   - Servizio attivo: ✅")
                logDebug(TAG, "   - Context: ${context::class.simpleName}")
                logDebug(TAG, "   - NotificationManager: ${notificationManager::class.simpleName}")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore log status: ${e.message}")
            }
        }
    }
}