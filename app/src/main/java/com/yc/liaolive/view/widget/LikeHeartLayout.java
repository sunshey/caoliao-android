package com.yc.liaolive.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.interfaces.OnAnimationListener;

/**
 * TinyHung@outlook.com
 * 2018/10/9
 * 单纯的点赞
 */

public class LikeHeartLayout extends RelativeLayout {

    private static final String TAG = LikeHeartLayout.class.getSimpleName();
    private final Context context;
    private LikeView mLikeView;
    private boolean isPriceAnimationPlaying=false;//是否正在播放动画


    public LikeHeartLayout(Context context) {
        this(context,null);
    }

    public LikeHeartLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LikeHeartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.video_group_layout,this);
        mLikeView = (LikeView) findViewById(R.id.like_view);
    }


    /**
     * 外界传值，是否支持点赞动画
     * @param isPrice
     */
    public void setIsPrice(boolean isPrice){
        this.isPriceAnimationPlaying =isPrice;
    }

    /**
     * 初始不显示点赞动画
     */
    public void setImageVisibility(){
        if(null!=mLikeView) mLikeView.setVisibility(GONE);
    }

    /**
     * 播放点赞动画
     */
    public void startPriceAnimation() {
        if(isPriceAnimationPlaying){
            return;
        }
        if(null!=mLikeView){
            mLikeView.startViewMotion(new OnAnimationListener() {
                @Override
                public void onStart() {
                    isPriceAnimationPlaying=true;
                    if(null!=mLikeView){
                        mLikeView.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onStop() {
                    isPriceAnimationPlaying=false;
                    if(null!=mLikeView){
                        mLikeView.setVisibility(GONE);
                    }
                }
            });
        }
    }
}
