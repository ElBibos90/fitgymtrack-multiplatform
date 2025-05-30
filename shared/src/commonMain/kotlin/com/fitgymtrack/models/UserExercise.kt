package com.fitgymtrack.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Rappresenta un esercizio personalizzato creato dall'utente
 */
@Serializable
data class UserExercise(
    val id: Int,
    val nome: String,
    @SerialName("gruppo_muscolare")
    val gruppoMuscolare: String,
    val descrizione: String? = null,
    val attrezzatura: String? = null,
    @SerialName("is_isometric")
    val isIsometricInt: Int = 0,
    @SerialName("created_by_user_id")
    val createdByUserId: Int,
    val status: String = "pending_review",
    @SerialName("immagine_url")
    val immagineUrl: String? = null
) {
    // Proprietà calcolata per convertire Int a Boolean
    val isIsometric: Boolean
        get() = isIsometricInt > 0
}

/**
 * Classe per la richiesta di creazione di un nuovo esercizio
 */
@Serializable
data class CreateUserExerciseRequest(
    val nome: String,
    @SerialName("gruppo_muscolare")
    val gruppoMuscolare: String,
    val descrizione: String? = null,
    val attrezzatura: String? = null,
    @SerialName("is_isometric")
    val isIsometric: Boolean = false,
    @SerialName("created_by_user_id")
    val createdByUserId: Int,
    val status: String = "pending_review"
)

/**
 * Classe per la richiesta di aggiornamento di un esercizio esistente
 */
@Serializable
data class UpdateUserExerciseRequest(
    val id: Int,
    val nome: String,
    @SerialName("gruppo_muscolare")
    val gruppoMuscolare: String,
    val descrizione: String? = null,
    val attrezzatura: String? = null,
    @SerialName("is_isometric")
    val isIsometric: Boolean = false,
    @SerialName("user_id")
    val userId: Int
)

/**
 * Risposta generica per le operazioni sugli esercizi
 */
@Serializable
data class UserExerciseResponse(
    val success: Boolean,
    val message: String,
    @SerialName("exercise_id")
    val exerciseId: Int? = null
)

/**
 * Risposta per l'elenco degli esercizi
 */
@Serializable
data class UserExercisesResponse(
    val success: Boolean,
    val exercises: List<UserExercise>? = null,
    val message: String? = null
)

/**
 * Richiesta per l'eliminazione di un esercizio
 */
@Serializable
data class DeleteUserExerciseRequest(
    @SerialName("exercise_id")
    val exerciseId: Int,
    @SerialName("user_id")
    val userId: Int
)
