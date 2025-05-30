plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.application") // in androidApp
}

kotlin {
    androidTarget()
}

android {
    namespace = "com.fitgymtrack.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fitgymtrack.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1-devw"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
	implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("org.jetbrains.compose.material3:material3:1.7.3")
    implementation("org.jetbrains.compose.ui:ui:1.7.3")
    implementation("org.jetbrains.compose.ui:ui-tooling:1.7.3")
    implementation("io.coil-kt:coil-compose:2.7.0")
}
