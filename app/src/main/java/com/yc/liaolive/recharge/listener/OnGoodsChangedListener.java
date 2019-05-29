package com.yc.liaolive.recharge.listener;

import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 */

public interface OnGoodsChangedListener {

    void onGoodsChanged(RechargeGoodsInfo goodsInfo);

    void onServer();

    void onPayChanlChanged(int chanl);
}
