package com.yc.liaolive.recharge.manager;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.pay.PayConfig;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;
import com.yc.liaolive.recharge.model.BuyVipEngine;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.recharge.ui.VipRewardDialogActivity;
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.BindMobileManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.ToastUtils;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.yc.liaolive.contants.NetContants.API_RESULT_CANT_PAY_CODE;

/**
 * Created by wanglin  on 2018/8/11 10:14.
 */
public class BuyVipPresenter extends RxBasePresenter<BuyVipContract.View> implements BuyVipContract.Presenter<BuyVipContract.View> {

    private BuyVipEngine mEngine;
    private LoadingProgressView mProgressView;
    private int count = 3;
    private Activity activity;

    public BuyVipPresenter(Activity activity) {
        this.activity = activity;
        mEngine = new BuyVipEngine(activity);
        mProgressView = new LoadingProgressView(activity);
    }

    /**
     * 创建订单
     * @param payWay
     * @param extra
     * @param rechargeGoodsInfo
     */
    @Override
    public void createOrder(final String payWay, String extra, final RechargeGoodsInfo rechargeGoodsInfo) {
        if (PayConfig.wx_pay.equals(payWay)) {
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(mContext, null);
            if (!iwxapi.isWXAppInstalled()) {
                ToastUtils.showCenterToast("未检测到微信APP");
                return;
            }
        }

        if (null != mProgressView &&!mProgressView.isShowing()) mProgressView.showMessage("支付准备中...");
        count=3;
        Subscription subscription = mEngine.createOrder(payWay, extra, rechargeGoodsInfo).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<OrderInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                dissmis();
                if(null!=mView) mView.showCreateOlderError(-1,"创建订单失败");
            }

            @Override
            public void onNext(ResultInfo<OrderInfo> data) {
                if(null!=mView){
                    if(null!=data){
                        if(data.getCode() == HttpConfig.STATUS_OK) {
                            mView.showOrderSuccess(data.getData(), payWay);
                        } else if (API_RESULT_CANT_PAY_CODE == data.getCode()) {
                            mView.showCantPayError(data.getCode(), data.getMsg());
                        } else {
                            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
                            String content="创建支付订单失败。渠道："+payWay+",errorCode:"+data.getCode()+",errorMsg:"+data.getMsg()+",appSign:"+appSign;
                            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
                            dissmis();
                            mView.showCreateOlderError(data.getCode(), data.getMsg());
                        }
                    }else{
                        dissmis();
                        mView.showCreateOlderError(-1,"请求失败");
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 订单校验
     * @param orderNumber
     */
    @Override
    public void checkOrder(final String orderNumber) {
        final Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_RECHARGE_CHECKORDER());
        params.put("order_sn", orderNumber);
        if (null!= mProgressView && mProgressView.isShowing()) {
            mProgressView.setMessage("校验订单中...");
        }else if(null!= mProgressView){
            mProgressView.showMessage("校验订单中...");
        }
        //延时校验订单
        new android.os.Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_RECHARGE_CHECKORDER(),
                        new TypeToken<ResultInfo<CheckOrderBean>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ResultInfo<CheckOrderBean>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dissmis();
                        if (null != mView) mView.showRechardeError(-1, NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<CheckOrderBean> data) {
                        count--;
                        if (null != data) {
                            if (NetContants.API_RESULT_CODE == data.getCode()) {
                                dissmis();
                                CheckOrderBean bean = data.getData();
                                if (bean != null && bean.getPopup_page() != null
                                        && bean.getPopup_page().getList() != null
                                        && bean.getPopup_page().getList().size() > 0) {
                                    UserManager.getInstance().setVip_phone(1);
                                    VipRewardDialogActivity.startRewardDialog(0, bean.getPopup_page());
                                }

                                if (null != mView){
                                    mView.showRechardeResult(bean);
                                }

                                if (bean != null && bean.getBind_mobile() == 2) {
                                    showBindMobileDialog(bean);
                                } else {

                                }

                            } else {
                                if (count <= 0) {
                                    dissmis();
//                                    //过滤第一遍订单错误提示
//                                    if(!"order_sn error".equals(data.getMsg())){
                                        if (null != mView) mView.showRechardeError(data.getCode(), NetContants.getErrorMsg(data));
//                                    }
                                } else {
                                    checkOrder(orderNumber);
                                }
                            }
                        } else {
                            if (count <= 0) {
                                dissmis();
                                if (null != mView)
                                    mView.showRechardeError(-1, NetContants.NET_REQUST_ERROR);
                            } else {
                                checkOrder(orderNumber);
                            }
                        }
                    }
                });
                addSubscrebe(subscription);
            }
        }, SystemClock.uptimeMillis()+2000);
    }

    /**
     * 获取VIP套餐信息
     */
    @Override
    public void getVipList() {
        mView.showLoading();
        Subscription subscription = mEngine.getVipInfo().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<VipListInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<VipListInfo> data) {
                if (data != null) {
                    if (data.getCode() == HttpConfig.STATUS_OK && data.getData() != null) {
                        mView.showVipLits(data.getData());
                        mView.hide();
                    } else {
                        mView.showNoData();
                    }
                } else {
                    mView.showNoNet();
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 关闭提示弹窗
     */
    public void dissmis() {
        if (null != mProgressView && mProgressView.isShowing()){
            mProgressView.dismiss();
        }
    }

    /**
     * 恢复查询次数
     * @param count
     */
    public void setCount(int count) {
        this.count=count;
    }

    /**
     * 显示绑定手机号的弹窗
     */
    public void showBindMobileDialog(final CheckOrderBean bean) {
        if (activity != null && !activity.isFinishing()) {
            QuireDialog dialog = QuireDialog.getInstance(activity)
                    .showTitle(false)
                    .setContentText("<br>为了防止您的账户丢失，<br>强烈建议您绑定手机号！")
                    .setCancelTitleVisible(View.GONE)
                    .setSubmitTitleText("去绑定")
                    .showCloseBtn(true)
                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent() {
                            BindMobileManager.getInstance().goBingMobile(false)
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (null != mView){
                                                mView.showRechardeResult(bean);
                                            }
                                        }
                                    });
                        }
                    })
                    .setDialogCancelable(true)
                    .setDialogCanceledOnTouchOutside(true);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (null != mView){
                        mView.showRechardeResult(bean);
                    }
                }
            });
            dialog.show();
        }
    }
}
