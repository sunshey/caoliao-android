
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.PrivateMedia;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/9/14
 * 私密，相册、视频
 */

public interface PrivateMediaContract {

    interface View extends BaseContract.BaseView {
        //多媒体文件列表
        void showPrivateMedias(List<PrivateMedia> data);
        void showPrivateMediaEmpty();
        void showPrivateMediaError(int code,String errorMsg);
        //修改访问权限回执
        void showModifyMediaFilePermissionResult(PrivateMedia media,int position,int code,String msg);
        //删除多媒体文件回执
        void showDeleteMediaFileResult(PrivateMedia media,int position,int code,String msg);
        //设置封面回执
        void showSetImageFrontResult(int code, String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取多媒体文件
        void getPrivateMedia(String homeUserID, int mediaType,int page);
        //删除多媒体文件
        void deleteMediaFile(PrivateMedia mediaInfo, int position);
        //修改多媒体文件访问权限
        void modifyMediaFilePrivatePermission(PrivateMedia mediaInfo, int position);
        //设置封面
        void setImageFront(PrivateMedia mediaInfo, int position);
    }
}
