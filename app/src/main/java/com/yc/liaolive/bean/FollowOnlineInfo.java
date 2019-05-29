package com.yc.liaolive.bean;

import com.yc.liaolive.live.bean.RoomList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/13
 * 我关注的列表
 */

public class FollowOnlineInfo {

    List<RoomList> list;//关注的
    List<RoomList> recommendList;//推荐的

    private int type;

    public List<RoomList> getList() {
        return list;
    }

    public void setList(List<RoomList> list) {
        this.list = list;
    }

    public List<RoomList> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<RoomList> recommendList) {
        this.recommendList = recommendList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
