package com.yc.liaolive.util;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.VideoApplication;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 渠道号配置信息
 * Created by yangxueqin on 2018/11/3.
 */

public class ChannelUtls {

    /**
     *  渠道号及ID
     app_server 0
     app_xiaomi 1
     app_huawei 2
     app_vivo 3
     app_oppo 4
     app_tencnet 5
     app_baidu 6
     app_private_test 7
     app_group 8
     app_tieniu 9
     app_web 10
     app_pay 101
     */
    private String AGENT_ID = "";

    private String CHANNLE_NAME = "";

    private String DEFAULT_ID = "0";

    private String DEFAULT_NAME = "app_server";

    private static SharedPreferences sharePreferences;

    private static ChannelUtls instance;

    public static ChannelUtls getInstance () {
        if (instance == null) {
            instance = new ChannelUtls();
            init();
        }
        return instance;
    }

    private static void init () {
        sharePreferences = AppEngine.getApplication().getSharedPreferences("channelUtils", 0);
//        int preAppCode = SharedPreferencesUtil.getInstance().getInt("preVerCode");
//        if (preAppCode != 0 && preAppCode < Utils.getVersionCode()) {
            //版本升级清除渠道号信息重新设置
            sharePreferences.edit().clear().apply();
//        }
    }

    /**
     * 获取渠道名称
     * @return
     */
    public String getChannel() {
        return getChannel(DEFAULT_NAME);
    }

    /**
     * 获取渠道名称
     * @param defaultChannel
     * @return
     */
    private String getChannel(String defaultChannel) {
        if (!TextUtils.isEmpty(CHANNLE_NAME)) {
            return CHANNLE_NAME;
        }

        String nowChannel = sharePreferences.getString("mChannel", "");
        if (!TextUtils.isEmpty(nowChannel)) {
            CHANNLE_NAME = nowChannel;
            return nowChannel;
        }
        setChannelInfoFromApk("channel");
        if (!TextUtils.isEmpty(CHANNLE_NAME)) {
            return CHANNLE_NAME;
        }
        //全部获取失败，使用默认
        return defaultChannel;
    }

    /**
     * 获取渠道号
     * @return
     */
    public String getAgentId() {
        if (!TextUtils.isEmpty(AGENT_ID)) {
            return AGENT_ID;
        }

        String nowChannelId = sharePreferences.getString("mChannelId", "");
        if (!TextUtils.isEmpty(nowChannelId)) {
            AGENT_ID = nowChannelId;
            return nowChannelId;
        }
        setChannelInfoFromApk("channel");
        if (!TextUtils.isEmpty(AGENT_ID)) {
            return AGENT_ID;
        }
        //全部获取失败，使用默认
        return DEFAULT_ID;
    }

    /**
     * 从apk中获取渠道信息信息
     *
     * @param channelKey 批量生成安装包中定义的渠道文件开头 用于文件的精准匹配
     * @return
     */
    private void setChannelInfoFromApk(String channelKey) {
        if (VideoApplication.TEST) {
            AGENT_ID = DEFAULT_ID;
            CHANNLE_NAME = DEFAULT_NAME;
        } else if ("caoliaoFlavorGroup".equals(BuildConfig.FLAVOR)) { //马甲包
            AGENT_ID = "8";
            CHANNLE_NAME = "app_group";
        } else {
            //从apk包中获取
            ApplicationInfo appinfo = AppEngine.getApplication().getApplicationInfo();
            String sourceDir = appinfo.sourceDir;
            //默认放在meta-inf/里， 所以需要再拼接一下
            String key = "META-INF/" + channelKey;
            String ret = "";
            ZipFile zipfile = null;
            try {
                zipfile = new ZipFile(sourceDir);
                Enumeration<?> entries = zipfile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName();
                    if (entryName.startsWith(key)) {
                        ret = entryName;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (zipfile != null) {
                    try {
                        zipfile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String[] split = ret.split("&");
            if (split.length > 2) {
                CHANNLE_NAME = split[1];
                AGENT_ID = split[2];
                SharedPreferences.Editor editor = sharePreferences.edit();
                editor.putString("mChannel", CHANNLE_NAME);
                editor.putString("mChannelId", AGENT_ID);
                editor.apply();
            }
        }
    }
}