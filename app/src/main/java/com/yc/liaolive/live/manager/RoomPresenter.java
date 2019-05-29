package com.yc.liaolive.live.manager;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.bean.RoomOutBean;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.util.Logger;

import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/5/23 10:53
 * 房间内部的业务
 */

public class RoomPresenter extends RxBasePresenter<RoomContract.View> implements RoomContract.Presenter<RoomContract.View> {

    /**
     * 派发房间号
     */
    @Override
    public void getRoomIDByServer(final RoomContract.OnRoomCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_GET_ROOMID());
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_GET_ROOMID(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if(null!=data.getData()){
                                try {
                                    JSONObject jsonObject = data.getData();
                                    if(null != data.getData() && null != jsonObject.optString("room_id")){
                                        callBackListener.onSuccess(jsonObject.optString("room_id"));
                                    }else{
                                        callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                                    }
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                    callBackListener.onFailure(data.getCode(),"解析数据失败："+NetContants.NET_REQUST_JSON_ERROR);
                                }
                            }else{
                                callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取房间信息
     * @param userID
     * @param callBackListener
     */
    @Override
    public void getRoomData(String userID, final RoomContract.OnRoomCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_INFO());
        params.put("to_user",userID);
        params.put("api_version","20190225");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_INFO(),
                new TypeToken<ResultInfo<RoomExtra>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RoomExtra>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<RoomExtra> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if(null!=data.getData()){
                                callBackListener.onSuccess(data.getData());
                            }else{
                                callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 查询购买房间信息
     * @param userID
     * @param isBuy 0:询问 1：购买
     * @param callBackListener
     */
    @Override
    public void getQueryRoomData(String userID, int isBuy,RoomContract.OnRoomCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_BUY_ROOM());
        params.put("to_user",userID);
        params.put("is_buy",String.valueOf(isBuy));
        params.put("api_version","20190225");
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_BUY_ROOM(),
                new TypeToken<ResultInfo<RoomExtra>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RoomExtra>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<RoomExtra> data) {
                        if(null!=callBackListener){
                            if(null!=data){
                                if(data.getCode()==NetContants.API_RESULT_CODE){
                                    if(null!=data.getData()){
                                        callBackListener.onSuccess(data.getData());
                                    }else{
                                        callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                                    }
                                }else{
                                    callBackListener.onFailure(data.getCode(),data.getMsg());
                                }
                            }else{
                                callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                            }
                        }
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 上传房间信息
     * @param roomID
     * @param state
     * @param roomTitle
     * @param frontcover
     * @param latitude
     * @param longitude
     */
    @Override
    public void uploadRoom(String roomID, int state,String roomTitle, String frontcover, double latitude, double longitude) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_UPLOAD_ROOM());
        params.put("room_id",roomID);
        params.put("title",roomTitle);
        params.put("state",String.valueOf(state));
        params.put("frontcover", TextUtils.isEmpty(frontcover)? Constant.DEFAULT_FRONT_COVER:frontcover);
        params.put("location","地球");
        params.put("latitude",String.valueOf(latitude));
        params.put("longitude",String.valueOf(longitude));
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_UPLOAD_ROOM(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),
                isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 获取群组中的禁言列表
     * @param roomID
     * @param callBackListener
     */
    @Override
    public void getSpeechList(String roomID, final RoomContract.OnRoomRequstBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_SPEECH_LIST());

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_SPEECH_LIST(), new TypeToken<ResultInfo<ResultList<FansInfo>>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<FansInfo>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<FansInfo>> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                                callBackListener.onSuccess(data.getData().getList());
                            }else{
                                callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 禁言、解除禁言
     * @param toUserID
     * @param type 0:解禁 1：封禁
     * @param callBackListener
     */
    @Override
    public void speechToUser(String toUserID, int type, final RoomContract.OnRoomCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_SPEECH_TOUSER());
        params.put("gag_userid",toUserID);
        params.put("type",String.valueOf(type));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_SPEECH_TOUSER(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),
                isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=callBackListener){
                    if(null!=data){
                        if(data.getCode()==NetContants.API_RESULT_CODE){
                            if(null!=data.getData()){
                                callBackListener.onSuccess(data.getData());
                            }else{
                                callBackListener.onFailure(data.getCode(),NetContants.NET_REQUST_JSON_ERROR);
                            }
                        }else{
                            callBackListener.onFailure(data.getCode(),data.getMsg());
                        }
                    }else{
                        callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 进入房间
     * @param roomID
     * @param userID
     */
    @Override
    public void roomIn(String roomID, String userID, final RoomContract.OnRoomCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().IN_ROOM());
        params.put("room_id",roomID);
        params.put("userid",userID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().IN_ROOM(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=callBackListener){
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                        } else {
                            if (null != callBackListener) callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        if (null != callBackListener) callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 退出房间
     * @param roomID
     * @param userID
     */
    @Override
    public void roomOut(String roomID, String userID, final LiveRoom.ExitRoomCallback callBackListener) {
        if(null==roomID) return;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().OUT_ROOM());
        params.put("room_id",roomID);
        params.put("userid",userID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().OUT_ROOM(),
                new TypeToken<ResultInfo<RoomOutBean>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<RoomOutBean>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<RoomOutBean> data) {
                if(null!=callBackListener){
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            if(null!=callBackListener) callBackListener.onSuccess(data.getData());
                        } else {
                            if (null != callBackListener) callBackListener.onError(data.getCode(), data.getMsg());
                        }
                    } else {
                        if (null != callBackListener) callBackListener.onError(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
