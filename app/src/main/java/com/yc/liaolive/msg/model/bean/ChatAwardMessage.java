package com.yc.liaolive.msg.model.bean;

/**
 * TinyHung@Outlook.com
 * 2019/1/4
 * 私信中奖消息
 */

public class ChatAwardMessage {

    private String giftName;//礼物名称
    private String sendUserName;//发送者昵称
    private int drawTimes;//中奖倍率
    private String msgContent;//提示内容
    private int sendUserVIP;//发送者VIP等级

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public int getDrawTimes() {
        return drawTimes;
    }

    public void setDrawTimes(int drawTimes) {
        this.drawTimes = drawTimes;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getSendUserVIP() {
        return sendUserVIP;
    }

    public void setSendUserVIP(int sendUserVIP) {
        this.sendUserVIP = sendUserVIP;
    }
}
