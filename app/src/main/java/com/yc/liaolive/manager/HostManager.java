package com.yc.liaolive.manager;

import android.text.TextUtils;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.contants.NetContants;

/**
 * TinyHung@Outlook.com
 * 2018/8/30
 * HOST管理
 */

public class HostManager {

    private static HostManager mInstance;
    //API HOST
    private String mHostUrl= NetContants.API_RELEASE+"/api/";
    //根 HOST http://a.tn990.com
    private String mRootHost="https://a.tn990.com";

    public static synchronized HostManager getInstance(){
        synchronized (HostManager.class){
            if(null==mInstance){
                mInstance=new HostManager();
            }
        }
        return mInstance;
    }

    /**
     * 初始化host
     */
    public void initHostUrl () {
        if ("caoliaoFlavorDevelop".equals(BuildConfig.FLAVOR)) {
            setHostUrl(NetContants.API_DEVELOP);
        } else if ("caoliaoFlavorTest".equals(BuildConfig.FLAVOR)) {
            setHostUrl(NetContants.API_TEST);
        } else if ("caoliaoFlavorPublish".equals(BuildConfig.FLAVOR)) {
            setHostUrl(NetContants.API_RELEASE);
        } else if ("caoliaoFlavorGroup".equals(BuildConfig.FLAVOR)) {
            setHostUrl(NetContants.API_RELEASE);
        } else if ("caoliaoFlavorPre".equals(BuildConfig.FLAVOR)) {
            setHostUrl(NetContants.API_PRE);
        } else if (BuildConfig.FLAVOR.contains("ttvideo")) {
            setHostUrl(NetContants.API_RELEASE_TTVIDEO);
        }
    }

    public void setHostUrl(String hostUrl){
        this.mRootHost=hostUrl;
        if (!hostUrl.endsWith("/")) {
            hostUrl += "/api/";
        }
        this.mHostUrl=hostUrl;
    }

    /**
     * API域名
     * @return
     */
    public String getHostUrl(){
        if (TextUtils.isEmpty(mHostUrl)) {
            initHostUrl();
        }
        return NetContants.API_PRE+"/api/";
    }

    /**
     * 登录协议
     * @return
     */
    public String getLoginServer(){
        return NetContants.getInstance().URL_LOGIN_SERVER();
    }

    /**
     * 根域名
     * @return
     */
    public String getRootHost(){
        return mRootHost;
    }
}