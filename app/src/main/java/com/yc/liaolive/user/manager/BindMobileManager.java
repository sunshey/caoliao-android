package com.yc.liaolive.user.manager;

import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.common.ControllerConstant;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * 跳转绑定手机
 * Created by yangxueqin on 2018/11/12.
 */

public class BindMobileManager {

    private static BindMobileManager mSingleton;

    private PublishSubject<Boolean> mBindSubject;

    public static BindMobileManager getInstance() {
        if (null == mSingleton) {
            mSingleton = new BindMobileManager();
        }
        return  mSingleton;
    }

    /**
     * 跳转绑定手机
     * @param cancelable 是否可以点击返回取消
     * @return
     */
    public Observable<Boolean> goBingMobile (final boolean cancelable) {
        return Observable.just("").concatMap(new Func1<String, Observable<? extends Boolean>>() {
            @Override
            public Observable<? extends Boolean> call(String s) {
                mBindSubject = PublishSubject.create();
                CaoliaoController.startActivity(ControllerConstant.BindPhoneTaskActivity);
                return mBindSubject;
            }
        });
    }

    public PublishSubject<Boolean> getBindSubject() {
        if (null == mBindSubject) {
            mBindSubject = PublishSubject.create();
        }
        return mBindSubject;
    }
}