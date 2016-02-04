# Spring with OkHttp
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# Spring
-dontwarn java.beans.**
-dontwarn org.w3c.dom.bootstrap.**
-dontwarn org.springframework.core.convert.**
-dontwarn org.springframework.http.converter.**

# No removal of fields annotated with JSON properties even if private
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* *;
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
