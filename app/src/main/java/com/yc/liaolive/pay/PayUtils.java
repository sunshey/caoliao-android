package com.yc.liaolive.pay;

import android.app.Activity;

import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.gui.AlipayInterimActivity;
import com.yc.liaolive.pay.gui.WXPayInterimActivity;

/**
 * Created by wanglin  on 2018/8/11 09:29.
 */
public class PayUtils {

    private static PayUtils instance;

    private IPayCallback callback;

    public static PayUtils getInstance() {
        synchronized (PayUtils.class) {
            if (instance == null) {
                synchronized (PayUtils.class) {
                    instance = new PayUtils();
                }
            }
        }
        return instance;
    }

    public PayUtils get(Activity activity) {
//        wxPay = new IWXPay1Impl(activity);
        return this;
    }

    public void pay(int payway, OrderInfo orderInfo, IPayCallback callback) {
        this.callback = callback;
//        if (wxPay == null) throw new NullPointerException("调用pay方法之前必须先调用get方法");
        if (payway == PayConfig.ALI) {    //支付宝支付
            AlipayInterimActivity.start(orderInfo);
        } else if (payway == PayConfig.WX) { // 微信支付
            WXPayInterimActivity.start(orderInfo);
        }
    }

    public IPayCallback getCallback() {
        return callback;
    }

    public void setCallback(IPayCallback callback) {
        this.callback = callback;
    }
}
