# 🏗️ FitGymTrack Multiplatform - PROJECT STRUCTURE

## 📁 DIRECTORY STRUCTURE COMPLETA

```
C:\Users\Eddy\Desktop\Repository\fitgymtrack-multiplatform\
├── settings.gradle.kts                 # ✅ Project modules definition
├── build.gradle.kts                    # ✅ Root build configuration
├── gradle.properties                   # ✅ Build optimizations
├── gradle/
│   └── libs.versions.toml              # ✅ Version catalog
├── shared/                             # ✅ Codice condiviso iOS + Android
│   ├── build.gradle.kts               # ✅ Multiplatform configuration
│   └── src/
│       ├── commonMain/                # ✅ Codice comune
│       │   └── kotlin/com/fitgymtrack/
│       │       ├── api/               # 🔄 API Services (10/12 migrati)
│       │       ├── models/            # 📋 Data Models (da migrare)
│       │       ├── repository/        # 📋 Repository Layer (FASE 2)
│       │       ├── utils/             # 📋 Utils Layer (FASE 2)
│       │       ├── ui/                # 📋 UI Layer (FASE 3-4)
│       │       │   ├── theme/         # 📋 Theme system
│       │       │   ├── components/    # 📋 UI Components
│       │       │   └── screens/       # 📋 UI Screens
│       │       ├── di/                # 📋 Dependency Injection
│       │       └── platform/          # ✅ expect declarations
│       ├── androidMain/               # ✅ Android implementations
│       │   └── kotlin/com/fitgymtrack/
│       │       └── platform/          # ✅ actual implementations
│       └── iosMain/                   # 📋 iOS implementations (FASE 5)
│           └── kotlin/com/fitgymtrack/
│               └── platform/          # 📋 actual implementations iOS
├── androidApp/                        # ✅ Android app wrapper
│   ├── build.gradle.kts              # ✅ Android-specific config
│   └── src/main/
│       ├── AndroidManifest.xml       # ✅ App manifest
│       ├── kotlin/com/fitgymtrack/app/android/
│       │   └── MainActivity.kt       # ✅ Android entry point
│       └── res/                      # ✅ Android resources
│           └── values/
│               ├── strings.xml       # ✅ App strings
│               ├── colors.xml        # ✅ App colors  
│               └── themes.xml        # ✅ App themes
└── iosApp/                           # 📋 iOS app wrapper (FASE 5)
    ├── Configuration/
    └── iosApp/
        ├── ContentView.swift
        └── iOSApp.swift
```

---

## 📁 DOVE SALVARE I FILE DURANTE MIGRAZIONE

### 🔄 **FASE 1: API & Models**

#### **API Services → `shared/src/commonMain/kotlin/com/fitgymtrack/api/`**
```
✅ ExerciseApiService.kt         → PROSSIMO DA MIGRARE
✅ ApiService.kt                 
✅ PaymentApiService.kt          
✅ StatsApiService.kt            
✅ SubscriptionApiService.kt     
✅ UserExerciseApiService.kt     
✅ WorkoutApiService.kt          
✅ WorkoutHistoryApiService.kt   
✅ ActiveWorkoutApiService.kt    
✅ FeedbackApiService.kt         
⚠️ ApiClient.kt                  → Logging fix needed
⚠️ NotificationApiService.kt     → Device info fix needed
```

#### **Data Models → `shared/src/commonMain/kotlin/com/fitgymtrack/models/`**
```
✅ ApiResponse.kt
✅ Exercise.kt
✅ LoginRequest.kt + LoginResponse.kt
✅ PasswordResetModels.kt
✅ RegisterRequest.kt + RegisterResponse.kt
✅ ResourceLimits.kt
✅ SeriesRequestModels.kt
✅ Subscription.kt
✅ UserExercise.kt
✅ UserProfile.kt
✅ UserStats.kt
✅ WorkoutPlanModels.kt
⚠️ ActiveWorkoutModels.kt        → DateTime fix
⚠️ Feedback.kt                   → DeviceInfo expect/actual
⚠️ NotificationModels.kt         → Platform info
⚠️ WorkoutHistory.kt             → DateTime fix
```

### 📋 **FASE 2: Business Logic**

#### **Repository → `shared/src/commonMain/kotlin/com/fitgymtrack/repository/`**
```
✅ ExerciseRepository.kt
✅ UserRepository.kt
✅ WorkoutRepository.kt
⚠️ PaymentRepository.kt
⚠️ SubscriptionRepository.kt
⚠️ UserExerciseRepository.kt
⚠️ WorkoutHistoryRepository.kt
⚠️ ActiveWorkoutRepository.kt
⚠️ AuthRepository.kt
⚠️ FeedbackRepository.kt
⚠️ StatsRepository.kt
⚠️ NotificationRepository.kt
```

#### **Utils → `shared/src/commonMain/kotlin/com/fitgymtrack/utils/`**
```
✅ WeightFormatter.kt
⚠️ PlateauDetector.kt            → CRITICO per business logic
⚠️ SubscriptionLimitChecker.kt
⚠️ SessionManager.kt
⚠️ ThemeManager.kt
⚠️ DeviceInfoUtils.kt
⚠️ FileAttachmentManager.kt
⚠️ NotificationUtils.kt
⚠️ SoundManager.kt
⚠️ NotificationManager.kt
```

### 📋 **FASE 3-4: UI Layer**

#### **Theme → `shared/src/commonMain/kotlin/com/fitgymtrack/ui/theme/`**
```
✅ Color.kt
✅ GradientCard.kt
✅ GradientUtils.kt
✅ Theme.kt
✅ Type.kt
✅ AppNavigation.kt
```

#### **Components → `shared/src/commonMain/kotlin/com/fitgymtrack/ui/components/`**
```
16 file VERDE (copia diretta)
7 file GIALLO (piccoli fix)
1 file ROSSO (workaround)
```

#### **Screens → `shared/src/commonMain/kotlin/com/fitgymtrack/ui/screens/`**
```
6 file VERDE (copia diretta)
13 file GIALLO (context fix)
```

---

## 🔧 BUILD CONFIGURATIONS

### **Root build.gradle.kts** (CURRENT STATE)
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// ⚠️ TODO: Ripristinare Git versioning system (TEMPORANEAMENTE DISABILITATO)
// val gitCommitCount: Int = getGitCommitCount()
// val versionName: String = getGitSha()

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

### **shared/build.gradle.kts** (CURRENT STATE)
```kotlin
kotlin {
    // === ANDROID TARGET (FUNZIONANTE) ===
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    // === iOS TARGETS (TEMPORANEAMENTE DISABILITATI PER WINDOWS) ===
    // TODO FASE 5: Re-enable on Mac/cloud
    // iosX64(), iosArm64(), iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            
            // Core multiplatform
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
        
        androidMain.dependencies {
            // Android-specific dependencies
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            
            // Networking
            implementation(libs.retrofit)
            implementation(libs.retrofit.converter.gson)
            implementation(libs.okhttp.logging.interceptor)
            implementation(libs.gson)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            
            // Storage & UI
            implementation(libs.coil.compose)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.compose.material.dialogs)
            implementation(libs.compose.numberpicker)
            implementation(libs.androidx.compose.material3.window.size)
        }
    }
}

android {
    namespace = "com.fitgymtrack.app"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
}
```

### **androidApp/build.gradle.kts** (CURRENT STATE)
```kotlin
android {
    namespace = "com.fitgymtrack.app.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fitgymtrack.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1  // ⚠️ TODO: Restore Git versioning
        versionName = "0.0.1-dev"  // ⚠️ TODO: Restore Git versioning
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    // Altri dependencies...
}
```

---

## 🔧 EXPECT/ACTUAL PATTERNS

### **IMPLEMENTATI E TESTATI (FASE 0):**

#### **Platform.kt** 
```kotlin
// shared/src/commonMain/kotlin/com/fitgymtrack/platform/Platform.kt
expect class Platform() {
    val name: String
    val version: String
}
expect fun getPlatform(): Platform
expect fun logDebug(tag: String, message: String)
fun getWelcomeMessage(): String  // Test function

// shared/src/androidMain/kotlin/com/fitgymtrack/platform/Platform.android.kt
actual class Platform {
    actual val name: String = "Android"
    actual val version: String = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
}
actual fun getPlatform(): Platform = Platform()
actual fun logDebug(tag: String, message: String) {
    android.util.Log.d(tag, message)
}

// shared/src/iosMain/kotlin/com/fitgymtrack/platform/Platform.ios.kt (PLACEHOLDER)
actual class Platform {
    actual val name: String = UIDevice.currentDevice.systemName() ?: "iOS"
    actual val version: String = UIDevice.currentDevice.systemVersion() ?: "Unknown"
}
```

### **DA IMPLEMENTARE (FASI 1-5):**

#### **Device Info (FASE 1)**
```kotlin
// shared/src/commonMain/kotlin/com/fitgymtrack/platform/DeviceInfo.kt
expect class DeviceInfoProvider {
    fun getOsVersion(): String
    fun getDeviceModel(): String
    fun getDeviceManufacturer(): String
    fun getAppVersion(): String
    fun createDeviceInfo(): DeviceInfo
}
```

#### **Storage (FASE 2)**
```kotlin
// shared/src/commonMain/kotlin/com/fitgymtrack/platform/Storage.kt
expect class SecureStorageManager(context: PlatformContext) {
    suspend fun store(key: String, value: String)
    suspend fun retrieve(key: String): String?
}
```

#### **Audio & Media (FASE 4)**
```kotlin
// shared/src/commonMain/kotlin/com/fitgymtrack/platform/Audio.kt
expect class SoundManager(context: PlatformContext) {
    fun playWorkoutSound(sound: WorkoutSound)
    fun vibrate(pattern: LongArray)
}
```

#### **UI Platform (FASE 3)**
```kotlin
// shared/src/commonMain/kotlin/com/fitgymtrack/platform/UI.kt
expect fun statusBarsPadding(): Modifier
expect class ScreenInfoProvider {
    fun isLandscape(): Boolean
    fun getScreenSize(): ScreenSize
}
```

---

## 🔄 PATTERN MODIFICHE STANDARD

### **1. Logging Fix (Pattern ripetuto in file GIALLO)**
```kotlin
// ORIGINALE (Android)
android.util.Log.d("TAG", "message")
android.util.Log.e("TAG", "error", exception)

// MULTIPLATFORM (Fixed)
logDebug("TAG", "message")
logError("TAG", "error", exception)
```

### **2. DateTime Fix (Pattern ripetuto)**
```kotlin
// ORIGINALE (Java/Android)
import java.util.Date
import java.text.SimpleDateFormat
val date = Date()
val formatted = SimpleDateFormat("yyyy-MM-dd").format(date)

// MULTIPLATFORM (kotlinx.datetime)
import kotlinx.datetime.*
val instant = Clock.System.now()
val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
val formatted = localDate.toString()
```

### **3. Context Fix (Pattern ripetuto)**
```kotlin
// ORIGINALE (Android)
class SomeManager(private val context: android.content.Context)

// MULTIPLATFORM (expect/actual)
class SomeManager(private val context: PlatformContext)
```

### **4. UUID Generation**
```kotlin
// ORIGINALE (Java)
import java.util.UUID
val id = UUID.randomUUID().toString()

// MULTIPLATFORM (Kotlin)
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)
val id = Uuid.random().toString()
```

### **5. JSON Parsing**
```kotlin
// ORIGINALE (Android org.json)
import org.json.JSONObject
val json = JSONObject(responseBody)

// MULTIPLATFORM (kotlinx.serialization)
import kotlinx.serialization.json.*
val json = Json.parseToJsonElement(responseBody)
```

---

## 📊 DEPENDENCIES STATUS

### ✅ **MULTIPLATFORM READY:**
- Compose Multiplatform (runtime, foundation, material3, ui)
- kotlinx.coroutines.core
- kotlinx.serialization.json
- kotlinx.datetime
- Ktor client (core + platform engines)

### ⚠️ **ANDROID-ONLY (temporary):**
- androidx.navigation.compose (TODO: move to commonMain when stable)
- androidx.lifecycle.viewmodel.compose (TODO: multiplatform setup)
- Retrofit + Gson (compatibility layer)
- DataStore preferences (multiplatform support exists)

### 📋 **TO BE ADDED:**
- ktor-client-darwin (iOS networking - FASE 5)
- iOS-specific UI libraries (FASE 5)
- Platform-specific permissions/hardware libs

---

## 🛠️ DEVELOPMENT SETUP

### **Local Development (Windows)**
```bash
# Build Android (current working)
cd C:\Users\Eddy\Desktop\Repository\fitgymtrack-multiplatform
./gradlew androidApp:build
./gradlew androidApp:installDebug

# Sync dependencies
./gradlew shared:dependencies --configuration commonMainImplementation
```

### **iOS Development (FASE 5 - Mac/Cloud required)**
```bash
# iOS framework generation
./gradlew shared:linkDebugFrameworkIosX64
./gradlew shared:linkReleaseFrameworkIosArm64

# iOS app build
xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp -sdk iphonesimulator
```

---

## 🎯 TECHNICAL DECISIONS

### **Networking: Ktor + Retrofit Hybrid**
- ✅ **Current:** Retrofit per Android compatibility
- 📋 **Future:** Pure Ktor multiplatform (FASE 1-2)
- **Reason:** Gradual migration, maintain Android stability

### **Serialization: kotlinx.serialization**
- ✅ **Chosen:** kotlinx.serialization (multiplatform native)
- **Migration:** @SerializedName → @SerialName, minimal changes

### **Storage: DataStore Multiplatform**
- ✅ **Chosen:** DataStore preferences core (multiplatform available)
- **Migration:** Android DataStore → DataStore core + expect/actual

### **UI: Compose Multiplatform**
- ✅ **Confirmed:** 96%+ compatibility with existing Compose code
- **Benefits:** Material3, Navigation, ViewModel all supported

---

*🏗️ Struttura solida, configurazioni testate, pattern chiari!*  
*📊 Foundation perfetta per le prossime 5 fasi di migrazione!*