
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.PrivateMedia;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/9/14
 * 私密，相册、视频
 */

public interface VideoPlayerContract {

    interface View extends BaseContract.BaseView {
        //多媒体文件列表
        void showMedias(List<PrivateMedia> data);
        void showMediaEmpty();
        void showMediaError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取多媒体文件
        void getMedias(String hostUrl,String homeUserID, int mediaType,int page,String source,long fileid);
    }
}
