package com.yc.liaolive.engine;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.HelpInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.user.manager.UserManager;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/7/8 16:36.
 */
public class HelpDetailEngine extends BaseEngine {
    public HelpDetailEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<HelpInfo>> getHelpInfoDetail(String helpid) {
        Map<String, String> params = new HashMap<>();
        params.put("help_id", helpid);
        params.put("userid", UserManager.getInstance().getUserId());

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_HELP_INFO(), new TypeToken<ResultInfo<HelpInfo>>() {
        }.getType(), params,getHeaders(), isrsa, iszip, isEncrypt);
    }
}
