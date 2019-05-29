package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.VideoActionContract;

import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * TinyHung@outlook.com
 * 2017/9/27
 * 视频点赞、分享上报
 */

public class VideoActionPresenter extends RxBasePresenter<VideoActionContract.View> implements VideoActionContract.Presenter<VideoActionContract.View> {

    private boolean isLoveing;

    public boolean isLoveing() {
        return isLoveing;
    }

    /**
     * 点赞和分享
     * @param privateMedia
     * @param actionType
     */
    @Override
    public void videoLoveShare(final PrivateMedia privateMedia, final int actionType) {
        if(null==privateMedia) return;
        if(isLoveing) return;
        isLoveing=true;
        if(null==privateMedia) return;

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_LOVE());
        params.put("file_id",String.valueOf(privateMedia.getId()));
        params.put("type",String.valueOf(actionType));

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_LOVE(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoveing=false;
                if(null!=mView) mView.showActionError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                isLoveing=false;
                if (null != mView) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            //点赞
                            if(0==actionType){
                                privateMedia.love_number++;
                                privateMedia.setIs_love(1);//标记为已经点赞了
                                //分享
                            }else{
                                privateMedia.share_number++;
                            }
                            mView.showActionResul(privateMedia,actionType);
                        }else{
                            mView.showActionError(data.getCode(), data.getMsg());
                        }
                    } else {
                        mView.showActionError(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 获取用户下面的多媒体文件
     * @param hostUrl
     * @param homeUserID
     * @param mediaType
     * @param page
     * @param source
     * @param fileid 需要过滤的文件ID
     */
    @Override
    public void getMedias(String hostUrl, String homeUserID, int mediaType, int page, int source, long fileid) {
        if(isLoading()) return;
        isLoading=true;

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FILE_LIST());
        params.put("file_type",String.valueOf(mediaType));
        params.put("page",String.valueOf(page));
        params.put("to_userid",homeUserID);
        params.put("fileid",String.valueOf(fileid));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FILE_LIST(),
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
