package com.yc.liaolive.media.bean;

/**
 * TinyHung@Outlook.com
 * 2018/11/20
 * 多媒体礼物动画
 */

public class MediaGiftInfo {

    private String cmd;
    //接收人
    private String accept_nikcname;
    private String accept_userid;
    private String gift_count;
    private String gift_id;
    private String gift_src;
    private String gift_big_svga;
    private String gift_title;
    //赠送人
    private String avatar;
    private String nikcname;
    private String userid;
    private String vip;
    private int drawTimes;//中奖倍数

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getAccept_nikcname() {
        return accept_nikcname;
    }

    public void setAccept_nikcname(String accept_nikcname) {
        this.accept_nikcname = accept_nikcname;
    }

    public String getAccept_userid() {
        return accept_userid;
    }

    public void setAccept_userid(String accept_userid) {
        this.accept_userid = accept_userid;
    }

    public String getGift_count() {
        return gift_count;
    }

    public void setGift_count(String gift_count) {
        this.gift_count = gift_count;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getGift_src() {
        return gift_src;
    }

    public void setGift_src(String gift_src) {
        this.gift_src = gift_src;
    }

    public String getGift_title() {
        return gift_title;
    }

    public void setGift_title(String gift_title) {
        this.gift_title = gift_title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNikcname() {
        return nikcname;
    }

    public void setNikcname(String nikcname) {
        this.nikcname = nikcname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public int getDrawTimes() {
        return drawTimes;
    }

    public void setDrawTimes(int drawTimes) {
        this.drawTimes = drawTimes;
    }

    public String getGift_big_svga() {
        return gift_big_svga;
    }

    public void setGift_big_svga(String gift_big_svga) {
        this.gift_big_svga = gift_big_svga;
    }
}
