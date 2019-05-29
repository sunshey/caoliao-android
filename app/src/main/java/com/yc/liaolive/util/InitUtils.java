package com.yc.liaolive.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.google.gson.Gson;
import com.kaikai.securityhttp.utils.FileUtil;
import com.kaikai.securityhttp.utils.LogUtil;
import com.yc.liaolive.bean.AppConfigInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * TinyHung@Outlook.com
 * 2018/10/26
 */

public class InitUtils {

    private static final String TAG = "InitUtils";

    public static InitUtils get() {
       return new InitUtils();
    }

    public AppConfigInfo getConfigInfo(Context context){
        String result1 = null;
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zf = null;
        try {
            zf = new ZipFile(sourceDir);
            ZipEntry ze1 = zf.getEntry("META-INF/channelconfig.json");
            InputStream in1 = zf.getInputStream(ze1);
            result1 = FileUtil.readString(in1);
            LogUtil.msg("包渠道->" + result1);
        } catch (Exception e) {
            LogUtil.msg("包渠道-> apk中gamechannel文件不存在");
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
            return getChannelInfo(result1);
        }
    }

    private AppConfigInfo getChannelInfo(String result1) {
        try {
            return new Gson().fromJson(result1, AppConfigInfo.class);
        } catch (Exception e) {
            LogUtil.msg("包渠道信息解析错误->" + e.getMessage());
        }
        return null;
    }
}
