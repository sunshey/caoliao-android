package com.yc.liaolive.live.bean;

/**
 * TinyHung@Outlook.com
 * 2018/12/5
 */

public class CustomMessageInfo {

    public String userName;
    public String userAvatar;
    public String cmd;
    public String msg;
    public String userID;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "CustomMessageInfo{" +
                "userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", cmd='" + cmd + '\'' +
                ", msg='" + msg + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
