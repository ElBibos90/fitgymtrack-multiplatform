# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# === FITGYMTRACK SPECIFIC RULES ===

# Keep all model classes (for Gson/Kotlinx.serialization)
-keep class com.fitgymtrack.models.** { *; }
-keepclassmembers class com.fitgymtrack.models.** { *; }

# Keep Retrofit service interfaces
-keep interface com.fitgymtrack.api.** { *; }

# Keep Gson/Kotlinx.serialization related
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.serialization.** { *; }

# Keep Compose related
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.compose.** { *; }

# Keep Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep expect/actual implementations
-keep class com.fitgymtrack.platform.** { *; }