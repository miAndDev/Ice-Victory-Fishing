# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
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

-keep @interface com.anor.security.StringShield
-keep class com.anor.security.AesDecryptor { *; }

# Strip debug/verbose logging from release builds.
# R8 removes these calls (and the dead string-building that feeds them)
# because they are declared to have no side effects.
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}
-obfuscationdictionary words
-packageobfuscationdictionary words
-classobfuscationdictionary words
-repackageclasses
-allowaccessmodification