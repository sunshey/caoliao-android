package com.yc.liaolive.user.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线用户列表数据
 * Created by yangxueqin on 18/12/14.
 */

public class OnlineUserBean implements Parcelable {

    private String call_time; //呼叫用户间隔时间 单位秒

    private String call_sum_num; //当天可呼叫用户未接通次数

    private String has_more_data = "0"; //是否还有更多数据 0没有 1有

    private List<OnlineUserItemBean> list = new ArrayList<>();

    protected OnlineUserBean(Parcel in) {
        call_time = in.readString();
        call_sum_num = in.readString();
        has_more_data = in.readString();
        list = in.createTypedArrayList(OnlineUserItemBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(call_time);
        dest.writeString(call_sum_num);
        dest.writeString(has_more_data);
        dest.writeTypedList(list);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OnlineUserBean> CREATOR = new Creator<OnlineUserBean>() {
        @Override
        public OnlineUserBean createFromParcel(Parcel in) {
            return new OnlineUserBean(in);
        }

        @Override
        public OnlineUserBean[] newArray(int size) {
            return new OnlineUserBean[size];
        }
    };

    public String getCall_time() {
        return call_time;
    }

    public void setCall_time(String call_time) {
        this.call_time = call_time;
    }

    public String getCall_sum_num() {
        return call_sum_num;
    }

    public void setCall_sum_num(String call_sum_num) {
        this.call_sum_num = call_sum_num;
    }

    public String getHas_more_data() {
        return has_more_data;
    }

    public void setHas_more_data(String has_more_data) {
        this.has_more_data = has_more_data;
    }

    public List<OnlineUserItemBean> getList() {
        return list;
    }

    public void setList(List<OnlineUserItemBean> list) {
        this.list = list;
    }

    public OnlineUserBean() {
    }

    public static class OnlineUserItemBean implements Parcelable {

        private String userid;

        private String nickname;

        private String avatar;

        private String money;

        private String msg_chat_but; //私信聊天  0不显示 1可点击  2不可点击

        private String video_chat_but; //视频聊天  0不显示 1可点击  2不可点击

        private String vip; //0非vip 1vip

        private String login_time;//用户登录时间

        private String call_num; //可呼叫测试

        private int redpoint_count = 0;

        public OnlineUserItemBean() {
        }

        protected OnlineUserItemBean(Parcel in) {
            userid = in.readString();
            nickname = in.readString();
            avatar = in.readString();
            money = in.readString();
            msg_chat_but = in.readString();
            video_chat_but = in.readString();
            vip = in.readString();
            login_time = in.readString();
            call_num = in.readString();
            redpoint_count = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userid);
            dest.writeString(nickname);
            dest.writeString(avatar);
            dest.writeString(money);
            dest.writeString(msg_chat_but);
            dest.writeString(video_chat_but);
            dest.writeString(vip);
            dest.writeString(login_time);
            dest.writeString(call_num);
            dest.writeInt(redpoint_count);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<OnlineUserItemBean> CREATOR = new Creator<OnlineUserItemBean>() {
            @Override
            public OnlineUserItemBean createFromParcel(Parcel in) {
                return new OnlineUserItemBean(in);
            }

            @Override
            public OnlineUserItemBean[] newArray(int size) {
                return new OnlineUserItemBean[size];
            }
        };

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
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

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMsg_chat_but() {
            return msg_chat_but;
        }

        public void setMsg_chat_but(String msg_chat_but) {
            this.msg_chat_but = msg_chat_but;
        }

        public String getVideo_chat_but() {
            return video_chat_but;
        }

        public void setVideo_chat_but(String video_chat_but) {
            this.video_chat_but = video_chat_but;
        }

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getLogin_time() {
            return login_time;
        }

        public void setLogin_time(String login_time) {
            this.login_time = login_time;
        }

        public String getCall_num() {
            return call_num;
        }

        public void setCall_num(String call_num) {
            this.call_num = call_num;
        }

        public int getRedpoint_count() {
            return redpoint_count;
        }

        public void setRedpoint_count(int redpoint_count) {
            this.redpoint_count = redpoint_count;
        }
    }
}
