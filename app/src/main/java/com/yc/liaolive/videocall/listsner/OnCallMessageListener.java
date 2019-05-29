package com.yc.liaolive.videocall.listsner;

import com.yc.liaolive.live.bean.CustomMsgInfo;

/**
 * TinyHung@Outlook.com
 * 2018/12/21
 * 视频通话建立状态监听
 */

public interface OnCallMessageListener {
    
    /**
     * CMD发送成功
     * @param cmd
     */
    void onCallCmdSendOK(String cmd);

    /**
     * 新的自定义消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    void onCustomMessage(CustomMsgInfo customMsgInfo,boolean isSystemPro);

    /**
     * 通话握手信令交互
     * @param message 消息体
     * @param toUserID 对方用户ID
     * @param code 内部消息类别
     */
    void onStatePost(int code,String message,String toUserID);

    /**
     * 通话错误，所有状态参阅：LiveConstant.CALL_STATE_ 常量
      * @param errorCode
     * @param errorMsg
     * @param toUserID 对方用户ID
     */
    void onCallError(int errorCode, String errorMsg,String toUserID);
}
