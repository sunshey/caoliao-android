package com.yc.liaolive.videocall.bean;

/**
 * TinyHung@Outlook.com
 * 2018/10/18
 */

public class CallResultInfo {

    //剩余可通话时长
    private long limit_time;
    private int chat_deplete;//视频聊价格,消耗
    private int chat_integral;//获得的积分
    //是否已经成功付费
    private int is_paid;// 0：未成功付费  1：已成功付费
    //通话连接建立TOKEN
    private ChatTokenBean chat_token;

    private String chat_paid_fee;//累计通话消耗钻石
    private String durtion;//通话时长 单位 秒

    public long getLimit_time() {
        return limit_time;
    }

    public void setLimit_time(long limit_time) {
        this.limit_time = limit_time;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public int getChat_integral() {
        return chat_integral;
    }

    public void setChat_integral(int chat_integral) {
        this.chat_integral = chat_integral;
    }

    public String getChat_paid_fee() {
        return chat_paid_fee;
    }

    public void setChat_paid_fee(String chat_paid_fee) {
        this.chat_paid_fee = chat_paid_fee;
    }

    public int getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(int is_paid) {
        this.is_paid = is_paid;
    }

    public ChatTokenBean getChat_token() {
        return chat_token;
    }

    public void setChat_token(ChatTokenBean chat_token) {
        this.chat_token = chat_token;
    }

    public String getDurtion() {
        return durtion;
    }

    public void setDurtion(String durtion) {
        this.durtion = durtion;
    }

    @Override
    public String toString() {
        return "CallResultInfo{" +
                "limit_time=" + limit_time +
                ", chat_deplete=" + chat_deplete +
                ", chat_integral=" + chat_integral +
                ", chat_paid_fee='" + chat_paid_fee + '\'' +
                ", is_paid=" + is_paid +
                ", chat_token=" + chat_token +
                ", durtion=" + durtion +
                '}';
    }
}
