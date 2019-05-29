package com.yc.liaolive.media.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.media.ui.contract.MediaPreviewContract;
import com.yc.liaolive.util.Logger;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/11/20
 * 多媒体预览
 */

public class MediaPreviewPresenter extends RxBasePresenter<MediaPreviewContract.View> implements MediaPreviewContract.Presenter<MediaPreviewContract.View> {

    /**
     * 获取多媒体文件的礼物赠送榜单
     * @param fileid
     * @param anchorid
     * @param page
     */
    @Override
    public void getMediaTop(long fileid, String anchorid, int page) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_MEDIA_GIFT_RANK());
        params.put("file_id",String.valueOf(fileid));
        params.put("anchorid", anchorid);
        params.put("page", String.valueOf(page));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_MEDIA_GIFT_RANK(),
                new TypeToken<ResultInfo<ResultList<FansInfo>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<FansInfo>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading=false;
                        if(null!=mView) mView.showMediaError(-1, NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<ResultList<FansInfo>> data) {
                        isLoading=false;
                        Logger.d(TAG,"getMediaTop-->:"+data.toString());
                        if(null!=data){
                            if(NetContants.API_RESULT_CODE == data.getCode()){
                                if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                                    if(null!=mView) mView.showMediaTopList(data.getData().getList());
                                }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                                    if(null!=mView) mView.showMediaError(-2,"数据为空");
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