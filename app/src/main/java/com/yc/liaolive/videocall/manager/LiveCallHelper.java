package com.yc.liaolive.videocall.manager;

import com.yc.liaolive.videocall.bean.CallExtraInfo;

/**
 * TinyHung@Outlook.com
 * 2019/3/7
 */

public class LiveCallHelper {

    private static LiveCallHelper mInstance;
    private CallExtraInfo mCallExtraInfo;

    /**
     * 单例初始化
     *
     * @return
     */
    public static synchronized LiveCallHelper getInstance() {
        synchronized (LiveCallHelper.class) {
            if (null == mInstance) {
                mInstance = new LiveCallHelper();
            }
        }
        return mInstance;
    }

    public void setCallData(CallExtraInfo callExtraInfo) {
        this.mCallExtraInfo=callExtraInfo;
    }

    public CallExtraInfo getCallData() {
        return mCallExtraInfo;
    }

    public void onReset(){
        mCallExtraInfo=null;
    }
}
