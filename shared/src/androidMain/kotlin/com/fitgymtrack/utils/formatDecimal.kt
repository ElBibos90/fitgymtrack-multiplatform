package com.fitgymtrack.utils

actual fun formatDecimal(value: Float, decimals: Int): String {
    return String.format("%.${decimals}f", value)
}
