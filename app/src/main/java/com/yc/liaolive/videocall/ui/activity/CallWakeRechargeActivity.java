package com.yc.liaolive.videocall.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.OlderExtra;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityCallWakeRechargeBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.pay.PayConfig;
import com.yc.liaolive.pay.PayUtils;
import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;
import com.yc.liaolive.recharge.adapter.RechargeGoldItemAdapter;
import com.yc.liaolive.recharge.manager.BuyVipPresenter;
import com.yc.liaolive.recharge.manager.GoodsPresenter;
import com.yc.liaolive.recharge.model.bean.RechargeBean;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.recharge.ui.WebPayActivity;
import com.yc.liaolive.recharge.view.PayChanlSelectedLayout;
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.ui.contract.GoodsContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.PayWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/22
 * 视频通话界面的快速充值
 */

public class CallWakeRechargeActivity extends TopBaseActivity implements BuyVipContract.View, GoodsContract.View {

    private static final String TAG = "CallWakeRechargeActivity";
    private ActivityCallWakeRechargeBinding bindingView;
    private RechargeGoldItemAdapter         mAdapter;
    private int                             mPosition=0;
    private RechargeGoodsInfo               mRechargeGoodsInfo=null;
    private BuyVipPresenter                 mPresenter;
    private GoodsPresenter                  mGoodsPresenter;

    public static void start(Activity context) {
        Intent intent=new Intent(context,CallWakeRechargeActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.menu_enter,0);//进场动画
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_call_wake_recharge);
        setActivityLayoutParams();
        mGoodsPresenter = new GoodsPresenter();
        mGoodsPresenter.attachView(this);
        mPresenter = new BuyVipPresenter(this);
        mPresenter.attachView(this);
        setFinishOnTouchOutside(true);//允许点击外部关闭Activity
        initViews();
        mGoodsPresenter.getGoldGoods(14,true);
    }

    /**
     * 组件初始化
     */
    private void initViews() {
        bindingView.tvRechargeTips.setText(getResources().getString(R.string.call_wake_recharge_tips));
        bindingView.tvRechargeTitle.setText("钻石不足");
        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        bindingView.recyclerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(5f)));
        mAdapter = new RechargeGoldItemAdapter(null);
        mAdapter.setSelectedDrawable(getResources().getDrawable(R.drawable.bg_gift_item_appstyle_true));
        mAdapter.setUnSelectedDrawable(getResources().getDrawable(R.drawable.bg_gift_item_appstyle));
        mAdapter.setItemStyle(1);
        bindingView.recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    RechargeGoodsInfo item = (RechargeGoodsInfo) view.getTag();
                    if(item.getItemType()==RechargeGoldItemAdapter.ITEM_TYPE_GOLD){
                        mAdapter.getData().get(mPosition).setSelected(false);
                        mAdapter.notifyItemChanged(mPosition,"update");
                        mRechargeGoodsInfo = mAdapter.getData().get(position);
                        mRechargeGoodsInfo.setSelected(true);
                        mAdapter.notifyItemChanged(position,"update");
                        mPosition=position;
                        if(bindingView.viewPay.getVisibility()!=View.VISIBLE){
                            bindingView.viewPay.setVisibility(View.VISIBLE);
                            bindingView.tvRechargeTitle.setText("选择充值方式");
                        }
                    }
                }
            }
        });
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                        //返回时间
                    case R.id.btn_back:
                        if(bindingView.viewPay.getVisibility()==View.VISIBLE){
                            bindingView.viewPay.setVisibility(View.GONE);
                            bindingView.tvRechargeTitle.setText("钻石不足");
                            return;
                        }
                        finish();
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        //渠道监听
//        bindingView.viewPayChannel.setZfbSelectedDrawable(getResources().getDrawable(R.drawable.vip_pay_selector1));
//        bindingView.viewPayChannel.setWxSelectedDrawable(getResources().getDrawable(R.drawable.vip_pay_selector1));
        bindingView.viewPayChannel.setOnPayChanlChangedListener(new PayChanlSelectedLayout.OnPayChanlChangedListener() {
            @Override
            public void onPayChanlChanged(int chanl) {
                recharg(chanl,mRechargeGoodsInfo);
            }
        });

        //支付监听
        bindingView.payWebView.setOnFunctionListener(new PayWebView.OnFunctionListener() {
            @Override
            public void weXinPay(String url) {
                openWxpay(url);
            }

            @Override
            public void aliPay(String url) {
                openAlipay(url);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=bindingView){
            String olderSn = bindingView.payWebView.getOlderSn();
            if(!TextUtils.isEmpty(olderSn)){
                VideoApplication.getInstance().setMineRefresh(true);
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
                if(null!=mPresenter) {
                    mPresenter.setCount(3);//还原查询次数
                    mPresenter.checkOrder(olderSn);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(bindingView.viewPay.getVisibility()==View.VISIBLE){
            bindingView.viewPay.setVisibility(View.GONE);
            bindingView.tvRechargeTitle.setText("钻石不足");
            return;
        }
        super.onBackPressed();
    }

    /**
     * 充值
     * @param payMode
     * @param rechargeInfo
     */
    public void recharg(int payMode, RechargeGoodsInfo rechargeInfo) {
        if (null == rechargeInfo || null == mPresenter) return;
        List<OlderExtra> olderExtras = new ArrayList<>();
        OlderExtra extra = new OlderExtra();
        extra.setGood_id(String.valueOf(rechargeInfo.getId()));
        extra.setNum(1);
        olderExtras.add(extra);
//        String s1 = JSONArray.toJSONString(olderExtras);
        if (null != mPresenter) mPresenter.createOrder(0 == payMode ? PayConfig.ali_pay : PayConfig.wx_pay, new Gson().toJson(olderExtras), rechargeInfo);
    }

    /**
     * H5微信支付
     * @param url
     */
    private void openWxpay(String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
            String content="H5微信支付唤起微信客户端失败，可能原因：未安装微信客户端、系统未识别到Scheme头协议，errorCode:"+0+",errorMsg:"+e.getMessage()+",appSign:"+appSign;
            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
        }
    }

    /**
     * H5支付宝支付
     * @param url
     */
    public void openAlipay(String url) {
        try {
            Intent intent;
            intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setComponent(null);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
            String content="H5支付宝支付唤起支付宝客户端失败，可能原因：未安支付宝客户端、系统未识别到Scheme头协议，errorCode:"+0+",errorMsg:"+e.getMessage()+",appSign:"+appSign;
            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
        }
    }

    /**
     * 设置适配器新数据
     * @param goods
     */
    private void setNewGoods(List<RechargeGoodsInfo> goods) {
        if(null!=goods&&goods.size()>0&&null!=mAdapter){
            mAdapter.setNewData(goods);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mGoodsPresenter) mGoodsPresenter.detachView();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.menu_exit);//出场动画
    }

    @Override
    public void hide() {

    }

    @Override
    public void showNoNet() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showNoData() {

    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showOrderSuccess(OrderInfo data, String payway) {
        if(CallWakeRechargeActivity.this.isFinishing()) return;
        if(null==bindingView) return;
        if (data.getPayway_info() != null && 2 == data.getPayway_info().getTrade_type()) {
            //H5支付
//            if(null != bindingView) {
//                bindingView.payWebView.starPlay(data.getCharge_order_sn(), data.getPayurl(), data.getPayway_info().getAuth_domain());
//            }
            if(null!=mPresenter){
                mPresenter.dissmis();
            }
            Intent intent=new Intent(CallWakeRechargeActivity.this,WebPayActivity.class);
            intent.putExtra("payurl",data.getPayurl());
            intent.putExtra("orderID",data.getCharge_order_sn());
            startActivityForResult(intent,Constant.PAY_REQUST);
        } else if(!TextUtils.isEmpty(data.getPayurl()) && !data.getPayurl().startsWith("alipay_sdk")){
            //微信支付
            if(data.getPayurl().startsWith("weixin://")){
                openWxpay(data.getPayurl());
                if(null!=bindingView) bindingView.payWebView.setOlderSn(data.getCharge_order_sn());
            }
            //原生支付
        }else{
            if(null!=bindingView) bindingView.payWebView.setOlderSn("");
            int paymethods;
            if (PayConfig.ali_pay.equals(payway)) {
                paymethods = PayConfig.ALI;
            } else {
                paymethods = PayConfig.WX;
            }

            PayUtils.getInstance().get(this).pay(paymethods, data, new IPayCallback() {
                @Override
                public void onSuccess(final OrderInfo orderInfo) {
                    VideoApplication.getInstance().setMineRefresh(true);
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
                    if(null!=mPresenter) mPresenter.checkOrder(orderInfo.getCharge_order_sn());
                }

                @Override
                public void onFailure(OrderInfo orderInfo) {
                    if(null!=mPresenter) mPresenter.dissmis();
                }

                @Override
                public void onCancel(OrderInfo orderInfo) {
                    if(null!=mPresenter) mPresenter.dissmis();
                }
            });
        }
    }

    @Override
    public void showCreateOlderError(int code, String netRequstJsonError) {
        ToastUtils.showCenterToast(netRequstJsonError);
        VideoApplication.getInstance().setMineRefresh(true);
        if(null!=bindingView) bindingView.payWebView.setTag(null);
    }

    @Override
    public void showCantPayError(int code, String msg) {
        QuireDialog.getInstance(CallWakeRechargeActivity.this).showCloseBtn(false)
                .showTitle(false)
                .setSubmitTitleText("确定")
                .setContentText(msg)
                .setCancelTitleVisible(View.GONE)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        super.onConsent();
                        if(null!=mPresenter) mPresenter.dissmis();
                    }
                }).setDialogCanceledOnTouchOutside(false).setDialogCancelable(false).show();
    }

    @Override
    public void showRechardeResult(CheckOrderBean data) {
        UserManager.getInstance().getFullUserData(UserManager.getInstance().getUserId(), UserManager.getInstance().getUserId(), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                finish();
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                finish();
            }
        });

    }

    @Override
    public void showRechardeError(int code, String msg) {
        ToastUtils.showCenterToast(msg);
    }

    @Override
    public void showVipLits(VipListInfo vipListInfo) {

    }

    @Override
    public void showGoldInfo(RechargeBean data) {
        if(null!=bindingView){
            bindingView.viewPayChannel.setPayListConfig(data.getPay_config(), CallWakeRechargeActivity.this);
        }
        if(null!=data.getList()){
            setNewGoods(data.getList());
        }
    }

    @Override
    public void showGoldEmpty() {
        ToastUtils.showCenterToast("获取充值数据为空，请稍后重试");
        finish();
    }

    @Override
    public void showGoldError(int code, String errorMsg) {
        ToastUtils.showCenterToast("获取充值数据失败，请稍后重试");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.PAY_REQUST==requestCode&&Constant.PAY_RESULT==resultCode){
            String stringExtra = data.getStringExtra("olderid");
            if(!TextUtils.isEmpty(stringExtra)){
                VideoApplication.getInstance().setMineRefresh(true);
                if(null!=mPresenter) {
                    mPresenter.setCount(3);//还原查询次数
                    mPresenter.checkOrder(stringExtra);
                }
            }
        }
    }
}
