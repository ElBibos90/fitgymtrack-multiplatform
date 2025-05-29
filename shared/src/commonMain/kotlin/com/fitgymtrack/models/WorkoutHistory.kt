package com.fitgymtrack.models

import kotlinx.serialization.SerialName
import kotlinx.datetime.*
import com.fitgymtrack.platform.logDebug
import com.fitgymtrack.platform.logError

/**
 * Rappresenta un elemento nella cronologia degli allenamenti
 */
data class WorkoutHistory(
    val id: Int,
    @SerialName("scheda_id")
    val schedaId: Int,
    @SerialName("data_allenamento")
    val dataAllenamento: String,
    @SerialName("durata_totale")
    val durataTotale: Int? = null,
    val note: String? = null,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("scheda_nome")
    val schedaNome: String? = null,
    // Proprietà calcolate
    val isCompleted: Boolean = durataTotale != null && durataTotale > 0
) {
    // Proprietà calcolata per la data formattata
    val formattedDate: String
        get() {
            return try {
                // Parse datetime string using kotlinx.datetime
                val instant = Instant.parse(dataAllenamento.replace(" ", "T") + "Z")
                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

                // Format as dd/MM/yyyy HH:mm
                val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
                val month = localDateTime.monthNumber.toString().padStart(2, '0')
                val year = localDateTime.year
                val hour = localDateTime.hour.toString().padStart(2, '0')
                val minute = localDateTime.minute.toString().padStart(2, '0')

                "$day/$month/$year $hour:$minute"
            } catch (e: Exception) {
                // Fallback: try to parse as yyyy-MM-dd format
                try {
                    val parts = dataAllenamento.split(" ")
                    if (parts.isNotEmpty()) {
                        val dateParts = parts[0].split("-")
                        if (dateParts.size == 3) {
                            val timePart = if (parts.size > 1) " ${parts[1].substring(0, minOf(5, parts[1].length))}" else ""
                            "${dateParts[2]}/${dateParts[1]}/${dateParts[0]}$timePart"
                        } else {
                            dataAllenamento
                        }
                    } else {
                        dataAllenamento
                    }
                } catch (e2: Exception) {
                    dataAllenamento
                }
            }
        }

    // Proprietà calcolata per la durata formattata
    val formattedDuration: String
        get() {
            return if (durataTotale != null && durataTotale > 0) {
                val hours = durataTotale / 60
                val minutes = durataTotale % 60

                if (hours > 0) {
                    "$hours h ${String.format("%02d", minutes)} min"
                } else {
                    "$minutes min"
                }
            } else {
                "N/D"
            }
        }

    companion object {
        /**
         * Crea un oggetto WorkoutHistory da una mappa di valori
         */
        fun fromMap(map: Map<String, Any>): WorkoutHistory {
            // Estrai e converte correttamente i valori numerici
            val idValue = map["id"]
            val schedaIdValue = map["scheda_id"]
            val userIdValue = map["user_id"]

            // Ottieni l'ID allenamento gestendo sia Number che String
            val id = when (idValue) {
                is Number -> idValue.toInt()
                is String -> idValue.toIntOrNull() ?: 0
                else -> {
                    logError("WorkoutHistory", "ID allenamento di tipo sconosciuto: ${idValue?.javaClass?.name}")
                    0
                }
            }

            // Ottieni l'ID scheda gestendo sia Number che String
            val schedaId = when (schedaIdValue) {
                is Number -> schedaIdValue.toInt()
                is String -> schedaIdValue.toIntOrNull() ?: 0
                else -> {
                    logError("WorkoutHistory", "ID scheda di tipo sconosciuto: ${schedaIdValue?.javaClass?.name}")
                    0
                }
            }

            // Ottieni l'ID utente gestendo sia Number che String
            val userId = when (userIdValue) {
                is Number -> userIdValue.toInt()
                is String -> userIdValue.toIntOrNull() ?: 0
                else -> {
                    logError("WorkoutHistory", "ID utente di tipo sconosciuto: ${userIdValue?.javaClass?.name}")
                    0
                }
            }

            // Ottieni la durata totale gestendo sia Number che String
            val durataTotaleValue = map["durata_totale"]
            val durataTotale = when (durataTotaleValue) {
                is Number -> durataTotaleValue.toInt()
                is String -> durataTotaleValue.toIntOrNull()
                else -> null
            }

            // Log per debug
            logDebug("WorkoutHistory", "Conversione allenamento: id=$id (orig=$idValue), " +
                    "schedaId=$schedaId (orig=$schedaIdValue), userId=$userId (orig=$userIdValue)")

            return WorkoutHistory(
                id = id,
                schedaId = schedaId,
                dataAllenamento = map["data_allenamento"]?.toString() ?: "",
                durataTotale = durataTotale,
                note = map["note"]?.toString(),
                userId = userId,
                schedaNome = map["scheda_nome"]?.toString()
            )
        }
    }
}