package com.yc.liaolive.start.model;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.AppConfigInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.InitUtils;
import com.yc.liaolive.util.Utils;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 版本检测
 * Created by yangxueqin on 2018/11/19.
 */

public class VersionCheckData {
    /**
     * 检查版本更新
     * @param callBackListener
     * @param isAuto 是否是自动更新
     */
    public static void  checkedVerstion(int isAuto, final UserServerContract.OnNetCallBackListener callBackListener) {
        final int versionCode = Utils.getVersionCode();
        Map<String, String> params = new HashMap<>();
        AppConfigInfo configInfo= InitUtils.get().getConfigInfo(VideoApplication.getInstance().getApplicationContext());
        params.put("imeil",VideoApplication.mUuid);
        params.put("userid", UserManager.getInstance().getUserId());
        params.put("version_code",String.valueOf(versionCode));
        params.put("agent_id", ChannelUtls.getInstance().getAgentId());
        params.put("is_auto",String.valueOf(isAuto));//是否是自动检查更新 0：自动 1：手动
        if(null!=configInfo) {
            params.put("site_id",configInfo.getSite_id());
            params.put("soft_id",configInfo.getSoft_id());
        }

        HttpCoreEngin.get(AppEngine.getApplication()).rxpost(NetContants.getInstance().URL_VERSTION(),
                new TypeToken<ResultInfo<UpdataApkInfo>>() {}.getType(), params,
                RxBasePresenter.getHeaders(), RxBasePresenter.isRsa, RxBasePresenter.isZip, RxBasePresenter.isEncryptResponse)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<UpdataApkInfo>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<UpdataApkInfo> data) {
                        if (null != data) {
                            if (NetContants.API_RESULT_CODE == data.getCode()) {
                                if(null!=data.getData()&&data.getData().getVersion_code()> versionCode){
                                    if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                                }else{
                                    if(null!=callBackListener) callBackListener.onFailure(-1,data.getMsg());
                                }
                            } else {
                                if (null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                            }
                        } else {
                            if (null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                        }
                    }
                });
    }
}
