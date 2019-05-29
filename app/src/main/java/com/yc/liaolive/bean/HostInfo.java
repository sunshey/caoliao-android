package com.yc.liaolive.bean;


import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/27
 */

public class HostInfo implements Serializable{

    private List<String> black_url_list;

    private String domain;
    /**
     * server_identify : 23584694
     * server_list : [{"name":"呆呆","identify":"hty_Yuye"},{"name":"WX","identify":"hty_Yuye"}]
     */

    private ServerBean server;
    private int homeIndex;
    //主页模块
    List<FragmentMenu> menus;
    //消息模块是否可用
    private int pri_msg_show=1; //0:不可用 1：可用

    /**
     * 支付宝支付 0 显示有中转页的方式 1使用直接支付的方式。
     */
    private int alipay_interim = 0;
    /**
     * img_src : http://a.197754.com/upload/bg1.jpg
     * upd_time : 1
     */
    private SplashBgBean splash_bg;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ServerBean getServer() {
        return server;
    }

    public void setServer(ServerBean server) {
        this.server = server;
    }

    public int getHomeIndex() {
        return homeIndex;
    }

    public void setHomeIndex(int homeIndex) {
        this.homeIndex = homeIndex;
    }

    public SplashBgBean getSplash_bg() {
        return splash_bg;
    }

    public void setSplash_bg(SplashBgBean splash_bg) {
        this.splash_bg = splash_bg;
    }

    public int getAlipay_interim() {
        return alipay_interim;
    }

    public void setAlipay_interim(int alipay_interim) {
        this.alipay_interim = alipay_interim;
    }

    public static class SplashBgBean implements Serializable{
        private String img_src;
        private int upd_time;

        public String getImg_src() {
            return img_src;
        }

        public void setImg_src(String img_src) {
            this.img_src = img_src;
        }

        public int getUpd_time() {
            return upd_time;
        }

        public void setUpd_time(int upd_time) {
            this.upd_time = upd_time;
        }
    }

    public List<FragmentMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<FragmentMenu> menus) {
        this.menus = menus;
    }

    public int getPri_msg_show() {
        return pri_msg_show;
    }

    public void setPri_msg_show(int pri_msg_show) {
        this.pri_msg_show = pri_msg_show;
    }
    public List<String> getBlack_url_list() {
        return black_url_list;
    }

    public void setBlack_url_list(List<String> black_url_list) {
        this.black_url_list = black_url_list;
    }
}
