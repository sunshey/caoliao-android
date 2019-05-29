package com.yc.liaolive.videocall.listsner;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/22
 * 自定义消息监听
 */

public interface OnCustomMessageListener {
    /**
     * 发送消息OK
     * @param object
     */
    void onSendOk(Object object);

    /**
     * 发送消息失败
     * @param code
     * @param msg
     */
    void onError(int code,String msg);
}
