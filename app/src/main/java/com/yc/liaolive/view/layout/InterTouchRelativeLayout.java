package com.yc.liaolive.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * TinyHung@Outlook.com
 * 2017/12/4.
 */

public class InterTouchRelativeLayout extends RelativeLayout {
    public InterTouchRelativeLayout(Context context) {
        super(context);
    }

    public InterTouchRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterTouchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//请求父View不要消费触摸事件
        return super.onInterceptTouchEvent(ev);
    }
}
