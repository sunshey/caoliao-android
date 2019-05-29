package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/11/7
 * 开播
 */

public class CreateRoomInfo implements Parcelable{

    /**
     * push_stream_url : rtmp://1120.push.whlshd.com/live/1120_37907299?txSecret=bb7081ca348cb3fd021ed1922ccf5eb2&txTime=5C274E31
     * room_id : room_1545993393774_596f
     * room_token : {"room_name":"hd37907299","tokens":"c5Nf7eqMjq6ugrGJ-m52WHFIhb_iTjzFH54vwsaj:_-V96oAsb3htVX5CvoRaql_-wpQ=:eyJhcHBJZCI6ImR2azNsaHZ3NCIsInVzZXJJZCI6IjM3OTA3Mjk5Iiwicm9vbU5hbWUiOiJoZDM3OTA3Mjk5IiwicGVybWlzc2lvbiI6InVzZXIiLCJleHBpcmVBdCI6MTU0NTk5Njk5M30="}
     */

    private String push_stream_url;
    private String room_id;
    /**
     * room_name : hd37907299
     * tokens : c5Nf7eqMjq6ugrGJ-m52WHFIhb_iTjzFH54vwsaj:_-V96oAsb3htVX5CvoRaql_-wpQ=:eyJhcHBJZCI6ImR2azNsaHZ3NCIsInVzZXJJZCI6IjM3OTA3Mjk5Iiwicm9vbU5hbWUiOiJoZDM3OTA3Mjk5IiwicGVybWlzc2lvbiI6InVzZXIiLCJleHBpcmVBdCI6MTU0NTk5Njk5M30=
     */

    private RoomTokenBean room_token;

    protected CreateRoomInfo(Parcel in) {
        push_stream_url = in.readString();
        room_id = in.readString();
        room_token = in.readParcelable(RoomTokenBean.class.getClassLoader());
    }

    public static final Creator<CreateRoomInfo> CREATOR = new Creator<CreateRoomInfo>() {
        @Override
        public CreateRoomInfo createFromParcel(Parcel in) {
            return new CreateRoomInfo(in);
        }

        @Override
        public CreateRoomInfo[] newArray(int size) {
            return new CreateRoomInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(push_stream_url);
        dest.writeString(room_id);
        dest.writeParcelable(room_token, flags);
    }

    public String getPush_stream_url() {
        return push_stream_url;
    }

    public void setPush_stream_url(String push_stream_url) {
        this.push_stream_url = push_stream_url;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public RoomTokenBean getRoom_token() {
        return room_token;
    }

    public void setRoom_token(RoomTokenBean room_token) {
        this.room_token = room_token;
    }
}
