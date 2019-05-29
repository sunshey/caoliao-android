package com.yc.liaolive.live.bean;

/**
 * TinyHung@Outlook.com
 * 2018/6/8
 * 礼物档次选择
 */

public class GiftLevelInfo {

    private int count;
    private boolean seleted;
    private int id;

    public GiftLevelInfo(int count, boolean seleted, int id) {
        this.count = count;
        this.seleted = seleted;
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSeleted() {
        return seleted;
    }

    public void setSeleted(boolean seleted) {
        this.seleted = seleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
