
package com.yc.liaolive.live.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.FansInfo;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 用户信息
 */

public interface LiveUserContract {

    interface View extends BaseContract.BaseView {
        //关注用户结果
        void showFollowUserResult(String data);
        void showFollowUserError(int code, String data);
        //是否关注用户
        void showIsFollow(int status);
        void showIsFollowError(int code,String data);
        //用户资料结果
        void showUserDetsilsResult(FansInfo userInfo);
        void showUserDetsilsError(int code,String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //关注
        void followUser(String userID, String friendUserID,int status);
        //获取用户资料
        void getUserDetsils(String userID);
    }
}
