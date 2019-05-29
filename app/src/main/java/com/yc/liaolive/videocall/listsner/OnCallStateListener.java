package com.yc.liaolive.videocall.listsner;

import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.videocall.bean.CallExtraInfo;

/**
 * TinyHung@Outlook.com
 * 2018/12/21
 * 视频通话状态监听
 */

public interface OnCallStateListener {

    /**
     * 新的视频通话
     * @param callMessage
     */
    void onNewCall(CallExtraInfo callMessage);

    /**
     * 新的视频通话推送
     * @param customMsgInfo
     */
    void onNewWakeCall(CustomMsgCall customMsgInfo);
    
    /**
     * 其他错误、超时未接
     * @param callID
     * @param errorCode
     * @param errorMsg
     */
    void onCallError(String callID,int errorCode,String errorMsg);
}
