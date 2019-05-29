package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.BlackListWrapper;
import com.yc.liaolive.contants.NetContants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/10 21:38.
 */
public class BlackListEngine extends BaseEngine {
    public BlackListEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<BlackListWrapper>> getBlackList(String usrid, int page, int page_size) {
        Map<String, String> params = new HashMap<>();

        params.put("userid", usrid);
        params.put("page", page + "");
        params.put("page_size", page_size + "");

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_BLACK_LIST(), new TypeToken<ResultInfo<BlackListWrapper>>() {
        }.getType(), getHeaders(), params, isrsa, iszip, isEncrypt);
    }


    public Observable<ResultInfo<JSONObject>> removeBlackList(String userid, String to_userid) {
        Map<String, String> params = new HashMap<>();

        params.put("userid", userid);
        params.put("black_userid", to_userid);

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_REMOVE_BLACKLIST(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
