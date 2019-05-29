package com.yc.liaolive.live.listener;

import com.yc.liaolive.live.bean.PushMessage;

/**
 * TinyHung@Outlook.com
 * 2019/1/15
 * 房间群组消息
 */

public interface GroupIMMessageListener {

    /**
     * IM连接成功
     */
    void onConnected();

    /**
     * IM断开连接
     */
    void onDisconnected();

    /**
     * 收到群文本消息
     */
    void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendVipLeve ,long sendGender,long sendUserType,String messageContent);

    /**
     * 收到自定义的群消息
     */
    void onGroupCustomMessage(String groupID, String senderID, String message);

    /**
     * 收到自定义的C2C消息
     */
    void onC2CCustomMessage(String sendID, String message);

    /**
     * IM群组销毁回调
     */
    void onGroupDestroyed(final String groupID);

    /**
     * 日志回调
     */
    void onDebugLog(String log);

    /**
     * 房间内群系统消息
     *
     * @param sender
     * @param message
     * @param groupID 接收者
     */
    void onGroupSystemMsg(String groupID, String sender, String message);

    /**
     * 系统的人数变化消息
     * @param groupId
     * @param sender
     * @param toJson
     */
    void onGroupSystemMsgNumber(String groupId, String sender, String toJson);

    /**
     * 直播间新的自定义消息
     * @param pushMessage
     */
    void onRoomPushMessage(PushMessage pushMessage);
}
