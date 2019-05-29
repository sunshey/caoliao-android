package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * @author TinyHung@Outlook.com
 * @version 1.0
 * @time 2016-10-13 21:00
 * @des $歌曲信息实体类，实现了排序的接口
 */
public class MusicInfo implements Comparable<MusicInfo>,Serializable {

    private static final String TAG = "MusicInfo";
    private String fileName;
    private String title;
    private int duration;
    private String singer;
    private String album;
    private String year;
    private String type;
    private String size;
    private String fileUrl;
    private String pinyin;
    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }



    public MusicInfo(){
        super();
    }



    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitle() {
        return title;
    }

    public int getDuration() {
        return duration;
    }

    public String getSinger() {
        return singer;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getPinyin() {
        return pinyin;
    }


    @Override
    public String toString() {
        return "Song{" +
                "fileName='" + fileName + '\'' +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", year='" + year + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }

    /**
     * 对对象拼音排序
     * @param another
     * @return
     */
    @Override
    public int compareTo(MusicInfo another) {
        return this.pinyin.compareTo(another.getPinyin());
    }
}
