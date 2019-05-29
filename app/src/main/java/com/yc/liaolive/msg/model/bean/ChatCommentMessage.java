package com.yc.liaolive.msg.model.bean;

/**
 * TinyHung@Outlook.com
 * 2019/1/4
 * 通用消息
 */

public class ChatCommentMessage {

    private int UserAction;//信令
    private String content;//文本内容
    private String cover;//封面
    private String actionParam;//输入状态

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getActionParam() {
        return actionParam;
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
    }
}
