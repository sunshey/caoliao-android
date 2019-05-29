package com.yc.liaolive.live.bean;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

/**
 * TinyHung@Outlook.com
 * 2018/5/16
 * 直播间列表信息
 */

public class LiveItemInfo implements Parcelable{

    public String userid;
    public String groupid;
//    public int   timestamp;
    public boolean  livePlay;
    public int viewerCount;
    public int likeCount;
    public String title;
    public String playurl;
    public String fileid;
    public String nickname;
    public String headpic;
    public String frontcover;
    public String location;
    public String avatar;
    public String createTime;
    public String startTime;
    public String hlsPlayUrl;
    public int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public boolean isLivePlay() {
        return livePlay;
    }

    public void setLivePlay(boolean livePlay) {
        this.livePlay = livePlay;
    }

    public int getViewerCount() {
        return viewerCount;
    }

    public void setViewerCount(int viewerCount) {
        this.viewerCount = viewerCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getFrontcover() {
        return frontcover;
    }

    public void setFrontcover(String frontcover) {
        this.frontcover = frontcover;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getHlsPlayUrl() {
        return hlsPlayUrl;
    }

    public void setHlsPlayUrl(String hlsPlayUrl) {
        this.hlsPlayUrl = hlsPlayUrl;
    }

    public LiveItemInfo() {
        super();
    }

    public LiveItemInfo(JSONObject data) {
        try {
            this.userid     = data.optString("userid");
            this.nickname   = data.optString("nickname");
            this.avatar     = data.optString("avatar");
            this.fileid     = data.optString("file_id");
            this.title      = data.optString("title");
            this.frontcover = data.optString("frontcover");
            this.location   = data.optString("location");
            this.playurl    = data.optString("play_url");
            this.hlsPlayUrl = data.optString("hls_play_url");
            this.createTime = data.optString("create_time");
            this.likeCount  = data.optInt("like_count");
            this.viewerCount  = data.optInt("viewer_count");
            this.startTime  = data.optString("start_time");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected LiveItemInfo(Parcel in) {
        userid = in.readString();
        groupid = in.readString();
        livePlay = in.readByte() != 0;
        viewerCount = in.readInt();
        likeCount = in.readInt();
        title = in.readString();
        playurl = in.readString();
        fileid = in.readString();
        nickname = in.readString();
        headpic = in.readString();
        frontcover = in.readString();
        location = in.readString();
        avatar = in.readString();
        createTime = in.readString();
        startTime = in.readString();
        hlsPlayUrl = in.readString();
        price = in.readInt();
    }

    public static final Creator<LiveItemInfo> CREATOR = new Creator<LiveItemInfo>() {
        @Override
        public LiveItemInfo createFromParcel(Parcel in) {
            return new LiveItemInfo(in);
        }

        @Override
        public LiveItemInfo[] newArray(int size) {
            return new LiveItemInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(groupid);
        dest.writeByte((byte) (livePlay ? 1 : 0));
        dest.writeInt(viewerCount);
        dest.writeInt(likeCount);
        dest.writeString(title);
        dest.writeString(playurl);
        dest.writeString(fileid);
        dest.writeString(nickname);
        dest.writeString(headpic);
        dest.writeString(frontcover);
        dest.writeString(location);
        dest.writeString(avatar);
        dest.writeString(createTime);
        dest.writeString(startTime);
        dest.writeString(hlsPlayUrl);
        dest.writeInt(price);
    }

}
