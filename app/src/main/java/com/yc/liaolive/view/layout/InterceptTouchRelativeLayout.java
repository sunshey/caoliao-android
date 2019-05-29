package com.yc.liaolive.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * TinyHung@Outlook.com
 * 2017/12/6.
 * 用来控制父Parentd是否消费触摸事件
 */

public class InterceptTouchRelativeLayout extends RelativeLayout {

    private boolean isRequestDisallowInterceptTouchEvent=false;//请求父Parent是否消费触摸事件

    public InterceptTouchRelativeLayout(Context context) {
        super(context);
    }

    public InterceptTouchRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptTouchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(isRequestDisallowInterceptTouchEvent);
        return super.onInterceptTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean flag){
        this.isRequestDisallowInterceptTouchEvent=flag;
    }
}
