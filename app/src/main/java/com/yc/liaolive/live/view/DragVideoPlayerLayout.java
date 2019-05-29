package com.yc.liaolive.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * TinyHung@Outlook.com
 * 2018/7/5
 */

public class DragVideoPlayerLayout extends RelativeLayout {

    private int x, y;
    private int r, l, t, b;

    public DragVideoPlayerLayout(Context context) {
        this(context, null);
    }

    public DragVideoPlayerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragVideoPlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                AnimatorSet setDown = new AnimatorSet();
//                setDown.playTogether(
//                        ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.2f),
//                        ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.2f),
//                        ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f)
//                );
//                setDown.start();
                break;
            case MotionEvent.ACTION_MOVE:
                l = (int) (x + getTranslationX() - getWidth() / 2 + getLeft());
                t = (int) (y + getTranslationY() - getHeight() / 2 + getTop());
                r = l + getWidth();
                b = t + getHeight();
                layout(l,t,r,b);
                break;
            case MotionEvent.ACTION_UP:
//                AnimatorSet setUp = new AnimatorSet();
//                setUp.playTogether(
//                        ObjectAnimator.ofFloat(this, "scaleX", 1.2f, 1f),
//                        ObjectAnimator.ofFloat(this, "scaleY", 1.2f, 1f),
//                        ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1f)
//                );
//                setUp.start();
                break;
        }

        return true;
    }
}
