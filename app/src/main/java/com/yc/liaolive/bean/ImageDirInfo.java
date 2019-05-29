package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/9/19
 */

public class ImageDirInfo {

    //目录路径
    private String dirPath;
    //路径名称
    private String dirName;
    //文件下第一张照片的绝对地址
    private String filePath;
    //目录下文件个数
    public int count;
    //是否选中状态
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
