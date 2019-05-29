package com.yc.liaolive.view.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import com.yc.liaolive.util.AnimationUtil;

/**
 * TinyHung@outlook.com
 * 2017/5/20 12:13
 */
public class FooterBehavior extends CoordinatorLayout.Behavior<View> {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private float viewY;//控件距离coordinatorLayout底部距离
    private boolean isAnimate;//动画是否在进行
    public FooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //coordinatorLayout中有滚动发生的时候会回调该方法。我们可以在该方法中获取到滚动的方向，可以获取到注册该行为的view，也就是child。大家一定要Ctrl+q看看英文的文档介绍。
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        //在第一次进入的时候获取到控件距离父布局（coordinatorLayout）底部距离。根据这个控件到底部的距离，使用插值器，来做显示隐藏的动画。
        if(child.getVisibility() == View.VISIBLE && viewY==0){
            viewY=coordinatorLayout.getHeight()-child.getY();
        }
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;//判断是否竖直滚动
    }
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        //手指向上移动,屏幕内容上移dy>0,手指下移dy<0
        if (dy >=0 && !isAnimate && child.getVisibility()==View.VISIBLE) {

            hide(child);
        } else if (dy <0 && !isAnimate && child.getVisibility()==View.GONE) {
            show(child);
        }
    }


    private void show(View child) {
        TranslateAnimation translateAnimation = AnimationUtil.moveToViewLocation();
        child.startAnimation(translateAnimation);
    }

    private void hide(View child) {
        TranslateAnimation translateAnimation = AnimationUtil.moveToViewBottom();
        child.startAnimation(translateAnimation);
    }
}
