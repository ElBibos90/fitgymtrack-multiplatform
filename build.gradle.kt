plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// Git versioning utilities (mantenuto dal tuo setup originale)
fun getGitCommitCount(): Int {
    return try {
        val output = "git rev-list --count HEAD".runCommand()
        output.trim().toInt()
    } catch (e: Exception) {
        1
    }
}

fun getGitSha(): String {
    return try {
        "git rev-parse --short HEAD".runCommand().trim()
    } catch (e: Exception) {
        "dev"
    }
}

fun String.runCommand(): String {
    return ProcessBuilder(*split(" ").toTypedArray())
        .directory(rootDir)
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
}

// Global configuration per tutti i moduli
allprojects {
    repositories {
        google()
        mavenCentral()
    }
    
    // Shared version configuration
    extra["gitCommitCount"] = getGitCommitCount()
    extra["gitSha"] = getGitSha()
    extra["versionName"] = "0.0.${getGitCommitCount()}-${getGitSha()}"
    
    // Configurazione comune Android
    extra["compileSdk"] = 35
    extra["targetSdk"] = 35
    extra["minSdk"] = 24
    extra["namespace"] = "com.fitgymtrack.app"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}