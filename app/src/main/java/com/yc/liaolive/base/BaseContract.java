
package com.yc.liaolive.base;


public interface BaseContract {

    interface BasePresenter<V> {
        void attachView(V view);
        void detachView();
    }

    interface BaseView {
        void showErrorView();
        void complete();
    }
}
