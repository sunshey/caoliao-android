package com.yc.liaolive.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/31
 */

public class ServerBean implements Serializable{

    private List<ServerListBean> server_list;
    /**
     * server_avatar : http://a.197754.com/uploads/head_img/kftx.png
     * server_nickname : 在线客服
     * server_desc : 投诉、申请售后及订单异常请联系我们
     */
    private String server_identify;
    private String server_avatar;
    private String server_nickname;
    private String server_desc;

    public String getServer_identify() {
        return server_identify;
    }

    public void setServer_identify(String server_identify) {
        this.server_identify = server_identify;
    }

    public List<ServerListBean> getServer_list() {
        return server_list;
    }

    public void setServer_list(List<ServerListBean> server_list) {
        this.server_list = server_list;
    }

    public String getServer_avatar() {
        return server_avatar;
    }

    public void setServer_avatar(String server_avatar) {
        this.server_avatar = server_avatar;
    }

    public String getServer_nickname() {
        return server_nickname;
    }

    public void setServer_nickname(String server_nickname) {
        this.server_nickname = server_nickname;
    }

    public String getServer_desc() {
        return server_desc;
    }

    public void setServer_desc(String server_desc) {
        this.server_desc = server_desc;
    }
}
