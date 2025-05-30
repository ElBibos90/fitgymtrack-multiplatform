package com.fitgymtrack.utils

/**
 * Utility per formattare i pesi
 */
object WeightFormatter {

    /**
     * Formatta un peso per la visualizzazione
     * @param weight Il peso da formattare
     * @return Stringa formattata del peso
     */
    fun formatWeight(weight: Float): String {
        return when {
            weight == weight.toInt().toFloat() -> {
                weight.toInt().toString()
            }
            weight % 0.5f == 0f -> {
                formatDecimal(weight, 1)
            }
            weight % 0.25f == 0f -> {
                formatDecimal(weight, 2)
            }
            else -> {
                formatDecimal(weight, 2)
            }
        }
    }

    /**
     * Formatta un peso per la visualizzazione con unità
     */
    fun formatWeightWithUnit(weight: Float): String {
        return "${formatWeight(weight)} kg"
    }

    /**
     * Converte una stringa in peso (Float)
     */
    fun parseWeight(weightString: String): Float? {
        return try {
            weightString.replace(",", ".").toFloatOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Arrotonda un peso al valore più vicino utilizzabile in palestra
     */
    fun roundToUsableWeight(weight: Float): Float {
        return when {
            weight < 5f -> (weight * 4).toInt() / 4f
            weight < 20f -> (weight * 2).toInt() / 2f
            weight < 50f -> weight.toInt().toFloat()
            else -> ((weight / 2.5f).toInt() * 2.5f)
        }
    }

    /**
     * Calcola il prossimo incremento di peso suggerito
     */
    fun getWeightIncrement(currentWeight: Float): Float {
        return when {
            currentWeight < 10f -> 0.5f
            currentWeight < 30f -> 1.25f
            currentWeight < 60f -> 2.5f
            currentWeight < 100f -> 5f
            else -> 10f
        }
    }

    /**
     * Calcola il peso target per la progressione
     */
    fun calculateProgressionWeight(currentWeight: Float): Float {
        val increment = getWeightIncrement(currentWeight)
        return roundToUsableWeight(currentWeight + increment)
    }
}

/**
 * Funzione multiplatform per formattare un decimale con n cifre decimali
 */
expect fun formatDecimal(value: Float, decimals: Int): String
