package com.yc.liaolive.live.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.RoomInitData;
import com.yc.liaolive.live.bean.VideoCallOlder;
import com.yc.liaolive.live.ui.contract.LiveRoomControllerContract;
import com.yc.liaolive.util.LogRecordUtils;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 直播间控制器网络数据交互
 */
public class LiveRoomControllerPresenter extends RxBasePresenter<LiveRoomControllerContract.View> implements LiveRoomControllerContract.Presenter<LiveRoomControllerContract.View> {

    /**
     * 视频通话预付费套餐
     * @param userid
     * @param acceptid
     * @param toUserChatDeplete
     * @param toUserChatMinite
     */
    @Override
    public void callSettlement(String userid, String acceptid, int toUserChatDeplete, int toUserChatMinite) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_MENUTE_SETTLE());
        params.put("userid",userid);
        params.put("acceptid", acceptid);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_MENUTE_SETTLE(),
                new TypeToken<ResultInfo<VideoCallOlder>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<VideoCallOlder>>() {
            @Override
            public void onCompleted() {
                isLoading=false;
            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showSettlementError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<VideoCallOlder> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getId()){
                            data.getData().setChat_minute(1);//默认是1分钟,测试通过后改回来
                            if(null!=mView) mView.showSettlementResult(data.getData());
                        }else{
                            if(null!=mView) mView.showSettlementError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showSettlementError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showSettlementError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 房间初始化
     * @param anchor
     * @param roomID
     */
    @Override
    public void roomInit(String anchor, String roomID) {

        Map<String, String> prames = getDefaultPrames(NetContants.getInstance().getInstance().URL_ROOM_INIT());
        prames.put("to_userid", anchor);
        prames.put("room_id", roomID);
//        prames.put("api_version", "20190110");

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_INIT(),
                new TypeToken<ResultInfo<RoomInitData>>() {}.getType(), prames, getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<RoomInitData>>() {
            @Override
            public void call(ResultInfo<RoomInitData> data) {
                if(null!=data){
                    if(null!=data.getData()){
                        if(NetContants.API_RESULT_CODE == data.getCode()){
                            if(null!=data.getData()&&null!=data.getData().getInfo()){
                                if(null!=mView) mView.showInitResult(data.getData().getInfo());
                            }else{
                                if(null!=mView) mView.showInitResultError(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            LogRecordUtils.getInstance().putLog(NetContants.getInstance().URL_ROOM_INIT(),data.getCode(),data.getMsg());
                            if(null!=mView) mView.showInitResultError(data.getCode(),NetContants.getErrorMsg(data));
                        }
                    }else{
                        //请求失败
                        if (null != data.getResponse()) {
                            LogRecordUtils.getInstance().putLog(NetContants.getInstance().URL_ROOM_INIT(), data.getResponse().code(), data.getResponse().message());
                        }
                        if (null != mView) mView.showInitResultError(-1, NetContants.NET_REQUST_ERROR);
                    }

                }else{
                    if (null != mView) mView.showInitResultError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
