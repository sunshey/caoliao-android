package com.yc.liaolive.base;

import android.app.Activity;

/**
 * TinyHung@outlook.com
 * 2018/9/5
 * MVP界面组件
 */

public class BaseMVPPresenter<V extends BaseMVPContract.BaseMVPView> implements BaseMVPContract.BaseMVPPresenter<V> {

    protected V mView;
    private Activity mContext;

    public BaseMVPPresenter(Activity activity){
        this.mContext=activity;
    }

    public BaseMVPPresenter(){

    }

    public Activity getContext(){
        return mContext;
    }

    public Activity getActivity(){
        return mContext;
    }

    @Override
    public void attachView(V view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        this.mContext=null;
    }
}
