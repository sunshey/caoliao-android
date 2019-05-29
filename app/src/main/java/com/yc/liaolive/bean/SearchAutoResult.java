package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/6/8 17:14
 * 搜索关键字热匹配
 */
public class SearchAutoResult implements Serializable{
    private String key;
    private int type;

    public SearchAutoResult(String key, int type) {
        this.key = key;
        this.type = type;
    }

    public SearchAutoResult(){super();}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
