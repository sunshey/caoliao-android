package com.yc.liaolive.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 视频聊详情 获取视频聊接通前的详情页
 * Created by yangxueqin on 2018/12/6.
 */

public class VideoChatInfo implements Parcelable {

    /**
     * is_attention : 1
     * nickname : ﹏『安』゛若兮
     * chat_deplete : 1500
     * signature :
     * avatar : http://thirdqq.qlogo.cn/qqapp/1106846629/753F0E0DB8358138A3BAEBCE7CFD10EA/100
     * user_state : offline
     * label : ["性感","萝莉","内衣秀","学生","挑逗","邻家女孩"]
     */

    private int is_attention;
    private String nickname;
    private int chat_deplete;
    private String signature;
    private String avatar;
    private String user_state;
    private List<String> label;

    public VideoChatInfo() {
    }

    protected VideoChatInfo(Parcel in) {
        is_attention = in.readInt();
        nickname = in.readString();
        chat_deplete = in.readInt();
        signature = in.readString();
        avatar = in.readString();
        user_state = in.readString();
        label = in.createStringArrayList();
    }

    public static final Creator<VideoChatInfo> CREATOR = new Creator<VideoChatInfo>() {
        @Override
        public VideoChatInfo createFromParcel(Parcel in) {
            return new VideoChatInfo(in);
        }

        @Override
        public VideoChatInfo[] newArray(int size) {
            return new VideoChatInfo[size];
        }
    };

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(is_attention);
        dest.writeString(nickname);
        dest.writeInt(chat_deplete);
        dest.writeString(signature);
        dest.writeString(avatar);
        dest.writeString(user_state);
        dest.writeStringList(label);
    }
}
