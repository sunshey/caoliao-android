package com.yc.liaolive.interfaces;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.Calendar;

/**
 * 避免在用户指定的毫秒数内产生多次点击
 */
public abstract class PerfectClickListener implements OnClickListener {

    public static  int MIN_CLICK_DELAY_TIME = 2500;
    private long lastClickTime = 0;
    private int id = -1;

    public PerfectClickListener(int delaedMillis) {
        this.MIN_CLICK_DELAY_TIME=delaedMillis;
    }

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int mId = v.getId();
        if (id != mId) {
            id = mId;
            lastClickTime = currentTime;
            onNoDoubleClick(v);
            return;
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);
}
