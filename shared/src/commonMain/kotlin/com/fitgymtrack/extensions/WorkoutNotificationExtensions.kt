package com.fitgymtrack.extensions

import com.fitgymtrack.platform.PlatformContext
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError
import com.fitgymtrack.models.WorkoutPlan
import com.fitgymtrack.services.NotificationIntegrationService
import com.fitgymtrack.utils.SubscriptionLimitChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Estensioni per integrare notifiche automatiche nei workflow esistenti
 */
object WorkoutNotificationExtensions {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private const val TAG = "WorkoutNotificationExt"

    /**
     * Notifica completamento workout
     */
    fun notifyWorkoutCompleted(
        context: PlatformContext,
        workoutName: String,
        durationMinutes: Long,
        exerciseCount: Int
    ) {
        scope.launch {
            try {
                logDebug(TAG, "💪 Workout completato: $workoutName")

                val service = NotificationIntegrationService.getInstance(context)
                service.notifyWorkoutCompleted(
                    workoutName = workoutName,
                    duration = durationMinutes,
                    exerciseCount = exerciseCount
                )

                // Check for achievements
                checkWorkoutAchievements(context, workoutName, durationMinutes)

            } catch (e: Exception) {
                logError(TAG, "❌ Errore notifica workout: ${e.message}")
            }
        }
    }

    /**
     * Controlla e notifica traguardi raggiunti
     */
    private fun checkWorkoutAchievements(
        context: PlatformContext,
        workoutName: String,
        durationMinutes: Long
    ) {
        scope.launch {
            try {
                val service = NotificationIntegrationService.getInstance(context)

                // Esempio traguardi
                when {
                    durationMinutes >= 60 -> {
                        service.notifyAchievement(
                            "Warrior!",
                            "Hai completato un allenamento di oltre 1 ora! 💪"
                        )
                    }
                    durationMinutes >= 30 -> {
                        service.notifyAchievement(
                            "Costanza!",
                            "Altro allenamento di 30+ minuti completato! 🔥"
                        )
                    }
                }

            } catch (e: Exception) {
                logError(TAG, "❌ Errore check achievements: ${e.message}")
            }
        }
    }

    /**
     * Controlla limiti prima di creare workout/esercizio
     * FIX: Gestisce correttamente le suspend functions
     */
    fun checkLimitsBeforeCreation(
        context: PlatformContext,
        resourceType: String, // "workouts" o "custom_exercises"
        onLimitReached: () -> Unit,
        onCanProceed: () -> Unit
    ) {
        scope.launch {
            try {
                logDebug(TAG, "🔍 Controllo limiti per: $resourceType")

                // FIX: Chiamate suspend corrette
                val (limitReached, currentCount, maxAllowed) = try {
                    when (resourceType) {
                        "workouts" -> SubscriptionLimitChecker.canCreateWorkout()
                        "custom_exercises" -> SubscriptionLimitChecker.canCreateCustomExercise()
                        else -> Triple(false, 0, null)
                    }
                } catch (e: Exception) {
                    logError(TAG, "❌ Errore chiamata SubscriptionLimitChecker: ${e.message}")
                    // In caso di errore, permettiamo la creazione
                    Triple(false, 0, null)
                }

                if (limitReached && maxAllowed != null) {
                    logDebug(TAG, "🚨 Limite raggiunto: $currentCount/$maxAllowed")

                    // Crea notifica invece di mostrare banner
                    val service = NotificationIntegrationService.getInstance(context)
                    service.checkResourceLimits(
                        resourceType = resourceType,
                        currentCount = currentCount,
                        maxAllowed = maxAllowed,
                        isLimitReached = true
                    )

                    // FIX: Callback per UI sul Main thread
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onLimitReached()
                    }
                } else {
                    logDebug(TAG, "✅ Può procedere: $currentCount/${maxAllowed ?: "∞"}")
                    // FIX: Callback per UI sul Main thread
                    withContext(Dispatchers.Main) {
                        onCanProceed()
                    }
                }

            } catch (e: Exception) {
                logError(TAG, "❌ Errore controllo limiti: ${e.message}")
                // In caso di errore, permetti la creazione
                onCanProceed()
            }
        }
    }

    /**
     * Integrazione con WorkoutPlan creazione
     */
    fun onWorkoutPlanCreated(context: PlatformContext, workoutPlan: WorkoutPlan) {
        scope.launch {
            try {
                // Usa il campo corretto del WorkoutPlan (probabilmente 'title' invece di 'name')
                val planName = workoutPlan.nome ?: "Workout Plan"
                logDebug(TAG, "📝 Workout plan creato: $planName")

                // Qui potresti aggiungere logica per:
                // - Notifiche di completamento creazione
                // - Suggerimenti per primo allenamento
                // - Achievement per numero schede create

            } catch (e: Exception) {
                logError(TAG, "❌ Errore on workout created: ${e.message}")
            }
        }
    }

    /**
     * Notifica per promemoria allenamento
     */
    fun scheduleWorkoutReminder(
        context: PlatformContext,
        workoutName: String,
        daysSinceLastWorkout: Int
    ) {
        if (daysSinceLastWorkout >= 3) {
            scope.launch {
                try {
                    logDebug(TAG, "⏰ Promemoria allenamento dopo $daysSinceLastWorkout giorni")

                    val service = NotificationIntegrationService.getInstance(context)
                    // Qui useresti REMINDER type quando sarà implementato

                } catch (e: Exception) {
                    logError(TAG, "❌ Errore promemoria: ${e.message}")
                }
            }
        }
    }
}

/**
 * Helper per integrazioni rapide
 */
object NotificationHelper {

    /**
     * Quick method per notificare subscription issues
     */
    fun checkSubscriptionAndNotify(context: PlatformContext) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val service = NotificationIntegrationService.getInstance(context)
                service.checkAppUpdates()

            } catch (e: Exception) {
                logError("NotificationHelper", "❌ Errore quick check: ${e.message}")
            }
        }
    }

    /**
     * Test method per creare notifiche di esempio
     */
    fun createTestNotifications(context: PlatformContext) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val service = NotificationIntegrationService.getInstance(context)

                // Test workout completion
                service.notifyWorkoutCompleted(
                    workoutName = "Push Day",
                    duration = 45,
                    exerciseCount = 8
                )

                // Test achievement
                service.notifyAchievement(
                    "Primo Traguardo!",
                    "Hai completato il tuo primo allenamento! 🎉"
                )

            } catch (e: Exception) {
                logError("NotificationHelper", "❌ Errore test notifications: ${e.message}")
            }
        }
    }
}