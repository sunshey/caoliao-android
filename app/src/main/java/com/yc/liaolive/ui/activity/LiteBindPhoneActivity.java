package com.yc.liaolive.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityLiteBindPhoneBinding;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.bean.VerificationInfo;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CountdownTextBotton;

/**
 * TinyHung@outlook.com
 * 2017/1/21
 * 精简版弹窗式的绑定手机号
 */

public class LiteBindPhoneActivity extends TopBaseActivity {

    private ActivityLiteBindPhoneBinding bindingView;
    private Animation mInputAnimation;

    public static void start(Context context) {
        Intent intent=new Intent(context,LiteBindPhoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_lite_bind_phone);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//需要添加的语句
        initLayoutParams();
        setFinishOnTouchOutside(false);
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //取消
                    case R.id.tv_cancel:
                        finish();
                        break;
                    case R.id.tv_submit:
                        bindPhone();
                        break;
                }
            }
        };
        bindingView.tvCancel.setOnClickListener(onClickListener);
        bindingView.tvSubmit.setOnClickListener(onClickListener);
        bindingView.btnGetCode.setOnCountdownClickListener(new CountdownTextBotton.OnCountdownClickListener() {
            @Override
            public void onCountDown() {
                getCode();
            }

            @Override
            public void onStopDown() {
                if(null!=bindingView){
                    String charSequence = bindingView.etPhoneNumber.getText().toString().trim();
                    if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                        bindingView.btnGetCode.setTextContentColor(Color.parseColor("#FF7575"));
                    }else{
                        bindingView.btnGetCode.setTextContentColor(Color.parseColor("#CCCCCC"));
                    }
                }
            }
        });
        bindingView.etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    bindingView.btnGetCode.setTextContentColor(Color.parseColor("#FF7575"));
                }else{
                    bindingView.btnGetCode.setTextContentColor(Color.parseColor("#CCCCCC"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 绑定手机号
     */
    private void bindPhone() {
        if(null==bindingView) return;
        final String account = bindingView.etPhoneNumber.getText().toString().trim();
        String code = bindingView.etPhoneCode.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            ToastUtils.showCenterToast("手机号码不能为空");
            if(null!=mInputAnimation) bindingView.etPhoneNumber.startAnimation(mInputAnimation);
            return;
        }
        if(!Utils.isPhoneNumber(account)){
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }

        if(TextUtils.isEmpty(code)){
            ToastUtils.showCenterToast("验证码不能为空");
            if(null!=mInputAnimation) bindingView.etPhoneCode.startAnimation(mInputAnimation);
            return;
        }
        showProgressDialog("操作中，请稍后...",false);
        UserManager.getInstance().onBindPlatformAccount(Constant.LOGIN_TYPE_PHONE, "",
                "", account, code, new UserServerContract.OnNetCallBackListener() {
                    @Override
                    public void onSuccess(Object object) {
                        closeProgressDialog();
                        UserManager.getInstance().setPhone(account);
                        ToastUtils.showCenterToast("已成功绑定");
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        closeProgressDialog();
                        ToastUtils.showCenterToast(errorMsg);
                    }
                });
    }

    /**
     * 准备获取验证码
     */
    private void getCode() {
        String account = bindingView.etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showCenterToast("手机号码不能为空");
            bindingView.etPhoneNumber.startAnimation(mInputAnimation);
            return;
        }
        if(!Utils.isPhoneNumber(account)){
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }
        getVerificationCode("86", account);
    }

    /**
     * 派发验证码
     * @param country
     * @param phone
     */
    public void getVerificationCode(String country, String phone) {
        showProgressDialog("正在请求发送验证码...",true);
        //派发验证码
        UserManager.getInstance().getVerificationCode(country, phone, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                ToastUtils.showCenterToast("验证码已发送至您的手机");
                if(null!=object && object instanceof VerificationInfo){
                    VerificationInfo data= (VerificationInfo) object;
                    if(null!=bindingView){
                        int time=60;
                        if(!TextUtils.isEmpty(data.getDelay_time())){
                            time=Integer.parseInt(data.getDelay_time());
                        }
                        bindingView.btnGetCode.startCountdown(time);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mInputAnimation) mInputAnimation.cancel();
        mInputAnimation=null;
        if(null!=bindingView) bindingView.btnGetCode.stopCountdown();
    }
}