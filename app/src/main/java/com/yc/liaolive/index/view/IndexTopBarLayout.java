package com.yc.liaolive.index.view;

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
import android.widget.ImageView;
import com.androidkun.xtablayout.XTabLayout;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/8/31
 * 首页Top标题栏
 */

public class IndexTopBarLayout extends FrameLayout{

    public XTabLayout mXTabLayout;
    private View mTabView;
    private boolean isRuning;
    private boolean isShowing;
    private boolean isHiding;

    public IndexTopBarLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IndexTopBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_index_top_layout,this);
        mXTabLayout = (XTabLayout) findViewById(R.id.tab_layout);
        mTabView=findViewById(R.id.tab_layout);
    }

    /**
     * 显示、隐藏标题栏
     * @param isShow
     */
    public void showMainTabLayout(final boolean isShow) {
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
                    if(null!=mOnTopbarChangedListener) mOnTopbarChangedListener.onChanged(isShow);
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
                    if(null!=mOnTopbarChangedListener) mOnTopbarChangedListener.onChanged(isShow);
                }
            });
            outAnimator.setInterpolator(new LinearInterpolator());
            outAnimator.start();
        }
    }
    public void setTollBarBackgroundResource(int resID){
        if(null!=mTabView) mTabView.setBackgroundResource(resID);
    }

    public void showLineView(boolean flag) {
        findViewById(R.id.view_line_view).setVisibility(flag?VISIBLE:GONE);
    }

    public void showSearchBar(boolean flag){
        View searchBar = findViewById(R.id.btn_search);
        searchBar.setVisibility(flag?VISIBLE:GONE);
        searchBar.setOnClickListener(flag?new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnTopbarChangedListener) mOnTopbarChangedListener.onSearch(v);
            }
        }:null);
    }

    public void setSearchBarRes(int res){
        ((ImageView) findViewById(R.id.btn_search)).setImageResource(res);
    }

    public void onDestroy(){
        isShowing=false;isHiding=false;
    }

    public interface OnTopbarChangedListener{
        void onChanged(boolean isShow);
        void onSearch(View view);
    }

    private OnTopbarChangedListener mOnTopbarChangedListener;

    public void setOnTopbarChangedListener(OnTopbarChangedListener onTopbarChangedListener) {
        mOnTopbarChangedListener = onTopbarChangedListener;
    }
}
