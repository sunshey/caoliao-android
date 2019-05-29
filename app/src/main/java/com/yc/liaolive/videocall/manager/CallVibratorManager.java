package com.yc.liaolive.videocall.manager;

import android.os.Vibrator;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/12/25
 * 震动
 */

public class CallVibratorManager {

    private static final String TAG = "CallVibratorManager";
    private static CallVibratorManager mInstance;
    private Vibrator mVibrator;

    public static synchronized CallVibratorManager getInstance(){
        synchronized (CallVibratorManager.class){
            if(null==mInstance){
                mInstance=new CallVibratorManager();
            }
        }
        return mInstance;
    }

    public void onStart(){
        onStop();
        mVibrator = (Vibrator) AppEngine.getApplication().getSystemService(android.content.Context.VIBRATOR_SERVICE);
        long[] patter = {2000,1000};
        mVibrator.vibrate(patter, 0);
    }

    public void onStop(){
        if(null!=mVibrator){
            mVibrator.cancel();
            mVibrator=null;
        }
    }
}
