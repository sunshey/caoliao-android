#指定代码的压缩级别
-optimizationpasses 5
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#混淆时是否记录日志
-verbose
#忽略警告
-ignorewarnings
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes EnclosingMethod
-dontoptimize


# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预检验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping priguardMapping.txt

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }

# 避免混淆泛型
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

#OK HTTP
#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#腾讯IM
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类有可能被外部调用
# 比如第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#如果引用了v4或者v7包
-dontwarn android.support.**
-dontwarn android.support.v4.**
-keep class android.support.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
    public void * (android.view.View);
}

-keep public class javax.**
-keep public class android.webkit.**
-keepattributes *JavascriptInterface*
-keepattributes Exceptions,InnerClasses,Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

-keep public class com.yc.liaolive.R$*{
    public static final int *;
}

-keep class com.yc.liaolive.base.adapter.**{*;}
-keep class com.yc.liaolive.msg.adapter.**{*;}
-keep class com.yc.liaolive.VideoApplication

#友盟
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

#-keep public class com.umeng.socialize.* {*;}
#-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
#-keep class com.umeng.socialize.sensor.**
#-keep class com.umeng.socialize.handler.**
#-keep class com.umeng.socialize.handler.*
#-keep class com.umeng.weixin.handler.**
#-keep class com.umeng.weixin.handler.*
#-keep class com.umeng.qq.handler.**
#-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

#微信登录、支付
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
#-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#通讯云
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**

-keep class tencent.**{*;}
-dontwarn tencent.**

-keep class qalsdk.**{*;}
-dontwarn qalsdk.**

#MOB
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**
#版本更新
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#支付宝
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

#百度定位
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**

#阿里云
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

#databinding
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

#nineoldandroids-2.4.0.jar
-keep class com.nineoldandroids.**{*;}

#个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

#rx
-keep class rx.android.**{*;}
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#slf4j 日志输出框架
-keep class org.slf4j.impl.** {*;}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#保持 Comparable 不被混淆
-keep class * implements java.lang.Comparable {
    *;
}
-keepnames class * implements java.lang.Comparable {
    *;
}

#图片加载
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#eventBus
-keep class org.simple.** { *; }
-keep interface org.simple.** { *; }
-keepclassmembers class * {
    void *(**On*Event);
    @org.simple.eventbus.Subscriber <methods>;
}

#bean对象
-keep class com.yc.liaolive.bean.**{*;}
-keep class com.yc.liaolive.msg.model.bean.**{*;}
-keep class com.yc.liaolive.index.model.bean.**{*;}
-keep class com.yc.liaolive.live.bean.**{*;}
-keep class com.yc.liaolive.upload.bean.**{*;}
-keep class com.yc.liaolive.video.bean.**{*;}
-keep class com.yc.liaolive.media.bean.**{*;}
-keep class com.yc.liaolive.pay.model.bean.**{*;}
-keep class com.yc.liaolive.recharge.model.bean.**{*;}
-keep class com.yc.liaolive.start.model.bean.**{*;}
-keep class com.kaikai.securityhttp.domain.**{*;}
-keep class com.yc.liaolive.user.model.bean.**{*;}
-keep class com.yc.liaolive.videocall.bean.**{*;}
-keep class com.yc.liaolive.user.bean.**{*;}
#MusicPlayer
-keep class com.music.player.lib.bean.**{*;}

#游戏盒子
-keep class com.kk.securityhttp.domain.**{*;}
-keep class com.yc.loanbox.model.bean.**{*;}

#七牛云播放器SDK
-keep class com.pili.pldroid.player.** { *; }
-keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}
#七牛云实时音视频
-keep class org.webrtc.** {*;}
-dontwarn org.webrtc.**
-keep class com.qiniu.droid.rtc.**{*;}
-keep interface com.qiniu.droid.rtc.**{*;}
#编译版本大于24，必须加入 libopenssl.so，防止崩溃
#金山云视频播放
-keep class com.ksyun.media.player.**{ *; }
-keep class com.ksy.statlibrary.**{ *;}

#WebView
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

#反射相关的类和方法
-keep class com.yc.liaolive.util.game.MResource { *; }
-keep class com.yc.liaolive.util.game.view.** { *; }