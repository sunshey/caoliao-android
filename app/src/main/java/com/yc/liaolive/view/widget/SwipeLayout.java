package com.yc.liaolive.view.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * TinyHung@Outlook.com
 * 2017/10/22
 * 一个侧滑拉出删除菜单的封装
 */

public class SwipeLayout extends FrameLayout {


    private android.support.v4.widget.ViewDragHelper mViewDragHelper;

    public SwipeLayout(@NonNull Context context) {
        this(context,null);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = android.support.v4.widget.ViewDragHelper.create(this,callBack);
    }

    private final android.support.v4.widget.ViewDragHelper.Callback callBack=new android.support.v4.widget.ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }
}
