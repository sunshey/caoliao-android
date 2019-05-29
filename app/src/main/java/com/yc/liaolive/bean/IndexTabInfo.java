package com.yc.liaolive.bean;

/**
 * TinyHung@outlook.com
 * 2017-05-29 1:50
 * 我的界面标题栏
 */

public class IndexTabInfo {

    private String titleName;
    private int aboutCount;
    private boolean isSelector;

    public IndexTabInfo(){
        super();
    }

    public IndexTabInfo(String titleName) {
        this.titleName = titleName;
    }

    public IndexTabInfo(String titleName, boolean isSelector) {
        this.titleName = titleName;
        this.isSelector = isSelector;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }


    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getAboutCount() {
        return aboutCount;
    }

    public void setAboutCount(int aboutCount) {
        this.aboutCount = aboutCount;
    }
}
