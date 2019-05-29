package com.yc.liaolive.base;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import android.content.Context;
import android.net.ConnectivityManager;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.user.manager.UserManager;

/**
 * TinyHung@outlook.com
 * 2018/6/5
 * 基于RX生命周期控制
 * 网络请求控制
 */

public class RxBasePresenter<V extends BaseContract.BaseView> implements BaseContract.BasePresenter<V> {

    protected static final String TAG = "RxBasePresenter";
    protected Context mContext;//全局Context
    protected V mView;
    protected CompositeSubscription mCompositeSubscription;
    protected     boolean isLoading         = false;
    public static boolean isRsa             = true;//是否加密正文
    public static boolean isZip             = true;//是否压缩请求体
    public static boolean isEncryptResponse = true;//是否解密返回值
    protected ConnectivityManager mConnectionManager;
    /**
     * 头部信息
     * @return
     */
    public static Map<String,String> getHeaders(){
         return UserManager.getInstance().getHeaders();
    }
   /**
     * 一些场景需要用到的回调响应
     */
    public interface OnResqustCallBackListener{
        void onSuccess(String data);
        void onFailure(int code,String errorMsg);
    }

    /**
     * 初始参数
     * @return
     */
    public Map<String,String> getDefaultPrames(String url){
        Map<String,String> params=new HashMap<>();
//        params.put("userid",UserManager.getInstance().getUserId());
        params.put("imeil",VideoApplication.mUuid);
        return params;
    }

    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 构造
     */
    public RxBasePresenter(){
        this.mContext= AppEngine.getApplication();
        if ("tice".equals(BuildConfig.BUILD_TYPE)) {
            isRsa = false;
            isZip = false;
            isEncryptResponse = false;
        }
    }

    /**
     * 绑定生命周期
     * @param subscription
     */
    protected void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void attachView(V view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        mConnectionManager=null;
        unSubscribe();
    }

    /**
     * 解除生命周期绑定
     */
    protected void unSubscribe() {
        if (mCompositeSubscription != null) mCompositeSubscription.unsubscribe();
        mContext=null;
    }
}
