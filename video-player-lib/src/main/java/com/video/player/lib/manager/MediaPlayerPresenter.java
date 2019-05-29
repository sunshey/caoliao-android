package com.video.player.lib.manager;

import android.content.Context;
import com.video.player.lib.listener.VideoPlayerEventListener;
import com.video.player.lib.model.VideoPlayerState;

/**
 * TinyHung@Outlook.com
 * 2019/4/9
 * VideoPlayer Persenter
 */

public interface MediaPlayerPresenter {

    /**
     * 设置循环模式
     * @param loop true:循环播放 false:反之
     */
    void setLoop(boolean loop);

    /**
     * 移动网络工作开关
     * @param enable true：允许移动网络工作 false：不允许
     */
    void setMobileWorkEnable(boolean enable);

    /**
     * 添加监听器
     * @param listener
     */
    void addOnPlayerEventListener(VideoPlayerEventListener listener);

    /**
     * 移除监听器
     */
    void removePlayerListener();

    /**
     * 开始准备并播放
     * @param dataSource 播放资源地址，支持file、https、http 等协议
     * @param context
     */
    void startVideoPlayer(String dataSource,Context context);

    /**
     * 开始播放
     * @param dataSource 播放资源地址，支持file、https、http 等协议
     * @param context
     * @param percentIndex 尝试从指定位置开始播放
     */
    void startVideoPlayer(String dataSource,Context context,int percentIndex);

    /**
     * 尝试重新播放
     * @param percentIndex 尝试从指定位置重新开始
     */
    void reStartVideoPlayer(long percentIndex);

    /**
     * 返回播放器内部播放状态
     * @return true：正在播放，fasle：未播放
     */
    boolean isPlaying();

    /**
     * 返回播放器内部工作状态
     * @return true：正在工作，包含暂停、缓冲等， false：未工作
     */
    boolean isWorking();

    /**
     * 开始、暂停播放
     */
    void playOrPause();

    /**
     * 恢复播放
     */
    void play();
    /**
     * 暂停播放
     */
    void pause();

    /**
     * 释放、还原播放、监听、渲染等状态
     */
    void onReset();

    /**
     * 停止播放
     */
    void onStop();

    /**
     * 跳转至指定位置播放
     * @param currentTime 事件位置，单位毫秒
     */
    void seekTo(long currentTime) ;

    /**
     * 返回当前播放对象的总时长
     * @return
     */
    long getDurtion();

    /**
     * 是否可以直接返回
     * @return true：可以直接返回 false：存在全屏或小窗口
     */
    boolean isBackPressed();

    /**
     * 返回内部播放器播放状态
     * @return
     */
    VideoPlayerState getVideoPlayerState();

    /**
     * 检查播放器内部状态
     */
    void checkedVidepPlayerState();

    /**
     * 组件对应生命周期调用
     */
    void onResume();

    /**
     * 组件对应生命周期调用
     */
    void onPause();

    /**
     * 组件对应生命周期调用
     */
    void onDestroy();
}