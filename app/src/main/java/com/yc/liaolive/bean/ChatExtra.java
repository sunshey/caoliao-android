package com.yc.liaolive.bean;

import com.tencent.TIMConversationType;
import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/12/12
 * 私信
 */

public class ChatExtra implements Serializable {

    private String identify;//发送者身份ID
    private String content;//发送内容
    private String avatar;//发送者头像
    private String sendNickName;//发送者用户昵称
    private TIMConversationType type;//会话类型

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public TIMConversationType getType() {
        return type;
    }

    public void setType(TIMConversationType type) {
        this.type = type;
    }

    public String getSendNickName() {
        return sendNickName;
    }

    public void setSendNickName(String sendNickName) {
        this.sendNickName = sendNickName;
    }

    @Override
    public String toString() {
        return "ChatExtra{" +
                "identify='" + identify + '\'' +
                ", content='" + content + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sendNickName='" + sendNickName + '\'' +
                ", type=" + type +
                '}';
    }
}
