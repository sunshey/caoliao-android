package com.yc.liaolive.msg.manager;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.msg.model.bean.ResetVoiceMessage;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.Logger;
import java.io.IOException;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/12/10
 * 音频播放管理
 */

public class VoiceModelManager implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "VoideModelManager";
    private Long currenPlayVoiceID;//正在播放的音频文件ID
    private static VoiceModelManager mInstance;
    private AnimationDrawable mAnimationDrawable;//界面动画
    private MediaPlayer mMediaPlayer;

    public static synchronized VoiceModelManager getInstance(){
        synchronized (VoiceModelManager.class){
            if(null==mInstance){
                mInstance=new VoiceModelManager();
            }
        }
        return mInstance;
    }

    /**
     * 返回音频消息的播放状态
     */
    public boolean isRead(Long voiceID){
        List<ResetVoiceMessage> allVoiceLists = ApplicationManager.getInstance().getVoiceDBManager().getAllVoiceLists();
        if(null==allVoiceLists) return false;
        for (int i = 0; i < allVoiceLists.size(); i++) {
            ResetVoiceMessage resetVoiceMessage = allVoiceLists.get(i);
            if(resetVoiceMessage.getId().equals(voiceID)){
                return true;
            }
        }
        return false;
    }

    /**
     * 返回某个音频文件的播放状态
     * @param voiceID
     * @return
     */
    public boolean isPlaying(Long voiceID){
        if(null==voiceID||null==currenPlayVoiceID) return false;
        return voiceID.equals(currenPlayVoiceID);
    }

    /**
     * 播放器初始化
     * @param voiceMessage
     * @param voiceMessage
     */
    private void initPlayer(ResetVoiceMessage voiceMessage) {
        if(null == voiceMessage || TextUtils.isEmpty(voiceMessage.getPath())) return;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnErrorListener(this);
        try {
            HttpProxyCacheServer cacheServer = AppEngine.getInstance().getProxyCacheServer();
            String proxyUrl = cacheServer.getProxyUrl(voiceMessage.getPath());
            mMediaPlayer.setDataSource(proxyUrl);
            mMediaPlayer.prepareAsync();
            currenPlayVoiceID=voiceMessage.getId();
        } catch (IOException e) {
            e.printStackTrace();
            onReset();
        }
    }

    /**
     * 时间格式化
     * @param durtion
     * @return
     */
    public String formatDurtion(long durtion) {
        if(durtion<=60) return durtion+"''";
        return DateUtil.timeFormatSecond(durtion*1000)+"''";
    }

    /**
     * 刷新动画对象
     * @param animationDrawable
     */
    public void updataAnimation(AnimationDrawable animationDrawable) {
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()){
            mAnimationDrawable.selectDrawable(0);
            mAnimationDrawable.stop();
            mAnimationDrawable=null;
        }
        this.mAnimationDrawable=animationDrawable;
        if(null!=mAnimationDrawable) mAnimationDrawable.start();
    }

    /**
     * 开始播放音频文件
     * @param voiceMessage
     * @param animationDrawable
     */
    public void startPlay(ResetVoiceMessage voiceMessage,AnimationDrawable animationDrawable) {
        if(null!=currenPlayVoiceID&&currenPlayVoiceID.equals(voiceMessage.getId())){
            onReset();
            return;
        }
        onReset();
        if(null==voiceMessage) return;
        this.mAnimationDrawable=animationDrawable;
        if(null!=mAnimationDrawable) mAnimationDrawable.start();
        voiceMessage.setIsRead(1);//标记已读状态
        ApplicationManager.getInstance().getVoiceDBManager().insertVoiceFromObject(voiceMessage);
        initPlayer(voiceMessage);
    }


    public void onPause(){
        onReset();
    }

    /**
     * 播放器重置
     */
    public void onReset(){
        if(null!=mAnimationDrawable) {
            mAnimationDrawable.selectDrawable(0);
            mAnimationDrawable.stop();
            mAnimationDrawable=null;
        }
        if(null!=mMediaPlayer){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        currenPlayVoiceID=null;
    }

    public void onDestroy(){
        onReset();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onReset();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(null!=mMediaPlayer) mMediaPlayer.start();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        onReset();
        return false;
    }
}
