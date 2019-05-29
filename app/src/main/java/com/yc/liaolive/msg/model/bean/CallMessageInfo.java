package com.yc.liaolive.msg.model.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 */

public class CallMessageInfo implements MultiItemEntity {

    private long id=0;//消息类型ID
    private int locIcon;//icon
    private String icon;//图标
    private String userid;
    private String nickname;
    private String avatar;
    private String title;//消耗类别标题
    private long time;
    private int chatid;
    private String content;
    private int state;//预约状态 0: 预约中 1 ：预约成功 2：
    private int type;//2：主播回拨 1：用户
    private int num;
    private String contentState;//收入还是支出类型
    private long price;//价格
    private int answerState;//接听状态 0：已接听 1：未接听
    private int level_integral;//用户等级
    private int intimate_value;//亲密值
    private int itemType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLocIcon() {
        return locIcon;
    }

    public void setLocIcon(int locIcon) {
        this.locIcon = locIcon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentState() {
        return contentState;
    }

    public void setContentState(String contentState) {
        this.contentState = contentState;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getLevel_integral() {
        return level_integral;
    }

    public void setLevel_integral(int level_integral) {
        this.level_integral = level_integral;
    }

    public int getIntimate_value() {
        return intimate_value;
    }

    public void setIntimate_value(int intimate_value) {
        this.intimate_value = intimate_value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAnswerState() {
        return answerState;
    }

    public void setAnswerState(int answerState) {
        this.answerState = answerState;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getChatid() {
        return chatid;
    }

    public void setChatid(int chatid) {
        this.chatid = chatid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
