
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.FollowVideoList;


/**
 * @time 2017/5/23 10:50
 * @des 获取关注的列表
 */
public interface FollowContract {

    interface View extends BaseContract.BaseView {
        void showloadFollowVideoList(FollowVideoList data);
        void showloadFollowListEmptry(String response);//单独专用于加载关注列表为空的情况下
        void showloadFollowListError();//加载关注列表失败
        void showHotVideoList(FollowVideoList data);
        void showHotVideoListEmpty(String data);
        void showHotVideoListError(String data);
        void showReportUserResult(String data);
        void showReportVideoResult(String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getFollowVideoList(String uid, String page, String pageSize);
        void getHotVideoList(String page, String uid);
        void onReportUser(String userID, String accuseUserId);
        void onReportVideo(String userID, String videoID);
    }
}
