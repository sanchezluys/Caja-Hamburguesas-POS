# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve line numbers and attributes for de-obfuscation and crash reporting on Google Play Console
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep app data entities and JSON models so Room and Moshi serialization function seamlessly
-keep class com.example.data.model.** { *; }
-keep class com.example.data.db.** { *; }

# Moshi specific rules
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonClass <init>(...);
}
-keep class * extends com.squareup.moshi.JsonAdapter {
    public <init>(...);
}
-dontwarn com.squareup.moshi.**

# Room Database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coroutines and standard libraries
-dontwarn kotlinx.coroutines.**

