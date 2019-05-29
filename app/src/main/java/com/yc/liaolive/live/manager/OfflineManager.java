package com.yc.liaolive.live.manager;

import android.content.Intent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.ui.activity.OfflineTipsActivity;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * TinyHung@Outlook.com
 * 2018/11/30
 * 离线管理
 */

public class OfflineManager {

    private static OfflineManager mInstance;
    private PublishSubject<Integer> mRoomTaskSubject;

    public static synchronized OfflineManager getInstance(){
        synchronized (OfflineManager.class){
            if(null==mInstance){
                mInstance=new OfflineManager();
            }
        }
        return mInstance;
    }

    /**
     * 打开离线设置
     * @return
     */
    public Observable<Integer> startOfflineSetting(){
        return Observable.just("").concatMap(new Func1<String, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(String s) {
                mRoomTaskSubject = PublishSubject.create();
                Intent intent = new Intent(AppEngine.getApplication().getApplicationContext(), OfflineTipsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppEngine.getApplication().getApplicationContext().startActivity(intent);
                return mRoomTaskSubject;
            }
        });
    }

    public PublishSubject<Integer> getOfflineSubject() {
        if (null == mRoomTaskSubject) {
            mRoomTaskSubject = PublishSubject.create();
        }
        return mRoomTaskSubject;
    }

    public void onDestroy(){
        mRoomTaskSubject=null;mInstance=null;
    }
}
