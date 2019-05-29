package com.yc.liaolive.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/6/26
 * 视频通话消息体封装
 */

public class CallNetBody implements Parcelable{

    public String type;
    public String userID;
    public String roomID;//房间号
    public String netCmd;//指令
    public String userName;
    public String userAvatar;
    public String message;//内容
    public String roomExtra;//自定义消息

    public CallNetBody(){

    }

    protected CallNetBody(Parcel in) {
        type = in.readString();
        userID = in.readString();
        roomID = in.readString();
        netCmd = in.readString();
        userName = in.readString();
        userAvatar = in.readString();
        message = in.readString();
        roomExtra = in.readString();
    }

    public static final Creator<CallNetBody> CREATOR = new Creator<CallNetBody>() {
        @Override
        public CallNetBody createFromParcel(Parcel in) {
            return new CallNetBody(in);
        }

        @Override
        public CallNetBody[] newArray(int size) {
            return new CallNetBody[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(userID);
        dest.writeString(roomID);
        dest.writeString(netCmd);
        dest.writeString(userName);
        dest.writeString(userAvatar);
        dest.writeString(message);
        dest.writeString(roomExtra);
    }

    @Override
    public String toString() {
        return "CallNetBody{" +
                "type='" + type + '\'' +
                ", userID='" + userID + '\'' +
                ", roomID='" + roomID + '\'' +
                ", netCmd='" + netCmd + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", message='" + message + '\'' +
                ", roomExtra='" + roomExtra + '\'' +
                '}';
    }
}
