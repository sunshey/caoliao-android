package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogCallContinueLayoutBinding;
import com.yc.liaolive.model.GlideCircleTransform;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 视频通话结束后，问询用户框
 */

public class CallContinueDialog extends BaseDialog<DialogCallContinueLayoutBinding> {

    private static final String TAG = "CallContinueDialog";
    private Timer mNoticeTimer;
    private int countDownCloseTime =11;
    private Handler mHandler;
    private boolean isAutoExpens=false;

    public static CallContinueDialog getInstance(Activity activity) {
        return new CallContinueDialog(activity);
    }

    public CallContinueDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_call_continue_layout);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_submit:
                        stopCountDownQuire();
                        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onConsent(null==bindingView?false:bindingView.btnRadioButton.isChecked());
                        break;
                    case R.id.tv_cancel:
                        stopCountDownQuire();
                        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onRefuse();
                        break;
                    //是否自动续费
                    case R.id.btn_radio_button:
                        isAutoExpens=!isAutoExpens;
                        bindingView.btnRadioButton.setChecked(isAutoExpens);
                        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onAutoExpens(isAutoExpens);
                        break;
                }
            }
        };
        bindingView.tvSubmit.setOnClickListener(onClickListener);
        bindingView.tvCancel.setOnClickListener(onClickListener);
        bindingView.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        bindingView.btnRadioButton.setOnClickListener(onClickListener);
    }


    /**
     * 设置标题
     * @param title
     * @return
     */
    public CallContinueDialog setTitleText(String title) {
        if(null!=bindingView) bindingView.tvTitle.setText(title);
        return this;
    }

    /**
     * 设置确定按钮文字内容
     * @param submitTitle
     * @return
     */
    public CallContinueDialog setSubmitTitleText(String submitTitle) {
        if(null!=bindingView) bindingView.tvSubmit.setText(submitTitle);
        return this;
    }


    /**
     * 设置取消文字按钮
     * @param cancelTitleText
     * @return
     */
    public CallContinueDialog setCancelTitleText(String cancelTitleText) {
        if(null!=bindingView) bindingView.tvCancel.setText(cancelTitleText);
        return this;
    }
    /**
     * 设置提示内容
     * @param content
     * @return
     */
    public CallContinueDialog setContentText(String content) {
        if(null!=bindingView) bindingView.tvContent.setText(content);
        return this;
    }

    /**
     * 设置标题文字颜色
     * @param color
     * @return
     */
    public CallContinueDialog setTitleTextColor(int color) {
        if(null!=bindingView) bindingView.tvTitle.setTextColor(color);
        return this;
    }


    /**
     * 设置确定按钮文字颜色
     * @param color
     * @return
     */
    public CallContinueDialog setSubmitTitleTextColor(int color) {
        if(null!=bindingView) bindingView.tvSubmit.setTextColor(color);
        return this;
    }

    /**
     * 设置取消文字颜色
     * @param color
     * @return
     */
    public CallContinueDialog setCancelTitleTextColor(int color) {
        if(null!=bindingView) bindingView.tvCancel.setTextColor(color);
        return this;
    }

    /**
     * 设置提示内容文字颜色
     * @param color
     * @return
     */
    public CallContinueDialog setContentTextColor(int color) {
        if(null!=bindingView) bindingView.tvContent.setTextColor(color);
        return this;
    }

    /**
     * 是否显示圆形图片
     * @param flag
     * @return
     */
    public CallContinueDialog showHeadView(boolean flag){
        if(null!=bindingView) bindingView.ivIcon.setVisibility(flag?View.VISIBLE:View.GONE);
        return this;
    }

    /**
     * 价格信息
     * @param priceContent
     * @return
     */
    public CallContinueDialog setPriceContent(String priceContent){
        if(null!=bindingView) bindingView.tvDespPrice.setText(priceContent);
        return this;
    }

    /**
     * 设置圆形图片地址URL
     * @param coverUrl
     * @return
     */
    public CallContinueDialog setHeadCover(String coverUrl){
        Glide.with(getContext())
                .load(coverUrl)
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(bindingView.ivIcon);
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public CallContinueDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public CallContinueDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.setCanceledOnTouchOutside(false);
        return this;
    }

    /**
     * 设在自动关闭弹窗时长
     * @param countDownCloseTime
     * @return
     */
    public CallContinueDialog setCountDownCloseTime(int countDownCloseTime) {
        this.countDownCloseTime =(countDownCloseTime +1);
        return this;
    }

    /**
     * 显示自己
     */
    public void showDialog(){
        showDialog(false);
    }

    /**
     * 显示自己
     * @param autoClose 是否自动关闭
     */
    public void showDialog(boolean autoClose){
        this.show();
        if(autoClose){
            startCountDownQuire();
        }
    }

    /**
     * 开始倒计时问询用户
     */
    private void startCountDownQuire(){
        //开始倒计时
        if (mNoticeTimer == null) {
            mNoticeTimer = new Timer();
        }
        mNoticeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                countDownCloseTime--;
                if(null!=mHandler){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(countDownCloseTime <=0){
                                countDownCloseTime =0;
                                stopCountDownQuire();
                            }else{
                                setCancelTitleText("退出("+ countDownCloseTime +")");
                            }
                        }
                    });
                }
            }
        }, 0, 1000);
    }
    /**
     * 结束倒计时问询用户
     */
    private void stopCountDownQuire(){
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
        if(null!=mNoticeTimer) mNoticeTimer.cancel();mNoticeTimer=null;
        CallContinueDialog.this.dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mNoticeTimer) mNoticeTimer.cancel();mNoticeTimer=null;
        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onDissmiss();
    }

    public abstract static class OnQueraConsentListener{
        public void onConsent(boolean autoContinuation){}
        public void onRefuse(){}
        public void onDissmiss(){}
        public void onAutoExpens(boolean isAutoExpens){}
    }
    private OnQueraConsentListener mOnQueraConsentListener;

    public CallContinueDialog setOnQueraConsentListener(OnQueraConsentListener onQueraConsentListener) {
        mOnQueraConsentListener = onQueraConsentListener;
        return this;
    }
}
