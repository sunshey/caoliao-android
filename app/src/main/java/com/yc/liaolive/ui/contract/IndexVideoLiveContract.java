
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.live.bean.RoomList;
import java.util.List;


/**
 * @time 2018/5/24
 * @des 首页-抢聊组件
 */
public interface IndexVideoLiveContract {

    interface View extends BaseContract.BaseView {
        //在线直播间列表
        void showLiveRooms(List<RoomList> data,boolean isRelease);
        void showLiveRoomEmpty();
        void showLiveRoomError(int code,String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getPrivateVideos(boolean isRelease);
    }
}
