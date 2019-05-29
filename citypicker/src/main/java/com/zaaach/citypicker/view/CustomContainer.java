package com.zaaach.citypicker.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by wanglin  on 2018/7/28 14:10.
 */
public class CustomContainer extends RelativeLayout {
    private static final String TAG = "CustomContainer";
    private SideIndexBar mIndexBar;
    private Rect indexBarRect = new Rect();//保存indexbar的位置

    public CustomContainer(Context context) {
        super(context);
    }

    public CustomContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIndexBar(final SideIndexBar indexBar) {
        this.mIndexBar = indexBar;
        this.mIndexBar.post(new Runnable() {
            @Override
            public void run() {
                indexBarRect.set(indexBar.getLeft(), indexBar.getTop(), indexBar.getRight(), indexBar.getBottom());
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIndexBar == null) throw new NullPointerException("indexBar 必须不能为空");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                if (indexBarRect.contains(x, y)) {
                    mIndexBar.setVisibility(VISIBLE);
                    mIndexBar.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIndexBar.setVisibility(GONE);

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
