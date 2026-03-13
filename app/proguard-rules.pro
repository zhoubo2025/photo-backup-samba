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

# Keep Room database classes
-keep class com.photobackup.data.** { *; }
-keep interface com.photobackup.data.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <methods>;
}

# Keep Hilt annotations
-keep @dagger.hilt.android.scopes.ActivityScoped class *
-keep @dagger.hilt.android.scopes.ServiceScoped class *
-keep @dagger.hilt.android.scopes.ViewModelScoped class *

# Keep Notification Channel classes
-keep class com.photobackup.**$* { *; }