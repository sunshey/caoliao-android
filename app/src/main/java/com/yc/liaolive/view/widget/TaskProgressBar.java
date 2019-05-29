package com.yc.liaolive.view.widget;

import android.content.res.TypedArray;
import android.widget.RelativeLayout;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;
import java.math.BigDecimal;

/**
 * TinyHung@Outlook.com
 * 2018/7/19
 * 任务进度条
 */

public class TaskProgressBar extends RelativeLayout {

    private static final String TAG = "TaskProgressBar";
    private int DURATION = 1000; // 默认渲染时长ms，默认1s
    private ValueAnimator mAnimator;
    private int mMaxProgress=100;//默认的最大阈值
    private int mGroupWidth;//控件实际的宽
    private int mGroupHeight;//控件实际的高
    private View mViewProgressBar;//进度
    private TextView mViewTvProgress;//文字
    private boolean mIsMoves=true;//文字是否随进度条移动
    private int mProgress;

    public TaskProgressBar(Context context) {
        super(context);
        init(context,null);
    }

    public TaskProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_task_progress_bar,this);
        mViewTvProgress = (TextView) findViewById(R.id.view_tv_progress);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TaskProgressBar);
            int textColor = typedArray.getColor(R.styleable.TaskProgressBar_taskProgressTextColor, getContext().getResources().getColor(R.color.white));
            mViewTvProgress.setTextColor(textColor);
            typedArray.recycle();
        }
        mViewProgressBar = findViewById(R.id.view_progress);
    }

    /**
     * 设置进度条背景资源样式
     * @param resID
     */
    public void setProgressBarBackground(int resID){
        setDrawable(findViewById(R.id.root_view),getContext().getResources().getDrawable(resID));
    }

    /**
     * 设置进度条背景资源样式
     * @param drawable
     */
    public void setProgressBarBackground(Drawable drawable){
        setDrawable(findViewById(R.id.root_view),drawable);
    }

    /**
     * 设置进度条资源样式
     * @param redID
     */
    public void setProgressBackground(int redID){
        if(null!=mViewProgressBar) setDrawable(mViewProgressBar,getContext().getResources().getDrawable(redID));
    }

    /**
     * 设置进度条资源样式
     * @param drawable
     */
    public void setProgressBackground(Drawable drawable){
        if(null!=mViewProgressBar) setDrawable(mViewProgressBar,drawable);
    }


    private void setDrawable(View view,Drawable drawabled){
        if(null==drawabled||null==view) return;
        view.setBackground(drawabled);
    }

    /**
     * 设置文字是否随进度条移动
     * @param isMoves
     */
    public void setTextIsMoves(boolean isMoves){
        this.mIsMoves=isMoves;
    }

    /**
     * 设置进度条执行的时长
     * @param duration
     */
    public void setMovesDuration(int duration) {
        DURATION = duration;
    }

    /**
     * 设置进度条最大阈值
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress){
        this.mMaxProgress=maxProgress;
    }

    /**
     * 设置进度条宽度
     * @param groupWidth
     */
    public void setGroupWidth(int groupWidth) {
        mGroupWidth = groupWidth;
    }

    /**
     * 设置进度条高度
     * @param groupHeight
     */
    public void setGroupHeight(int groupHeight) {
        mGroupHeight = groupHeight;
    }


    /**
     * 设置进度文字
     * @param content
     */
    public void setProgressText(String content){
        if(null!=mViewTvProgress) mViewTvProgress.setText(content);
    }

    /**
     * 设置进度条进度指示字体颜色
     * @param color
     */
    public void setPeogressTextColor(String color){
        setPeogressTextColor(Color.parseColor(color));
    }

    /**
     * 设置进度条进度指示字体颜色
     * @param color
     */
    public void setPeogressTextColor(int color){
        if(null!=mViewTvProgress) mViewTvProgress.setTextColor(color);
    }

    /**
     * 设置进度条文字大小
     * @param textSize  等同于sp
     */
    public void setTextSize(int textSize){
        if(null!=mViewTvProgress) mViewTvProgress.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
    }

    /**
     * 设置进度条
     * @param progress
     * 必须先指定最大阈值 setMaxProgress(max);
     */
    public void setProgress(int progress){
        this.mProgress=progress;
        if(null!=mViewProgressBar){
            clearAnimator();
            //构建阶梯动画
            mAnimator = ValueAnimator.ofInt(0, progress);
            startAnimator();
        }
    }

    /**
     * 清除可能未完成的动画
     */
    public void clearAnimator() {
        if (null != mAnimator) {
            if (mAnimator.isRunning()) {
                mAnimator.removeAllListeners();
                mAnimator.removeAllUpdateListeners();
                mAnimator.cancel();
            }
            mAnimator = null;
        }
    }

    /**
     * 开始动画播放
     */
    private void startAnimator() {
        if (null != mAnimator) {
            mAnimator.setDuration(DURATION);
            mAnimator.setInterpolator(new LinearInterpolator());
            final double scale = new BigDecimal((float)mGroupWidth/mMaxProgress).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            // 动画过程中获取当前值，显示
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    double newProgress =scale * ((int) valueAnimator.getAnimatedValue()) ;
                    ViewGroup.LayoutParams layoutParams = mViewProgressBar.getLayoutParams();
                    layoutParams.width= (int) newProgress;
                    layoutParams.height=mGroupHeight;
                    mViewProgressBar.setLayoutParams(layoutParams);
                    if(mIsMoves) setProgressText(valueAnimator.getAnimatedValue()+"/"+mMaxProgress);
                    invalidate();
                }
            });
            mAnimator.start();
        }
    }
}
