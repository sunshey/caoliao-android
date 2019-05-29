package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/10/26
 */

public class AppConfigInfo {

    private String node_id="resver";
    private String node_url;//下载地址
    private String site_id;//站点ID 如5577
    private String soft_id;//软件ID 如235688.apk

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getSoft_id() {
        return soft_id;
    }

    public void setSoft_id(String soft_id) {
        this.soft_id = soft_id;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getNode_url() {
        return node_url;
    }

    public void setNode_url(String node_url) {
        this.node_url = node_url;
    }

    @Override
    public String toString() {
        return "AppConfigInfo{" +
                "node_id='" + node_id + '\'' +
                ", node_url='" + node_url + '\'' +
                '}';
    }
}
