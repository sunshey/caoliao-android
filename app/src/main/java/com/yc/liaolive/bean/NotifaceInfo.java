package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/8/1
 * 解析通知消息
 */

public class NotifaceInfo <T>{

    private String cmd;
    private T data;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NotifaceInfo{" +
                "cmd='" + cmd + '\'' +
                ", data=" + data +
                '}';
    }
}
