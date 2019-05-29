
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.live.bean.RoomList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 主页-直播
 */

public interface IndexLiveContract {

    interface View extends BaseContract.BaseView {
        //在线直播间列表
        void showLiveRooms(List<RoomList> data);
        void showLiveRoomEmpty();
        void showLiveRoomError(int code,String errorMsg);
        //附近的人
        void showNearbyUsers(List<UserInfo> data);
        void showNearbyEmpty();
        void showNearbyError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void createPublisgUrl();
        //获取在线直播间列表
        void getLiveRooms(String type,int page);
        //获取推荐直播间列表
        void getRecommendRooms(String type);
        //获取热门直播间列表
        void getHotRooms(String type, String lastUserID,boolean isNotify);
        //获取附近的人
        void getNearbyUsers(double latitude,double longitude,int page,boolean isNotify);
    }
}
