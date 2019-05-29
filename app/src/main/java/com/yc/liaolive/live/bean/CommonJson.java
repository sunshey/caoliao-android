package com.yc.liaolive.live.bean;

/**
 * TinyHung@Outlook.com
 * 2018/6/26
 */

public class CommonJson <T>{

    public int UserAction;
    public String cmd;
    public T      data;

    public CommonJson() {

    }

    @Override
    public String toString() {
        return "CommonJson{" +
                "UserAction=" + UserAction +
                ", cmd='" + cmd + '\'' +
                ", data=" + data +
                '}';
    }
}
