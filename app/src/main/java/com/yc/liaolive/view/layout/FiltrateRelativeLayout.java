package com.yc.liaolive.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * TinyHung@Outlook.com
 * 2017/11/16
 */

public class FiltrateRelativeLayout extends RelativeLayout {

    public FiltrateRelativeLayout(Context context) {
        this(context,null);
    }

    public FiltrateRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FiltrateRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(null!=mOnClickListener){
                mOnClickListener.onClickView(FiltrateRelativeLayout.this);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnClickListener {
        void onClickView(View view);
    }
    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
