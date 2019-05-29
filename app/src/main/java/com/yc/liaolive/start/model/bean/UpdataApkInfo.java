package com.yc.liaolive.start.model.bean;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017-07-02 13:31
 * 版本更新
 */

public class UpdataApkInfo  implements Serializable{

    /**
     * down_url : http://z.197754.com/app_0.5.12.apk
     * size : 21.22
     * update_log : 支付流程优化 反馈的一些BUG修复  直播间赠送礼物动画、奢侈动画、礼物面板选中动画优化1
     * version : 0.5.12
     * version_code : 27
     */

    private String down_url;
    private String size;
    private String update_log;
    private String version;
    private int version_code;
    private int compel_update;//0：非强制更新 >0：强制更新
    private int wifi_auto_down; //Wi-Fi下是否自动下载 0自动下载， 1不自动下载

    private boolean alreadyDownload = false; //是否已经下载完成

    public UpdataApkInfo(){
        super();
    }

    public String getDown_url() {
        return down_url;
    }

    public void setDown_url(String down_url) {
        this.down_url = down_url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public int getCompel_update() {
        return compel_update;
    }

    public void setCompel_update(int compel_update) {
        this.compel_update = compel_update;
    }

    public int getWifi_auto_down() {
        return wifi_auto_down;
    }

    public void setWifi_auto_down(int wifi_auto_down) {
        this.wifi_auto_down = wifi_auto_down;
    }

    public boolean isAlreadyDownload() {
        return alreadyDownload;
    }

    public void setAlreadyDownload(boolean alreadyDownload) {
        this.alreadyDownload = alreadyDownload;
    }
}
