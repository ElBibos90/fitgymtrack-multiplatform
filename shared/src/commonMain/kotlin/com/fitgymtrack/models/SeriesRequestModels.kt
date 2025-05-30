package com.fitgymtrack.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelli per le richieste di eliminazione e aggiornamento delle serie
 */

/**
 * Richiesta per eliminare una serie completata
 */
@Serializable
data class DeleteSeriesRequest(
    @SerialName("serie_id")
    val serieId: String
)

/**
 * Richiesta per aggiornare una serie completata
 */
@Serializable
data class UpdateSeriesRequest(
    @SerialName("serie_id")
    val serieId: String,
    val peso: Float,
    val ripetizioni: Int,
    @SerialName("tempo_recupero")
    val tempoRecupero: Int? = null,
    val note: String? = null
)

/**
 * Risposta generica per le operazioni sulle serie
 */
@Serializable
data class SeriesOperationResponse(
    val success: Boolean,
    val message: String
)
