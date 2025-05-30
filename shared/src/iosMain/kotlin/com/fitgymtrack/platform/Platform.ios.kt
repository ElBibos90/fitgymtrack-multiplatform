package com.fitgymtrack.platform

import platform.UIKit.UIDevice
import platform.Foundation.NSBundle

/**
 * iOS implementation - Context placeholder
 * In iOS non esiste Context come Android, quindi usiamo una class vuota
 */
actual class PlatformContext()

/**
 * iOS implementation of Platform
 */
actual class Platform {
    actual val name: String = UIDevice.currentDevice.systemName() ?: "iOS"
    actual val version: String = UIDevice.currentDevice.systemVersion() ?: "Unknown"
}

/**
 * iOS implementation per ottenere platform info
 */
actual fun getPlatform(): Platform = Platform()

// === LOGGING FUNCTIONS ===

/**
 * iOS implementation del logging - DEBUG level
 * (usando println per semplicità)
 * In produzione si potrebbe usare CocoaLumberjack o altro
 */
actual fun platformLogDebug(tag: String, message: String) {
    println("[$tag] DEBUG: $message")
}

/**
 * iOS implementation del logging - ERROR level
 * (usando println con prefix ERROR)
 */
actual fun platformLogError(tag: String, message: String) {
    println("[$tag] ERROR: $message")
}

// === VERSION & DEVICE INFO FUNCTIONS ===

/**
 * iOS implementation - Version name
 */
actual fun getVersionName(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
}

/**
 * iOS implementation - Version code
 */
actual fun getVersionCode(): Int {
    return (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)?.toIntOrNull() ?: 1
}

/**
 * iOS implementation - Platform name
 */
actual fun getPlatformName(): String {
    return UIDevice.currentDevice.systemName() ?: "iOS"
}

/**
 * iOS implementation - Device manufacturer
 */
actual fun getDeviceManufacturer(): String {
    return "Apple"
}

/**
 * iOS implementation - Device model
 */
actual fun getDeviceModel(): String {
    return UIDevice.currentDevice.model() ?: "Unknown"
}

/**
 * iOS implementation - OS version
 */
actual fun getOSVersion(): String {
    return UIDevice.currentDevice.systemVersion() ?: "Unknown"
}
