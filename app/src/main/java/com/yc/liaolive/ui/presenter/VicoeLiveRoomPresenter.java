package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.RoomInitData;
import com.yc.liaolive.ui.contract.VicoeLiveRoomContract;
import com.yc.liaolive.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/6/6
 * 语音直播间
 */
public class VicoeLiveRoomPresenter extends RxBasePresenter<VicoeLiveRoomContract.View> implements VicoeLiveRoomContract.Presenter<VicoeLiveRoomContract.View> {

    /**
     * 获取房间观众列表
     * @param page
     * @param roomID
     */
    @Override
    public void getAudienceList(int page,String roomID) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_AUDIENCE_LIST());
        params.put("page", String.valueOf(page));
        params.put("room_id", roomID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_AUDIENCE_LIST(),
                new TypeToken<ResultInfo<ResultList<UserInfo>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<ResultList<UserInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showAudienceError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<UserInfo>> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            if(null!=mView) mView.showAudienceList(data.getData().getList());
                        }else if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()<=0){
                            if(null!=mView) mView.showAudienceEmpty();
                        }else{
                            if(null!=mView) mView.showAudienceError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showAudienceError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showAudienceError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 关注用户
     * @param userID 自己的ID
     * @param friendUserID 好友的ID
     * @param status  1：关注、0：取关
     */
    @Override
    public void followUser(String userID,String friendUserID,int status) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_FOLLOW_USER());
        params.put("userid", String.valueOf(userID));
        params.put("attentid", friendUserID);
        params.put("op", String.valueOf(status));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_FOLLOW_USER(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showFollowUserError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()){
                            try {
                                JSONObject jsonObject = data.getData();
                                if(null!=jsonObject && jsonObject.length()>0){
                                    if(null!=mView) mView.showIsFollow(jsonObject.getInt("is_attent"));
                                }else{
                                    //关注、取关成功
                                    if(null!=mView) mView.showFollowUserResult(data.getData().toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if(null!=mView) mView.showFollowUserResult(data.getData().toString());
                            }
                        }else{
                            if(null!=mView) mView.showFollowUserResult(data.getData().toString());
                        }
                    }else{
                        if(null!=mView) mView.showFollowUserError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showFollowUserError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    @Override
    public void init(String anchor,String roomID) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_INIT());
        params.put("to_userid", anchor);
        params.put("room_id", roomID);

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_INIT(),
                new TypeToken<ResultInfo<RoomInitData>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RoomInitData>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showInitResultError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<RoomInitData> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getInfo()){
                        }else{
                            if(null!=mView) mView.showInitResultError(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showInitResultError(data.getCode(),NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showInitResultError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
