package com.yc.liaolive.bean;

import com.yc.liaolive.live.bean.GiftInfo;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/16
 */

public class AllGiftInfo {

    List<GiftInfo> list;
    private int version;

    public List<GiftInfo> getList() {
        return list;
    }

    public void setList(List<GiftInfo> list) {
        this.list = list;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
