package com.yc.liaolive.live.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/6/29
 */

public class GiftTypeInfo  implements Serializable{
    /**
     * addtime : 1530260031
     * id : 4
     * name : whole
     * title : 全站
     */
    private boolean selected;
    private int addtime;
    private int id;
    private String name;
    private String title;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "GiftTypeInfo{" +
                "selected=" + selected +
                ", addtime=" + addtime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
