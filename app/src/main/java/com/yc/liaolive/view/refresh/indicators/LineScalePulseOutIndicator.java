package com.yc.liaolive.view.refresh.indicators;

import android.animation.ValueAnimator;
import java.util.ArrayList;

/**
 * TinyHung@Outlook.com
 * 2018/10/10
 * 加载框线性动画
 */

public class LineScalePulseOutIndicator extends LineScaleIndicator {

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators=new ArrayList<>();
        long[] delays=new long[]{360,180,0,180,360};
        for (int i = 0; i < 5; i++) {
            final int index=i;
            ValueAnimator scaleAnim=ValueAnimator.ofFloat(1,0.3f,1);
            scaleAnim.setDuration(500);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim,new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
        }
        return animators;
    }
}
