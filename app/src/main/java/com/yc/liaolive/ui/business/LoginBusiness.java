package com.yc.liaolive.ui.business;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.tencent.TIMCallBack;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;

import java.util.List;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * TinyHung@outlook.com
 * 2017/12/25
 * IM 登录、登出
 */

public class LoginBusiness {

    private static final String TAG = "LoginBusiness";
    private static LoginBusiness mInstance;

    public synchronized static LoginBusiness getInstance(){
        synchronized (LoginBusiness.class){
            if(null==mInstance){
                mInstance=new LoginBusiness();
            }
        }
        return mInstance;
    }

    /**
     * IM create
     * @param context
     */
    public void init(android.content.Context context) {
        //初始化imsdk
        TIMManager.getInstance().init(context);
        TIMManager.getInstance().setLogLevel(TIMLogLevel.OFF);
        //禁止服务器自动代替上报已读
        TIMManager.getInstance().disableAutoReport();
    }

    /**
     * 登录imsdk
     * @param identify 用户id
     * @param userSig 用户签名
     * @param callBack
     */
    public void loginIm(String identify, String userSig, TIMCallBack callBack){
        if (identify == null || userSig == null){
            if(null!=callBack) callBack.onError(-1,"用户身份或签名错误！");
            return;
        }
        try {
            TIMUser user = new TIMUser();
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(UserManager.getInstance().getAccountType());
            stringBuilder.append(":");
            stringBuilder.append(String.valueOf(UserManager.getInstance().getSDKAppID()));
            stringBuilder.append(":");
            stringBuilder.append(identify);
            user.parseFromString(stringBuilder.toString());
            //发起登录请求
            TIMManager.getInstance().login(Integer.parseInt(UserManager.getInstance().getSDKAppID()), user, userSig, callBack);
        }catch (RuntimeException e){
            if(null!=callBack) callBack.onError(-1,"登录失败，参数非法！");
        }catch (Exception e){
            if(null!=callBack) callBack.onError(-1,"登录失败，参数非法！");
        }
    }

    /**
     * 登出imsdsk
     * @param callBack 登出后回调
     */
    public void logout(TIMCallBack callBack){
        TIMManager.getInstance().logout(callBack);
    }


    public boolean isPackageInstalled(android.content.Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packageInfos = pm.getInstalledPackages(GET_META_DATA);
            if (packageInfos != null) {
                for (PackageInfo packageInfo : packageInfos) {
                    if (packageName.equals(packageInfo.packageName)) {
                        return true;
                    }
                }
            }
            String error="未安装应用程序";
            if(TextUtils.equals(Constant.PACKAGE_WEIXIN,packageName)){
                error="未安装微信";
            }else if(TextUtils.equals(Constant.PACKAGE_QQ,packageName)){
                error="未安装QQ";
            }
            ToastUtils.showCenterToast(error);
        }catch (RuntimeException e){
            return true;
        }
        return false;
    }

    /**
     * 判断是否安装了支付宝
     * @return true 为已经安装
     */
    public boolean hasZhifubao(android.content.Context context) {
        PackageManager manager = context.getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse("alipays://"));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }
}