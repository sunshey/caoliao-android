package com.yc.liaolive.gift.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.gift.listener.AnimatorPlayListener;
import com.yc.liaolive.gift.view.RoomSuperAwardAnimaorView;
import com.yc.liaolive.live.bean.AwardInfo;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/16
 * 直播间超级大奖动画播放管理者
 */

public class RoomSuperAwardAnimatorGroupManager extends FrameLayout {

    private static final String TAG = "RoomSuperAwardAnimatorGroupManager";
    private Queue<AwardInfo> mInfoArrayDeque;
    private boolean isRunning;//队列是否正在执行
    private RoomSuperAwardAnimaorView.WindownMode windowMode= RoomSuperAwardAnimaorView.WindownMode.FULL;
    private RoomSuperAwardAnimaorView mAwardAnimaorLayout;
    private long CLEAN_MILLIS =10000;//10秒后清除自己
    private int groupAniCount=23;//一组动画中有多少个元素
    private AnimatorPlayListener mAnimatorPlayListener;

    public RoomSuperAwardAnimatorGroupManager(@NonNull Context context) {
        this(context,null);
    }

    public RoomSuperAwardAnimatorGroupManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_award_play_layout,this);
        mInfoArrayDeque=new ArrayDeque<>();
    }

    /**
     * 设置动画播放延续时长
     * @param CLEAN_MILLIS 单位毫秒
     */
    public void setAutoCleanMillis(long CLEAN_MILLIS) {
        this.CLEAN_MILLIS = CLEAN_MILLIS;
    }

    /**
     * 设置应用场景
     * @param windowMode
     */
    public void setWindowMode(RoomSuperAwardAnimaorView.WindownMode windowMode){
        this.windowMode=windowMode;
    }

    /**
     * 设置一组动画(3秒内)的金币掉落个数
     */
    public void setGroupAniCount(int groupAniCount){
        this.groupAniCount = groupAniCount;
    }
    /**
     * 添加一个中奖信息至队列
     * @param awardInfo
     */
    public void addAwardToTask(AwardInfo awardInfo){
        if(null==mInfoArrayDeque) mInfoArrayDeque=new ArrayDeque<>();
        mInfoArrayDeque.add(awardInfo);
        startTask();
    }

    /**
     * 开始任务
     */
    private void startTask() {
        if(null!=mInfoArrayDeque&&mInfoArrayDeque.size()>0){
            if(isRunning) return;
            isRunning=true;
            RoomSuperAwardAnimatorGroupManager.this.removeAllViews();
            final AwardInfo awardInfo = mInfoArrayDeque.poll();
            if(null!=awardInfo){
                mAwardAnimaorLayout = new RoomSuperAwardAnimaorView(getContext());
                mAwardAnimaorLayout.setWindowMode(windowMode);
                mAwardAnimaorLayout.setGroupAniCount(groupAniCount);
                mAwardAnimaorLayout.startAnimatior("恭喜“"+awardInfo.getNickName()+"”","喜获 "+awardInfo.getMonery()+" 钻石",awardInfo.isMine());
                RoomSuperAwardAnimatorGroupManager.this.addView(mAwardAnimaorLayout);
                AnimationUtil.startSuperAwardAnimIn(mAwardAnimaorLayout, 500, new AnimatorPlayListener() {
                    @Override
                    public void onStart(AwardInfo awardInfo) {

                    }

                    @Override
                    public void onEnd() {
                        if(null!=mAnimatorPlayListener) mAnimatorPlayListener.onStart(awardInfo);
                    }
                });
            }
            RoomSuperAwardAnimatorGroupManager.this.postDelayed(cleanAwardRunnable,CLEAN_MILLIS);
        }
    }

    private Runnable cleanAwardRunnable=new Runnable() {
        @Override
        public void run() {
            RoomSuperAwardAnimatorGroupManager.this.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mAwardAnimaorLayout) mAwardAnimaorLayout.onDestroy();
                    AnimationUtil.startSuperAwardAnimOut(mAwardAnimaorLayout, 500, new AnimatorPlayListener() {
                        @Override
                        public void onStart(AwardInfo awardInfo) {

                        }

                        @Override
                        public void onEnd() {
                            if(null!=mAwardAnimaorLayout){
                                mAwardAnimaorLayout.onDestroy();
                                mAwardAnimaorLayout=null;
                            }
                            RoomSuperAwardAnimatorGroupManager.this.removeAllViews();
                            isRunning=false;
                            if(null!=mAnimatorPlayListener) mAnimatorPlayListener.onEnd();
                            startTask();
                        }
                    });
                }
            });
        }
    };

    /**
     * 对应生命周期调用
     */
    public void onResume(){
        startTask();
    }

    /**
     * 对应生命周期调用
     */
    public void onPause(){
        onReset();
    }

    public void onReset() {
        if(null!=mAwardAnimaorLayout){
            mAwardAnimaorLayout.onPause();
            this.removeCallbacks(cleanAwardRunnable);
            this.removeAllViews();
            isRunning=false;
            mAwardAnimaorLayout=null;
        }
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        if(null!=mAwardAnimaorLayout) mAwardAnimaorLayout.onDestroy();
        this.removeCallbacks(cleanAwardRunnable);
        this.removeAllViews();
        isRunning=false;
        if(null!=mInfoArrayDeque)  mInfoArrayDeque.clear();
        mInfoArrayDeque=null;  mAwardAnimaorLayout=null;
    }

    /**
     * 监听动画播放状态监听
     * @param listener
     */
    public void setAnimatorPlayListener(AnimatorPlayListener listener){
        this.mAnimatorPlayListener=listener;
    }
}
