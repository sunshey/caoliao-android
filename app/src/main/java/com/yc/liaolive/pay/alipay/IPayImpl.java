package com.yc.liaolive.pay.alipay;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangkai on 2017/3/17.
 */

public abstract class IPayImpl {

    protected static final String appidStr = "?app_id=2";

    public static boolean isGen() {
        return isGen;
    }

    public static void setIsGen(boolean isGen) {
        IPayImpl.isGen = isGen;
    }

    protected static boolean isGen = false;

    public static OrderInfo uOrderInfo;
    public static IPayCallback uiPayCallback;
    public static String appid;
    protected Activity mContext;

    public Handler mHandler = new Handler();

    public IPayImpl(Activity context) {
        this.mContext = context;
    }

    public abstract void pay(OrderInfo orderInfo, IPayCallback iPayCallback);

    public Object befor(Object... obj) {
        return null;
    }

    public Object after(Object... obj) {
        return null;
    }

    protected String get(String cStr, String dStr) {
        return cStr == null || cStr.isEmpty() ? dStr : cStr;
    }



}
