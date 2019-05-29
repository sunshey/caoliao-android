package com.yc.liaolive.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;


/**
 * TinyHung@outlook.com
 * 2017/9/13 16:36
 * 点击有动画效果的ImageView
 */

public class ClickAnimationmageView extends ImageView {

    private static String TAG = "EffectsButton";
    private boolean clickable = true;
    private ScaleAnimation upAnimation = createUpAnim();
    private OnClickListener mOnClickListener;
    private boolean shouldAbortAnim;
    private int preX;
    private int preY;
    private int[] locationOnScreen;
    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        public void onAnimationEnd(Animation paramAnonymousAnimation) {
            //setSelected(!isSelected());
            clearAnimation();
            //Log.d(TAG, "onAnimationEnd: ");
            if (mOnClickListener != null) {
                mOnClickListener.onClickView(ClickAnimationmageView.this);
            }
        }

        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}

        public void onAnimationStart(Animation paramAnonymousAnimation) {}
    };

    public ClickAnimationmageView(Context paramContext) {
        this(paramContext, null);
    }

    public ClickAnimationmageView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public ClickAnimationmageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        upAnimation.setAnimationListener(this.animationListener);
        locationOnScreen = new int[2];
    }

    private ScaleAnimation createUpAnim() {
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.2F, 1.0F, 1.2F, 1.0F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation.setDuration(50L);
        localScaleAnimation.setFillEnabled(true);
        localScaleAnimation.setFillEnabled(false);
        localScaleAnimation.setFillAfter(true);
        return localScaleAnimation;
    }

    private ScaleAnimation createDownAnim() {
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.0F, 1.2F, 1.0F, 1.2F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation.setDuration(50L);
        localScaleAnimation.setFillEnabled(true);
        localScaleAnimation.setFillBefore(false);
        localScaleAnimation.setFillAfter(true);
        return localScaleAnimation;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        if (!this.clickable) {
            return false;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            clearAnimation();
            startAnimation(createDownAnim());
            this.shouldAbortAnim = false;
            getLocationOnScreen(this.locationOnScreen);
            preX = (this.locationOnScreen[0] + getWidth() / 2);
            preY = (this.locationOnScreen[1] + getHeight() / 2);
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            clearAnimation();
            if (!this.shouldAbortAnim) {
                startAnimation(this.upAnimation);
            }
            this.shouldAbortAnim = false;
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            clearAnimation();
            startAnimation(createUpAnim());
            this.shouldAbortAnim = false;
        }
        else if ((motionEvent.getAction() == MotionEvent.ACTION_MOVE) && (!this.shouldAbortAnim) && (!checkPos(motionEvent.getRawX(), motionEvent.getRawY()))) {
            this.shouldAbortAnim = true;
            clearAnimation();
            startAnimation(createUpAnim());
        }
        return true;
    }

    boolean checkPos(float rawX, float rawY) {
        rawX = Math.abs(rawX - this.preX);
        rawY = Math.abs(rawY - this.preY);
        return (rawX <= getWidth() / 2) && (rawY <= getHeight() / 2);
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void setOnClickListener(OnClickListener parama) {
        this.mOnClickListener = parama;
    }

    public interface OnClickListener {
        void onClickView(ClickAnimationmageView view);
    }
}
