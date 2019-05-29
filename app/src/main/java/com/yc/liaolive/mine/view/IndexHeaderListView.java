package com.yc.liaolive.mine.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * TinyHung@Outlook.com
 * 2018/5/26
 * 个人中心配合头部缩放的ListView
 */

public class IndexHeaderListView extends ListView {
    private ImageView mImageView;  // 头布局image
    int mOriginalHeight; //原始高度(圆图片的高度)
    int mSettingHeight;   //布局设置高度
    float MRATIO = 2f;   //手指滑动距离 X  让图像高度变大 (X / 3f).高度

    public IndexHeaderListView(Context context) {
        this(context, null);
    }
    public IndexHeaderListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public IndexHeaderListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 拿到header image的高度
     * @param view
     */
    public void setHeardView(ImageView view) {
        this.mImageView = view;
        mSettingHeight = mImageView.getHeight();
        mOriginalHeight = mImageView.getDrawable().getIntrinsicHeight();
    }

    /**
     * @param deltaX         x方向瞬间变化量  顶部下拉为  -  .
     * @param deltaY
     * @param scrollX        x方向变化量
     * @param scrollY
     * @param scrollRangeX   x方向滑动范围
     * @param scrollRangeY
     * @param maxOverScrollX x方向最大滑动范围
     * @param maxOverScrollY
     * @param isTouchEvent   如果是手指滑动的话true,惯性滑动为false.
     * @return
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (deltaY < 0 && isTouchEvent) {
            if (mImageView.getHeight() <= mOriginalHeight) {   //头布局没有完全显示出来
                int cCurrentHeight = (int) (mImageView.getHeight() + Math.abs(deltaY / MRATIO));
                //改变头布局高度
                mImageView.getLayoutParams().height = cCurrentHeight;
                mImageView.requestLayout();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                //抬起时 图片高度.
                int startHeight = mImageView.getHeight();
                int endHeight = mSettingHeight;
                startAnim(startHeight, endHeight);
                break;
        }
        //此次不能返回 true.  如果返回true的话,那么overScrollBy将不会执行了.因为overScrollBy
        // 的调用必须经过onTouchEvent中调用的.
        return super.onTouchEvent(ev);
    }

    /**
     * 执行回弹动画
     * @param startHeight 动画的开始位置
     * @param endHeight   动画的结束位置
     */
    private void startAnim(final int startHeight, final int endHeight) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 动画执行的的比例进度
                float fraction = (float) animation.getAnimatedValue();
                // 瞬间应该回弹的距离
                float winkDis = (float) (fraction * (startHeight - endHeight));
                //设置mImageView的高度.
                mImageView.getLayoutParams().height = (int) (startHeight - winkDis);
                mImageView.requestLayout();
            }
        });
        animator.setDuration(300);
        animator.start();
    }
}
