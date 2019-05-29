package com.yc.liaolive.ui.presenter;

import android.app.Activity;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.BlackListWrapper;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.engine.BlackListEngine;
import com.yc.liaolive.ui.contract.BlackListContract;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.user.manager.UserManager;

import org.json.JSONObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2018/7/10 21:37.
 */
public class BlackListPresenter extends RxBasePresenter<BlackListContract.View> implements BlackListContract.Presenter<BlackListContract.View> {

    private final BlackListEngine mEngine;
    private LoadingProgressView progressView;

    public BlackListPresenter(Activity activity) {
        mEngine = new BlackListEngine(activity);
        progressView = new LoadingProgressView(activity);
    }

    @Override
    public void getBlackList(final int page, int page_size) {
        if (page == 1) {

            mView.showLoading();
        }
        Subscription subscription = mEngine.getBlackList(UserManager.getInstance().getUserId(), page, page_size).subscribe(new Subscriber<ResultInfo<BlackListWrapper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (page == 1) {
                    mView.showNoNet();
                }
            }

            @Override
            public void onNext(ResultInfo<BlackListWrapper> blackListWrapperResultInfo) {
                if (blackListWrapperResultInfo != null) {
                    if (blackListWrapperResultInfo.getCode() == HttpConfig.STATUS_OK && blackListWrapperResultInfo.getData() != null
                            && blackListWrapperResultInfo.getData().getList() != null && blackListWrapperResultInfo.getData().getList().size() > 0) {
                        mView.showBlackList(blackListWrapperResultInfo.getData().getList());
                        mView.hide();
                    } else {
                        if (page == 1) {
                            mView.showNoData();
                        }
                    }
                } else {
                    if (page == 1) {
                        mView.showNoNet();
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    @Override
    public void removeBlackList(final FansInfo fansInfo) {
        if (progressView != null) {
            progressView.showMessage("正在将" + fansInfo.getNickname() + "移除黑名单，请稍候...");
        }

        Subscription subscription = mEngine.removeBlackList(UserManager.getInstance().getUserId(), fansInfo.getBlack_userid()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
                progressView.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                progressView.dismiss();
            }

            @Override
            public void onNext(ResultInfo<JSONObject> stringResultInfo) {
                if (stringResultInfo != null && stringResultInfo.getCode() == HttpConfig.STATUS_OK) {
                    mView.showRemoveResult(fansInfo);
                }
            }
        });
        addSubscrebe(subscription);
    }
}
