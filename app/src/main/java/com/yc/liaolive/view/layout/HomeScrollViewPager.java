package com.yc.liaolive.view.layout;

import android.content.Context;
import android.support.annotation.Px;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * TinyHung@outlook.com
 * 2017/5/22 18:19
 * 不可手动滑动的ViewPager
 */
public class HomeScrollViewPager extends ViewPager {

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    private boolean isScroll=true;

    public HomeScrollViewPager(Context context) {
        super(context);
    }

    public HomeScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isScroll){
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isScroll){
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        super.scrollTo(x, y);
    }
}
