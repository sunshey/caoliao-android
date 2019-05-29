
package com.yc.liaolive.ui.contract;
import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.UserInfo;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 直播间业务
 */

public interface VicoeLiveRoomContract {

    interface View extends BaseContract.BaseView {
        void showAudienceList(List<UserInfo> data);
        void showAudienceEmpty();
        void showAudienceError(int code, String errorMsg);
        //关注用户结果
        void showFollowUserResult(String data);
        void showFollowUserError(int code,String data);
        //是否关注用户
        void showIsFollow(int status);
        void showIsFollowError(int code,String data);
        void showInitResultError(int code,String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取观众列表
        void getAudienceList(int page,String roomID);
        //关注
        void followUser(String userID,String friendUserID,int status);
        //初始化
        void init(String anchor,String roomID);
    }
}
