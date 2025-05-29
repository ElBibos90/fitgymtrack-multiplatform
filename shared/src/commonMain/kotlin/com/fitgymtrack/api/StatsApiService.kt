package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Service per le API delle statistiche utente
 * Implementazione Ktor multiplatform
 */
class StatsApiService(private val httpClient: HttpClient) {

    /**
     * Recupera le statistiche generali dell'utente
     */
    suspend fun getUserStats(userId: Int): UserStatsResponse {
        return httpClient.get("android_user_stats.php") {
            parameter("user_id", userId)
        }.body()
    }

    /**
     * Recupera le statistiche per un periodo specifico
     */
    suspend fun getPeriodStats(
        userId: Int,
        period: String,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<PeriodStats> {
        return httpClient.get("android_period_stats.php") {
            parameter("user_id", userId)
            parameter("period", period)
            startDate?.let { parameter("start_date", it) }
            endDate?.let { parameter("end_date", it) }
        }.body()
    }

    /**
     * Confronta le statistiche tra periodi
     */
    suspend fun getStatsComparison(
        userId: Int,
        periodType: String,
        currentPeriod: String,
        previousPeriod: String
    ): ApiResponse<StatsComparison> {
        return httpClient.get("android_stats_comparison.php") {
            parameter("user_id", userId)
            parameter("period_type", periodType)
            parameter("current_period", currentPeriod)
            parameter("previous_period", previousPeriod)
        }.body()
    }

    /**
     * Recupera i record personali dell'utente
     */
    suspend fun getPersonalRecords(
        userId: Int,
        exerciseId: Int? = null
    ): ApiResponse<List<PersonalRecord>> {
        return httpClient.get("android_personal_records.php") {
            parameter("user_id", userId)
            exerciseId?.let { parameter("exercise_id", it) }
        }.body()
    }

    /**
     * Recupera gli achievement dell'utente
     */
    suspend fun getAchievements(userId: Int): ApiResponse<List<Achievement>> {
        return httpClient.get("android_achievements.php") {
            parameter("user_id", userId)
        }.body()
    }

    /**
     * Recupera la frequenza degli allenamenti
     */
    suspend fun getWorkoutFrequency(
        userId: Int,
        period: String = "month"
    ): ApiResponse<WorkoutFrequency> {
        return httpClient.get("android_workout_frequency.php") {
            parameter("user_id", userId)
            parameter("period", period)
        }.body()
    }

    /**
     * Aggiorna un obiettivo/goal dell'utente
     */
    suspend fun updateUserGoal(request: UpdateGoalRequest): ApiResponse<String> {
        return httpClient.post("android_update_goal.php") {
            setBody(request)
        }.body()
    }

    /**
     * Calcola le statistiche aggregate
     */
    suspend fun calculateStats(request: CalculateStatsRequest): ApiResponse<UserStats> {
        return httpClient.post("android_calculate_stats.php") {
            setBody(request)
        }.body()
    }
}

/**
 * Richiesta per aggiornare un obiettivo
 */
@Serializable
data class UpdateGoalRequest(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("goal_type")
    val goalType: String,
    @SerialName("target_value")
    val targetValue: Int,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null
)

/**
 * Richiesta per calcolare statistiche
 */
@Serializable
data class CalculateStatsRequest(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("recalculate_all")
    val recalculateAll: Boolean = false,
    @SerialName("include_achievements")
    val includeAchievements: Boolean = true
)