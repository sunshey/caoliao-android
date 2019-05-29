package com.yc.liaolive.live.manager;

import android.content.Context;

import com.yc.liaolive.live.ui.activity.RoomTaskActivity;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * TinyHung@Outlook.com
 * 2018/11/29
 * 直播间任务
 */

public class RoomTaskManager {

    private static RoomTaskManager mInstance;
    private PublishSubject<Integer> mRoomTaskSubject;

    public static synchronized RoomTaskManager getInstance(){
        synchronized (RoomTaskManager.class){
            if(null==mInstance){
                mInstance=new RoomTaskManager();
            }
        }
        return mInstance;
    }

    /**
     * 打开任务面板
     * @param context
     * @return
     */
    public Observable<Integer> startTask(Context  context){
        return Observable.just("").concatMap(new Func1<String, Observable<? extends Integer>>() {
            @Override
            public Observable<? extends Integer> call(String s) {
                mRoomTaskSubject = PublishSubject.create();
                RoomTaskActivity.start(context);
                return mRoomTaskSubject;
            }
        });
    }


    public PublishSubject<Integer> getRoomTaskSubject() {
        if (null == mRoomTaskSubject) {
            mRoomTaskSubject = PublishSubject.create();
        }
        return mRoomTaskSubject;
    }

    public void onDestroy(){
        mRoomTaskSubject=null;mInstance=null;
    }
}
