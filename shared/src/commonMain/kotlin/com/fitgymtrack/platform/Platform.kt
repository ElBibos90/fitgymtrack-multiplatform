package com.fitgymtrack.platform

/**
 * Platform-specific context type
 */
expect class PlatformContext

/**
 * Platform information interface
 */
expect class Platform() {
    val name: String
    val version: String
}

/**
 * Function per ottenere informazioni platform corrente
 */
expect fun getPlatform(): Platform

// === LOGGING FUNCTIONS ===
/**
 * Logging multiplatform - DEBUG level
 */
expect fun platformLogDebug(tag: String, message: String)

/**
 * Logging multiplatform - ERROR level  
 */
expect fun platformLogError(tag: String, message: String)

// === VERSION & DEVICE INFO FUNCTIONS ===
/**
 * Ottiene il nome della versione app
 */
expect fun getVersionName(): String

/**
 * Ottiene il codice versione app
 */
expect fun getVersionCode(): Int

/**
 * Ottiene il nome della piattaforma
 */
expect fun getPlatformName(): String

/**
 * Ottiene il produttore del dispositivo
 */
expect fun getDeviceManufacturer(): String

/**
 * Ottiene il modello del dispositivo
 */
expect fun getDeviceModel(): String

/**
 * Ottiene la versione del sistema operativo
 */
expect fun getOSVersion(): String

// === CONVENIENCE FUNCTIONS ===
/**
 * Test function per verificare il setup
 */
fun getWelcomeMessage(): String {
    val platform = getPlatform()
    platformLogDebug("FitGymTrack", "Running on ${platform.name} ${platform.version}")
    return "🚀 FitGymTrack Multiplatform\n" +
            "Platform: ${platform.name}\n" +
            "Version: ${platform.version}\n" +
            "✅ Setup Successful!"
}

/**
 * Convenient logging aliases (use these in your code)
 */
fun logDebug(tag: String, message: String) = platformLogDebug(tag, message)
fun logError(tag: String, message: String) = platformLogError(tag, message)