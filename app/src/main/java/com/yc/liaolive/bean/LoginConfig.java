package com.yc.liaolive.bean;

import com.yc.liaolive.recharge.model.bean.VipRechargePoppupBean;

/**
 * TinyHung@Outlook.com
 * 2018/6/1
 * 登录成功后的配置信息
 */

public class LoginConfig {

    private int expires;
    private String login_token;
    private String refresh_token;

    private RoomserviceSignBean roomservice_sign;
    private String token;

    //用户是否购买了话费充值vip 0：未购买 1：已购买
    private int vip_phone;
    private int vip_coin;//用户是否有钻石奖励  1：有钻石奖励 0：没有
    //礼物列表数据最后一次修改的时间
    private String gift_edit_lasttime;

    //用户领取话费弹窗
    private VipRechargePoppupBean popup_page;

    private NoticeMessage official_notice;//直播间、私信、视频通话公告消息
    //礼物配置
    private Object gift_config;

    //芝麻实名认证是否开启
    private String verification_zhima; //0：未开启 1：开启
    //强制绑定手机号是否开启
    private String verification_phone;//0：未开启 1：开启

    public Object getGift_config() {
        return gift_config;
    }

    public void setGift_config(Object gift_config) {
        this.gift_config = gift_config;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public RoomserviceSignBean getRoomservice_sign() {
        return roomservice_sign;
    }

    public void setRoomservice_sign(RoomserviceSignBean roomservice_sign) {
        this.roomservice_sign = roomservice_sign;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NoticeMessage getOfficial_notice() {
        return official_notice;
    }

    public void setOfficial_notice(NoticeMessage official_notice) {
        this.official_notice = official_notice;
    }

    public String getGift_edit_lasttime() {
        return gift_edit_lasttime;
    }

    public void setGift_edit_lasttime(String gift_edit_lasttime) {
        this.gift_edit_lasttime = gift_edit_lasttime;
    }

    public int getVip_coin() {
        return vip_coin;
    }

    public void setVip_coin(int vip_coin) {
        this.vip_coin = vip_coin;
    }

    /**
     * IM配置信息
     */
    public static class RoomserviceSignBean {
        private String accountType;
        private String sdkAppID;
        private String userID;
        private String userSig;
        private String userName;
        private String userHead;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserHead() {
            return userHead;
        }

        public void setUserHead(String userHead) {
            this.userHead = userHead;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getSdkAppID() {
            return sdkAppID;
        }

        public void setSdkAppID(String sdkAppID) {
            this.sdkAppID = sdkAppID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getUserSig() {
            return userSig;
        }

        public void setUserSig(String userSig) {
            this.userSig = userSig;
        }
    }

    public int getVip_phone() {
        return vip_phone;
    }

    public void setVip_phone(int vip_phone) {
        this.vip_phone = vip_phone;
    }

    public VipRechargePoppupBean getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(VipRechargePoppupBean popup_page) {
        this.popup_page = popup_page;
    }

    public String getVerification_zhima() {
        return verification_zhima;
    }

    public void setVerification_zhima(String verification_zhima) {
        this.verification_zhima = verification_zhima;
    }

    public String getVerification_phone() {
        return verification_phone;
    }

    public void setVerification_phone(String verification_phone) {
        this.verification_phone = verification_phone;
    }

    @Override
    public String toString() {
        return "LoginConfig{" +
                "expires=" + expires +
                ", login_token='" + login_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", roomservice_sign=" + roomservice_sign +
                ", token='" + token + '\'' +
                ", vip_phone=" + vip_phone +
                ", gift_edit_lasttime='" + gift_edit_lasttime + '\'' +
                ", popup_page=" + popup_page +
                ", official_notice=" + official_notice +
                ", gift_config=" + gift_config +
                ", verification_zhima='" + verification_zhima + '\'' +
                ", verification_phone='" + verification_phone + '\'' +
                ", vip_coin='" + vip_coin + '\'' +
                '}';
    }
}
