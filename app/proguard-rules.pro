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
-ignorewarnings
-keep class com.spawn.ai.model.* {*;}
-keepclassmembers class com.spawn.ai.model.* {*;}

-keep class com.spawn.ai.model.content_urls.* {*;}

-keep class com.spawn.ai.model.websearch.* {*;}

-keep class com.spawn.ai.activities.SpawnWebActivity {*;}

-keep class com.spawn.ai.interfaces.* {*;}

-keep class com.google.gson.* {*;}
-keepclassmembers class com.google.gson.* {*;}

-keep class com.crashlytics.android.* {*;}