package com.yc.liaolive.user.model;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.BaseEngine;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.model.bean.SettingActivityMenuBean;
import com.yc.liaolive.util.ChannelUtls;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/8 17:15.
 */
public class SettingEngine extends BaseEngine {
    public SettingEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<String>> setVideoExpire(String userid, String chat_deplete, String time) {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("chat_deplete", chat_deplete);
        params.put("chat_minute", time);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_SET_CHAT_DEPLETE(), new TypeToken<ResultInfo<String>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }

    public Observable<ResultInfo<SettingActivityMenuBean>> getActivityMenu() {

        Map<String, String> params = new HashMap<>();
        params.put("userid", UserManager.getInstance().getUserId());
//        params.put("userid", "24764797");
        params.put("equipment", VideoApplication.mUuid);
        params.put("agent_id", ChannelUtls.getInstance().getAgentId());
        params.put("imeil", VideoApplication.mUuid);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_ACTIVITY_MENU(),
                new TypeToken<ResultInfo<SettingActivityMenuBean>>() {}.getType(),
                params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
