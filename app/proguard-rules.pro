## OkHttp
-dontnote okhttp3.internal.Platform
-dontnote com.squareup.okhttp.internal.Platform
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

## Okio
# java.nio.file.* usage which cannot be used at runtime. Animal sniffer annotation.
-dontwarn okio.Okio
# JDK 7-only method which is @hide on Android. Animal sniffer annotation.
-dontwarn okio.DeflaterSink

## Kotlin
-keepattributes *Annotation*

-keep class kotlin.** { *; }
-keep class org.jetbrains.** { *; }

## Kotlin Reflect
-dontwarn kotlin.reflect.jvm.internal.**

## Project
-dontwarn com.google.errorprone.annotations.**
-dontnote org.apache.http.**
-dontnote android.net.http.*
-keepattributes Signature
-keepattributes Exceptions
# This rule is required for the AndroidX navigation lib
# not including it causes the fragments to not be included in the dex paths
-keep class * extends android.support.v4.app.Fragment{}
# This rule is required to prevent the shrinker from removing Dagger libraries
# Not sure why it's happening, but seems related to the gradle version
-keep class com.andrewvora.apps.lensemblem.**

## Android architecture components: Lifecycle
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-dontwarn android.arch.**

## Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
