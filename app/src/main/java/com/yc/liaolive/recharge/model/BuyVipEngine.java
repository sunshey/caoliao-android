package com.yc.liaolive.recharge.model;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.BaseEngine;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.pay.alipay.OrderInfo;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by wanglin  on 2018/8/11 10:02.
 */
public class BuyVipEngine extends BaseEngine {

    public BuyVipEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<VipListInfo>> getVipInfo() {
        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().BUY_VIP2(), new TypeToken<ResultInfo<VipListInfo>>() {
        }.getType(), null, getHeaders(), isrsa, iszip, isEncrypt);
    }


    public Observable<ResultInfo<OrderInfo>> createOrder(String payWay, String extra, RechargeGoodsInfo rechargeGoodsInfo) {
        Map<String, String> params = new HashMap<>();
        params.put("app_id", "1");
        params.put("title", rechargeGoodsInfo.getName());
        params.put("price_total", String.valueOf(rechargeGoodsInfo.getPrice()));
        params.put("money", String.valueOf(rechargeGoodsInfo.getPrice()));
        params.put("imeil", GoagalInfo.get().uuid);
        params.put("pay_way_name", payWay);
        params.put("goods_list", extra);

        return HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().CREATE_ORDES(), new TypeToken<ResultInfo<OrderInfo>>() {
        }.getType(), params, getHeaders(), isrsa, iszip, isEncrypt);
    }
}
