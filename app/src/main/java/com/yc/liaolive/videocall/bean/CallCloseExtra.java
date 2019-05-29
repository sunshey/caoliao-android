package com.yc.liaolive.videocall.bean;

/**
 * TinyHung@Outlook.com
 * 2019/1/8
 * 视频通话结算必要信息
 */

public class CallCloseExtra {

    private String roomID;//房间ID，通话ID
    private String toUserID;//对方用户ID
    private String toNickName;//对方昵称
    private String toAvatar;//对方头像
    private int idType;//发起人身份
    private String closeMsg;//关闭提示

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public String getToAvatar() {
        return toAvatar;
    }

    public void setToAvatar(String toAvatar) {
        this.toAvatar = toAvatar;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getCloseMsg() {
        return closeMsg;
    }

    public void setCloseMsg(String closeMsg) {
        this.closeMsg = closeMsg;
    }

    @Override
    public String toString() {
        return "CallCloseExtra{" +
                "roomID='" + roomID + '\'' +
                ", toUserID='" + toUserID + '\'' +
                ", toNickName='" + toNickName + '\'' +
                ", toAvatar='" + toAvatar + '\'' +
                ", idType=" + idType +
                ", closeMsg='" + closeMsg + '\'' +
                '}';
    }
}
