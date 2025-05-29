import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

// Retrieve shared configuration from root project
val compileSdk: Int by rootProject.extra
val targetSdk: Int by rootProject.extra
val minSdk: Int by rootProject.extra
val namespace: String by rootProject.extra

kotlin {
    // === ANDROID TARGET ===
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    // === iOS TARGETS ===
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    // === SOURCE SETS CONFIGURATION ===
    sourceSets {
        // === COMMON MAIN ===
        commonMain.dependencies {
            // === COMPOSE MULTIPLATFORM ===
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            
            // === COROUTINES ===
            implementation(libs.kotlinx.coroutines.core)
            
            // === SERIALIZATION ===
            implementation(libs.kotlinx.serialization.json)
            
            // === NETWORKING (KTOR) ===
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            
            // === DATE/TIME ===
            implementation(libs.kotlinx.datetime)
            
            // === STORAGE ===
            implementation(libs.androidx.datastore.preferences.core)
            
            // === NAVIGATION ===
            implementation(libs.androidx.navigation.compose)
            
            // === LIFECYCLE & VIEWMODEL ===
            implementation(libs.androidx.lifecycle.viewmodel.compose)
        }
        
        // === ANDROID MAIN ===
        androidMain.dependencies {
            // === ANDROID COROUTINES ===
            implementation(libs.kotlinx.coroutines.android)
            
            // === ANDROID CORE ===
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.activity.compose)
            
            // === NETWORKING (RETROFIT for Android compatibility) ===
            implementation(libs.retrofit)
            implementation(libs.retrofit.converter.gson)
            implementation(libs.okhttp.logging.interceptor)
            implementation(libs.gson)
            
            // === KTOR ANDROID ENGINE ===
            implementation(libs.ktor.client.android)
            
            // === IMAGE LOADING ===
            implementation(libs.coil.compose)
            
            // === DATASTORE ANDROID ===
            implementation(libs.androidx.datastore.preferences)
            
            // === UI COMPONENTS ===
            implementation(libs.compose.material.dialogs)
            implementation(libs.compose.numberpicker)
            
            // === WINDOW SIZE ===
            implementation(libs.androidx.compose.material3.window.size)
        }
        
        // === iOS MAIN ===
        iosMain.dependencies {
            // === KTOR iOS ENGINE ===
            implementation(libs.ktor.client.darwin)
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
    namespace = namespace
    compileSdk = compileSdk
    
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = minSdk
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