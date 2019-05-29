package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.UserLevelInfo;
import com.yc.liaolive.contants.NetContants;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/8 09:57.
 */
public class UserLevelEngine extends BaseEngine {

    public UserLevelEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<UserLevelInfo>> getUserLevel(String userid) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_RANK(), new TypeToken<ResultInfo<UserLevelInfo>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
