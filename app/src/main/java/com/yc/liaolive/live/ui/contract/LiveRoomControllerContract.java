
package com.yc.liaolive.live.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.live.bean.RoomInitInfo;
import com.yc.liaolive.live.bean.VideoCallOlder;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 直播、视频通话控制器网络数据 交互
 */

public interface LiveRoomControllerContract {

    interface View extends BaseContract.BaseView {
        void showSettlementResult(VideoCallOlder data);
        void showSettlementError(int code,String data);
        //初始化
        void showInitResult(RoomInitInfo data);
        void showInitResultError(int code,String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //结算
        void callSettlement(String userid, String acceptid, int toUserChatDeplete, int toUserChatMinite);
        //房间初始化
        void roomInit(String anchor,String roomID);
    }
}
