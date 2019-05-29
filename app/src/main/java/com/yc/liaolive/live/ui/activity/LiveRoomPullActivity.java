package com.yc.liaolive.live.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseLiveActivity;
import com.yc.liaolive.bean.NoticeContent;
import com.yc.liaolive.bean.NoticeInfo;
import com.yc.liaolive.bean.ShareInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.interfaces.OnMediaPlayerListener;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.PushMessage;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.listener.OnExceptionListener;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.room.BaseRoom;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.live.ui.dialog.LiveDetailsDialog;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.view.SwipeAnimationController;
import com.yc.liaolive.live.view.VideoLiveControllerView;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/5/16
 * 直播间-拉流
 */

public class LiveRoomPullActivity extends BaseLiveActivity<VideoLiveControllerView> implements  RoomContract.View, Observer{

    private static final String TAG = LiveRoomPullActivity.class.getSimpleName();
    private  static  LiveRoomPullActivity mInstance;
    private RoomExtra mRoomExtra;
    private LiveDetailsDialog mNewInstance;//结束、错误提示框
    private LiveVideoPlayerManager mPlayerManager;
    public static LiveRoomPullActivity getInstance(){
        return mInstance;
    }
    private SwipeAnimationController mSwipeAnimationController;//手势动画
    private Handler mHandler;

    /**
     * 拉流入口
     * @param context
     * @param roomExtra
     */
    public static void start(Context context, RoomExtra roomExtra){
        Intent intent=new Intent(context, LiveRoomPullActivity.class);
        intent.putExtra(Constant.APP_START_EXTRA_ROOM,roomExtra);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 告诉父类继承人身份
     * @return
     */
    @Override
    protected int getExtendsClassIdentify() {
        return BaseRoom.USER_IDENTITY_PULL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen(true);//全屏
        super.onCreate(savedInstanceState);
        mInstance = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        getRoomExtra(getIntent());
        if(null==mRoomExtra||TextUtils.isEmpty(mRoomExtra.getUserid())){
            finish();
            ToastUtils.showCenterToast("进入直播间错误");
            return;
        }
        setContentView(R.layout.activity_live_pull);
        LiveRoomManager.getInstance().getLiveRoom().setUserIdentity(LiveRoom.USER_IDENTITY_PULL);//绑定观众身份到Room
        LiveRoomManager.getInstance().getLiveRoom().onCreate();
        LiveRoomManager.getInstance().getLiveRoom().setLiveRoomListener(this);//注册房间事件监听
        ApplicationManager.getInstance().addObserver(this);
        initRoomViews();
        initData();
    }

    /**
     * 取参
     * @param intent
     */
    private void getRoomExtra(Intent intent) {
        if(null!=intent){
            mRoomExtra = (RoomExtra) intent.getSerializableExtra(Constant.APP_START_EXTRA_ROOM);
        }
    }

    @Override
    protected void initRoomViews() {
        //子类自有控制器实现
        mController = new VideoLiveControllerView(LiveRoomPullActivity.this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mController.setLayoutParams(layoutParams);
        super.initRoomViews();
        if (null == mRoomExtra || null == mController) return;
        mController.setIdentityType(VideoLiveControllerView.LIVE_SCENE_PULL);//设置交互控制器为直播间拉流场景
        //交互点击监听
        mController.setOnViewClickListener(new VideoLiveControllerView.OnViewClickListener() {
            @Override
            public void onClickMenu(int position) {
                super.onClickMenu(position);
                switch (position) {
                    //聊天
                    case 0:
                        if(!TextUtils.isEmpty(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID())){
                            showInputMsgDialog();
                        }
                        break;
                }
            }

            @Override
            public void onBack() {
                super.onBack();
                finish();
            }
        });
        //异常交互监听
        mController.setOnExceptionListener(new OnExceptionListener() {
            //重试拉流
            @Override
            public void onTautology() {
                if(null!=mController){
                    mController.errorStateReset();
                }
                createPlayer(false);
            }
        });
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_player);
        mPlayerManager.setLooping(true);
        //播放事件回调
        mPlayerManager.setMediaPlayerListener(new OnMediaPlayerListener() {
            @Override
            public void onStart() {
                if(null!=mController) mController.hideLoadingView();
                //首次进入APP，自动打开礼物面板
                if(null!=mController&&0== SharedPreferencesUtil.getInstance().getInt(Constant.SP_ROOM_FIRST_ENTER,0)){
                    mController.showGiftBoardView();//显示礼物面板
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
                if (null != mController){
                    mController.showPullError(R.drawable.ic_leave,"播放超时，轻触屏幕重试！");
                }
            }

            @Override
            public void onError(int errorCode) {
                VideoApplication.getInstance().setIndexRefresh(true);
                if (null != mController){
                    mController.showPullError(R.drawable.ic_leave,"播放超时，轻触屏幕重试！");
                }
            }
        });
        LiveUtils.setLiveImage(LiveRoomPullActivity.this, mRoomExtra.getAvatar(),((ImageView) findViewById(R.id.view_anchor_head)));
        //配置手势滑动
        mSwipeAnimationController = new SwipeAnimationController(this);
        mSwipeAnimationController.setAnimationView(((RelativeLayout) findViewById(R.id.video_controller)));
        findViewById(R.id.root_view_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(null!=mController) mController.clickHeart();
                }
                return mSwipeAnimationController.processEvent(event);
            }
        });
    }

    /**
     * 更新主播信息
     */
    private void setAnchorData() {
        if(null==mController||null==mRoomExtra) return;
        mController.setHeadData(mRoomExtra.getNickname(), "0", mRoomExtra.getAvatar());
        UserInfo userInfo=new UserInfo();
        userInfo.setAvatar(mRoomExtra.getAvatar());
        userInfo.setUserid(mRoomExtra.getUserid());
        userInfo.setNickname(mRoomExtra.getNickname());
        userInfo.setRoomID(mRoomExtra.getRoom_id());
        userInfo.setFrontcover(mRoomExtra.getFrontcover());
        //绑定主播信息
        mController.setAnchorUserData(userInfo);
        if(null!=mPlayerManager) mPlayerManager.setVideoCover(mRoomExtra.getFrontcover(),false);
    }

    /**
     * 直播间初始化
     */
    private void initData() {
        if(null==mRoomExtra) return;
        if(!TextUtils.isEmpty(mRoomExtra.getRoom_id())&&!TextUtils.isEmpty(mRoomExtra.getPull_steram())){
            enterRoom();
        }else{
            getRoomData();
        }
    }

    /**
     * 进入房间
     */
    private void enterRoom() {
        setAnchorData();
        LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(mRoomExtra.getRoom_id());
        //初始化房间信息
        if(null!=mController){
            mController.showControllerView();
            mController.initData(mRoomExtra.getRoom_id());
        }
        //在聊天列表中增加一条本地系统消息
        CustomMsgExtra sysMsg=new CustomMsgExtra();
        sysMsg.setCmd(Constant.MSG_CUSTOM_NOTICE);
        NoticeContent noticeMessage = UserManager.getInstance().getNoticeMessage(UserManager.NoticeType.Live);
        sysMsg.setMsgContent(noticeMessage.getContent());
        CustomMsgInfo customInfo = LiveUtils.packMessage(sysMsg, null);
        customInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());//这里用主播ID当做房间ID，避免控制器中心消息被拦截不展示
        customInfo.setMsgContentColor(noticeMessage.getColor());
        newSystemCustomMessage(customInfo,false);
        //自己的进场消息
        CustomMsgExtra sysMsgEnter=new CustomMsgExtra();
        sysMsgEnter.setCmd(Constant.MSG_CUSTOM_ADD_USER);
        sysMsgEnter.setMsgContent("闪亮登场");
        CustomMsgInfo customInfoEnter = LiveUtils.packMessage(sysMsgEnter, null);
        customInfoEnter.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());//这里用主播ID当做房间ID，避免控制器中心消息被拦截不展示
        newSystemCustomMessage(customInfoEnter,false);
        if(Utils.isCheckNetwork()&&NetContants.NETWORK_STATE_WIFI!= Utils.getNetworkType()&& !VideoApplication.getInstance().isNetwork()){
            QuireDialog.getInstance(LiveRoomPullActivity.this)
                    .setTitleText("非WIFI环境提示")
                    .setContentText(getResources().getString(R.string.text_tips_4g))
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
                            finish();
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
        showProgressDialog("获取房间信息中",true);
        LiveRoomManager.getInstance().getLiveRoom().getRoomData(mRoomExtra.getUserid(), new RoomContract.OnRoomCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                if(isDestroy) return;
                if(null!=object&&object instanceof RoomExtra){
                    RoomExtra data= (RoomExtra) object;
                    LiveRoomPullActivity.this.mRoomExtra=data;
                    mRoomExtra.setPull_steram(TextUtils.isEmpty(mRoomExtra.getPlay_url_rtmp())?mRoomExtra.getPlay_url_flv():mRoomExtra.getPlay_url_rtmp());
                    setAnchorData();
                    initData();
                }else{
                    getRoomDataError("请求房间信息失败");
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                if(isDestroy) return;
                getRoomDataError(errorMsg);
            }
        });
    }

    /**
     * 显示错误提示
     * @param message
     */
    private void getRoomDataError(String message) {
        QuireDialog quireDialog = QuireDialog.getInstance(LiveRoomPullActivity.this);
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
                        finish();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
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
                    if(!LiveRoomPullActivity.this.isFinishing()){
                        showErrorAndQuit(getResources().getString(R.string.text_live_over));
                    }
                }else{
                    String errorMessage = VideoCallManager.getInstance().getErrorMessage(errCode);
                    ToastUtils.showCenterToast(errorMessage);
                    if(!LiveRoomPullActivity.this.isFinishing()){
                        //房间还未解散，5秒后重试
                        if(null==mHandler) mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addRoom();
                            }
                        },5000);
                    }
                }
            }

            @Override
            public void onSuccess(Object data) {
                //检查是否有直播间任务可领取
                if(!LiveRoomPullActivity.this.isFinishing()){
                    if(null!=mController) mController.startReckonTime();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mController) mController.onResume();
        //恢复视频画面
        if(null!=mPlayerManager) mPlayerManager.onInForeground();
        //用户对主播的关注状态发生了变化
        if(VideoApplication.getInstance().getFollowState()>-1){
            if(null!=mController) mController.updataFollowState(VideoApplication.getInstance().getFollowState(),false);
            VideoApplication.getInstance().setFollowState(-1);
        }
        if(VideoApplication.getInstance().isVipSuccess()){
            VideoApplication.getInstance().setVipSuccess(false);
            if(null!=mController) mController.checkedBannerTask();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=mController) mController.onPause();
        //暂停视频画面，音频继续
        if(null!=mPlayerManager){
            mPlayerManager.onInBackground(true);
            mPlayerManager.setComeBackFromShare(true);
        }
    }

    @Override
    public void finish() {
        stopPlay(false);
        super.finish();
    }

    /**
     * 观众端调用 结束播放
     * @param isExit 是否关闭界面
     */
    public void stopPlay(boolean isExit) {
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        backRoom(isExit);
    }

    /**
     * 分享
     */
    public void onShare() {
        if(null==mRoomExtra) return;
        ShareInfo shareInfo=new ShareInfo();
        shareInfo.setTitle("直播邀请");
        shareInfo.setRoomid(mRoomExtra.getRoom_id());
        shareInfo.setDesp("我正在["+mRoomExtra.getNickname()+"]的直播间观看直播");
        shareInfo.setUserID(mRoomExtra.getUserid());
        shareInfo.setImageLogo(mRoomExtra.getAvatar());
        shareInfo.setReport(true);
        shareInfo.setUrl("http://cl.dapai52.com/share/share.html");
        shareInfo.setShareTitle("分享直播到");
        share(shareInfo);
    }

    /**
     * 发送一条关注主播的大厅消息
     * @param customMsgInfo
     */
    public void sendFollowMsg(CustomMsgInfo customMsgInfo){
        sendCustomMessage(customMsgInfo);
    }

    /**
     * 主播端发送过来的消息
     * @param pushMessage 消息体
     */
    @Override
    public void onRoomPushMessage(PushMessage pushMessage) {
        if(null!=pushMessage){
            //主播APP可见状态发生变化
            if(null!=mController) mController.switchAppState(pushMessage.getForegroundState());
        }
    }

    /**
     * 群组已被销毁
     * @param roomID 房间ID
     */
    @Override
    public void onRoomClosed(String roomID) {
        showErrorAndQuit("主播已退出房间");
    }

    /**
     * 弹出错误提示框
     * @param errorMsg
     */
    protected void showErrorAndQuit(String errorMsg) {
        VideoApplication.getInstance().setIndexRefresh(true);
        stopPlay(false);
        if(!LiveRoomPullActivity.this.isFinishing()&&null!=mRoomExtra&&null==mNewInstance){
            try {
                mNewInstance = LiveDetailsDialog.newInstance(
                        LiveRoomPullActivity.this, mRoomExtra.getUserid(),
                        LiveDetailsDialog.SCENE_MODE_PLAY, null, errorMsg);
                mNewInstance.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mNewInstance=null;
                        finish();
                    }
                });
                mNewInstance.show();
            }catch (Exception e){
                finish();
            }
            return;
        }
        ToastUtils.showCenterToast(errorMsg);
    }

    @Override
    public void onDebugLog(String log) {}

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInstance =null;
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mController) mController.onDestroy();
        if(null!=mNewInstance) mNewInstance.dismiss();

        if(null!=mSwipeAnimationController) mSwipeAnimationController.onDestroy();
        MobclickAgent.onEvent(LiveRoomPullActivity.this, "playing_out");
        ApplicationManager.getInstance().removeObserver(this);
        mRoomExtra=null;mHandler=null;mNewInstance=null;mController=null;
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}

    /**
     * 观察者注册
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
//        if(null!=arg && arg instanceof String ){
//            //用户网路发生了变化
//            if(TextUtils.equals(Constant.OBSERVER_CMD_NET_WORK_CHANGED, (String) arg)){
//                if(!ForegroundManager.getInstance().isForeground()&&NetContants.NETWORK_STATE_3G==Utils.getNetworkType()){
//                    if(null!=mPlayerManager) mPlayerManager.onPause();//暂停拉流
//                }else if(!ForegroundManager.getInstance().isForeground()&&NetContants.NETWORK_STATE_WIFI==Utils.getNetworkType()){
//                    if(null!=mPlayerManager) mPlayerManager.onResume();//暂停拉流
//                }
//            }
//            return;
//        }
        if(null!=arg && arg instanceof NoticeInfo){
            NoticeInfo data= (NoticeInfo) arg;
            //收到服务端推送消息：账号已被封禁
            if(Constant.NOTICE_CMD_ACCOUNT_CLOSE.equals(data.getCmd())){
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopPlay(true);
                        }
                    });
                }catch (RuntimeException e){
                    finish();
                }
                return;
            }
            //收到服务端推送消息：有新的任务可领取
            if(Constant.NOTICE_CMD_ROOM_TASK_FINLISH.equals(data.getCmd())){
                if(!LiveRoomPullActivity.this.isFinishing()&&null!=mController){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mController.checkedTaskAward();
                        }
                    });
                }
                return;
            }
        }
    }
}