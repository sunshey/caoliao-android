package com.yc.liaolive.live.bean;

import com.yc.liaolive.bean.BannerInfo;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/28
 * 进入房间必要参数
 */

public class RoomExtra implements Serializable {

    /**
     * avatar : http://a.197754.com/uploads/images/20181009/ce07475a512e64cea8f0f5c9cdd9ba70.jpg
     * frontcover : http://a.197754.com/uploads/images/20180926/6f825cf1106b2142a6de35e4c17530e4.jpg
     * member_total : 1763
     * nickname : 尤美晨子
     * play_url_flv : https://s.197754.com/vedio/2.mp4
     * play_url_m3u8 : https://s.197754.com/vedio/2.mp4
     * play_url_rtmp : https://s.197754.com/vedio/2.mp4
     * pushers : [{"accelerateURL":"https://s.197754.com/vedio/2.mp4","userAvatar":"http://a.197754.com/uploads/images/20181009/ce07475a512e64cea8f0f5c9cdd9ba70.jpg","userID":"12342415","userName":"尤美晨子"}]
     * room_id : room_1536718909950_47c1
     * title : ~吖咩~
     * userid : 12342415
     */

    private String avatar;
    private String frontcover;
    private String nickname;
    private String play_url_flv;
    private String play_url_m3u8;
    private String play_url_rtmp;
    private String room_id;
    private String title;
    private String userid;
    private String pull_steram;//拉流地址
    private String noticaContent;//通知栏内容
    private String noticaTitle;//通知栏标题
    private List<PusherInfo> pushers;
    private String headset_img; //提示佩戴耳机图片

    private String itemCategory;//条目标识  数据
    //广告
    private List<BannerInfo> banners;


    public RoomExtra(){
        super();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFrontcover() {
        return frontcover;
    }

    public void setFrontcover(String frontcover) {
        this.frontcover = frontcover;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPlay_url_flv() {
        return play_url_flv;
    }

    public void setPlay_url_flv(String play_url_flv) {
        this.play_url_flv = play_url_flv;
    }

    public String getPlay_url_m3u8() {
        return play_url_m3u8;
    }

    public void setPlay_url_m3u8(String play_url_m3u8) {
        this.play_url_m3u8 = play_url_m3u8;
    }

    public String getPlay_url_rtmp() {
        return play_url_rtmp;
    }

    public void setPlay_url_rtmp(String play_url_rtmp) {
        this.play_url_rtmp = play_url_rtmp;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPull_steram() {
        return pull_steram;
    }

    public void setPull_steram(String pull_steram) {
        this.pull_steram = pull_steram;
    }

    public String getNoticaContent() {
        return noticaContent;
    }

    public void setNoticaContent(String noticaContent) {
        this.noticaContent = noticaContent;
    }

    public String getNoticaTitle() {
        return noticaTitle;
    }

    public void setNoticaTitle(String noticaTitle) {
        this.noticaTitle = noticaTitle;
    }

    public List<PusherInfo> getPushers() {
        return pushers;
    }

    public void setPushers(List<PusherInfo> pushers) {
        this.pushers = pushers;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public List<BannerInfo> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerInfo> banners) {
        this.banners = banners;
    }

    public String getHeadset_img() {
        return headset_img;
    }

    public void setHeadset_img(String headset_img) {
        this.headset_img = headset_img;
    }

    @Override
    public String toString() {
        return "RoomExtra{" +
                "avatar='" + avatar + '\'' +
                ", frontcover='" + frontcover + '\'' +
                ", nickname='" + nickname + '\'' +
                ", play_url_flv='" + play_url_flv + '\'' +
                ", play_url_m3u8='" + play_url_m3u8 + '\'' +
                ", play_url_rtmp='" + play_url_rtmp + '\'' +
                ", room_id='" + room_id + '\'' +
                ", title='" + title + '\'' +
                ", userid='" + userid + '\'' +
                ", pull_steram='" + pull_steram + '\'' +
                ", noticaContent='" + noticaContent + '\'' +
                ", noticaTitle='" + noticaTitle + '\'' +
                ", pushers=" + pushers +
                ", itemCategory='" + itemCategory + '\'' +
                ", banners=" + banners +
                '}';
    }
}
