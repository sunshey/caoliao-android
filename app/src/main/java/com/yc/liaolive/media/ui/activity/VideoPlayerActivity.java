package com.yc.liaolive.media.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.VideoPlayerActivityBinding;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.bean.VideoChatInfo;
import com.yc.liaolive.live.constants.IVideoPlayerView;
import com.yc.liaolive.live.presenter.VideoPlayerPresenter;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.view.VideoPlayerStatusController;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by yangxueqin on 2018/12/6.
 * 1v1列表图片点击进入主播小视频
 */

public class VideoPlayerActivity extends BaseActivity<VideoPlayerActivityBinding> implements Observer, View.OnClickListener, IVideoPlayerView {

    private static final String TAG = "VideoPlayerActivity";
    private RoomList mRoomData;
    private VideoPlayerPresenter mPresenter;
    private boolean is_attention; //是否关注了主播
    private LiveVideoPlayerManager mPlayerManager;

    public static void start (RoomList mData) {
        Intent intent = CaoliaoController.createIntent(VideoPlayerActivity.class.getName());
        intent.putExtra("roomdata", (Serializable) mData);
        CaoliaoController.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);//全屏
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);//禁止用户截屏
        setContentView(R.layout.video_player_activity);
        ApplicationManager.getInstance().addObserver(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(null!=mPlayerManager) mPlayerManager.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(null!=mPlayerManager) mPlayerManager.onStop();
    }

    @Override
    public void initViews() {
        mPresenter = new VideoPlayerPresenter();
        mPresenter.attachView(this);
        bindingView.videoView.setOnClickListener(this);
        bindingView.viewBtnClose.setOnClickListener(this);
        bindingView.viewHeadIcon.setOnClickListener(this);
        bindingView.viewAddFollow.setOnClickListener(this);
        bindingView.btnToVideo.setOnClickListener(this);
        //初始化播放器
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setLooping(true);
        mPlayerManager.setStatusController(new VideoPlayerStatusController(   VideoPlayerActivity.this));
    }


    @Override
    public void initData() {
        Intent intent = getIntent();
        mRoomData = (RoomList) intent.getSerializableExtra("roomdata");
        if (null == mRoomData || mRoomData.getVideo_chat() == null || null == bindingView) {
            finish();
            return;
        }
        mPlayerManager.setVideoCover(mRoomData.getVideo_chat().getImg_path(),false);
        mPlayerManager.startPlay(mRoomData.getVideo_chat().getFile_path(),true);
        //主播状态
        bindingView.userOfflineState.setData(mRoomData.getItemCategory(), 0);
        mPresenter.getVideoData(mRoomData.getUserid());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_view:
                if(null!=mPlayerManager) mPlayerManager.pauseAndStartPlay();
                break;
            case R.id.btn_to_video:
                //尝试与其视频通话
                CallExtraInfo callExtraInfo=new CallExtraInfo();
                callExtraInfo.setToUserID(mRoomData.getUserid());
                callExtraInfo.setToNickName(mRoomData.getNickname());
                callExtraInfo.setToAvatar(mRoomData.getAvatar());
                callExtraInfo.setAnchorFront(mRoomData.getVideo_chat().getImg_path());
                callExtraInfo.setVideoPath(mRoomData.getVideo_chat().getFile_path());
                MakeCallManager.getInstance().attachActivity(((Activity) getContext())).mackCall(callExtraInfo, 1);
                break;
            case R.id.view_btn_close:
                finish();
                break;
            case R.id.view_head_icon:
                PersonCenterActivity.start(getContext(),mRoomData.getUserid());
                break;
            case R.id.view_add_follow:
                if(null != mRoomData) {
                    UserManager.getInstance().followUser(mRoomData.getUserid(), is_attention ? 0 : 1, new UserServerContract.OnNetCallBackListener() {
                        @Override
                        public void onSuccess(Object object) {
                            is_attention = !is_attention;
                            if(is_attention) ToastUtils.showCenterToast("已关注");
                            VideoApplication.getInstance().setMineRefresh(true);
                            updataFollowState();
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            ToastUtils.showCenterToast(errorMsg);
                        }
                    });
                }
                break;
        }
    }

    /**
     * 刷新关注状态
     */
    private void updataFollowState() {
        if(null != bindingView){
            if(null != mRoomData && TextUtils.equals(mRoomData.getUserid(), UserManager.getInstance().getUserId())){
                bindingView.viewAddFollow.setVisibility(View.INVISIBLE);
            } else {
                bindingView.viewAddFollow.setVisibility(is_attention ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (null != arg && arg instanceof String ){
            //关注
            if(TextUtils.equals(Constant.OBSERVER_CMD_FOLLOW_TRUE, (String) arg)){
                is_attention = true;
                updataFollowState();
            //取关
            }else if(TextUtils.equals(Constant.OBSERVER_CMD_FOLLOW_FALSE, (String) arg)){
                is_attention = false;
                updataFollowState();
            }else if( TextUtils.equals(Constant.OBSERVER_FINLISH_MEDIA_PLAYER, (String) arg)){
                finish();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if(null!=mPlayerManager) mPlayerManager.onDestroy();
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        MakeCallManager.getInstance().onDestroy();
    }

    @Override
    public void setVideoData(VideoChatInfo info) {
        if (bindingView != null && info != null) {
            //设置用户头像
            if(!TextUtils.isEmpty(mRoomData.getAvatar())){
                Glide.with(getContext())
                        .load(mRoomData.getAvatar())
                        .error(R.drawable.ic_default_user_head)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                        .centerCrop()//中心点缩放
                        .skipMemoryCache(true)//跳过内存缓存
                        .transform(new GlideCircleTransform(getContext()))
                        .into(bindingView.viewHeadIcon);
            }
            //昵称
            bindingView.viewTitle.setText(mRoomData.getNickname());
            //在线状态
            bindingView.userOfflineState.setData(info.getUser_state(), 0);
            //0未关注 1关注
            is_attention = info.getIs_attention() == 1;
            updataFollowState();

            bindingView.viewVideoDesp.setText(info.getSignature());
            bindingView.tvVideoPrice.setText(MessageFormat.format("（{0}钻石/分钟）", info.getChat_deplete()));

            bindingView.userFlags.removeAllViews();
            for (String label : info.getLabel()) {
                TextView flagTv = new TextView(this);
                flagTv.setTextSize(10);
                flagTv.setTextColor(getResources().getColor(R.color.white));
                flagTv.setText(label);
                flagTv.setGravity(Gravity.CENTER);
                flagTv.setBackground(ContextCompat.getDrawable(AppEngine.getApplication(), R.drawable.anchor_flags_bg));
                bindingView.userFlags.addView(flagTv);
            }
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }
}