import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.library") // in shared
}


kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

                implementation("io.ktor:ktor-client-core:2.3.9")

                implementation("io.ktor:ktor-client-logging:2.3.9")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
				
				implementation("io.ktor:ktor-client-auth:2.3.9")                    
				implementation("io.ktor:ktor-client-content-negotiation:2.3.9") 


                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
                implementation("org.jetbrains.compose.foundation:foundation:1.7.3")
                implementation("org.jetbrains.compose.material3:material3:1.7.3")
                implementation("org.jetbrains.compose.ui:ui:1.7.3")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
                implementation("androidx.activity:activity-compose:1.10.1")
                implementation("androidx.navigation:navigation-compose:2.9.0")
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
				implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.compose.ui:ui:1.6.4")
                implementation("androidx.compose.material:material:1.6.4")
                implementation("io.ktor:ktor-client-okhttp:2.3.9")

                implementation("com.squareup.retrofit2:retrofit:2.11.0")
                implementation("com.squareup.retrofit2:converter-gson:2.11.0")
                implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
                implementation("com.google.code.gson:gson:2.11.0")

                implementation("io.ktor:ktor-client-android:3.0.3")

                implementation("io.coil-kt:coil-compose:2.7.0")

                implementation("androidx.datastore:datastore-preferences-core:1.1.7")
                implementation("androidx.datastore:datastore-preferences:1.1.7")
            }
        }

        val iosMain by creating {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.9")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

android {
    namespace = "com.fitgymtrack.app"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        compose = true
    }
	
	compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}
