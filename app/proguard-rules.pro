# Nijatva Translate - release shrinker rules

# kotlinx.serialization keeps generated serializers via annotations.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclassmembers class com.nijatva.translate.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.nijatva.translate.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
