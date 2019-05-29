package com.yc.liaolive.ui.presenter;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.HelpInfo;
import com.yc.liaolive.engine.HelpDetailEngine;
import com.yc.liaolive.ui.contract.HelpDetailContract;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2018/7/8 16:34.
 */
public class HelpDetailPresenter extends RxBasePresenter<HelpDetailContract.View> implements HelpDetailContract.Presenter<HelpDetailContract.View> {
    private HelpDetailEngine mEngine;

    public HelpDetailPresenter(Activity activity) {
        mEngine = new HelpDetailEngine(activity);
    }


    @Override
    public void getHelpInfoDetail(final String helpid) {
        Subscription subscription = mEngine.getHelpInfoDetail(helpid).subscribe(new Subscriber<ResultInfo<HelpInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showNoNet();
            }

            @Override
            public void onNext(ResultInfo<HelpInfo> helpInfoResultInfo) {
                if (helpInfoResultInfo != null) {
                    if (helpInfoResultInfo.getCode() == HttpConfig.STATUS_OK && helpInfoResultInfo.getData() != null) {
                        mView.showHelpDetail(helpInfoResultInfo.getData());
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
