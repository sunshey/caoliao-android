package com.yc.liaolive.media.ui.pager;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.MusicUtils;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BasePager;
import com.yc.liaolive.bean.MediaFileInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.ShareInfo;
import com.yc.liaolive.bean.UnReadMsg;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.PagerVerticalVideoPlayerBinding;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.interfaces.OnMediaPlayerListener;
import com.yc.liaolive.interfaces.ShareFinlishListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.media.ui.activity.VerticalVideoPlayerAvtivity;
import com.yc.liaolive.media.view.PlayerAdLayout;
import com.yc.liaolive.media.view.VideoGroupRelativeLayout;
import com.yc.liaolive.media.view.VideoPlayerControllerLayout;
import com.yc.liaolive.media.view.VideoPlayerStatusController;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.contract.VideoActionContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.VideoActionPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.ui.activity.CallRechargeActivity;
import com.yc.liaolive.videocall.ui.dialog.QuireVideoDialog;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 垂直手势滚动视频播放器片段
 */

public class VerticalVideoPlayerPager extends BasePager<PagerVerticalVideoPlayerBinding> implements VideoActionContract.View, Observer {

    private final PrivateMedia mVideoInfo;
    private VideoPlayerControllerLayout mControllerLayout;
    private final VideoActionPresenter mPresenter;
    private int is_buy = 0;//是否第一次观看视频  0：询问用户 1：直接购买
    private LiveVideoPlayerManager mPlayerManager;
    //媒体类型，参考 MEDIA_TYPE_ASMR_AUDIO
    private int mMediaType;
    private boolean mIsTouchSeekBar;
    /**
     * @param context
     * @param indexVideoInfo 视频对象
     * @param position 当前预览到第几个位置
     */
    public VerticalVideoPlayerPager(Activity context, PrivateMedia indexVideoInfo, int position) {
        super(context);
        this.mVideoInfo=indexVideoInfo;
        setContentView(R.layout.pager_vertical_video_player);
        mPresenter = new VideoActionPresenter();
        mPresenter.attachView(this);
        ApplicationManager.getInstance().addObserver(this);
    }

    /**
     * UI组件初始化
     */
    @Override
    public void initViews() {
        if(null==mVideoInfo||null==bindingView) return;
        //控制器
        mControllerLayout = new VideoPlayerControllerLayout(getContext());
        mControllerLayout.setVisibility(View.GONE);
        mControllerLayout.setControllerFunctionListener(new VideoPlayerControllerLayout.OnControllerFunctionListener() {
            //点赞
            @Override
            public void onLike(PrivateMedia privateMedia) {
                if(null!=mVideoInfo){
                    if(null!=bindingView) bindingView.heartLayout.startPriceAnimation();
                    if(null!=mPresenter&&!mPresenter.isLoveing()) mPresenter.videoLoveShare(mVideoInfo,0);
                }
            }

            //分享
            @Override
            public void onShare(PrivateMedia privateMedia) {
                if(null!=mVideoInfo&&null!=getContext()&&getContext() instanceof VerticalVideoPlayerAvtivity){
                    if(1==mVideoInfo.getIs_private()){
                        ToastUtils.showCenterToast("私密视频无法分享");
                        return;
                    }
                    VerticalVideoPlayerAvtivity activity= (VerticalVideoPlayerAvtivity) getContext();
                    ShareInfo shareInfo=new ShareInfo();
                    shareInfo.setTitle(TextUtils.isEmpty(mVideoInfo.getVideo_desp())?mVideoInfo.getNickname():mVideoInfo.getVideo_desp());
                    shareInfo.setVideoID(String.valueOf(mVideoInfo.getId()));
                    shareInfo.setRoomid("0");
                    shareInfo.setDesp(mVideoInfo.getNickname()+"的视频");
                    shareInfo.setUserID(mVideoInfo.getUserid());
                    shareInfo.setImageLogo(mVideoInfo.getAvatar());
                    shareInfo.setReport(true);
                    shareInfo.setUrl("http://cl.dapai52.com/share/share.html");
                    shareInfo.setShareTitle("分享视频到");
                    activity.share(shareInfo, new ShareFinlishListener() {
                        //分享成功的VideoID,平台ID
                        @Override
                        public void shareSuccess(String id,int platformID) {
                            if(null!=mVideoInfo&&null!=mPresenter&&!mPresenter.isLoveing()) mPresenter.videoLoveShare(mVideoInfo,1);
                        }
                    });
                }
            }

            //关闭
            @Override
            public void onBack() {
                if(null!=getContext()){
                    getContext().onBackPressed();
                }
            }

            //去付费购买多媒体文件
            @Override
            public void buyMediaFile(PrivateMedia privateMedia) {
                onStartPlay();
            }
        });
        bindingView.videoController.addView(mControllerLayout);
        mControllerLayout.setVideoData(mVideoInfo);//更新界面元素
        checkedReadMsg();//检查未读消息
        //双击、单机时间监听
        bindingView.heartLayout.setImageVisibility();//默认点赞是隐藏的
        bindingView.heartLayout.setOnDoubleClickListener(new VideoGroupRelativeLayout.OnDoubleClickListener() {
            //双击事件
            @Override
            public void onDoubleClick() {
                if(null==mVideoInfo) return;
                if(!TextUtils.isEmpty(mVideoInfo.getFile_path())){
                    bindingView.heartLayout.startPriceAnimation();
                    if(null!=mPresenter&&!mPresenter.isLoveing()) mPresenter.videoLoveShare(mVideoInfo,0);
                }else{
                    onStartPlay();
                }
            }

            //单击事件
            @Override
            public void onSingleClick() {
                if(null==mVideoInfo||null==mPlayerManager) return;
                //暂停、开始视频播放
                if(!TextUtils.isEmpty(mVideoInfo.getFile_path())){
                    if(mPlayerManager.startPlaying()){
                        mPlayerManager.pauseAndStartPlay();
                    }else{
                        onStartPlay();
                    }
                }else{
                    onStartPlay();
                }
            }

            //向左滑动
            @Override
            public void onLeftSwipe() {

            }

            //向右滑动
            @Override
            public void onRightSwipe() {

            }
        });
        //播放器事件监听
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setStatusController(new VideoPlayerStatusController(getContext()));
        mPlayerManager.setLooping(true);
    }

    @Override
    public void initData() {
        if(null==mVideoInfo||null==bindingView) return;
        //广告组件初始化
        if(mVideoInfo.getItemType()== IndexVideoListAdapter.ITEM_TYPE_BANNERS||mVideoInfo.getItemType()== IndexVideoListAdapter.ITEM_TYPE_BANNER){
            bindingView.adViewLayout.setVisibility(View.VISIBLE);
            bindingView.adViewLayout.setOnAdClickListener(new PlayerAdLayout.OnAdClickListener() {
                @Override
                public void onBack(View view) {
                    if(null!=getContext()){
                        getContext().onBackPressed();
                    }
                }
            });
            bindingView.adViewLayout.init(mVideoInfo);
        }else{
            if(null!=mPlayerManager) mPlayerManager.setVideoCover(mVideoInfo.getImg_path(),false);
        }
    }

    /**
     * ASMR视频不显示主播信息，增加进度条调节
     * @param mediatype 4:ASMR视频
     */
    public void setMediaType(int mediatype) {
        this.mMediaType=mediatype;
        Logger.d(TAG,"setMediaType-->mediatype:"+mediatype);
        if(null!=mControllerLayout){
            mControllerLayout.setMediaType(mediatype);
        }
        if(null!=mVideoInfo&&mVideoInfo.getItemType()==IndexVideoListAdapter.ITEM_TYPE_ASMR_VIDEO){
            bindingView.bottomProgressView.setVisibility(View.VISIBLE);
        }
        //ASMR视频
        if(null!=mPlayerManager&&mediatype==Constant.MEDIA_TYPE_ASMR_VIDEO){
            bindingView.current.setText("00:00");
            bindingView.total.setText("00:00");
            bindingView.bottomSeekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        long durtion = MusicPlayerManager.getInstance().getDurtion();
                        if(durtion>0){
                            bindingView.current.setText(MusicUtils.getInstance().stringForAudioTime(progress * durtion / 100));
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mIsTouchSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mIsTouchSeekBar=false;
                    if(null!=bindingView){
                        long durtion = mPlayerManager.getDurtion();
                        if(durtion>0){
                            long currentTime = seekBar.getProgress() * durtion / 100;
                            mPlayerManager.seekTo(currentTime);
                        }
                    }
                }
            });

            mPlayerManager.setMediaPlayerListener(new OnMediaPlayerListener() {

                @Override
                public void onPrepared(long totalDurtion) {
                    if(null!=bindingView){
                        bindingView.total.setText(MusicUtils.getInstance().stringForAudioTime(totalDurtion));
                    }
                }

                @Override
                public void onBufferingUpdate(int progress) {
                    if(null!=bindingView){
                        bindingView.bottomSeekProgress.setSecondaryProgress(progress);
                    }
                }

                @Override
                public void onPlayingProgress(long currentDurtion, long totalDurtion) {
                    if(null!=bindingView){
                        if(!mIsTouchSeekBar){
                            int progress = (int) (((float) currentDurtion / totalDurtion) * 100);
                            bindingView.bottomSeekProgress.setProgress(progress);
                        }
                        bindingView.current.setText(MusicUtils.getInstance().stringForAudioTime(currentDurtion));
                    }
                }

                @Override
                public void onCompletion() {
                    if(null!=bindingView){
                        bindingView.bottomSeekProgress.setProgress(100);
                    }
                }

                @Override
                public void onError(int errorCode) {
                    if(null!=bindingView){
                        bindingView.current.setText("00:00");
                        bindingView.bottomSeekProgress.setSecondaryProgress(0);
                        bindingView.bottomSeekProgress.setProgress(0);
                    }
                }
            });
        }
    }

    /**
     * 开始播放
     */
    public void startPlayer(){
        if(isVisible()&&null!=mPlayerManager&&null!=mVideoInfo&&!TextUtils.isEmpty(mVideoInfo.getFile_path())){
            mPlayerManager.startPlay(mVideoInfo.getFile_path(),true);
        }
    }

    /**
     * 开始播放
     */
    private void onStartPlay() {
        if(null==mVideoInfo||null==bindingView) return;
        //避免快速滑动过程中，正在购买多媒体文件而导致界面不可见可能出现的视频播放
        if(isVisible()){
            if(null!=mPlayerManager) mPlayerManager.onStartPrepared();
            //需要付费的
            if(TextUtils.isEmpty(mVideoInfo.getFile_path())){
                getMediaPath(mVideoInfo);
                return;
            }
            //购买成功的
            if(null==getContext()) return;
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
     * 获取绝对路径再查看
     * @param videoInfo
     */
    private void getMediaPath(final PrivateMedia videoInfo) {
        if(!isVisible()) return;
        //付费点播
        UserManager.getInstance().browseMediaFile(videoInfo.getId(), videoInfo.getUserid(), Constant.MEDIA_VIDEO_LIST,is_buy,new UserServerContract.OnCallBackListener() {
            @Override
            public void onSuccess(int code,Object object,String msg) {
                if(null!=object&& object instanceof MediaFileInfo){
                    MediaFileInfo data= (MediaFileInfo) object;
                    if(null!=videoInfo&&isVisible()){
                        //先更新界面
                        videoInfo.setAttent(data.getAttent());
                        videoInfo.setLove_number(data.getLove_number());
                        videoInfo.setShare_number(data.getShare_number());
                        videoInfo.setBrowse_number(data.getBrowse_number());
                        if(!TextUtils.isEmpty(data.getSignature()))videoInfo.setSignature(data.getSignature());
                        videoInfo.setIs_love(data.getIs_love());
                        videoInfo.setIs_online(data.getIs_online());
                        videoInfo.setFile_path(data.getFile_path());
                        videoInfo.setUser_state(data.getUser_state());
                        videoInfo.setIdentity_audit(data.getIdentity_audit());
                        if(!TextUtils.isEmpty(data.getNickname()))videoInfo.setNickname(data.getNickname());
                        if(!TextUtils.isEmpty(data.getAvatar()))videoInfo.setAvatar(data.getAvatar());
                        if(null!=data.getRoom_info()) videoInfo.setRoomInfo(data.getRoom_info());//房间信息
                        if(null!=mControllerLayout) {
                            mControllerLayout.setVideoData(videoInfo);//刷新基本信息
                            mControllerLayout.updateRoomOffline();//刷新在线状态
                            mControllerLayout.onCreateGift(data.getGift_info(),data.getGift_rank());
                        }
                        //播放视频
                        if(!TextUtils.isEmpty(videoInfo.getFile_path())){
                            if(isVisible()) onStartPlay();
                            return;
                        }
                    }
                    //购买失败结束缓冲状态
                    if(null!=mPlayerManager) mPlayerManager.reset();
                    //余额不足
                    if(NetContants.API_RESULT_ARREARAGE_CODE==code&&isVisible()){
                        onRechgre(videoInfo);
                        return;
                    }
                    //需要购买
                    if(NetContants.API_RESULT_BUY==code&&isVisible()){
                        onBuyTips(msg);
                        return;
                    }
                    //其他错误信息
                    if(isVisible()) ToastUtils.showCenterToast(msg);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(null!=mPlayerManager) mPlayerManager.reset();
                if(isVisible()){
                    //文件未找到
                    if(NetContants.API_RESULT_CANT_FIND==code&&isVisible()){
                        onCantFind(errorMsg);
                        return;
                    }
                    ToastUtils.showCenterToast(errorMsg);
                }
            }
        });
    }

    /**
     * 未找到文件
     * @param msg
     */
    private void onCantFind(String msg) {
        if(null==getContext()) return;
        QuireVideoDialog.getInstance(getContext())
                .setTipsData(msg,null,"确定")
                .show();
    }


    /**
     * 购买钻石
     * @param videoInfo
     */
    private void onRechgre(PrivateMedia videoInfo) {
        if(null==getContext()) return;
        QuireDialog.getInstance(((Activity) getContext()))
                .setTitleText("钻石不足")
                .setContentText("观看此视频需要打赏"+videoInfo.getPrice()+"钻石")
                .setSubContentText("开通VIP免费看<font color='#FF7575'>海量私密视频</font>")
                .setSubmitTitleText("开通VIP")
                .setCancelTitleText("充值钻石")
                .setDialogCancelable(true)
                .showCloseBtn(true)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        MobclickAgent.onEvent(getContext(), "preview_file_rechgre_vip_"+mMediaType);
                        VipActivity.startForResult(((Activity) getContext()),1);
                    }

                    @Override
                    public void onRefuse() {
                        MobclickAgent.onEvent(getContext(), "preview_file_rechgre_gold_"+mMediaType);
                        CallRechargeActivity.start(getContext(), 20, null);
                    }
                }).show();
    }

    /**
     * 购买询问
     * @param msg  "<font color='#2A2A2A'>观看此视频需要打赏"+1500+"钻石</font>"
     */
    private void onBuyTips(String msg) {
        if(null==getContext()) return;
        QuireDialog.getInstance(((Activity) getContext()))
                .showTitle(false)
                .setContentTextColor(getContext().getResources().getColor(R.color.gray_text))
                .setContentText(msg)
                .setSubmitTitleText("确定")
                .setCancelTitleText("取消")
                .setDialogCancelable(true)
                .setDialogCanceledOnTouchOutside(true)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        MobclickAgent.onEvent(getContext(), "preview_file_ask_buy_"+mMediaType);
                        is_buy = 1;
                        onStartPlay();
                    }

                    @Override
                    public void onRefuse() {
                        MobclickAgent.onEvent(getContext(), "preview_file_ask_close_"+mMediaType);
                    }
                }).show();
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
        //视频预览
        if(null!=mVideoInfo&&(mVideoInfo.getItemType()==IndexVideoListAdapter.ITEM_TYPE_VIDEO||mVideoInfo.getItemType()==IndexVideoListAdapter.ITEM_TYPE_ASMR_VIDEO)){
            if(null!=mControllerLayout) mControllerLayout.setVisibility(View.VISIBLE);
            onStartPlay();
        //预览广告
        }else{
            showAdView();
        }
    }

    /**
     * 处于可见
     */
    @Override
    public void onResume() {
        super.onResume();
        if(null!=VideoApplication.getInstance().getMediaTops()&&VideoApplication.getInstance().getMediaTops().size()>0){
           if(null!=mControllerLayout) mControllerLayout.onUpdataMediaTops(VideoApplication.getInstance().getMediaTops());
            VideoApplication.getInstance().cleanMediaTops();
        }
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
        if(null!=mPlayerManager){
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
        mIsTouchSeekBar=false;
        if(null!=mPlayerManager) mPlayerManager.onStop(false);
        if(null!=mVideoInfo) mVideoInfo.setFile_path(null);//清空购买的文件地址
        if(null!=mControllerLayout) {
            mControllerLayout.onStop();
            mControllerLayout.setVisibility(View.GONE);
        }
        //还原进度条
        if(null!=bindingView){
            bindingView.current.setText("00:00");
            bindingView.bottomSeekProgress.setProgress(0);
            bindingView.bottomSeekProgress.setSecondaryProgress(0);
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
        if(null!=arg && arg instanceof String ){
            //自己对改视频作者有未读消息
            if(TextUtils.equals(Constant.OBSERVER_LIVE_MESSAGE_CHANGED, (String) arg)){
                checkedReadMsg();
            //关注
            }else if(TextUtils.equals(Constant.OBSERVER_CMD_FOLLOW_TRUE, (String) arg)){
                if(null!=mVideoInfo&&null!=mControllerLayout){
                    mVideoInfo.setAttent(1);
                    mControllerLayout.updataFollowState();
                }
            //取关
            }else if(TextUtils.equals(Constant.OBSERVER_CMD_FOLLOW_FALSE, (String) arg)){
                if(null!=mVideoInfo&&null!=mControllerLayout){
                    mVideoInfo.setAttent(0);
                    mControllerLayout.updataFollowState();
                }
            }
        }
    }

    /**
     * 检查未读消息
     */
    private synchronized void checkedReadMsg() {
        if(null==mVideoInfo||null==mControllerLayout) return;
        if(VideoApplication.getInstance().getUnReadMsgMap().containsKey(mVideoInfo.getUserid())){
            Iterator<Map.Entry<String, UnReadMsg>> iterator = VideoApplication.getInstance().getUnReadMsgMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, UnReadMsg> next = iterator.next();
                if(next.getKey().equals(mVideoInfo.getUserid())){
                    if(next.getValue().count>0){
                        mControllerLayout.setMsgIcon(R.drawable.ic_video_private_chat_new);
                    }else{
                        mControllerLayout.setMsgIcon(R.drawable.ic_video_private_chat);
                    }
                    break;
                }
            }
        }else{
            mControllerLayout.setMsgIcon(R.drawable.ic_video_private_chat);
        }
    }

    /**
     * 触发了返回时间
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
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        if(null!=mVideoInfo) mVideoInfo.setFile_path(null);//清空购买的文件地址
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
    }

    //==========================================网络交互回调=========================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showActionResul(PrivateMedia privateMedia, int actionType) {
        if(1==actionType){
            ToastUtils.showCenterToast("已分享");
        }
        if(null!=mControllerLayout) mControllerLayout.setVideoData(privateMedia);
    }

    @Override
    public void showActionError(int code, String errorMsg) {

    }

    @Override
    public void showMedias(List<PrivateMedia> data) {

    }

    @Override
    public void showMediaEmpty() {

    }

    @Override
    public void showMediaError(int code, String errorMsg) {

    }

}