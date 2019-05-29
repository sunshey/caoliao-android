package com.yc.liaolive.live.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.VideoChatInfo;
import com.yc.liaolive.live.constants.IVideoPlayerView;
import com.yc.liaolive.util.ToastUtils;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by yangxueqin on 2018/12/6.
 * 1v1列表图片点击进入主播小视频
 */

public class VideoPlayerPresenter extends RxBasePresenter<IVideoPlayerView> implements BaseContract.BasePresenter<IVideoPlayerView> {


    public void getVideoData(String to_userid) {
        if(isLoading()) return;
        isLoading = true;

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_VIDEOCHAT_INFO());
        params.put("equipment", GoagalInfo.get().uuid);
        params.put("to_userid", to_userid);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_VIDEOCHAT_INFO(),
                new TypeToken<ResultInfo<VideoChatInfo>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<VideoChatInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading = false;
                ToastUtils.showToast(NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<VideoChatInfo> data) {
                isLoading=false;
                if(null != data){
                    VideoChatInfo info = data.getData();
                    if(NetContants.API_RESULT_CODE == data.getCode() && info != null){
                        mView.setVideoData(info);
                    }else{
                        ToastUtils.showToast("获取通话价格失败" + data.getMsg());
                    }
                } else {
                    ToastUtils.showToast("获取通话价格失败");
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
