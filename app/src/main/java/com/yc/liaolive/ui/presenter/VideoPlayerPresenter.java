package com.yc.liaolive.ui.presenter;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.VideoPlayerContract;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/9/27
 * 适用于播放器内页
 * 获取多媒体文件
 */
public class VideoPlayerPresenter extends RxBasePresenter<VideoPlayerContract.View> implements VideoPlayerContract.Presenter<VideoPlayerContract.View> {

    /**
     * 获取多媒体文件
     * @param hostUrl API
     * @param homeUserID 宿主ID，为空表示从主页分类跳转而来
     * @param mediaType 文件类型 0：图片 1：视频
     * @param page 分页
     * @param source 主页的类型 0：时间排序 1：浏览排序 2：喜欢排序 3：加密多媒体文件 4：推荐的多媒体文件
     * @param fileid 需要过滤的文件ID
     */
    @Override
    public void getMedias(String hostUrl, String homeUserID, int mediaType, int page, String source,long fileid) {
        if(isLoading()) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(hostUrl);
        params.put("file_type",String.valueOf(mediaType));
        params.put("fileid",String.valueOf(fileid));
        params.put("allow_ad",String.valueOf(0));//内屏视频播放需要广告
        //主页的
        if(TextUtils.isEmpty(homeUserID)){
            params.put("source", source);
        //用户多媒体中心的
        }else{
            params.put("to_userid",homeUserID);
        }
        params.put("page",String.valueOf(page));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(hostUrl,
                new TypeToken<ResultInfo<ResultList<PrivateMedia>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<PrivateMedia>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showMediaError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<PrivateMedia>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showMedias(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showMediaEmpty();
                        }else{
                            if(null!=mView) mView.showMediaError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showMediaError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showMediaError(-1,  NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
