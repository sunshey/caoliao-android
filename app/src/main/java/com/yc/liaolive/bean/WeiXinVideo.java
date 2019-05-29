package com.yc.liaolive.bean;

import android.support.annotation.NonNull;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * $TinyHung@Outlook.com
 * 2017/8/3
 * 扫描本地微信视频
 */
public class WeiXinVideo  implements Comparable,Serializable, MultiItemEntity {

    private String fileName;
    private String videoPath;
    private Long videoCreazeTime;
    private int videoDortion;
    private boolean isSelector;
    private long fileSize;
    private String videpThbunPath;
    private String fileKey;
    private int itemType;

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileKey() {
        return this.fileKey;
    }
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
    public String getVidepThbunPath() {
        return this.videpThbunPath;
    }
    public void setVidepThbunPath(String videpThbunPath) {
        this.videpThbunPath = videpThbunPath;
    }
    public boolean getIsSelector() {
        return this.isSelector;
    }
    public void setIsSelector(boolean isSelector) {
        this.isSelector = isSelector;
    }
    public int getVideoDortion() {
        return this.videoDortion;
    }
    public void setVideoDortion(int videoDortion) {
        this.videoDortion = videoDortion;
    }
    public Long getVideoCreazeTime() {
        return this.videoCreazeTime;
    }
    public void setVideoCreazeTime(Long videoCreazeTime) {
        this.videoCreazeTime = videoCreazeTime;
    }
    public String getVideoPath() {
        return this.videoPath;
    }
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        WeiXinVideo weiXinVideo= (WeiXinVideo) o;
        return videoCreazeTime>weiXinVideo.getVideoCreazeTime()?1:0;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
