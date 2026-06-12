# Add project specific ProGuard rules here.
-keep class com.immersiads.app.data.model.** { *; }
-keep class com.immersiads.app.data.local.entities.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn java.lang.invoke.*
