pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
    plugins {
        id("com.android.application") version "8.6.0"
        id("com.android.library") version "8.6.0"
        id("org.jetbrains.kotlin.multiplatform") version "2.0.21"
        id("org.jetbrains.kotlin.android") version "2.0.21"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
        id("org.jetbrains.compose") version "1.7.3"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
}

rootProject.name = "fitgymtrack-multiplatform"
include(":shared", ":androidApp")
