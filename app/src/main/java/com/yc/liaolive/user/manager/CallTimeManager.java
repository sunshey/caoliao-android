package com.yc.liaolive.user.manager;

import com.yc.liaolive.util.CLEventBus;

/**
 * 在线用户列表 呼叫用户间隔时间倒计时管理
 * Created by yangxueqin on 18/12/15.
 */
public class CallTimeManager {
    private CLEventBus mEventBus;

    private static CallTimeManager manager;

    public CLEventBus getmEventBus() {
        return mEventBus;
    }

    public static CallTimeManager getInstance() {
        if (manager == null) {
            manager = new CallTimeManager();
        }

        return manager;
    }

    public CallTimeManager() {
        mEventBus = new CLEventBus();
    }
}
