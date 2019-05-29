package com.yc.liaolive.index.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/8/18
 * 首页推荐的视频聊 抖动控件
 */

@SuppressLint("AppCompatCustomView")
public class LiveRoomItemStateView extends ImageView{

    private static final String TAG = "LiveRoomItemStateView";

    private Animation shakeAnimation;
    private Handler mHandler;

    public LiveRoomItemStateView(Context context) {
        super(context);
        init(context);
    }

    public LiveRoomItemStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        mHandler = new Handler();
    }

    private void init(Context context) {
        shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.live_shake);
    }

    public void start(){
        if(null!=mHandler) mHandler.removeMessages(0);
        mHandler.postAtTime(autoPlayRunnable, SystemClock.uptimeMillis()+2000);
    }

    public void stop(){
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
    }

    private Runnable autoPlayRunnable=new Runnable() {
        @Override
        public void run() {
            if(null!=shakeAnimation){
                shakeAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(null!=mHandler) mHandler.postAtTime(autoPlayRunnable,SystemClock.uptimeMillis()+3900);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                LiveRoomItemStateView.this.startAnimation(shakeAnimation);
            }
//            AnimationUtil.startShakeByPropertyAnim(LiveRoomItemStateView.this,0.8f,1.0f,1.0f,2000);

        }
    };
}
