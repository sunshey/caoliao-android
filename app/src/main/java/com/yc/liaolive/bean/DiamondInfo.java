package com.yc.liaolive.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;

/**
 * Created by wanglin  on 2018/7/10 14:45.
 */
public class DiamondInfo implements MultiItemEntity {

    private int itemType;
    /**
     * hidden : 0
     * to_avatar : http://t.tn990.com/uploads/head_img/16f2873afed0bddd!380x240.jpg
     * to_nickname : 时光的旧
     * to_userid : 11278848
     * wawa : 0
     */

    private int hidden;
    private String to_avatar;
    private String to_nickname;
    private String to_userid;
    private String tips;//说明描述
    private int wawa;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
    private String nickname;
    private String avatar;
    private int adddate;
    private String addon;
    private long addtime;

    private long coin;
    private int id;
    private String title;
    private int type;
    private int userid;
    private int edittime;
    private long pintai_coin;
    private long pintai_coin_total;
    private long pintai_money;
    private long pintai_money_total;
    private long rmb_coin;
    private long rmb_coin_total;
    private long rmb_money;
    private long rmb_money_total;
    private long points;//我的总积分
    private long cash;//我的总钻石


    public long getCash() {
        return cash;
    }
    // 石

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAdddate() {
        return adddate;
    }

    public void setAdddate(int adddate) {
        this.adddate = adddate;
    }

    public String getAddon() {
        return addon;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getEdittime() {
        return edittime;
    }

    public void setEdittime(int edittime) {
        this.edittime = edittime;
    }

    public long getPintai_coin() {
        return pintai_coin;
    }

    public void setPintai_coin(long pintai_coin) {
        this.pintai_coin = pintai_coin;
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

    public long getRmb_coin() {
        return rmb_coin;
    }

    public void setRmb_coin(long rmb_coin) {
        this.rmb_coin = rmb_coin;
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

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public String getTo_avatar() {
        return to_avatar;
    }

    public void setTo_avatar(String to_avatar) {
        this.to_avatar = to_avatar;
    }

    public String getTo_nickname() {
        return to_nickname;
    }

    public void setTo_nickname(String to_nickname) {
        this.to_nickname = to_nickname;
    }

    public String getTo_userid() {
        return to_userid;
    }

    public void setTo_userid(String to_userid) {
        this.to_userid = to_userid;
    }

    public int getWawa() {
        return wawa;
    }

    public void setWawa(int wawa) {
        this.wawa = wawa;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
