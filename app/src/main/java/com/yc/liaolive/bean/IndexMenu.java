package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2019/3/27
 */

public class IndexMenu {

    public IndexMenu(int id, String title) {
        this.id = id;
        this.title = title;
    }

    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
