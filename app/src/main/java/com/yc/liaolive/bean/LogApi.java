package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/8/9
 * 日志信息
 */

public class LogApi {

    private String requstUrl;//请求报错的URL
    private int errCode;//报错Code
    private String errMessage;//错误信息
    private long totalTime;
    private long requstTime;
    private long currentTime;
    private String state;
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRequstUrl() {
        return requstUrl;
    }

    public void setRequstUrl(String requstUrl) {
        this.requstUrl = requstUrl;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getRequstTime() {
        return requstTime;
    }

    public void setRequstTime(long requstTime) {
        this.requstTime = requstTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "LogApi{" +
                "requstUrl='" + requstUrl + '\'' +
                ", errCode=" + errCode +
                ", errMessage='" + errMessage + '\'' +
                ", totalTime=" + totalTime +
                ", requstTime=" + requstTime +
                ", currentTime=" + currentTime +
                ", state='" + state + '\'' +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
