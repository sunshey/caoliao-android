package com.yc.liaolive.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/26
 */

public class AttentInfo {

    private int is_attent;//是否已经关注了此用户  0:未关注 1：已关注
    private String is_black="0";//是否在黑名单  0：不在黑名单列表 1：在黑名单列表
    private String identity_audit="0";//是否是主播 0：未认证 1：审核中 2：已认证

    private List<BannerInfo> popup_page;

    public int getIs_attent() {
        return is_attent;
    }

    public void setIs_attent(int is_attent) {
        this.is_attent = is_attent;
    }

    public List<BannerInfo> getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(List<BannerInfo> popup_page) {
        this.popup_page = popup_page;
    }

    public String getIs_black() {
        return is_black;
    }

    public void setIs_black(String is_black) {
        this.is_black = is_black;
    }

    public String getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(String identity_audit) {
        this.identity_audit = identity_audit;
    }

    @Override
    public String toString() {
        return "AttentInfo{" +
                "is_attent=" + is_attent +
                ", is_black='" + is_black + '\'' +
                ", identity_audit='" + identity_audit + '\'' +
                ", popup_page=" + popup_page +
                '}';
    }
}
