package com.yc.liaolive.base;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.music.player.lib.manager.MusicWindowManager;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.bean.NumberChangedInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.PushMessage;
import com.yc.liaolive.live.bean.RoomOutBean;
import com.yc.liaolive.live.im.IMMessageMgr;
import com.yc.liaolive.live.listener.LiveRoomActionListener;
import com.yc.liaolive.live.listener.TXPhoneStateListener;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.live.ui.dialog.LiveDetailsDialog;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.manager.ScreenLayoutChangedHelp;
import com.yc.liaolive.media.manager.VideoAudioManager;
import com.yc.liaolive.ui.activity.LiteBindPhoneActivity;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.ui.dialog.InputKeyBoardDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.videocall.manager.VideoCallManager;

/**
 * TinyHung@outlook.com
 * 2017/3/19 14:51
 * 直播间
 */

public abstract class BaseLiveActivity < C extends RoomBaseController> extends TopBaseActivity implements LiveRoomActionListener{

    protected C mController;//直播间控制器,方便子类各自实现不同的控制器功能
    protected static final String TAG = BaseLiveActivity.class.getSimpleName();
    protected InputKeyBoardDialog mInputTextMsgDialog;
    //电话打断
    private PhoneStateListener mPhoneListener = null;
    protected ScreenLayoutChangedHelp mLayoutChangedListener;//屏幕监听
    protected boolean isDestroy=false;
    protected int videoEncodeWidth =480;//编码输出分辨率
    protected int videoEncodeHeight =848;
    private LiveDetailsDialog mNewInstance;

    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    protected Context getContext() {
        return this;
    }

    /**
     * 返回继承人的身份
     * @return 0: 观看者 1：表演者
     */
    protected abstract int getExtendsClassIdentify();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        VideoCallManager.getInstance().setBebusying(true);
        VideoAudioManager.getInstance().getAudioManager(getApplicationContext()).requestAudioFocus();
        MusicWindowManager.getInstance().onInvisible();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
    }

    /**
     * 推流交互控制器初始化
     */
    protected void initRoomViews() {
        isDestroy=false;
        //初始化直播间控制器
        RelativeLayout videoController = (RelativeLayout) findViewById(R.id.video_controller);
        videoController.addView(mController);
        //主播端初始化的内容
        if(getExtendsClassIdentify()==LiveRoom.USER_IDENTITY_PUSH){
            mPhoneListener = new TXPhoneStateListener(LiveRoomManager.getInstance().getLiveRoom());
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        //软键盘高度检测
        mLayoutChangedListener= ScreenLayoutChangedHelp.get(BaseLiveActivity.this).setOnSoftKeyBoardChangeListener(new ScreenLayoutChangedHelp.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if(null!=mController) mController.showInputKeyBord(true,height);
            }

            @Override
            public void keyBoardHide(int height) {
                if(null!=mController) mController.showInputKeyBord(false,height);
            }
        });
    }

    /**
     * 发消息弹出框
     */
    protected void showInputMsgDialog() {
        if(null==mInputTextMsgDialog){
            //输入框
            mInputTextMsgDialog = InputKeyBoardDialog.getInstance(this)
                    .setBackgroundWindown(0.0f)
                    .setHintText("请输入聊天内容")
                    .setMode(1)
                    .setOnActionFunctionListener(new InputKeyBoardDialog.OnActionFunctionListener() {
                        //提交发射
                        @Override
                        public void onSubmit(String content, boolean tanmuOpen) {
                            sendTextMessage(content,tanmuOpen);
                        }
                        //告知输入框是否可发射状态
                        @Override
                        public boolean isAvailable() {
                            if(UserManager.getInstance().isVerificationPhone()&&TextUtils.isEmpty(UserManager.getInstance().getPhone())){
                                LiteBindPhoneActivity.start(AppEngine.getApplication().getApplicationContext());
                                return false;
                            }
                            return true;
                        }
                    });
            mInputTextMsgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(null!=mController) mController.showInputKeyBord(false,0);
                }
            });
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth()); //设置宽度
            mInputTextMsgDialog.getWindow().setAttributes(lp);
            mInputTextMsgDialog.setCancelable(true);
            mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        mInputTextMsgDialog.show();
    }

    /**
     * 发送自定义消息
     * @param msgInfo
     */
    protected void sendCustomMessage(final CustomMsgInfo msgInfo){
        if(null==msgInfo.getCmd()) return;
        if(null!=msgInfo){
            //本地自定义礼物消息体
            if(TextUtils.equals(Constant.MSG_CUSTOM_GIFT,msgInfo.getCmd().get(0))){
                newSystemCustomMessage(msgInfo,false);
                return;
            }
            String content=new Gson().toJson(msgInfo);
            LiveRoomManager.getInstance().getLiveRoom().sendRoomCustomMsg(msgInfo.getCmd().get(0), content, new IMMessageMgr.Callback() {
                @Override
                public void onError(int code, String errInfo) {
                    LogApi logInfo=new LogApi();
                    logInfo.setRequstUrl("发送群组自定义消息");
                    logInfo.setErrMessage(errInfo);
                    logInfo.setErrCode(code);
                    ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
                    actionLogInfo.setData(logInfo);
                    UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_GROUP_MESSAGE,actionLogInfo,null);
                    if(!BaseLiveActivity.this.isFinishing()){
                        String errorMsg="";
                        if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_ROOM){
                            errorMsg="你已被群管理员禁止发言";
                        }else if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_APP){
                            errorMsg="你已被管理员禁止全平台发言";
                        }
                        if(!TextUtils.isEmpty(errorMsg)){
                            QuireDialog.getInstance(BaseLiveActivity.this).setTitleText("发送失败").setContentText(errorMsg).setSubmitTitleText("确定").setCancelTitleText("关闭").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                                @Override
                                public void onConsent() {
                                }

                                @Override
                                public void onRefuse() {

                                }
                            }).show();
                        }
                    }
                }

                @Override
                public void onSuccess(Object... args) {
                    //渲染至本地消息弹幕列表
                    if(!BaseLiveActivity.this.isFinishing()){
                        newSystemCustomMessage(msgInfo,false);
                    }
                }
            });
        }
    }

    /**
     * 发送纯文本消息
     * @param content 纯文本内容
     * @param isDanmu 是否是弹幕消息
     * 敏感过滤示例：自定义：$ 官方 |
     * 足球玩法$$足球玩法|习近平|习近平   MESSAGE:我们的足球玩法是习近平教的
     */
    protected void sendTextMessage(String content, boolean isDanmu){
        if(null==content) return;
        LiveRoomManager.getInstance().getLiveRoom().sendGroupTextMessage(content, isDanmu,new IMMessageMgr.Callback() {
            @Override
            public void onError(int code, String errInfo) {
                if(!BaseLiveActivity.this.isFinishing()){
                    if(code==Constant.ROOM_CUSTOMMSG_CODE_DANGER){
                        CustomMsgInfo customMsgInfo=new CustomMsgInfo(0);
                        customMsgInfo.setChildCmd(Constant.MSG_CUSTOM_ERROR);
                        customMsgInfo.setMsgContent("因消息包含敏感信息未发送成功");
                        onNewTextMessage(customMsgInfo,false);
                        return;
                    }
                    String errorMsg=errInfo;
                    if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_ROOM){
                        errorMsg="你已被群管理员禁止本房间发言！";
                    }else if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_APP){
                        errorMsg="你已被管理员禁止全平台发言！";
                    }else if(code==Constant.ROOM_CUSTOMMSG_CODE_DANGER){
                        errorMsg="发送的文本内容包含敏感词汇！";
                    }
                    if(code!=Constant.ROOM_CUSTOMMSG_CODE_DANGER){
                        LogApi logInfo=new LogApi();
                        logInfo.setRequstUrl("发送群组自定义消息");
                        logInfo.setErrMessage(errorMsg);
                        logInfo.setErrCode(code);
                        ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
                        actionLogInfo.setData(logInfo);
                        UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_GROUP_MESSAGE,actionLogInfo,null);
                    }
                    if(!TextUtils.isEmpty(errorMsg)){
                        QuireDialog.getInstance(BaseLiveActivity.this).setTitleText("发送失败").setContentText(errorMsg).setSubmitTitleText("确定").setCancelTitleText("关闭").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent() {
                            }

                            @Override
                            public void onRefuse() {

                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onSuccess(Object... args) {
                //渲染至本地消息弹幕列表
                if(null!=args&&args.length>0){
                    Object arg = args[0];
                    if(arg instanceof TIMMessage&&!BaseLiveActivity.this.isFinishing()){
                        TIMMessage timMessage= (TIMMessage) arg;
                        TIMElem element = timMessage.getElement(0);
                        if(element.getType()== TIMElemType.Text){
                            TIMTextElem timTextElem= (TIMTextElem) element;
                            onNewTextMessage(timTextElem.getText());
                        }
                    }
                }
            }
        });
    }

    /**
     * 收到新的消息交给控制器统一处理刷新
     * 播放礼物动画
     * @param customMsgInfo
     * @param isSystemPro   是否来系统推送的
     */
    protected void newSystemCustomMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if (null == customMsgInfo) return;
        if (null != mController) mController.newSystemCustomMessage(customMsgInfo, isSystemPro);
    }

    /**
     * 纯文本消息
     */
    protected synchronized void onNewTextMessage(String content) {
        CustomMsgInfo customMsgInfo=new CustomMsgInfo(0);
        customMsgInfo.setChildCmd(Constant.MSG_CUSTOM_TEXT);
        customMsgInfo.setMsgContent(content);
        onNewTextMessage(customMsgInfo,false);
    }

    /**
     * 新的自定义消息
     * @param customMsgInfo
     * @param isSystemPro 是否来自远端
     */
    private void onNewTextMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro){
        if (null != mController) mController.onNewTextMessage(customMsgInfo,isSystemPro);
    }

    /**
     * 群组纯文本消息
     * @param groupID
     * @param senderID 发送人信息
     * @param sendNickname
     * @param sendhead
     * @param sendLeve
     * @param sendGender
     * @param sendUserType
     * @param messageContent 消息内容
     */
    @Override
    public void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendLeve ,long sendGender,long sendUserType,String messageContent) {
        if(null!=messageContent){
            CustomMsgInfo customMsgInfo=new CustomMsgInfo();
            customMsgInfo.setChildCmd(Constant.MSG_CUSTOM_TEXT);
            customMsgInfo.setSendUserID(senderID);
            customMsgInfo.setSendUserName(sendNickname);
            customMsgInfo.setSendUserHead(sendhead);
            customMsgInfo.setSendUserGradle(0);
            customMsgInfo.setSendUserVIP((int) sendLeve);
            customMsgInfo.setSendUserGradle((int) sendGender);
            customMsgInfo.setSendUserType((int) sendUserType);
            customMsgInfo.setMsgContent(messageContent);
            onNewTextMessage(customMsgInfo,true);
        }
    }

    /**
     * 群组中，用户发送的自定义消息
     * @param roomID 房间ID
     * @param userID 发送者ID
     * @param userName 发送者昵称
     * @param userAvatar 发送者头像
     * @param cmd 自定义cmd
     * @param message 自定义消息内容
     */
    @Override
    public void onRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, String message) {
        try {
            CustomMsgInfo liveChatMsgInfo = new Gson().fromJson(message, CustomMsgInfo.class);
            liveChatMsgInfo.setAccapGroupID(roomID);
            newSystemCustomMessage(liveChatMsgInfo, true);
        } catch (RuntimeException e) {}
    }

    /**
     * 新的系统消息
     * @param sender
     * @param message
     * @param groupID
     */
    @Override
    public void onRoomSystemMsg(String groupID, String sender, String message) {
        try {
            CustomMsgInfo liveChatMsgInfo = new Gson().fromJson(message, CustomMsgInfo.class);
            liveChatMsgInfo.setAccapGroupID(groupID);
            newSystemCustomMessage(liveChatMsgInfo, true);
        } catch (RuntimeException e) {}
    }

    /**
     * 房间人数变化
     * @param groupId 裙子ID
     * @param sender 发送人
     * @param toJson 消息体
     */
    @Override
    public void onRoomNumberSystemMsg(String groupId, String sender, String toJson) {
        if(!TextUtils.isEmpty(toJson)){
            NumberChangedInfo changedInfo = new Gson().fromJson(toJson, NumberChangedInfo.class);
            if(null!=mController) mController.onNewMinMessage(groupId,sender,changedInfo);
        }
    }

    @Override
    public void onDebugLog(String log) {}
    @Override
    public void onRoomClosed(String roomID) {}
    @Override
    public void onError(final int errorCode, final String errorMessage) {}
    @Override
    public void onRoomPushMessage(PushMessage pushMessage) {}
    @Override
    public void onC2CCustomMessage(String sendID, String message) {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(getExtendsClassIdentify()==LiveRoom.USER_IDENTITY_PUSH){
            showComfirmDialog("正在直播，确定要结束并退出直播间吗？","直播已结束",true);
        }else{
            backRoom(true);
        }
    }

    /**
     * 观众端-退出直播间
     */
    protected void backRoom(boolean isExit) {
        LiveRoomManager.getInstance().getLiveRoom().exitRoom(null);
        LiveRoomManager.getInstance().getLiveRoom().stopLive(false);
        if(isExit) finish();
    }

    /**
     * 主播端-显示确认消息
     * @param quiteTips 提示内容
     * @param errorMsg 结算界面提示内容
     * @param showTipsDialog 是否走退出对话框
     */
    public void showComfirmDialog(final String quiteTips, final String errorMsg, boolean showTipsDialog) {
        if(!BaseLiveActivity.this.isFinishing()){
            if(showTipsDialog){
                QuireDialog.getInstance(BaseLiveActivity.this)
                        .setTitleText("系统提示")
                        .setContentText(quiteTips)
                        .setSubmitTitleText("确定")
                        .setCancelTitleText("取消")
                        .setDialogCanceledOnTouchOutside(true)
                        .setDialogCancelable(true)
                        .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent() {
                                showProgressDialog("操作中，请稍后...", true);
                                exitPublishState(errorMsg);
                            }

                            @Override
                            public void onRefuse() {

                            }
                        }).show();
                return;
            }
            exitPublishState(errorMsg);
        }
    }

    /**
     * 主播端-结束推流状态
     * @param errorMsg
     */
    protected void exitPublishState(final String errorMsg) {
        showErrorAndQuit(0,errorMsg);
    }

    protected void showErrorAndQuit(int errorCode, final String errorMsg) {
        if(-2==errorCode){
            //网络糟糕
            if(!BaseLiveActivity.this.isFinishing()) CommenNoticeDialog.getInstance(BaseLiveActivity.this).setTipsData("网络提示",errorMsg,"确定").show();
            return;
        }
        exitRoom(new LiveRoom.ExitRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                closeProgressDialog();
                RoomOutBean roomOutBean=null;
                if(null!=mController){
                    roomOutBean=new RoomOutBean();
                    roomOutBean.setDuration(DateUtil.durtionFormat(mController.getSecond()));
                }
                quiteDestroyRoom(roomOutBean,errorMsg);
            }

            @Override
            public void onSuccess(Object data) {
                closeProgressDialog();
                RoomOutBean roomOutBean=null;
                if(!BaseLiveActivity.this.isFinishing()){
                    if(null!=data&&data instanceof  RoomOutBean){
                        roomOutBean=(RoomOutBean) data;
                    }
                }
                quiteDestroyRoom(roomOutBean,errorMsg);
            }
        });
    }


    /**
     * 主播端-房间销毁回收
     * @param roomOutBean
     * @param errorMsg
     */
    private void quiteDestroyRoom(RoomOutBean roomOutBean,String errorMsg) {
        LiveRoomManager.getInstance().getLiveRoom().stopLive(true);
        if(null!=mController) {
            mController.stopReckonTime();
            mController.onDestroy();
            mController=null;
        }
        if(null!=roomOutBean){
            mNewInstance = LiveDetailsDialog.newInstance(BaseLiveActivity.this,
                    UserManager.getInstance().getUserId(), LiveDetailsDialog.SCENE_MODE_PLAYER, roomOutBean,errorMsg);
            mNewInstance.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            mNewInstance.show();
            return;
        }
        finish();
    }

    /**
     * 主播端-关闭并退出、销毁 直播间
     * @param callback
     */
    protected void exitRoom(LiveRoom.ExitRoomCallback callback) {
        LiveRoomManager.getInstance().getLiveRoom().exitRoom(callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VideoAudioManager.getInstance().releaseAudioFocus();
        if (ConfigSet.getInstance().isAudioOpenWindown()) {
            MusicWindowManager.getInstance().onVisible();
        }
        isDestroy=true;
        if(null!=mInputTextMsgDialog){
            mInputTextMsgDialog.dismiss();
            mInputTextMsgDialog=null;
        }
        if(null!=mNewInstance){
            mNewInstance.dismiss();
            mNewInstance=null;
        }
        LiveRoomManager.getInstance().getLiveRoom().setLiveRoomListener(null);
        LiveRoomManager.getInstance().getLiveRoom().onDestroy();
        VideoCallManager.getInstance().setBebusying(false);
        if(null!= mLayoutChangedListener) {
            mLayoutChangedListener.onDestroy();
            mLayoutChangedListener=null;
        }
        if (mPhoneListener != null) {
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            mPhoneListener = null;
        }
    }
}
