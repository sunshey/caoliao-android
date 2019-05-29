package com.yc.liaolive.bean;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * TinyHung@outlook.com
 * 2017/4/15 11:30
 * 本地视频BEAN
 */

public class LoactionVideoInfo  {

    private String name;
    private String path;
    private byte[] thumbnail;
    private int xpHeight;
    private int xpWidth;
    private Long lookDuration;
    private int lookProgress;
    private Long fileSize;
    private long duration;
    private boolean isSelector;
    private Long lastModified;//最后修改时间


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getXpHeight() {
        return xpHeight;
    }

    public void setXpHeight(int xpHeight) {
        this.xpHeight = xpHeight;
    }

    public int getXpWidth() {
        return xpWidth;
    }

    public void setXpWidth(int xpWidth) {
        this.xpWidth = xpWidth;
    }

    public Long getLookDuration() {
        return lookDuration;
    }

    public void setLookDuration(Long lookDuration) {
        this.lookDuration = lookDuration;
    }

    public int getLookProgress() {
        return lookProgress;
    }

    public void setLookProgress(int lookProgress) {
        this.lookProgress = lookProgress;
    }

    public byte[] getThumbnail(){
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        this.thumbnail = baos.toByteArray();
    }
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }
    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "LoactionVideoInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", xpHeight=" + xpHeight +
                ", xpWidth=" + xpWidth +
                ", lookDuration=" + lookDuration +
                ", lookProgress=" + lookProgress +
                '}';
    }
}
