
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.PrivateMedia;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/9/27
 * 视频点赞、分享上报
 */

public interface VideoActionContract {

    interface View extends BaseContract.BaseView {
        void showActionResul(PrivateMedia privateMedia, int actionType);
        void showActionError(int code, String errorMsg);

        //多媒体文件列表
        void showMedias(List<PrivateMedia> data);
        void showMediaEmpty();
        void showMediaError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //点赞和分享
        void videoLoveShare(PrivateMedia privateMedia, int actionType);
        //获取多媒体文件
        void getMedias(String hostUrl,String homeUserID, int mediaType,int page,int source,long fileid);
    }
}
