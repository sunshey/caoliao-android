package com.yc.liaolive.pay.IView;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.pay.alipay.OrderInfo;


/**
 * Created by yangxueqin on 2018/10/30.
 */

public interface IAlipayInterimActivityView extends BaseContract.BaseView {

    /**
     * @param orderInfo orderInfo 不为空时生成了新的支付单支付
     *                  orderInfo 为空时使用原有payurl支付
     */
    void repay(OrderInfo orderInfo);

    void payConfirmSuccess();

    void showPayFaildDialog(OrderInfo orderInfo);

    void paySuceessBtnEnable(boolean enable);

}
