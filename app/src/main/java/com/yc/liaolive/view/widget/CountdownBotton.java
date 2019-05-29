package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/5/28
 * 倒计时控件
 */

public class CountdownBotton extends RelativeLayout implements View.OnClickListener {

    private View mRootBgView;
    private TextView mViewTitle;
    private int mTextColorOutFocus;//失去焦点文字颜色
    private int mTextColorGetFocus;//默认获取焦点文字颜色
    private Drawable mBgDrawableGetFocus;//默认获取焦点背景
    private Drawable mBgDrawableOutFocus;//默认失去焦点背景
    private String mGetFocusText;
    private int COUNT_DOWN_TIME=60;//默认倒计时60秒
    private Handler mHandler;

    public CountdownBotton(Context context) {
        super(context);
        init(context,null);
    }

    public CountdownBotton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_btn_countdown_layout,this);
        mRootBgView = findViewById(R.id.view_root_view);
        mViewTitle = (TextView) findViewById(R.id.view_title);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountdownBotton);
            mTextColorGetFocus = typedArray.getColor(R.styleable.CountdownBotton_countdownTextColorGetFocus, getContext().getResources().getColor(R.color.colorTextG6));
            mTextColorOutFocus = typedArray.getColor(R.styleable.CountdownBotton_countdownTextColorOutFocus, getContext().getResources().getColor(R.color.colorTextG6));
            mBgDrawableGetFocus = typedArray.getDrawable(R.styleable.CountdownBotton_countdownBackgroundGetFocus);
            mBgDrawableOutFocus = typedArray.getDrawable(R.styleable.CountdownBotton_countdownBackgroundOutFocus);
            mGetFocusText = typedArray.getString(R.styleable.CountdownBotton_countdownTextGetFocus);
            int textSize = typedArray.getInt(R.styleable.CountdownBotton_countdownTextSize,13);
            mViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            setGetFocusBtn();
            typedArray.recycle();
        }
        mHandler=new Handler();
    }

    /**
     * 开始倒计时
     * @param spaceGainMinute 间隔获取时长 单位 秒
     */
    public void startCountdown(int spaceGainMinute){
        stopCountdown();
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
    }

    /**
     * 定时任务
     */
    Runnable taskRunnable=new Runnable() {
        @Override
        public void run() {
            if(null==mViewTitle) return;
            mViewTitle.setText(COUNT_DOWN_TIME+"s后重试");
            COUNT_DOWN_TIME--;
            if(COUNT_DOWN_TIME<0){
                //还原
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
        if(null!=mViewTitle){
            mViewTitle.setTextColor(mTextColorGetFocus);
            mViewTitle.setText(mGetFocusText);
        }
        this.setOnClickListener(this);
    }

    /**
     * 失去焦点，正在倒计时中
     */
    private void setOutFocusBtn() {
        if(null!= mBgDrawableOutFocus) mRootBgView.setBackground(mBgDrawableOutFocus);
        if(null!=mViewTitle){
            mViewTitle.setTextColor(mTextColorOutFocus);
        }
        this.setOnClickListener(null);
    }

    public void onDestroy(){
        if(null!=taskRunnable&&null!=mHandler) mHandler.removeCallbacks(taskRunnable);
        if(null!=mHandler) mHandler.removeMessages(0);
        mBgDrawableGetFocus=null;mBgDrawableOutFocus=null;mRootBgView=null;mViewTitle=null;
        mHandler=null;taskRunnable=null;
    }

    @Override
    public void onClick(View v) {
        if(null!=mOnCountdownClickListener){
            mOnCountdownClickListener.onCountDown();
        }
    }

    /**
     * 设置间隔重新获取倒计时时长
     * @param time 单位 秒
     */
    public void setCountdownTime(int time) {
        this.COUNT_DOWN_TIME = time;
    }


    public void setTextGetFocus(String content){
        if(null!=mViewTitle) mViewTitle.setText(content);
        this.mGetFocusText=content;
    }

    public void setTextColorGetFocus(int color){
        if(null!=mViewTitle) mViewTitle.setTextColor(color);
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


    public interface OnCountdownClickListener{
        void onCountDown();
    }

    private OnCountdownClickListener mOnCountdownClickListener;

    public void setOnCountdownClickListener(OnCountdownClickListener onCountdownClickListener) {
        mOnCountdownClickListener = onCountdownClickListener;
    }
}
