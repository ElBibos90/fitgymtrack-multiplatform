import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    // === ANDROID TARGET ===
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // === iOS TARGETS DISABLED FOR WINDOWS ===
    // TODO: Re-enable in FASE 5 on Mac/cloud environment

    // === SOURCE SETS CONFIGURATION ===
    sourceSets {
        // === COMMON MAIN (MINIMAL) ===
        commonMain.dependencies {
            // === COMPOSE MULTIPLATFORM BASIC ===
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            // === COROUTINES ===
            implementation(libs.kotlinx.coroutines.core)

            // === SERIALIZATION ===
            implementation(libs.kotlinx.serialization.json)

            // === DATE/TIME ===
            implementation(libs.kotlinx.datetime)
        }

        // === ANDROID MAIN ===
        androidMain.dependencies {
            // === ANDROID COROUTINES ===
            implementation(libs.kotlinx.coroutines.android)

            // === ANDROID CORE ===
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.activity.compose)

            // === NAVIGATION (ANDROID ONLY FOR NOW) ===
            implementation(libs.androidx.navigation.compose)

            // === LIFECYCLE & VIEWMODEL (ANDROID ONLY FOR NOW) ===
            implementation(libs.androidx.lifecycle.viewmodel.compose)

            // === NETWORKING (RETROFIT + KTOR) ===
            implementation(libs.retrofit)
            implementation(libs.retrofit.converter.gson)
            implementation(libs.okhttp.logging.interceptor)
            implementation(libs.gson)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // === IMAGE LOADING ===
            implementation(libs.coil.compose)

            // === DATASTORE ===
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.datastore.preferences)

            // === UI COMPONENTS ===
            implementation(libs.compose.material.dialogs)
            implementation(libs.compose.numberpicker)
            implementation(libs.androidx.compose.material3.window.size)
        }

        // === COMMON TEST ===
        commonTest.dependencies {
            implementation(libs.junit)
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            implementation(kotlin("test"))
        }
    }
}

// === ANDROID CONFIGURATION ===
android {
    namespace = "com.fitgymtrack.app"
    compileSdk = 35

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 24
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

// === COMPOSE CONFIGURATION ===
compose.experimental {
    web.application {}
}