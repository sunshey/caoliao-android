package com.yc.liaolive.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityPhoneRegisterBinding;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.bean.VerificationInfo;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.UserDataComplementActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.view.widget.CountdownBotton;

/**
 * TinyHung@Outlook.com
 * 2018/5/28
 * 手机号码注册
 */

public class RegisterPhoneActivity extends BaseActivity<ActivityPhoneRegisterBinding>{

    private static final String TAG = "RegisterPhoneActivity";
    private Animation mInputAnimation;

    @Override
    public void initViews() {
        //获取验证码
        bindingView.btnGetCode.setOnCountdownClickListener(new CountdownBotton.OnCountdownClickListener() {
            @Override
            public void onCountDown() {
                getCode();
            }
        });
        //标题的返回事件
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                onBackPressed();
            }
        });
        //下一步
        bindingView.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
//        String text="如长时间收不到验证码，试试其他方式";
//        SpannableString spannableString = LiveChatUserGradleSpan.stringFormat(text, "其他方式",bindingView.tvTips, Color.parseColor("#FFFF0101"),true,new OnSpannableUserClickListener() {
//            @Override
//            public void onClick(String userID) {
//                onBackPressed();
//            }
//        });
//        bindingView.tvTips.setText(spannableString);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_phone_register);
        }catch (RuntimeException e){

        }
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }


    /**
     * 准备获取验证码
     */
    private void getCode() {
        String account = bindingView.inputAccount.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            ToastUtils.showCenterToast("手机号码不能为空");
            if(null!=mInputAnimation) bindingView.inputAccount.startAnimation(mInputAnimation);
            return;
        }
        if(!Utils.isPhoneNumber(account)){
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }
        getVerificationCode("86",account);
    }

    /**
     * 注册
     */
    private void register() {
        if(null==bindingView) return;
        String account = bindingView.inputAccount.getText().toString().trim();
        String code = bindingView.inputCode.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            ToastUtils.showCenterToast("手机号码不能为空");
            if(null!=mInputAnimation) bindingView.inputAccount.startAnimation(mInputAnimation);
            return;
        }
        if(!Utils.isPhoneNumber(account)){
            ToastUtils.showCenterToast("手机号码格式不正确");
            return;
        }
        if(TextUtils.isEmpty(code)){
            ToastUtils.showCenterToast("验证码不能为空");
            if(null!=mInputAnimation) bindingView.inputCode.startAnimation(mInputAnimation);
            return;
        }

        showProgressDialog("验证中,请稍后..",true);
        UserManager.getInstance().registerByPhoone(code, account, "86", new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                if(null!=object&&object instanceof  Integer){
                    Integer code= (Integer) object;
                    if(1109==code){
                        Intent intent=new Intent();
                        intent.putExtra("registerPhone","1");
                        setResult(Constant.REGISTER_PHONE_RESULT_CODE,intent);
                        finish();
                        return;
                    }
                }
                //完善用户资料
                startActivityForResult(new Intent(RegisterPhoneActivity.this, UserDataComplementActivity.class),Constant.REGISTER_COMPLEMENT_REQUST_CODE);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if(null!=mInputAnimation) mInputAnimation.cancel();
        if(null!=bindingView) bindingView.btnGetCode.onDestroy();
        super.onDestroy();
        mInputAnimation=null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //用户信息补全成功后自动登录
        if(requestCode==Constant.REGISTER_COMPLEMENT_REQUST_CODE&&resultCode==Constant.REGISTER_COMPLEMENT_RESULT_CODE&&null!=data){
            Intent intent=new Intent();
            intent.putExtra("registerPhone","1");
            setResult(Constant.REGISTER_PHONE_RESULT_CODE,intent);
            finish();
        }
    }
}