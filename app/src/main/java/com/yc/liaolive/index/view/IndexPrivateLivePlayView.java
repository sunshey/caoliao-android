package com.yc.liaolive.index.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.danikula.videocache.HttpProxyCacheServer;
import com.ksyun.media.player.IMediaPlayer;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.interfaces.OnMediaPlayerListener;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.view.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/8/18
 * 首页 视频通话推荐
 */

public class IndexPrivateLivePlayView extends RelativeLayout {

    private static final String TAG = "IndexPrivateLivePlayView";
    public static final int MODE_SINGER_PLAY = 0;//单场次播放
    public static final int MODE_LIST_PLAY = 1;//列表播放
    private View mBtnRefresh;
    private RoundImageView mVideoCover;
    List<RoomList> mRoomLists=null;
    private int playCurrentIndex=0;
    private int mScenMode;//应用场景
    private boolean isDestroy;//是否已经销毁了
    private Handler mHandler;
    private LiveVideoPlayerManager mPlayerManager;

    public IndexPrivateLivePlayView(Context context) {
        super(context);
        init(context,null);
    }

    public IndexPrivateLivePlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_index_private_play_layout,this);
        mBtnRefresh = findViewById(R.id.view_btn_refresh);
        mVideoCover = (RoundImageView)findViewById(R.id.view_video_cover);
        mVideoCover.setImageResource(R.drawable.ic_default_item_cover);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndexLivePlayView);
            Drawable closeDrawable = typedArray.getDrawable(R.styleable.IndexLivePlayView_indexLiveCloseSrc);
            if(null!=closeDrawable) mBtnRefresh.setBackground(closeDrawable);
            typedArray.recycle();
        }
        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        //切换视频源
                        stopPlay();
                        startPlay(true);
                    }
                });
            }
        });
        //初始化拉流相关
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setLooping(true);
        mPlayerManager.setMediaPlayerListener(new OnMediaPlayerListener() {
            @Override
            public void onStart() {
                if(null!=mBtnRefresh){
                    mBtnRefresh.setTag(Constant.PLAY_STATE_PLAYING);
                    mBtnRefresh.setVisibility(View.GONE);
                }
            }

            @Override
            public void onBufferingUpdate(int progress) {

            }

            @Override
            public void onStatus(int event) {
                switch (event) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if(null!=mBtnRefresh)mBtnRefresh.setTag(Constant.PLAY_STATE_LOADING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if(null!=mBtnRefresh)mBtnRefresh.setTag(Constant.PLAY_STATE_PLAYING);
                        break;
                }
            }

            @Override
            public void onCompletion() {
                if(null!=mBtnRefresh)mBtnRefresh.setTag(Constant.PLAY_STATE_NOIMAL);

            }

            @Override
            public void onError(int errorCode) {
                if(null!=mBtnRefresh){
                    mBtnRefresh.setTag(Constant.PLAY_STATE_NOIMAL);
                    mBtnRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
        mHandler = new Handler();
    }

    /**
     * 设置播放信息
     * @param roomList
     */
    public void setPlayerInfo(RoomList roomList){
        if(null==mRoomLists) mRoomLists=new ArrayList<>();
        mRoomLists.add(roomList);
    }

    /**
     * 设置批量播放信息
     * @param roomLists
     */
    public void setPlayerInfo(List<RoomList> roomLists){
        mRoomLists=roomLists;
    }

    /**
     * 应用场景 在播放前设置
     * @param scenMode 0：但场次 1：列表播放
     */
    public void setScenMode(int scenMode) {
        this.mScenMode=scenMode;
    }

    /**
     * 初始化
     * @param frontcover
     */
    public void onInit(String frontcover){
        initCover(frontcover);
    }


    /**
     * 在 setPlayerInfo 之后调用
     * @param frontcover
     */
    public void start(String frontcover){
        isDestroy=false;
        waitPlayVideo(1500,frontcover);
    }

    /**
     * 开启延缓任务
     * @param misTime 需要延缓多久
     * @param frontcover
     */
    private void waitPlayVideo(long misTime, String frontcover) {
        if(null!=mHandler) mHandler.removeMessages(0);
        initCover(frontcover);
        if(null!=mHandler) {
            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    if(!isDestroy&&null!=mPlayerManager){
                        startPlay(false);
                    }else{
                    }
                }
            }, SystemClock.uptimeMillis()+misTime);
        }
    }


    /**
     * 加载中
     */
    private void showLoadingView(){
        if(null!= mBtnRefresh) mBtnRefresh.setVisibility(GONE);
        if(null!=mPlayerManager) mPlayerManager.startLoadingView();
    }

    /**
     * 隐藏加载中
     */
    private void stopLoadingView(){
        if(null!= mBtnRefresh) mBtnRefresh.setVisibility(mScenMode==MODE_LIST_PLAY?VISIBLE:GONE);
        if(null!=mPlayerManager) mPlayerManager.stopLoadingView();
    }

    /**
     * 开始拉流
     * @param isInitCover 是否需要初始化封面
     */
    protected void startPlay(boolean isInitCover) {
        if(null==mRoomLists||null==mPlayerManager) return;
        if((mRoomLists.size()-1)<=playCurrentIndex) playCurrentIndex=0;
        RoomList roomList = mRoomLists.get(playCurrentIndex);
        if(isInitCover) initCover(roomList);
        if(null!=mPlayerManager&&null!=roomList&&!TextUtils.isEmpty(roomList.getPlayUrl())){
            HttpProxyCacheServer cacheServer = AppEngine.getInstance().getProxyCacheServer();
            String proxyUrl = cacheServer.getProxyUrl(roomList.getPlayUrl());
            mPlayerManager.startPlay(proxyUrl);
        }
        playCurrentIndex++;
    }

    /**
     * 设置封面
     * @param roomList
     */
    private void initCover(RoomList roomList) {
        if(null==roomList) return;
        initCover(roomList.getVideoCover());
    }

    /**
     * 设置封面
     * @param frontCover
     */
    private void initCover(String frontCover) {
        if(null==frontCover||null==mVideoCover) return;
        Glide.with(getContext())
                .load(frontCover)
                .asBitmap()
                .placeholder(R.drawable.ic_default_item_cover)
                .error(R.drawable.ic_default_item_cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .into(new BitmapImageViewTarget(mVideoCover) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                    }
                });
//        Glide.with(getContext())
//                .load(frontCover)
//                .error(R.drawable.ic_item_default_cover)
//                .placeholder(R.drawable.ic_item_default_cover)
//                .crossFade()//渐变
//                .thumbnail(0.1f)
//                .transform(new GlideRoundTransform(getContext(),10))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
//                .centerCrop()//中心点缩放
//                .skipMemoryCache(true)//跳过内存缓存
//                .into(mVideoCover);
    }

    /**
     * 结束播放
     */
    protected void stopPlay() {
        if(null!=mPlayerManager) mPlayerManager.onDestroy();
    }


    /**
     * 开始
     */
    public void onStart(){
        isDestroy=false;
        waitPlayVideo(1500,null);
    }

    /**
     * 伪 onResume
     */
    public void onResume(){
        if(null!=mPlayerManager) mPlayerManager.onResume();
    }


    /**
     * 伪 onStop
     */
    public void onStop(){
        if(null!=mHandler) mHandler.removeMessages(0);
        isDestroy=true;
        stopPlay();
    }


    /**
     * 伪 onPause
     */
    public void onPause(){
        if(null!=mPlayerManager) mPlayerManager.onPause();
    }



    /**
     * 伪 onDestroy
     */
    public void onDestroy(){
        isDestroy=true;
        if(null!=mHandler) mHandler.removeMessages(0);
        stopPlay();
        playCurrentIndex=0; mScenMode=0;
        if(null!=mRoomLists) mRoomLists.clear(); mRoomLists=null;
    }
}
