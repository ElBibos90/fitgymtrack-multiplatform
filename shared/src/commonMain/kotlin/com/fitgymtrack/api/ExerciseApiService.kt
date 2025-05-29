package com.fitgymtrack.api

import com.fitgymtrack.models.Exercise
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Service per l'API degli esercizi
 * Implementazione Ktor multiplatform
 */
class ExerciseApiService(private val httpClient: HttpClient) {

    /**
     * Recupera tutti gli esercizi disponibili
     */
    suspend fun getExercises(): List<Exercise> {
        return httpClient.get("esercizi.php").body()
    }

    /**
     * Recupera un esercizio specifico per ID
     */
    suspend fun getExercise(id: Int): Exercise {
        return httpClient.get("esercizi.php") {
            parameter("id", id)
        }.body()
    }
}