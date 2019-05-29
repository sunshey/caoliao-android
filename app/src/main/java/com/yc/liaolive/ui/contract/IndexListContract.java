
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.index.model.bean.OneListBean;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 主页-直播
 */

public interface IndexListContract {

    interface View extends BaseContract.BaseView {
        //在线直播间列表
        void showLiveRooms(OneListBean data);
        void showLiveRoomEmpty();
        void showLiveRoomError(int code,String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getLiveLists(String lastUserID, String type, int page);
    }
}
