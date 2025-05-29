# 📝 FitGymTrack Multiplatform - TODO & ROADMAP

## 🚀 PROSSIMI STEP IMMEDIATI (FASE 1)

### **🔧 TASK 1: Migrare ExerciseApiService.kt (PRIORITÀ ALTA)**
**Status:** 🟢 VERDE - Copia diretta, zero modifiche necessarie  
**Destinazione:** `shared/src/commonMain/kotlin/com/fitgymtrack/api/ExerciseApiService.kt`

#### **Actions:**
1. **Copia file da progetto Android originale**
2. **Salva in path specificato sopra**
3. **Verifica compilation in shared module**
4. **Test import in MainActivity per confermare funzionamento**

### **🔧 TASK 2: Migrare altri 9 API Services VERDE (PRIORITÀ ALTA)**
**Status:** 🟢 VERDE - Copia diretta per tutti

#### **Lista file da copiare (in ordine):**
```
✅ ApiService.kt                 → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ PaymentApiService.kt          → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ StatsApiService.kt            → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ SubscriptionApiService.kt     → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ UserExerciseApiService.kt     → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ WorkoutApiService.kt          → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ WorkoutHistoryApiService.kt   → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ ActiveWorkoutApiService.kt    → shared/src/commonMain/kotlin/com/fitgymtrack/api/
✅ FeedbackApiService.kt         → shared/src/commonMain/kotlin/com/fitgymtrack/api/
```

### **🔧 TASK 3: Fix ApiClient.kt (PRIORITÀ MEDIA)**
**Status:** 🟡 GIALLO - Logging fix necessario

#### **Modifiche necessarie:**
```kotlin
// DA SOSTITUIRE:
android.util.Log.e("ApiClient", "Errore parsing JSON: ${e.message}")

// CON:
logError("ApiClient", "Errore parsing JSON: ${e.message}")
// Oppure temporaneamente:
println("ApiClient - Errore parsing JSON: ${e.message}")
```

### **🔧 TASK 4: Fix NotificationApiService.kt (PRIORITÀ MEDIA)**
**Status:** 🟡 GIALLO - Device info fix necessario

#### **Modifiche necessarie:**
```kotlin
// DA SOSTITUIRE:
android.os.Build.VERSION.RELEASE
android.os.Build.MANUFACTURER
android.os.Build.MODEL

// CON (implementare expect/actual):
DeviceInfoProvider().getOsVersion()
DeviceInfoProvider().getDeviceManufacturer()
DeviceInfoProvider().getDeviceModel()
```

---

## 📋 ROADMAP DETTAGLIATA FASI 1-6

### **🔄 FASE 1: API & MODELS (2 settimane) - IN CORSO**

#### **Week 1 (Giorni 1-7): API Services**
- [x] ✅ FASE 0 completata (setup multiplatform)
- [ ] 🔄 ExerciseApiService.kt (PROSSIMO STEP)
- [ ] 📋 Altri 9 API Services VERDE (copia diretta)
- [ ] 📋 ApiClient.kt (logging fix)
- [ ] 📋 NotificationApiService.kt (device info fix)
- [ ] 📋 Test API calls con Ktor client

#### **Week 2 (Giorni 8-14): Data Models**
- [ ] 📋 Migrare 12 Models VERDE (copia diretta)
- [ ] 📋 Fix 4 Models GIALLO (datetime + device info)
- [ ] 📋 Setup Ktor networking completo
- [ ] 📋 Test serialization/deserialization
- [ ] 📋 Integration test API + Models

### **📋 FASE 2: BUSINESS LOGIC (2 settimane)**

#### **Week 3 (Giorni 15-21): Repository Layer**
- [ ] 📋 Migrare 3 Repository VERDE (copia diretta)
- [ ] 📋 Fix 9 Repository GIALLO (logging + context)
- [ ] 📋 Setup DataStore multiplatform
- [ ] 📋 Test CRUD operations

#### **Week 4 (Giorni 22-28): Utils & Services**
- [ ] 📋 Migrare WeightFormatter.kt (VERDE)
- [ ] 📋 Fix PlateauDetector.kt (CRITICO - logging)
- [ ] 📋 Setup expect/actual per audio/file/storage
- [ ] 📋 Test business logic layer completo

### **📋 FASE 3: UI FOUNDATION (2 settimane)**

#### **Week 5 (Giorni 29-35): Theme & Navigation**
- [ ] 📋 Migrare Theme System (6 file VERDE)
- [ ] 📋 Setup Navigation multiplatform
- [ ] 📋 Test Material3 rendering
- [ ] 📋 Setup expect/actual per UI platform

#### **Week 6 (Giorni 36-42): UI Components**
- [ ] 📋 Migrare 16 Components VERDE
- [ ] 📋 Fix 7 Components GIALLO
- [ ] 📋 Workaround 1 Component ROSSO
- [ ] 📋 Test UI components library

### **📋 FASE 4: UI SCREENS (2 settimane)**

#### **Week 7 (Giorni 43-49): Simple Screens**
- [ ] 📋 Migrare 6 Screens VERDE (auth + forms)
- [ ] 📋 Fix 7 Screens GIALLO (context)
- [ ] 📋 Test user flows basic

#### **Week 8 (Giorni 50-56): Complex Screens**
- [ ] 📋 Fix 6 Screens GIALLO (business logic)
- [ ] 📋 Migrare ActiveWorkoutScreen (most complex)
- [ ] 📋 Test advanced user flows

### **📋 FASE 5: PLATFORM INTEGRATION (1.5 settimane)**

#### **Week 9-10 (Giorni 57-63): iOS & Hardware**
- [ ] 📋 Re-enable iOS targets (Mac/cloud needed)
- [ ] 📋 iOS payment integration
- [ ] 📋 Hardware features (audio, permissions)
- [ ] 📋 Test build iOS

### **📋 FASE 6: TESTING & DEPLOYMENT (1 settimana)**

#### **Week 11 (Giorni 64-70): Release**
- [ ] 📋 Integration testing
- [ ] 📋 CI/CD pipeline
- [ ] 📋 App Store preparation
- [ ] 📋 Release candidate

---

## ⚠️ FIX TEMPORANEI DA RIPRISTINARE

### **🔧 PRIORITÀ ALTA: Git Versioning System**

#### **PROBLEMA:**
Le variabili `extra` dal root build.gradle.kts non vengono propagate ai submodules.

#### **FIX TEMPORANEI APPLICATI:**
```kotlin
// androidApp/build.gradle.kts - HARDCODED:
versionCode = 1
versionName = "0.0.1-dev"

// shared/build.gradle.kts - HARDCODED:
val compileSdk = 35
val namespace = "com.fitgymtrack.app"
```

#### **DA RIPRISTINARE (FASE 1):**
1. **Debug root build.gradle.kts:**
   - Verificare funzioni `getGitCommitCount()` e `getGitSha()`
   - Testare propagazione variabili `extra`
   - Possibili cause: Git non nel PATH, order evaluation, sintassi

2. **Ripristinare in submodules:**
   ```kotlin
   // androidApp/build.gradle.kts:
   val gitCommitCount: Int by rootProject.extra
   val versionName: String by rootProject.extra
   
   // shared/build.gradle.kts:
   val compileSdk: Int by rootProject.extra
   val namespace: String by rootProject.extra
   ```

3. **Test versioning automatico:**
   - `./gradlew build` con Git versioning
   - Verificare versionCode incrementale
   - Controllare versionName con SHA

### **🍎 PRIORITÀ MEDIA: iOS Targets (FASE 5)**

#### **PROBLEMA:**
iOS targets causano conflitti su Windows development environment.

#### **FIX TEMPORANEI APPLICATI:**
```kotlin
// shared/build.gradle.kts - DISABLED:
// iosX64(), iosArm64(), iosSimulatorArm64()
// iosMain.dependencies { ktor-client-darwin }

// gradle.properties - DISABLED:
// org.gradle.configuration-cache=true
```

#### **DA RIPRISTINARE (FASE 5):**
1. **Re-enable iOS targets:**
   ```kotlin
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
   ```

2. **Re-enable iOS dependencies:**
   ```kotlin
   iosMain.dependencies {
       implementation(libs.ktor.client.darwin)
   }
   ```

3. **Re-enable configuration cache:**
   ```properties
   org.gradle.configuration-cache=true
   ```

4. **Test su Mac/Cloud:**
   - `./gradlew shared:linkDebugFrameworkIosX64`
   - iOS app compilation
   - iOS simulator testing

### **🎨 PRIORITÀ MEDIA: Theme System & Resources (FASE 3)**

#### **PROBLEMA:**
Material3 non disponibile, themes malformati, risorse mancanti.

#### **FIX TEMPORANEI APPLICATI:**
```xml
<!-- AndroidManifest.xml - RIMOSSO:
android:dataExtractionRules="@xml/data_extraction_rules"
android:fullBackupContent="@xml/backup_rules"
android:icon="@mipmap/ic_launcher"
android:roundIcon="@mipmap/ic_launcher_round"
android:theme="@style/Theme.FitGymTrack"
-->

<!-- themes.xml - SEMPLIFICATO:
<style name="Theme.FitGymTrack" parent="Theme.AppCompat.Light.DarkActionBar">
    <!-- minimal attributes -->
</style>
-->
```

```kotlin
// MainActivity.kt - RIMOSSO:
// FitGymTrackTheme custom
// SOSTITUITO CON: MaterialTheme basic
```

#### **DA RIPRISTINARE (FASE 3):**
1. **App Icons:**
   - Creare ic_launcher.png + ic_launcher_round.png
   - Aggiungere alle risorse androidApp/src/main/res/mipmap/

2. **Backup Rules:**
   - Creare data_extraction_rules.xml
   - Creare backup_rules.xml
   - Aggiungere alle risorse androidApp/src/main/res/xml/

3. **Material3 Theme completo:**
   ```xml
   <style name="Theme.FitGymTrack" parent="Theme.Material3.DayNight">
       <item name="colorPrimary">@color/purple_500</item>
       <!-- + attributi Material3 completi -->
   </style>
   ```

4. **FitGymTrackTheme custom:**
   - Migrare tema custom da progetto Android originale
   - Creare theme/ folder in shared/src/commonMain/
   - Sostituire MaterialTheme con FitGymTrackTheme in MainActivity

5. **Splash Screen:**
   ```xml
   <style name="Theme.FitGymTrack.Starting" parent="Theme.SplashScreen">
       <item name="windowSplashScreenBackground">@color/purple_200</item>
       <item name="windowSplashScreenAnimatedIcon">@mipmap/ic_launcher</item>
   </style>
   ```

### **🔗 PRIORITÀ BASSA: Dependencies Optimization (FASE 2)**

#### **PROBLEMA:**
Alcune dipendenze Android-only in androidMain potrebbero essere multiplatform.

#### **DA OTTIMIZZARE:**
1. **Navigation Compose:**
   - Verificare supporto multiplatform
   - Spostare da androidMain a commonMain se disponibile

2. **ViewModel Compose:**
   - Setup ViewModel per iOS
   - Lifecycle management multiplatform

3. **Ktor Complete:**
   - Sostituire Retrofit+Gson con pure Ktor
   - Test networking consistency iOS/Android

4. **DataStore:**
   - Migrare da androidx.datastore.preferences a core
   - Setup expect/actual per PlatformContext

---

## 📊 CHECKLIST MILESTONE

### ✅ **FASE 0 - COMPLETATA**
- [x] Repository multiplatform setup
- [x] Build Android successful
- [x] App deployed e funzionante su device
- [x] expect/actual Platform testato
- [x] 16 file configurazione funzionanti

### 🔄 **FASE 1 - IN PROGRESS**
- [ ] ExerciseApiService.kt migrato
- [ ] 9 API Services VERDE migrati
- [ ] 2 API Services GIALLO fixed
- [ ] 12 Data Models VERDE migrati
- [ ] 4 Data Models GIALLO fixed
- [ ] Ktor networking setup completo
- [ ] API calls testati su Android

### 📋 **FASE 2 - PLANNED**
- [ ] 12 Repository migrati
- [ ] 10 Utils migrati
- [ ] 2 Services migrati
- [ ] DataStore multiplatform setup
- [ ] Business logic layer completo

### 📋 **FASE 3 - PLANNED**
- [ ] Theme system completo
- [ ] 24 UI Components migrati
- [ ] Navigation multiplatform
- [ ] Material3 rendering testato

### 📋 **FASE 4 - PLANNED**
- [ ] 20 UI Screens migrati
- [ ] User flows completi
- [ ] Advanced features integrate

### 📋 **FASE 5 - PLANNED**
- [ ] iOS targets re-enabled
- [ ] iOS build successful
- [ ] Payment integration iOS
- [ ] Hardware features iOS

### 📋 **FASE 6 - PLANNED**
- [ ] Integration testing
- [ ] CI/CD pipeline
- [ ] App Store ready
- [ ] Release candidate

---

## 🎯 SUCCESS CRITERIA

### **FASE 1 SUCCESS:**
- ✅ Tutti i 12 API Services compilano in shared module
- ✅ Tutti i 16 Data Models compilano in shared module
- ✅ Ktor client funziona su Android
- ✅ JSON serialization/deserialization OK
- ✅ Zero dipendenze Android-specific in API layer

### **PROGETTO SUCCESS:**
- ✅ App iOS funzionante con 90%+ codice condiviso
- ✅ Feature parity Android/iOS
- ✅ Performance comparable
- ✅ UX coerente mobile-first
- ✅ CI/CD pipeline automatizzata
- ✅ Release candidate App Store ready

---

## 🚨 RISK MITIGATION

### **RISCHI IDENTIFICATI:**
- **BASSO:** Architettura eccellente per multiplatform
- **MEDIO:** iOS testing limitato su Windows
- **BASSO:** Hardware features iOS differenti

### **STRATEGIE MITIGAZIONE:**
- **Repository separata** = zero rischi Android attuale
- **Migrazione graduale** file-by-file con testing continuo
- **expect/actual pattern** per hardware differences
- **Cloud/Mac testing** per iOS in FASE 5
- **Fallback strategies** per features complesse

---

## 📝 DEVELOPMENT NOTES

### **COMMIT STRATEGY:**
- Commit dopo ogni file migrato con successo
- Branch per ogni fase (fase-1-api, fase-2-business, etc.)
- PR review prima di merge in main

### **TESTING STRATEGY:**
- Test compilation dopo ogni file
- Integration test dopo ogni layer
- Device testing per UI changes
- Automated testing in CI/CD

### **DOCUMENTATION:**
- Aggiornare FITGYMTRACK-MASTER.md ad ogni milestone
- Documentare workarounds e decisions
- Mantenere TODO-ROADMAP aggiornato

---

*📝 Task chiari, priorità definite, progresso monitorato!*  
*🎯 Ready for execution with high confidence!*