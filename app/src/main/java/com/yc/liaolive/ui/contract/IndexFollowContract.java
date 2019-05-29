
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.index.model.bean.OneListBean;

/**
 * TinyHung@outlook.com
 * 2018/8/18
 * 主页-关注
 */

public interface IndexFollowContract {

    interface View extends BaseContract.BaseView {
        void showLiveRooms(OneListBean data, int type);
        void showLiveRoomEmpty(int type);
        void showLiveRoomError(int code, String errorMsg,int type);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        /**
         *
         * @param last_userid
         * @param type type: 0：关注 1：推荐
         * @param data_type 默认-1 1.获取1对1 2.获取一对多 -1.获取全部
         */
        void getFollows(String last_userid,int type, String data_type );
    }
}
