package com.yc.liaolive.user.model;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.BaseEngine;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.user.model.bean.OnlineUserBean;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * 在线用户列表 请求
 * Created by yangxueqin on 18/12/15.
 */
public class OnlineUserEngine extends BaseEngine {
    public OnlineUserEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<OnlineUserBean>> getListData(String login_time, String userid, int type) {

        Map<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(type));
        params.put("login_time", login_time);
        params.put("userid", userid);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ONLINE_LIST(),
                new TypeToken<ResultInfo<OnlineUserBean>>() {}.getType(),
                params,getHeaders(), isrsa, iszip, isEncrypt);
    }

}
