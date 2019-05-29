package com.yc.liaolive.live.listener;

import com.yc.liaolive.live.bean.PushMessage;

/**
 * TinyHung@Outlook.com
 * 2018/12/5
 * 直播间消息
*/
public interface LiveRoomActionListener {

    /**
     * 直播间纯文本消息
     * @param groupID
     * @param senderID 发送人信息
     * @param sendNickname
     * @param sendhead
     * @param sendVipLeve
     * @param sendGender
     * @param sendUserType
     * @param messageContent 消息内容
     */
    void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendVipLeve ,long sendGender,long sendUserType,String messageContent);

    /**
     * 直播间自定义消息
     * @param roomID 房间ID
     * @param userID 发送者ID
     * @param userName 发送者昵称
     * @param userAvatar 发送者头像
     * @param cmd 自定义cmd
     * @param message 自定义消息内容
     */
    void onRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, String message);

    /**
     * 直播间系统消息
     * @param sender 发送人
     * @param message 消息体
     * @param groupID 接收者
     */
    void onRoomSystemMsg(String groupID, String sender, String message);

    /**
     * 直播间人数变化消息
     * @param groupId 裙子ID
     * @param sender 发送人
     * @param toJson 消息体
     */
    void onRoomNumberSystemMsg(String groupId, String sender, String toJson);

    /**
     * 直播间主播端的自定义消息
     * @param pushMessage
     */
    void onRoomPushMessage(PushMessage pushMessage);

    /**
     * 来自直播间的C2C消息
     * @param sendID 发送人
     * @param message
     */
    void onC2CCustomMessage(String sendID, String message);

    /**
     * 直播间收到房间解散通知
     * @param roomID 房间ID
     */
    void onRoomClosed(String roomID);

    /**
     * 直播间日志回调
     * @param log 日志内容
     */
    void onDebugLog(String log);

    /**
     * 直播间所有错误日志回调
     * @param errorCode 错误码
     * @param errorMessage 错误描述
     */
    void onError(int errorCode, String errorMessage);
}
