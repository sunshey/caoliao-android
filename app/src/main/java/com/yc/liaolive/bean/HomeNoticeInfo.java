package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/7/11
 * 首页通告
 */

public class HomeNoticeInfo implements Serializable {

    /**
     * addtime : 1531281265
     * announce_id : 5
     * content : 账户冻结通知
     * sort : 1
     * title : 账户冻结通知
     */

    private long addtime;
    private int announce_id;
    private String content;
    private int sort;
    private String title;
    private boolean isRead;//是否已读

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public int getAnnounce_id() {
        return announce_id;
    }

    public void setAnnounce_id(int announce_id) {
        this.announce_id = announce_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
