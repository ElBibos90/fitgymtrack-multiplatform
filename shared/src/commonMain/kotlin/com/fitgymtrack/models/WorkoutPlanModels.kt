package com.fitgymtrack.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Rappresenta una scheda di allenamento
 */
@Serializable
data class WorkoutPlan(
    val id: Int,
    val nome: String,
    val descrizione: String?,
    @SerialName("data_creazione")
    val dataCreazione: String?,
    val esercizi: List<WorkoutExercise> = emptyList()
)

@Serializable
data class WorkoutExercise(
    val id: Int,
    @SerialName("scheda_esercizio_id")
    val schedaEsercizioId: Int? = null,
    val nome: String,
    @SerialName("gruppo_muscolare")
    val gruppoMuscolare: String? = null,
    val attrezzatura: String? = null,
    val descrizione: String? = null,
    val serie: Int = 3,
    val ripetizioni: Int = 10,
    val peso: Double = 0.0,
    val ordine: Int = 0,
    @SerialName("tempo_recupero")
    val tempoRecupero: Int = 90,
    val note: String? = null,
    @SerialName("set_type")
    val setType: String = "normal",
    @SerialName("linked_to_previous")
    val linkedToPreviousInt: Int = 0,
    @SerialName("is_isometric")
    val isIsometricInt: Int = 0
) {
    val linkedToPrevious: Boolean
        get() = linkedToPreviousInt > 0

    val isIsometric: Boolean
        get() = isIsometricInt > 0
}

fun WorkoutExercise.safeCopy(
    id: Int = this.id,
    schedaEsercizioId: Int? = this.schedaEsercizioId,
    nome: String = this.nome,
    gruppoMuscolare: String? = this.gruppoMuscolare,
    attrezzatura: String? = this.attrezzatura,
    descrizione: String? = this.descrizione,
    serie: Int = this.serie,
    ripetizioni: Int = this.ripetizioni,
    peso: Double = this.peso,
    ordine: Int = this.ordine,
    tempoRecupero: Int = this.tempoRecupero,
    note: String? = this.note,
    setType: String = this.setType,
    linkedToPrevious: Boolean = this.linkedToPrevious,
    isIsometric: Boolean = this.isIsometric
): WorkoutExercise {
    return WorkoutExercise(
        id = id,
        schedaEsercizioId = schedaEsercizioId,
        nome = nome,
        gruppoMuscolare = gruppoMuscolare,
        attrezzatura = attrezzatura,
        descrizione = descrizione,
        serie = serie,
        ripetizioni = ripetizioni,
        peso = peso,
        ordine = ordine,
        tempoRecupero = tempoRecupero,
        note = note,
        setType = setType.ifEmpty { "normal" },
        linkedToPreviousInt = if (linkedToPrevious) 1 else 0,
        isIsometricInt = if (isIsometric) 1 else 0
    )
}

fun createWorkoutExercise(
    id: Int,
    schedaEsercizioId: Int? = null,
    nome: String,
    gruppoMuscolare: String? = null,
    attrezzatura: String? = null,
    descrizione: String? = null,
    serie: Int = 3,
    ripetizioni: Int = 10,
    peso: Double = 0.0,
    ordine: Int = 0,
    tempoRecupero: Int = 90,
    note: String? = null,
    setType: String = "normal",
    linkedToPrevious: Boolean = false,
    isIsometric: Boolean = false
): WorkoutExercise {
    return WorkoutExercise(
        id = id,
        schedaEsercizioId = schedaEsercizioId,
        nome = nome,
        gruppoMuscolare = gruppoMuscolare,
        attrezzatura = attrezzatura,
        descrizione = descrizione,
        serie = serie,
        ripetizioni = ripetizioni,
        peso = peso,
        ordine = ordine,
        tempoRecupero = tempoRecupero,
        note = note,
        setType = setType.ifEmpty { "normal" },
        linkedToPreviousInt = if (linkedToPrevious) 1 else 0,
        isIsometricInt = if (isIsometric) 1 else 0
    )
}

@Serializable
data class CreateWorkoutPlanRequest(
    @SerialName("user_id")
    val userId: Int,
    val nome: String,
    val descrizione: String?,
    val esercizi: List<WorkoutExerciseRequest>
)

@Serializable
data class WorkoutExerciseRequest(
    val id: Int,
    val serie: Int,
    val ripetizioni: Int,
    val peso: Double,
    val ordine: Int,
    @SerialName("tempo_recupero")
    val tempoRecupero: Int = 90,
    val note: String? = null,
    @SerialName("set_type")
    val setType: String = "normal",
    @SerialName("linked_to_previous")
    val linkedToPrevious: Int = 0
)

@Serializable
data class UpdateWorkoutPlanRequest(
    @SerialName("scheda_id")
    val schedaId: Int,
    val nome: String,
    val descrizione: String?,
    val esercizi: List<WorkoutExerciseRequest>,
    val rimuovi: List<WorkoutExerciseToRemove>? = null
)

@Serializable
data class WorkoutExerciseToRemove(
    val id: Int
)

@Serializable
data class WorkoutPlanResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class WorkoutPlansResponse(
    val success: Boolean,
    val schede: List<WorkoutPlan>
)

@Serializable
data class WorkoutExercisesResponse(
    val success: Boolean,
    val esercizi: List<WorkoutExercise>
)
