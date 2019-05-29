package com.yc.liaolive.manager;

import android.app.Activity;
import android.text.TextUtils;

import com.kaikai.securityhttp.domain.GoagalInfo;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.UserDataInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.ui.business.LoginBusiness;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;

import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2018/9/6
 * 绑定第三方账号辅助
 */

public class PlatformAccountBindHelp {

    private static final String TAG = "BindOtherAccountMode";
    private static PlatformAccountBindHelp mInstance;
    private Activity mActivity;
    private int mPlatfromID;//账号类型 参考 Constant.LOGIN_TYPE_QQ 类型

    public static synchronized PlatformAccountBindHelp getInstance(){
        synchronized (PlatformAccountBindHelp.class){
            if(null==mInstance){
                mInstance=new PlatformAccountBindHelp();
            }
        }
        return mInstance;
    }

    /**
     * 必须先调用 attachActivity 方法
     * @param activity 依附宿主
     */
    public PlatformAccountBindHelp attachActivity(Activity activity){
        this.mActivity=activity;
        return mInstance;
    }

    /**
     * 绑定第三方平台账号
     * @param platfromID 平台账号类型
     */
    public void onBindPlatformAccount(int platfromID){
        if(null==mActivity) {
            throw new IllegalArgumentException("Context cannot be empty! Please first officer attachActivity(Activity context) function!");
        }
        if(mActivity.isFinishing()) return;
        this.mPlatfromID =platfromID;
        SHARE_MEDIA platform=SHARE_MEDIA.QQ;
        switch (platfromID) {
            case Constant.LOGIN_TYPE_QQ:
                if(!LoginBusiness.getInstance().isPackageInstalled(mActivity,Constant.PACKAGE_QQ)){
                    ToastUtils.showCenterToast("请先安装QQ客户端");
                    if(null!=mOnBindChangedListener) mOnBindChangedListener.onFailure(-1,"请先安装QQ客户端");
                    return;
                }
                platform = SHARE_MEDIA.QQ;
                break;
            case Constant.LOGIN_TYPE_WEXIN:
                if(!LoginBusiness.getInstance().isPackageInstalled(mActivity,Constant.PACKAGE_WEIXIN)){
                    ToastUtils.showCenterToast("请先安装微信客户端");
                    if(null!=mOnBindChangedListener) mOnBindChangedListener.onFailure(-1,"请先安装微信客户端");
                    return;
                }
                platform = SHARE_MEDIA.WEIXIN;
                break;
            case Constant.LOGIN_TYPE_WEIBO:
                platform = SHARE_MEDIA.SINA;
                break;
        }
        bindPlatform(platform);
    }

    /**
     * 绑定手机号
     * @param platfromID
     * @param phone
     * @param code
     */
    public void onBindPlatformAccount(int platfromID,String phone,String code){
        if(null==mActivity) {
            throw new IllegalArgumentException("Context cannot be empty! Please first officer attachActivity(Activity context) function!");
        }
        if(mActivity.isFinishing()) return;
        this.mPlatfromID =platfromID;
        UserManager.getInstance().onBindPlatformAccount(platfromID, "", "", phone, code, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                ToastUtils.showCenterToast("操作成功");
                if(null!=mOnBindChangedListener) mOnBindChangedListener.onSuccess(mPlatfromID,null);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(null!=mOnBindChangedListener) mOnBindChangedListener.onFailure(code,errorMsg);
            }
        });
    }

    /**
     * 解绑第三方账号
     * @param platfromID
     */
    private void onUnBindPlatformAccount(int platfromID) {
        if(null==mActivity) {
            throw new IllegalArgumentException("Context cannot be empty! Please first officer attachActivity(Activity context) function!");
        }
        showProgressDialog("操作中,请稍后...");
        UserManager.getInstance().onUnBindPlatformAccount(platfromID, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                ToastUtils.showCenterToast("操作成功");
                closeProgressDialog();
                if(null!=mOnBindChangedListener) mOnBindChangedListener.onSuccess(mPlatfromID,null);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                if(null!=mOnBindChangedListener) mOnBindChangedListener.onFailure(code,errorMsg);
            }
        });
    }

    /**
     * 开始请求Server端绑定第三方账号
     * @param userDataInfo
     * @param platform
     */
    private void bindPlatformAccount(UserDataInfo userDataInfo, SHARE_MEDIA platform) {
        //立即删除授权记录
        if(null==mActivity) return;
        UMShareAPI.get(mActivity).deleteOauth(mActivity, platform, null);
        UserManager.getInstance().onBindPlatformAccount(mPlatfromID, userDataInfo.getOpenid(), userDataInfo.getAccessToken(),"","", new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                ToastUtils.showCenterToast("绑定成功");
                if(null!=mOnBindChangedListener) mOnBindChangedListener.onSuccess(mPlatfromID,null);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                ToastUtils.showCenterToast(errorMsg);
                if(null!=mOnBindChangedListener) mOnBindChangedListener.onFailure(code,errorMsg);
            }
        });
    }

    /**
     * QQ、微信、微博 授权
     * @param media
     */
    private void bindPlatform(SHARE_MEDIA media) {
        if(null==mActivity) return;
        boolean isauth = UMShareAPI.get(mActivity).isAuthorize(mActivity, media);//判断当前APP有没有授权登录
        if (isauth) {
            UMShareAPI.get(mActivity).getPlatformInfo(mActivity, media, LoginAuthListener);//获取用户信息
        } else {
            UMShareAPI.get(mActivity).doOauthVerify(mActivity, media, LoginAuthListener);//用户授权登录
        }
    }

    /**
     * QQ 微信 微博 登陆后回调
     */
    UMAuthListener LoginAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            showProgressDialog("操作中,请稍后...");
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            int loginType = Constant.LOGIN_TYPE_QQ;
            switch (platform) {
                case QQ:
                    loginType = Constant.LOGIN_TYPE_QQ;
                    break;
                case WEIXIN:
                    loginType = Constant.LOGIN_TYPE_WEXIN;
                    break;
                case SINA:
                    loginType = Constant.LOGIN_TYPE_WEIBO;
                    break;
            }
            try {
                if (null != data && data.size() > 0) {
                    UserDataInfo userDataInfo = new UserDataInfo();
                    userDataInfo.setIemil(GoagalInfo.get().uuid);
                    userDataInfo.setLoginType(String.valueOf(loginType));
                    //新浪微博
                    if (platform == SHARE_MEDIA.SINA) {
                        userDataInfo.setNickname(data.get("name"));
                        userDataInfo.setProvince(data.get("location"));
                        userDataInfo.setFigureurl_qq_2(data.get("iconurl"));
                        userDataInfo.setGender(data.get("gender"));
                        userDataInfo.setOpenid(data.get("id"));
                        userDataInfo.setImageBG(data.get("cover_image_phone"));
                        //微信、QQ
                    } else {
                        userDataInfo.setNickname(data.get("screen_name"));
                        userDataInfo.setCity(data.get("city"));
                        userDataInfo.setFigureurl_qq_2(data.get("iconurl"));
                        userDataInfo.setGender(data.get("gender"));
                        userDataInfo.setProvince(data.get("province"));
                        userDataInfo.setOpenid(data.get("openid"));
                        userDataInfo.setAccessToken(data.get("accessToken"));
                        userDataInfo.setUid(data.get("uid"));
                    }
                    //授权成功,再次请求用户token和用户基本信息
                    if (TextUtils.isEmpty(userDataInfo.getNickname()) && TextUtils.isEmpty(userDataInfo.getFigureurl_qq_2())) {
                        bindPlatform(platform);
                    } else {
                        //授权 App成功,防止微博
                        if (!TextUtils.isEmpty(userDataInfo.getOpenid())) {
                            bindPlatformAccount(userDataInfo,platform);
                        } else {
                            bindPlatform(platform);
                        }
                    }
                } else {
                    closeProgressDialog();
                    ToastUtils.showCenterToast("操作失败，请重试!");
                }
            } catch (Exception e) {
                closeProgressDialog();
                ToastUtils.showCenterToast("操作失败，请重试!");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            closeProgressDialog();
            ToastUtils.showCenterToast("操作失败，请重试!");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            closeProgressDialog();
            ToastUtils.showCenterToast("操作取消");
        }
    };

    /**
     * 显示加载中
     * @param msg
     */
    private void showProgressDialog(String msg) {
        if(null!=mActivity&&!mActivity.isFinishing()&&mActivity instanceof TopBaseActivity){
            TopBaseActivity activity = (TopBaseActivity) mActivity;
            activity.showProgressDialog(msg,false);
        }
    }

    /**
     * 关闭弹窗
     */
    private void closeProgressDialog(){
        if(null!=mActivity&&!mActivity.isFinishing()&&mActivity instanceof TopBaseActivity){
            TopBaseActivity activity = (TopBaseActivity) mActivity;
            activity.closeProgressDialog();
        }
        mActivity=null;
    }

    public void onDestroy(){
        closeProgressDialog();
    }

    /**
     * 绑定、解绑状态监听
     */
    public interface OnBindChangedListener{
        void onSuccess(int platfromID,String content);
        void onFailure(int code,String errorMsg);
    }

    private OnBindChangedListener mOnBindChangedListener;

    public PlatformAccountBindHelp setOnBindChangedListener(OnBindChangedListener onBindChangedListener) {
        mOnBindChangedListener = onBindChangedListener;
        return mInstance;
    }
}
