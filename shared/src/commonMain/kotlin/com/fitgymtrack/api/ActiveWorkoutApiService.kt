package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Service per le API relative agli allenamenti attivi
 * Implementazione Ktor multiplatform
 */
class ActiveWorkoutApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://fitgymtrack.com/api/"
) {
    /**
     * Inizia un nuovo allenamento
     */
    suspend fun startWorkout(request: StartWorkoutRequest): StartWorkoutResponse {
        return httpClient.post("${baseUrl}start_active_workout_standalone.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Recupera gli esercizi di una scheda specifica
     */
    suspend fun getWorkoutExercises(schedaId: Int): WorkoutExercisesResponse {
        return httpClient.get("${baseUrl}schede_standalone.php") {
            parameter("scheda_id", schedaId)
        }.body()
    }

    /**
     * Recupera le serie completate per un allenamento
     */
    suspend fun getCompletedSeries(allenamentoId: Int): GetCompletedSeriesResponse {
        return httpClient.get("${baseUrl}get_completed_series_standalone.php") {
            parameter("allenamento_id", allenamentoId)
        }.body()
    }

    /**
     * Salva una serie completata
     */
    suspend fun saveCompletedSeries(request: SaveCompletedSeriesRequest): SaveCompletedSeriesResponse {
        return httpClient.post("${baseUrl}save_completed_series.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Completa un allenamento
     */
    suspend fun completeWorkout(request: CompleteWorkoutRequest): CompleteWorkoutResponse {
        return httpClient.post("${baseUrl}complete_allenamento_standalone.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * Elimina un allenamento
     */
    suspend fun deleteWorkout(allenamentoId: Int): SeriesOperationResponse {
        return httpClient.post("${baseUrl}delete_allenamento_standalone.php") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("allenamento_id" to allenamentoId))
        }.body()
    }
}