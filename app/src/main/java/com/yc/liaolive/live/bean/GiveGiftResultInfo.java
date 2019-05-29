package com.yc.liaolive.live.bean;


/**
 * TinyHung@Outlook.com
 * 2018/6/7
 * 赠送礼物回调数据
 * 这里的泛型所指的是观众对象
 */

public class GiveGiftResultInfo {

    private GiftUserInfo acceptinfo;
    private GiftUserInfo userinfo;
    private GiftUserInfo change;

    public GiftUserInfo getAcceptinfo() {
        return acceptinfo;
    }

    public void setAcceptinfo(GiftUserInfo acceptinfo) {
        this.acceptinfo = acceptinfo;
    }

    public GiftUserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(GiftUserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public GiftUserInfo getChange() {
        return change;
    }

    public void setChange(GiftUserInfo change) {
        this.change = change;
    }
}
