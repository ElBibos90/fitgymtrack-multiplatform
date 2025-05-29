// === commonMain/Platform.kt ===
package com.fitgymtrack.platform

/**
 * Platform information interface per testare il setup multiplatform
 */
expect class Platform() {
    val name: String
    val version: String
}

/**
 * Function per ottenere informazioni platform corrente
 */
expect fun getPlatform(): Platform

/**
 * Logging multiplatform di test
 */
expect fun logDebug(tag: String, message: String)

/**
 * Test function per verificare il setup
 */
fun getWelcomeMessage(): String {
    val platform = getPlatform()
    logDebug("FitGymTrack", "Running on ${platform.name} ${platform.version}")
    return "🚀 FitGymTrack Multiplatform\n" +
            "Platform: ${platform.name}\n" +
            "Version: ${platform.version}\n" +
            "✅ Setup Successful!"
}