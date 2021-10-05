-dontwarn com.imagealgorithmlab.**
-dontwarn com.hsm.**
-dontwarn com.rscja.deviceapi.**
-keep class com.hsm.** {*; }
-keep class com.rscja.deviceapi.** {*; }
-keep interface com.rscja.ht.ui.JavascriptInterface {*;}
-keep class * implements com.rscja.ht.ui.JavascriptInterface {*;}

-keep class no.nordicsemi.android.dfu.** { *; }