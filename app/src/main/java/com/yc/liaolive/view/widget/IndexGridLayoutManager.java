package com.yc.liaolive.view.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * TinyHung@Outlook.com
 * 2018/9/29
 */

public class IndexGridLayoutManager extends GridLayoutManager {

    public IndexGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public IndexGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public IndexGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            return super.scrollHorizontallyBy(dx, recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
