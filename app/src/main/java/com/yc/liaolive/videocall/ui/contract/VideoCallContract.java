package com.yc.liaolive.videocall.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.videocall.listsner.OnVideoCallBackListener;

/**
 * TinyHung@outlook.com
 * 2018/12/28
 * 视频通话
 */

public interface VideoCallContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        //预约主播
        void subscribeAnchor(String userID, String anchorID, OnVideoCallBackListener callBackListener);

        //检查通话权限
        void checkedMakeCallPermission(String userId, String anchorid, String reserve_id, int scene, OnVideoCallBackListener callBackListener);

        //开始通话
        void startCall(String api, String userId, String anchorid, String reserve_id, String idType, OnVideoCallBackListener callBackListener);

        //改变通话状态
        void changedCallState(String api, String userId, String anchorid, String reserve_id, String idType, OnVideoCallBackListener callBackListener);

        //购买通话时长
        void buyCallDuration(String userId, String anchorid, String reserve_id, int is_select, OnVideoCallBackListener callBackListener);

        //视频通话结束
        void endCall(String userID, String anchorid, String reserveId, int idType, OnVideoCallBackListener callBackListener);

        //查询通话时长
        void queryCallData(String roomID, OnVideoCallBackListener callBackListener);

        //上报视频通话心跳状态
        void postCallHeartState(String userId, String anchorid, int idType, OnVideoCallBackListener callBackListener);
    }
}
