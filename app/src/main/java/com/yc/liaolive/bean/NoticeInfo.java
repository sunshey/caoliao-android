package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/8/2
 */

public class NoticeInfo {

    private String cmd;
    //推送标题
    private String title;
    //推送内容
    private String content;
    //身份审核
    private int identity_audit;
    //主播、宿主用户ID
    private String userid;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(int identity_audit) {
        this.identity_audit = identity_audit;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "NoticeInfo{" +
                "cmd='" + cmd + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", identity_audit=" + identity_audit +
                ", userid='" + userid + '\'' +
                '}';
    }
}
