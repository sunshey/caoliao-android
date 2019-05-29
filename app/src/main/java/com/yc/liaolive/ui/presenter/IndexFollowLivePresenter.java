package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.FollowOnlineInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.ui.contract.IndexFollowLiveContract;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 直播间 关注
 */
public class IndexFollowLivePresenter extends RxBasePresenter<IndexFollowLiveContract.View> implements IndexFollowLiveContract.Presenter<IndexFollowLiveContract.View> {

    /**
     * 获取已关注的用户中正在直播的列表
     * @param type 类别
     * @param last_userid 从此用户开始下面的用户
     */
    @Override
    public void getRoomsByFollowUser(String type, final String last_userid) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FOLLOW_ONLINES());
        params.put("last_userid",last_userid);
        params.put("page_size", "20");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FOLLOW_ONLINES(),
                new TypeToken<ResultInfo<FollowOnlineInfo>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<FollowOnlineInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<FollowOnlineInfo> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showLiveRooms(data.getData().getList(),data.getData().getType());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showLiveRoomEmpty();
                        }else{
                            if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showLiveRoomError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showLiveRoomError(-1,  NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
