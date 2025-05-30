package com.fitgymtrack.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Modello per rappresentare un esercizio restituito dall'API esercizi.php
 */
@Serializable
data class Exercise(
    val id: Int,
    val nome: String,
    val descrizione: String,
    // ✅ FIX: @SerializedName → @SerialName
    @SerialName("immagine_url")
    val immagineUrl: String,
    @SerialName("gruppo_muscolare")
    val gruppoMuscolare: String,
    val attrezzatura: String,
    @SerialName("is_isometric")
    val isIsometric: Int = 0, // 0 = false, 1 = true
    @SerialName("equipment_type_id")
    val equipmentTypeId: Int? = null,
    val status: String? = null
) {
    // ✅ MANTIENI: Business logic multiplatform
    val isIsometricBool: Boolean
        get() = isIsometric > 0
}