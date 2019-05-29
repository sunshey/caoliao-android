package com.yc.liaolive.pay.alipay;

/**
 * Created by zhangkai on 2017/3/17.
 */

public interface IPayCallback {
    void onSuccess(OrderInfo orderInfo);

    void onFailure(OrderInfo orderInfo);

    void onCancel(OrderInfo orderInfo);
}
