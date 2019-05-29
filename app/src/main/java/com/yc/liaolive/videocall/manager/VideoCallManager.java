package com.yc.liaolive.videocall.manager;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.droid.rtc.QNErrorCode;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMValueCallBack;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.live.ui.activity.AsmrRoomPullActivity;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.manager.CLNotificationManager;
import com.yc.liaolive.manager.ForegroundManager;
import com.yc.liaolive.media.ui.activity.VerticalAnchorPlayerAvtivity;
import com.yc.liaolive.media.ui.activity.VerticalVideoPlayerAvtivity;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.videocall.bean.CallCmdExtra;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.listsner.OnCallMessageListener;
import com.yc.liaolive.videocall.listsner.OnCallStateListener;
import com.yc.liaolive.videocall.listsner.OnCustomMessageListener;
import com.yc.liaolive.videocall.ui.activity.LiveCallActivity;

import static com.tencent.TIMElemType.Custom;

/**
 * TinyHung@Outlook.com
 * 2018/12/21
 * 视频通话管理者
 */

public class VideoCallManager {

    public static final String TAG = "VideoCallManager";
    private static VideoCallManager mInstance;
    private CallStatus mCallStatus = CallStatus.CALL_OFFLINE;//用户、主播默认是离线的状态
    private OnCallMessageListener mCallMessageListener =null;//通话状态监听器
    private OnCallStateListener mCallStateListener;//新视频通话监听
    private long CALL_TIME_OUT=70000;//呼出超时，默认70秒
    private long CALL_TIME_IN =60000;//呼入超时未接时间 默认60秒
    private Handler mHandler;
    private CallExtraInfo mCallExtraInfo;//本次呼叫信息
    private String mToUserID="";//对方用户ID
    private static boolean mIsBebusying=false;//是否正在忙碌中

    public boolean isBebusying() {
        return mIsBebusying;
    }

    public void setBebusying(boolean isBebusying) {
        this.mIsBebusying = isBebusying;
    }

    /**
     * 内部通话状态
     */
    public enum CallStatus {
        CALL_OFFLINE,       //用户已设置为离线状态
        CALL_FREE,          //空闲状态
        CALL_ANSWERING,     //接听、应答他人通话请求中
        CALL_CONVERSE,      //通话中
        CALL_MORE,          //其他
    }

    public static synchronized VideoCallManager getInstance() {
        synchronized (VideoCallManager.class) {
            if (null == mInstance) {
                mInstance = new VideoCallManager();
            }
        }
        return mInstance;
    }

    /**
     * 初始构造
     */
    public VideoCallManager() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 更新用户状态
     * @param callStatus
     */
    public VideoCallManager setCallStatus(CallStatus callStatus){
        this.mCallStatus=callStatus;
        return mInstance;
    }

    /**
     * 添加新的视频通话监听器
     * @param listener
     * @return
     */
    public VideoCallManager addCallStateListener(OnCallStateListener listener){
        this.mCallStateListener=listener;
        return mInstance;
    }

    /**
     * 移除视频通话监听器
     * @return
     */
    public VideoCallManager removeCallStateListener(){
        this.mCallStateListener=null;
        return mInstance;
    }

    /**
     * 添加视频通话状态监听器
     * @param listener
     */
    public VideoCallManager addCallMessageListener(OnCallMessageListener listener) {
        this.mCallMessageListener =listener;
        return mInstance;
    }

    /**
     * 移除视频通话状态监听器
     */
    public VideoCallManager deleteCallMessageListener() {
        this.mCallMessageListener =null;
        return mInstance;
    }

    /**
     * 设置视频来电呼入超时时长
     * @param timeOut 单位 毫秒
     */
    public VideoCallManager setCallInTimeOut(long timeOut){
        this.CALL_TIME_IN =timeOut;
        return mInstance;
    }

    /**
     * 设置视频来电呼出超时时长
     * @param timeOut 单位 毫秒
     */
    public VideoCallManager setCallOutTimeOut(long timeOut){
        this.CALL_TIME_OUT=timeOut;
        return mInstance;
    }

    /**
     * 防止意外发生，当调用此方法，应立即关闭视频通话窗口
     * @param callID
     * @return
     */
    public VideoCallManager onError(String callID) {
        if(null!=mCallMessageListener)mCallMessageListener.onCallError(LiveConstant.CALL_STATE_NOIMAL,callID,TextUtils.isEmpty(mToUserID)?"":mToUserID);
        return mInstance;
    }

    /**
     * 发起视频通话请求
     * @param toUserID 接受人ID
     * @param callExtraInfo 建立通话必要参数
     */
    public void makeCall(String toUserID, CallExtraInfo callExtraInfo) {
        this.mToUserID=toUserID;
        sendCallRequest(LiveConstant.VIDEO_CALL_CMD_MACKCALL,toUserID,callExtraInfo,"请求视频通话");
        //超时处理
        getHandler().removeCallbacks(timeOutRunnable);
        getHandler().postDelayed(timeOutRunnable,CALL_TIME_OUT);
    }

    /**
     * 取消、拒绝视频通话请求
     * @param toUserID 接受人ID
     * @param msgContent 通信内容
     */
    public void cancelCall(String toUserID,String msgContent) {
        onCancelTimeOutAction();
        sendCallRequest(LiveConstant.VIDEO_CALL_CMD_REJECT,toUserID,null,msgContent);
        setCallStatus(CallStatus.CALL_FREE);//更新为空闲状态
    }

    /**
     * 呼叫过程中信令传输
     * @param toUserID 接受人ID
     * @param msgContent 通信内容
     */
    public void statePost(String toUserID,String msgContent) {
        sendCallRequest(LiveConstant.VIDEO_CALL_CMD_POST,toUserID,null,msgContent);
    }

    /**
     * 视频通话超时未接、取消
     * 
     * @param toUserID 接受人ID
     * @param msgContent 通信内容
     */
    public void timeOut(String toUserID,String msgContent) {
        sendCallRequest(LiveConstant.VIDEO_CALL_CMD_TIMOUT,toUserID,null,msgContent);
    }

    /**
     * 超时事件取消
     */
    public VideoCallManager onCancelTimeOutAction(){
        getHandler().removeCallbacks(timeOutRunnable);
        mCallExtraInfo=null;
        return mInstance;
    }

    /**
     * 获取异常信息
     * @param errorCode
     * @param scene 场景 0：视频通话 1：直播间
     * @return
     */
    public String getErrorCodeMsg(int errorCode,int scene) {
        String msg="视频通话失败";
        switch (errorCode) {
            //HTTP连接超时
            case QNErrorCode.ERROR_HTTP_SOCKET_TIMEOUT:
                msg="请求视频通话被关闭,请检查网络连接";
                break;
            //TOKE无效
            case QNErrorCode.ERROR_TOKEN_INVALID:
                msg="通话信息已过期";
                break;
            //状态异常
            case QNErrorCode.ERROR_WRONG_STATUS:
                msg="请求视频通话被关闭,请检查网络连接";
                break;
            //HTTP响应失败
            case QNErrorCode.ERROR_HTTP_RESPONSE_EXCEPTION:
                msg="请求视频通话被关闭,请检查网络连接";
                break;
            //ICE通信失败
            case QNErrorCode.ERROR_ICE_FAILED:
                msg="请求视频通话被关闭,请检查网络连接";
                break;
            case QNErrorCode.ERROR_PEERCONNECTION:
                msg="对方网络异常，视频通话已结束";
                break;
            //TOKE已经过期
            case QNErrorCode.ERROR_TOKEN_EXPIRED:
                msg="通话信息已过期";
                break;
            //房间已关闭
            case QNErrorCode.ERROR_ROOM_INSTANCE_CLOSED:
                msg="对方结束了视频通话";
                break;
            //reconnect token 错误(过期等)
            case QNErrorCode.ERROR_RECONNECT_TOKEN_ERROR:
                msg="请求视频通话失败";
                break;
            //被T出房间
            case QNErrorCode.ERROR_KICKED_OUT_OF_ROOM:
                msg="对方结束了视频通话";
                break;
            //房间不存在
            case QNErrorCode.ERROR_ROOM_NOT_EXIST:
                msg="对方结束了视频通话";
                break;
            //重复进入房间
            case QNErrorCode.ERROR_PLAYER_ALREADY_EXIST:
                msg=0==scene?"通话信息验证失败":"直播重复";
                break;
            //订阅流不存在
            case QNErrorCode.ERROR_SUBSCRIBE_STREAM_NOT_EXIST:
                msg=0==scene?"通话信息验证失败":"订阅流不存在";
                break;
            //流重复订阅
            case QNErrorCode.ERROR_SUBSCRIBE_STREAM_ALREADY_EXIST:
                msg="视频通话异常";
                break;
            //鉴权失败
            case QNErrorCode.ERROR_NO_PERMISSION:
                msg="通话信息验证失败";
                break;
            //服务器不可用
            case QNErrorCode.ERROR_SERVER_UNAVAILABLE:
                msg="通话服务不可用";
                break;
            //ROOM TOKEN失败
            case QNErrorCode.ERROR_TOKEN_ERROR:
                msg="通话信息验证失败";
                break;

        }
        return msg;
    }

    /**
     * 视频通话信令消息发送
     * @param cmd 通话信令
     * @param toUserID 接收人
     * @param contentMsg 文字交互内容
     */
    public void sendCallRequest(final String cmd, String toUserID, CallExtraInfo callExtraInfo, String contentMsg) {
        if(TextUtils.isEmpty(toUserID)){
            if(null!= mCallMessageListener) mCallMessageListener.onCallError(LiveConstant.CALL_STATE_NOIMAL,"对方用户ID不能为空",toUserID);
            return;
        }
        CallCmdExtra cmdExtra= new CallCmdExtra();
        if(null!=callExtraInfo){
            //视频通话发起时间带上时间戳，接听方过滤幽灵来电
            if(cmd.equals(LiveConstant.VIDEO_CALL_CMD_MACKCALL)){
                Logger.d(TAG,"CMD:"+cmd);
                cmdExtra.setCtime(System.currentTimeMillis());
            }
            cmdExtra.setSenderRoomToken(callExtraInfo.getSenderRoomToken());
            cmdExtra.setReceiverRoomToken(callExtraInfo.getReceiverRoomToken());
            cmdExtra.setPrice(callExtraInfo.getPrice());
            cmdExtra.setAvatar(UserManager.getInstance().getAvatar());
            cmdExtra.setName(UserManager.getInstance().getNickname());
            cmdExtra.setUid(UserManager.getInstance().getUserId());
            cmdExtra.setRid(callExtraInfo.getRecevierID());
            cmdExtra.setRecall(TextUtils.isEmpty(callExtraInfo.getRecevierID())?false:true);
            cmdExtra.setRoomid(callExtraInfo.getRoomID());
        }else{
            cmdExtra.setUid(UserManager.getInstance().getUserId());
        }
        cmdExtra.setCmd(cmd);
        cmdExtra.setContent(contentMsg);
        try {
            CommonJson<CallCmdExtra> request = new CommonJson<>();
            request.cmd = LiveConstant.VIDEO_CALL_CMD;//视频通话信令
            request.data = cmdExtra;
            String content = new Gson().toJson(request, new TypeToken<CommonJson<CallCmdExtra>>() {}.getType());
            TIMMessage customMessage = new TIMMessage();
            TIMCustomElem elem = new TIMCustomElem();
            elem.setData(content.getBytes());
            customMessage.addElement(elem);
            //只发送在线消息
            TIMManager.getInstance().getConversation(TIMConversationType.C2C, toUserID).sendMessage(customMessage, new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    if(null!= mCallMessageListener){
                        String msg=getErrorMessage(i);
                        mCallMessageListener.onCallError(i,msg,toUserID);
                    }
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    if(null!= mCallMessageListener){
                        mCallMessageListener.onCallCmdSendOK(cmd);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if(null!= mCallMessageListener){
                mCallMessageListener.onCallError(LiveConstant.CALL_STATE_NOIMAL,e.getMessage(),toUserID);
            }
        }
    }

    /**
     * 自定义消息发送
     * @param customMsgInfo
     * @param toUserID
     * @param listener
     */
    public void sendCustomMessage(CustomMsgInfo customMsgInfo, String toUserID,OnCustomMessageListener listener){
        sendCustomMessage(Constant.MSG_CUSTOM_ROOM_SYSTEM,customMsgInfo,toUserID,false,listener);
    }


    /**
     * 自定义在线消息发送
     * @param customMsgInfo
     * @param toUserID
     * @param listener
     */
    public void sendCustomOnlineMessage(CustomMsgInfo customMsgInfo, String toUserID,OnCustomMessageListener listener){
        sendCustomMessage(Constant.MSG_CUSTOM_ROOM_SYSTEM,customMsgInfo,toUserID,true,listener);
    }


    /**
     * C2C自定义礼物消息内容发送
     * @param toUserID
     * @param customMsgInfo
     * @param listener
     */
    public void sendCustomGiftMessage(CustomMsgInfo customMsgInfo, String toUserID,OnCustomMessageListener listener){
        sendCustomMessage(LiveConstant.MSG_CUSTOM_ROOM_PRIVATE_GIFT,customMsgInfo,toUserID,false,listener);
    }

    /**
     * C2C自定义在线礼物消息内容发送
     * @param toUserID
     * @param customMsgInfo
     * @param listener
     */
    public void sendCustomOnlineGiftMessage(CustomMsgInfo customMsgInfo, String toUserID,OnCustomMessageListener listener){
        sendCustomMessage(LiveConstant.MSG_CUSTOM_ROOM_PRIVATE_GIFT,customMsgInfo,toUserID,true,listener);
    }

    /**
     * C2C自定义消息发送
     * @param toUserID 接收人
     * @param customMsgInfo 自定义消息体
     * @param isOnline 是否是在线消息
     * @param listener
     */
    public void sendCustomMessage(String cmd,CustomMsgInfo customMsgInfo, String toUserID,boolean isOnline,OnCustomMessageListener listener){
        if(TextUtils.isEmpty(toUserID)){
            if(null!= listener) listener.onError(-1,"对方用户ID不能为空");
            return;
        }
        try {
            CommonJson<CustomMsgInfo> request = new CommonJson<>();
            request.cmd = cmd;
            request.data = customMsgInfo;
            String content = new Gson().toJson(request, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
            TIMMessage customMessage = new TIMMessage();
            TIMCustomElem elem = new TIMCustomElem();
            elem.setData(content.getBytes());
            customMessage.addElement(elem);
            if(isOnline){
                //只发送在线消息
                TIMManager.getInstance().getConversation(TIMConversationType.C2C, toUserID).sendOnlineMessage(customMessage, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        if(null!= listener){
                            String msg=getErrorMessage(i);
                            listener.onError(i,msg);
                        }
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        if(null!= listener) listener.onSendOk(customMsgInfo);
                        if(null!=mCallMessageListener) mCallMessageListener.onCustomMessage(customMsgInfo,false);
                    }
                });
            }else{
                //只发送在线消息
                TIMManager.getInstance().getConversation(TIMConversationType.C2C, toUserID).sendMessage(customMessage, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        if(null!= listener){
                            String msg=getErrorMessage(i);
                            listener.onError(i,msg);
                        }
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        if(null!= listener) listener.onSendOk(customMsgInfo);
                        if(null!=mCallMessageListener) mCallMessageListener.onCustomMessage(customMsgInfo,false);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(null!= listener){
                listener.onError(-1,e.getMessage());
            }
        }
    }

    /**
     * 根据错误码返回对应错误消息
     * 参考 https://cloud.tencent.com/document/product/269/1671
     * @param code
     * @return
     */
    public String getErrorMessage(int code) {
        String msg="呼叫失败";
        switch (code) {
            case 6004:
                msg="呼叫失败，请检查您的登陆状态";
                break;
            case 6011:
                msg="呼叫失败，对方用户不存在";
                break;
            case 6012:
                msg="呼叫超时，请检查网络连接";
                break;
            case 6013:
                msg="账户未初始化成功，请退出账号重新登陆";
                break;
            case 6014:
                msg="账户未登陆成功，请退出账号重新登陆";
                break;
            case 91000:
                msg="内部服务器错误，请重试";
                break;
        }
        return msg;
    }

    /**
     * 超时处理
     */
    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            //接听方60秒未接听回复呼叫方超时拒接
            if(null!= mCallExtraInfo){
                Logger.d(TAG,"接听方回复超时未接");
                timeOut(mCallExtraInfo.getToUserID(),"超时未接听");
                setCallStatus(CallStatus.CALL_FREE);//更新接听方为空闲状态
                MakeCallManager.getInstance().endCall(mCallExtraInfo.getCallUserID(),mCallExtraInfo.getCallAnchorID(),mCallExtraInfo.getRecevierID(),MakeCallManager.getInstance().getIdType(mCallExtraInfo.getCallUserID()),null);
                if(null!= mCallStateListener) mCallStateListener.onCallError(null==mCallExtraInfo?"":mCallExtraInfo.getRoomID(),LiveConstant.CALL_STATE_TIMEOUT,"超时未接听");
                mCallExtraInfo=null;
                return;
            }
            //呼叫方70秒未收到接听放成功建立连接的信令
            if(null!=mCallMessageListener) mCallMessageListener.onCallError(LiveConstant.CALL_STATE_TIMEOUT,"视频通话超时",mToUserID);
        }
    };

    /**
     * 新的消息下发
     * @param message
     */
    public void onNewMessage(TIMMessage message) {
        Logger.d(TAG,"onNewMessage:--1");
        if(null==message) return;
        if (message.status() == TIMMessageStatus.HasDeleted) {
            return;
        }
        if (null!=message.getConversation()&&message.getConversation().getType() == TIMConversationType.System) {
            return;
        }
        Logger.d(TAG,"onNewMessage:--2");
        if(message.getElementCount()>0){
            TIMElem element = message.getElement(0);
            if(null!=element&&element.getType()==Custom){
                TIMCustomElem customElem = (TIMCustomElem) element;
                if(null!=customElem&&null!=customElem.getData()){
                    String result = new String(customElem.getData());
                    Logger.d(TAG,"onNewMessage:--3");
                    CommonJson<Object> commonUserJson = new Gson().fromJson(result, new TypeToken<CommonJson<Object>>() {}.getType());
                    if(!TextUtils.isEmpty(commonUserJson.cmd)){
                        if(LiveConstant.VIDEO_CALL_CMD.equals(commonUserJson.cmd)){
                            CommonJson<CallCmdExtra> messageCommonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CallCmdExtra>>() {}.getType());
                            newCallMessage(message.getSender(),messageCommonJson.data);
                        }else if(Constant.MSG_CUSTOM_ROOM_SYSTEM.equals(commonUserJson.cmd)){
                            CommonJson<CustomMsgInfo> messageCommonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            if(null!=mCallMessageListener) mCallMessageListener.onCustomMessage(messageCommonJson.data,true);
                        }else if(LiveConstant.MSG_CUSTOM_ROOM_PRIVATE_GIFT.equals(commonUserJson.cmd)){
                            CommonJson<CustomMsgInfo> messageCommonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                            if(null!=mCallMessageListener) mCallMessageListener.onCustomMessage(messageCommonJson.data,true);
                        }else if(Constant.MESSAGE_PRIVATE_CUSTOM_WAKEUP.equals(commonUserJson.cmd)){
                            String json = new Gson().toJson(commonUserJson.data);
                            CustomMsgCall customMsgInfo = new Gson().fromJson(json, CustomMsgCall.class);
                            if(null!= mCallStateListener) mCallStateListener.onNewWakeCall(customMsgInfo);
                        }
                    }
                }
            }
        }
    }

    /**
     * 新的视频通话相关消息
     * @param message 消息体
     * @param sender 发送人
     */
    private void newCallMessage(String sender,CallCmdExtra message) {
        if(null!=message){
            switch (message.getCmd()) {
                //新的视频通话邀请
                case LiveConstant.VIDEO_CALL_CMD_MACKCALL:
                    long ctime = message.getCtime();//开始呼叫时间
                    long conTime = System.currentTimeMillis()-ctime;//建立通话耗时
                    String content = "建立视频通话通信耗时："+conTime+"毫秒,主播状态："+getState()+(conTime>5500?"，视频通话已过期":"视频通话未过期");
                    LogRecordUtils.getInstance().postCallLogs("收到视频通话邀请",message.getName(),0,System.currentTimeMillis(),0,content);
                    if(conTime>55000){
                        return;
                    }
                    //超时注册
                    getHandler().removeCallbacks(timeOutRunnable);
                    getHandler().postDelayed(timeOutRunnable,CALL_TIME_IN);//超时拒接
                    setCallStatus(CallStatus.CALL_ANSWERING);//更新用户状态为应答中
                    //包装业务场景参数
                    newCallFramtExtra(message);
                    break;
                //对方取消、拒绝了视频通话邀请
                case LiveConstant.VIDEO_CALL_CMD_REJECT:
                    //本地来电通知取消
                    if(null!= mCallStateListener &&null!=mCallExtraInfo){
                        mCallStateListener.onCallError(mCallExtraInfo.getRoomID(),LiveConstant.CALL_STATE_CANCEL,TextUtils.isEmpty(message.getContent())?"取消了视频通话":message.getContent());
                    }
                    Logger.d(TAG,"对方取消、拒绝了视频通话邀请");
                    //通话房间内被取消
                    if(null!=mCallMessageListener) mCallMessageListener.onCallError(LiveConstant.CALL_STATE_CANCEL,TextUtils.isEmpty(message.getContent())?"取消了视频通话":message.getContent(),sender);
                    break;
                //对方超时未接听、建立视频通话超时
                case LiveConstant.VIDEO_CALL_CMD_TIMOUT:
                    if(null!=mCallMessageListener) mCallMessageListener.onCallError(LiveConstant.CALL_STATE_TIMEOUT,TextUtils.isEmpty(message.getContent())?"视频通话超时":message.getContent(),sender);
                    break;
                //对方占线
                case LiveConstant.VIDEO_CALL_CMD_BEBUSY:
                    if(null!=mCallMessageListener) mCallMessageListener.onCallError(LiveConstant.CALL_STATE_BEBUSY,TextUtils.isEmpty(message.getContent())?"占线中":message.getContent(),sender);
                    break;
                //对方已设置为离线状态
                case LiveConstant.VIDEO_CALL_CMD_OFFLINE:
                    if(null!=mCallMessageListener) mCallMessageListener.onCallError(LiveConstant.CALL_STATE_OFFLINE,TextUtils.isEmpty(message.getContent())?"已离线":message.getContent(),sender);
                    break;
                //通话过程握手信令交互
                case LiveConstant.VIDEO_CALL_CMD_POST:
                    if(null!=mCallMessageListener) mCallMessageListener.onStatePost(LiveConstant.CALL_STATE_POST,TextUtils.isEmpty(message.getContent())?"接通中,请稍后...":message.getContent(),sender);
                    break;
            }
        }
    }

    private Handler getHandler() {
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    private String getState() {
        String content="其他未知";
        switch (mCallStatus) {
            case CALL_OFFLINE:
                content="设置为离线状态";
                break;
            case CALL_FREE:
                content="空闲中";
                break;
            case CALL_ANSWERING:
                content="应答中";
                break;
            case CALL_CONVERSE:
                content="通话中";
                break;
            case CALL_MORE:
                content="其他未知";
                break;
        }
        return content+(VideoCallManager.getInstance().isBebusying()?"，主播正处于忙碌状态":"主播正处于非忙碌状态");
    }

    /**
     * 解析来电信令参数，解析包装本地业务必要参数
     * @param message
     */
    private void newCallFramtExtra(CallCmdExtra message) {
        if(null!= mCallStateListener &&null!=message){
            mCallExtraInfo=null;
            mCallExtraInfo=new CallExtraInfo();
            CallExtraInfo callExtraInfo=new CallExtraInfo();
            //对方信息
            callExtraInfo.setToUserID(message.getUid());
            callExtraInfo.setToAvatar(message.getAvatar());
            callExtraInfo.setToNickName(message.getName());
            callExtraInfo.setContent(message.getContent());
            //通话必要参数
            callExtraInfo.setRoomID(message.getRoomid());
            callExtraInfo.setPrice(message.getPrice());
            callExtraInfo.setRecevierID(message.getRid());
            callExtraInfo.setSenderRoomToken(message.getSenderRoomToken());
            callExtraInfo.setReceiverRoomToken(message.getReceiverRoomToken());
            callExtraInfo.setSystemTime(message.getCtime());
            callExtraInfo.setCtime(message.getCtime());

            mCallExtraInfo.setToUserID(message.getUid());
            mCallExtraInfo.setToAvatar(message.getAvatar());
            mCallExtraInfo.setToNickName(message.getName());
            mCallExtraInfo.setContent(message.getContent());
            mCallExtraInfo.setRoomID(message.getRoomid());
            mCallExtraInfo.setPrice(message.getPrice());
            mCallExtraInfo.setRecevierID(message.getRid());
            mCallExtraInfo.setSenderRoomToken(message.getSenderRoomToken());
            mCallExtraInfo.setReceiverRoomToken(message.getReceiverRoomToken());
            mCallExtraInfo.setSystemTime(message.getCtime());
            mCallExtraInfo.setCtime(message.getCtime());

            //确定用户、主播身份
            changedCallIdentify(callExtraInfo,message);
            changedCallIdentify(mCallExtraInfo,message);
            final Activity runActivity = ForegroundManager.getInstance().getRunActivity();
            //结束可能正在显示的结算页
            if(null!=runActivity&&runActivity instanceof TopBaseActivity){
                ((TopBaseActivity) runActivity).resetCallState();
            }
            if(runActivity instanceof VerticalAnchorPlayerAvtivity
                    || runActivity instanceof LiveRoomPullActivity
                    || runActivity instanceof AsmrRoomPullActivity
                    || runActivity instanceof VerticalVideoPlayerAvtivity){
                runActivity.finish();
            }
            //主播端不再校验正在忙碌
//            if(VideoCallManager.getInstance().isBebusying()) {
//                Logger.d(TAG,"newCallFramtExtra--正在忙碌");
//                return;
//            }
            //屏幕已上锁
            KeyguardManager km = (KeyguardManager) AppEngine.getApplication().getApplicationContext().getSystemService(android.content.Context.KEYGUARD_SERVICE);
            if(km.inKeyguardRestrictedInputMode()){
                LogRecordUtils.getInstance().postCallLogs("新的视频通话请求--3",message.getName(),0,System.currentTimeMillis(),0,"用户屏幕已上锁");
                CLNotificationManager.getInstance().sendCallNotification(AppEngine.getApplication().getApplicationContext(),callExtraInfo,CLNotificationManager.NOTIFICATION_ID_CALL);
                return;
            }
            //此处不下发通话事件，内部直接处理
//            if(null!= mCallStateListener) mCallStateListener.onNewCall(callExtraInfo);
            callExtraInfo.setCallType(0);
            callExtraInfo.setEnterIdentify(1);
            LiveCallHelper.getInstance().setCallData(callExtraInfo);
            try {
                Intent intent=new Intent(AppEngine.getApplication().getApplicationContext(),LiveCallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppEngine.getApplication().getApplicationContext().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                LogRecordUtils.getInstance().postCallLogs("打开LiveCallActivity失败",message.getName(),0,System.currentTimeMillis(),0,e.getMessage());
            }
        }
    }

    /**
     * 转换身份
     * @param callExtraInfo
     * @param message
     */
    public void changedCallIdentify(CallExtraInfo callExtraInfo, CallCmdExtra message) {
        if(null==callExtraInfo||null==message) return;
        callExtraInfo.setCallUserID(TextUtils.isEmpty(callExtraInfo.getRecevierID())?message.getUid():UserManager.getInstance().getUserId());
        callExtraInfo.setCallAnchorID(TextUtils.isEmpty(callExtraInfo.getRecevierID())?UserManager.getInstance().getUserId():message.getUid());
    }

    /**
     * 通话销毁重置
     */
    public void onReset(){
        if(null!=mHandler){
            mHandler.removeCallbacks(timeOutRunnable);
            mHandler=null;
        }
        mCallMessageListener=null;
    }

    /**
     * 全局销毁
     */
    public void onDestroy() {
        if(null!=mHandler) mHandler.removeCallbacks(timeOutRunnable);
        mCallMessageListener=null;mHandler=null;mCallStateListener =null;
    }
}