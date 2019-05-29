package com.yc.liaolive.live.bean;

/**
 * TinyHung@Outlook.com
 * 2018/7/5
 * 视频通话订单
 */

public class VideoCallOlder {

    /**
     * acceptid : 21907019
     * addtime : 1530778629
     * chat_deplete : 5
     * chat_minute : 5
     * id : 32
     * pintai_coin : 0
     * rmb_coin : 300
     * status : 0
     * userid : 24811645
     */

    private String acceptid;
    private int addtime;
    private int chat_deplete;
    private int chat_minute;
    private String id;
    private int pintai_coin;
    private int rmb_coin;
    private int status;
    private String userid;

    public String getAcceptid() {
        return acceptid;
    }

    public void setAcceptid(String acceptid) {
        this.acceptid = acceptid;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public int getChat_minute() {
        return chat_minute;
    }

    public void setChat_minute(int chat_minute) {
        this.chat_minute = chat_minute;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPintai_coin() {
        return pintai_coin;
    }

    public void setPintai_coin(int pintai_coin) {
        this.pintai_coin = pintai_coin;
    }

    public int getRmb_coin() {
        return rmb_coin;
    }

    public void setRmb_coin(int rmb_coin) {
        this.rmb_coin = rmb_coin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
