package com.yc.liaolive.pay.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.pay.PayUtils;
import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.IWXPay1Impl;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.manager.AlipayInterimActivityPresenter;
import com.yc.liaolive.util.ToastUtils;

/**
 * 微信支付中转页，用于多个微信时取消动作
 * Created by yangxueqin on 2018/10/31.
 */

public class WXPayInterimActivity extends Activity {

    private OrderInfo orderInfo;

    //是否已经跳转微信支付，为true时处理
    private boolean hasJump = false;

    //是否拿到了支付回调
    private boolean hasPayCallback = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 启动微信支付中转页
     */
    public static void start(OrderInfo orderInfo) {
        Intent intent = new Intent(AppEngine.getApplication(), WXPayInterimActivity.class);
        intent.putExtra("orderInfo", orderInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppEngine.getApplication().startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasJump && !hasPayCallback) {
            hasJump = true;
            orderInfo = (OrderInfo) getIntent().getSerializableExtra("orderInfo");
            new IWXPay1Impl(this).pay(orderInfo, new WXPayCallback());
        } else if (!hasPayCallback) {
            //已跳转微信支付，但是回到页面时无支付回调
            orderInfo.setMessage("支付取消");
            if (PayUtils.getInstance().getCallback() != null) {
                PayUtils.getInstance().getCallback().onCancel(orderInfo);
            }
            cancelOrder ();
            finish();
        }
    }

    public void payConfirmSuccess() {
        //支付成功
        if (PayUtils.getInstance().getCallback() != null) {
            PayUtils.getInstance().getCallback().onSuccess(orderInfo);
        }
        finish();
    }

    class WXPayCallback implements IPayCallback {

        @Override
        public void onSuccess(OrderInfo orderInfo) {
            hasPayCallback = true;
//            mPresenter.ordersQuery(3, orderInfo.getCharge_order_sn());
            payConfirmSuccess();
        }

        @Override
        public void onFailure(OrderInfo orderInfo) {
            hasPayCallback = true;
            ToastUtils.showCenterToast(orderInfo.getMessage());
            if (PayUtils.getInstance().getCallback() != null) {
                PayUtils.getInstance().getCallback().onFailure(orderInfo);
            }
            cancelOrder ();
            finish();
        }

        @Override
        public void onCancel(OrderInfo orderInfo) {
            hasPayCallback = true;
            ToastUtils.showCenterToast(orderInfo.getMessage());
            if (PayUtils.getInstance().getCallback() != null) {
                PayUtils.getInstance().getCallback().onCancel(orderInfo);
            }
            cancelOrder ();
            finish();
        }
    }

    /**
     * 取消支付
     */
    private void cancelOrder () {
        new AlipayInterimActivityPresenter(this).orderCancel(orderInfo.getCharge_order_sn());
    }
}
