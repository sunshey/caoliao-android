package com.yc.liaolive.user.manager;

import rx.Subscriber;
import rx.Subscription;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.HelpInfoWrapper;
import com.yc.liaolive.engine.HelpEngine;
import com.yc.liaolive.user.IView.HelpContract;

/**
 * Created by wanglin  on 2018/7/8 15:35.
 */
public class HelpPresenter extends RxBasePresenter<HelpContract.View> implements HelpContract.Presenter<HelpContract.View> {

    private HelpEngine mEngine;

    public HelpPresenter(Activity activity) {
        mEngine = new HelpEngine(activity);
    }


    @Override
    public void getHelpList() {
        mView.showLoading();
        Subscription subscription = mEngine.getHelpList(UserManager.getInstance().getUserId()).subscribe(new Subscriber<ResultInfo<HelpInfoWrapper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showNoNet();
            }

            @Override
            public void onNext(ResultInfo<HelpInfoWrapper> listResultInfo) {
                if (listResultInfo != null) {
                    if (listResultInfo.getCode() == HttpConfig.STATUS_OK && listResultInfo.getData() != null && listResultInfo.getData().getList() != null) {
                        mView.showHelpList(listResultInfo.getData().getList());
                        mView.hide();
                    } else {
                        mView.showNoData();
                    }
                } else {
                    mView.showNoNet();
                }
            }
        });
        addSubscrebe(subscription);

    }
}
