package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/12/18
 */

public class FollowState {

    private int is_attent=-1;

    public int getIs_attent() {
        return is_attent;
    }

    public void setIs_attent(int is_attent) {
        this.is_attent = is_attent;
    }

    @Override
    public String toString() {
        return "FollowState{" +
                "is_attent=" + is_attent +
                '}';
    }
}
