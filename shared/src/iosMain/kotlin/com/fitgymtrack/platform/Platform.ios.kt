package com.fitgymtrack.platform

import platform.UIKit.UIDevice

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

/**
 * iOS implementation del logging (usando println per semplicità)
 * In produzione si potrebbe usare CocoaLumberjack o altro
 */
actual fun logDebug(tag: String, message: String) {
    println("[$tag] $message")
}