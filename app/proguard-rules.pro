# Add project specific ProGuard rules here.

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature
-keep class com.ambercatalbas.vaktinde.core.data.remote.aladhan.** { *; }
-keep class com.ambercatalbas.vaktinde.core.data.remote.diyanet.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
