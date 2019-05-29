package com.yc.liaolive.user.manager;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.user.IView.SettingContract;
import com.yc.liaolive.user.model.SettingEngine;
import com.yc.liaolive.user.model.bean.SettingActivityMenuBean;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2018/7/8 17:13.
 */
public class SettingPresenter extends RxBasePresenter<SettingContract.View> implements SettingContract.Presenter<SettingContract.View> {

    private SettingEngine mEngine;

    public SettingPresenter(Activity activity) {
        mEngine = new SettingEngine(activity);
    }

    @Override
    public void setVideoExpire(String chat_deplete, String time) {
        Subscription subscription = mEngine.setVideoExpire(
                UserManager.getInstance().getUserId(), chat_deplete, time)
                .subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<String> stringResultInfo) {
                if (stringResultInfo!=null&&stringResultInfo.getCode()== HttpConfig.STATUS_OK){
                    mView.showResult(stringResultInfo.getData());
                }
            }
        });
        addSubscrebe(subscription);
    }

    @Override public void getActivityMenu() {
        Subscription subscription = mEngine.getActivityMenu()
                .subscribe(new Subscriber<ResultInfo<SettingActivityMenuBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResultInfo<SettingActivityMenuBean> resultInfo) {
                        if (resultInfo != null &&
                                resultInfo.getCode() == HttpConfig.STATUS_OK){
                            mView.setActivityMenu(resultInfo.getData());
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
