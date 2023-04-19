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

#-----obfuse dictionary config-----
-obfuscationdictionary dic.txt
-classobfuscationdictionary dic.txt
-packageobfuscationdictionary dic.txt
#-----obfuse dictionary config-----

#-----app self-----
#binding reflect
-keep public class * implements androidx.viewbinding.ViewBinding {
    public inflate(android.view.LayoutInflater);
    public bind(android.view.View);
}

-keepclassmembers class * extends fun.qianxiao.originalassistant.base.BaseRecycleViewHolder{
    <init>(...);
}

-keep public interface * extends fun.qianxiao.originalassistant.api.translate.TranslateApi{
    public java.lang.String API_NAME;
}

-keep public interface * extends fun.qianxiao.originalassistant.api.appquery.AppQueryaApi{
    public java.lang.String API_NAME;
}
#-----app self-----

#-----brankj-----
#-keep class com.blankj.utilcode.util.** {*;}
#-----brankj-----

#-----umeng-----
-keep class com.umeng.** {*;}
-keep class org.repackage.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#-----umeng-----

