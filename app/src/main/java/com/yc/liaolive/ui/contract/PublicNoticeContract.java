
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.bean.HomeNoticeInfo;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 系统通告
 */

public interface PublicNoticeContract {

    interface View extends BaseContract.BaseView {
        void showPublicNotices(List<HomeNoticeInfo> data);
        void showPublicNoticeEmpty();
        void showPublicNoticeError(int code, String errorMsg);
        //通告详情
        void showNoticeDetails(HomeNoticeInfo data);
        void showNoticeDetailError(int code, String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getPublicNotices(int page);
        void getNoticeDetails(String id);
    }
}
