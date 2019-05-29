package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.MyVipInfo;
import com.yc.liaolive.contants.NetContants;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/10 11:15.
 */
public class MyVipEngine extends BaseEngine {
    public MyVipEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<MyVipInfo>> getMyVipInfo(String userid, String to_userid) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("userid", userid);
        parmas.put("to_userid", to_userid);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_MYVIP(), new TypeToken<ResultInfo<MyVipInfo>>() {
                }.getType(), parmas,getHeaders(),
                isrsa, iszip, isEncrypt);
    }
}
