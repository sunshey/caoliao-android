package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/12/28
 */

public class RoomTokenBean implements Parcelable{

    /**
     * room_name : hd37907299
     * tokens : c5Nf7eqMjq6ugrGJ-m52WHFIhb_iTjzFH54vwsaj:_-V96oAsb3htVX5CvoRaql_-wpQ=:eyJhcHBJZCI6ImR2azNsaHZ3NCIsInVzZXJJZCI6IjM3OTA3Mjk5Iiwicm9vbU5hbWUiOiJoZDM3OTA3Mjk5IiwicGVybWlzc2lvbiI6InVzZXIiLCJleHBpcmVBdCI6MTU0NTk5Njk5M30=
     */

    private String room_name;
    private String tokens;

    protected RoomTokenBean(Parcel in) {
        room_name = in.readString();
        tokens = in.readString();
    }

    public static final Creator<RoomTokenBean> CREATOR = new Creator<RoomTokenBean>() {
        @Override
        public RoomTokenBean createFromParcel(Parcel in) {
            return new RoomTokenBean(in);
        }

        @Override
        public RoomTokenBean[] newArray(int size) {
            return new RoomTokenBean[size];
        }
    };

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(room_name);
        dest.writeString(tokens);
    }
}
