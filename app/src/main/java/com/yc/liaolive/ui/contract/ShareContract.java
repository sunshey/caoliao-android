
package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;

/**
 * TinyHung@outlook.com
 * 2017/8/5 10:53
 * 分享
 */

public interface ShareContract {

    interface View extends BaseContract.BaseView {
        //关注用户结果
        void showWebResult(String data);
        void showWebResultError(int code, String data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //关注
        void getWebUrl(String roomID);
    }
}
