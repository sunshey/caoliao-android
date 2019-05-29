package com.yc.liaolive.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * @time 2016/12/29 15:58
 * @des $上下拉回弹的阻尼效果
 */
public class ReboundListView extends ListView {

    private static final int MAX_Y_OVERSCROLL_DISTANCE = 200;

    private Context mContext;
    private int mMaxYOverscrollDistance;

    public ReboundListView(Context context) {
        super(context);
        mContext = context;
        initBounceListView();
    }

    public ReboundListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBounceListView();
    }

    public ReboundListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceListView();
    }

    private void initBounceListView() {
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }
    /**
     * 设置滚动接口
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener = onScrollListener;
    }
    /**
     * 滚动的回调接口
     */
    public interface OnScrollListener{
        /**
         * 回调方法，返回第一个条目的可见状态
         */
        void onIsTopVisible(boolean isVisible);
    }
    private OnScrollListener onScrollListener;

    private boolean isTop=true;


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
        //&& getChildAt(0).getTop() >= getPaddingTop()
        if (getChildCount() > 0 && getFirstVisiblePosition() == 0 ) {
            isTop= true;
        // && getChildAt(1).getTop() >= getPaddingTop()
        }else if(getChildCount() > 0 && getFirstVisiblePosition() == 1){
            isTop=false;
        }
        if(onScrollListener!=null){
            onScrollListener.onIsTopVisible(isTop);
        }
    }
}
