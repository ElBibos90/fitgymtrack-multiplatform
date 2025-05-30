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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

/**
 * Servizio per l'integrazione automatica delle notifiche
 */
class NotificationIntegrationService private constructor(
    private val context: PlatformContext
) {

    private val TAG = "NotificationIntegrationService"
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val notificationManager = NotificationManager.getInstance(context)

    companion object {
        private var INSTANCE: NotificationIntegrationService? = null
        private val mutex = Mutex()

        suspend fun getInstance(context: PlatformContext): NotificationIntegrationService {
            return mutex.withLock {
                INSTANCE ?: NotificationIntegrationService(context)
                    .also { INSTANCE = it }
            }
        }
    }

    // === SUBSCRIPTION NOTIFICATIONS ===

    fun checkSubscriptionStatus(subscription: Subscription?) {
        if (subscription == null) return

        scope.launch {
            try {
                logDebug(TAG, "🔍 Controllo stato subscription: ${subscription.planName}")

                if (subscription.price > 0.0 && subscription.end_date != null) {
                    val daysRemaining = calculateDaysRemaining(subscription.end_date)

                    when {
                        daysRemaining < 0 -> {
                            logDebug(TAG, "🚨 Subscription scaduta da ${kotlin.math.abs(daysRemaining)} giorni")
                            createSubscriptionExpiredNotification(subscription.planName)
                        }
                        daysRemaining == 0 -> {
                            logDebug(TAG, "⚠️ Subscription scade oggi")
                            createSubscriptionExpiryNotification(0, subscription.planName)
                        }
                        daysRemaining in 1..7 -> {
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

    private fun createSubscriptionExpiredNotification(planName: String) {
        notificationManager.createNotification(
            type = NotificationType.SUBSCRIPTION_EXPIRED,
            data = mapOf("planName" to planName)
        )
    }

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

    fun initialize() {
        scope.launch {
            try {
                logDebug(TAG, "🚀 Inizializzazione NotificationIntegrationService")

                checkAppUpdates()
                startPeriodicCleanup()

                logDebug(TAG, "✅ NotificationIntegrationService inizializzato")

            } catch (e: Exception) {
                logError(TAG, "❌ Errore inizializzazione: ${e.message}")
            }
        }
    }

    private fun calculateDaysRemaining(endDateString: String): Int {
        return try {
            val endDate = try {
                val dateTime = if (endDateString.contains(" ")) {
                    endDateString.replace(" ", "T") + "Z"
                } else {
                    "${endDateString}T00:00:00Z"
                }
                Instant.parse(dateTime)
            } catch (e: Exception) {
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

    private suspend fun notificationExists(type: NotificationType): Boolean {
        return try {
            false
        } catch (e: Exception) {
            logError(TAG, "❌ Errore verifica notifica esistente: ${e.message}")
            false
        }
    }

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
