package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/11/21
 * 视频通话呼叫 唤醒
 */

public class CustomMsgCall implements Parcelable{

    /**
     * id : 1
     * anchorId : 123456
     * anchorAvatar : http://
     * anchorNickName :
     * desc : 发来一个视频邀请
     * file_path : http://123.mp4
     * file_img : http://123.png
     * chat_deplete : 1000
     */

    private int id;
    private String anchorId;
    private String callUserID;
    private String anchorAvatar;
    private String anchorNickName;
    private String desc;
    private String file_path;
    private String file_img;
    private int chat_deplete;
    private long send_time;
    private String exprie_time="";//过期时间
    private long time_out;//超时关闭时间 0：不限制


    protected CustomMsgCall(Parcel in) {
        id = in.readInt();
        anchorId = in.readString();
        callUserID = in.readString();
        anchorAvatar = in.readString();
        anchorNickName = in.readString();
        desc = in.readString();
        file_path = in.readString();
        file_img = in.readString();
        chat_deplete = in.readInt();
        send_time = in.readLong();
        exprie_time = in.readString();
        time_out = in.readLong();
    }

    public static final Creator<CustomMsgCall> CREATOR = new Creator<CustomMsgCall>() {
        @Override
        public CustomMsgCall createFromParcel(Parcel in) {
            return new CustomMsgCall(in);
        }

        @Override
        public CustomMsgCall[] newArray(int size) {
            return new CustomMsgCall[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(anchorId);
        dest.writeString(callUserID);
        dest.writeString(anchorAvatar);
        dest.writeString(anchorNickName);
        dest.writeString(desc);
        dest.writeString(file_path);
        dest.writeString(file_img);
        dest.writeInt(chat_deplete);
        dest.writeLong(send_time);
        dest.writeString(exprie_time);
        dest.writeLong(time_out);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getCallUserID() {
        return callUserID;
    }

    public void setCallUserID(String callUserID) {
        this.callUserID = callUserID;
    }

    public String getAnchorAvatar() {
        return anchorAvatar;
    }

    public void setAnchorAvatar(String anchorAvatar) {
        this.anchorAvatar = anchorAvatar;
    }

    public String getAnchorNickName() {
        return anchorNickName;
    }

    public void setAnchorNickName(String anchorNickName) {
        this.anchorNickName = anchorNickName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_img() {
        return file_img;
    }

    public void setFile_img(String file_img) {
        this.file_img = file_img;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public String getExprie_time() {
        return exprie_time;
    }

    public void setExprie_time(String exprie_time) {
        this.exprie_time = exprie_time;
    }

    public long getTime_out() {
        return time_out;
    }

    public void setTime_out(long time_out) {
        this.time_out = time_out;
    }
}
