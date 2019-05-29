package com.yc.liaolive.bean;

import com.yc.liaolive.bean.WeiXinVideo;

import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/5/24 15:52
 * 事件总线
 */
public class ScanMessageEvent {

    private String message;
    private List<WeiXinVideo> mWeiXinVideos;

    public ScanMessageEvent(){
        super();
    }
    public ScanMessageEvent(String message,List<WeiXinVideo> weiXinVideos) {
        this.message = message;
        this.mWeiXinVideos = weiXinVideos;
    }

    public List<WeiXinVideo> getWeiXinVideos() {
        return mWeiXinVideos;
    }

    public void setWeiXinVideos(List<WeiXinVideo> weiXinVideos) {
        mWeiXinVideos = weiXinVideos;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
