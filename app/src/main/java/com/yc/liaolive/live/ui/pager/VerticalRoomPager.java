package com.yc.liaolive.live.ui.pager;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BasePager;
import com.yc.liaolive.bean.NoticeContent;
import com.yc.liaolive.bean.NumberChangedInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.PagerLiveRoomPullBinding;
import com.yc.liaolive.interfaces.OnMediaPlayerListener;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.listener.OnExceptionListener;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.presenter.RoomControllerInstance;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.view.LiveRoomAccountLayout;
import com.yc.liaolive.live.view.SwipeAnimationController;
import com.yc.liaolive.live.view.VideoLiveControllerView;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.media.view.PlayerAdLayout;
import com.yc.liaolive.media.view.VideoPlayerStatusController;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.manager.VideoCallManager;

/**
 * TinyHung@Outlook.com
 * 2019/1/24
 * 垂直直播间片段
 */

public class VerticalRoomPager  extends BasePager<PagerLiveRoomPullBinding> {

    private static final String TAG = "VerticalRoomPager";
    private RoomExtra mRoomExtra;
    private final RoomControllerInstance mControllerInstance;//父组件Controller
    private VideoLiveControllerView mLiveControllerView;//房间交互控制器
    private SwipeAnimationController mSwipeAnimationController;//手势控制
    private LiveVideoPlayerManager mPlayerManager;//播放器实例
    private Handler mHandler;
    private LiveRoomAccountLayout mAccountLayout;

    public Handler getHandler(){
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public VerticalRoomPager(Activity context, RoomControllerInstance controllerInstance,RoomExtra roomInfo, int position) {
        super(context);
        this.mRoomExtra=roomInfo;
        this.mControllerInstance=controllerInstance;
        setContentView(R.layout.pager_live_room_pull);
    }

    /**
     * UI组件初始化
     */
    @Override
    public void initViews() {
        if(null==getContext()) return;
        //初始化直播间控制器
        RelativeLayout videoController = (RelativeLayout) findViewById(R.id.video_controller);
        mLiveControllerView = new VideoLiveControllerView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLiveControllerView.setLayoutParams(layoutParams);
        videoController.addView(mLiveControllerView);

        if (null == mRoomExtra ) return;
        mLiveControllerView.setIdentityType(VideoLiveControllerView.LIVE_SCENE_PULL);//设置交互控制器为直播间拉流场景
        //交互点击监听
        mLiveControllerView.setOnViewClickListener(new VideoLiveControllerView.OnViewClickListener() {
            @Override
            public void onClickMenu(int position) {
                super.onClickMenu(position);
                switch (position) {
                    //聊天
                    case 0:
                        if(!TextUtils.isEmpty(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID())){
                            if(null!=mControllerInstance) mControllerInstance.onInputChatText();
                        }
                        break;
                }
            }

            @Override
            public void onBack() {
                super.onBack();
                if(null!=mControllerInstance) mControllerInstance.onFinish();
            }
        });
        //异常交互监听
        mLiveControllerView.setOnExceptionListener(new OnExceptionListener() {
            //重试拉流
            @Override
            public void onTautology() {
                if(null!=mLiveControllerView){
                    mLiveControllerView.errorStateReset();
                }
                createPlayer(false);
            }
        });
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_player);
        mPlayerManager.setLooping(true);
        mPlayerManager.setStatusController(new VideoPlayerStatusController(getContext()));
        //播放事件回调
        mPlayerManager.setMediaPlayerListener(new OnMediaPlayerListener() {
            @Override
            public void onStart() {
                if(null!=mLiveControllerView) mLiveControllerView.hideLoadingView();
                //首次进入APP，自动打开礼物面板
                if(null!=mLiveControllerView&&0== SharedPreferencesUtil.getInstance().getInt(Constant.SP_ROOM_FIRST_ENTER,0)){
                    mLiveControllerView.showGiftBoardView();//显示礼物面板
                    SharedPreferencesUtil.getInstance().putInt(Constant.SP_ROOM_FIRST_ENTER,1);
                }
            }

            @Override
            public void onBufferingUpdate(int progress) {

            }

            @Override
            public void onStatus(int event) {

            }

            @Override
            public void onCompletion() {
                VideoApplication.getInstance().setIndexRefresh(true);
                if (null != mLiveControllerView){
                    mLiveControllerView.showPullError(R.drawable.ic_leave,"播放超时，轻触屏幕重试！");
                }
            }

            @Override
            public void onError(int errorCode) {
                VideoApplication.getInstance().setIndexRefresh(true);
                if (null != mLiveControllerView){
                    mLiveControllerView.showPullError(R.drawable.ic_leave,"播放超时，轻触屏幕重试！");
                }
            }
        });
        //主播头像
        LiveUtils.setLiveImage(getContext(), mRoomExtra.getAvatar(),((ImageView) findViewById(R.id.view_anchor_head)));
        //主播封面
        mPlayerManager.setVideoCover(mRoomExtra.getFrontcover(),false);
        //配置手势滑动
        mSwipeAnimationController = new SwipeAnimationController(getContext());
        mSwipeAnimationController.setAnimationView(((RelativeLayout) findViewById(R.id.video_controller)));
        findViewById(R.id.root_view_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(null!=mLiveControllerView) mLiveControllerView.clickHeart();
                }
                return mSwipeAnimationController.processEvent(event);
            }
        });
    }

    @Override
    public void initData() {
        if(null==mRoomExtra||null==bindingView) return;
        //广告组件初始化
        //if(Constant.INDEX_ITEM_TYPE_BANNERS.equals(mRoomExtra.getItemCategory()))
        if(null!=mRoomExtra.getBanners()&&mRoomExtra.getBanners().size()>0){
            bindingView.adViewLayout.setVisibility(View.VISIBLE);
            bindingView.adViewLayout.setOnAdClickListener(new PlayerAdLayout.OnAdClickListener() {
                @Override
                public void onBack(View view) {
                    if(null!=getContext()){
                        getContext().onBackPressed();
                    }
                }
            });
            bindingView.adViewLayout.init(mRoomExtra);
        }else{
            bindingView.adViewLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 更新主播信息
     */
    private void setAnchorData() {
        if(null==mLiveControllerView||null==mRoomExtra) return;
        mLiveControllerView.setHeadData(mRoomExtra.getNickname(), "0", mRoomExtra.getAvatar());
        UserInfo userInfo=new UserInfo();
        userInfo.setAvatar(mRoomExtra.getAvatar());
        userInfo.setUserid(mRoomExtra.getUserid());
        userInfo.setNickname(mRoomExtra.getNickname());
        userInfo.setRoomID(mRoomExtra.getRoom_id());
        userInfo.setFrontcover(mRoomExtra.getFrontcover());
        //绑定主播信息
        mLiveControllerView.setAnchorUserData(userInfo);
    }

    /**
     * 进入房间
     */
    private void enterRoom() {
        if(null==mRoomExtra) return;
        setAnchorData();
        LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(mRoomExtra.getRoom_id());
        //初始化房间信息
        if(null!=mLiveControllerView){
            mLiveControllerView.showControllerView();
            mLiveControllerView.initData(mRoomExtra.getRoom_id());
        }
        if(null!=mPlayerManager) mPlayerManager.startLoadingView();
        //在聊天列表中增加一条本地系统消息
        CustomMsgExtra sysMsg=new CustomMsgExtra();
        sysMsg.setCmd(Constant.MSG_CUSTOM_NOTICE);
        NoticeContent noticeMessage = UserManager.getInstance().getNoticeMessage(UserManager.NoticeType.Live);
        sysMsg.setMsgContent(noticeMessage.getContent());
        CustomMsgInfo customInfo = LiveUtils.packMessage(sysMsg, null);
        customInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());//这里用主播ID当做房间ID，避免控制器中心消息被拦截不展示
        customInfo.setMsgContentColor(noticeMessage.getColor());
        onRoomNewSystemCustomMessage(customInfo,false);
        //自己的进场消息
        CustomMsgExtra sysMsgEnter=new CustomMsgExtra();
        sysMsgEnter.setCmd(Constant.MSG_CUSTOM_ADD_USER);
        sysMsgEnter.setMsgContent("闪亮登场");
        CustomMsgInfo customInfoEnter = LiveUtils.packMessage(sysMsgEnter, null);
        customInfoEnter.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());//这里用主播ID当做房间ID，避免控制器中心消息被拦截不展示
        onRoomNewSystemCustomMessage(customInfoEnter,false);
        if(Utils.isCheckNetwork()&& NetContants.NETWORK_STATE_WIFI!= Utils.getNetworkType()&& !VideoApplication.getInstance().isNetwork()){
            QuireDialog.getInstance(getContext())
                    .setTitleText("非WIFI环境提示")
                    .setContentText(getContext().getResources().getString(R.string.text_tips_4g))
                    .setSubmitTitleText("确定")
                    .setCancelTitleText("取消")
                    .setDialogCanceledOnTouchOutside(false)
                    .setDialogCancelable(false)
                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent() {
                            VideoApplication.getInstance().setNetwork(true);
                            createPlayer(true);
                        }

                        @Override
                        public void onRefuse() {
                            if(null!=mControllerInstance) mControllerInstance.onFinish();
                        }
                    }).show();
        }else{
            createPlayer(true);
        }
    }

    /**
     * 获取房间信息
     */
    private void getRoomData(){
        if(null==mRoomExtra) return;
        if(null!=mControllerInstance) mControllerInstance.showLoadingDialog("获取房间信息中");
        LiveRoomManager.getInstance().getLiveRoom().getRoomData(mRoomExtra.getUserid(), new RoomContract.OnRoomCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=mControllerInstance) mControllerInstance.hideLoadingDialog();
                if(null!=object&&object instanceof RoomExtra){
                    RoomExtra data= (RoomExtra) object;
                    VerticalRoomPager.this.mRoomExtra=data;
                    mRoomExtra.setPull_steram(TextUtils.isEmpty(mRoomExtra.getPlay_url_rtmp())?mRoomExtra.getPlay_url_flv():mRoomExtra.getPlay_url_rtmp());
                    setAnchorData();
                    initData();
                }else{
                    getRoomDataError("请求房间信息失败");
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if(null!=mControllerInstance) mControllerInstance.hideLoadingDialog();
                getRoomDataError(errorMsg);
            }
        });
    }

    /**
     * 显示错误提示
     * @param message
     */
    private void getRoomDataError(String message) {
        QuireDialog quireDialog = QuireDialog.getInstance(getContext());
        quireDialog.setTitleText("系统提示")
                .setContentText(message)
                .setSubmitTitleText("重试")
                .setCancelTitleText("关闭")
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        getRoomData();
                    }

                    @Override
                    public void onRefuse() {
                        if(null!=mControllerInstance) mControllerInstance.onFinish();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(null!=mControllerInstance) mControllerInstance.onFinish();
            }
        });
        quireDialog.show();
    }

    /**
     * 准备开始拉流
     * @param isAddRoom 是否继续加入房间
     */
    private void createPlayer(boolean isAddRoom) {
        if(null==mRoomExtra) return;
        //拉流
        if(null!=mPlayerManager) mPlayerManager.startPlay(mRoomExtra.getPull_steram(),false);
        if(isAddRoom) addRoom();
    }

    /**
     * 加入房间
     */
    protected void addRoom() {
        if(null==mRoomExtra||TextUtils.isEmpty(mRoomExtra.getRoom_id())) return;
        LiveRoomManager.getInstance().getLiveRoom().addRoom(mRoomExtra.getRoom_id(), new LiveRoom.EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if(Constant.ROOM_CUSTOMMSG_CLOSE==errCode){
                    if(null!=getContext()) showErrorAndQuit(getContext().getResources().getString(R.string.text_live_over));
                }else{
                    String errorMessage = VideoCallManager.getInstance().getErrorMessage(errCode);
                    ToastUtils.showCenterToast(errorMessage);
                    //房间还未解散，5秒后重试
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addRoom();
                        }
                    },5000);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if(null!=mControllerInstance) mControllerInstance.newRoomInstance(VerticalRoomPager.this);
                //检查是否有直播间任务可领取
                if(null!=mLiveControllerView)mLiveControllerView.startReckonTime();
            }
        });
    }

    /**
     * 房间已经被解散
     * @param message
     */
    private void showErrorAndQuit(String message) {
        if(null==getContext()||null==mRoomExtra) return;
        if(null==mAccountLayout){
            RelativeLayout videoController = (RelativeLayout) findViewById(R.id.video_controller);
            mAccountLayout = new LiveRoomAccountLayout(getContext());
            mAccountLayout.setIdentify(LiveRoomAccountLayout.SCENE_MODE_PLAY);
            mAccountLayout.setTipsContent(message);
            mAccountLayout.setButtonContent(null!=mControllerInstance?mControllerInstance.getButtonText():"观看下一个");
            mAccountLayout.setToUserID(mRoomExtra.getUserid());
            mAccountLayout.setToUserFront(mRoomExtra.getFrontcover());
            //关闭按钮，这里处理为下一个动作
            mAccountLayout.setOnFunctionListener(new LiveRoomAccountLayout.OnFunctionListener() {
                @Override
                public void onClose() {
                    if(null!=mControllerInstance) mControllerInstance.onNextRoom();
                }
            });
            videoController.addView(mAccountLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 输入框高度发生了变化
     * @param flag
     * @param keyBordHeight
     */
    public void onRoomShowInputKeyBord(boolean flag, int keyBordHeight) {
        if(null!=mLiveControllerView) mLiveControllerView.showInputKeyBord(flag,keyBordHeight);
    }

    /**
     * 直播间新的系统消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    public void onRoomNewSystemCustomMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null!=mLiveControllerView) mLiveControllerView.newSystemCustomMessage(customMsgInfo,isSystemPro);
    }

    /**
     * 房间新的纯文本消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    public void onRoomNewTextMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null!=mLiveControllerView) mLiveControllerView.onNewTextMessage(customMsgInfo,isSystemPro);
    }

    /**
     * 房间精简消息,人数变化
     * @param groupId
     * @param sender
     * @param changedInfo
     */
    public void onRoomNewMinMessage(String groupId, String sender, NumberChangedInfo changedInfo) {
        if(null!=mLiveControllerView) mLiveControllerView.onNewMinMessage(groupId,sender,changedInfo);
    }

    /**
     * 控制器透明度设置
     * @param alpha
     */
    public void setConntrollerAlpha(float alpha) {
        if(null!=mLiveControllerView) mLiveControllerView.setConntrollerAlpha(alpha);
    }
    
    /**
     * 房间处于可见
     */
    @Override
    public void onResume() {
        super.onResume();
        if(null!=mLiveControllerView) mLiveControllerView.onResume();
        //恢复视频画面
        if(null!=mPlayerManager) mPlayerManager.onInForeground();
        //用户对主播的关注状态发生了变化
        if(VideoApplication.getInstance().getFollowState()>-1){
            if(null!=mLiveControllerView) mLiveControllerView.updataFollowState(VideoApplication.getInstance().getFollowState(),false);
            VideoApplication.getInstance().setFollowState(-1);
        }
        if(VideoApplication.getInstance().isVipSuccess()){
            VideoApplication.getInstance().setVipSuccess(false);
            if(null!=mLiveControllerView) mLiveControllerView.checkedBannerTask();
        }
    }

    /**
     * 房间处于不可见
     */
    @Override
    public void onPause() {
        super.onPause();
        if(null!=mLiveControllerView) mLiveControllerView.onPause();
        //暂停视频画面，音频继续
        if(null!=mPlayerManager){
            mPlayerManager.onInBackground(true);
            mPlayerManager.setComeBackFromShare(true);
        }
    }

    /**
     * 直播间始化，加入房间并开始拉流，在此延时两秒执行任务
     */
    @Override
    public void onStart() {
        super.onStart();
        if(null==mRoomExtra) return;
        if(null!=mRoomExtra.getBanners()&&mRoomExtra.getBanners().size()>0){
            return;
        }
        if(!TextUtils.isEmpty(mRoomExtra.getRoom_id())&&!TextUtils.isEmpty(mRoomExtra.getPull_steram())){
            if(null!=mPlayerManager) mPlayerManager.startLoadingView();
            getHandler().postAtTime(new startLiveRunnable(), SystemClock.uptimeMillis()+1500);//设置延缓任务
        }else{
            getRoomData();
        }
    }

    /**
     * 直播间销毁，退出房间并结束拉流
     */
    @Override
    public void onStop() {
        super.onStop();
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mControllerInstance) mControllerInstance.newRoomInstance(null);
        LiveRoomManager.getInstance().getLiveRoom().exitRoom(null);
        LiveRoomManager.getInstance().getLiveRoom().stopLive(false,false);
        LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(null);
        if(null!=mPlayerManager) mPlayerManager.onStop();
        if(null!=mLiveControllerView) mLiveControllerView.onReset();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        if(null!=mLiveControllerView) {
            mLiveControllerView.stopReckonTime();
            mLiveControllerView.onDestroy();
            mLiveControllerView=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveRoomManager.getInstance().getLiveRoom().stopLive(false,true);
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mLiveControllerView) {
            mLiveControllerView.stopReckonTime();
            mLiveControllerView.onDestroy();
            mLiveControllerView=null;
        }
        if(null!=mSwipeAnimationController) mSwipeAnimationController.onDestroy();
        mRoomExtra=null;mHandler=null;
    }
    /**
     * 这个Runnable用来执行延缓任务，waitPlayPoistin是记录要执行的延缓任务，只有当前显示的viewPager cureenItem与当时提交的cureenItem相等才允许播放
     * 防止用户手速过快
     */
    private class startLiveRunnable implements Runnable{
        @Override
        public void run() {
            enterRoom();
        }
    }
}