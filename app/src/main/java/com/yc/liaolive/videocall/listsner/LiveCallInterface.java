package com.yc.liaolive.videocall.listsner;

import com.yc.liaolive.videocall.bean.CallExtraInfo;

/**
 * TinyHung@Outlook.com
 * 2019/2/28
 */

public interface LiveCallInterface {

    String TAG = "LiveCallInterface";

    /**
     * 初始化
     * @param callUserInfo 通话参数
     * @param userIndetity 通话者身份
     */
    void onCreate(CallExtraInfo callUserInfo, int userIndetity);

    /**
     * 功能事件监听器
     * @param onFunctionListener
     */
    void setOnFunctionListener(OnFunctionListener onFunctionListener);

    /**
     * 新的额消息
     * @param message
     */
    void setNewMessage(String message);

    /**
     * 销毁
     */
    void onDestroy();


    abstract class OnFunctionListener{
        //接听
        public void onAcceptCall(){}
        //挂断
        public void onRejectCall(){}
    }
}
