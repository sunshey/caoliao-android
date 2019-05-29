package com.yc.liaolive.live.util;

import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.adapter.LiveFansListAdapter;
import com.yc.liaolive.live.ui.contract.LiveControllerInterface;
import com.yc.liaolive.live.view.VideoLiveControllerView;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/8/13
 * 直播间数据缓存池
 */

public class RoomDataCache {

    private static final String TAG = "RoomDataCache";
    private static RoomDataCache mInstance;
    private static List<FansInfo> mTempOnlines = new ArrayList<>();//临时的在线观众列表
    private int priceCount;
    private LiveControllerInterface controllerInterface;

    public static synchronized RoomDataCache getInstance(){
        synchronized (RoomDataCache.class){
            if(null==mInstance){
                mInstance=new RoomDataCache();
            }
        }
        return mInstance;
    }

    public void registerHostView(LiveControllerInterface controllerInterface){
        this.controllerInterface=controllerInterface;
    }

    /**
     * 更新最新的在线成员缓存列表
     * @param newOnlines
     */
    public synchronized void putOnlineToCache(List<FansInfo> newOnlines){
        if(null== mTempOnlines) mTempOnlines=new ArrayList<>();
        mTempOnlines.clear();
        mTempOnlines.addAll(newOnlines);
    }

    /**
     * 刷新在线列表
     * @param adapter
     * @param scenMode
     */
    public synchronized void updataOnlines(LiveFansListAdapter adapter, int scenMode){
        if(null==adapter) return;
        //发生了变化
        if(null != mTempOnlines && mTempOnlines.size() > 0){
            checkedUserPoistion(mTempOnlines, scenMode);
            adapter.setNewData(null);
            try {
                List<FansInfo> data = adapter.getData();
                for (FansInfo tempOnline: mTempOnlines) {
                    data.add(tempOnline);
                }
                mTempOnlines.clear();
                adapter.notifyDataSetChanged();
            }catch (RuntimeException e){
                if(null!=controllerInterface) controllerInterface.onMemberReset();
            }
            return;
        }
    }


    /**
     * 将自己排在首位
     */
    private synchronized void checkedUserPoistion(List<FansInfo> mOnlines, int scenMode){
        //推流模式下直接显示列表
        if(scenMode == VideoLiveControllerView.LIVE_SCENE_PUSH ){
            return ;
        }
        try {
            //添加自己至首位
            int poistion = -1;
            for (int i = 0; i < mOnlines.size(); i++ ) {
                if(UserManager.getInstance().getUserId().equalsIgnoreCase(mOnlines.get(i).getUserid())){
                    poistion = i;
                    break;
                }
            }
            if(-1 == poistion){
                //不存在自己，将自己添加至首位
                FansInfo roomAudienceInfo = new FansInfo();
                roomAudienceInfo.setLevel_integral(UserManager.getInstance().getUserGradle());
                roomAudienceInfo.setAvatar(UserManager.getInstance().getAvatar());
                roomAudienceInfo.setUserid(UserManager.getInstance().getUserId());
                roomAudienceInfo.setNickname(UserManager.getInstance().getNickname());
                mOnlines.add(0, roomAudienceInfo);
            } else {
                //存在自己，将自己移动至首位
                FansInfo remove = mOnlines.remove(poistion);
                mOnlines.add(0, remove);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 对应函数调用
     */
    public void onDestroy(){
        if(null!= mTempOnlines) mTempOnlines.clear();
        controllerInterface=null;
        priceCount=0;
    }
}
