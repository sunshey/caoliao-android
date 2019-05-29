package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/11/1
 */

public class ActionLogInfo <T> {

    private String agent_id;//渠道
    private String imeil;//设备号
    private String version;//APP版本号
    private String brand;//品牌
    private String model;//型号
    private int networkType;//网络环境  1:WIFI 0:移动网络
    private int actionType;//类型
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getImeil() {
        return imeil;
    }

    public void setImeil(String imeil) {
        this.imeil = imeil;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }
}
