package com.yc.liaolive.ui.presenter;

import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.live.bean.VideoCallOlder;
import com.yc.liaolive.ui.contract.LiveCallContact;
import com.yc.liaolive.util.Logger;

import org.json.JSONObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/7/14
 * 视频电话相关
 */

public class LiveCallPresenter extends RxBasePresenter<LiveCallContact.View> implements LiveCallContact.Presenter<LiveCallContact.View> {

    /**
     * 获取主播的视频通话价目
     * @param homeUserID
     */
    @Override
    public void getAnchorPackage(String homeUserID) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_CHAT_DEPLETE());
        params.put("to_userid",homeUserID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_CHAT_DEPLETE(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showAnchorPackageError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()){
                            JSONObject jsonObject = data.getData();
                            if(null != jsonObject && jsonObject.length() > 0){
                                int chat_deplete = jsonObject.optInt("chat_deplete");
                                int chat_minite = jsonObject.optInt("chat_minute");
                                if(null != mView){
                                    mView.showAnchorPackage(chat_deplete,chat_minite);
                                }
                            }else{
                                if(null != mView) mView.showAnchorPackageError(data.getCode(),data.getMsg());
                            }
                        }
                    }else{
                        if(null!=mView) mView.showAnchorPackageError(-1,data.getMsg());
                    }
                }else{
                    if(null!=mView) mView.showAnchorPackageError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }


    /**
     * 视频通话预付费套餐
     * @param userid
     * @param acceptid
     * @param toUserChatDeplete
     * @param toUserChatMinite
     */
    @Override
    public void callSettlement(String userid, String acceptid, int toUserChatDeplete, int toUserChatMinite) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_MENUTE_SETTLE());
        params.put("acceptid", acceptid);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_MENUTE_SETTLE(),
                new TypeToken<ResultInfo<VideoCallOlder>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<VideoCallOlder>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=mView) mView.showSettlementError(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<VideoCallOlder> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getId()){
                            if(null!=mView) mView.showSettlementResult(data.getData());
                        }else{
                            if(null!=mView) mView.showSettlementError(-1,NetContants.NET_REQUST_JSON_ERROR);
                        }
                    }else{
                        if(null!=mView) mView.showSettlementError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showSettlementError(-1, NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 取消视频通话套餐
     * @param olderID
     */
    @Override
    public void cancelVideoCallOlder(String olderID, final RxBasePresenter.OnResqustCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_MENUTE_UNSETTLE());
        params.put("id", olderID);
        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_MENUTE_UNSETTLE(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                if(null!=mView) mView.showCancelVideoCallOlderResult(false);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=callBackListener) callBackListener.onSuccess(data.getData().toString());
                        if(null!=mView) mView.showCancelVideoCallOlderResult(true);
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(-1,data.getMsg());
                        if(null!=mView) mView.showCancelVideoCallOlderResult(false);
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    if(null!=mView) mView.showCancelVideoCallOlderResult(false);
                }
            }
        });
        addSubscrebe(subscribe);
    }

    /**
     * 改变主播的在线状态
     * @param status
     */
    @Override
    public void updataAnchorState(int status, final RxBasePresenter.OnResqustCallBackListener callBackListener) {

        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_ANCHOR_STATE());
        params.put("state", String.valueOf(status));

        Subscription subscribe = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_ANCHOR_STATE(),
                new TypeToken<ResultInfo<JSONObject>>(){}.getType(), params,getHeaders(),isRsa,isZip,isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultInfo<JSONObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                if(null!=mView) mView.showUpdataAnchorStateResult(false);
            }

            @Override
            public void onNext(ResultInfo<JSONObject> data) {
                if(null!=data){
                    if(NetContants.API_RESULT_CODE == data.getCode()){
                        if(null!=callBackListener) callBackListener.onSuccess(data.getData().toString());
                        if(null!=mView) mView.showUpdataAnchorStateResult(true);
                    }else{
                        if(null!=callBackListener) callBackListener.onFailure(-1,data.getMsg());
                        if(null!=mView) mView.showUpdataAnchorStateResult(false);
                    }
                }else{
                    if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    if(null!=mView) mView.showUpdataAnchorStateResult(false);
                }
            }
        });
        addSubscrebe(subscribe);
    }
}
