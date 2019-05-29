package com.yc.liaolive.live.bean;

import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.FansInfo;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/4
 * 直播间初始化信息
 */

public class RoomInitInfo {
    //在线观众
    private List<FansInfo> members;
    private RoomInit roominfo;
    private List<BannerInfo> popup_page;

    public List<FansInfo> getMembers() {
        return members;
    }

    public void setMembers(List<FansInfo> members) {
        this.members = members;
    }

    public RoomInit getRoominfo() {
        return roominfo;
    }

    public void setRoominfo(RoomInit roominfo) {
        this.roominfo = roominfo;
    }

    public List<BannerInfo> getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(List<BannerInfo> popup_page) {
        this.popup_page = popup_page;
    }

    public class RoomInit{

        /**
         * attent : 1
         * day_jifen : 184412
         * total_jifen : 5825252
         */
        private int attent;
        private long day_jifen;
        private long total_jifen;
        private long online_num;//在线人数

        public int getAttent() {
            return attent;
        }

        public void setAttent(int attent) {
            this.attent = attent;
        }

        public long getDay_jifen() {
            return day_jifen;
        }

        public void setDay_jifen(long day_jifen) {
            this.day_jifen = day_jifen;
        }

        public long getTotal_jifen() {
            return total_jifen;
        }

        public void setTotal_jifen(long total_jifen) {
            this.total_jifen = total_jifen;
        }

        public long getOnline_num() {
            return online_num;
        }

        public void setOnline_num(long online_num) {
            this.online_num = online_num;
        }

        @Override
        public String toString() {
            return "RoomInit{" +
                    "attent=" + attent +
                    ", day_jifen=" + day_jifen +
                    ", total_jifen=" + total_jifen +
                    ", online_num=" + online_num +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RoomInitInfo{" +
                "members=" + members +
                ", roominfo=" + roominfo +
                ", popup_page=" + popup_page +
                '}';
    }
}
