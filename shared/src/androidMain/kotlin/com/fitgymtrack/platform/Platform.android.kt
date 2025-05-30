package com.fitgymtrack.platform

import android.os.Build
import android.util.Log

/**
 * Android implementation - Context wrapper
 */
actual class PlatformContext(val androidContext: android.content.Context)

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

// === LOGGING FUNCTIONS ===
/**
 * Android implementation del logging - DEBUG level
 */
actual fun platformLogDebug(tag: String, message: String) {
    Log.d(tag, message)
}

/**
 * Android implementation del logging - ERROR level
 */
actual fun platformLogError(tag: String, message: String) {
    Log.e(tag, message)
}

// === VERSION & DEVICE INFO FUNCTIONS ===
/**
 * Android implementation - Version name
 */
actual fun getVersionName(): String {
    return try {
        AndroidBuildConfig.versionName
    } catch (e: Exception) {
        "Unknown"
    }
}

/**
 * Android implementation - Version code
 */
actual fun getVersionCode(): Int {
    return try {
        AndroidBuildConfig.versionCode
    } catch (e: Exception) {
        1
    }
}

/**
 * Android implementation - Platform name
 */
actual fun getPlatformName(): String {
    return "Android"
}

/**
 * Android implementation - Device manufacturer
 */
actual fun getDeviceManufacturer(): String {
    return Build.MANUFACTURER ?: "Unknown"
}

/**
 * Android implementation - Device model
 */
actual fun getDeviceModel(): String {
    return Build.MODEL ?: "Unknown"
}

/**
 * Android implementation - OS version
 */
actual fun getOSVersion(): String {
    return Build.VERSION.RELEASE ?: "Unknown"
}