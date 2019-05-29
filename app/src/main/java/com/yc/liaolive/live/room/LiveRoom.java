package com.yc.liaolive.live.room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CustomMessageInfo;
import com.yc.liaolive.live.bean.PushMessage;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.im.IMMessageMgr;
import com.yc.liaolive.live.listener.LiveRoomActionListener;
import com.yc.liaolive.live.listener.RoomCallBack;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/7 直播间模块代码根据项目需求优化
 * 2018/12/5 切换至七牛云
 */

public class LiveRoom extends BaseRoom{

    private RoomActionListenerCallback mRoomActionListenerCallback;//所有消息事件回调
    private List<FansInfo> mSpeechUsers;//本群已被禁言的用户列表

    /**
     * LiveRoom 直播房间
     * 初始化的时候先提前初始化播放器
     */
    public LiveRoom(Context context) {
        super(context);
    }

    //=============================================拉流API==========================================

    /**
     * 设置房间消息事件回调
     * @param listener
     */
    public void setLiveRoomListener(LiveRoomActionListener listener) {
        if(null== mRoomActionListenerCallback) {
            mRoomActionListenerCallback =new RoomActionListenerCallback(listener);
            return;
        }
        mRoomActionListenerCallback.setRoomActionListener(listener);
    }

    /**
     * 获取房间信息
     * @param userID
     * @param callBackListener
     */
    public void getRoomData(String userID,RoomContract.OnRoomCallBackListener callBackListener){
        if(null!=mPresenter) mPresenter.getRoomData(userID,callBackListener);
    }

    /**
     * 获取房间信息
     * @param userID
     * @param callBackListener
     */
    public void getQueryRoomData(String userID,int isBuy,RoomContract.OnRoomCallBackListener callBackListener){
        if(null!=mPresenter) mPresenter.getQueryRoomData(userID,isBuy,callBackListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        switchToForeground();
    }
    /**
     * 更新房间信息至服务器
     * @param roomID
     * @param state
     * @param roomTitle
     * @param frontcover
     * @param latitude
     * @param longitude
     */
    public void uploadRoom(String roomID,int state ,String roomTitle,String frontcover,double latitude,double longitude){
        if(null!=mPresenter) mPresenter.uploadRoom(roomID,state,roomTitle,frontcover,latitude,longitude);
    }


    @Override
    public void onPause() {
        super.onPause();
        switchToBackground();
    }


    public void addRooms(List<RoomList> list) {
        if(null!=mRoomList){
            mRoomList.clear();
            mRoomList.addAll(list);
        }
    }

    public List<RoomList> getRoomList(){
        return mRoomList;
    }

    /**
     * LiveRoom 进入房间Callback
     */
    public interface EnterRoomCallback extends RoomCallBack{

        @Override
        void onError(int errCode, String errInfo);

        @Override
        void onSuccess(Object data);
    }

    private long currentTime;


    /**
     * 通过SDK进入房间
     * @param roomID    房间号
     * @param cb        进入房间完成的回调
     */
    public void addRoom(@NonNull final String roomID, final EnterRoomCallback cb) {
        mIsDestroy=false;
        final MainCallback<String> callback = new MainCallback<>(cb);
        // 调用IM的joinGroup
        jionGroup(roomID, new JoinGroupCallback() {
            @Override
            public void onError(int code, String errInfo) {
                callback.onError(code, errInfo);
                long millis = System.currentTimeMillis();
                if(millis-currentTime>60000){
                    postLogs("jionGroup",code,errInfo);
                    currentTime=millis;
                }
            }

            @Override
            public void onSuccess() {
                mCurrRoomID  = roomID;
                LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(roomID);
                callback.onSuccess();
            }
        });
    }

    /**
     * 通过Server进入房间
     * @param roomID    房间号
     * @param callBackListener 回调
     */
    public void addRoomByServer(@NonNull final String roomID, final RoomContract.OnRoomCallBackListener callBackListener) {
        mIsDestroy=false;
        if(null!=mPresenter){
            mPresenter.roomIn(roomID, UserManager.getInstance().getUserId(), new RoomContract.OnRoomCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    setCurrRoomID(roomID);
                    if(null!=callBackListener) callBackListener.onSuccess(object);
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    if(null!=callBackListener) callBackListener.onFailure(code,errorMsg);
                }
            });
        }
    }

    /**
     * LiveRoom 离开房间Callback
     */
    public interface ExitRoomCallback extends RoomCallBack{
        @Override
        void onError(int errCode, String errInfo);
        @Override
        void onSuccess(Object data);
    }

    /**
     * 离开房间
     * @param callback 离开房间完成的回调
     */
    public void exitRoom(final ExitRoomCallback callback) {
        if(null != mPresenter){
            mPresenter.roomOut(mCurrRoomID, UserManager.getInstance().getUserId(), callback);
        } else if (callback != null){
            callback.onError(-1, "退出直播");
        }
    }

    public String getMixedPlayUrl() {
        return mixedPlayUrl;
    }

    public void setMixedPlayUrl(String mixedPlayUrl) {
        this.mixedPlayUrl = mixedPlayUrl;
    }
    //=============================================推流API==========================================

    /**
     * 获取房间的禁言用户
     * @param roomID
     * @param callBackListener
     */
    public void getSpeechList(String roomID, final RoomContract.OnRoomRequstBackListener callBackListener){
        if(null!=mPresenter) mPresenter.getSpeechList(roomID,callBackListener);
    }

    /**
     * 封禁、解禁某用户
     * @param toUserID
     * @param type
     * @param callBackListener
     */
    public void speechToUser(String toUserID, int type, final RoomContract.OnRoomCallBackListener callBackListener){
        if(null!=mPresenter) mPresenter.speechToUser(toUserID,type,callBackListener);
    }

    /**
     * 设定本群的禁言列表
     * @param data
     */
    public void setSpeechList(List<FansInfo> data) {
        this.mSpeechUsers=data;
    }

    /**
     * 获取本地可能存在的禁言列表库
     */
    public List<FansInfo> getSpeechList() {
        return mSpeechUsers;
    }

    /**
     * 查询此用户是否在禁言列表中
     * @param userID
     */
    public boolean isExistSpeechToUser(String userID) {
        if(TextUtils.isEmpty(userID)) return false;
        if(null==mSpeechUsers) return false;
        for (int i = 0; i < mSpeechUsers.size(); i++) {
            FansInfo fansInfo = mSpeechUsers.get(i);
            if(null!=fansInfo&&!TextUtils.isEmpty(fansInfo.getUserid())&&userID.equals(fansInfo.getUserid())){
                return true;
            }
        }
        return false;
    }

    /**
     * 添加一个用户到禁言列表中
     * @param userID
     */
    public void addUserToSpeechs(String userID) {
        if(null==mSpeechUsers) mSpeechUsers=new ArrayList<>();
        FansInfo fansInfo=new FansInfo();
        fansInfo.setUserid(userID);
        mSpeechUsers.add(fansInfo);
    }

    /**
     * 将用户从本地禁言列表中移除
     * @param userID
     */
    public synchronized void removeUserForSpeechs(String userID) {
        if(null==mSpeechUsers) return;
        int index=-1;
        for (int i = 0; i < mSpeechUsers.size(); i++) {
            if(userID.equalsIgnoreCase(mSpeechUsers.get(i).getUserid())){
                index=i;
                break;
            }
        }
        if(-1!=index){
            mSpeechUsers.remove(index);
        }
    }

    /**
     * 退出直播、断开流等一系列操作
     * @param isDestroyGroup 是否销毁群组
     */
    public void stopLive (boolean isDestroyGroup) {
        stopLive(isDestroyGroup,false);
    }

    /**
     * 退出直播、断开流等一系列操作
     * @param isDestroyGroup 是否销毁群组
     */
    public void stopLive (boolean isDestroyGroup,boolean isDestroy) {
        if(isDestroyGroup){
            if(null!=mIMMessageMgr&&!TextUtils.isEmpty(mCurrRoomID)) mIMMessageMgr.destroyGroup(mCurrRoomID, null);
        }else{
            if(null!=mIMMessageMgr&&!TextUtils.isEmpty(mCurrRoomID)) mIMMessageMgr.quitGroup(mCurrRoomID, null);
        }
        if(null!=mSpeechUsers) mSpeechUsers.clear(); mSpeechUsers=null;
        if(isDestroy) onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mRoomActionListenerCallback) mRoomActionListenerCallback.onDestroy();
        mRoomActionListenerCallback=null;
    }

    //=============================================推流端API=============================================

    /**
     * 从前台切换到后台，关闭采集摄像头数据，推送默认图片
     */
    public void switchToBackground(){
        //主播端
        if(USER_IDENTITY_PUSH==mUserIdentity){
            //通知所有用户，应用已切换至后台
            PushMessage pushMessage=new PushMessage();
            pushMessage.setMessage("已切换至后台");
            pushMessage.setForegroundState(1);
            pushMessage.setCmd(Constant.MSG_CUSTOM_ROOM_PUSH_SWITCH_CHANGED);
            String content = new Gson().toJson(pushMessage);
            sendRoomActionCustomMsg(content, null);
        }
    }

    /**
     * 由后台切换到前台，开启摄像头数据采集
     */
    public void switchToForeground(){
        //主播端
        if(USER_IDENTITY_PUSH==mUserIdentity){
            //通知所有用户，应用已切换至前台
            PushMessage pushMessage=new PushMessage();
            pushMessage.setMessage("已切换至前台");
            pushMessage.setCmd(Constant.MSG_CUSTOM_ROOM_PUSH_SWITCH_CHANGED);
            pushMessage.setForegroundState(0);
            String content = new Gson().toJson(pushMessage);
            sendRoomActionCustomMsg( content, null);
        }
    }

    /**
     * LiveRoom 发送自定义消息
     * @param cmd
     * @param message
     * @param callback
     */
    public void sendRoomCustomMsg(@NonNull String cmd, @NonNull String message, final IMMessageMgr.Callback callback) {
        super.sendRoomCustomMsg(cmd, message, new IMMessageMgr.Callback() {
            @Override
            public void onError(final int code, final String errInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(code, errInfo);
                        }
                    }
                });
            }

            @Override
            public void onSuccess(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }

    /**
     * LiveRoom 发送自定义事件消息
     * @param message
     * @param callback
     */
    public void sendRoomActionCustomMsg(@NonNull String message, final IMMessageMgr.Callback callback) {
        super.sendRoomActionCustomMsg(message, new IMMessageMgr.Callback() {
            @Override
            public void onError(final int code, final String errInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(code, errInfo);
                        }
                    }
                });
            }

            @Override
            public void onSuccess(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                });
            }
        });
    }


    /**
     * IM连接状态
     */
    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {
        ToastUtils.showCenterToast("网络异常，聊天房间断开，您可能收不到消息");
    }

    /**
     * 群组被销毁了
     * @param groupID
     */
    @Override
    public void onGroupDestroyed(final String groupID) {
        if(null!=mRoomActionListenerCallback&&TextUtils.equals(groupID,getCurrRoomID())) mRoomActionListenerCallback.onRoomClosed(groupID);
    }

    /**
     * 新的群组文本消息
     * @param groupID
     * @param senderID 发送人信息
     * @param sendNickname
     * @param sendhead
     * @param sendVipLeve
     * @param sendGender
     * @param sendUserType
     * @param messageContent 消息内容
     */
    @Override
    public void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendVipLeve ,long sendGender,long sendUserType,String messageContent) {
        if(null!=mRoomActionListenerCallback) mRoomActionListenerCallback.onGroupTextMessage(groupID, senderID, sendNickname, sendhead, sendVipLeve,sendGender,sendUserType,messageContent);
    }

    /**
     * 新的群组自定义消息
     * @param groupID
     * @param senderID
     * @param message
     */
    @Override
    public void onGroupCustomMessage(String groupID, String senderID, String message) {
        if(null!= mRoomActionListenerCallback &&!TextUtils.isEmpty(message)){
            CustomMessageInfo customMessage =  new Gson().fromJson(message, CustomMessageInfo.class);
            mRoomActionListenerCallback.onRoomCustomMsg(groupID, senderID, customMessage.userName, customMessage.userAvatar, customMessage.cmd, customMessage.msg);
        }
    }

    /**
     * 直播间C2C消息
     * @param sendID
     * @param message
     */
    @Override
    public void onC2CCustomMessage(String sendID, String message) {
        if(null!=mRoomActionListenerCallback) mRoomActionListenerCallback.onC2CCustomMessage(sendID, message);
    }

    /**
     * 新的群组系统消息
     * @param groupID
     * @param sender
     * @param message
     */
    @Override
    public void onGroupSystemMsg(String groupID, String sender, String message) {
        if(null!=mRoomActionListenerCallback) mRoomActionListenerCallback.onRoomSystemMsg(groupID,sender,message);
    }

    /**
     * 新的群组人数变化消息
     * @param groupId
     * @param sender
     * @param toJson
     */
    @Override
    public void onGroupSystemMsgNumber(String groupId, String sender, String toJson) {
        if(null!= mRoomActionListenerCallback) mRoomActionListenerCallback.onRoomNumberSystemMsg(groupId,sender,toJson);
    }

    /**
     * 群组主播端发起的自定义消息，如：应用程序前后台切换等
     * @param pushMessage
     */
    @Override
    public void onRoomPushMessage(PushMessage pushMessage) {
        if(null!=mRoomActionListenerCallback) mRoomActionListenerCallback.onRoomPushMessage(pushMessage);
    }

    @Override
    public void onDebugLog(final String log) {
        if(null!=mRoomActionListenerCallback) mRoomActionListenerCallback.onDebugLog(log);
    }


    /**
     * 一个将子线程的消息抛出到主线程的包装实现
     * 方法说明参考：LiveRoomActionListener 接口
     */
    private class RoomActionListenerCallback implements LiveRoomActionListener {

        private Handler mHandler;
        private LiveRoomActionListener mLiveRoomListener;

        public RoomActionListenerCallback(LiveRoomActionListener liveRoomListener) {
            this.mLiveRoomListener = liveRoomListener;
            this.mHandler = new Handler(Looper.getMainLooper());
        }

        /**
         * 更新监听器
         * @param liveRoomListener
         */
        public void setRoomActionListener(LiveRoomActionListener liveRoomListener) {
            this.mLiveRoomListener = liveRoomListener;
        }

        /**
         * 对应函数调用
         */
        public void onDestroy(){
            if(null!=mHandler) mHandler.removeMessages(0);
            mHandler=null;mLiveRoomListener=null;
        }

        @Override
        public void onRoomClosed(final String roomId) {
            if(null!=mLiveRoomListener&&null!=mHandler)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onRoomClosed(roomId);
                    }
                });
        }

        @Override
        public void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendVipLeve ,long sendGender,long sendUserType,String messageContent) {
            if(null!=mLiveRoomListener&&null!=mHandler)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onGroupTextMessage(groupID, senderID, sendNickname, sendhead, sendVipLeve,sendGender,sendUserType,messageContent);
                    }
                });
        }

        @Override
        public void onRoomCustomMsg(final String roomID, final String userID, final String userName, final String headPic, final String cmd, final String message) {
            if(null!=mLiveRoomListener&&null!=mHandler)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onRoomCustomMsg(roomID, userID, userName, headPic, cmd, message);
                    }
                });
        }

        @Override
        public void onRoomSystemMsg(final String groupID, final String sender, final String message) {
            if(null!=mLiveRoomListener&&null!=mHandler)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onRoomSystemMsg(groupID,sender, message);
                    }
                });
        }

        @Override
        public void onRoomNumberSystemMsg(final String groupId, final String sender, final String toJson) {
            if(null!=mLiveRoomListener&&null!=mHandler){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onRoomNumberSystemMsg(groupId,sender, toJson);
                    }
                });
            }
        }

        @Override
        public void onRoomPushMessage(final PushMessage pushMessage) {
            if(null!=mLiveRoomListener&&null!=mHandler){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onRoomPushMessage(pushMessage);
                    }
                });
            }
        }

        @Override
        public void onC2CCustomMessage(final String sendID, final String message) {
            if(null!=mLiveRoomListener&&null!=mHandler){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onC2CCustomMessage(sendID,message);
                    }
                });
            }
        }


        @Override
        public void onDebugLog(final String line) {
            if(null!=mLiveRoomListener&&null!=mHandler)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onDebugLog(line);
                    }
                });
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if(null!=mLiveRoomListener&&null!=mHandler)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLiveRoomListener.onError(errorCode, errorMessage);
                    }
                });
        }
    }
}
