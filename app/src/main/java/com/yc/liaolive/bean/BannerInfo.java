package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/7/11
 * 首页的广告
 */

public class BannerInfo implements Serializable {

    public static final int TASK_ACTION_BIND_PHONE=9;//绑定手机号
    public static final int TASK_ACTION_VIP=11;//会员任务
    public static final int APP_TASK_FIRST_RECHGRE=1;//首充任务

    /**
     * active_type : 0
     * activity :
     * extra_pramas :
     * img : http://zb.6071.com/uploads/ic_home_banner.png
     * sort : 0
     * title : 标题
     * url : http://www.baidu.com
     */
    private int id;
    private String img;
    private int sort;
    private String title;
    private int taskid;//任务ID
    private int monery;//任务奖励积分
    private int is_get;//任务是否已经获取
    private int width;
    private int height;
    private String playimg;//广告的大图
    private String jump_url;

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getPlayimg() {
        return playimg;
    }

    public void setPlayimg(String playimg) {
        this.playimg = playimg;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMonery() {
        return monery;
    }

    public void setMonery(int monery) {
        this.monery = monery;
    }

    public int getIs_get() {
        return is_get;
    }

    public void setIs_get(int is_get) {
        this.is_get = is_get;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int task_id) {
        this.taskid = task_id;
    }

    @Override
    public String toString() {
        return "BannerInfo{" +
                "id=" + id +
                ", img='" + img + '\'' +
                ", sort=" + sort +
                ", title='" + title + '\'' +
                ", taskid=" + taskid +
                ", monery=" + monery +
                ", is_get=" + is_get +
                ", width=" + width +
                ", height=" + height +
                ", playimg='" + playimg + '\'' +
                ", jump_url='" + jump_url + '\'' +
                '}';
    }
}
