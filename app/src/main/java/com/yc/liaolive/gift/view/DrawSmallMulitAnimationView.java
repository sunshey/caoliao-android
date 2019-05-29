package com.yc.liaolive.gift.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import com.yc.liaolive.gift.listener.AnimatorPlayListener;
import com.yc.liaolive.live.bean.AwardInfo;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/6/15
 * 小倍率的中奖动画
 */

public class DrawSmallMulitAnimationView extends android.support.v7.widget.AppCompatTextView {

    private static final long CLEAN_MILLIS = 2500;//定时清除自己
    private static final String TAG = "DrawSmallMulitAnimationView";
    private int strokeColor=Color.BLACK;//描边颜色


    public DrawSmallMulitAnimationView(Context context) {
        super(context);
    }

    public DrawSmallMulitAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStrokeColor(int strokeColor){
        this.strokeColor=strokeColor;
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    /**
     * 开始
     * @param content
     */
    public void startText(String content) {
        this.clearAnimation();
        if(null!=cleanRunnable) this.removeCallbacks(cleanRunnable);
        this.setText(content);
        AnimationUtil.startSmallAwardAnim(this,1000,null);
        this.postDelayed(cleanRunnable,CLEAN_MILLIS);
    }

    /**
     * 结束
     */
    private void stop() {
        AnimationUtil.startSmallAwardAnimOut(this, 300, new AnimatorPlayListener() {
            @Override
            public void onStart(AwardInfo awardInfo) {
            }

            @Override
            public void onEnd() {
                DrawSmallMulitAnimationView.this.setBackgroundResource(0);
                DrawSmallMulitAnimationView.this.setText("");
            }
        });
    }

    private  Runnable cleanRunnable=new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    public void onDestroy(){
        if(null!=cleanRunnable) this.removeCallbacks(cleanRunnable);
        DrawSmallMulitAnimationView.this.setBackgroundResource(0);
    }
}


