package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/10/26
 * 日志
 */

public class CallLogInfo {

    private String userid;
    private String anchorid;
    private String version;
    private int sendKbps;
    private int recvKbps;
    private double sendRate;
    private double recvRate;
    private int appCPURate;
    private int sysCPURate;
    private long netSpeedDown;
    private long netSpeedUp;

    public long getNetSpeedDown() {
        return netSpeedDown;
    }

    public void setNetSpeedDown(long netSpeedDown) {
        this.netSpeedDown = netSpeedDown;
    }

    public long getNetSpeedUp() {
        return netSpeedUp;
    }

    public void setNetSpeedUp(long netSpeedUp) {
        this.netSpeedUp = netSpeedUp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAnchorid() {
        return anchorid;
    }

    public void setAnchorid(String anchorid) {
        this.anchorid = anchorid;
    }

    public int getSendKbps() {
        return sendKbps;
    }

    public void setSendKbps(int sendKbps) {
        this.sendKbps = sendKbps;
    }

    public int getRecvKbps() {
        return recvKbps;
    }

    public void setRecvKbps(int recvKbps) {
        this.recvKbps = recvKbps;
    }

    public double getSendRate() {
        return sendRate;
    }

    public void setSendRate(double sendRate) {
        this.sendRate = sendRate;
    }

    public double getRecvRate() {
        return recvRate;
    }

    public void setRecvRate(double recvRate) {
        this.recvRate = recvRate;
    }

    public int getAppCPURate() {
        return appCPURate;
    }

    public void setAppCPURate(int appCPURate) {
        this.appCPURate = appCPURate;
    }

    public int getSysCPURate() {
        return sysCPURate;
    }

    public void setSysCPURate(int sysCPURate) {
        this.sysCPURate = sysCPURate;
    }
}
