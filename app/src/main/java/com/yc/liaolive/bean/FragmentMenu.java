package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2019/1/15
 * 主页片段
 */

public class FragmentMenu implements Serializable{

    /**
     * fragment_type : 1
     * fragment_title : 小视频
     */

    private int fragment_type;
    private int fragment_id;
    private String fragment_title;

    public int getFragment_type() {
        return fragment_type;
    }

    public void setFragment_type(int fragment_type) {
        this.fragment_type = fragment_type;
    }

    public String getFragment_title() {
        return fragment_title;
    }

    public void setFragment_title(String fragment_title) {
        this.fragment_title = fragment_title;
    }

    public int getFragment_id() {
        return fragment_id;
    }

    public void setFragment_id(int fragment_id) {
        this.fragment_id = fragment_id;
    }

    @Override
    public String toString() {
        return "FragmentMenu{" +
                "fragment_type=" + fragment_type +
                "fragment_id=" + fragment_id +
                ", fragment_title='" + fragment_title + '\'' +
                '}';
    }
}
