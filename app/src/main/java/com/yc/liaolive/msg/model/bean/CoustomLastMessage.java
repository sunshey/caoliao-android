package com.yc.liaolive.msg.model.bean;

import android.content.Context;
import com.yc.liaolive.msg.adapter.ChatAdapter;
import com.yc.liaolive.msg.model.Message;

/**
 * TinyHung@Outlook.com
 * 2018/12/20
 * 自定义需要，最后一条会话消息
 */

public class CoustomLastMessage extends Message {

    //用户身份
    private String identifier = "";
    //添加时间
    private long addTime;
    //未读数量
    private long unreadCount;
    //消息摘要
    private String summary="";

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {

    }

    /**
     * 信息摘要
     * @return
     */
    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public void save() {

    }
}
