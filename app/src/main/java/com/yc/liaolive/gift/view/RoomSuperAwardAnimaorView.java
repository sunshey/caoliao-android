package com.yc.liaolive.gift.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.gold.FlakeView;
import com.yc.liaolive.view.widget.GoldWireLayout;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/16
 * 超级大奖动画
 */

public class RoomSuperAwardAnimaorView extends FrameLayout {

    private static final String TAG = "RoomSuperAwardAnimaorView";
    private ImageView mAnimatorBgImage;
    private FlakeView mFlakeView;
    private MediaPlayer mPlayer;
    private static final int MAX_PLAY_COUNT = 120;//金币掉落动画执行次数
    private int count=0;//金币掉落动画已执行了几遍
    private boolean isRuning;//金币掉落动画是否正在执行
    private TimerTask mTask;
    private Timer mTimer;
    private int[] mStartScrrenPosition;//金币掉落动画屏幕中XY轴起始位置
    private int[] mEndScrrenPosition;//金币掉落动画屏幕中XY轴结束位置
    private int groupAniCount=23;//一组动画中有多少个元素

    public enum WindownMode{
        FULL,  //全屏
        SMALL  //小窗口
    }
    public RoomSuperAwardAnimaorView(@NonNull Context context) {
        this(context,null);
    }

    public RoomSuperAwardAnimaorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_award_animator_layout,this);
        mAnimatorBgImage = (ImageView) findViewById(R.id.view_animator_bg_icon);
        mStartScrrenPosition =new int[2];
        mEndScrrenPosition=new int[2];
    }

    /**
     * 一键指定动画在直播间的使用场景
     * @param windowMode
     */
    public void setWindowMode(WindownMode windowMode){
        FrameLayout animatorGroup = (FrameLayout) findViewById(R.id.view_animator_group);
        if(windowMode==WindownMode.FULL){
            int width = ScreenUtils.getScreenWidth() - 80;
            animatorGroup.getLayoutParams().width= width;
            animatorGroup.getLayoutParams().height= width;
            setAnimatorBackground(R.drawable.ic_room_gift_award_bg_big);
            setAnimatorFront(R.drawable.ic_room_gift_award_big);
            setTextSize(23);
            setSubTextSize(23);
            mStartScrrenPosition[0]=ScreenUtils.getScreenWidth()/2;
            mStartScrrenPosition[1]=ScreenUtils.getScreenHeight()/2;

        }else if(windowMode==WindownMode.SMALL){
            int width = ScreenUtils.dpToPxInt(75f);
            int startY=ScreenUtils.dpToPxInt(70f)/2;
            if(ScreenUtils.getScreenDensity()<300){
                width=ScreenUtils.dpToPxInt(105f);
                startY=ScreenUtils.dpToPxInt(105f)/2;
            }
            animatorGroup.getLayoutParams().width= width;
            animatorGroup.getLayoutParams().height= width;
            setAnimatorBackground(R.drawable.ic_room_gift_award_bg_min);
            setAnimatorFront(R.drawable.ic_room_gift_award_min);
            setTextSize(10);
            setSubTextSize(10);
            mStartScrrenPosition[0]=ScreenUtils.dpToPxInt(158f)/2;
            mStartScrrenPosition[1]=startY;
        }
        mEndScrrenPosition[0]=(ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(50f));
        mEndScrrenPosition[1]= (ScreenUtils.getScreenHeight()-ScreenUtils.dpToPxInt(135f)) ;
    }

    /**
     * 设置金币掉落在屏幕的起始位置 [0]:X轴 [1]:Y轴
     * @param startPosition
     */
    public void setStartPosition(int[] startPosition){
        this.mStartScrrenPosition =startPosition;
    }

    /**
     * 设置金币掉落在屏幕的结束位置 [0]:X轴 [1]:Y轴
     * @param endPosition
     */
    public void setEndPosition(int[] endPosition){
        this.mEndScrrenPosition=endPosition;
    }

    /**
     * 设置金币掉落动画中一组动画的金币个数
     * @param groupAniCount
     */
    public void setGroupAniCount(int groupAniCount) {
        this.groupAniCount = groupAniCount;
    }

    /**
     * 指定动画显示的区域大小
     * @param width
     * @param height
     */
    public void setLayoutParams(int width,int height){
        FrameLayout animatorGroup = (FrameLayout) findViewById(R.id.view_animator_group);
        animatorGroup.getLayoutParams().width= width;
        animatorGroup.getLayoutParams().height= height;
    }

    /**
     * 设置背景旋转的素材
     * @param resID
     */
    public void setAnimatorBackground(int resID){
        if(null!=mAnimatorBgImage) mAnimatorBgImage.setImageResource(resID);
    }

    /**
     * 设置前景ICON
     * @param resID
     */
    public void setAnimatorFront(int resID){
        ((ImageView) findViewById(R.id.view_animator_icon)).setImageResource(resID);
    }

    /**
     * 标题文字大小
     * @param textSize
     */
    public void setTextSize(int textSize){
        ((TextView) findViewById(R.id.view_animator_title)).setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
    }

    /**
     * 副标题文字大小
     * @param textSize
     */
    public void setSubTextSize(int textSize){
        ((TextView) findViewById(R.id.view_animator_subtitle)).setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
    }
    
    /**
     * 开始动画
     * @param title  标题
     * @param subTitle   副标题
     * @param isMine 是自己中奖了嘛?
     */
    public void startAnimatior(String title, String subTitle, final boolean isMine){
        FrameLayout animatorGroup = (FrameLayout) findViewById(R.id.view_animator_group);
        TextView viewTitle = (TextView) findViewById(R.id.view_animator_title);
        TextView viewSubTitle = (TextView) findViewById(R.id.view_animator_subtitle);
        viewTitle.setText(title);
        viewSubTitle.setText(subTitle);
        mFlakeView = new FlakeView(getContext());
        mFlakeView.addFlakes(groupAniCount);
        mFlakeView.setLayerType(View.LAYER_TYPE_NONE, null);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        mFlakeView.setLayoutParams(layoutParams);
        mPlayer = MediaPlayer.create(getContext().getApplicationContext(), R.raw.haliluya);
        mPlayer.start();
        animatorGroup.addView(mFlakeView);
        //背景旋转
        if(null!=mAnimatorBgImage){
            RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(2000);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.setRepeatCount(Animation.INFINITE);//
            rotate.setRepeatMode(Animation.RESTART);//无限循环
            mAnimatorBgImage.startAnimation(rotate);
        }
        AnimationUtil.startSuperAwardUserAnim(viewTitle,4000,null);
        AnimationUtil.startSuperAwardUserAnim(viewSubTitle,4000,null);
        //监听动画执行完毕，开始金币掉落动画
//        AnimationUtil.startAwardAnim(viewSubTitle, 4000, new AnimatorPlayListener() {
//
//            @Override
//            public void onStart(AwardInfo awardInfo) {}
//
//            @Override
//            public void onEnd() {
//                if(isMine){
//                    if(null==mStartScrrenPosition||null==mEndScrrenPosition){
//                        throw new NullPointerException("Please make call setWindowMode || (setStartPosition && setEndPosition) method");
//                    }
//                    startTask();
//                }
//            }
//        });
    }

    /**
     * 开始执行
     */
    private void startTask(){
        /**
         * 中奖金币掉落场景位置
         * 起始位置X：在动画布局的X轴中心位置+100和-100像素之间
         * 起始位置Y：在动画布局的Y轴中心位置
         * 中间位置X：暂无
         * 中间位置Y：暂无
         * 终点位置X：直播间的倒计时按钮X轴中部偏上
         * 终点位置Y：直播间的倒计时按钮Y轴中部偏上
         */
        if(isRuning) return;
        count=0;
        isRuning=true;
        mTask = new TimerTask() {
            @Override
            public void run() {
                RoomSuperAwardAnimaorView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=getContext()){
                            GoldWireLayout goldWireLayout = new GoldWireLayout(getContext());
                            int xRandomMin = mStartScrrenPosition[0] - 100;
                            int xRandomMax = mStartScrrenPosition[0] + 60;
//                            Logger.d(TAG,"随机X1:"+xRandomMin+",X2:"+xRandomMax);
                            int startX = Utils.getRandomNum(xRandomMin,xRandomMax);
                            int startY=mStartScrrenPosition[1]-80;
                            //起始位置X:在制定的位置左右 -80到+80之间 Y:在设定位置偏上20像素
                            goldWireLayout.setStartPosition(new Point(startX,startY));
//                            Logger.d(TAG,"真实起始位置X::"+startX+",Y:"+startY);
                            ViewGroup rootView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
                            rootView.addView(goldWireLayout);
                            //最终到达位置,落在倒计时控件上的大概位置
                            goldWireLayout.setEndPosition(new Point(mEndScrrenPosition[0], mEndScrrenPosition[1]));
//                            Logger.d(TAG,"真实结束位置X::"+mEndScrrenPosition[0]+",Y:"+mEndScrrenPosition[1]);
                            goldWireLayout.startBeizerAnimation();
                            count++;
                            if(count>=MAX_PLAY_COUNT){
                                stopTask();
                            }
                        }
                    }
                });
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 30);
    }

    private void stopTask() {
        if(null!=mTimer) mTimer.cancel();
        mTimer=null;mTask=null;
        isRuning=false;count=0;
    }

    /**
     * 对应生命周期调用
     */
    public void onResume(){
        if(null!=mFlakeView) mFlakeView.resume();
        if(null!=mPlayer) mPlayer.start();
    }
    
    /**
     * 对应生命周期调用
     */
    public void onPause(){
        stopTask();
        if(null!=mFlakeView) mFlakeView.pause();
        if(null!=mPlayer&&mPlayer.isPlaying()) mPlayer.pause();
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        if(null!=mFlakeView) mFlakeView.onDestroy();
        if(null!=mAnimatorBgImage){
            mAnimatorBgImage.clearAnimation();
            mAnimatorBgImage.setImageResource(0);
        }
        stopTask();
        if(null!=mPlayer){
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
        }
        mStartScrrenPosition=null;mEndScrrenPosition=null;
        mAnimatorBgImage=null;mFlakeView=null; mPlayer=null;isRuning=false;
    }
}
