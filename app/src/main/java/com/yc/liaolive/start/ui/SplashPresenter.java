package com.yc.liaolive.start.ui;

import com.yc.liaolive.base.BaseMVPPresenter;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.ui.contract.SplashContract;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;

/**
 * TinyHung@outlook.com
 * 2017/9/5
 * 开屏启动,用户注册登录处理
 * 获取APP配置信息
 */

public class SplashPresenter extends BaseMVPPresenter<SplashContract.View> implements SplashContract.Presenter<SplashContract.View>{

    private static final String TAG = "SplashPresenter";

    /**
     * 初始化
     */
    @Override
    public void onCreate() {
        //存在注册记录，直接登录
        if(UserManager.getInstance().isUserLogin()){
            loginToServer();
        //从设置中退出了账号，路由到登录界面
        }else if(1== SharedPreferencesUtil.getInstance().getInt(Constant.SP_SETTING_EXIT)){
            if(null!=mView) mView.navToLogin();
        //第一次打开APP游客账号自动注册
        }else{
            //注册游客
            UserManager.getInstance().visitorRegister(new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    loginToServer();
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    if(null!=mView) mView.navToLogin();
                }
            });
        }
    }

    /**
     * 用户登录
     */
    private void loginToServer() {
        //登录
        UserManager.getInstance().autoLogin(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=mView) mView.navToHome();
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(null!=mView) mView.navToLogin();
            }
        });
    }
}
