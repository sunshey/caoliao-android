package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/9/19
 * 信令沟通
 */

public class ImageObserverEvent {

    private int position;

    private ImageInfo imageInfo;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}
