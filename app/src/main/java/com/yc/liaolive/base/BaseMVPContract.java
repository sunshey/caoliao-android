package com.yc.liaolive.base;

/**
 * TinyHung@Outlook.com
 * 2018/9/5
 */

public interface BaseMVPContract {

    interface BaseMVPPresenter<V> {
        void attachView(V view);
        void detachView();
    }

    interface BaseMVPView {
        void onDestroy();
    }
}
