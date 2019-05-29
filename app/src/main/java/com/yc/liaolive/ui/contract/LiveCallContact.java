package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.live.bean.VideoCallOlder;

/**
 * TinyHung@outlook.com
 * 2018/5/15
 * 直播房间
 */

public interface LiveCallContact {

    interface View extends BaseContract.BaseView {
        //获取主播设定的套餐结果
        void showAnchorPackage(int chatDeplete,int chatMinite);
        void showAnchorPackageError(int code,String errorMsg);
        //结算结果
        void showSettlementResult(VideoCallOlder videoCallOlder);
        void showSettlementError(int code,String data);
        //撤销结果
        void showCancelVideoCallOlderResult(boolean isSuccess);
        //在线状态改变结果
        void showUpdataAnchorStateResult(boolean isSuccess);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取主播设定的通话套餐
        void getAnchorPackage(String homeUserID);
        //结算
        void callSettlement(String userid, String acceptid, int toUserChatDeplete, int toUserChatMinite);
        //撤销冻结的视频通话资费
        void cancelVideoCallOlder(String olderID,RxBasePresenter.OnResqustCallBackListener callBackListener);
        //改变直播的在线状态
        void updataAnchorState(int status,RxBasePresenter.OnResqustCallBackListener callBackListener);
    }
}
