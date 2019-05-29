package com.yc.liaolive.live.ui.presenter;

import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.domain.ResultInfo;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.ResultList;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.engine.HttpCoreEngin;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.GiveGiftResultInfo;
import com.yc.liaolive.live.manager.GiftManager;
import com.yc.liaolive.live.ui.contract.LiveGiftContact;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import java.util.Map;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2018/5/15
 * 礼物模块
 * 礼物素材的获取
 * 礼物赠送逻辑
 */

public class LiveGiftPresenter extends RxBasePresenter<LiveGiftContact.View> implements LiveGiftContact.Presenter<LiveGiftContact.View> {

    /**
     * 获取礼物列表
     */
    @Override
    public void getGiftsForType(final String type, final int sourceApiType) {
        if(isLoading) return;
        isLoading=true;
        Map<String, String> params = getDefaultPrames(NetContants.getInstance().URL_ROOM_GIFT());
        params.put("gift_type",type);
        params.put("scene_api_type",String.valueOf(sourceApiType));
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(NetContants.getInstance().URL_ROOM_GIFT() , new TypeToken<ResultInfo<ResultList<GiftInfo>>>() {}.getType(), params,getHeaders(), isRsa, isZip, isEncryptResponse).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<ResultList<GiftInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isLoading=false;
                if(null!=mView) mView.showGiftError(-1, NetContants.NET_REQUST_ERROR);
            }

            @Override
            public void onNext(ResultInfo<ResultList<GiftInfo>> data) {
                isLoading=false;
                if(null!=data){
                    if(NetContants.API_RESULT_CODE==data.getCode()){
                        if(null!=data.getData()&&null!=data.getData().getList()&&data.getData().getList().size()>0){
                            ApplicationManager.getInstance().putGiftCache(type,data.getData().getList(),sourceApiType);
                            if(null!=mView) mView.showGifts(data.getData().getList(),type);
                        }else if(null!=data.getData()&&null==data.getData().getList()||data.getData().getList().size()<=0){
                            ApplicationManager.getInstance().putGiftCache(type,null,sourceApiType);
                            if(null!=mView) mView.showGiftEmpty(type);
                        }else{
                            if(null!=mView) mView.showGiftError(data.getCode(),data.getMsg());
                        }
                    }else{
                        if(null!=mView) mView.showGiftError(data.getCode(), NetContants.getErrorMsg(data));
                    }
                }else{
                    if(null!=mView) mView.showGiftError(data.getCode(), NetContants.NET_REQUST_ERROR);
                }
            }
        });
        addSubscrebe(subscription);
    }

    /**
     * 根据场景获取对应API
     * @param sceneType
     */
    private String getApi(int sceneType){
        switch (sceneType) {
            //直播间
            case LiveGiftDialog.GIFT_MODE_ROOM:
                return NetContants.getInstance().URL_GIFT_GIVI();
            //视频通话
            case LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM:
                return NetContants.getInstance().URL_GIFT_PRIVATE_GIFT();
            //私聊
            case LiveGiftDialog.GIFT_MODE_PRIVATE_CHAT:
            //用户中心
            case LiveGiftDialog.GIFT_MODE_PRIVATE_USER:
                return NetContants.getInstance().URL_GIFT_CHAT_GIFT();
            //自己对自己
            case LiveGiftDialog.GIFT_MODE_ONESELF:
                return NetContants.getInstance().URL_GIFT_SELF_GIFT();
            //多媒体预览
            case LiveGiftDialog.GIFT_MODE_PRIVATE_MEDIA:
                return NetContants.getInstance().URL_GIFT_MEDIA();

        }
        return NetContants.getInstance().URL_GIFT_GIVI();
    }

    /**
     * 礼物交易
     * @param giftInfo 礼物信息
     * @param acceptUserID 接收人
     * @param giftID 礼物ID
     * @param giftCount 礼物数量
     * @param roomID 房号 为0，私对私模式
     * @param isDoubleClick 是否连击
     * @param giftMode API 场景
     */
    @Override
    public void givePresentGift(final GiftInfo giftInfo, String acceptUserID, String giftID, final int giftCount, String roomID, final boolean isDoubleClick, int giftMode) {

        String api = getApi(giftMode);
        Map<String, String> params = getDefaultPrames(api);
        params.put("userid", UserManager.getInstance().getUserId());
        params.put("acceptid",acceptUserID);
        params.put("gift_id",giftID);
        params.put("count",String.valueOf(giftCount));
        //小视频
        if(giftMode==LiveGiftDialog.GIFT_MODE_PRIVATE_MEDIA){
            if(!TextUtils.isEmpty(roomID)) params.put("file_id",roomID);
            //其他
        }else{
            if(!TextUtils.isEmpty(roomID)) params.put("room_id",roomID);
        }
        Subscription subscription = HttpCoreEngin.get(mContext).rxpost(api,
                new TypeToken<ResultInfo<GiveGiftResultInfo>>() {}.getType(),
                params, getHeaders(),isRsa, isZip, isEncryptResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultInfo<GiveGiftResultInfo>>() {
                    @Override
                    public void call(ResultInfo<GiveGiftResultInfo> data) {
                        if (null != data){
                            if (NetContants.API_RESULT_CODE == data.getCode()){
                                if (null != data.getData()){
                                    if (null != mView) mView.showGivePresentSuccess(giftInfo,giftCount,data.getData(),isDoubleClick);
                                } else {
                                    if(null != mView) mView.showGivePresentError(-1,NetContants.NET_REQUST_JSON_ERROR);
                                }
                            //积分不够
                            } else if(NetContants.API_RESULT_ARREARAGE_CODE == data.getCode()) {
                                if (null != mView) mView.onRecharge();
                            //白名单用户今日钻石不足
                            } else if (NetContants.API_RESULT_WHITE_ARREARAGE_CODE == data.getCode()){
                                if (null != mView) mView.showGivePresentError(data.getCode(), data.getMsg());
                                GiftManager.getInstance().setShowWhiteTips(data.getMsg());
                            } else{
                                if (null != mView) mView.showGivePresentError(data.getCode(),NetContants.getErrorMsg(data));
                            }
                        } else {
                            if (null != mView) mView.showGivePresentError(-1, NetContants.NET_REQUST_ERROR);
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
