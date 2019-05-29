package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/7/13
 */

public class OlderInfo {


    private long pintai_coin;
    private long rmb_coin;
    private long addtime;
    private long edittime;
    private long pintai_coin_total;
    private long pintai_money;
    private long pintai_money_total;
    private long rmb_coin_total;
    private long rmb_money;
    private long rmb_money_total;
    private String userid;

    public long getPintai_coin() {
        return pintai_coin;
    }

    public void setPintai_coin(long pintai_coin) {
        this.pintai_coin = pintai_coin;
    }

    public long getRmb_coin() {
        return rmb_coin;
    }

    public void setRmb_coin(long rmb_coin) {
        this.rmb_coin = rmb_coin;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getEdittime() {
        return edittime;
    }

    public void setEdittime(long edittime) {
        this.edittime = edittime;
    }

    public long getPintai_coin_total() {
        return pintai_coin_total;
    }

    public void setPintai_coin_total(long pintai_coin_total) {
        this.pintai_coin_total = pintai_coin_total;
    }

    public long getPintai_money() {
        return pintai_money;
    }

    public void setPintai_money(long pintai_money) {
        this.pintai_money = pintai_money;
    }

    public long getPintai_money_total() {
        return pintai_money_total;
    }

    public void setPintai_money_total(long pintai_money_total) {
        this.pintai_money_total = pintai_money_total;
    }

    public long getRmb_coin_total() {
        return rmb_coin_total;
    }

    public void setRmb_coin_total(long rmb_coin_total) {
        this.rmb_coin_total = rmb_coin_total;
    }

    public long getRmb_money() {
        return rmb_money;
    }

    public void setRmb_money(long rmb_money) {
        this.rmb_money = rmb_money;
    }

    public long getRmb_money_total() {
        return rmb_money_total;
    }

    public void setRmb_money_total(long rmb_money_total) {
        this.rmb_money_total = rmb_money_total;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "OlderInfo{" +
                "pintai_coin=" + pintai_coin +
                ", rmb_coin=" + rmb_coin +
                ", addtime=" + addtime +
                ", edittime=" + edittime +
                ", pintai_coin_total=" + pintai_coin_total +
                ", pintai_money=" + pintai_money +
                ", pintai_money_total=" + pintai_money_total +
                ", rmb_coin_total=" + rmb_coin_total +
                ", rmb_money=" + rmb_money +
                ", rmb_money_total=" + rmb_money_total +
                ", userid='" + userid + '\'' +
                '}';
    }
}
