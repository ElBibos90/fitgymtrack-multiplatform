package com.fitgymtrack.utils

import platform.Foundation.*

actual fun formatDecimal(value: Float, decimals: Int): String {
    val formatter = NSNumberFormatter()
    formatter.numberStyle = NSNumberFormatterDecimalStyle
    formatter.minimumFractionDigits = decimals.toLong().toInt()
    formatter.maximumFractionDigits = decimals.toLong().toInt()
    return formatter.stringFromNumber(value.toDouble()) ?: value.toString()
}
