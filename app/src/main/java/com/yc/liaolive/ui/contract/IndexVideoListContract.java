
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.PrivateMedia;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/9/25
 * 首页视频列表
 */

public interface IndexVideoListContract {

    interface View extends BaseContract.BaseView {
        //视频列表
        void showLiveVideos(List<PrivateMedia> data);
        void showVideoEmpty();
        void showVideoError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getVideoLists(String url, int page,int fileType,String source);
    }
}
