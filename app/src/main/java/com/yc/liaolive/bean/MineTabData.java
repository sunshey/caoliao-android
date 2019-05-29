package com.yc.liaolive.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/3/5
 */

public class MineTabData {

    List<TabMineUserInfo> list;
    private String quite="1";
    private String identity_audit;

    public List<TabMineUserInfo> getList() {
        return list;
    }

    public void setList(List<TabMineUserInfo> list) {
        this.list = list;
    }

    public String getQuite() {
        return quite;
    }

    public void setQuite(String quite) {
        this.quite = quite;
    }

    public String getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(String identity_audit) {
        this.identity_audit = identity_audit;
    }
}
