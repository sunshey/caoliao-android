package com.yc.liaolive.pay.manager;

import android.app.Activity;
import android.text.TextUtils;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.pay.IView.IAlipayInterimActivityView;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.AlipayInterimEngine;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;

import org.json.JSONObject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 支付宝支付中转页
 * Created by yangxueqin on 2018/10/30.
 */

public class AlipayInterimActivityPresenter extends RxBasePresenter<IAlipayInterimActivityView> {

    private AlipayInterimEngine mEngine;
    private LoadingProgressView mProgressView;

    public AlipayInterimActivityPresenter(Activity activity) {
        mEngine = new AlipayInterimEngine(activity);
        mProgressView = new LoadingProgressView(activity);
    }

//    /**
//     * 订单查询接口
//     * 点击“已完成支付”调用服务器请求支付状态
//     * @param order_sn  充值订单号
//     */
//    public void ordersQuery(final String order_sn) {
//        if (null != mProgressView &&!mProgressView.isShowing()) {
//            mProgressView.show();
//        }
//
//        Subscription subscription = mEngine.getOrdersPayStatus(order_sn)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResultInfo>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                if(null!=mProgressView) mProgressView.dismiss();
//            }
//
//            @Override
//            public void onNext(ResultInfo data) {
//                Logger.d(TAG,"createOrder--onNext"+data.getData().toString());
//                mProgressView.dismiss();
//                if(null != mView){
//                    if(null != data){
//                        if(data.getCode() == HttpConfig.STATUS_OK){
//                            mView.payConfirmSuccess();
//                        } else if ((data.getCode() == 1001)){
//                            mView.showPayFaildDialog();
//                        } else {
//                            ToastUtils.showCenterToast(data.getMsg());
//                        }
//                    }else{
//                        ToastUtils.showCenterToast("请求失败，请稍后重试");
//                    }
//                }
//            }
//        });
//        addSubscrebe(subscription);
//    }

    /**
     * 查询、继续支付接口
     * @param type 0:点击“继续支付” 调用服务器请求是否可继续支付，或者使用新的payurl支付
     *            1:点击“已完成支付”调用服务器请求支付状态
     *            2、SDK支付成功请求服务器检测支付回调状态
     * @param order_sn  充值订单号
     */
    public void ordersQuery(final int type, final String order_sn) {
        if (null != mProgressView && !mProgressView.isShowing()) {
            mProgressView.show();
        }

        Subscription subscription = mEngine.orderPayagain(order_sn)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<OrderInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null!=mProgressView) mProgressView.dismiss();
                    }

                    @Override
                    public void onNext(ResultInfo<OrderInfo> data) {
                        mProgressView.dismiss();
                        if(null != mView){
                            if(null != data){
                                if(data.getCode() == HttpConfig.STATUS_OK) {
                                    OrderInfo info ;
                                    if (data.getData() != null && !TextUtils.isEmpty(data.getData().getPayurl())) {
                                        info = data.getData();
                                    } else {
                                        info = null;
                                    }
                                    if (type == 0) {
                                        mView.repay(info);
                                    } else  if (type == 1) {
                                        mView.showPayFaildDialog(info);
                                    } else {
                                        ToastUtils.showCenterToast("同步失败，请点击\"已完成支付\"重试");
                                    }
                                } else if (data.getCode() == 400) {
                                    //订单已支付完成
                                    ToastUtils.showCenterToast("支付成功");
                                    mView.payConfirmSuccess();
                                } else {
                                    ToastUtils.showCenterToast(data.getMsg());
                                }
                            }else{
                                ToastUtils.showCenterToast("请求失败，请稍后重试");
                            }
                            if (type == 1) {
                                mView.paySuceessBtnEnable(true);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 发送日志
     * @param orderInfo
     * @param msg
     */
    public void postLog(OrderInfo orderInfo, String msg) {
        orderInfo.setAction_msg(msg);
        ActionLogInfo<OrderInfo> actionLogInfo=new ActionLogInfo();
        actionLogInfo.setData(orderInfo);
        UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_RECHGRE, actionLogInfo,null);
    }

    /**
     * 取消订单
     * @param order_sn
     */
    public void orderCancel(String order_sn) {
        mEngine.orderCancle(order_sn)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResultInfo data) {

            }
        });
    }
}
