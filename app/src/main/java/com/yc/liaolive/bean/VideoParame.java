package com.yc.liaolive.bean;

/**
 * TinyHung@outlook.com
 * 2017/5/26 19:30
 */

public class VideoParame {
    private int xpHeight;
    private int xpWidth;
    private long durtaion;

    public VideoParame(){
        super();
    }
    public VideoParame(int xpHeight, int xpWidth, long durtaion) {
        this.xpHeight = xpHeight;
        this.xpWidth = xpWidth;
        this.durtaion = durtaion;
    }

    public int getXpHeight() {
        return xpHeight;
    }

    public void setXpHeight(int xpHeight) {
        this.xpHeight = xpHeight;
    }

    public long getDurtaion() {
        return durtaion;
    }

    public void setDurtaion(long durtaion) {
        this.durtaion = durtaion;
    }

    public int getXpWidth() {
        return xpWidth;
    }

    public void setXpWidth(int xpWidth) {
        this.xpWidth = xpWidth;
    }

    @Override
    public String toString() {
        return "VideoParame{" +
                "xpHeight=" + xpHeight +
                ", xpWidth=" + xpWidth +
                ", durtaion=" + durtaion +
                '}';
    }
}
