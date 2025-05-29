package com.fitgymtrack.api

import com.fitgymtrack.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Service per le API relative agli esercizi personalizzati
 * Implementazione Ktor multiplatform
 */
class UserExerciseApiService(private val httpClient: HttpClient) {

    /**
     * Recupera tutti gli esercizi creati dall'utente
     */
    suspend fun getUserExercises(userId: Int): UserExercisesResponse {
        return httpClient.get("user_exercises_standalone.php") {
            parameter("user_id", userId)
        }.body()
    }

    /**
     * Crea un nuovo esercizio personalizzato
     */
    suspend fun createUserExercise(request: CreateUserExerciseRequest): UserExerciseResponse {
        return httpClient.post("custom_exercise_standalone.php") {
            setBody(request)
        }.body()
    }

    /**
     * Aggiorna un esercizio esistente
     */
    suspend fun updateUserExercise(request: UpdateUserExerciseRequest): UserExerciseResponse {
        return httpClient.put("custom_exercise_standalone.php") {
            setBody(request)
        }.body()
    }

    /**
     * Elimina un esercizio
     */
    suspend fun deleteUserExercise(request: DeleteUserExerciseRequest): UserExerciseResponse {
        return httpClient.delete("user_exercises_standalone.php") {
            setBody(request)
        }.body()
    }
}