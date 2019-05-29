package com.yc.liaolive.start.manager;


import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.TextUtils;

import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.kaikai.securityhttp.utils.LogUtil;
import com.ksyun.media.player.KSYHardwareDecodeWhiteList;
import com.qiniu.droid.rtc.QNRTCEnv;
import com.tencent.bugly.imsdk.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.common.ControllerConstant;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.engine.CaoliaoNetEngine;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.observer.FriendshipEvent;
import com.yc.liaolive.observer.GroupEvent;
import com.yc.liaolive.observer.RefreshEvent;
import com.yc.liaolive.ui.business.LoginBusiness;
import com.yc.liaolive.util.ACache;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.EmotionUtils;
import com.yc.liaolive.util.SharedPreferencesUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * AppManager，APP组件初始化和销毁管理，不是用于存储各种公共或非公共数据的
 * Created by yangxueqin on 2018/10/26.
 */
public class AppManager {

    private static final String TAG = "AppManager";
    private static AppManager mInstance;
    private static Application context = AppEngine.getApplication();
    //应该有一个FileManager来管理文件目录相关的操作，外部访问提供get方法
    private static String AppFileSystemDir;
    private List<String> mUrlBlackList;

    public static synchronized AppManager getInstance() {
        synchronized (AppManager.class) {
            if (null == mInstance) {
                mInstance = new AppManager();
            }
        }
        return mInstance;
    }

    private AppManager() {

    }

    /**
     * 启动时初始化代码请放在这个方法中
     */
    public void onCreate() {
        fixOPPOTimeOutException();
        //初始化IMSDK
        LoginBusiness.getInstance().init(context.getApplicationContext());
        //设置刷新监听
        RefreshEvent.getInstance();
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        //美颜
//        FURenderer.initFURenderer(context);
        //七牛云实时音视频
        QNRTCEnv.init(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initParams();
                initUmeng();
                //联网日志开关
                LogUtil.setDEBUG(VideoApplication.TEST);
                //调试日志开关
                ConfigSet.getInstance().setDebug(VideoApplication.TEST);
                //初始化表情
                EmotionUtils.getInstance().init(context);
                //友盟统计、分享
                PlatformConfig.setWeixin(Constant.LOGIN_WX_KEY, Constant.LOGIN_WX_SECRET);//设置微信SDK账号
                PlatformConfig.setSinaWeibo(Constant.LOGIN_WEIBO_KEY, Constant.LOGIN_WEIBO_SECRET, Constant.LOGIN_WEIBO_CALL_BACK_URL);//设置微博分享/登录SDK//https://api.weibo.com/oauth2/default.html
                PlatformConfig.setQQZone(Constant.LOGIN_QQ_KEY, Constant.LOGIN_QQ_SECRET);//设置QQ/空间SDK账号
                //友盟分享
                UMShareAPI.get(context);
                MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);//普通统计模式
                CrashReport.initCrashReport(context.getApplicationContext(), Constant.BUGLY_APP_ID, false);
                // 注册文件系统变动观察者
                // registFileSystemObser();
                //金山云硬解白名单
                KSYHardwareDecodeWhiteList.getInstance().init(context);
                //写入启动时间
                SharedPreferencesUtil.getInstance().putLong(Constant.START_APP_TIME,System.currentTimeMillis());
            }
        }).start();
        ControllerConstant.init();
    }


    /**
     * 初始化参数
     */

    private void initParams() {
        SharedPreferencesUtil.init(context, context.getPackageName() + Constant.SP_NAME, Context.MODE_MULTI_PROCESS);
        ACache cache = ACache.get(context);
        ApplicationManager.getInstance().setCacheExample(cache);//初始化后需要设置给通用管理者
        setHttpDefaultParams();
        //设置初始化
        ConfigSet.getInstance().init();
    }

    /**
     * 初始化友盟 设置渠道
     */
    private void initUmeng () {
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(context,
                BuildConfig.FLAVOR.contains("caoliao") ? "5c492270f1f5564d62001272" : "5c21e2ceb465f522870000b0",
                ChannelUtls.getInstance().getChannel()));
    }

    /**
     * /注册文件系统变动观察者
     */
    private void registFileSystemObser() {
        DetectSdcard in = new DetectSdcard();
        IntentFilter intentf = new IntentFilter();
        intentf.addAction(Intent.ACTION_MEDIA_MOUNTED);// 已挂载SDCARD
        intentf.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// 未挂载SDCARD
        intentf.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);// 恶意拔SDCARD
        intentf.addAction(Intent.ACTION_MEDIA_SHARED);// 被挂起SDCARD
        intentf.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);// 无法挂载SDCARD
        // 隐式intent需要加上下面这句作匹配，否则接收不到广播
        intentf.addDataScheme("file");
        context.registerReceiver(in, intentf);
    }

    /**
     * 遍历所有Activity并finish
     */
    public void exit() {
        //是否需要杀进程
        if (AppEngine.getIsNeedKillProcess()) {
            System.exit(0);
            MobclickAgent.onKillProcess(context);
        }
    }

    /**
     * 强制退出
     */
    public void forceExit() {
        AppEngine.setIsNeedKillProcess(true);
        exit();
    }

    public void setUrlBlackList(List<String> urlBlackList) {
        mUrlBlackList = urlBlackList;
    }

    public List<String> getUrlBlackList() {
        return mUrlBlackList;
    }

    /**
     * URL是否存在于黑名单列表中
     * @param url
     * @return
     */
    public boolean existBlackList(String url) {
        if(TextUtils.isEmpty(url)||null==getUrlBlackList())return false;
        List<String> urlBlackList = getUrlBlackList();
        for (int i = 0; i < urlBlackList.size(); i++) {
            if(url.contains(urlBlackList.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * 检测是否有sdcard
     */
    public class DetectSdcard extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                // 挂载的...
                getSafeCacheFileDir();
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                // 非挂载...
                getSafeCacheFileDir();
            } else if (intent.getAction().equals(
                    Intent.ACTION_MEDIA_BAD_REMOVAL)) {
                getSafeCacheFileDir();
            } else if (intent.getAction().equals(Intent.ACTION_MEDIA_SHARED)) {
                getSafeCacheFileDir();
            } else if (intent.getAction().equals(
                    Intent.ACTION_MEDIA_UNMOUNTABLE)) {
                getSafeCacheFileDir();
            }
            // DebugUtil.println("文件系统变更通知：" + AppFileSystemDir);
        }
    }

    /**
     * 得到一个当前的安全的文件存储目录,防止可能的外置存储器错误，如 sdcard被移除;
     */
    private String getSafeCacheFileDir() {
        initFileSystem();
        return AppFileSystemDir;
    }

    public static String AppDir;

    /**
     * 初始化文件系统
     */
    private void initFileSystem() {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File file = new File(AppEngine.getApplication().getExternalFilesDir(null)
                        + File.separator
                        + AppDir
                        + File.separator);
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        AppFileSystemDir = file.getAbsolutePath() + File.separator;
                    } else {
                        AppFileSystemDir = file.getAbsolutePath() + File.separator;
                    }
                } else {
                    AppFileSystemDir = file.getAbsolutePath() + File.separator;
                }
            } else {
                AppFileSystemDir = context.getFilesDir().getAbsolutePath() + File.separator;
            }
            // DebugUtil.println("文件系统当前目录:" + AppFileSystemDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试修复OPPO机型错误
     */
    private void fixOPPOTimeOutException() {
        //stop FinalizerWatchdogDaemon
        try {
            Class<?> clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get("null"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //将Timeout的时间设置超长
        try {
            Class<?> c = Class.forName("java.lang.Daemons");
            Field maxField = c.getDeclaredField("MAX_FINALIZE_NANOS");
            maxField.setAccessible(true);
            maxField.set(null, Long.MAX_VALUE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setHttpDefaultParams() {
//        HttpConfig.setPublickey(Constant.URL_PUBLIC_KEY);
        //设置http默认参数
        HttpConfig.setDefaultParams(CaoliaoNetEngine.getCommonParams());
    }
}