package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Service per le API della cronologia workout
 * Implementazione Ktor multiplatform
 */
class WorkoutHistoryApiService(private val httpClient: HttpClient) {

    /**
     * Get the workout history for a user
     */
    suspend fun getWorkoutHistory(userId: Int): Map<String, Any> {
        return httpClient.get("get_allenamenti_standalone.php") {
            parameter("user_id", userId)
        }.body()
    }

    /**
     * Get the completed series for a specific workout
     */
    suspend fun getWorkoutSeriesDetail(allenamentoId: Int): GetCompletedSeriesResponse {
        return httpClient.get("get_completed_series_standalone.php") {
            parameter("allenamento_id", allenamentoId)
        }.body()
    }

    /**
     * Delete a specific series
     */
    suspend fun deleteCompletedSeries(request: DeleteSeriesRequest): SeriesOperationResponse {
        return httpClient.delete("delete_completed_series.php") {
            setBody(request)
        }.body()
    }

    /**
     * Update a specific series
     */
    suspend fun updateCompletedSeries(request: UpdateSeriesRequest): SeriesOperationResponse {
        return httpClient.put("update_completed_series.php") {
            setBody(request)
        }.body()
    }

    /**
     * Delete an entire workout
     */
    suspend fun deleteWorkout(request: Map<String, Int>): SeriesOperationResponse {
        return httpClient.post("delete_allenamento_standalone.php") {
            setBody(request)
        }.body()
    }
}