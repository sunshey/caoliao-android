package com.yc.liaolive.bean;

import com.yc.liaolive.media.bean.MediaGiftInfo;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/27
 * 预览多媒体文件信息
 */

public class MediaFileInfo {

    /**
     * attent : 0
     * avatar : http://thirdqq.qlogo.cn/qqapp/1106846629/A8D78A6445B8AF47E2DA6B6397AA9FA8/100
     * browse_number : 0
     * file_path : http://s.197754.com/zb/image/a5a290a6139349912cc907b183247507.png
     * is_online : 1
     * love_number : 0
     * nickname : 第四个
     * room_info : {"play_url_flv":"http://24603.liveplay.myqcloud.com/live/24603_55176509.flv","play_url_m3u8":"http://24603.liveplay.myqcloud.com/live/24603_55176509.m3u8","play_url_rtmp":"rtmp://24603.liveplay.myqcloud.com/live/24603_55176509","roomid":"room_1538032174493_a338"}
     * share_number : 0
     * signature :
     * userid : 55176509
     */
    private String signature;
    private String userid;
    private String nickname;
    private String avatar;
    private int attent;//关注状态 1：已关注 0：未关注
    private long browse_number;//预览人数
    private long love_number;//点赞人数
    private long share_number;//分享人数
    private String file_path;//购买成功文件地址
    private int is_online;//主播是否正在直播
    private int is_love;//是否对该视频点赞
    private int identity_audit;//是否是通过认证的主播
    private String user_state;//用户状态 offline 离线、live 正在直播、 videocall 视频聊天、disturbed  防止打扰、 free 空闲
    private String headset_img;

    public String getHeadset_img() {
        return headset_img;
    }

    public void setHeadset_img(String headset_img) {
        this.headset_img = headset_img;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    /**
     * play_url_flv : http://24603.liveplay.myqcloud.com/live/24603_55176509.flv
     * play_url_m3u8 : http://24603.liveplay.myqcloud.com/live/24603_55176509.m3u8
     * play_url_rtmp : rtmp://24603.liveplay.myqcloud.com/live/24603_55176509
     * roomid : room_1538032174493_a338
     */



    private VideoRoomInfo room_info;//直播间信息

    //礼物信息
    private List<MediaGiftInfo> gift_info;//礼物历史记录
    private List<FansInfo> gift_rank;//赠送榜单列表

    public List<MediaGiftInfo> getGift_info() {
        return gift_info;
    }

    public void setGift_info(List<MediaGiftInfo> gift_info) {
        this.gift_info = gift_info;
    }

    public List<FansInfo> getGift_rank() {
        return gift_rank;
    }

    public void setGift_rank(List<FansInfo> gift_rank) {
        this.gift_rank = gift_rank;
    }

    public int getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(int identity_audit) {
        this.identity_audit = identity_audit;
    }

    public int getAttent() {
        return attent;
    }

    public void setAttent(int attent) {
        this.attent = attent;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getBrowse_number() {
        return browse_number;
    }

    public void setBrowse_number(long browse_number) {
        this.browse_number = browse_number;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public long getLove_number() {
        return love_number;
    }

    public void setLove_number(long love_number) {
        this.love_number = love_number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public VideoRoomInfo getRoom_info() {
        return room_info;
    }

    public void setRoom_info(VideoRoomInfo room_info) {
        this.room_info = room_info;
    }

    public long getShare_number() {
        return share_number;
    }

    public void setShare_number(long share_number) {
        this.share_number = share_number;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getIs_love() {
        return is_love;
    }

    public void setIs_love(int is_love) {
        this.is_love = is_love;
    }
}
