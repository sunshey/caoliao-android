package com.yc.liaolive.recharge.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.androidkun.xtablayout.XTabLayout;
import com.google.gson.Gson;
import com.tencent.TIMConversationType;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.OlderExtra;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityVipBinding;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.pay.PayConfig;
import com.yc.liaolive.pay.PayUtils;
import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;
import com.yc.liaolive.recharge.manager.BuyVipPresenter;
import com.yc.liaolive.recharge.model.bean.PayConfigBean;
import com.yc.liaolive.recharge.model.bean.RechargeInfo;
import com.yc.liaolive.recharge.ui.fragment.GoodsDiamondFragment;
import com.yc.liaolive.recharge.ui.fragment.GoodsVipFragment;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.ui.dialog.PayWayQuire;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.VipRewardActivity;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.PayWebView;
import org.jetbrains.annotations.Nullable;
import org.simple.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/22
 * 会员、金币充值
 */

public class VipActivity extends BaseActivity<ActivityVipBinding> implements BuyVipContract.View {

    private static final String TAG = "VipActivity";
    private List<Fragment> fragments = new ArrayList<>();
    private int mIndex;
    private BuyVipPresenter mPresenter;
    private RechargeInfo mGoodsInfo;
    private List<String> mTitles;
    private TextView mCustomTitle;
    private boolean backResult = false;
    private String mOlderID;//订单号
    private String source; //页面来源
    private boolean rechageSuccess = false;
    private CheckOrderBean bean;

    /**
     * @param context
     * @param index    跳转的界面 0:充值 1：vip
     */
    public static void start(Activity context, int index) {
        start(context, index, "");
    }

    /**
     * @param context
     * @param index    跳转的界面 0:充值 1：vip
     * @param source 来源，用于统计区分
     */
    public static void start(Activity context, int index, String source) {
        Intent intent = new Intent(context, VipActivity.class);
        intent.putExtra("index", String.valueOf(index));
        intent.putExtra("source", source);
        context.startActivityForResult(intent,Constant.RECHARGE_REQUST_CODE);
    }

    /**
     * @param context
     * @param index    跳转的界面 0:充值 1：vip
     */
    public static void startForResult(Activity context, int index) {
        startForResult(context, index, "");
    }

    /**
     * @param context
     * @param index    跳转的界面 0:充值 1：vip
     * @param source 来源，用于统计区分
     */
    public static void startForResult(Activity context, int index, String source) {
        Intent intent = new Intent(context, VipActivity.class);
        intent.putExtra("index", String.valueOf(index));
        intent.putExtra("result",true);
        intent.putExtra("source", source);
        context.startActivityForResult(intent,Constant.RECHARGE_REQUST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String index = getIntent().getStringExtra("index");
        mIndex = TextUtils.isEmpty(index) ? 0 : Integer.parseInt(index);
        backResult = getIntent().getBooleanExtra("result", false);
        source = getIntent().getStringExtra("source");
        setContentView(R.layout.activity_vip);
        mPresenter = new BuyVipPresenter(this);
        mPresenter.attachView(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String index = intent.getStringExtra("index");
        mIndex = TextUtils.isEmpty(index) ? 0 : Integer.parseInt(index);
        backResult = intent.getBooleanExtra("result", false);
        source = intent.getStringExtra("source");
        if (bindingView != null) {
            bindingView.vipViewpager.setCurrentItem(mIndex);
        }
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

    @Override
    public void initViews() {
        mTitles = new ArrayList<>();
        mTitles.add("充值钻石");
        if (UserManager.getInstance().isVip()) {
            mTitles.add("查看VIP");
        } else {
            mTitles.add("开通VIP");
        }
        fragments.add(new GoodsDiamondFragment());
        fragments.add(new GoodsVipFragment());
        AppFragmentPagerAdapter myAppFragmentPagerAdapter = new AppFragmentPagerAdapter(getSupportFragmentManager(), fragments, mTitles);
        bindingView.vipViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex=position;
                //处理选中的自定义TAB
                if(null!=mCustomTitle){
                    mCustomTitle.setSelected(false);
                }
                XTabLayout.Tab tabAt = bindingView.tabLayout.getTabAt(position);
                if(null==tabAt||null==tabAt.getCustomView()) return;
                TextView titleView = tabAt.getCustomView().findViewById(R.id.tv_item_title);//标题
                if(null==titleView) return;
                titleView.setSelected(true);
                mCustomTitle=titleView;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bindingView.vipViewpager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.vipViewpager.setOffscreenPageLimit(2);
        bindingView.tabLayout.setupWithViewPager(bindingView.vipViewpager);
        bindingView.tabLayout.setTabMode(XTabLayout.MODE_FIXED);
        //设定自定义View
        for (int i = 0; i < bindingView.tabLayout.getTabCount(); i++) {
            XTabLayout.Tab tab = bindingView.tabLayout.getTabAt(i);
            if (tab != null) {
                View tabView = getTabView(i);
                if(null!=tabView) tab.setCustomView(tabView);
            }
        }
        bindingView.vipViewpager.setCurrentItem(mIndex);
        //H5支付监听
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
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                    case R.id.btn_server:
                        ChatActivity.navToChat(VipActivity.this, UserManager.getInstance().getServerIdentify(), true, TIMConversationType.C2C);
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnServer.setOnClickListener(onClickListener);
    }

    //充气自定义View
    private View getTabView(int index) {
        if(null!=mTitles&&mTitles.size()>index){
            View inflate = View.inflate(VipActivity.this, R.layout.tab_vip_item, null);
            TextView tvItemTitle = (TextView) inflate.findViewById(R.id.tv_item_title);
            tvItemTitle.setText(mTitles.get(index));
            //确定是左边还是右边
            tvItemTitle.setBackgroundResource(0==index?R.drawable.tab_vip_left_selector:R.drawable.tab_vip_right_selector);
            //确定默认选中的项,默认显示用户打开界面显示的那一项
            if(mIndex==index){
                mCustomTitle=tvItemTitle;
                tvItemTitle.setSelected(true);
            }else{
                tvItemTitle.setSelected(false);
            }
            return inflate;
        }
        return null;
    }

    public void showServer() {
        if(null!=bindingView) bindingView.btnServer.setVisibility(View.VISIBLE);
    }

    /**
     * 切换至对应界面
     * @param index
     */
    public void setCurrentItem(int index){
        if(null!=bindingView) bindingView.vipViewpager.setCurrentItem(index);
    }

    @Override
    public void initData() { }

    /**
     * 绑定待支付商品信息
     * @param info 商品信息
     */
    public void setGoodsInfo(RechargeInfo info) {
        this.mGoodsInfo = info;
    }

    /**
     * 会员充值界面 提示用户选择充值渠道
     * @param flag
     */
    public void setShowBuyTips(RechargeInfo flag, PayConfigBean payCofingBean) {
        if(null!=flag){
            setGoodsInfo(flag);
        }
        if(null!=mGoodsInfo){
            PayWayQuire.getInstance(VipActivity.this)
                    .setTitleText("选择充值方式")
                    .setPayConfigBean(payCofingBean)
                    .setDialogCanceledOnTouchOutside(true)
                    .setOnPayChanlChangedListener(new PayWayQuire.OnPayChanlChangedListener() {
                @Override
                public void onPayChanlChanged(int chanl) {
                    if(null!=mGoodsInfo){
                        mGoodsInfo.setPayway(chanl);
                        pay();
                    }
                }
            }).show();
        }
    }

    /**
     * 充值金币
     */
    public void pay(){
        if(null!=mGoodsInfo){
            createPay(mGoodsInfo.getPayway(), mGoodsInfo);
        }
    }

    /**
     * 开始支付
     * @param payMode
     */
    public void createPay(int payMode, RechargeInfo rechargeInfo) {
        if (null == rechargeInfo.getGoodsInfo() || null == mPresenter) return;
        List<OlderExtra> olderExtras = new ArrayList<>();
        OlderExtra extra = new OlderExtra();
        extra.setGood_id(String.valueOf(rechargeInfo.getGoodsInfo().getId()));
        extra.setNum(1);
        olderExtras.add(extra);
//        String s1 = JSONArray.toJSONString(olderExtras);
        if (null != mPresenter) mPresenter.createOrder(0 == payMode ? PayConfig.ali_pay : PayConfig.wx_pay,
                new Gson().toJson(olderExtras), rechargeInfo.getGoodsInfo());
    }

    /**
     * 会员功能支付完成
     */
    public void finishSelf() {
        VideoApplication.getInstance().setMineRefresh(true);
        Intent intent = new Intent();
        intent.putExtra("vip", Constant.VIP_SUCCESS);
        setResult(Constant.RECHARGE_RESULT_CODE, intent);
        finish();
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
     * 创建订单成功
     * @param data
     * @param payway
     */
    @Override
    public void showOrderSuccess(OrderInfo data, String payway) {
        if(VipActivity.this.isFinishing()) return;
        if (data.getPayway_info() != null && 2 == data.getPayway_info().getTrade_type()) {
            //H5支付
//            if(null != bindingView) {
//                bindingView.payWebView.starPlay(data.getCharge_order_sn(),data.getPayurl(), data.getPayway_info().getAuth_domain());
//            }
            if(null!=mPresenter){
                mPresenter.dissmis();
            }
            Intent intent=new Intent(VipActivity.this,WebPayActivity.class);
            intent.putExtra("payurl",data.getPayurl());
            intent.putExtra("orderID",data.getCharge_order_sn());
            startActivityForResult(intent,Constant.PAY_REQUST);
        } else if(!TextUtils.isEmpty(data.getPayurl()) && !data.getPayurl().startsWith("alipay_sdk")){
            //混合微信支付
            if(data.getPayurl().startsWith("weixin://")){
                openWxpay(data.getPayurl());
                if(null!=bindingView) bindingView.payWebView.setOlderSn(data.getCharge_order_sn());
            }
        //原生支付
        } else {
            if(null!=bindingView) bindingView.payWebView.setOlderSn("");
            int paymethods;
            if (PayConfig.ali_pay.equals(payway)) {
                paymethods = PayConfig.ALI;
            } else {
                paymethods = PayConfig.WX;
            }
            data.setPayWay(paymethods);
            this.mOlderID=data.getCharge_order_sn();
            PayUtils.getInstance().get(this).pay(paymethods, data, new IPayCallback() {
                @Override
                public void onSuccess(final OrderInfo orderInfo) {
                    VideoApplication.getInstance().setMineRefresh(true);
                    if(null!=mPresenter) mPresenter.checkOrder(orderInfo.getCharge_order_sn());
                }

                @Override
                public void onFailure(OrderInfo orderInfo) {
                    if(null!=mPresenter){
                        mPresenter.dissmis();
                    }
                }

                @Override
                public void onCancel(OrderInfo orderInfo) {
                    if(null!=mPresenter){
                        mPresenter.dissmis();
                    }
                }
            });
        }
    }

    @Override
    public void showCreateOlderError(int code, String netRequstJsonError) {
        ToastUtils.showCenterToast(netRequstJsonError);
    }

    @Override
    public void showCantPayError(int code, String msg) {
        QuireDialog.getInstance(VipActivity.this).showCloseBtn(false)
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
        rechageSuccess = true;
        this.bean = data;
        VideoApplication.getInstance().setMineRefresh(true);
        if(null!=bindingView) bindingView.payWebView.setTag(null);
        refreshData(data);
    }

    @Override
    public void showRechardeError(int code, String msg) {
        ToastUtils.showCenterToast(msg + code);
    }

    @Override
    public void showVipLits(VipListInfo vipListInfo) {}
    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}
    @Override
    public void hide() {}
    @Override
    public void showLoading() {}
    @Override
    public void showNoData() {}
    @Override
    public void showNoNet() {}

    /**
     * 更新用户关键信息
     * @param data
     */
    private void refreshData(CheckOrderBean data) {
        //充值奖励
        if(null!=data&&null!=data.getPopup_page()&&null!=data.getPopup_page().getList_coin()){
            AppEngine.getInstance().setVipListCoin(data.getPopup_page().getList_coin());
            Intent intent = new Intent(VipActivity.this, VipRewardActivity.class);
            startActivityForResult(intent, 1010);
//            VipRewardActivity.start();
            getUserData(false);
            return;
        }
        getUserData(true);
//        if(TextUtils.isEmpty(mOlderID)){
//            getUserData(true);
//            return;
//        }
//        UserManager.getInstance().queryActivitys(2,mOlderID, new UserServerContract.OnNetCallBackListener() {
//            @Override
//            public void onSuccess(Object object) {
//                if(null!=object && object instanceof VipPopupData){
//                    VipPopupData vipPopupData= (VipPopupData) object;
//                    if(null!=vipPopupData.getPopup_page()&&null!=vipPopupData.getPopup_page().getList_coin()){
//                        AppEngine.getInstance().setVipListCoin(vipPopupData.getPopup_page().getList_coin());
//                        getUserData(false);
//                        VipRewardActivity.start();
//                    }else{
//                        getUserData(true);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(int code, String errorMsg) {
//                getUserData(true);
//            }
//        });
    }

    /**
     * 获取用户信息
     * @param isFinlish
     */
    private void getUserData(final boolean isFinlish) {
        if (null != bindingView) {
            UserManager.getInstance().getFullUserData(UserManager.getInstance().getUserId(), UserManager.getInstance().getUserId(), new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if(null!=mPresenter) mPresenter.dissmis();
                    if (mIndex == 0) {
                        ((GoodsDiamondFragment)fragments.get(mIndex)).refreshDiamons();
                    } else if (mIndex == 1) {
                        ((GoodsVipFragment)fragments.get(mIndex)).refresh();
                        EventBus.getDefault().post(true, "VIP_RECHARGE_SUCCESS");
                    }
                    if(isFinlish&&backResult){
                        finishSelf();
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    if(null!=mPresenter) mPresenter.dissmis();
                }
            });
        }else{
            if(null!=mPresenter) mPresenter.dissmis();
            if(isFinlish){
                finishSelf();
            }else{
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1010 == requestCode && 1011 == resultCode) {
            if (null != bean && bean.getBind_mobile() == 2) {
                mPresenter.showBindMobileDialog(bean);
            }
        }else if(Constant.PAY_REQUST==requestCode&&Constant.PAY_RESULT==resultCode){
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

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(source)) {
            MobclickAgent.onEvent(getContext(), "recharge_page_back_" + source);
        }
        if (backResult && rechageSuccess) {
            finishSelf();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.detachView();
        if(null!=bindingView) bindingView.payWebView.onDestroy();
    }
}