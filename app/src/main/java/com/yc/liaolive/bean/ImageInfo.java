package com.yc.liaolive.bean;

import android.support.annotation.NonNull;
import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import java.io.Serializable;

/**
 * $TinyHung@Outlook.com
 * 2017/8/3
 * 扫描本地微信视频
 */
public class ImageInfo  implements Comparable,Serializable, MultiItemEntity {

    private String fileName;
    private String filePath;
    private Long fileCreazeTime;
    private boolean isSelector;
    private long fileSize;
    private String fileKey;
    private int itemType;
    private int selecte;
    private int position;//在列表中所在的位置

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSelecte() {
        return selecte;
    }

    public void setSelecte(int selecte) {
        this.selecte = selecte;
    }

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

    public Long getFileCreazeTime() {
        return this.fileCreazeTime;
    }
    public void setFileCreazeTime(Long fileCreazeTime) {
        this.fileCreazeTime = fileCreazeTime;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ImageInfo weiXinVideo= (ImageInfo) o;
        return fileCreazeTime >weiXinVideo.getFileCreazeTime()?1:0;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
