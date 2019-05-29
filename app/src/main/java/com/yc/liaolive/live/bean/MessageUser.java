package com.yc.liaolive.live.bean;

/**
 * TinyHung@Outlook.com
 * 2018/12/7
 */

public class MessageUser {

    public String nickName;//发送者用户昵称
    public String headPic;//发送者用户头像
    public String userID;//用户ID
    public int userGradle;//用户等级

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getUserGradle() {
        return userGradle;
    }

    public void setUserGradle(int userGradle) {
        this.userGradle = userGradle;
    }

    @Override
    public String toString() {
        return "MessageUser{" +
                "nickName='" + nickName + '\'' +
                ", headPic='" + headPic + '\'' +
                ", userID='" + userID + '\'' +
                ", userGradle=" + userGradle +
                '}';
    }
}
