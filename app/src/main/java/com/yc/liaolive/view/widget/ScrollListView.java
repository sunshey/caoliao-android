package com.yc.liaolive.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ListView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/5/28
 * 配合头部下拉缩放
 */

public class ScrollListView extends ListView {

    private ImageView mHeaderImg;
    private int mDrawAbleHeight;

    public ScrollListView(Context context) {
        super(context);
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDrawAbleHeight = context.getResources().getDimensionPixelSize(R.dimen.header_drawable_height);
    }

    public void setHeaderViewImg(ImageView img) {
        this.mHeaderImg = img;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (deltaY < 0) {
            mHeaderImg.getLayoutParams().height = mHeaderImg.getHeight() - deltaY;
            mHeaderImg.requestLayout();
        }else if (deltaY >= 0 && mHeaderImg.getHeight() > mDrawAbleHeight) {
            mHeaderImg.getLayoutParams().height = mHeaderImg.getHeight() - deltaY;
            //高度虽然改变了，必须调用重新测量；
            mHeaderImg.requestLayout();
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View vParent= (View) mHeaderImg.getParent();
        if(mHeaderImg.getHeight()>mDrawAbleHeight&&vParent.getTop()<0){
            mHeaderImg.getLayoutParams().height = mHeaderImg.getHeight() + vParent.getTop();
            vParent.layout(0,0,mHeaderImg.getWidth(),mHeaderImg.getHeight());
            mHeaderImg.requestLayout();
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mHeaderImg.getHeight() > mDrawAbleHeight) {
                    ScrollAnimation scrollAnimation = new ScrollAnimation(mHeaderImg, mDrawAbleHeight);
                    scrollAnimation.setDuration(300);
                    mHeaderImg.startAnimation(scrollAnimation);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public class ScrollAnimation extends Animation {
        private int nowImgHeight;
        private int imgHeightDiffer;

        public ScrollAnimation(ImageView img, int initedImgHeight) {
            nowImgHeight = img.getHeight();
            imgHeightDiffer = nowImgHeight - initedImgHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mHeaderImg.getLayoutParams().height = (int) (nowImgHeight - imgHeightDiffer * interpolatedTime);
            //高度虽然改变了，必须调用重新测量；
            mHeaderImg.requestLayout();
            super.applyTransformation(interpolatedTime, t);
        }
    }
}
