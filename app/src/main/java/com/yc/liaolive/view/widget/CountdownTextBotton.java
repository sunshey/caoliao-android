package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2019/1/21
 * 倒计时控件
 */

public class CountdownTextBotton extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

    private View mRootBgView;
    private int mTextColorOutFocus;//失去焦点文字颜色
    private int mTextColorGetFocus;//默认获取焦点文字颜色
    private Drawable mBgDrawableGetFocus;//默认获取焦点背景
    private Drawable mBgDrawableOutFocus;//默认失去焦点背景
    private String mGetFocusText;
    private int COUNT_DOWN_TIME=0;//默认倒计时60秒
    private Handler mHandler;
    private int GET_COUNT=0;//第几次获取验证码

    public CountdownTextBotton(Context context) {
        this(context,null);
    }

    public CountdownTextBotton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler=new Handler();
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountdownBotton);
            mTextColorGetFocus = typedArray.getColor(R.styleable.CountdownBotton_countdownTextColorGetFocus, getContext().getResources().getColor(R.color.colorTextG6));
            mTextColorOutFocus = typedArray.getColor(R.styleable.CountdownBotton_countdownTextColorOutFocus, getContext().getResources().getColor(R.color.colorTextG6));
            mGetFocusText = typedArray.getString(R.styleable.CountdownBotton_countdownTextGetFocus);
            typedArray.recycle();
        }
        setGetFocusBtn();
    }

    /**
     * 开始倒计时
     * @param spaceGainMinute 间隔获取时长 单位 秒
     */
    public void startCountdown(int spaceGainMinute){
        COUNT_DOWN_TIME=0;
        if(null!=taskRunnable&&null!=mHandler) mHandler.removeCallbacks(taskRunnable);
        if(null!=mHandler) mHandler.removeMessages(0);
        setOutFocusBtn();
        COUNT_DOWN_TIME=spaceGainMinute;
        if(null!=mHandler&&null!=taskRunnable) mHandler.postDelayed(taskRunnable,0);
    }

    /**
     * 结束倒计时
     */
    public void stopCountdown(){
        COUNT_DOWN_TIME=0;
        if(null!=taskRunnable&&null!=mHandler) mHandler.removeCallbacks(taskRunnable);
        if(null!=mHandler) mHandler.removeMessages(0);
        setGetFocusBtn();
        if(null!=mOnCountdownClickListener) mOnCountdownClickListener.onStopDown();
    }

    /**
     * 定时任务
     */
    Runnable taskRunnable=new Runnable() {
        @Override
        public void run() {
            setText(COUNT_DOWN_TIME+"S后重新重试");
            COUNT_DOWN_TIME--;
            if(COUNT_DOWN_TIME<0){
                //还原
                GET_COUNT++;
                stopCountdown();
                return;
            }
            if(null!=mHandler) mHandler.postDelayed(this,1000);
        }
    };

    /**
     * 获得焦点，默认状态
     */
    private void setGetFocusBtn() {
        if(null!= mBgDrawableGetFocus) mRootBgView.setBackground(mBgDrawableGetFocus);
        setTextColor(mTextColorGetFocus);
        if(GET_COUNT>0){
            setText("重新获取");
        }else{
            setText(mGetFocusText);
        }
        this.setOnClickListener(this);
    }

    /**
     * 失去焦点，正在倒计时中
     */
    private void setOutFocusBtn() {
        if(null!= mBgDrawableOutFocus) mRootBgView.setBackground(mBgDrawableOutFocus);
        setTextColor(mTextColorOutFocus);
        this.setOnClickListener(null);
    }

    public void onDestroy(){
        if(null!=taskRunnable&&null!=mHandler) mHandler.removeCallbacks(taskRunnable);
        if(null!=mHandler) mHandler.removeMessages(0);
        mBgDrawableGetFocus=null;mBgDrawableOutFocus=null;mRootBgView=null;
        mHandler=null;taskRunnable=null;
    }

    @Override
    public void onClick(View v) {
        if(null!=mOnCountdownClickListener){
            mOnCountdownClickListener.onCountDown();
        }
    }

    /**
     * 设置间隔获取验证码倒计时时间
     * @param time
     */
    public void setCountdownTime(int time) {
        this.COUNT_DOWN_TIME = time;
    }


    public void setTextGetFocus(String content){
        setText(content);
        this.mGetFocusText=content;
    }

    public void setTextColorGetFocus(int color){
        setTextColor(color);
        this.mTextColorGetFocus=color;
    }

    public void setTextColorOutFocus(int color){
        this.mTextColorOutFocus=color;
    }

    public void setBackgroundGetFocus(Drawable drawable){
        if(null!=mRootBgView) mRootBgView.setBackground(drawable);
        this.mBgDrawableGetFocus=drawable;
    }

    public void setBackgroundOutFocus(Drawable drawable){
        this.mBgDrawableOutFocus=drawable;
    }

    /**
     * 文本颜色
     * @param color
     */
    public void setTextContentColor(int color) {
        //只在倒计时未运行时生效
        if(COUNT_DOWN_TIME<=0){
            setTextColor(color);
        }
    }


    public interface OnCountdownClickListener{
        void onCountDown();
        void onStopDown();
    }

    private OnCountdownClickListener mOnCountdownClickListener;

    public void setOnCountdownClickListener(OnCountdownClickListener onCountdownClickListener) {
        mOnCountdownClickListener = onCountdownClickListener;
    }
}