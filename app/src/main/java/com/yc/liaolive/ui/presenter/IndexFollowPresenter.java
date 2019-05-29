package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.index.model.bean.OneListBean;
import com.yc.liaolive.ui.contract.IndexFollowContract;
import com.yc.liaolive.util.LogRecordUtils;

import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 直播间 关注
 */

public class IndexFollowPresenter extends RxBasePresenter<IndexFollowContract.View> implements IndexFollowContract.Presenter<IndexFollowContract.View> {

    /**
     * 获取用户已关注的用户列表
     * @param lastUserid
     * @param type 0：关注 1：推荐
     */
    @Override
    public void getFollows(final String lastUserid, final int type, String data_type) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FOLLOW_LIST());
        params.put("last_userid",lastUserid);
        params.put("page_size", "50");
        params.put("type", String.valueOf(type));
        params.put("data_type", data_type);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FOLLOW_LIST(),
                new TypeToken<ResultInfo<OneListBean>>() {}.getType(), params, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<OneListBean>>() {
            @Override
            public void call(ResultInfo<OneListBean> data) {
                isLoading=false;
                if (null != data) {
                    if (null != data.getData()) {
                        if(NetContants.API_RESULT_CODE == data.getCode()){
                            if(null!=mView){
                                if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                                    mView.showLiveRooms(data.getData(),type);
                                }else if(null!=data.getData() && (null == data.getData().getList()|| data.getData().getList().size()<=0)){
                                    mView.showLiveRoomEmpty(type);
                                }else{
                                    mView.showLiveRoomError(-1,NetContants.NET_REQUST_JSON_ERROR,type);
                                }
                            }
                        }else{
                            LogRecordUtils.getInstance().putLog(NetContants.getInstance().URL_FOLLOW_LIST(),data.getCode(),data.getMsg());
                            if(null!=mView) mView.showLiveRoomError(data.getCode(), NetContants.getErrorMsg(data),type);
                        }
                    } else {
                        //请求失败
                        if (null != data.getResponse()) {
                            LogRecordUtils.getInstance().putLog(NetContants.getInstance().URL_FOLLOW_LIST(), data.getResponse().code(), data.getResponse().message());
                        }
                        if (null != mView) mView.showLiveRoomError(-1, NetContants.NET_REQUST_ERROR,type);
                    }
                } else {
                    if (null != mView) mView.showLiveRoomError(-1, NetContants.NET_REQUST_ERROR,type);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
