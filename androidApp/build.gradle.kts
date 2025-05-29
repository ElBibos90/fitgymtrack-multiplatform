plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Retrieve shared configuration from root project
val compileSdk: Int by rootProject.extra
val targetSdk: Int by rootProject.extra
val minSdk: Int by rootProject.extra
val namespace: String by rootProject.extra
val gitCommitCount: Int by rootProject.extra
val gitSha: String by rootProject.extra
val versionName: String by rootProject.extra

android {
    namespace = "$namespace.android"
    compileSdk = compileSdk

    defaultConfig {
        applicationId = namespace
        minSdk = minSdk
        targetSdk = targetSdk
        versionCode = gitCommitCount
        versionName = versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
            buildConfigField("int", "VERSION_CODE", "$gitCommitCount")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
            buildConfigField("int", "VERSION_CODE", "$gitCommitCount")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // === SHARED MODULE ===
    implementation(project(":shared"))
    
    // === ANDROID COMPOSE BOM ===
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.foundation)
    
    // === ACTIVITY COMPOSE ===
    implementation(libs.androidx.activity.compose)
    
    // === TESTING ===
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    
    // === DEBUG ===
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}