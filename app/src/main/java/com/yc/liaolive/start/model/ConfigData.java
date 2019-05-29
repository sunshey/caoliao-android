package com.yc.liaolive.start.model;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.AppConfigInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.start.model.bean.ConfigBean;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.InitUtils;
import com.yc.liaolive.util.Utils;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

/**
 * 获取配置信息
 * Created by yangxueqin on 2019/1/21.
 */

public class ConfigData {

    public static Observable<ResultInfo<ConfigBean>> getConfig() {
        Map<String,String> params=new HashMap<>();
        AppConfigInfo configInfo= InitUtils.get().getConfigInfo(AppEngine.getApplication());
        if(null!=configInfo){
            params.put("site_id",configInfo.getSite_id());
            params.put("soft_id",configInfo.getSoft_id());
            params.put("node_id",configInfo.getNode_id());
            params.put("node_url",configInfo.getNode_url());
        }
        params.put("app_version", String.valueOf(Utils.getVersionCode()));
        params.put("agent_id", ChannelUtls.getInstance().getAgentId());
        params.put("app_name",AppEngine.getApplication().getResources().getString(R.string.app_name));
        return HttpCoreEngin.get(AppEngine.getApplication()).rxpost(NetContants.getInstance().URL_USER_CONFIG(),
                new TypeToken<ResultInfo<ConfigBean>>() {}.getType(), params, RxBasePresenter.isRsa, RxBasePresenter.isZip, RxBasePresenter.isEncryptResponse);
    }
}