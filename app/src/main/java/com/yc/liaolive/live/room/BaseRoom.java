package com.yc.liaolive.live.room;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.CustomMessageInfo;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.im.IMMessageMgr;
import com.yc.liaolive.live.listener.GroupIMMessageListener;
import com.yc.liaolive.live.listener.RoomCallBack;
import com.yc.liaolive.live.manager.RoomPresenter;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;

import java.util.ArrayList;

/**
 * TinyHung@Outlook.com
 * 2018/11/7 直播间模块代码根据项目需求优化
 * 2018/12/5 切换至七牛云
 */

public abstract class BaseRoom implements GroupIMMessageListener, RoomContract.View{

    public static final String TAG = "BaseRoom";
    public static final int PUBLISH_MODE_VIDEO = 0;//视频推流模式
    public static final int PUBLISH_MODE_AUDIO = 1;//音频推流模式
    public static final int USER_IDENTITY_PULL=0;//用户
    public static final int USER_IDENTITY_PUSH=1;//主播
    protected Context mContext;
    protected Handler mHandler;
    protected String mCurrRoomID;//当前绑定活动的群组ID
    protected IMMessageMgr  mIMMessageMgr;//配合直播间的IM
    protected boolean mIsDestroy             = false;
    protected ArrayList<RoomList> mRoomList = new ArrayList<>();//直播间列表
    protected int mUserIdentity=USER_IDENTITY_PULL;
    protected int mPublishMediaType =PUBLISH_MODE_VIDEO;//推流模式
    protected RoomPresenter mPresenter;
    protected String mixedPlayUrl;//当前房间的拉流地址

    /**
     * 初始化构造
     * @param context
     */
    public BaseRoom(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
    }

    /**
     * 公共
     * 在组件对应函数调用
     */
    public void onCreate() {
        mIsDestroy=false;
        if(null==mContext) return;
        if(null==mIMMessageMgr) {
            mIMMessageMgr = new IMMessageMgr(mContext);
            mIMMessageMgr.setIMMessageListener(this);
            mIMMessageMgr.setGroupID(mCurrRoomID);
        }
        if(null==mPresenter){
            mPresenter = new RoomPresenter();
            mPresenter.attachView(this);
        }
    }

    /**
     * 公共
     * 在组件对应函数调用
     */
    public void onResume(){
    }

    /**
     * 公共
     * 在组件对应函数调用
     */
    public void onPause(){
    }

    /**
     * 公共
     * 在组件对应函数调用
     */
    public void onDestroy() {
        mIsDestroy=true;
        if (mIMMessageMgr != null){
            mIMMessageMgr.setIMMessageListener(null);
            mIMMessageMgr.setGroupID(null);
            mIMMessageMgr=null;
        }
        if(null!=mPresenter){
            mPresenter.detachView();
            mPresenter=null;
        }
        if(null!=mRoomList) mRoomList.clear();
        mCurrRoomID=null;mPublishMediaType =0;mixedPlayUrl=null;mUserIdentity=0;
    }


    /**
     * 公共
     * 设置推流类型
     * @param publishMediaType 0：视频 1：音频
     */
    public void setPublishMediaType(int publishMediaType) {
        this.mPublishMediaType =publishMediaType;
    }

    /**
     * 公共
     * @return
     */
    public String getCurrRoomID() {
        return mCurrRoomID;
    }

    /**
     * 公共
     * @param roomID
     */
    public void setCurrRoomID(String roomID){
        this.mCurrRoomID=roomID;
        if(null!=mIMMessageMgr) mIMMessageMgr.setGroupID(mCurrRoomID);
    }

    /**
     * 公共
     * 设置用户身份
     * @param useridentity  1:主播 2：观众
     */
    public void setUserIdentity(int useridentity) {
        this.mUserIdentity=useridentity;
    }

    /**
     * 公共
     * 返回用户身份
     */
    public int getUserIdentity() {
        return mUserIdentity;
    }

    /**
     * 公共
     * 上报直播间错误日志
     * @param publishUrl
     * @param errCode
     * @param errInfo
     */
    public void postLogs(String publishUrl, int errCode, String errInfo) {
        LogApi simpli=new LogApi();
        simpli.setErrCode(errCode);
        simpli.setErrMessage(errInfo);
        simpli.setRequstUrl(publishUrl);
        ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
        actionLogInfo.setData(simpli);
        UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_ROOM,actionLogInfo,null);
    }

    /**
     * 群组纯文本消息
     * @param message
     * @param callback
     */
    public void sendGroupTextMessage(@NonNull String message, boolean isDanmu,final IMMessageMgr.Callback callback){
        if(null!=mIMMessageMgr) mIMMessageMgr.sendTextMessage(message,isDanmu, callback);
    }

    /**
     * 公共
     * 发送群组自定义消息
     * @param cmd
     * @param message
     * @param callback
     */
    public void sendRoomCustomMsg(@NonNull String cmd, @NonNull String message, final IMMessageMgr.Callback callback) {
        if(null!=mIMMessageMgr){
        CommonJson<CustomMessageInfo> customMessage = new CommonJson<>();
            customMessage.cmd = "CustomCmdMsg";
            customMessage.data = new CustomMessageInfo();
            customMessage.data.userName = UserManager.getInstance().getNickname();
            customMessage.data.userAvatar =UserManager.getInstance().getAvatar();
            customMessage.data.cmd = cmd;
            customMessage.data.msg = message ;
            customMessage.data.userID=UserManager.getInstance().getUserId();
            String content = new Gson().toJson(customMessage, new TypeToken<CommonJson<CustomMessageInfo>>(){}.getType());
            mIMMessageMgr.sendGroupCustomMessage(content, callback);
        }
    }

    /**
     * 公共
     * 发送群组自定义消息
     * @param message
     * @param callback
     */
    public void sendRoomActionCustomMsg( @NonNull String message, final IMMessageMgr.Callback callback) {
        if(null!=mIMMessageMgr){
            CommonJson<String> customMessage = new CommonJson<>();
            customMessage.cmd = "CustomCmdMsg";
            customMessage.data = message;
            String content = new Gson().toJson(customMessage, new TypeToken<CommonJson<String>>(){}.getType());
            mIMMessageMgr.sendGroupCustomMessage(content, callback);
        }
    }

    /**
     * 设置主播切换至后台推送的画面
     * @param bitmap
     */
    public void setPauseImage(final @Nullable Bitmap bitmap) {
//        if (mTXLivePusher != null) {
//            TXLivePushConfig config = mTXLivePusher.getConfig();
//            config.setPauseImg(bitmap);
//            config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
//            mTXLivePusher.setConfig(config);
//        }
    }


    /**
     * 创建AV群组
     * @param groupID
     * @param groupName
     * @param callBack
     */
    protected void createGroup(String groupID,String groupName,IMMessageMgr.Callback callBack){
        if(null!=mIMMessageMgr){
            mIMMessageMgr.createGroup(groupID,groupName,callBack);
        }
    }

    public interface JoinGroupCallback {
        void onError(int errCode, String errInfo);
        void onSuccess();
    }

    /**
     * 进入群组
     * @param roomID
     * @param callback
     */
    public void jionGroup(String roomID, final JoinGroupCallback callback){
        if(!TextUtils.isEmpty(roomID)&&null!=mIMMessageMgr){
            mIMMessageMgr.jionGroup(roomID, new IMMessageMgr.Callback() {
                @Override
                public void onError(int code, String errInfo) {
                    if(null!=callback) callback.onError(code, errInfo);
                }

                @Override
                public void onSuccess(Object... args) {
                    if(null!=callback)  callback.onSuccess();
                }
            });
        }
    }

    /**
     * 退出群组
     * @param roomID
     */
    public void exitGroup(String roomID){
        //2. 调用IM的quitGroup
        if(!TextUtils.isEmpty(roomID)&&null!=mIMMessageMgr){
            mIMMessageMgr.quitGroup(roomID, new IMMessageMgr.Callback() {
                @Override
                public void onError(int code, String errInfo) {
                    //cb.onError(code, errInfo);
                }

                @Override
                public void onSuccess(Object... args) {
                    //cb.onSuccess();
                }
            });
        }
    }

    protected void runOnUiThread(final Runnable runnable){
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            });
        }
    }

    protected void runOnUiThreadDelay(final Runnable runnable, long delayMills){
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, delayMills);
        }
    }

    /**
     * 公共
     * @param <T>
     */
    protected class MainCallback<T> {
        private final RoomCallBack callback;
        public MainCallback(RoomCallBack callback) {
            this.callback = callback;
        }
        public void onError(final int errCode, final String errInfo) {
            if(null!=callback) runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(errCode,errInfo);
//                    try {
//                        Method onError = callback.getClass().getMethod("onError", int.class, String.class);
//                        onError.invoke(callback, errCode, errInfo);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            });
        }

        public void onSuccess(final T obj) {
            if(null!=callback) runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(obj);
                }
            });
        }

        public void onSuccess() {
            if(null!=callback) runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess("");
                }
            });
        }
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}
}