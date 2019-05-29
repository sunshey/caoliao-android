package com.yc.liaolive.live.manager;

/**
 * 礼物赠送
 * Created by yangxueqin on 2018/11/23.
 */

public class GiftManager {

    private static GiftManager giftManager;

    private String showWhiteTips; //提示白名单钻石不足 礼物发送拦截

    public static GiftManager getInstance() {
        if (giftManager == null) {
            giftManager = new GiftManager();
        }
        return giftManager;
    }

    public String getShowWhiteTips() {
        return showWhiteTips;
    }

    public void setShowWhiteTips(String showWhiteTips) {
        this.showWhiteTips = showWhiteTips;
    }
}
