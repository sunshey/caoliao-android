package com.yc.liaolive.pay.model;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.BaseEngine;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.user.manager.UserManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * 支付中转页网络请求
 * Created by yangxueq  on 2018/10/31
 */
public class AlipayInterimEngine extends BaseEngine {

    public AlipayInterimEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo> getOrdersPayStatus(String order_sn) {
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", order_sn);
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().ORDES_QUERY(),
                new TypeToken<ResultInfo>() {}.getType(),
                params, getHeaders(), isrsa, iszip, isEncrypt);
    }


    public Observable<ResultInfo<OrderInfo>> orderPayagain(String order_sn) {
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", order_sn);

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().ORDES_PAYAGAIN(), new TypeToken<ResultInfo<OrderInfo>>() {
        }.getType(), params, getHeaders(), isrsa, iszip, isEncrypt);

    }

    public Observable<ResultInfo<JSONObject>> orderCancle(String order_sn) {
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", order_sn);
        params.put("userid", UserManager.getInstance().getUserId());

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().ORDES_CANCEL(), new TypeToken<ResultInfo<JSONObject>>() {
        }.getType(), params, getHeaders(), isrsa, iszip, isEncrypt);

    }
}
