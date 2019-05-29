package com.android.imusic.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.android.imusic.R;
import com.android.imusic.music.base.MusicBaseActivity;
import com.android.imusic.music.engin.IndexPersenter;
import com.android.imusic.music.net.MusicNetUtils;
import com.android.imusic.music.utils.MediaUtils;
import com.android.imusic.video.adapter.VideoDetailsAdapter;
import com.android.imusic.video.bean.OpenEyesIndexInfo;
import com.android.imusic.video.bean.OpenEyesIndexItemBean;
import com.android.imusic.video.bean.VideoParams;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.reflect.TypeToken;
import com.music.player.lib.adapter.base.OnItemClickListener;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicUtils;
import com.video.player.lib.base.BaseVideoPlayer;
import com.video.player.lib.controller.DetailsCoverController;
import com.video.player.lib.manager.VideoPlayerManager;
import com.video.player.lib.model.VideoPlayerScene;
import com.video.player.lib.utils.VideoUtils;
import com.video.player.lib.view.VideoDetailsPlayerTrackView;
import com.video.player.lib.view.VideoPlayerTrackView;
import com.video.player.lib.view.VideoTextureView;

/**
 * TinyHung@Outlook.com
 * 2019/4/10
 * VideoPlayer Activity
 */

public class VideoPlayerActviity extends MusicBaseActivity<IndexPersenter> {

    private static final String TAG = "VideoPlayerActviity";
    private VideoDetailsPlayerTrackView  mVideoPlayer;
    private VideoDetailsAdapter mAdapter;
    //视频参数
    private VideoParams mVideoParams;
    private boolean mIsPlaying;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_activity);
        initViews();
        mPresenter=new IndexPersenter();
        getIntentParams(getIntent(),true);
    }

    private void initViews() {
        //播放器控件宽高
        mVideoPlayer = (VideoDetailsPlayerTrackView ) findViewById(R.id.video_player);
        int itemHeight = MusicUtils.getInstance().getScreenWidth(this) * 9 / 16;
        mVideoPlayer.getLayoutParams().height=itemHeight;
        DetailsCoverController coverController = new DetailsCoverController(VideoPlayerActviity.this);
        mVideoPlayer.setCoverController(coverController,false);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new VideoDetailsAdapter(VideoPlayerActviity.this,null);
        //条目点击事件
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long itemId){
                if(null!=view.getTag()){
                    OpenEyesIndexItemBean indexItemBean = (OpenEyesIndexItemBean) view.getTag();
                    VideoPlayerTrackView playerTrackView = (VideoPlayerTrackView) view.findViewById(R.id.video_track);
                    Intent intent=new Intent(VideoPlayerActviity.this, VideoPlayerActviity.class);
                    VideoParams videoParams= MediaUtils.getInstance().formatVideoParams(indexItemBean);
                    intent.putExtra(MusicConstants.KEY_VIDEO_PARAMS,videoParams);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_tiny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mVideoPlayer){
                    int startX=VideoUtils.getInstance().getScreenWidth(VideoPlayerActviity.this)/2-VideoUtils.getInstance().dpToPxInt(VideoPlayerActviity.this,10f);
                    int startY=mVideoPlayer.getMeasuredHeight()+VideoUtils.getInstance().dpToPxInt(VideoPlayerActviity.this,10f);
                    mVideoPlayer.startTinyWindowPlay(startX,startY,0,0);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d(TAG,"onNewIntent-->");
        getIntentParams(intent,false);
    }

    /**
     * 获取视频入参
     * @param intent
     * @param isCreate
     */
    private void getIntentParams(Intent intent,boolean isCreate) {
        if(null==intent) return;
        mVideoParams = intent.getParcelableExtra(MusicConstants.KEY_VIDEO_PARAMS);
        mIsPlaying = intent.getBooleanExtra(MusicConstants.KEY_VIDEO_PLAYING,false);
        if(null!=mAdapter&&mAdapter.getData().size()>0){
            mAdapter.getData().get(0).setVideoParams(mVideoParams);
            mAdapter.notifyDataSetChanged();
        }
        if(null==mVideoParams){
            Toast.makeText(VideoPlayerActviity.this,"缺少必要参数",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(TextUtils.isEmpty(mVideoParams.getVideoUrl())){
            Toast.makeText(VideoPlayerActviity.this,"缺少必要参数",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initVideoParams(isCreate);
    }

    /**
     * 播放器初始化
     * @param isCreate
     */
    private void initVideoParams(boolean isCreate) {
        if(null!=mVideoParams){
            mVideoPlayer.setDataSource(mVideoParams.getVideoUrl(),mVideoParams.getVideoTitle(), VideoPlayerScene.NOIMAL);
            mVideoPlayer.setLoop(true);
            mVideoPlayer.setWorking(true);
            //封面
            if(null!=mVideoPlayer.getCoverController()){
                Glide.with(VideoPlayerActviity.this)
                        .load(mVideoParams.getVideoCover())
                        .placeholder(R.drawable.ic_video_default_cover)
                        .error(R.drawable.ic_video_default_cover)
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mVideoPlayer.getCoverController().mVideoCover);
            }
            //无缝衔接外部播放任务
            if(mIsPlaying&&null!=VideoPlayerManager.getInstance().getTextureView()){
                addTextrueViewToView(mVideoPlayer);
                VideoPlayerManager.getInstance().addOnPlayerEventListener(mVideoPlayer);
                //手动检查播放器内部状态，同步常规播放器状态至全屏播放器
                VideoPlayerManager.getInstance().checkedVidepPlayerState();
            }else{
                //开始全新播放任务
                mVideoPlayer.starPlaytVideo();
            }
            if(isCreate){
                //获取推荐视频
                mPresenter.getRecommendVideoList(mVideoParams.getVideoiId(),new TypeToken<OpenEyesIndexInfo>(){}.getType(),new MusicNetUtils.OnOtherRequstCallBack<OpenEyesIndexInfo>() {

                    @Override
                    public void onResponse(OpenEyesIndexInfo data) {
                        if(null!=mAdapter){
                            if(null!=data.getItemList()&&data.getItemList().size()>0){
                                mAdapter.onLoadComplete();
                                OpenEyesIndexItemBean openEyesIndexItemBean=new OpenEyesIndexItemBean();
                                openEyesIndexItemBean.setType(MusicConstants.VIDEO_HEADER);
                                openEyesIndexItemBean.setVideoParams(mVideoParams);
                                data.getItemList().add(0,openEyesIndexItemBean);
                                mAdapter.setNewData(data.getItemList());
                            }else{
                                mAdapter.onLoadEnd();
                            }
                        }
                    }

                    @Override
                    public void onError(int code, String errorMsg) {
                        Logger.d(TAG,"onError-->code:"+code+",errorMsg:"+errorMsg);
                        if(null!=mAdapter){
                            mAdapter.onLoadError();
                        }
                    }
                });
            }
        }
    }


    /**
     * 添加一个视频渲染组件至View
     * @param videoPlayer
     */
    private void addTextrueViewToView(BaseVideoPlayer videoPlayer) {
        //先移除存在的TextrueView
        if(null!=VideoPlayerManager.getInstance().getTextureView()){
            VideoTextureView textureView = VideoPlayerManager.getInstance().getTextureView();
            if(null!=textureView.getParent()){
                ((ViewGroup) textureView.getParent()).removeView(textureView);
            }
        }
        if(null!=VideoPlayerManager.getInstance().getTextureView()){
            videoPlayer.mSurfaceView.addView(VideoPlayerManager.getInstance().getTextureView(),new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(VideoPlayerManager.getInstance().isBackPressed()){
            VideoPlayerManager.getInstance().onDestroy();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG,"onDestroy");
        VideoPlayerManager.getInstance().onDestroy();
        if(null!=mAdapter){
            mAdapter.onDestroy();
            mAdapter=null;
        }
        if(null!=mVideoPlayer){
            mVideoPlayer.destroy();
            mVideoPlayer=null;
        }
    }
}