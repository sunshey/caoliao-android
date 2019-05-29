package com.yc.liaolive.engine;

import android.os.Build;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.AppConfigInfo;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.InitUtils;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.Utils;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求引擎 <br>
 * 公共的响应码可以定义在这个类中，非公共的定义到各自的model层中
 * Created by yangxueqin on 2018/11/22.
 */

public class CaoliaoNetEngine {

    private static final String TAG = "NetEngine";

    public static Map<String, String> getCommonParams() {
        Map<String, String> params = new HashMap<>();
        String deviceID = Utils.getDeviceID(AppEngine.getInstance().getApplication().getApplicationContext());
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
        }
        params.put("agent_id", ChannelUtls.getInstance().getAgentId());
        params.put("ts", System.currentTimeMillis() + "");
        params.put("device_type", "2");
        params.put("device_id", deviceID);
        params.put("um_channel", ChannelUtls.getInstance().getChannel());//友盟渠道
        if (!SharedPreferencesUtil.getInstance().getString("period", "").isEmpty()) {
            params.put("period", SharedPreferencesUtil.getInstance().getString("period", ""));
        }
        if (SharedPreferencesUtil.getInstance().getInt("grade", 0) != 0) {
            params.put("default_grade", SharedPreferencesUtil.getInstance().getInt("grade", 0) + "");
        }
        params.put("imeil", GoagalInfo.get().getUUID(AppEngine.getApplication().getApplicationContext()));
        String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
        params.put("sys_version", sv);
        params.put("app_version", String.valueOf(Utils.getVersionCode()));
        params.put("api_version", "20190124");

        AppConfigInfo configInfo= InitUtils.get().getConfigInfo(AppEngine.getApplication());
        if(null!=configInfo){
            params.put("site_id",configInfo.getSite_id());
            params.put("soft_id",configInfo.getSoft_id());
            params.put("node_id",configInfo.getNode_id());
            params.put("node_url",configInfo.getNode_url());
        }
        params.put("app_name",AppEngine.getApplication().getResources().getString(R.string.app_name));
        return params;
    }
}
