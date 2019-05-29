package com.yc.liaolive.msg.model.bean;

/**
 * TinyHung@Outlook.com
 * 2019/1/4
 * 私信礼物消息、输入状态
 */

public class ChatGiftMessage {

    private int UserAction;//信令
    private String content;
    private String icon;//ICON
    private String name;//礼物名称
    private int count;//礼物数量
    private int totalPrice;//礼物价格
    private long giftId;//礼物ID
    private String url;//BIG SRC


    public int getUserAction() {
        return UserAction;
    }

    public void setUserAction(int userAction) {
        UserAction = userAction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
