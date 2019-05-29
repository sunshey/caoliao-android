package com.yc.liaolive.view.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * TinyHung@Outlook.com
 * 2018/4/12
 */

public class NestedScrollSwipeRefreshLayout extends SwipeRefreshLayout implements NestedScrollingChild{

    private static final int INVALID_POINTER = -1;
    //最小有效滑动
    private int mTouchSlop;
    private int mActivePointerId = INVALID_POINTER;

    private NestedScrollingChildHelper mChildHelper;


    public NestedScrollSwipeRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public NestedScrollSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }


    /**
     * 实现一些跟NestedScrollingParent交互的一些方法,通知父view
     * @param axes
     * @return
     */
    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    /**
     * 结束整个流程。
     */
    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    /**
     * 向父view汇报滚动情况，包括子view消费的部分和子view没有消费的部分。
     * 这个函数一般在子view处理scroll后调用。
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     * @param offsetInWindow
     * @return  如果父view接受了它的滚动参数，进行了部分消费，则这个函数返回true，否则为false。
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    /**
     * 一般在MotionEvent.Move里调用 通知父View滑动的距离
     *
     *
     * 由于窗体进行了移动，如果你记录了手指最后的位置，需要根据第四个参数offsetInWindow计算偏移量，
     * 才能保证下一次的touch事件的计算是正确的。
     * 一般在子view处理scroll前调用
     * @param dx
     * @param dy
     * @param consumed 第一个元素是父view消费的x方向的滚动距离；第二个元素是父view消费的y方向的滚动距离
     * @param offsetInWindow 子View的窗体偏移量
     * @return  如果父view接受了它的滚动参数，进行了部分消费，则这个函数返回true，否则为false
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    //记住最后按下时点的y值
    private float mLastMotionY;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    //标识是否已经开始拖动
    private boolean mIsBeginDrag = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                //获得触摸点id.up的时候id会失效
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                final float initialDownY = getMotionEventY(ev, mActivePointerId);

                //为-1,多点触摸，之后抬起，把事件交给父布局处理
                if (initialDownY == -1) {
                    return false;
                }
                //保存按下的y值，填充给最后移动的距离
                mLastMotionY = initialDownY;
                //通知父view开始嵌套滚动
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                super.onInterceptTouchEvent(ev);
                //初始化标识
                mIsBeginDrag = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //如果触摸点id 为 -1  把点击事件交给父布局处理
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                //标记触摸点的y值
                final float y = getMotionEventY(ev, mActivePointerId);
                //为-1,多点触摸，之后抬起，把点击事件交给父布局处理
                if (y == -1) {
                    return false;
                }
                //计算移动的距离
                int deltaY = (int)(mLastMotionY - y);
                //更新 最后按下时点的y值
                mLastMotionY  = y;
                //移动的距离是否有效，更新 是否正在拖动的标识
                if (Math.abs(deltaY) >= mTouchSlop) {
                    mIsBeginDrag = true;
                }

                /**
                 * 正在拖动,父view消耗了部分view,就交给父view处理
                 * mScrollOffset[1] 子view窗口y轴的偏移量
                 * mScrollOffset[1] 父view消费的y轴偏移量
                 */
                if (mIsBeginDrag && dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1];//修正最后按下的点的y值
                    deltaY -= mScrollConsumed[1];//修正被父view消耗后的移动的距离
                    ev.offsetLocation(0, mScrollConsumed[1]);//修正ev的位置
                    //向父View汇报，如果父view接受了参数,进行了部分消费,返回true,否则返回false
                    if (dispatchNestedScroll(0, 0, 0, deltaY, mScrollOffset)) {
                        mLastMotionY -= mScrollOffset[1];//修正最后按下的点的y值
                        ev.offsetLocation(0, mScrollOffset[1]);//修正点击的位置
                    }
                    return false;
                } else {
                    //否则正常处理
                    return super.onInterceptTouchEvent(ev);
                }
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                //停止嵌套滑动
                stopNestedScroll();
                //初始化相关值
                mActivePointerId = INVALID_POINTER;
                mIsBeginDrag = false;
                return super.onInterceptTouchEvent(ev);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 返回 坐标的y值
     * 解决多点触摸的问题
     * @param ev
     * @param activePointerId
     * @return
     */
    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        //得到触点的索引值，范围是 0 到 ev.getPointerCount()-1;
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }
}
