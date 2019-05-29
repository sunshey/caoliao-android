package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/9/27
 */

public class VideoRoomInfo {

    private String play_url_flv;
    private String play_url_m3u8;
    private String play_url_rtmp;
    private String roomid;

    public VideoRoomInfo(){
        super();
    }

    public String getPlay_url_flv() {
        return play_url_flv;
    }

    public void setPlay_url_flv(String play_url_flv) {
        this.play_url_flv = play_url_flv;
    }

    public String getPlay_url_m3u8() {
        return play_url_m3u8;
    }

    public void setPlay_url_m3u8(String play_url_m3u8) {
        this.play_url_m3u8 = play_url_m3u8;
    }

    public String getPlay_url_rtmp() {
        return play_url_rtmp;
    }

    public void setPlay_url_rtmp(String play_url_rtmp) {
        this.play_url_rtmp = play_url_rtmp;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }
}
