package com.yc.liaolive.interfaces;

/**
 * TinyHung@Outlook.com
 * 2018/12/7
 * 流、视频播放监听
 */

public abstract class OnMediaPlayerListener {
    /**
     * 播放开始
     */
    public void onStart(){}

    /**
     * 播放器准备好了
     * @param totalDurtion 总时长
     */
    public void onPrepared(long totalDurtion){}

    /**
     * 缓冲进度
     * @param progress 已经缓冲的数据量占整个视频时长的百分比
     */
    public void onBufferingUpdate(int progress){}

    /**
     * 播放进度
     * @param currentDurtion 实时播放位置
     * @param totalDurtion 总长度
     */
    public void onPlayingProgress(long currentDurtion,long totalDurtion){}

    /**
     * 播放中各种状态
     */
    public void onStatus(int event){}

    /**
     * 播放结束
     */
    public void onCompletion(){}

    /**
     * 失败
     * @param errorCode
     */
    public void onError(int errorCode){}

}
