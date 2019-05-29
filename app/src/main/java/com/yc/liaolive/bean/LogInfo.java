package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/8/9
 * 日志信息
 */

public class LogInfo implements Serializable{

    private String userid;//用户标识
    private String brand;//品牌
    private String model;//型号
    private int sdkInt;//API 版本
    private String versionName;//APP版本
    private String imeil;//设备号
    private int networkType;//网络环境
    private String requstUrl;//请求报错的URL
    private int errCode;//报错Code
    private String errMessage;//错误信息
    private long totalTime;
    private long requstTime;
    private String state;


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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getSdkInt() {
        return sdkInt;
    }

    public void setSdkInt(int sdkInt) {
        this.sdkInt = sdkInt;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getImeil() {
        return imeil;
    }

    public void setImeil(String imeil) {
        this.imeil = imeil;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
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

    @Override
    public String toString() {
        return "LogInfo{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", userid='" + userid + '\'' +
                ", sdkInt=" + sdkInt +
                ", versionName='" + versionName + '\'' +
                ", imeil='" + imeil + '\'' +
                ", networkType=" + networkType +
                ", requstUrl='" + requstUrl + '\'' +
                ", errCode=" + errCode +
                ", errMessage='" + errMessage + '\'' +
                '}';
    }
}
