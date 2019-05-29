package com.yc.liaolive.videocall.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.music.player.lib.manager.MusicWindowManager;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.OlderExtra;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityCallRechargeBinding;
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
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.ui.contract.GoodsContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.PayWebView;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 视频通话界面的快速充值
 */

public class CallRechargeActivity extends TopBaseActivity implements BuyVipContract.View, GoodsContract.View {

    private static final String TAG = "CallRechargeActivity";
    private static boolean isRunning;
    private ActivityCallRechargeBinding bindingView;
    private RechargeGoldItemAdapter mAdapter;
    private BuyVipPresenter mPresenter;
    private GoodsPresenter mGoodsPresenter;
    private int mApiType=14;

    public static void start(Activity context) {
        Intent intent=new Intent(context,CallRechargeActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.menu_enter,0);//进场动画
    }

    /**
     * 适合在赠送礼物金币不足，弹出快捷充值入口
     * @param context
     * @param apiType API TYPE   14：视频通话 18：直播间 19:amsr声音  20：amsr视频
     * @param tipsContent 交互文字
     */
    public static void start(Activity context,int apiType,String tipsContent) {
        Intent intent=new Intent(context,CallRechargeActivity.class);
        intent.putExtra("apiType",apiType);
        intent.putExtra("tipsContent",tipsContent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.menu_enter,0);//进场动画
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiType = getIntent().getIntExtra("apiType", 18);
        isRunning=true;
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_call_recharge);
        setActivityLayoutParams();
        mGoodsPresenter = new GoodsPresenter();
        mGoodsPresenter.attachView(this);
        mPresenter = new BuyVipPresenter(this);
        mPresenter.attachView(this);
        setFinishOnTouchOutside(true);//允许点击外部关闭Activity
        initViews();
        mGoodsPresenter.getGoldGoods(mApiType,false);
        MusicWindowManager.getInstance().onInvisible();
    }

    /**
     * 组件初始化
     */
    private void initViews() {
        String tipsContent = getIntent().getStringExtra("tipsContent");
        if(!TextUtils.isEmpty(tipsContent)){
            bindingView.tvTipsContent.setVisibility(View.VISIBLE);
            bindingView.tvTipsContent.setText(tipsContent);
        }
        if (19 == mApiType || 20 ==mApiType) {
            bindingView.tvTipsTitle.setText("一键充值");
        }
        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        bindingView.recyclerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(5f)));
        mAdapter = new RechargeGoldItemAdapter(null);
        bindingView.recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    RechargeGoodsInfo item = (RechargeGoodsInfo) view.getTag();
                    if(item.getItemType()==RechargeGoldItemAdapter.ITEM_TYPE_GOLD){
                        if(mAdapter.getSelectPosition()!=position){
                            mAdapter.getData().get(mAdapter.getSelectPosition()).setSelected(false);
                            mAdapter.notifyItemChanged(mAdapter.getSelectPosition(),"update");
                            mAdapter.getData().get(position).setSelected(true);
                            mAdapter.notifyItemChanged(position,"update");
                        }
                    }
                }
            }
        });
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_close:
                        if (19 == mApiType) {
                            MobclickAgent.onEvent(CallRechargeActivity.this, "amsr_sounds_recharge_close_click");
                        } else if (20 == mApiType){
                            MobclickAgent.onEvent(CallRechargeActivity.this, "amsr_video_recharge_close_click");
                        }
                        finish();
                        break;
                    case R.id.btn_recharge:
                        recharg(bindingView.viewPayChannel.getPayChannel(), mAdapter.getSelectGoodsInfo());
                        if (19 == mApiType) {
                            MobclickAgent.onEvent(CallRechargeActivity.this, "amsr_sounds_recharge_pay_click");
                        } else if (20 == mApiType){
                            MobclickAgent.onEvent(CallRechargeActivity.this, "amsr_video_recharge_pay_click");
                        }
                        break;
                }
            }
        };
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnRecharge.setOnClickListener(onClickListener);
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
        setMoney(UserManager.getInstance().getDiamonds());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=bindingView){
            String olderSn = bindingView.payWebView.getOlderSn();
            if(!TextUtils.isEmpty(olderSn)){
                VideoApplication.getInstance().setMineRefresh(true);
                if(null!=mPresenter) {
                    mPresenter.setCount(3);//还原查询次数
                    mPresenter.checkOrder(olderSn);
                }
            }
        }
    }

    /**
     * 设置余额信息
     * @param pintaiMoney
     */
    private void setMoney(long pintaiMoney) {
        if(null!=bindingView) bindingView.tvMonery.setText(Html.fromHtml("余额：<font color='#FF0000'>"+Utils.formatWan(pintaiMoney,true)+"</font>"));
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
        if (null != mPresenter) mPresenter.createOrder(0 == payMode ? PayConfig.ali_pay : PayConfig.wx_pay,
                new Gson().toJson(olderExtras), rechargeInfo);
    }

    /**
     * 打开WEB微信支付
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
        }
    }

    /**
     * 打来阿里支付
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mGoodsPresenter) mGoodsPresenter.detachView();
        isRunning=false;
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
        if(CallRechargeActivity.this.isFinishing()) return;

        if(null==bindingView) return;
        if (data.getPayway_info() != null && 2 == data.getPayway_info().getTrade_type()) {
            //H5支付
//            if(null != bindingView) {
//                bindingView.payWebView.starPlay(data.getCharge_order_sn(), data.getPayurl(), data.getPayway_info().getAuth_domain());
//            }
            if(null!=mPresenter){
                mPresenter.dissmis();
            }
            Intent intent=new Intent(CallRechargeActivity.this,WebPayActivity.class);
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
        QuireDialog.getInstance(CallRechargeActivity.this).showCloseBtn(false)
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
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_PRIVATE_RECHARGE_SUCCESS);
        UserManager.getInstance().getFullUserData(UserManager.getInstance().getUserId(), UserManager.getInstance().getUserId(), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
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

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void showGoldInfo(RechargeBean data) {
        if(null!=bindingView){
            bindingView.viewPayChannel.setPayListConfig(data.getPay_config(), CallRechargeActivity.this);
        }
        if(null != data.getList() && data.getList().size()>0 && null!=mAdapter){
            mAdapter.setNewData(data.getList());
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
