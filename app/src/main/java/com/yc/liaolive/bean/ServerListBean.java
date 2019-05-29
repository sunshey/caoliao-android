package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/10/31
 */

public class ServerListBean  implements Serializable {

    /**
     * name : 呆呆
     * identify : hty_Yuye
     */

    private String name;//微信昵称
    private String identify;//微信ID

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }
}
