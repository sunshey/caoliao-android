package com.yc.liaolive.videocall.bean;

/**
 * TinyHung@Outlook.com
 * 2018/12/22
 */

public class ChatTokenBean {

    private String room_name;
    private Object tokens;

    public Object getTokens() {
        return tokens;
    }

    public void setTokens(Object tokens) {
        this.tokens = tokens;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    @Override
    public String toString() {
        return "ChatTokenBean{" +
                "room_name='" + room_name + '\'' +
                ", tokens='" + tokens + '\'' +
                '}';
    }
}
