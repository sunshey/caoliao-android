package com.yc.liaolive.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.util.Logger;

/**
 * YuyeTinyHung@outlook.com
 * 2017/7/16
 * 静态的监听网络变化
 */

public class NetWorkChangeReivcer extends BroadcastReceiver{

    private static final String TAG = NetWorkChangeReivcer.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_NET_WORK_CHANGED);
    }
}
