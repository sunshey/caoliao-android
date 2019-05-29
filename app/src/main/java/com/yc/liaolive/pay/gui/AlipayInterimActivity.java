package com.yc.liaolive.pay.gui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.HostInfo;
import com.yc.liaolive.databinding.ActivityAlipayInterimBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.pay.IView.IAlipayInterimActivityView;
import com.yc.liaolive.pay.PayUtils;
import com.yc.liaolive.pay.alipay.IAliPay1Impl;
import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.manager.AlipayInterimActivityPresenter;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.ToastUtils;

import org.jetbrains.annotations.Nullable;

/**
 * 支付宝支付中转页，用户支付中、支付失败转态展示、重新支付、手动获取支付成功等
 * Created by yangxueqin on 2018/10/30.
 */

public class AlipayInterimActivity extends BaseActivity<ActivityAlipayInterimBinding> implements IAlipayInterimActivityView, View.OnClickListener {

    private static final String TAG = "AlipayInterimActivity";

    private AlipayInterimActivityPresenter mPresenter;

    private IAliPay1Impl aliPay;

    private OrderInfo orderInfo;

    private CountDownTimer timer; //倒计时计时器

    private AlipayCallback alipayCallback;

    private boolean showInterim = true; //是否显示中转页

    private int payStatues = 0; //支付结果状态 0取消 1成功 -1失败

    public static void start(OrderInfo orderInfo) {
        Intent intent = new Intent(AppEngine.getApplication(), AlipayInterimActivity.class);
        intent.putExtra("orderInfo", orderInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppEngine.getApplication().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderInfo = (OrderInfo) getIntent().getSerializableExtra("orderInfo");
        mPresenter = new AlipayInterimActivityPresenter(this);
        mPresenter.attachView(this);
        aliPay = new IAliPay1Impl(this);
        alipayCallback = new AlipayCallback();
        HostInfo info = (HostInfo) ApplicationManager.getInstance().getCacheExample().getAsObject("init_info");
        showInterim = info == null || info.getAlipay_interim() == 0;
        if (showInterim) {
            intCountDown();
            setContentView(R.layout.activity_alipay_interim);
        }
        startPay();
        setSwipeBackEnable(false);
    }

    @Override
    public void initViews() {
        if (showInterim) {
            bindingView.payBackBtn.setOnClickListener(this);
            bindingView.alipayGoPay.setOnClickListener(this);
            bindingView.alipaySuccess.setOnClickListener(this);
        }
    }

    @Override
    public void initData() {

    }

    private void startPay () {
        aliPay.pay(orderInfo, alipayCallback);
        if (showInterim) {
            bindingView.alipayTimecountTv.setVisibility(View.VISIBLE);
            timer.start();
        }
    }

    /**
     * 初始化倒计时
     */
    private void intCountDown () {
        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bindingView.alipayTimecountTv.setText("正在尝试打开支付宝客户端 " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                bindingView.alipayTimecountTv.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_back_btn:
                onBackPressed();
                break;
            case R.id.alipay_go_pay:
                mPresenter.ordersQuery(0, orderInfo.getCharge_order_sn());
                mPresenter.postLog(orderInfo, "支付宝支付中转页点击继续支付");
                break;
            case R.id.alipay_success:
                paySuceessBtnEnable(false);
                mPresenter.ordersQuery(1, orderInfo.getCharge_order_sn());
                mPresenter.postLog(orderInfo, "支付宝支付中转页点击已完成付款");
                break;
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void repay(OrderInfo orderInfo) {
        if (orderInfo != null && !TextUtils.isEmpty(orderInfo.getPayurl())) {
            this.orderInfo = orderInfo;
        }
        startPay();
    }

    @Override
    public void payConfirmSuccess() {
        //支付成功
        if (PayUtils.getInstance().getCallback() != null) {
            PayUtils.getInstance().getCallback().onSuccess(orderInfo);
        }
        finish();
    }

    @Override
    public void showPayFaildDialog(final OrderInfo data) {
        QuireDialog.getInstance(AlipayInterimActivity.this)
                .showTitle(false)
                .setContentText("暂时无法获取支付结果，稍后请至支付宝账单或交易发起应用/网站查看")
                .setCancelTitleText("取消").setCancelTitleTextColor(Color.parseColor("#00aaee"))
                .setSubmitTitleText("继续支付").setSubmitTitleTextColor(Color.parseColor("#00aaee"))
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        repay(data);
                        mPresenter.postLog(orderInfo, "支付宝支付中转页弹窗点击继续支付");
                    }

                    @Override
                    public void onRefuse() {
                        mPresenter.postLog(orderInfo, "支付宝支付中转页弹窗点击取消");
                    }
                }).show();
    }

    @Override
    public void paySuceessBtnEnable(boolean enable) {
        bindingView.alipaySuccess.setEnabled(enable);
        if (true) {
            bindingView.alipaySuccess.setTextColor(Color.parseColor("#00aaee"));
        } else {
            bindingView.alipaySuccess.setTextColor(Color.parseColor("#dbdbdb"));
        }
    }

    class AlipayCallback implements IPayCallback {

        @Override
        public void onSuccess(OrderInfo orderInfo) {
            payStatues = 1;
            //支付宝支付成功，请求服务器同步支付结果 暂时由各个调用方处理，后续优化
//            mPresenter.ordersQuery(2, orderInfo.getCharge_order_sn());
            payConfirmSuccess();
        }

        @Override
        public void onFailure(OrderInfo orderInfo) {
            payStatues = -1;
            ToastUtils.showCenterToast(orderInfo.getMessage());
            if (showInterim) {
                if (bindingView.alipayTimecountTv.isShown()) {
                    //倒计时结束并隐藏
                    if (timer != null) {
                        timer.cancel();
                    }
                    bindingView.alipayTimecountTv.setVisibility(View.GONE);
                }
            } else {
                mPresenter.orderCancel(orderInfo.getCharge_order_sn());
                if (PayUtils.getInstance().getCallback() != null) {
                    PayUtils.getInstance().getCallback().onFailure(orderInfo);
                }
                finish();
            }
        }


        @Override
        public void onCancel(OrderInfo orderInfo) {
            payStatues = 0;
            if (showInterim) {
                if (bindingView.alipayTimecountTv.isShown()) {
                    //倒计时结束并隐藏
                    if (timer != null) {
                        timer.cancel();
                    }
                    bindingView.alipayTimecountTv.setVisibility(View.GONE);
                }
            } else {
                ToastUtils.showCenterToast(orderInfo.getMessage());
                mPresenter.orderCancel(orderInfo.getCharge_order_sn());
                if (PayUtils.getInstance().getCallback() != null) {
                    PayUtils.getInstance().getCallback().onCancel(orderInfo);
                }
                finish();
            }
            mPresenter.postLog(orderInfo, "支付宝取消支付");
        }
    }



    @Override
    public void onBackPressed() {
        ToastUtils.showCenterToast(orderInfo.getMessage());
        mPresenter.orderCancel(orderInfo.getCharge_order_sn());
        mPresenter.postLog(orderInfo, "支付宝支付中转页点击返回");
        if (PayUtils.getInstance().getCallback() != null) {
            if (payStatues ==  -1) {
                PayUtils.getInstance().getCallback().onFailure(orderInfo);
            } else {
                PayUtils.getInstance().getCallback().onCancel(orderInfo);
            }
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onBackPressed();
    }
}
