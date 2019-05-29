package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.yc.liaolive.R;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;

/**
 * TinyHung@Outlook.com
 * 2018/12/28
 * 播放器界面需要的状态变化交互
 */

public class VideoPlayerStatusController extends FrameLayout{

    private LoadingIndicatorView mIndicatorView;
    private ImageView mBtnPlay;
    private ProgressBar mProgressBar;

    public VideoPlayerStatusController(@NonNull Context context) {
        this(context,null);
    }

    public VideoPlayerStatusController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_video_player_status_controller_layout,this);
        mIndicatorView = (LoadingIndicatorView) findViewById(R.id.view_loading_view);
        mBtnPlay = (ImageView) findViewById(R.id.view_btn_play);
        mProgressBar = (ProgressBar) findViewById(R.id.bottom_progress);
    }

    public LoadingIndicatorView getIndicatorView() {
        return mIndicatorView;
    }

    public ImageView getBtnPlay() {
        return mBtnPlay;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public synchronized void startLoadingView(){
        if(null!=mIndicatorView){
            mIndicatorView.smoothToShow();
        }
    }

    public synchronized void stopLoadingView(){
        if(null!=mIndicatorView){
            mIndicatorView.setVisibility(GONE);
        }
    }

    /**
     * 开始准备中
     */
    public synchronized void onStartPrepared() {
        if(mBtnPlay.getVisibility()!=GONE) mBtnPlay.setVisibility(GONE);
        startLoadingView();
    }

    /**
     * 重置为待播放状态
     */
    public synchronized void reset() {
        stopLoadingView();
        if(null!=mBtnPlay&&mBtnPlay.getVisibility()!=VISIBLE) mBtnPlay.setVisibility(VISIBLE);
    }

    public void hidePlayBtn(){
        if(null!=mBtnPlay&&mBtnPlay.getVisibility()!=GONE) mBtnPlay.setVisibility(GONE);
    }

    public synchronized void onDestroy(){
        if(null!=mIndicatorView) mIndicatorView.hide();
        if(null!=mBtnPlay) mBtnPlay.setVisibility(GONE);
    }
}
