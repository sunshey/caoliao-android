package com.yc.liaolive.view.refresh;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import com.yc.liaolive.view.refresh.header.XinQuRefreshHeaderView;
import com.yc.liaolive.view.refresh.listener.OnRefreshHeaderListener;


/**
 * TinyHung@Outlook.com
 * 2017/9/28.
 * 自定义下拉刷新控件，适用于所有ViewGroup
 */

public class SwipePullRefreshLayout extends ViewGroup {

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int DRAG_MAX_DISTANCE = 80;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;
    public static final int STYLE_MATERIAL = 0;
    private static final String TAG = "PullRefreshLayout";
    private View mTarget;
    private View mHeaderView=null;
    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
    private int mSpinnerFinalOffset;
    private int mTotalDragDistance;//头部刷新View高度
    private int mCurrentOffsetTop;
    private boolean mRefreshing;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private int mFrom;
    private boolean mNotify;
    private OnRefreshListener mListener;
    public int mDurationToStartPosition;
    public int mDurationToCorrectPosition;
    private int mInitialOffsetTop;
    private boolean mDispatchTargetTouchDown;
    private float mDragPercent;
    private State state = State.RESET;
    private int mNewCount;//刷新的数量，如果不为0，需要通知给头部刷新

    public SwipePullRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipePullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        int defaultDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mDurationToStartPosition = defaultDuration;
        mDurationToCorrectPosition = defaultDuration;
        mSpinnerFinalOffset = mTotalDragDistance = dp2px(DRAG_MAX_DISTANCE);

        XinQuRefreshHeaderView xinQuRefreshHeaderView=new XinQuRefreshHeaderView(context);
        mHeaderView=xinQuRefreshHeaderView;
        addView(mHeaderView,0);
        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }


    /**
     * 提供设置自定义HeaderView方法
     * @param view
     */
    public void setRefreshHeaderView(View view){
        if(null!=view&&view!=mHeaderView){
            if(null!=mHeaderView) removeView(mHeaderView);
            view.setVisibility(View.GONE);
            mHeaderView=view;
            addView(mHeaderView,0);
        }
        invalidate();
    }

    /**
     * 返回滑动实时数值
     * @return
     */
    public int getFinalOffset() {
        return mSpinnerFinalOffset;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || (canChildScrollUp() && !mRefreshing)) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mRefreshing) {
                    setTargetOffsetTop(0, true);
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
                mInitialOffsetTop = mCurrentOffsetTop;
                mDispatchTargetTouchDown = false;
                mDragPercent = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                if (mRefreshing) {
                    mIsBeingDragged = !(yDiff < 0 && mCurrentOffsetTop <= 0);
                } else if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                int targetY;
                if (mRefreshing) {
                    targetY = (int) (mInitialOffsetTop + yDiff);
                    if (canChildScrollUp()) {
                        targetY = -1;
                        mInitialMotionY = y;
                        mInitialOffsetTop = 0;
                        if (mDispatchTargetTouchDown) {
                            mTarget.dispatchTouchEvent(ev);
                        } else {
                            MotionEvent obtain = MotionEvent.obtain(ev);
                            obtain.setAction(MotionEvent.ACTION_DOWN);
                            mDispatchTargetTouchDown = true;
                            mTarget.dispatchTouchEvent(obtain);
                        }
                    } else {
                        if (targetY < 0) {
                            if (mDispatchTargetTouchDown) {
                                mTarget.dispatchTouchEvent(ev);
                            } else {
                                MotionEvent obtain = MotionEvent.obtain(ev);
                                obtain.setAction(MotionEvent.ACTION_DOWN);
                                mDispatchTargetTouchDown = true;
                                mTarget.dispatchTouchEvent(obtain);
                            }
                            targetY = 0;
                        } else if (targetY > mTotalDragDistance) {
                            targetY = mTotalDragDistance;
                        } else {
                            if (mDispatchTargetTouchDown) {
                                MotionEvent obtain = MotionEvent.obtain(ev);
                                obtain.setAction(MotionEvent.ACTION_CANCEL);
                                mDispatchTargetTouchDown = false;
                                mTarget.dispatchTouchEvent(obtain);
                            }
                        }
                    }
                 //该状态下表示没有正在刷新或者触摸滑动事件被子ContentView获取
                } else {
                    //状态为Reset并且已滑动的距离小于等于0认为是第一次滑动
                    if(state == State.RESET&&0==mDragPercent){
                        changeState(State.PULL);
                    }
                    //没有正在刷新处理滑动的事件
                    final float scrollTop = yDiff * DRAG_RATE;

                    float originalDragPercent = scrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    mDragPercent = Math.min(1f, Math.abs(originalDragPercent));

                    float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                    float slingshotDist = mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;
                    targetY = (int) ((slingshotDist * mDragPercent) + extraMove);
                    //回调给头部自行改变状态
                    if (mHeaderView instanceof OnRefreshHeaderListener) {
                        ((OnRefreshHeaderListener) mHeaderView).onPositionChange(scrollTop, mTotalDragDistance, mDragPercent);
                    }
                    if(null!=mOnPullChangeListener){
                        mOnPullChangeListener.onPullOfset(mDragPercent);
                    }
                }
                if (mHeaderView.getVisibility() != View.VISIBLE) {
                    mHeaderView.setVisibility(View.VISIBLE);
                }

                setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                if (mRefreshing) {
                    if (mDispatchTargetTouchDown) {
                        mTarget.dispatchTouchEvent(ev);
                        mDispatchTargetTouchDown = false;
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    mRefreshing = false;
                    changeState(State.RESET);
                    animateOffsetToStartPosition();
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }




    private void changeState(State state) {
        this.state = state;
        OnRefreshHeaderListener refreshHeader = this.mHeaderView instanceof OnRefreshHeaderListener ? ((OnRefreshHeaderListener) this.mHeaderView) : null;
        if (refreshHeader != null) {
            switch (state) {
                case RESET:
                    refreshHeader.onReset();
                    if(null!=mOnPullChangeListener){
                        mOnPullChangeListener.onUp();
                    }
                    break;
                case PULL:
                    refreshHeader.onThuchPull();
                    break;
                case LOADING:
                    refreshHeader.onRefreshing();
                    break;
                case COMPLETE:
                    refreshHeader.onRefreshComplete();
                    break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureTarget();
        if (mTarget == null)
            return;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        mHeaderView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mHeaderView)
                    mTarget = child;
            }
        }
    }


    public void setDurations(int durationToStartPosition, int durationToCorrectPosition) {
        mDurationToStartPosition = durationToStartPosition;
        mDurationToCorrectPosition = durationToCorrectPosition;
    }

    /**
     * 停止刷新，慢慢隐藏头部
     */
    private void animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mDurationToStartPosition);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimateToStartPosition);
    }


    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mSpinnerFinalOffset;
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();
            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    /**
     * 开始加载更多
     */
    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(mDurationToCorrectPosition);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener);
        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimateToCorrectPosition);
    }


    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };



    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mHeaderView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                if (mHeaderView instanceof OnRefreshHeaderListener) {
                    ((OnRefreshHeaderListener) mHeaderView).onRefreshStart();
                }
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            } else {
                if (mHeaderView instanceof OnRefreshHeaderListener) {
                    ((OnRefreshHeaderListener) mHeaderView).onRefreshEnd();
                }
                mHeaderView.setVisibility(View.GONE);
                animateOffsetToStartPosition();
            }
            mCurrentOffsetTop = mTarget.getTop();
        }
    };


    /**
     * 刷新完成，回到最初
     * @param interpolatedTime
     */
    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        int offset = targetTop - mTarget.getTop();
        setTargetOffsetTop(offset, false);
    }


    /**
     *
     * @param refreshing
     * @param newCount 0：不显示刷新提示 -1：刷新失败 >1:显示刷新结果
     */
    public void setRefreshing(boolean refreshing,int newCount) {
        if (mRefreshing != refreshing) {
            this.mNewCount=newCount;
            setRefreshing(refreshing, false);
        }
    }


    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            //开始刷新
            if (mRefreshing) {
                animateOffsetToCorrectPosition();
                changeState(State.LOADING);
            //停止刷新
            } else {
                if(0==mNewCount){
                    animateOffsetToStartPosition();
                    changeState(State.COMPLETE);
                    changeState(State.RESET);
                }else if(mNewCount>0){
                    if (mHeaderView instanceof OnRefreshHeaderListener) {
                        ((OnRefreshHeaderListener) mHeaderView).onRefreshNewCount(mNewCount);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateOffsetToStartPosition();
                            changeState(State.COMPLETE);
                            changeState(State.RESET);
                            mNewCount=0;
                        }
                    },1400);
                }else if(-1==mNewCount){
                    if (mHeaderView instanceof OnRefreshHeaderListener) {
                        ((OnRefreshHeaderListener) mHeaderView).onRefreshError();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateOffsetToStartPosition();
                            changeState(State.COMPLETE);
                            changeState(State.RESET);
                        }
                    },1400);

                }else{
                    animateOffsetToStartPosition();
                    changeState(State.COMPLETE);
                    changeState(State.RESET);
                }
                mNewCount=0;
            }
        }
    }


    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            //通知头部完成了所有事情
            if (mHeaderView instanceof OnRefreshHeaderListener) {
                ((OnRefreshHeaderListener) mHeaderView).onRefreshEnd();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
//            mRefreshDrawable.stop();
            mHeaderView.setVisibility(View.GONE);
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
//        mRefreshView.bringToFront();
        mTarget.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureTarget();
        if (mTarget == null)
            return;
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        mTarget.layout(left, top + mTarget.getTop(), left + width - right, top + height - bottom + mTarget.getTop());
        mHeaderView.layout(left, top, left + width - right, top + height - bottom);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public static interface OnRefreshListener {
        public void onRefresh();
    }

    private enum State {
        RESET, PULL, LOADING, COMPLETE
    }
    //提供为调用者的坚持持续下拉事件

    public interface OnPullChangeListener{
        void onPullOfset(float ofset);
        void onUp();//松手
    }
    private OnPullChangeListener mOnPullChangeListener;
    public void setOnPullChangeListener(OnPullChangeListener onPullChangeListener) {
        mOnPullChangeListener = onPullChangeListener;
    }
}
