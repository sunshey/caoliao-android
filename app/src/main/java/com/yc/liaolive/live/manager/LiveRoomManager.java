package com.yc.liaolive.live.manager;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.room.LiveRoom;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/5/12
 * 直播间、在线主播 数据Manager
 */

public class LiveRoomManager {

    private static LiveRoomManager mInstance;
    private static LiveRoom liveRoom = null;
    private List<RoomList> mData;
    private int mPosition;//当前已经预览到第几页了
    private int page;//数据加载到了第几页
    private String type;//数据加载到了第几页
    private String lastUserID;//数据的最后一个用户的IF

    public static synchronized LiveRoomManager getInstance() {
        synchronized (LiveRoomManager.class){
            if(null == mInstance){
                mInstance=new LiveRoomManager();
            }
        }
        return mInstance;
    }

    public LiveRoom getLiveRoom(){
        if(null==liveRoom){
            liveRoom=new LiveRoom(AppEngine.getApplication().getApplicationContext());
        }
        return liveRoom;
    }

    public List<RoomList> getData() {
        return mData;
    }

    public LiveRoomManager setData(List<RoomList> data) {
        mData = data;
        return mInstance;
    }

    public int getPosition() {
        return mPosition;
    }

    public LiveRoomManager setPosition(int position) {
        mPosition = position;
        return mInstance;
    }

    public int getPage() {
        return page;
    }

    public LiveRoomManager setPage(int page) {
        this.page = page;
        return mInstance;
    }

    public String getType() {
        return type;
    }

    public LiveRoomManager setType(String type) {
        this.type = type;
        return mInstance;
    }

    public String getLastUserID() {
        return lastUserID;
    }

    public LiveRoomManager setLastUserID(String lastUserID) {
        this.lastUserID = lastUserID;
        return mInstance;
    }

    public void addData(List<RoomList> data) {
        if(null!=mData){
            mData.addAll(data);
        }
    }
}