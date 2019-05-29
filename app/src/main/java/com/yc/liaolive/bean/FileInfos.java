package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/9/14
 * 文件的基本信息
 */

public class FileInfos {

    private int fileWidth;
    private int fileHeight;
    private long videoDurtion;
    private int videoRotation;
    private long fileSize;

    public int getFileWidth() {
        return fileWidth;
    }

    public void setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
    }

    public int getFileHeight() {
        return fileHeight;
    }

    public void setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
    }

    public long getVideoDurtion() {
        return videoDurtion;
    }

    public void setVideoDurtion(long videoDurtion) {
        this.videoDurtion = videoDurtion;
    }

    public int getVideoRotation() {
        return videoRotation;
    }

    public void setVideoRotation(int videoRotation) {
        this.videoRotation = videoRotation;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
