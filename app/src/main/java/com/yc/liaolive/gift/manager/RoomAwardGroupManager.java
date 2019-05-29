package com.yc.liaolive.gift.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.gift.listener.OnFunctionListener;
import com.yc.liaolive.gift.view.RoomSuperAwardAnimaorView;
import com.yc.liaolive.gift.view.RoomAwardItemView;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * TinyHung@Outlook.com
 * 2018/12/15
 * 直播间中奖信息公示动画管理者
 */

public class RoomAwardGroupManager extends FrameLayout {

    private List<RoomAwardItemView> mRoomAwardItemViews;
    private static final String TAG = "RoomAwardGroupManager";
    private Queue<CustomMsgInfo> mGroupGiftQueue;//父队列，负责统一接收任务，心跳线程中分发任务给子队列
    private Queue<CustomMsgInfo> mChildGiftQueue1=new ArrayDeque<>();//子队列1
    private Queue<CustomMsgInfo> mChildGiftQueue2=new ArrayDeque<>();//子队列2
    private Queue<CustomMsgInfo> mChildGiftQueue3=new ArrayDeque<>();//子队列3
    private boolean taskRunning;//礼物动画是否正在执行
    private RoomSuperAwardAnimatorGroupManager mAwardAnimatorMinPlayManager;//超级大机箱
    private boolean isMultiplex=false;//条目动画是否复用

    public RoomAwardGroupManager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RoomAwardGroupManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 中奖动画个数初始化
     * @param context
     */
    private void init(Context context) {
        View.inflate(context, R.layout.view_room_award_group_layout,this);
        RoomAwardItemView giftItemView1 = (RoomAwardItemView) findViewById(R.id.view_award_child1);
        RoomAwardItemView giftItemView2 = (RoomAwardItemView) findViewById(R.id.view_award_child2);
        RoomAwardItemView giftItemView3 = (RoomAwardItemView) findViewById(R.id.view_award_child3);
        mRoomAwardItemViews =new ArrayList<>();
        mRoomAwardItemViews.add(giftItemView1);
        mRoomAwardItemViews.add(giftItemView2);
        mRoomAwardItemViews.add(giftItemView3);
        if(ScreenUtils.getScreenDensity()<300){
            mRoomAwardItemViews.remove(2);
            mChildGiftQueue3=null;
            this.removeView(giftItemView3);
            this.invalidate();
        }
        for (RoomAwardItemView roomAwardItemView : mRoomAwardItemViews) {
            roomAwardItemView.setOnFunctionListener(new OnFunctionListener() {
                @Override
                public void onSendGift(FansInfo userInfo) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.onSendGift(userInfo);
                }
            });
        }
        //超级大奖
        mAwardAnimatorMinPlayManager = (RoomSuperAwardAnimatorGroupManager) findViewById(R.id.award_animator_min_manager);
        mAwardAnimatorMinPlayManager.setWindowMode(RoomSuperAwardAnimaorView.WindownMode.SMALL);
        mAwardAnimatorMinPlayManager.setGroupAniCount(6);
        mAwardAnimatorMinPlayManager.setAutoCleanMillis(10000);//动画延续时长，毫秒
    }

    /**
     * 设定ItemView条目是否复用
     * @param multiplex
     */
    public void setMultiplex(boolean multiplex) {
        isMultiplex = multiplex;
        if(null!= mRoomAwardItemViews){
            for (RoomAwardItemView roomAwardItemView : mRoomAwardItemViews) {
                roomAwardItemView.setMultiplex(multiplex);
            }
        }
    }

    /**
     * 绑定用户身份
     * @param identityType
     */
    public void setIdentityType(int identityType) {
        if(null!= mRoomAwardItemViews){
            for (RoomAwardItemView roomAwardItemView : mRoomAwardItemViews) {
                roomAwardItemView.setIdentityType(identityType);
            }
        }
    }

    /**
     * 返回动画实例
     * @return
     */
    public RoomSuperAwardAnimatorGroupManager getAwardAnimatorMinPlayManager(){
        return mAwardAnimatorMinPlayManager;
    }

    /**
     * 添加一个中奖信息
     * @param data 中奖数据
     */
    public synchronized void addAwardItemToLayout(CustomMsgInfo data){
        if(null== mRoomAwardItemViews ||null==data) return;
        GiftInfo giftInfo = data.getGift();
        if(null==giftInfo) return;
        if(null==mGroupGiftQueue) mGroupGiftQueue=new ArrayDeque<>();
        if(!isMultiplex){
            mGroupGiftQueue.add(data);
            return;
        }
        String tagID=data.getSendUserID()+giftInfo.getId()+data.getAccapUserID();
        //1.中奖信息已经在前台队列中
        for (int i = 0; i < mRoomAwardItemViews.size(); i++) {
            RoomAwardItemView frameLayout = mRoomAwardItemViews.get(i);
            //当前队列正在显示的View属性与新动画相符
            if(null != frameLayout.getTag() && TextUtils.equals(tagID, (String) frameLayout.getTag())){
                addTask(data,i);
                return;
            }
        }
        //新礼物 添加至总队任务队列，等待空闲子队列产生
        mGroupGiftQueue.add(data);
    }

    /**
     * 开启动画播放任务
     */
    public void startPlayTask(){
        if(taskRunning) return;
        new Thread(){
            @Override
            public void run() {
                super.run();
                taskRunning =true;
                while (taskRunning){
                    playGiftAnimation();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 开始普通礼物的播放逻辑
     */
    private synchronized void playGiftAnimation() {
        if(null== mRoomAwardItemViews) return;
        //先分配一把
        if(null!=mGroupGiftQueue&&mGroupGiftQueue.size()>0){
            ///取出最前面一个元素但不擦除源数据
            CustomMsgInfo peekInfo = mGroupGiftQueue.peek();
            if(null!=peekInfo&&null!=peekInfo.getGift()){
                if(!isMultiplex){
                    out: for (int i = 0; i < mRoomAwardItemViews.size(); i++) {
                        RoomAwardItemView frameLayout = mRoomAwardItemViews.get(i);
                        if(null==frameLayout.getTag()){
                            addTask(mGroupGiftQueue.poll(),i);
                            break out;
                        }
                    }
                }else{
                    String tagID=peekInfo.getSendUserID()+peekInfo.getGift().getId()+peekInfo.getAccapUserID();
                    //分配任务
                    out: for (int i = 0; i < mRoomAwardItemViews.size(); i++) {
                        RoomAwardItemView frameLayout = mRoomAwardItemViews.get(i);
                        //当前列表展示的属性和新的分配的相符
                        if(null!=frameLayout.getTag()&&TextUtils.equals(tagID, (String) frameLayout.getTag())){
                            addTask(mGroupGiftQueue.poll(),i);
                            Logger.d(TAG,"跳出循环1");
                            break out;
                        }else{
                            //新的动画任务
                            if(null==frameLayout.getTag()){
                                addTask(mGroupGiftQueue.poll(),i);
                                Logger.d(TAG,"跳出循环2");
                                break out;
                            }
                        }
                    }
                }
            }
        }
        //判断是否播放礼物动画,若有数据，同步播放礼物动画
        if(null!=mChildGiftQueue1&&mChildGiftQueue1.size()>0){
            addGiftItemView(mChildGiftQueue1.poll(),0);
        }
        //同上判断
        if(null!=mChildGiftQueue2&&mChildGiftQueue2.size()>0){
            addGiftItemView(mChildGiftQueue2.poll(),1);
        }
        //同上判断
        if(null!=mChildGiftQueue3&&mChildGiftQueue3.size()>0){
            addGiftItemView(mChildGiftQueue3.poll(),2);
        }
    }


    /**
     * 添加至对应任务队列
     * @param data
     * @param index
     */
    private void addTask(CustomMsgInfo data, int index) {
        switch (index) {
            case 0:
                if(null!=mChildGiftQueue1) mChildGiftQueue1.add(data);
                break;
            case 1:
                if(null!=mChildGiftQueue2) mChildGiftQueue2.add(data);
                break;
            case 2:
                if(null!=mChildGiftQueue3) mChildGiftQueue3.add(data);
                break;
        }
    }

    //========================================View动画的绘制和播放====================================
    /**
     * 添加到某个子队列下开始执行
     * @param pollInfo
     * @param index 父容器Index
     */
    private void addGiftItemView(final CustomMsgInfo pollInfo, final int index) {
        if(null==pollInfo||null==pollInfo.getGift()) return;
        this.post(new Runnable() {
            @Override
            public void run() {
                //tagID 在条目需要复用的情况下，有用处
                String tagID=pollInfo.getSendUserID()+pollInfo.getGift().getId()+pollInfo.getAccapUserID();
                addGifView(tagID,pollInfo,index);
            }
        });
    }

    /**
     * 增加一个礼物，
     * @param tagID 赠送人和礼物的ID合并起来的
     * @param msgInfo 赠送人的基本信息
     * @param index 父容器index
     */
    private void addGifView(String tagID, CustomMsgInfo msgInfo,int index) {
        if(null== mRoomAwardItemViews) return;
        //动画的父容器
        RoomAwardItemView frameLayout = mRoomAwardItemViews.get(index);
        boolean b = frameLayout.addGiftItem(tagID, msgInfo);
        if(!b){
            if(null==mGroupGiftQueue) mGroupGiftQueue=new ArrayDeque<>();
            Logger.d(TAG,"未添加成功，回收");
            mGroupGiftQueue.add(msgInfo);
        }
    }

    public void onReset(){
        taskRunning =false;
        if(null!=mGroupGiftQueue) mGroupGiftQueue.clear();
        if(null!=mChildGiftQueue1) mChildGiftQueue1.clear();
        if(null!=mChildGiftQueue2) mChildGiftQueue2.clear();
        if(null!=mChildGiftQueue3) mChildGiftQueue3.clear();
    }

    /**
     * 结束播放任务
     */
    public void stopPlayTask(){
        taskRunning =false;
        if(null!=mGroupGiftQueue) mGroupGiftQueue.clear(); mGroupGiftQueue=null;
        if(null!=mChildGiftQueue1) mChildGiftQueue1.clear(); mChildGiftQueue1=null;
        if(null!=mChildGiftQueue2) mChildGiftQueue2.clear();mChildGiftQueue2=null;
        if(null!=mChildGiftQueue3) mChildGiftQueue3.clear();mChildGiftQueue3=null;
    }

    public void onResume(){
        if(null!=mAwardAnimatorMinPlayManager) mAwardAnimatorMinPlayManager.onResume();
    }

    public void onPause() {
        if(null!=mAwardAnimatorMinPlayManager) mAwardAnimatorMinPlayManager.onPause();
    }

    /**
     * 对应方法调用
     */
    public void onDestroy(){
        stopPlayTask();
        if(null!= mRoomAwardItemViews){
            for (RoomAwardItemView awardItemChildView : mRoomAwardItemViews) {
                awardItemChildView.onDestroy();
            }
            mRoomAwardItemViews =null;
        }
        if(null!=mAwardAnimatorMinPlayManager) mAwardAnimatorMinPlayManager.onDestroy();
        mAwardAnimatorMinPlayManager=null;mOnFunctionListener=null;
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
