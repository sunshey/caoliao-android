package com.yc.liaolive.live.bean;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/16
 * 中将信息
 */

public class AwardInfo {

    private String userid;//中奖对象
    private String nickName;//中奖人昵称
    private long monery;//中奖金额
    private boolean isMine;//是否是自己中奖了

    //中奖的礼物基本信息
    private long id;//礼物ID
    private String title;//礼物标题
    private int price;//礼物价格
    private String src;//礼物ICON
    private String accaptUserID;//接收人
    private int count;//礼物个数

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getMonery() {
        return monery;
    }

    public void setMonery(long monery) {
        this.monery = monery;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getAccaptUserID() {
        return accaptUserID;
    }

    public void setAccaptUserID(String accaptUserID) {
        this.accaptUserID = accaptUserID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
