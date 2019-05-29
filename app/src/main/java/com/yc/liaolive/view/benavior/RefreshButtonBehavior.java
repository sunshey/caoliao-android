package com.yc.liaolive.view.benavior;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import com.yc.liaolive.view.widget.SwipeRefreshProgress;

/**
 * TinyHung@Outlook.com
 * 2018/7/26
 * 观察AppBarLayout的滚动状态
 */

public class RefreshButtonBehavior extends CoordinatorLayout.Behavior<View> {

    private String TAG="RefreshButtonBehavior";
    private float oldY=0;

    public RefreshButtonBehavior(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 被观察者
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }


    /**
     * 当被观察的对象发生了变化
     * @param parent 根容器View
     * @param child  观察者
     * @param dependency 被观察者
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.abs(dependency.getTop());
        if(null==child) return false;
        //当被观察的AppBarLayout发生变化，自己做出改变
        if(child instanceof SwipeRefreshProgress){
            SwipeRefreshProgress progress= (SwipeRefreshProgress) child;
            //往上滑动,隐藏刷新按钮
            if(translationY>oldY){
                progress.setEnabled(false);
                //往下滑动 显示刷新按钮
            }else if(translationY<oldY){
                progress.setEnabled(true);
            }
            oldY=translationY;
        }
//        child.setTranslationY(translationY);
        return true;
    }
}
