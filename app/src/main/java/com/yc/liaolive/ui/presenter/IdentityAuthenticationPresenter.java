package com.yc.liaolive.ui.presenter;

import android.app.Activity;
import android.content.Context;

import com.kaikai.securityhttp.domain.ResultInfo;
import com.kaikai.securityhttp.net.contains.HttpConfig;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.UploadInfo;
import com.yc.liaolive.engine.IdentityAuthenticationEngine;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.contract.IdentityAuthenticationContract;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ToastUtils;

import org.json.JSONObject;

import java.io.File;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2018/7/8 11:09.
 */
public class IdentityAuthenticationPresenter extends RxBasePresenter<IdentityAuthenticationContract.View> implements IdentityAuthenticationContract.Presenter<IdentityAuthenticationContract.View> {

    private final IdentityAuthenticationEngine mEngine;
    private LoadingProgressView progressView;
    private Activity activity;

    public IdentityAuthenticationPresenter(Context context) {
        mEngine = new IdentityAuthenticationEngine(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        progressView = new LoadingProgressView(activity);
    }

    /**
     * @param file
     * @param flag 0正面 1 反面
     */
    @Override
    public void upload(File file, final int flag) {
        Subscription subscription = mEngine.upload(UserManager.getInstance().getUserId(), file).subscribe(new Subscriber<ResultInfo<UploadInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResultInfo<UploadInfo> stringResultInfo) {
                if (stringResultInfo != null) {
                    if (stringResultInfo.getCode() == HttpConfig.STATUS_OK && stringResultInfo.getData() != null) {
                        mView.showUploadResult(stringResultInfo.getData().getUrl(), flag);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    @Override
    public void identityAuthentication(String name, String id_number, String expiration_date, String card_front, String card_back) {
        if(isLoading()) return;
        isLoading=true;
        progressView.showMessage("正在上传信息...");
        Subscription subscription = mEngine.identityAuthentication(UserManager.getInstance().getUserId(),
                name, id_number, expiration_date, card_front, card_back)
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
                if(null!=progressView) progressView.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=activity){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showCenterToast("提交失败");
                        }
                    });
                }
            }

            @Override
            public void onNext(final ResultInfo<JSONObject> stringResultInfo) {
                isLoading=false;
                if (stringResultInfo != null) {
                    if (stringResultInfo.getCode() == HttpConfig.STATUS_OK && stringResultInfo.getData() != null) {
                        UserManager.getInstance().setIdentity_audit(1);
                        ApplicationManager.getInstance().observerUpdata("authentication");
                        if(null!=activity) activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showCenterToast("上传成功，请等待验证结果");
                            }
                        });
                        mView.finish();
                    } else {
                        if(null!=activity) activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showCenterToast(stringResultInfo.getMsg());
                            }
                        });
                    }
                }else{
                    if(null!=activity) activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showCenterToast("提交失败");
                        }
                    });
                }
            }
        });
        addSubscrebe(subscription);
    }
}
