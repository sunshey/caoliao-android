package com.yc.liaolive.ui.contract;

import com.yc.liaolive.base.BaseMVPContract;

/**
 * TinyHung@outlook.com
 * 2017/9/5
 * 开屏启动
 */
public interface SplashContract {

    interface View extends BaseMVPContract.BaseMVPView{
        //用户在设置中心退出了账号后、或者其他登录失败情况下 跳转至登录注册界面
        void navToLogin();
        //自动登录成功、游客账号注册并登录成功后前往主页
        void navToHome();
    }

    interface Presenter<V> extends BaseMVPContract.BaseMVPPresenter<V> {
        //初始化
        void onCreate();
    }
}
