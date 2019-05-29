package com.yc.liaolive.live.bean;

import android.text.TextUtils;
import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.FamilyInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.adapter.LiveListOneAdapter;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/2
 */

public class RoomList implements MultiItemEntity,Serializable{

    /**
     * userid : 21907019
     * title : 3214
     * frontcover : http://zb.6071.com/uploads/20180628/2253d8a8acaae373d3376d8ccc89043f.jpg
     * nickname : ☀️梦一场
     * avatar : http://thirdqq.qlogo.cn/qqapp/1106846629/D752461C42466B03BA04EFB2974D91AC/100
     * user_frontcover : http://zb.6071.com/uploads/20180628/2253d8a8acaae373d3376d8ccc89043f.jpg
     * member_total : 1
     */
    //私有的房间
    private int online;//私有房间 0：下线 1：上线
    private String roomid;
    private String room_id;//同roomid，优先 room_id
    private String push_stream;
    private String push_stream_flv;
    private String push_stream_m3u8;
    //播放地址
    private String play_url_flv;
    private String play_url_m3u8;
    private String play_url_rtmp;
    private int chat_deplete;
    private int chat_minite;
    private String userid;
    private String title;
    private String frontcover;
    private String nickname;
    private String avatar;
    private String user_frontcover;
    private int room_type;//房间类别：0：直播间 1：视频通话 2：语音连麦
    private int member_total;
    private String city;
    private double distance;
    private int id;
    private double latitude;
    private int level_integral;
    private double longitude;
    private int sex;
    private int vip;
    private int is_family;//1：在家族里面 0：不再家族
    private int is_online;//1：在线 0：不在线
    private String user_state;//用户在线状态
    private String itemCategory;//条目标识  数据
    private int itemType;//条目类型  UI
    //广告
    private List<BannerInfo> banners;
    private String playUrl;//宣传片
    private String videoCover;//短片封面
    //私聊视频
    private FamilyInfo family;
    //小主播
    private List<PusherInfo> pushers;

    //相册列表，优选取
    private List<PrivateMedia> my_image_list;
    //当前在聊时间单位分钟
    private int chat_time;

    //视频通话 信息
    private VideoChatBean video_chat;

    private String signature;
    private String asmr;//是否是预设主播
    private int is_pay;//是否需要付费\已经付费 观看  0：无需购买 1：需要购买

    public int getIs_family() {
        return is_family;
    }

    public void setIs_family(int is_family) {
        this.is_family = is_family;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public FamilyInfo getFamily() {
        return family;
    }

    public void setFamily(FamilyInfo family) {
        this.family = family;
    }

    public List<PusherInfo> getPushers() {
        return pushers;
    }

    public void setPushers(List<PusherInfo> pushers) {
        this.pushers = pushers;
    }

    public int getChat_time() {
        return chat_time;
    }

    public void setChat_time(int chat_time) {
        this.chat_time = chat_time;
    }

    public VideoChatBean getVideo_chat() {
        return video_chat;
    }

    public void setVideo_chat(VideoChatBean video_chat) {
        this.video_chat = video_chat;
    }

    public List<PrivateMedia> getMy_image_list() {
        return my_image_list;
    }

    public void setMy_image_list(List<PrivateMedia> my_image_list) {
        this.my_image_list = my_image_list;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public RoomList(){}

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

    public String getPush_stream_m3u8() {
        return push_stream_m3u8;
    }

    public void setPush_stream_m3u8(String push_stream_m3u8) {
        this.push_stream_m3u8 = push_stream_m3u8;
    }

    public String getPush_stream_flv() {
        return push_stream_flv;
    }

    public void setPush_stream_flv(String push_stream_flv) {
        this.push_stream_flv = push_stream_flv;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getRoomid() {
        if(!TextUtils.isEmpty(room_id)){
            return room_id;
        }
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getPush_stream() {
        return push_stream;
    }

    public void setPush_stream(String push_stream) {
        this.push_stream = push_stream;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public int getChat_minite() {
        return chat_minite;
    }

    public void setChat_minite(int chat_minite) {
        this.chat_minite = chat_minite;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_frontcover() {
        return user_frontcover;
    }

    public void setUser_frontcover(String user_frontcover) {
        this.user_frontcover = user_frontcover;
    }

    public int getRoom_type() {
        return room_type;
    }

    public void setRoom_type(int room_type) {
        this.room_type = room_type;
    }

    public int getMember_total() {
        return member_total;
    }

    public void setMember_total(int member_total) {
        this.member_total = member_total;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLevel_integral() {
        return level_integral;
    }

    public void setLevel_integral(int level_integral) {
        this.level_integral = level_integral;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getIs_pay() {
        return is_pay;
    }

    public void setIs_pay(int is_pay) {
        this.is_pay = is_pay;
    }

    public String getAsmr() {
        return asmr;
    }

    public void setAsmr(String amsr) {
        this.asmr = amsr;
    }

    public static class VideoChatBean {

        private String file_path; //小视频地址

        private String img_path; //小视频的截图

        public VideoChatBean() {
        }
        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

        public String getImg_path() {
            return img_path;
        }

        public void setImg_path(String img_path) {
            this.img_path = img_path;
        }

        @Override
        public String toString() {
            return "{file_path="+file_path + ", img_path="+img_path+"}";
        }
    }

    @Override
    public int getItemType() {
        //直播间
        if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_ROOM)){
            itemType= LiveListOneAdapter.ITEM_TYPE_ROOM;
        //在线视频通话
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_PRIVATE)){
            itemType= LiveListOneAdapter.ITEM_TYPE_PRIVATE;
        //广告Banners集合
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_BANNERS)){
            itemType= LiveListOneAdapter.ITEM_TYPE_BANNERS;
        //单条广告
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_BANNER)){
            itemType= LiveListOneAdapter.ITEM_TYPE_BANNER;
        //推荐标题
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_RECOMMEND)){
            itemType= LiveListOneAdapter.ITEM_TYPE_RECOMMEND;
        //空闲状态1v1视频通话
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_VIDEOCALL)){
            itemType= LiveListOneAdapter.ITEM_TYPE_PRIVATE;
        //勿扰模式1v1
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_QUITE)){
            itemType= LiveListOneAdapter.ITEM_TYPE_PRIVATE;
        //空闲状态1v1
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_FREE)){
            itemType= LiveListOneAdapter.ITEM_TYPE_PRIVATE;
        //空离线状态1v1
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_OFFLINE)){
            itemType= LiveListOneAdapter.ITEM_TYPE_PRIVATE;
        //关注列表为空
        }else if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_EMPTY)){
            itemType= LiveListOneAdapter.ITEM_TYPE_EMPTY;
        //识别不出来的
        }else{
            itemType=LiveListOneAdapter.ITEM_TYPE_UNKNOWN;
        }
        return itemType;//暂时处理为默认的直播间
    }

    @Override
    public String toString() {
        return "RoomList{" +
                "online=" + online +
                ", roomid='" + roomid + '\'' +
                ", room_id='" + room_id + '\'' +
                ", push_stream='" + push_stream + '\'' +
                ", push_stream_flv='" + push_stream_flv + '\'' +
                ", push_stream_m3u8='" + push_stream_m3u8 + '\'' +
                ", play_url_flv='" + play_url_flv + '\'' +
                ", play_url_m3u8='" + play_url_m3u8 + '\'' +
                ", play_url_rtmp='" + play_url_rtmp + '\'' +
                ", chat_deplete=" + chat_deplete +
                ", chat_minite=" + chat_minite +
                ", userid='" + userid + '\'' +
                ", title='" + title + '\'' +
                ", frontcover='" + frontcover + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", user_frontcover='" + user_frontcover + '\'' +
                ", room_type=" + room_type +
                ", member_total=" + member_total +
                ", city='" + city + '\'' +
                ", distance=" + distance +
                ", id=" + id +
                ", latitude=" + latitude +
                ", level_integral=" + level_integral +
                ", longitude=" + longitude +
                ", sex=" + sex +
                ", vip=" + vip +
                ", is_family=" + is_family +
                ", is_online=" + is_online +
                ", user_state='" + user_state + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", itemType=" + itemType +
                ", banners=" + banners +
                ", playUrl='" + playUrl + '\'' +
                ", videoCover='" + videoCover + '\'' +
                ", family=" + family +
                ", pushers=" + pushers +
                ", my_image_list=" + my_image_list +
                ", chat_time=" + chat_time +
                ", video_chat=" + video_chat +
                ", signature='" + signature + '\'' +
                ", is_pay='" + is_pay + '\'' +
                ", asmr='" + asmr + '\'' +
                '}';
    }
}
