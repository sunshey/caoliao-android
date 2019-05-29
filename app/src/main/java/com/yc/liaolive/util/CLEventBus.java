package com.yc.liaolive.util;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

/**
 * 消息转发管理器
 * Created by yangxueqin on 2018/12/15.
 */
public class CLEventBus {

    private final String TAG = "CLEventBus";

    private ArrayMap<Class<?>, Subject> mSubjects;

    /**
     * 推出时调用
     */
    public void destory() {
        mSubjects.clear();
    }

    public CLEventBus() {
        mSubjects = new ArrayMap<Class<?>, Subject>();
    }

    /**
     * 注册监听
     *
     * @param type 注册监听的类型
     * @return
     */
    public <T> Observable<T> registerType(Class<T> type) {

        Subject<T, T> subject;
        if (mSubjects.containsKey(type)) {
            subject = mSubjects.get(type);
        } else {
            subject = PublishSubject.create();
            mSubjects.put(type, subject);
        }

        return subject.subscribeOn(Schedulers.io());
    }

    /**
     *  注销类型
     *
     * @param type
     * @param <T>
     */
    public <T> void unRrgisterType(Class<T> type) {
        if (mSubjects.containsKey(type)) {
            mSubjects.remove(type);
        }
    }

    /**
     * post 一个事件
     *
     * @param object
     */
    public <T> void post(@NonNull Class<T> type, @NonNull T object) {
        if (mSubjects.isEmpty()) {
            return;
        }
        for (Class<?> key : mSubjects.keySet()) {
            if (key.getName().equals(type.getName())) {
                Subject subject = mSubjects.get(key);
                subject.onNext(object);
                //JPLog.d(TAG, "post " + key);
                break;
            }
        }

    }

}
