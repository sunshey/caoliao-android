package com.yc.liaolive.index.model.bean;

import com.yc.liaolive.live.bean.RoomList;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播间、视频聊对象
 * Created by yangxueqin on 2019/1/25.
 */

public class OneListBean{
    private List<RoomList> list = new ArrayList<>();

    private String image_small_show; //0 大图模式 1 小图模式

    OneListBean () {

    }

    public List<RoomList> getList() {
        return list;
    }

    public void setList(List<RoomList> list) {
        this.list = list;
    }

    public String getImage_small_show() {
        return image_small_show;
    }

    public void setImage_small_show(String image_small_show) {
        this.image_small_show = image_small_show;
    }
}