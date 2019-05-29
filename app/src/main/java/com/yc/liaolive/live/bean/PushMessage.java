package com.yc.liaolive.live.bean;

/**
 * TinyHung@Outlook.com
 * 2018/8/8
 * 推流的消息下发
 */

public class PushMessage {

    private String cmd="";
    private String roomID;
    private String sendUserID;
    private String message;
    private int foregroundState;

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getSendUserID() {
        return sendUserID;
    }

    public void setSendUserID(String sendUserID) {
        this.sendUserID = sendUserID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getForegroundState() {
        return foregroundState;
    }

    public void setForegroundState(int foregroundState) {
        this.foregroundState = foregroundState;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "PushMessage{" +
                "cmd='" + cmd + '\'' +
                ", roomID='" + roomID + '\'' +
                ", sendUserID='" + sendUserID + '\'' +
                ", message='" + message + '\'' +
                ", foregroundState=" + foregroundState +
                '}';
    }
}
