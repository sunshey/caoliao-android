package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 视频封面
 */

public class VideoEditInfo implements Serializable {

    public String path; //图片的sd卡路径
    public long time;//图片所在视频的时间  毫秒
    private boolean selected;//是否被选中


    public VideoEditInfo() {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
