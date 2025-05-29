package com.fitgymtrack.platform

import android.os.Build
import android.util.Log

/**
 * Android implementation of Platform
 */
actual class Platform {
    actual val name: String = "Android"
    actual val version: String = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
}

/**
 * Android implementation per ottenere platform info
 */
actual fun getPlatform(): Platform = Platform()

/**
 * Android implementation del logging
 */
actual fun logDebug(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun logError(tag: String, message: String) {
    android.util.Log.e(tag, message)
}