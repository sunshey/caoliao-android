package com.yc.liaolive.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/6
 */

public class RoomTaskDataInfo {

    private String title;
    private List<TaskInfo> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {this.title = title;
    }

    public List<TaskInfo> getList() {
        return list;
    }

    public void setList(List<TaskInfo> list) {
        this.list = list;
    }
}
