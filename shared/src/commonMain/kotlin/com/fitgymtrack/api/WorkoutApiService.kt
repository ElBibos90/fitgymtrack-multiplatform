package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

/**
 * Service per le API dei workout/schede
 * Implementazione Ktor multiplatform
 */
class WorkoutApiService(private val httpClient: HttpClient) {

    /**
     * Recupera tutte le schede dell'utente
     */
    suspend fun getWorkoutPlans(userId: Int): WorkoutPlansResponse {
        return httpClient.get("schede_standalone.php") {
            parameter("user_id", userId)
        }.body()
    }

    /**
     * Recupera gli esercizi di una scheda specifica
     */
    suspend fun getWorkoutExercises(schedaId: Int): WorkoutExercisesResponse {
        return httpClient.get("schede_standalone.php") {
            parameter("scheda_id", schedaId)
        }.body()
    }

    /**
     * Crea una nuova scheda
     */
    suspend fun createWorkoutPlan(request: CreateWorkoutPlanRequest): WorkoutPlanResponse {
        return httpClient.post("create_scheda_standalone.php") {
            setBody(request)
        }.body()
    }

    /**
     * Aggiorna una scheda esistente
     */
    suspend fun updateWorkoutPlan(request: UpdateWorkoutPlanRequest): WorkoutPlanResponse {
        return httpClient.put("schede_standalone.php") {
            setBody(request)
        }.body()
    }

    /**
     * Elimina una scheda - Form URL encoded
     */
    suspend fun deleteWorkoutPlan(schedaId: Int): WorkoutPlanResponse {
        return httpClient.submitForm(
            url = "schede_standalone.php",
            formParameters = Parameters.build {
                append("scheda_id", schedaId.toString())
            }
        ) {
            method = HttpMethod.Delete
        }.body()
    }

    /**
     * Recupera tutti gli esercizi disponibili per la creazione/modifica schede
     */
    suspend fun getAvailableExercises(userId: Int): ExercisesResponse {
        return httpClient.get("get_esercizi_standalone.php") {
            parameter("user_id", userId)
        }.body()
    }
}

/**
 * Risposte per gli esercizi disponibili
 */
data class ExercisesResponse(
    val success: Boolean,
    val esercizi: List<ExerciseItem>
)

/**
 * Modello per esercizio disponibile
 */
data class ExerciseItem(
    val id: Int,
    val nome: String,
    val descrizione: String?,
    val gruppo_muscolare: String?,
    val attrezzatura: String?,
    val immagine_url: String?,
    val is_isometric: Boolean = false,
    val serie_default: Int? = null,
    val ripetizioni_default: Int? = null,
    val peso_default: Double? = null
)