package com.yc.liaolive.media.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.FansInfo;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2018/11/20
 * 多媒体预览
 */

public interface MediaPreviewContract {

    interface View extends BaseContract.BaseView {
        void showMediaTopList(List<FansInfo> data);
        void showMediaError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getMediaTop(long fileid,String anchorid,int page);
    }
}
