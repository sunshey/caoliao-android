package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.IHide;
import com.yc.liaolive.base.ILoading;
import com.yc.liaolive.base.INoData;
import com.yc.liaolive.base.INoNet;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;

/**
 * Created by wanglin  on 2018/8/11 10:14.
 */
public interface BuyVipContract {

    interface View extends BaseContract.BaseView, ILoading, INoNet, INoData, IHide {
        void showOrderSuccess(OrderInfo data, String rechargeGoodsInfo);
        void showCreateOlderError(int code, String netRequstJsonError);
        //支付繁忙，弹框提示
        void showCantPayError(int code, String msg);
        //支付结果
        void showRechardeResult(CheckOrderBean data);
        void showRechardeError(int code,String msg);
        //会员信息相关
        void showVipLits(VipListInfo vipListInfo);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void createOrder(String payWay, String extra, RechargeGoodsInfo rechargeGoodsInfo);
        //订单校验
        void checkOrder(String orderNumber);
        //获取VIP套餐信息
        void getVipList();
    }
}
