package com.yc.liaolive.recharge.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityPayWebBinding;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;
import com.yc.liaolive.recharge.manager.BuyVipPresenter;
import com.yc.liaolive.recharge.view.WebPayView;
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.user.ui.VipRewardActivity;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * TinyHung@Outlook.com
 * 2019/3/19
 */

public class WebPayActivity extends BaseActivity<ActivityPayWebBinding> implements BuyVipContract.View {

    private BuyVipPresenter mPresenter;
    private String mOrderID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_web);
        bindingView.webPayView.setOnFunctionListener(new WebPayView.OnFunctionListener() {
            @Override
            public void weXinPay(String url) {
                mOrderID = getIntent().getStringExtra("orderID");
                openWxpay(url);
            }

            @Override
            public void aliPay(String url) {
                mOrderID = getIntent().getStringExtra("orderID");
                openAlipay(url);
            }
        });
        String payurl = getIntent().getStringExtra("payurl");
        String orderID = getIntent().getStringExtra("orderID");
        if(TextUtils.isEmpty(payurl)){
            ToastUtils.showCenterToast("参数错误");
            return;
        }
        mPresenter = new BuyVipPresenter(this);
        mPresenter.attachView(this);
        bindingView.webPayView.showWebView();
        bindingView.titleView.setTitle("订单支付");
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                onBackPressed();
            }
        });
        bindingView.webPayView.starPlay(orderID,payurl,null);
    }

    @Override
    public void initViews() {}

    @Override
    public void initData() {}

    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(mOrderID)){
            Intent intent=new Intent();
            intent.putExtra("olderid",mOrderID);
            setResult(Constant.PAY_RESULT,intent);
            finish();
        }
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
     * 更新用户关键信息
     * @param data
     */
    private void refreshData(CheckOrderBean data) {
        //充值奖励
        if(null!=data&&null!=data.getPopup_page()&&null!=data.getPopup_page().getList_coin()){
            AppEngine.getInstance().setVipListCoin(data.getPopup_page().getList_coin());
            Intent intent = new Intent(WebPayActivity.this, VipRewardActivity.class);
            startActivityForResult(intent, 1010);
            return;
        }
        finish();
    }

    @Override
    public void showOrderSuccess(OrderInfo data, String rechargeGoodsInfo) {}

    @Override
    public void showCreateOlderError(int code, String netRequstJsonError) {}

    @Override
    public void showCantPayError(int code, String msg) { }

    @Override
    public void showRechardeResult(CheckOrderBean data) {
        VideoApplication.getInstance().setMineRefresh(true);
        refreshData(data);
    }

    @Override
    public void showRechardeError(int code, String msg) {
        ToastUtils.showCenterToast(msg + code);
    }

    @Override
    public void showVipLits(VipListInfo vipListInfo) {}

    @Override
    public void hide() {}

    @Override
    public void showNoNet() {}

    @Override
    public void showLoading() {}

    @Override
    public void showNoData() {}

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    /**
     * 拦截返回
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(null!=bindingView&&bindingView.webPayView.backPress()){
            return;
        }
        LogRecordUtils.getInstance().cancelOeder(mOrderID);
        super.onBackPressed();
    }
}