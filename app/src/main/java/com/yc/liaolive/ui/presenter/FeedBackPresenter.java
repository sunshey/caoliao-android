package com.yc.liaolive.ui.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.engine.FeedBackEngine;
import com.yc.liaolive.ui.contract.FeedBackContract;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ToastUtils;

import org.json.JSONObject;

import java.io.File;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2018/7/8 14:48.
 */
public class FeedBackPresenter extends RxBasePresenter<FeedBackContract.View> implements FeedBackContract.Presenter<FeedBackContract.View> {

    private final FeedBackEngine mEngine;
    private LoadingProgressView progressView;
    private Activity activity;

    public FeedBackPresenter(Activity context) {
        activity = context;
        mEngine = new FeedBackEngine(context);
        progressView = new LoadingProgressView(context);

    }


    @Override
    public void feedBack(String content, File file, String contact) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showCenterToast("请输入反馈内容");
            return;
        }
        if (TextUtils.isEmpty(contact)) {
            ToastUtils.showCenterToast("请输入联系方式");
            mView.showEmptyNum();
            return;
        }
        progressView.showMessage("正在提交反馈信息，请稍候...");
        Subscription subscription = mEngine.feedBack(UserManager.getInstance().getUserId(), content, file, contact).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                progressView.dismiss();
            }

            @Override
            public void onNext(final ResultInfo<JSONObject> stringResultInfo) {
                progressView.dismiss();
                if (stringResultInfo != null) {
                    if (stringResultInfo.getCode() == HttpConfig.STATUS_OK) {
                        mView.finish();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showCenterToast("意见反馈成功");
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showCenterToast(stringResultInfo.getMsg());
                            }
                        });
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showCenterToast("意见反馈失败");
                        }
                    });
                }

            }
        });
        addSubscrebe(subscription);
    }
}
