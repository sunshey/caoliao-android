package com.yc.liaolive.index.model;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.BaseEngine;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.index.model.bean.NearbyUserBean;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * 附近用户  请求
 * Created by yangxueqin on 19/1/18.
 */
public class NearbyUserEngine extends BaseEngine {
    public NearbyUserEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<NearbyUserBean>> getListData() {

        Map<String, String> params = new HashMap<>();
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_NEARBY(),
                new TypeToken<ResultInfo<NearbyUserBean>>() {}.getType(),
                params,getHeaders(), isrsa, iszip, isEncrypt);
    }

}
