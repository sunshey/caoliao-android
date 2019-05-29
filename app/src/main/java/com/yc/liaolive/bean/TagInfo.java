package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 标签
 */

public class TagInfo {

    /**
     * id : 3
     * content : 清纯
     * type : 1
     * color : #12f9cb
     * sort : 1
     * state : 1
     * addtime : 1539668025
     */

    private int id;
    private String content;
    private int type;
    private String color;
    private int sort;
    private int state;
    private int addtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }
}
