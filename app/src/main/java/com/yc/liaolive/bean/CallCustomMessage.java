package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/11/14
 * 视频通话自定义消息
 */

public class CallCustomMessage {

    /**
     * AVRoomID : 1745707162
     * CallDate : 1542170487544
     * CallType : 2
     * CallUUID : 531_10593690_1542170390
     * Sender : 10593690
     * UserAction : 0
     */

    private int AVRoomID;//群组ID，通话ID
    private long CallDate;
    private int CallType;
    private String CallUUID;
    private String Sender;
    private String CustomParam;//自定义消息字段
    private int UserAction;

    public int getAVRoomID() {
        return AVRoomID;
    }

    public void setAVRoomID(int AVRoomID) {
        this.AVRoomID = AVRoomID;
    }

    public long getCallDate() {
        return CallDate;
    }

    public void setCallDate(long CallDate) {
        this.CallDate = CallDate;
    }

    public int getCallType() {
        return CallType;
    }

    public void setCallType(int CallType) {
        this.CallType = CallType;
    }

    public String getCallUUID() {
        return CallUUID;
    }

    public void setCallUUID(String CallUUID) {
        this.CallUUID = CallUUID;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public int getUserAction() {
        return UserAction;
    }

    public void setUserAction(int UserAction) {
        this.UserAction = UserAction;
    }
}
