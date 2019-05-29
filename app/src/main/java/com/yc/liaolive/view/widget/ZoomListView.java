package com.yc.liaolive.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ListView;
import java.lang.ref.WeakReference;

/**
 * TinyHung@Outlook.com
 * 2018/9/22
 * 用户中心阻尼下拉
 */

public class ZoomListView extends ListView {

    public ZoomListView(Context context) {
        super(context);
    }

    public ZoomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    static class ResetAnimation extends Animation {
        private WeakReference<ZoomListView> listviewRef;
        private ImageView mImageView;
        private int mCurrentHeight;
        private int mExtraHeight;

        public ResetAnimation(ZoomListView listView){
            listviewRef = new WeakReference<ZoomListView>(listView);
            mCurrentHeight = listviewRef.get().mImageView.getHeight();
            mExtraHeight = mCurrentHeight - listviewRef.get().mMinHeaderHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            listviewRef.get().mImageView.getLayoutParams().height = (int) (mCurrentHeight - mExtraHeight * interpolatedTime);
            listviewRef.get().mImageView.requestLayout();
            super.applyTransformation(interpolatedTime, t);
        }
    }

    private View mImageView;
    private int mMinHeaderHeight;


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        /**
         * 拉伸过渡时，处理图片的缩放 ---处理图片高度
         * deltaY: y轴方向拉伸过度的偏移量
         *  - ：下拉过度
         *  + ：上拉过度
         */
        if(null!=mImageView){
            if(deltaY < 0 && mImageView.getHeight() < mMinHeaderHeight * 1.6){
                mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY;
                mImageView.requestLayout();
            }else if(deltaY > 0 && mImageView.getHeight() > mMinHeaderHeight) {
                mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY;
                mImageView.requestLayout();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(null!=mImageView){
            View headerView = (View) mImageView.getParent();
            int top = headerView.getTop();
            if(top < 0 && mMinHeaderHeight < mImageView.getHeight()){
                mImageView.getLayoutParams().height = mImageView.getHeight() + top;
                headerView.layout(headerView.getLeft(), 0, headerView.getRight(), headerView.getHeight());
                mImageView.requestLayout();
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(null!=mImageView&&mMinHeaderHeight == 0){
            mMinHeaderHeight = mImageView.getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(null!=mImageView){
                    ResetAnimation animation = new ResetAnimation(this);
                    animation.setDuration(350);
                    mImageView.startAnimation(animation);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setHeadView(View headView) {
        this.mImageView = headView;
    }
}
