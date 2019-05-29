
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.live.bean.RoomList;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 主页-关注
 */

public interface IndexFollowLiveContract {

    interface View extends BaseContract.BaseView {
        void showLiveRooms(List<RoomList> data, int type);
        void showLiveRoomEmpty();
        void showLiveRoomError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getRoomsByFollowUser(String type,String last_userid);
    }
}
