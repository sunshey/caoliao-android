package com.yc.liaolive.bean;

/**
 * TinyHung@outlook.com
 * 2017/5/31 16:59
 * 分享信息
 */
public class ShareInfo {

    private String title;
    private String url;
    private String desp;
    private String videoID;
    private String videoPath;
    private String userID;
    private String netUrl;
    private String shareTitle;//分享弹窗标题
    private String roomid;
    private boolean isReport;//是否需要上报

    public boolean isReport() {
        return isReport;
    }

    public void setReport(boolean report) {
        isReport = report;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }


    public String getImageLogo() {
        return imageLogo;
    }

    public void setImageLogo(String imageLogo) {
        this.imageLogo = imageLogo;
    }

    private String imageLogo;


    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }


    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    private String logoUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }
}
