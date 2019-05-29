package com.yc.liaolive.live.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Created by jac on 2017/10/30.
 * 房间信息
 */

public class RoomInfo implements Parcelable {


    /**
     * avatar : http://thirdqq.qlogo.cn/qqapp/1106846629/CE8CD6280DB4E94AD94391FA8716E6BA/100
     * frontcover : http://zbtest.6071.com/uploads/images/20180702/3e3a59256b1fd0fa9e943a781c8547be.jpg
     * nickname : 快抢
     * play_url_flv : http://24603.liveplay.myqcloud.com/live/24603_28804734.flv
     * play_url_m3u8 : http://24603.liveplay.myqcloud.com/live/24603_28804734.m3u8
     * play_url_rtmp : rtmp://24603.liveplay.myqcloud.com/live/24603_28804734
     * pushers : [{"accelerateURL":"rtmp://24603.liveplay.myqcloud.com/live/24603_28804734","userAvatar":"http://thirdqq.qlogo.cn/qqapp/1106846629/CE8CD6280DB4E94AD94391FA8716E6BA/100","userID":"28804734","userName":"快抢"}]
     */
    private String userid;
    private String room_id;
    private String avatar;
    private String frontcover;
    private String nickname;
    private String title;
    private String play_url_flv;
    private String play_url_m3u8;
    private String play_url_rtmp;
    /**
     * accelerateURL : rtmp://24603.liveplay.myqcloud.com/live/24603_28804734
     * userAvatar : http://thirdqq.qlogo.cn/qqapp/1106846629/CE8CD6280DB4E94AD94391FA8716E6BA/100
     * userID : 28804734
     * userName : 快抢
     */

    public List<PusherInfo> pushers;

    public RoomInfo() {
        super();
    }

    private int member_total;
    private int minute_price;//视频聊天的套餐价格
    private int minute;//单位分钟

    protected RoomInfo(Parcel in) {
        avatar = in.readString();
        frontcover = in.readString();
        nickname = in.readString();
        play_url_flv = in.readString();
        play_url_m3u8 = in.readString();
        play_url_rtmp = in.readString();
        title = in.readString();
        userid = in.readString();
        room_id = in.readString();
        member_total = in.readInt();
        minute_price = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<RoomInfo> CREATOR = new Creator<RoomInfo>() {
        @Override
        public RoomInfo createFromParcel(Parcel in) {
            return new RoomInfo(in);
        }

        @Override
        public RoomInfo[] newArray(int size) {
            return new RoomInfo[size];
        }
    };

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getMember_total() {
        return member_total;
    }

    public void setMember_total(int member_total) {
        this.member_total = member_total;
    }

    public int getMinute_price() {
        return minute_price;
    }

    public void setMinute_price(int minute_price) {
        this.minute_price = minute_price;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
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

    public List<PusherInfo> getPushers() {
        return pushers;
    }

    public void setPushers(List<PusherInfo> pushers) {
        this.pushers = pushers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(frontcover);
        dest.writeString(nickname);
        dest.writeString(play_url_flv);
        dest.writeString(play_url_m3u8);
        dest.writeString(play_url_rtmp);
        dest.writeString(title);
        dest.writeString(userid);
        dest.writeString(room_id);
        dest.writeInt(member_total);
        dest.writeInt(minute_price);
        dest.writeInt(minute);
    }

    public static class PushersBean {
        private String accelerateURL;
        private String userAvatar;
        private String userID;
        private String userName;

        public String getAccelerateURL() {
            return accelerateURL;
        }

        public void setAccelerateURL(String accelerateURL) {
            this.accelerateURL = accelerateURL;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "userid='" + userid + '\'' +
                ", room_id='" + room_id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", frontcover='" + frontcover + '\'' +
                ", nickname='" + nickname + '\'' +
                ", title='" + title + '\'' +
                ", play_url_flv='" + play_url_flv + '\'' +
                ", play_url_m3u8='" + play_url_m3u8 + '\'' +
                ", play_url_rtmp='" + play_url_rtmp + '\'' +
                ", pushers=" + pushers +
                ", member_total=" + member_total +
                ", minute_price=" + minute_price +
                ", minute=" + minute +
                '}';
    }
}
