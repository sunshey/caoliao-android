package com.yc.liaolive.start.model.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2019/3/7
 */

public class IndexShowConfig implements Serializable {

    /**
     * first : 0 首次显示显示的位置
     * second : 0 >首次
     */

    private String first;
    private String second;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
