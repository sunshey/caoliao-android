package com.yc.liaolive.interfaces;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.Calendar;

/**
 * 避免在800毫秒秒内出发多次点击
 * 使用Adapter中，所有Item不能频繁点击
 */
public abstract class PerfectClickViewListener implements OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 200;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onClickView(v);
        }
    }

    protected abstract void onClickView(View view);
}
