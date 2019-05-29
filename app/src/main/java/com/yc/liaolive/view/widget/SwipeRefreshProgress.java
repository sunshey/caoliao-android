package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.AnimationUtil;

/**
 * TinyHung@Outlook.com
 * 2018/7/25
 * 刷新按钮
 */

public class SwipeRefreshProgress extends RelativeLayout {

    private static final String TAG = "SwipeRefreshProgress";

    private RelativeLayout mReProgress;
    private boolean isHideing=false;//是否隐藏中
    private boolean isShowing=false;//是否显示中
    private RotateAnimation mRotate;
    private boolean isRefresh=false;//是否正在刷新中
    private Vibrator mVibrator;


    public SwipeRefreshProgress(Context context) {
        super(context);
        init(context,null);
    }

    public SwipeRefreshProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_swipe_loading_layout,this);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeRefreshProgress);
            Drawable drawable = typedArray.getDrawable(R.styleable.SwipeRefreshProgress_swipeload_loadIcon);
            if(null!=drawable){
                ((ImageView) findViewById(R.id.view_refresh_icon)).setImageDrawable(drawable);
            }
            typedArray.recycle();
        }
        mReProgress = (RelativeLayout) findViewById(R.id.re_progress);
        mReProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRefresh) return;
                try {
                    //触摸反馈
                    if(null==mVibrator) mVibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                    mVibrator.vibrate(Constant.VIBRATOR_MILLIS);
                }catch (RuntimeException e){

                }
                setRefreshing(true);
                if(null!=mOnRefreshListener) mOnRefreshListener.onRefresh();
            }
        });
    }

    /**
     * 改变刷新状态
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing){
        if(null==mReProgress) return;
        showLoadingProgress();
        if(refreshing){
            isRefresh=true;
            mRotate = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mRotate.setDuration(800);
            mRotate.setInterpolator(new LinearInterpolator());
            mRotate.setRepeatCount(Animation.INFINITE);
            mRotate.setRepeatMode(Animation.RESTART);//无限循环
            mReProgress.startAnimation(mRotate);
        }else{
            isRefresh=false;
            if(null!=mRotate) mRotate.cancel(); mRotate=null;
        }
    }

    /**
     * 设置刷新按钮得状态
     * @param enabled
     */
    public void setEnabled(boolean enabled){
//        Logger.d(TAG,"enabled:"+enabled+",isShowing:"+isShowing+",isHideing:"+isHideing);
        if(enabled){
            showLoadingProgress();
        }else{
            hideLoadProgress();
        }
    }

    /**
     * 显示刷新按钮
     */
    private void showLoadingProgress(){
        if(null==mReProgress) return;
        if(mReProgress.getVisibility()==VISIBLE) return;
        if(isShowing) return;
        isShowing=true;
        mReProgress.setVisibility(VISIBLE);
        ScaleAnimation scaleAnimation = AnimationUtil.scalViewAnimationToBig();
        mReProgress.startAnimation(scaleAnimation);
        isShowing=false;
    }

    /**
     * 隐藏刷新按钮
     */
    private void hideLoadProgress(){
        if(null==mReProgress) return;
        if(mReProgress.getVisibility()==INVISIBLE) return;
        if(isHideing) return;
        isHideing=true;
        ScaleAnimation scaleAnimation = AnimationUtil.scalViewAnimationToHid();
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isHideing=false;
                mReProgress.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mReProgress.startAnimation(scaleAnimation);
    }

    public interface OnRefreshListener{
        void onRefresh();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }
}
