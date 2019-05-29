package com.yc.liaolive.videocall.ui.presenter;

import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.videocall.bean.CallResultInfo;
import com.yc.liaolive.videocall.listsner.OnVideoCallBackListener;
import com.yc.liaolive.videocall.ui.contract.VideoCallContract;
import org.json.JSONObject;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * TinyHung@outlook.com
 * 2018/12/28
 * 视频通话
 */
public class VideoCallPresenter extends RxBasePresenter<VideoCallContract.View> implements VideoCallContract.Presenter<VideoCallContract.View> {

    /**
     * 预约主播
     * @param userID
     * @param anchorID
     * @param callBackListener
     */
    @Override
    public void subscribeAnchor(String userID, String anchorID, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_RESERVE_ANCHOR());
        params.put("userid",userID);
        params.put("anchorid",anchorID);

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_RESERVE_ANCHOR(),
                new TypeToken<ResultInfo<JSONObject>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<JSONObject>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
                    }

                    @Override
                    public void onNext(ResultInfo<JSONObject> data) {
                        if (null != callBackListener) {
                            if (null != data) {
                                if (NetContants.API_RESULT_CODE == data.getCode()) {
                                    callBackListener.onSuccess(data.getData());
                                }else{
                                    callBackListener.onFailure(data.getCode(), data.getMsg());
                                }
                            } else {
                                callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 检查拨号权限
     * @param userId
     * @param anchorid
     * @param reserve_id
     * @param callBackListener
     */
    @Override
    public void checkedMakeCallPermission(String userId, String anchorid, String reserve_id, int scene, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_CHECKED_PER());
        params.put("userid",userId);
        params.put("anchorid",anchorid);
        params.put("scene", String.valueOf(scene));
        params.put("reserve_id",TextUtils.isEmpty(reserve_id)?"":reserve_id);
//        params.put("api_version","20181221");

        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_CHECKED_PER(), new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 视频通话付费
     * @param api
     * @param userId
     * @param anchorid
     * @param reserve_id
     * @param idType
     * @param callBackListener
     */
    @Override
    public void startCall(String api, String userId, String anchorid, String reserve_id, String idType, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(api);
        params.put("userid",userId);
        params.put("anchorid",anchorid);
        params.put("reserve_id",TextUtils.isEmpty(reserve_id)?"":reserve_id);
        params.put("id_type",idType);
//        params.put("api_version","20181221");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(api, new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 改变视频通话状态
     * @param api
     * @param userId
     * @param anchorid
     * @param reserve_id
     * @param idType
     * @param callBackListener
     */
    @Override
    public void changedCallState(String api, String userId, String anchorid, String reserve_id, String idType, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(api);
        params.put("userid",userId);
        params.put("anchorid",anchorid);
        params.put("reserve_id",TextUtils.isEmpty(reserve_id)?"":reserve_id);
        params.put("id_type",idType);
//        params.put("api_version","20181221");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(api, new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 查询、购买视频通话
     * @param userId
     * @param anchorid
     * @param reserve_id
     * @param is_select
     * @param callBackListener
     */
    @Override
    public void buyCallDuration(String userId, String anchorid, String reserve_id, int is_select, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_BUILD());
        params.put("userid",userId);
        params.put("anchorid",anchorid);
        params.put("reserve_id",TextUtils.isEmpty(reserve_id)?"":reserve_id);
        params.put("is_select",String.valueOf(is_select));
//        params.put("api_version","20181221");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_BUILD(), new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 视频通话结束
     * @param userID
     * @param anchorid
     * @param reserveId
     * @param idType
     * @param callBackListener
     */
    @Override
    public void endCall(String userID, String anchorid, String reserveId, int idType, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_CALL_END());
        params.put("userid",userID);
        params.put("anchorid",anchorid);
        params.put("reserve_id",TextUtils.isEmpty(reserveId)?"":reserveId);
        params.put("id_type",String.valueOf(idType));
//        params.put("api_version","20181221");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_CALL_END(), new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 查询通话信息
     * @param roomID
     * @param callBackListener
     */
    @Override
    public void queryCallData(String roomID, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_GET_VIDEO_CALL_FREE());
        params.put("room_id",roomID);
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_GET_VIDEO_CALL_FREE(), new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 上报通话心跳
     * @param userId
     * @param anchorid
     * @param idType
     * @param callBackListener
     */
    @Override
    public void postCallHeartState(String userId, String anchorid, int idType, OnVideoCallBackListener callBackListener) {
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_POST_HEART());
        params.put("userid",userId);
        params.put("anchorid",anchorid);
        params.put("id_type",String.valueOf(idType));
//        params.put("api_version","20181221");
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_POST_HEART(), new TypeToken<ResultInfo<CallResultInfo>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<CallResultInfo>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                if(null!=callBackListener) callBackListener.onFailure(-1,NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<CallResultInfo> data) {
                if (null != callBackListener) {
                    if (null != data) {
                        if (NetContants.API_RESULT_CODE == data.getCode()) {
                            callBackListener.onSuccess(data.getData());
                        }else{
                            callBackListener.onFailure(data.getCode(), data.getMsg());
                        }
                    } else {
                        callBackListener.onFailure(-1, NetContants.NET_REQUST_ERROR);
                    }
                }
            }
        });
        addSubscrebe(subscription);
    }
}