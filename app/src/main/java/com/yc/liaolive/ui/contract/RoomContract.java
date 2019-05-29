package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.room.LiveRoom;

import java.util.List;


/**
 * TinyHung@outlook.com
 * 2018/8/5 10:53
 * 直播间相关的
 */

public interface RoomContract {

    /**
     * 获取房间信息回调
     */
    interface OnRoomCallBackListener{
        void onSuccess(Object object);
        void onFailure(int code,String errorMsg);
    }

    /**
     * 禁言用户
     */
    interface OnRoomRequstBackListener{
        void onSuccess(List<FansInfo> data);
        void onFailure(int code,String errorMsg);
    }

    interface View extends BaseContract.BaseView {

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //派发房间号
        void getRoomIDByServer(OnRoomCallBackListener callBackListener);

        //获取房间信息
        void getRoomData(String userID,OnRoomCallBackListener callBackListener);

        //查询、购买 房间
        void getQueryRoomData(String userID,int isBuy,OnRoomCallBackListener callBackListener);

        //更新房间信息至服务器
        void uploadRoom(String roomID,int state ,String roomTitle,String frontcover,double latitude,double longitude);

        //获取此群组中的禁言名单
        void getSpeechList(String roomID,OnRoomRequstBackListener callBackListener);

        //禁言/解除禁言
        void speechToUser(String toUserID, int type, OnRoomCallBackListener callBackListener);

        //进入房间
        void roomIn(String roomID,String userID,OnRoomCallBackListener callBackListener);

        //退出房间
        void roomOut(String roomID,String userID, LiveRoom.ExitRoomCallback callBackListener);

    }
}
