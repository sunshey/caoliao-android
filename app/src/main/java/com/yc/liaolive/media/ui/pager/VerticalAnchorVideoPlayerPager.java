package com.yc.liaolive.media.ui.pager;

import android.app.Activity;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BasePager;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.PagerVerticalAnchorVideoPlayerBinding;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.media.view.AnchorVideoPlayerController;
import com.yc.liaolive.media.view.PlayerAdLayout;
import com.yc.liaolive.media.view.VideoPlayerStatusController;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/1/14
 * 垂直手势滚动在线主播列表
 */

public class VerticalAnchorVideoPlayerPager extends BasePager<PagerVerticalAnchorVideoPlayerBinding> implements Observer{

    private final RoomList mAnchorInfo;
    private AnchorVideoPlayerController mControllerLayout;
    private LiveVideoPlayerManager mPlayerManager;

    /**
     * @param context
     * @param roomList 主播对象
     * @param position 当前预览到第几个位置
     */
    public VerticalAnchorVideoPlayerPager(Activity context, RoomList roomList, int position) {
        super(context);
        this.mAnchorInfo =roomList;
        setContentView(R.layout.pager_vertical_anchor_video_player);
        ApplicationManager.getInstance().addObserver(this);
    }

    /**
     * UI组件初始化
     */
    @Override
    public void initViews() {
        if(null== mAnchorInfo ||null==bindingView) return;
        //控制器
        mControllerLayout = new AnchorVideoPlayerController(getContext());
        mControllerLayout.setVisibility(View.GONE);
        mControllerLayout.setControllerFunctionListener(new AnchorVideoPlayerController.OnControllerFunctionListener() {
            //开始视频通话
            @Override
            public void onStartVideoCall(RoomList roomList) {
                if(null!=roomList&&null!=getContext()){
                    //尝试与其视频通话
                    CallExtraInfo callExtraInfo=new CallExtraInfo();
                    callExtraInfo.setToUserID(roomList.getUserid());
                    callExtraInfo.setToNickName(roomList.getNickname());
                    callExtraInfo.setToAvatar(roomList.getAvatar());
                    if(null!=roomList.getVideo_chat()){
                        callExtraInfo.setAnchorFront(roomList.getVideo_chat().getImg_path());
                        callExtraInfo.setVideoPath(roomList.getVideo_chat().getFile_path());
                    }
                    MakeCallManager.getInstance().attachActivity(((Activity) getContext())).mackCall(callExtraInfo, 1);
                }
            }

            //关闭
            @Override
            public void onBack() {
                if(null!=getContext()){
                    getContext().onBackPressed();
                }
            }
        });
        bindingView.videoController.addView(mControllerLayout);
        bindingView.videoController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(null!=mPlayerManager){
                     if(mPlayerManager.startPlaying()){
                         mPlayerManager.pauseAndStartPlay();
                     }else{
                         startPlayer();
                     }
                 }
            }
        });
        mControllerLayout.setAnchorData(mAnchorInfo);//更新界面元素

        //播放器事件监听
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setStatusController(new VideoPlayerStatusController(getContext()));
        mPlayerManager.setLooping(true);
        mPlayerManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayerManager.startPlaying()){
                    mPlayerManager.pauseAndStartPlay();
                }
            }
        });
    }

    @Override
    public void initData() {
        if(null== mAnchorInfo ||null==bindingView) return;
        //广告组件初始化
        if(null!=mAnchorInfo.getBanners()&&mAnchorInfo.getBanners().size()>0){
            bindingView.adViewLayout.setVisibility(View.VISIBLE);
            bindingView.adViewLayout.setOnAdClickListener(new PlayerAdLayout.OnAdClickListener() {
                @Override
                public void onBack(View view) {
                    if(null!=getContext()){
                        getContext().onBackPressed();
                    }
                }
            });
            if(null!=mAnchorInfo.getBanners()&&mAnchorInfo.getBanners().size()>0){
                bindingView.adViewLayout.init(mAnchorInfo);
            }
        }else{
            String cover=mAnchorInfo.getAvatar();
            if(null!=mAnchorInfo.getMy_image_list()&&mAnchorInfo.getMy_image_list().size()>0){
                cover=mAnchorInfo.getMy_image_list().get(0).getImg_path();
            }
            if(null!=mPlayerManager) mPlayerManager.setVideoCover(cover,false);
        }
    }

    /**
     * 开始播放
     */
    public void startPlayer(){
        if(isVisible()&&null!=mPlayerManager&&null!= mAnchorInfo &&null!=mAnchorInfo.getVideo_chat()){
            mPlayerManager.startPlay(mAnchorInfo.getVideo_chat().getFile_path(),true);
        }
    }

    /**
     * 开始播放
     */
    private void onStartPlay() {
        if(null== mAnchorInfo ||null==bindingView) return;
        //避免快速滑动过程中，正在购买多媒体文件而导致界面不可见可能出现的视频播放
        if(isVisible()){
            if(null==getContext()) return;
            if(null!=mPlayerManager) mPlayerManager.onStartPrepared();
            //开始视频播放，检查用户网络
            if(Utils.isCheckNetwork()&& NetContants.NETWORK_STATE_WIFI!= Utils.getNetworkType()&& !VideoApplication.getInstance().isVideoNetwork()){
                QuireDialog.getInstance(((Activity) getContext()))
                        .setTitleText("非WIFI环境提示")
                        .setContentText(getContext().getResources().getString(R.string.text_tips_4g))
                        .setSubmitTitleText("确定")
                        .setCancelTitleText("取消")
                        .setDialogCanceledOnTouchOutside(false)
                        .setDialogCancelable(false)
                        .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent() {
                                VideoApplication.getInstance().setVideoNetwork(true);
                                startPlayer();
                            }

                            @Override
                            public void onRefuse() {
                                if(null!=mPlayerManager) mPlayerManager.reset();
                            }
                        }).show();
            }else{
                startPlayer();
            }
        }
    }

    /**
     * 控制器的透明度
     * @param alpha
     */
    public void setConntrollerAlpha(float alpha) {
        if(null!=mPlayerManager) mPlayerManager.setCoverAlpha(alpha);
        if(null!=mControllerLayout) mControllerLayout.setTabAlpha(alpha);
        if(null!=bindingView) bindingView.adViewLayout.setAlpha(alpha);
    }

    /**
     * 处于初始化阶段
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 准备开始视频播放
     * 有关付费验证在这里进行
     */
    @Override
    public void onStart() {
        super.onStart();
        if(null==bindingView) return;
        if(null!=mControllerLayout) mControllerLayout.onStart();
        if(null==mAnchorInfo) return;
        //预览广告
        if(null!=mAnchorInfo.getBanners()&&mAnchorInfo.getBanners().size()>0){
            showAdView();
        }else{
            //视频预览
            if(null!=mControllerLayout) mControllerLayout.setVisibility(View.VISIBLE);
            onStartPlay();
        }
    }

    /**
     * 处于可见
     */
    @Override
    public void onResume() {
        super.onResume();
        if(null!=mPlayerManager){
            mPlayerManager.onStart();
            mPlayerManager.onInForeground();
        }
    }

    /**
     * 处于不可见阶段
     */
    @Override
    public void onPause() {
        super.onPause();
        if(null!= mPlayerManager){
            mPlayerManager.onStop();
            mPlayerManager.onInBackground(true);
        }
    }

    /**
     * 处于回收阶段
     */
    @Override
    public void onStop() {
        super.onStop();
        if(null!=mPlayerManager) mPlayerManager.onStop(false);
        if(null!=mControllerLayout) {
            mControllerLayout.onStop();
            mControllerLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 临时性的广告View
     */
    public void showAdView() {
        if(bindingView.adViewLayout.getVisibility()!=View.VISIBLE){
            bindingView.adViewLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg && arg instanceof String){
            String cmd= (String) arg;
            if(cmd.equals(Constant.OBSERVER_CMD_FOLLOW_TRUE)||cmd.equals(Constant.OBSERVER_CMD_FOLLOW_FALSE)){
                if(null!=mControllerLayout) mControllerLayout.onReset();
            }
        }
    }

    /**
     * 触发了返回事件
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
//        if(null!=mControllerLayout) mControllerLayout.setVisibility(View.GONE);
    }

    /**
     * 完全销毁阶段
     */
    @Override
    public void onDestroy() {
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        if(null!=mControllerLayout) mControllerLayout.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
    }
}