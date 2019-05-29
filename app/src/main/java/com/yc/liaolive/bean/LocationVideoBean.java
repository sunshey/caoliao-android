package com.yc.liaolive.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * TinyHung@outlook.com
 * 2017/5/2 23:27
 */

public class LocationVideoBean implements Serializable {

    private int id;
    private String title;
    private String album;
    private String artist;
    private String displayName;
    private String mimeType;
    private String path;
    private Long size;
    private long duration;
    private Long lastModified;//最后修改时间
    private Bitmap thumbnail;
    private boolean isSelector;

    public LocationVideoBean(int id, String title, String album, String artist, String displayName, String mimeType,
                             String path, Long size, long duration, Long lastModified, Bitmap thumbnail, boolean isSelector) {
        super();
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.duration = duration;
        this.lastModified = lastModified;
        this.thumbnail = thumbnail;
        this.isSelector = isSelector;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getMimeType() {
        return mimeType;
    }
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }
}
