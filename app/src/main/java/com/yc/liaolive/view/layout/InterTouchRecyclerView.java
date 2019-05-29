package com.yc.liaolive.view.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * TinyHung@Outlook.com
 * 2017/12/4.
 */

public class InterTouchRecyclerView extends RecyclerView {
    public InterTouchRecyclerView(Context context) {
        super(context);
    }

    public InterTouchRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterTouchRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        getParent().requestDisallowInterceptTouchEvent(true);//请求父View不要消费触摸事件
        return super.onInterceptTouchEvent(e);
    }
}
