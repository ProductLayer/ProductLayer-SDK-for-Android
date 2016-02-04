# Picasso with OkHttp
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# Renderscript
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class android.support.v8.renderscript.** { *; }

# Reflection is used to force the display of icons in popup menu
-keepclassmembernames class android.support.v7.widget.PopupMenu {
    private android.support.v7.view.menu.MenuPopupHelper mPopup;
}
-keepclassmembernames class android.support.v7.view.menu.MenuPopupHelper {
    public void setForceShowIcon(boolean);
}

# code/allocation/variable would result in errors during dexDebug
-optimizations !code/allocation/variable

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all public classes, and their public and protected fields and methods.
-keep public class * {
    public protected *;
}

# Preserve all .class method names.
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Explicitly preserve all serialization members.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
