package com.yc.liaolive.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2018/8/24
 * 自定义View+属性动画实现咻一咻雷达效果
 */

public class CircleRadarLayout extends FrameLayout{

    private Paint mPaint;
    private int mColor=getResources().getColor(R.color.app_style);//默认是APP主题色
    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private int mStrokeWidth;
    private boolean isRuning;//是否正在运行
    private int mIntervalDurtion=1000;//间隔多久绘制一个圆 毫秒
    private int mPlayDurtion=5000;//播放一个圆圈需要多久 毫秒
    private Timer timer;
    private boolean mIsRing;//是否绘制环形
    private static int DEFAULT_STROKE_WIDTH = 2;//圆环的宽度 仅在mIsRing为true有效 单位dp
    private boolean mIsAutoRuning;
    private float mMinAlpha =0f;//圆圈最小的透明度
    private ImageView mImageView;
    private Animation mInputAnimation;//抖动动画
    private boolean mIsCustomLayout;
    //位置摆放类型
    public static final String CENTER ="center";
    public static final String RIGHT ="right";
    public static final String LEFT ="left";
    public static final String BOTTOM ="bottom";
    public static final String TOP ="top";

    public CircleRadarLayout(Context context) {
        super(context);
        init(context,null);
    }

    public CircleRadarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    public CircleRadarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mImageView = new ImageView(getContext());
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleRadarLayout );
            mColor = typedArray.getInt(R.styleable.CircleRadarLayout_radarColor, Color.parseColor("#FF0000"));//圆圈、圆环颜色
            mPlayDurtion = typedArray.getInt(R.styleable.CircleRadarLayout_radarPlayDurtion, 3000);//一圈动画需要多久播放完成
            mIntervalDurtion = typedArray.getInt(R.styleable.CircleRadarLayout_radarIntervalDurtion, 1000);//间隔多久绘制一个新的圆环并开始动画
            DEFAULT_STROKE_WIDTH = typedArray.getInt(R.styleable.CircleRadarLayout_radarStrokeWidth, 2);//圆环的宽度
            mIsRing = typedArray.getBoolean(R.styleable.CircleRadarLayout_radarIsRing, false);//是否是圆环
            mIsAutoRuning = typedArray.getBoolean(R.styleable.CircleRadarLayout_radarIsAutoRun, false);//是否自动开始
            mIsCustomLayout = typedArray.getBoolean(R.styleable.CircleRadarLayout_radarCustomLayout, false);//是否是自定义布局
            mMinAlpha = typedArray.getFloat(R.styleable.CircleRadarLayout_radarMinAlpha, 0f);//圆圈的最小透明度
            Drawable drawable = typedArray.getDrawable(R.styleable.CircleRadarLayout_radarIconSrc);//中间Icon
            int iconWidth = typedArray.getInt(R.styleable.CircleRadarLayout_radarIconWidth,50);//ICON宽
            int iconHeight = typedArray.getInt(R.styleable.CircleRadarLayout_radarIconHeight,50);//ICON高
            int mIconPadding = typedArray.getInt(R.styleable.CircleRadarLayout_radarIconPadding,3);//ICON边距
            String typedGravity = typedArray.getString(R.styleable.CircleRadarLayout_radarIconGravity); //位置类型
            //非自定义布局情况下添加一个ICON
            if(!mIsCustomLayout){
                FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(iconWidth>0?ScreenUtils.dpToPxInt(iconWidth):FrameLayout.LayoutParams.MATCH_PARENT, iconHeight>0?ScreenUtils.dpToPxInt(iconHeight):FrameLayout.LayoutParams.MATCH_PARENT);
                mImageView.setLayoutParams(layoutParams);
                layoutParams.gravity=geIcontGravity(typedGravity);
                int paddingLayoutParams= ScreenUtils.dpToPxInt(mIconPadding);
                mImageView.setPadding(paddingLayoutParams,paddingLayoutParams,paddingLayoutParams,paddingLayoutParams);
                addView(mImageView);
                if(null!=drawable) mImageView.setImageDrawable(drawable);
                mInputAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            }
            typedArray.recycle();
        }
        mStrokeWidth = ScreenUtils.dpToPxInt(DEFAULT_STROKE_WIDTH);
        if(mIsAutoRuning) onStart();
    }

    /**
     * @param typedGravity 位置类型
     * @return
     */
    private int geIcontGravity(String typedGravity) {
        if(TextUtils.equals(typedGravity,CENTER)){
            return Gravity.CENTER;
        }else if(TextUtils.equals(typedGravity,RIGHT)){
            return Gravity.RIGHT;
        }else if(TextUtils.equals(typedGravity,LEFT)){
            return Gravity.LEFT;
        }else if(TextUtils.equals(typedGravity,BOTTOM)){
            return Gravity.BOTTOM;
        }else if(TextUtils.equals(typedGravity,TOP)){
            return Gravity.TOP;
        }
        return Gravity.CENTER;
    }

    /**
     * 设置圆圈、圆环颜色
     * @param color
     */
    public void setStyleColor(int color){
        this.mColor=color;
        invalidate();
    }

    /**
     * 设置一圈需要多久播放完成
     * @param playMillis
     * 将在下次绘制生效
     */
    public void setPlayDurtion(int playMillis){
        this.mPlayDurtion=playMillis;
    }

    /**
     * 设置间隔多久绘制一个圆、圈
     * @param intervalDurtion
     * 将在下次绘制生效
     */
    public void setIntervalDurtion(int intervalDurtion){
        this.mIntervalDurtion=intervalDurtion;
    }

    /**
     * 是否绘制环形
     * @param isRing
     */
    public void setDrawCircleRing(boolean isRing){
        this.mIsRing=isRing;
        invalidate();
    }

    /**
     * 设置环形的宽度
     * @param widthDP 单位DP
     * 将在下次绘制生效
     */
    public void setRingStrokeWidthDP(int widthDP){
        this.DEFAULT_STROKE_WIDTH=widthDP;
    }

    /**
     * 设置环形的宽度
     * @param widthPX 单位PX
     * 将在下次绘制生效
     */
    public void setRingStrokeWidthPX(int widthPX){
        this.mStrokeWidth=widthPX;
    }

    /**
     * 设置最小透明度
     * @param minAlpha
     * 将在下次绘制生效
     */
    public void setMinAlpha(float minAlpha){
        this.mMinAlpha =minAlpha;
    }

    /**
     * 设置ICON图标
     * @param drawable
     */
    public void setIcon(Drawable drawable){
        if(null!=drawable&&null!=mImageView){
            mImageView.setImageDrawable(drawable);
        }
    }

    /**
     * 设置Icon图标
     * @param resID
     */
    public void setIcon(int resID){
        if(mIsCustomLayout||null==mImageView) return;
        Drawable drawable = getContext().getResources().getDrawable(resID);

        if(null!=drawable){
            mImageView.setImageDrawable(drawable);
        }
    }

    public void setIconView (int width, int height, int mIconPadding, int resID) {
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(width, height);
        mImageView.setLayoutParams(layoutParams);
        layoutParams.gravity = Gravity.CENTER;
        mImageView.setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPadding);
        addView(mImageView);
        mImageView.setImageDrawable(getContext().getResources().getDrawable(resID));
    }

    /**
     * 设置Icon图标
     * @param icon
     */
    public void setIcon(String icon){
        if(mIsCustomLayout||null==mImageView) return;
        Glide.with(getContext())
                .load(icon)
                .error(R.drawable.ic_main_tab)
                .crossFade()//渐变
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .into(mImageView);
    }


    /**
     * 开始执行
     */
    public void onStart(){
        startPlayer(false);
    }

    public void startPlayer(boolean isAnimation){
        if(isRuning) return;
        isRuning=true;
        if(isAnimation&&null!=mInputAnimation&&null!=mImageView) mImageView.startAnimation(mInputAnimation);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CircleRadarLayout.this.post(new Runnable() {
                    @Override
                    public void run() {
                        addACircle();
                    }
                });
            }
        };
        if(null==timer) timer = new Timer();
        timer.schedule(task, 0, mIntervalDurtion);
    }

    /**
     * 结束执行
     */
    public void onStop(){
        isRuning=false;
        if(null!=timer) timer.cancel(); timer=null;
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        onStop();
        if(null!=mInputAnimation) mInputAnimation.cancel(); mInputAnimation=null;
        mPaint=null; mIsRing=false;
        mImageView=null;
    }


    /**
     * 绘制一个圆
     */
    private void addACircle() {
        //绘制一个个自己一样大小的圆、环
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        List<Animator> animators = new ArrayList<Animator>();
        final RadarView radarView = new RadarView(getContext());
        radarView.setScaleX(0);
        radarView.setScaleY(0);
        radarView.setAlpha(1);
        //添加在布局中最底层
        addView(radarView, 0, params);
        // 属性动画
        animators.add(create(radarView, "scaleX", 0, 1));
        animators.add(create(radarView, "scaleY", 0, 1));
        animators.add(create(radarView, "alpha", 1, mMinAlpha));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(mMinAlpha>0) radarView.setAlpha(0);//用户设置的透明通道大于0，强制隐藏
                CircleRadarLayout.this.removeView(radarView);//移除刚才添加的
            }
        });
        animatorSet.setDuration(mPlayDurtion);
        animatorSet.start();
    }

    private ObjectAnimator create(View target, String propertyName, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, propertyName, from, to);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        return animator;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        // 确定圆的圆点坐标及半径
        mCenterX = width * 0.5f;
        mCenterY = height * 0.5f;
        mRadius = Math.min(width, height) * 0.5f;
    }

    private class RadarView extends View {
        public RadarView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            if (null == mPaint) {
                mPaint = new Paint();
                mPaint.setColor(mColor);
                mPaint.setAntiAlias(true);
                // 注意Style的用法，【STROKE：画环】【FILL：画圆】
                mPaint.setStyle(mIsRing ? Paint.Style.STROKE : Paint.Style.FILL);
                mPaint.setStrokeWidth(mIsRing ? mStrokeWidth : 0);
            }
            // 画圆或环
            canvas.drawCircle(mCenterX, mCenterY, mIsRing ? mRadius - mStrokeWidth : mRadius, mPaint);
        }
    }
}
