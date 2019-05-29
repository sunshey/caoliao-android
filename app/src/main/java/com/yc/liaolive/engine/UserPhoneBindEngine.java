package com.yc.liaolive.engine;


import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.NetContants;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/6 21:19.
 */
public class UserPhoneBindEngine extends BaseEngine {
    public UserPhoneBindEngine(Context context) {
        super(context);
    }


    public Observable<ResultInfo<FansInfo>> bindPhone(String userid, String phone, String zone, String code) {
        Map<String, String> params = new HashMap<>();

        params.put("userid", userid);
        params.put("phone", phone);
        params.put("zone", zone);
        params.put("code", code);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_BIND_MOBILE(), new TypeToken<ResultInfo<FansInfo>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
