package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.HelpInfoWrapper;
import com.yc.liaolive.contants.NetContants;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/8 15:45.
 */
public class HelpEngine extends BaseEngine {
    public HelpEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<HelpInfoWrapper>> getHelpList(String userid) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HELP(), new TypeToken<ResultInfo<HelpInfoWrapper>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
