package com.yc.liaolive.bean;

import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/5/10
 */

public class TempPusherUrlInfo  implements Serializable{
    /**
     * url_play_flv : http://3891.liveplay.myqcloud.com/live/3891_user_27f208d0_9279.flv
     * url_play_hls : http://3891.liveplay.myqcloud.com/live/3891_user_27f208d0_9279.m3u8
     * url_push : rtmp://3891.livepush.myqcloud.com/live/3891_user_27f208d0_9279?bizid=3891&txSecret=0b900bc56ca9529e8da04742449d78b9&txTime=5AFD89FB
     * url_play_acc : rtmp://3891.liveplay.myqcloud.com/live/3891_user_27f208d0_9279?bizid=3891&txSecret=0b900bc56ca9529e8da04742449d78b9&txTime=5AFD89FB
     * url_play_rtmp : rtmp://3891.liveplay.myqcloud.com/live/3891_user_27f208d0_9279
     */
    private String url_play_flv;
    private String url_play_hls;
    private String url_push;
    private String url_play_acc;
    private String url_play_rtmp;

    public String getUrl_play_flv() {
        return url_play_flv;
    }

    public void setUrl_play_flv(String url_play_flv) {
        this.url_play_flv = url_play_flv;
    }

    public String getUrl_play_hls() {
        return url_play_hls;
    }

    public void setUrl_play_hls(String url_play_hls) {
        this.url_play_hls = url_play_hls;
    }

    public String getUrl_push() {
        return url_push;
    }

    public void setUrl_push(String url_push) {
        this.url_push = url_push;
    }

    public String getUrl_play_acc() {
        return url_play_acc;
    }

    public void setUrl_play_acc(String url_play_acc) {
        this.url_play_acc = url_play_acc;
    }

    public String getUrl_play_rtmp() {
        return url_play_rtmp;
    }

    public void setUrl_play_rtmp(String url_play_rtmp) {
        this.url_play_rtmp = url_play_rtmp;
    }
}
