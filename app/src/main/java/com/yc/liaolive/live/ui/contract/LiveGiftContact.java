package com.yc.liaolive.live.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.GiveGiftResultInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/5/15
 * 直播房间
 */

public interface LiveGiftContact {

    interface View extends BaseContract.BaseView {
        //礼物分类
        void showGifts(List<GiftInfo> data,String type);
        void showGiftEmpty(String type);
        void showGiftError(int code,String errMsg);
        //礼物交易成功
        void showGivePresentSuccess(GiftInfo giftInfo,int giftCount,GiveGiftResultInfo data, boolean isDoubleClick);
        void showGivePresentError(int code,String data);
        //需要充值
        void onRecharge();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getGiftsForType(String type,int sourceApiType);
        void givePresentGift(GiftInfo giftInfo,String acceptUserID,String giftID,int giftCount,String roomID,boolean isDoubleClick,int sceneType);//发送人，接收人，礼物ID，房号,是否连击模式,应用场景
    }
}
