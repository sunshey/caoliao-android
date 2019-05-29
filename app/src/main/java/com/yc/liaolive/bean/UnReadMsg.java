package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/9/29
 * 未读消息
 */

public class UnReadMsg {

    private String identify;
    public long count;

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
