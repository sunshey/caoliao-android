package com.yc.liaolive.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityZhimaAuthentiBinding;
import com.yc.liaolive.ui.business.LoginBusiness;
import com.yc.liaolive.user.bean.ZhimaParams;
import com.yc.liaolive.user.bean.ZhimaResult;
import com.yc.liaolive.user.ui.contract.ZhimaContract;
import com.yc.liaolive.user.ui.presenter.ZhimaPresenter;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.webview.ui.WebViewActivity;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 主播端芝麻认证表单填写
 */

public class ZhimaAuthentiActivity extends BaseActivity<ActivityZhimaAuthentiBinding> implements ZhimaContract.View {

    private static final String TAG = "ZhimaAuthentiActivity";
    private Animation mInputAnimation;
    private ZhimaPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhima_authenti);
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        mPresenter = new ZhimaPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void initViews() {
        //监听输入框文字
        bindingView.inputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = bindingView.inputName.getText().toString();
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0&&!TextUtils.isEmpty(text)){
                    bindingView.btnNext.setBackgroundResource(R.drawable.bt_app_style_bg_selector);
                }else{
                    bindingView.btnNext.setBackgroundResource(R.drawable.bg_comment_button_false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                finish();
            }
        });
        bindingView.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthenti();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(VideoApplication.getInstance().isZmAuthentResult()){
            VideoApplication.getInstance().setZmAuthentResult(false);
            finish();
            return;
        }
        String result = SharedPreferencesUtil.getInstance().getString(Constant.SP_ZHIMA_AUTHENTI_RESULT);
        if(!TextUtils.isEmpty(result)){
            String authentiResult=result;
            SharedPreferencesUtil.getInstance().remove(Constant.SP_ZHIMA_AUTHENTI_RESULT);
            ZhimaAuthentiResultActivity.start(AppEngine.getApplication().getApplicationContext(),authentiResult);
        }
    }

    /**
     * 绑定手机号
     */
    private void startAuthenti() {
        if(null==bindingView) return;
        if(!LoginBusiness.getInstance().hasZhifubao(ZhimaAuthentiActivity.this)){
            ToastUtils.showCenterToast("请先安装支付宝软件");
            return;
        }
        String nickName = bindingView.inputName.getText().toString().trim();
        String number= bindingView.inputNumber.getText().toString().trim();
        if(TextUtils.isEmpty(nickName)){
            ToastUtils.showCenterToast("请输入身份证姓名");
            if(null!=mInputAnimation) bindingView.inputName.startAnimation(mInputAnimation);
            return;
        }

        if(TextUtils.isEmpty(number)){
            ToastUtils.showCenterToast("请输入身份证号码");
            if(null!=mInputAnimation) bindingView.inputNumber.startAnimation(mInputAnimation);
            return;
        }
        showProgressDialog("操作中，请稍后...",false);
        if(null!=mPresenter&&!mPresenter.isLoading()){
            mPresenter.getZhimaParams(nickName,number,Constant.CONTENT_AGREEMENT_AUTHENTI);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mInputAnimation){
            mInputAnimation.cancel();
            mInputAnimation=null;
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showZhimaParams(ZhimaParams data) {
        closeProgressDialog();
        if(!TextUtils.isEmpty(data.getUrl())){
//            Intent action = new Intent(Intent.ACTION_VIEW);
//            action.setData(Uri.parse(data.getUrl()));
//            startActivity(action);
            WebViewActivity.loadUrl(ZhimaAuthentiActivity.this,data.getUrl(),"芝麻认证");
//            ZhimaAuthentiWebActivity.start(AppEngine.getApplication().getApplicationContext(),data.getUrl(),"芝麻认证");
        }
    }

    @Override
    public void showZhimaParamsError(int code, String data) {
        closeProgressDialog();
        ToastUtils.showCenterToast(data);
    }

    @Override
    public void showCheckedZhimaResult(ZhimaResult data) {

    }

    @Override
    public void showCheckedZhimaError(int code, String data) {

    }
}