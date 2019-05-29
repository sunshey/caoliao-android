package com.yc.liaolive.bean;

/**
 * Created by wanglin  on 2018/7/5 15:12.
 */
public class HelpInfo {
    private String id;
    private String title;
    /**
     * addtime : 1530934086
     * content : 内容
     * id : 1
     * sort : 1
     * type : help
     */

    private long addtime;
    private String content;

    private int sort;
    private String type;

    public HelpInfo() {
    }

    public HelpInfo(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }
}
