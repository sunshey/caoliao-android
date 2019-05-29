package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@outlook.com
 * 2017/6/1 14:57
 * 粉丝列表  //image_bg
 */
public class FansInfo implements Parcelable {

    private String avatar;
    private int identity;//1：主播 2：观众
    private int is_online;//1：在直播间 2：不在直播间
    private int level_integral;
    private String nickname;
    private int sex;
    private String userid;
    private int vip;
    private String position;
    private String signature;
    private int day_points;
    private int total_points;
    private String black_userid;
    private long addtime;
    private long edittime;
    private String phone;
    private String desp;//客服说明
    private String playerid;
    private int today;
    private String file_id;

    public FansInfo() {

    }

    protected FansInfo(Parcel in) {
        avatar = in.readString();
        identity = in.readInt();
        is_online = in.readInt();
        level_integral = in.readInt();
        nickname = in.readString();
        sex = in.readInt();
        userid = in.readString();
        vip = in.readInt();
        position = in.readString();
        signature = in.readString();
        day_points = in.readInt();
        total_points = in.readInt();
        black_userid = in.readString();
        addtime = in.readLong();
        edittime = in.readLong();
        phone = in.readString();
        desp = in.readString();
        playerid = in.readString();
        today = in.readInt();
        file_id = in.readString();
    }

    public static final Creator<FansInfo> CREATOR = new Creator<FansInfo>() {
        @Override
        public FansInfo createFromParcel(Parcel in) {
            return new FansInfo(in);
        }

        @Override
        public FansInfo[] newArray(int size) {
            return new FansInfo[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public int getLevel_integral() {
        return level_integral;
    }

    public void setLevel_integral(int level_integral) {
        this.level_integral = level_integral;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getDay_points() {
        return day_points;
    }

    public void setDay_points(int day_points) {
        this.day_points = day_points;
    }

    public int getTotal_points() {
        return total_points;
    }

    public void setTotal_points(int total_points) {
        this.total_points = total_points;
    }

    public String getBlack_userid() {
        return black_userid;
    }

    public void setBlack_userid(String black_userid) {
        this.black_userid = black_userid;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getEdittime() {
        return edittime;
    }

    public void setEdittime(long edittime) {
        this.edittime = edittime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public int getToday() {
        return today;
    }

    public void setToday(int today) {
        this.today = today;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    @Override
    public String toString() {
        return "FansInfo{" +
                "avatar='" + avatar + '\'' +
                ", identity=" + identity +
                ", is_online=" + is_online +
                ", level_integral=" + level_integral +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", userid='" + userid + '\'' +
                ", vip=" + vip +
                ", position='" + position + '\'' +
                ", signature='" + signature + '\'' +
                ", day_points=" + day_points +
                ", total_points=" + total_points +
                ", black_userid='" + black_userid + '\'' +
                ", addtime=" + addtime +
                ", edittime=" + edittime +
                ", phone='" + phone + '\'' +
                ", desp='" + desp + '\'' +
                ", playerid='" + playerid + '\'' +
                ", today=" + today +
                ", file_id='" + file_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeInt(identity);
        dest.writeInt(is_online);
        dest.writeInt(level_integral);
        dest.writeString(nickname);
        dest.writeInt(sex);
        dest.writeString(userid);
        dest.writeInt(vip);
        dest.writeString(position);
        dest.writeString(signature);
        dest.writeInt(day_points);
        dest.writeInt(total_points);
        dest.writeString(black_userid);
        dest.writeLong(addtime);
        dest.writeLong(edittime);
        dest.writeString(phone);
        dest.writeString(desp);
        dest.writeString(playerid);
        dest.writeInt(today);
        dest.writeString(file_id);
    }
}
