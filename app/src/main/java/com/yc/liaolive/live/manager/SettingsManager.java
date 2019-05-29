package com.yc.liaolive.live.manager;

import android.content.Intent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.user.ui.SettingActivity;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * TinyHung@Outlook.com
 * 2018/11/30
 * 设置中心
 */

public class SettingsManager {

    private static SettingsManager mInstance;
    private PublishSubject<Integer> mRoomTaskSubject;

    public static synchronized SettingsManager getInstance(){
        synchronized (SettingsManager.class){
            if(null==mInstance){
                mInstance=new SettingsManager();
            }
        }
        return mInstance;
    }

    /**
     * 打开设置中心
     * @return
     */
    public Observable<Integer> startSetting(){
        return Observable.just("").concatMap(new Func1<String, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(String s) {
                mRoomTaskSubject = PublishSubject.create();
                Intent intent = new Intent(AppEngine.getApplication().getApplicationContext(), SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppEngine.getApplication().getApplicationContext().startActivity(intent);
                return mRoomTaskSubject;
            }
        });
    }


    public PublishSubject<Integer> getSettingSubject() {
        if (null == mRoomTaskSubject) {
            mRoomTaskSubject = PublishSubject.create();
        }
        return mRoomTaskSubject;
    }

    public void onDestroy(){
        mRoomTaskSubject=null;mInstance=null;
    }
}
