package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/12/17
 */

public class NoticeContent  implements Serializable {

    private String content;
    private String color;

    public NoticeContent(){
        super();
    }

    public NoticeContent(String content, String color) {
        this.content = content;
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
