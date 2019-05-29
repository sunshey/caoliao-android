package com.video.player.lib.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.video.player.lib.R;
import com.video.player.lib.constants.VideoConstants;
import com.video.player.lib.base.BaseVideoController;
import com.video.player.lib.manager.VideoPlayerManager;
import com.video.player.lib.model.VideoPlayerState;
import com.video.player.lib.utils.Logger;
import com.video.player.lib.utils.VideoUtils;

/**
 * TinyHung@Outlook.com
 * 2019/4/8
 * Common Controller
 */

public class DefaultVideoController extends BaseVideoController implements SeekBar.OnSeekBarChangeListener {

    private View mTopBarLayout,mBottomBarLayout,mErrorLayout,mMobileLayout,mVideoStart,mBtnBackTiny;
    private TextView mVideoTitle,mVideoCurrent,mVideoTotal;
    private ProgressBar mBottomProgressBar,mProgressBar;
    private SeekBar mSeekBar;
    private ImageView mBtnStart;
    //用户是否手指正在持续滚动
    private boolean isTouchSeekBar=false;
    //实时播放位置
    private long mOldPlayProgress;

    public DefaultVideoController(@NonNull Context context) {
        this(context,null);
    }

    public DefaultVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DefaultVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.video_default_controller_layout,this);
        mTopBarLayout = findViewById(R.id.video_top_tab);
        mBottomBarLayout = findViewById(R.id.video_bottom_tab);
        mErrorLayout = findViewById(R.id.error_layout);
        mMobileLayout = findViewById(R.id.mobile_layout);

        View btnResetPlay = findViewById(R.id.video_btn_reset_play);
        mBtnBackTiny = findViewById(R.id.video_btn_back_tiny);
        View btnBack = findViewById(R.id.video_btn_back);
        View btnMenu = findViewById(R.id.video_btn_menu);
        mBtnStart = (ImageView) findViewById(R.id.video_btn_start);
        View btnFull = findViewById(R.id.video_full_screen);

        mVideoTitle = (TextView) findViewById(R.id.video_title);
        mVideoCurrent = (TextView) findViewById(R.id.video_current);
        mVideoTotal = (TextView) findViewById(R.id.video_total);
        mBottomProgressBar = findViewById(R.id.bottom_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.video_loading);
        mSeekBar = (SeekBar) findViewById(R.id.video_seek_progress);
        mVideoStart = findViewById(R.id.video_start);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.error_layout) {
                    VideoPlayerManager.getInstance().reStartVideoPlayer(mOldPlayProgress);
                }else if(id == R.id.video_btn_reset_play){
                    VideoPlayerManager.getInstance().setMobileWorkEnable(true);
                    VideoPlayerManager.getInstance().reStartVideoPlayer(0);
                }else if(id == R.id.video_btn_back_tiny){
                    if(null!=mOnFuctionListener){
                        mOnFuctionListener.onTinyBack();
                    }
                }else if(id == R.id.video_btn_back){
                    if(null!=mOnFuctionListener){
                        mOnFuctionListener.onBackPressed();
                    }
                }else if(id == R.id.video_btn_menu){

                }else if(id == R.id.video_btn_start){
                    VideoPlayerManager.getInstance().playOrPause();
                }else if(id == R.id.video_full_screen){
                    if(null!=mOnFuctionListener){
                        mOnFuctionListener.onStartFullPlay();
                    }
                }
            }
        };
        mErrorLayout.setOnClickListener(onClickListener);
        btnResetPlay.setOnClickListener(onClickListener);
        mBtnBackTiny.setOnClickListener(onClickListener);
        btnBack.setOnClickListener(onClickListener);
        btnMenu.setOnClickListener(onClickListener);
        mBtnStart.setOnClickListener(onClickListener);
        btnFull.setOnClickListener(onClickListener);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 负责控制器显示、隐藏
     */
    private  Runnable controllerRunnable=new Runnable() {
        @Override
        public void run() {
            if(null!=mBottomBarLayout){
                mBottomBarLayout.setVisibility(INVISIBLE);
            }
            if(null!=mTopBarLayout){
                mTopBarLayout.setVisibility(INVISIBLE);
            }
            if(null!=mBottomProgressBar){
                mBottomProgressBar.setVisibility(VISIBLE);
            }
        }
    };

    //=========================================SEEK BAR=============================================

    /**
     * 用户手指持续拨动中
     * @param seekBar
     * @param progress 实时进度
     * @param fromUser 是否来自用户手动拖动
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            long durtion = VideoPlayerManager.getInstance().getDurtion();
            if(durtion>0){
                mVideoCurrent.setText(VideoUtils.getInstance().stringForAudioTime(progress * durtion / 100));
            }
        }
    }

    /**
     * 手指按下
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar=true;
        removeCallbacks(View.VISIBLE);
    }

    /**
     * 手指松开
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar=false;
        //恢复控制器显示隐藏规则
        changeControllerState(mScrrenOrientation,false);
        //非暂停状态下置为加载中状态
        if(!VideoPlayerManager.getInstance().getVideoPlayerState().equals(VideoPlayerState.MUSIC_PLAYER_PAUSE)){
            startBuffer();
        }
        //跳转至某处
        long durtion = VideoPlayerManager.getInstance().getDurtion();
        if(durtion>0){
            long currentTime = seekBar.getProgress() * durtion / 100;
            VideoPlayerManager.getInstance().seekTo(currentTime);
        }
    }

    /**
     * 设置视频标题
     * @param videoTitle 视频标题内容
     */
    @Override
    protected void setTitle(String videoTitle) {
        if(null!=mVideoTitle){
            mVideoTitle.setText(videoTitle);
        }
    }

    //=========================================播放状态=============================================

    /**
     * 开始播放器准备中
     */
    @Override
    public void readyPlaying() {
        Logger.d(TAG,"readyPlaying");
        removeCallbacks(View.INVISIBLE);
        //小窗口
        if(mScrrenOrientation==VideoConstants.SCREEN_ORIENTATION_TINY){
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE);
        }else{
            updateVideoControllerUI(View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
        }
    }

    /**
     * 开始缓冲中
     */
    @Override
    public void startBuffer() {
        Logger.d(TAG,"startBuffer");
        removeCallbacks(View.INVISIBLE);
        //小窗口
        if(mScrrenOrientation==VideoConstants.SCREEN_ORIENTATION_TINY){
            updateVideoControllerUI(View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE);
        }else{
            updateVideoControllerUI(View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
        }
    }

    /**
     * 缓冲结束
     */
    @Override
    public void endBuffer() {
        Logger.d(TAG,"endBuffer");
        if(mScrrenOrientation==VideoConstants.SCREEN_ORIENTATION_TINY){
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE);
        }else{
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
        }
    }

    /**
     * 已暂停
     * tips:播放器组件处理了暂停时若处在小窗口播放，则停止播放,故此处无需处理小窗口事件
     */
    @Override
    public void pause() {
        Logger.d(TAG,"pause");
        if(null!=mBtnStart){
            mBtnStart.setImageResource(R.drawable.ic_video_controller_play);
        }
        removeCallbacks(View.VISIBLE);
        updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
    }

    /**
     * 恢复播放
     * tips:播放器组件处理了暂停时若处在小窗口播放，则停止播放,故此处无需处理小窗口事件
     */
    @Override
    public void repeatPlay() {
        Logger.d(TAG,"repeatPlay");
        if(null!=mBtnStart){
            mBtnStart.setImageResource(R.drawable.ic_video_controller_pause);
        }
        updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
        changeControllerState(mScrrenOrientation,false);
    }

    /**
     * 播放失败
     * tips:播放器组件处理了播放失败时若处在小窗口播放，则停止播放,故此处无需处理小窗口事件
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public void error(int errorCode, String errorMessage) {
        Logger.d(TAG,"error,errorMessage:"+errorMessage);
        if(null!=mBtnStart){
            mBtnStart.setImageResource(R.drawable.ic_video_controller_play);
        }
        removeCallbacks(View.INVISIBLE);
        updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE);
    }

    /**
     * 已开始
     */
    @Override
    public void play() {
        Logger.d(TAG,"play");
        if(null!=mBtnStart){
            mBtnStart.setImageResource(R.drawable.ic_video_controller_pause);
        }
        if(mScrrenOrientation==VideoConstants.SCREEN_ORIENTATION_TINY){
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE);
        }else{
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
        }
    }

    /**
     * 横屏模式开启,默认展开控制器
     */
    @Override
    public void startHorizontal() {
        Logger.d(TAG,"startHorizontal");
        changeControllerState(VideoConstants.SCREEN_ORIENTATION_FULL,false);
    }

    /**
     * 开启小窗口播放
     */
    @Override
    public void startTiny() {
        Logger.d(TAG,"startTiny");
        removeCallbacks(View.INVISIBLE);
        updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE);
    }

    /**
     * 移动网络环境下工作
     */
    @Override
    public void mobileWorkTips() {
        Logger.d(TAG,"mobileWorkTips");
        removeCallbacks(View.INVISIBLE);
        if(mScrrenOrientation==VideoConstants.SCREEN_ORIENTATION_TINY){
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE,View.VISIBLE);
        }else{
            updateVideoControllerUI(View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.VISIBLE,View.INVISIBLE);
        }
    }

    /**
     * 还原所有状态
     */
    @Override
    public void reset() {
        Logger.d(TAG,"reset");
        removeCallbacks(View.INVISIBLE);
        updateVideoControllerUI(View.VISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE,View.INVISIBLE);
        if(null!=mVideoTotal){
            mVideoTotal.setText("00:00");
            mVideoCurrent.setText("00:00");
        }
        if(null!=mSeekBar){
            mSeekBar.setSecondaryProgress(0);
            mSeekBar.setProgress(0);
        }
        if(null!=mBottomProgressBar){
            mBottomProgressBar.setSecondaryProgress(0);
            mBottomProgressBar.setProgress(0);
        }
    }


    /**
     * 播放进度
     * @param totalDurtion 总时长
     * @param currentDurtion 已播放时长
     */
    @Override
    public void onTaskRuntime(long totalDurtion, long currentDurtion) {
        Logger.d("播放实时进度","onTaskRuntime-->totalDurtion:"+totalDurtion+",currentDurtion:"+currentDurtion);
        if(totalDurtion>-1){
            mOldPlayProgress=currentDurtion;
            if(null!=mVideoTotal){
                mVideoTotal.setText(VideoUtils.getInstance().stringForAudioTime(totalDurtion));
                mVideoCurrent.setText(VideoUtils.getInstance().stringForAudioTime(currentDurtion));
            }
            //得到当前进度
            int progress = (int) (((float) currentDurtion / totalDurtion) * 100);
            if(null!=mSeekBar&&!isTouchSeekBar){
                mSeekBar.setProgress(progress);
            }
            if(null!=mBottomProgressBar){
                mBottomProgressBar.setProgress(progress);
            }
        }
    }

    /**
     * 缓冲进度
     * @param percent
     */
    @Override
    public void onBufferingUpdate(int percent) {
        Logger.d("onBufferingUpdate","percent-->"+percent);
        if(null!=mSeekBar&&mSeekBar.getSecondaryProgress()<100){
            mSeekBar.setSecondaryProgress(percent);
        }
        if(null!=mBottomProgressBar&&mBottomProgressBar.getSecondaryProgress()<100){
            mBottomProgressBar.setSecondaryProgress(percent);
        }
    }

    /**
     * 更新控制器
     * @param start 开始
     * @param loading 加载中
     * @param bottomProgress 底部进度条
     * @param errorLayout 失败图层
     * @param mobileLayout 移动网络提示图层
     * @param tinyLayout 小窗口删除按钮
     */
    private void updateVideoControllerUI(int start,int loading,int bottomProgress,int errorLayout,int mobileLayout,int tinyLayout) {
        if(null!=mVideoStart){
            mVideoStart.setVisibility(start);
        }
        if(null!=mProgressBar){
            mProgressBar.setVisibility(loading);
        }
        if(null!=mBtnBackTiny){
            mBtnBackTiny.setVisibility(tinyLayout);
        }
        //如果是显示底部进度条，当底部控制器显示时不应该显示底部进度条
        if(null!=mBottomProgressBar){
            if(bottomProgress==View.VISIBLE){
                //仅当底部控制器处于非显示状态下，才显示底部进度条
                if(null!=mBottomBarLayout&&mBottomBarLayout.getVisibility()!=VISIBLE){
                    mBottomProgressBar.setVisibility(bottomProgress);
                }
            }else{
                mBottomProgressBar.setVisibility(bottomProgress);
            }
        }
        if(null!=mErrorLayout){
            mErrorLayout.setVisibility(errorLayout);
        }

        if(null!=mMobileLayout){
            mMobileLayout.setVisibility(mobileLayout);
        }
    }

    /**
     * 移除定时器，保留在最后的状态
     * @param visible 最后的状态
     */
    private void removeCallbacks(int visible) {
        DefaultVideoController.this.removeCallbacks(controllerRunnable);
        if(null!=mBottomBarLayout){
            mBottomBarLayout.setVisibility(visible);
        }
        //横屏下才显示、隐藏顶部菜单栏
        if(mScrrenOrientation== VideoConstants.SCREEN_ORIENTATION_FULL){
            if(null!=mTopBarLayout){
                mTopBarLayout.setVisibility(visible);
            }
        }
        if(View.INVISIBLE==visible){
            mBottomProgressBar.setVisibility(VISIBLE);
        }
    }

    /**
     * 显示、隐藏 控制器 上下交互功能区
     * 手动点击，根据播放状态自动处理，手势交互处理等状态
     * @param scrrenOrientation 横竖屏状态
     * @param isInterceptIntent 是否拦截意图
     */
    @Override
    public void changeControllerState(int scrrenOrientation,boolean isInterceptIntent) {
        if(null==mBottomBarLayout) return;
        Logger.d(TAG,"changeControllerState-->"+scrrenOrientation+",isInterceptIntent:"+isInterceptIntent);
        //小窗口样式不处理任何事件
        if(scrrenOrientation==VideoConstants.SCREEN_ORIENTATION_TINY){
            return;
        }
        //重复显示
        if(isInterceptIntent&&mBottomBarLayout.getVisibility()==View.VISIBLE){
            removeCallbacks(View.INVISIBLE);
            return;
        }
        //移除已post的事件
        DefaultVideoController.this.removeCallbacks(controllerRunnable);
        if(mBottomBarLayout.getVisibility()!=View.VISIBLE){
            mBottomBarLayout.setVisibility(View.VISIBLE);
        }
        //横屏下才显示、隐藏顶部菜单栏
        if(scrrenOrientation==VideoConstants.SCREEN_ORIENTATION_FULL){
            if(mTopBarLayout.getVisibility()!=VISIBLE){
                mTopBarLayout.setVisibility(View.VISIBLE);
            }
        }
        if(null!=mBottomProgressBar){
            mBottomProgressBar.setVisibility(View.INVISIBLE);
        }
        DefaultVideoController.this.postDelayed(controllerRunnable,5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTopBarLayout=null; mBottomBarLayout=null; mErrorLayout=null; mMobileLayout=null; mVideoStart=null;
        mVideoTitle=null; mVideoCurrent=null; mVideoTotal=null; mBottomProgressBar=null; mProgressBar=null;
        mSeekBar=null; mBtnStart=null; isTouchSeekBar=false; mOldPlayProgress=0;
    }
}