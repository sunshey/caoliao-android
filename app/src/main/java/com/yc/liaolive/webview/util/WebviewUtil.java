package com.yc.liaolive.webview.util;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.kaikai.securityhttp.domain.GoagalInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.engine.CaoliaoNetEngine;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * webview 工具类 处理head、cookie等信息
 * Created by yangxueqin on 2018/11/22.
 */

public class WebviewUtil {
    private static final String TAG = "WebviewUtil";

    public static WebviewUtil instance;

    public static WebviewUtil getInstance() {
        if (instance == null) {
            instance = new WebviewUtil();
        }
        return instance;
    }

    /**
     * webview加载url中Header信息
     * 可以使用webview.loadUrl(String url, Map<String, String> additionalHttpHeaders)方式加载设置head
     * @return
     */
    public Map<String, String> getHttpHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(CaoliaoNetEngine.getCommonParams());
        return map;
    }

    /**
     * 给定url设置默认cookie；在相同域下，设置相同cookie会覆盖重写
     *
     * @param cookieManager
     * @param url
     * @param mContext
     */
    public void setCookie(CookieManager cookieManager, String url,
                          Context mContext) {
        // 改为通过host来添加cookie
        String host = "";
        try {
            host = getRootDomain(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cookieManager != null) {
            cookieManager.setCookie(host,
                    "userid=" + UserManager.getInstance().getUserId() + ";");
            cookieManager.setCookie(host,
                    "cl_token=" + UserManager.getInstance().getmLoginToken() + ";");
            cookieManager.setCookie(host,
                    "agent_id=" + ChannelUtls.getInstance().getAgentId() + ";");
            cookieManager.setCookie(host,
                    "platform=android;");
            cookieManager.setCookie(host,
                    "imeil=" + GoagalInfo.get().uuid + ";");
            String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                    .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                    .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
            cookieManager.setCookie(host,
                    "sys_version=" + sv + ";");
            cookieManager.setCookie(host,
                    "app_version=" + String.valueOf(Utils.getVersionCode()) + ";");
            try {
                String appName = URLEncoder.encode(AppEngine.getApplication().getResources().getString(R.string.app_name), "UTF-8");
                cookieManager.setCookie(host,
                        "app_name=" + appName + ";");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
        CookieSyncManager.getInstance().sync();
        String cookie = "";
        if (cookieManager != null) {
            cookie = cookieManager.getCookie(host);
        }
    }

    /**
     * 获取本地cookie
     *
     * @param cookieManager
     * @param url
     * @return
     */
    public String getCookie(CookieManager cookieManager, String url) {
        String host = "";
        try {
            host = getRootDomain(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cookie = "";
        if (cookieManager != null) {
            cookie = cookieManager.getCookie(host);
        }
        return cookie;
    }

    /**
     * 给定url设置指定cookie；在相同域下，设置相同cookie会覆盖重写
     *
     * @param cookieManager
     * @param url
     * @param mContext
     */
    public void setCookies(CookieManager cookieManager, String url,
                           Context mContext, String[] cookies) {
        // 改为通过host来添加cookie
        String host = "";
        host = getRootDomain(url);
        if (cookieManager != null) {
            for (int i = 0; i < cookies.length; i++) {
                cookieManager.setCookie(host, cookies[i]);
            }
        }
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 获取url根域名. 如www.caoliao.com取.caoliao.com
     */
    public String getRootDomain(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
        }catch (MalformedURLException e){
            return url;
        }

//        StringBuffer root = new StringBuffer();
//        String[] rootMembers = host.split("\\.");
//
//        //一般情况下，取host的最后2个，如www.juanpi.com取.juanpi.com
//        int n = 2;
//
//        //判断TLD后缀是否超过1个，caoliao.cn，如果是，则判断域名倒数第2个后缀是否是.com等（.com.cn形式）
//        if (rootMembers.length > 2) {
//            String secondLastMember = rootMembers[rootMembers.length - 2];
//
//            if (secondLastMember.matches("^com$|^net$|^gov$|^edu$|^co$|^org$")) {
//                n = 3;
//            }
//        }
//
//        //确定了n的取数后，将结果保存到StringBuffer中，保存顺序是自左至右
//        for (int i = n; i > 0; i--) {
//            try {
//                root.append(".");
//                root.append(rootMembers[host.split("\\.").length - i]);
//            } catch (ArrayIndexOutOfBoundsException e) {
//                Logger.i(TAG, "getRootDomain# wrong url:" + url);
//            }
//        }
//        return root.toString();
        return url;
    }

    /**
     * 清除WebView缓存,必须在主线程调用
     */
    public void clearCache() {
        try {
            WebView web = new WebView(AppEngine.getApplication());
            web.clearCache(true);
            //            web.clearHistory();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
