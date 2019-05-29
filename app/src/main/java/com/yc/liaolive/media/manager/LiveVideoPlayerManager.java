package com.yc.liaolive.media.manager;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.danikula.videocache.HttpProxyCacheServer;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.interfaces.OnMediaPlayerListener;
import com.yc.liaolive.media.view.VideoPlayerStatusController;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * TinyHung@Outlook.com
 * 2018/12/7
 * 直播、小视频 播放器管理
 */

public class LiveVideoPlayerManager extends FrameLayout implements IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener{

    private static final String TAG = "LiveVideoPlayerManager";
    //视频画面缩放模式
    public static final int VIDEO_SCALING_MODE_SCALE_TO_CROPPING  = 0; //拉伸至全屏，画面会存在变形
    public static final int VIDEO_SCALING_MODE_NOSCALE_TO_FIT     = 1; //保持原比例不缩放
    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT       = 2; //裁剪缩放至铺满全屏，不变行

    private OnMediaPlayerListener mListener;
    private ImageView mCover;
    private VideoPlayerStatusController mSatusController;
    private boolean isPlaying=false;//是否正在播放中
    private boolean isStartPlaying=false;//是否已经播放过
    private String mCurrentPlayUrl;//正在播放的地址
    private KSYTextureView mTextureView;
    private boolean mLooping=true;
    private int mVideoScalingMode;
    private boolean mIsAgent;
    private boolean mAutoRotateEnabled=false;//是否禁用自动旋转

    private PlayTimerTask mPlayTimerTask;
    private Timer mTimer;

    public LiveVideoPlayerManager(@NonNull Context context) {
        this(context,null);
    }

    public LiveVideoPlayerManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_media_player_layout,this);
        mCover = (ImageView) findViewById(R.id.view_background);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LiveVideoPlayerManager);
            boolean aBoolean = typedArray.getBoolean(R.styleable.LiveVideoPlayerManager_videoPlayerDefaultBackground, true);
            if(aBoolean){
                mCover.setBackgroundResource(R.drawable.music_default_music_bg);
            }
            typedArray.recycle();
        }
        mTextureView = (KSYTextureView) findViewById(R.id.view_textureview);
        //设置监听器
        mTextureView.setOnBufferingUpdateListener(this);
        mTextureView.setOnCompletionListener(this);
        mTextureView.setOnPreparedListener(this);
        mTextureView.setOnInfoListener(this);
        mTextureView.setOnVideoSizeChangedListener(this);
        mTextureView.setOnErrorListener(this);
        mTextureView.setOnSeekCompleteListener(this);
        //设置播放参数
        mTextureView.setBufferTimeMax(5.0f);
        mTextureView.setTimeout(5, 30);
        setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT);
    }

    /**
     * 绑定播放状态控制器
     * @param statusController
     */
    public void setStatusController(VideoPlayerStatusController statusController) {
        if(null!=mSatusController) {
            this.removeView(mSatusController);
            mSatusController=null;
        }
        if(null==statusController) return;
        this.mSatusController=statusController;
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSatusController.setLayoutParams(layoutParams);
        this.addView(mSatusController);
    }

    /**
     * 设置画面缩放模式
     */
    public void setVideoScalingMode(int videoScalingMode){
        int mode=KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
        switch (videoScalingMode) {
            case VIDEO_SCALING_MODE_SCALE_TO_CROPPING:
                mode=KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
                break;
            case VIDEO_SCALING_MODE_NOSCALE_TO_FIT:
                mode=KSYMediaPlayer.VIDEO_SCALING_MODE_NOSCALE_TO_FIT;
                break;
            case VIDEO_SCALING_MODE_SCALE_TO_FIT:
                mode=KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT;
                break;
        }
        this.mVideoScalingMode=videoScalingMode;
        if(null!=mTextureView)mTextureView.setVideoScalingMode(mode);
    }

    /**
     * 设置音量大小
     * @param leftVolume 音量大小 0-1.0
     * @param rightVolume 音量大小 0-1.0
     */
    public void setVolume(float leftVolume,float rightVolume){
        if(null!=mTextureView) mTextureView.setVolume(leftVolume,rightVolume);
    }

    /**
     * 是否静音
     * @param muteMode true:静音
     */
    public void setMuteMode(boolean muteMode){
        if(muteMode){
            setVolume(0,0);
        }
    }

    /**
     * 主播设置，追赶阈值 秒
     * @param timeMax
     */
    public void setBufferTimeMax(float timeMax){
        if(null!=mTextureView) mTextureView.setBufferTimeMax(timeMax);
    }

    /**
     * 播放事件监听
     * @param listener
     */
    public void setMediaPlayerListener(OnMediaPlayerListener listener){
        this.mListener=listener;
    }

    /**
     * 设置播放器不可见时是否继续播放
     * @param isBackFromShare
     */
    public void setComeBackFromShare(boolean isBackFromShare){
        if(null!=mTextureView) mTextureView.setComeBackFromShare(isBackFromShare);
    }

    /**
     * 视频旋转角度
     * @param rotateDegree 只能设置90/180/270中的某个值
     */
    public void setRotateDegree(int rotateDegree){
        if(null!=mTextureView) mTextureView.setRotateDegree(rotateDegree);
    }

    /**
     * 是否禁用自动旋转,将根据实际视频分辨率旋转视频方向以适应屏幕分辨率
     * @param enabled true:禁用 false:不禁用
     */
    public void setAutoRotateEnabled(boolean enabled) {
        this.mAutoRotateEnabled=enabled;
    }

    /**
     * 加载中状态
     */
    public synchronized void startLoadingView(){
        if(null!=mSatusController) mSatusController.startLoadingView();
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
        return isPlaying;
    }

    /**
     * 是否曾经播放过
     * @return
     */
    public boolean startPlaying() {
        return isStartPlaying;
    }

    /**
     * 返回当前正在播放的对象
     * @return
     */
    public String getCurrentPlayUrl() {
        return mCurrentPlayUrl;
    }

    /**
     * 结束加载中状态
     */
    public synchronized void stopLoadingView(){
        if(null!=mSatusController) mSatusController.stopLoadingView();
    }

    /**
     * 是否重复播放
     * @param looping
     */
    public void setLooping(boolean looping){
        this.mLooping=looping;
        if(null!=mTextureView)mTextureView.setLooping(mLooping);
    }

    public VideoPlayerStatusController getSatusController() {
        return mSatusController;
    }

    /**
     * 视频封面
     * @param frontCover 视频封面地址
     * @param transformat 是否毛玻璃显示
     */
    public void setVideoCover(String frontCover,boolean transformat) {
        if(TextUtils.isEmpty(frontCover)){
            if(null!=mCover) mCover.setImageResource(0);
            return;
        }
        if(null!=mCover){
            if(transformat){
                Glide.with(getContext())
                        .load(frontCover)
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.ic_default_item_cover)
                        .error(R.drawable.ic_default_item_cover)
                        .dontAnimate()
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .bitmapTransform(new BlurTransformation(getContext(), 15))
                        .into(mCover);
            }else{
                Glide.with(getContext())
                        .load(frontCover)
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.ic_default_item_cover)
                        .error(R.drawable.ic_default_item_cover)
                        .dontAnimate()
                        .animate(R.anim.item_alpha_in)//加载中动画
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .into(mCover);
            }
        }
    }

    /**
     * 对应生命周期调用
     */
    public void onResume(){
        if(null!=mTextureView&&!isPlaying){
            isPlaying=true;
            mTextureView.start();
        }
    }

    /**
     * 对应生命周期调用
     */
    public void onPause(){
        if(null!=mTextureView&&isPlaying) {
            isPlaying=false;
            mTextureView.pause();
        }
    }

    /**
     * 界面可见还原视频播放，适合直播间调用
     */
    public void onInForeground(){
        if(null!=mTextureView) mTextureView.runInForeground();
    }

    /**
     * 界面不可见是否继续播放音频,适合直播间调用
     * @param isPlayAudio 音频是否继续播放，若为false:停止所有播放
     */
    public void onInBackground(boolean isPlayAudio){
        if(null!=mTextureView) mTextureView.runInBackground(isPlayAudio);
    }

    /**
     * 创建播放实例并开始播放
     */
    public void onStart(){
        if(null!=mTextureView&&TextUtils.isEmpty(mCurrentPlayUrl)){
            mTextureView.setBufferTimeMax(5.0f);
            mTextureView.setTimeout(5, 30);
        }
        startPlay(mCurrentPlayUrl,mIsAgent);
    }

    /**
     * 停止播放并销毁播放实例
     */
    public void onStop(){
        onStop(false);
    }

    /**
     * 对应生命周期调用
     * @param cleanUrl 是否清除当前播放记录
     */
    public void onStop(boolean cleanUrl){
        stopTimer();
        stopLoadingView();
        if(null!=mTextureView){
            mTextureView.stop();
            mTextureView.reset();
        }
        if(cleanUrl) mCurrentPlayUrl=null;
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        mListener=null;mCurrentPlayUrl=null;
        stopLoadingView();
        if(null!=mTextureView){
            mTextureView.stop();
            mTextureView.reset();
            mTextureView.release();
            mTextureView=null;
        }
        stopTimer();
        setVideoCover(null,false);
    }

    //=========================================控制器的交互==========================================
    /**
     * 暂停、停止播放
     */
    public synchronized void pauseAndStartPlay() {
        if(isPlaying){
            onPause();
            if(null!=mSatusController) AnimationUtil.playPlayPauseAnimation(mSatusController.getBtnPlay());
        }else{
            onResume();
            if(null!=mSatusController) AnimationUtil.playPlayPlayAnimation(mSatusController.getBtnPlay());
        }
    }

    /**
     * 开始准备中
     */
    public void onStartPrepared() {
        if(null!=mSatusController) mSatusController.onStartPrepared();
    }

    /**
     * 控制器重置
     */
    public synchronized void reset() {
        if(null!=mSatusController) mSatusController.reset();
    }

    /**
     * 封面透明度
     * @param alpha
     */
    public void setCoverAlpha(float alpha) {
        if(null!=mCover) mCover.setAlpha(alpha);
    }

    /**
     * 设置播放地址
     * @param mediaPath
     */
    public synchronized void startPlay(String mediaPath){
        startPlay(mediaPath,false);
    }

    /**
     * 设置播放地址
     * @param mediaPath
     * @param isAgent 是否启用缓存代理
     */
    public synchronized void startPlay(String mediaPath,boolean isAgent){
        if(null!=mTextureView&&!TextUtils.isEmpty(mediaPath)){
            startLoadingView();
            mTextureView.setBufferTimeMax(5.0f);
            mTextureView.setTimeout(5, 30);
            mCurrentPlayUrl = mediaPath;
            this.mIsAgent=isAgent;
            String playUrl=mediaPath;
            if(isAgent){
                HttpProxyCacheServer cacheServer = AppEngine.getInstance().getProxyCacheServer();
                playUrl = cacheServer.getProxyUrl(mediaPath);
            }
            Logger.d(TAG,"startPlay--playUrl:"+playUrl+",isAgent:"+isAgent);
            try {
                mTextureView.setLooping(mLooping);
                mTextureView.setDataSource(playUrl);
                mTextureView.prepareAsync();
                isStartPlaying=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取视频总时长
     * @return
     */
    public long getDurtion() {
        try {
            if(null!=mTextureView&&mTextureView.isPlaying()){
                return mTextureView.getDuration();
            }
        }catch (RuntimeException e){

        }
        return 0;
    }

    /**
     * 跳转至某处播放
     * @param currentTime
     */
    public void seekTo(long currentTime) {
        try {
            if(null!=mTextureView){
                mTextureView.seekTo(currentTime);
            }
        }catch (RuntimeException e){

        }
    }

    /**
     * 开始计时任务
     */
    private void startTimer() {
        stopTimer();
        mPlayTimerTask = new PlayTimerTask();
        mTimer = new Timer();
        //立即执行，1000毫秒循环一次
        mTimer.schedule(mPlayTimerTask, 0, 1000);
    }

    /**
     * 结束计时任务
     */
    private void stopTimer() {
        if (null != mPlayTimerTask) {
            mPlayTimerTask.cancel();
            mPlayTimerTask = null;
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 播放进度、闹钟倒计时进度 计时器
     */
    private class PlayTimerTask extends TimerTask {
        @Override
        public void run() {
            if(null!=mListener&&null!=mTextureView){
                LiveVideoPlayerManager.this.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onPlayingProgress(mTextureView.getCurrentPosition(),mTextureView.getDuration());
                    }
                });
            }
        }
    }

    //==========================================播放事件监听==========================================

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        isPlaying=false;
        isStartPlaying=false;
        stopTimer();
        LiveVideoPlayerManager.this.post(new Runnable() {
            @Override
            public void run() {
                if(null!=mTextureView) mTextureView.reset();
                if(null!=mSatusController) mSatusController.reset();
                if(null!=mListener) mListener.onCompletion();
            }
        });
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        if(null!=mTextureView){
            mTextureView.setVideoScalingMode(mVideoScalingMode);
            mTextureView.start();
        }
        isPlaying=true;
        LiveVideoPlayerManager.this.post(new Runnable() {
            @Override
            public void run() {
                if(null!=mSatusController) mSatusController.hidePlayBtn();
                if(null!= mTextureView) mTextureView.setAlpha(1.0f);
                stopLoadingView();
                if(null!=mListener){
                    mListener.onPrepared(iMediaPlayer.getDuration());
                }
            }
        });
        startTimer();
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int event, int i1) {
        switch (event) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if(null!=mSatusController) mSatusController.startLoadingView();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                if(null!=mSatusController) mSatusController.stopLoadingView();
                break;
            case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if(null!=mListener) mListener.onStart();
                break;
        }
        if(null!=mListener) mListener.onStatus(event);
        return false;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int code, int msg) {
        stopTimer();
        isStartPlaying=false;
        //错误发生时触发，错误码见接口文档
        LiveVideoPlayerManager.this.post(new Runnable() {
            @Override
            public void run() {
                if(null!=mSatusController) mSatusController.reset();
                if(null!=mTextureView){
                    mTextureView.stop();
                    mTextureView.reset();
                }
                if(null!=mListener) mListener.onError(code);
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {

    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        if(null!=mListener) mListener.onBufferingUpdate(i);
    }
}