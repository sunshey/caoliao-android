package com.yc.liaolive.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/8/31
 * 首页Top标题栏
 */

public class IndexFollowTopBarLayout extends FrameLayout{

    private View mTabView;
    private boolean isRuning;
    private boolean isShowing;
    private boolean isHiding;

    public IndexFollowTopBarLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IndexFollowTopBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_index_follow_top_layout,this);
        mTabView = findViewById(R.id.tab_layout);
    }

    /**
     * 显示、隐藏标题栏
     * @param isShow
     */
    public void showMainTabLayout(boolean isShow) {
        if(null==mTabView) return;
        if(isRuning) return;
        if(isShow){
            isHiding=false;
            if(isShowing) return;
            isShowing=true;
            isRuning=true;
            ObjectAnimator inAnimator = ObjectAnimator.ofFloat(mTabView, "translationY",  -mTabView.getHeight(),0);
            inAnimator.setInterpolator(new LinearInterpolator());
            inAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isRuning=false;
                }
            });
            inAnimator.setDuration(300);
            inAnimator.start();
        }else{
            isShowing=false;
            if(isHiding) return;
            isHiding=true;
            isRuning=true;
            ObjectAnimator outAnimator = ObjectAnimator.ofFloat(mTabView, "translationY", 0,-mTabView.getHeight());
            outAnimator.setDuration(300);
            outAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isRuning=false;
                }
            });
            outAnimator.setInterpolator(new LinearInterpolator());
            outAnimator.start();
        }
    }

    public void setTitle(String title){
        TextView view = (TextView) findViewById(R.id.index_follow_title);
        if(null!=view) view.setText(title);
    }

    public void onDestroy(){
        isShowing=false;isHiding=false;
    }
}
