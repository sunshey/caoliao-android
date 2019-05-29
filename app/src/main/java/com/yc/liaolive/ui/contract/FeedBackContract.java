package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseContract;

import java.io.File;

/**
 * Created by wanglin  on 2018/7/8 14:49.
 */
public interface FeedBackContract {

    interface View extends BaseContract.BaseView {
        void showEmptyNum();

        void finish();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void feedBack(String content, File file, String contact);
    }
}
