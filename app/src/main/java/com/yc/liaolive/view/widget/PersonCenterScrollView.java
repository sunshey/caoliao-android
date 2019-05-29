package com.yc.liaolive.view.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * TinyHung@Outlook.com
 * 2018/9/22
 * 个人用心阻尼下拉ScrillView
 */

public class PersonCenterScrollView extends ScrollView {

    private static final float DEFAULT_LOAD_FACTOR = 3.0F;
    private static final int DEFAULT_SCROLL_HEIGHT = 500;
    private View mHeaderView;
    private int mOriginHeight;
    private int mZoomedHeight;
    private int mMaxScrollHeight = DEFAULT_SCROLL_HEIGHT;

    public PersonCenterScrollView(Context context) {
        super(context);
    }

    public PersonCenterScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public PersonCenterScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(null!=mOnScrollChangeListener) mOnScrollChangeListener.onScrollChange(l,t,oldl,oldt);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if(null != mHeaderView) {
            if(isTouchEvent && deltaY < 0) {
                mHeaderView.getLayoutParams().height += Math.abs(deltaY / DEFAULT_LOAD_FACTOR);
                mHeaderView.requestLayout();
                mZoomedHeight = mHeaderView.getHeight();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(null != mHeaderView && 0 != mOriginHeight && 0 != mZoomedHeight) {
            int action = ev.getAction();
            if(MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
                resetHeaderViewHeight();
            }
        }
        return super.onTouchEvent(ev);
    }

    public void setHeaderView(View headerView) {
        this.mHeaderView = headerView;
        updateHeaderViewHeight();
    }

    private void updateHeaderViewHeight() {
        mOriginHeight = null == mHeaderView ? 0 : mHeaderView.getHeight();
        if(0 == mOriginHeight && null != mHeaderView) {
            post(new Runnable() {
                @Override
                public void run() {
                    mOriginHeight = mHeaderView.getHeight();
                }
            });
        }
    }

    private int evaluateAlpha(int t) {
        if (t >= mMaxScrollHeight) {
            return 255;
        }
        return (int) (255 * t /(float) mMaxScrollHeight);
    }

    private void resetHeaderViewHeight() {
        if(mHeaderView.getLayoutParams().height != mOriginHeight) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(mZoomedHeight, mOriginHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHeaderView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    mHeaderView.requestLayout();
                }
            });
            valueAnimator.setDuration(200);
            valueAnimator.start();
        }
    }

    public interface OnScrollChangeListener{
        void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        mOnScrollChangeListener = onScrollChangeListener;
    }
}
