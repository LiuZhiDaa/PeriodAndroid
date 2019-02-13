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
-keepattributes SourceFile,LineNumberTable
-keep class com.google.gson.** {*;}
-keepattributes SourceFile,LineNumberTable
-keep class com.drinkwater.app.bean.**{*;}
#greenDao
-keep class freemarker.** { *; }
-dontwarn freemarker.**
-keep class com.airbnb.lottie.**{*;}
-keep class com.haibin.calendarview.**{*;}
-keep class org.greenrobot.greendao.**{*;}
-dontwarn org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-dontwarn net.sqlcipher.database.**
-keep class net.sqlcipher.database.**{*;}
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

-keep class **$Properties
-keepclassmembers class **$Properties {*;}
-dontwarn com.google.**
-keep class com.period.app.widget.**{*;}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
