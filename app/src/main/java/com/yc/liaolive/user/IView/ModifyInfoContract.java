package com.yc.liaolive.user.IView;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.PrivateMedia;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/10/17
 * 用户资料修改
 */

public interface ModifyInfoContract {
    interface View extends BaseContract.BaseView {

        //用户头像
        void showHeadList(List<PrivateMedia> list);
        //获取头像错误
        void showHeadError(int code, String errorMsg);
        //用户视频
        void showVideoList(List<PrivateMedia> list);
        //获取视频错误
        void showVideoError(int code, String errorMsg);
        //设置封面回执
        void showSetImageFrontResult(int code, String msg);
        //设置头像回执
        void showSetUserHeadResult(int code, String msg);
        //删除头像回执
        void showDeleteHeadImageResult(PrivateMedia media,int position,int code,String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取用户头像
        void getUserHeads();
        //获取用户视频
        void getUserVideos();
        //删除头像
        void deleteHeadImage(PrivateMedia mediaInfo, int position);
        //设置封面
        void setImageFront(PrivateMedia mediaInfo, int position);
        //设置用户头像
        void setUserHead(PrivateMedia mediaInfo, int position);
    }

}
