package com.yc.liaolive.live.bean;

import com.tencent.TIMMessage;

/**
 * TinyHung@Outlook.com
 * 2018/7/7
 */

public class SystemMessageInfo {

    private TIMMessage mTIMMessage;
    private String type;
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TIMMessage getTIMMessage() {
        return mTIMMessage;
    }

    public void setTIMMessage(TIMMessage TIMMessage) {
        mTIMMessage = TIMMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
