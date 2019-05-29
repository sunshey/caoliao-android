package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.bean.TempPusherUrlInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.contract.IndexLiveContract;
import com.yc.liaolive.util.Logger;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/23 10:53
 * 首页-直播间、附近的人、我的关注
 */
public class IndexLivePresenter extends RxBasePresenter<IndexLiveContract.View> implements IndexLiveContract.Presenter<IndexLiveContract.View> {

    private static final String TAG = "LiveIndexPresenter";

    private boolean isGetHot=false;
    private boolean isLiveRoom=false;
    private boolean isNearbyUser=false;

    public boolean isGetHot() {
        return isGetHot;
    }


    public boolean isLiveRoom() {
        return isLiveRoom;
    }


    public boolean isNearbyUser() {
        return isNearbyUser;
    }


    /**
     * 生成一个临时的推流地址
     */
    @Override
    public void createPublisgUrl(){
        if(isLoading) return;
        isLoading=true;
        Subscription subscribe = HttpCoreEngin.get(mContext).rxget(LiveConstant.TEMP_PUSH_URL ,
                new TypeToken<TempPusherUrlInfo>(){}.getType(), null,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TempPusherUrlInfo>() {
            @Override
            public void call(TempPusherUrlInfo data) {
                isLoading=false;
                if(null!=mView){
                    if(null!=data){

                    }else{

                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取正在直播的列表
     * @param type 类别
     * @param page 页眉
     */
    @Override
    public void getLiveRooms(String type, final int page) {
        if(isLiveRoom) return;
        isLiveRoom=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_LIST());
        params.put("page",String.valueOf(page));
        params.put("page_size", "20");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_LIST(),
                new TypeToken<ResultInfo<ResultList<RoomList>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<RoomList>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_REFRESH_FINLISH);
                isLiveRoom=false;
                if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<RoomList>> data) {
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_REFRESH_FINLISH);
                isLiveRoom=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showLiveRooms(data.getData().getList());
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

    /**
     * 获取推荐直播间列表 首页的
     * @param type
     */
    @Override
    public void getRecommendRooms(String type) {
        if(isGetHot) return;
        isGetHot=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_RECOMMEND());
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_RECOMMEND(),
                new TypeToken<ResultInfo<ResultList<RoomList>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<RoomList>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                isGetHot=false;
                if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<RoomList>> data) {
                isGetHot=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showLiveRooms(data.getData().getList());
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


    /**
     * 获取热门直播间列表
     * @param type
     * @param lastUserID
     * @param lastUserID isNotify 请求完成后是否发送通知给首页
     */
    @Override
    public void getHotRooms(String type, String lastUserID, final boolean isNotifyRefresh) {
        if(isGetHot) return;
        isGetHot=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_HOT());
        params.put("last_userid",lastUserID);
        params.put("page_size", "20");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_HOT(),
                new TypeToken<ResultInfo<ResultList<RoomList>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<RoomList>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(isNotifyRefresh) ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_REFRESH_FINLISH);
                isGetHot=false;
                if(null!=mView) mView.showLiveRoomError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<RoomList>> data) {
                if(isNotifyRefresh) ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_REFRESH_FINLISH);
                isGetHot=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showLiveRooms(data.getData().getList());
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


    /**
     * 获取附近的用户列表
     * @param latitude 纬度
     * @param longitude 经度
     */
    @Override
    public void getNearbyUsers(double latitude, double longitude, int page, final boolean isNotifyRefresh) {
        if(isNearbyUser) return;
        isNearbyUser=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_USER_NEARBY_LIST());
        params.put("latitude",String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.put("radius", "5000000");//半径
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_USER_NEARBY_LIST(),
                new TypeToken<ResultInfo<ResultList<UserInfo>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<UserInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(isNotifyRefresh) ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_REFRESH_FINLISH);
                isNearbyUser=false;
                if(null!=mView) mView.showNearbyError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<UserInfo>> data) {
                isNearbyUser=false;
                if(isNotifyRefresh) ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_REFRESH_FINLISH);
                if(null!=mView){
                    if(null!=data){
                        if(NetContants.API_RESULT_CODE==data.getCode()){
                            if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                                mView.showNearbyUsers(data.getData().getList());
                            }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                                mView.showNearbyEmpty();
                            }else{
                                mView.showNearbyError(data.getCode(),data.getMsg());
                            }
                        }else{
                            mView.showNearbyError(data.getCode(), NetContants.getErrorMsg(data));
                        }
                    }else{
                        mView.showNearbyError(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
