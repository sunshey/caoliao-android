package com.yc.liaolive.live.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseLiveActivity;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.interfaces.OnMediaPlayerListener;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.PushMessage;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.listener.OnExceptionListener;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.room.BaseRoom;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.live.ui.dialog.EarphoneTipsDialog;
import com.yc.liaolive.live.ui.dialog.LiveDetailsDialog;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.view.AsmrRoomControllerView;
import com.yc.liaolive.live.view.VideoLiveControllerView;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.contract.RoomContract;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.ui.activity.CallRechargeActivity;

/**
 * TinyHung@Outlook.com
 * 2019/2/2
 * ASMR 直播间-拉流
 */

public class AsmrRoomPullActivity extends BaseLiveActivity<AsmrRoomControllerView> implements  RoomContract.View{

    private static final String TAG = AsmrRoomPullActivity.class.getSimpleName();
    private  static AsmrRoomPullActivity mInstance;
    private RoomExtra mRoomExtra;
    private LiveDetailsDialog mNewInstance;//结束、错误提示框
    private LiveVideoPlayerManager mPlayerManager;
    private int mIsBuy=0;

    public static AsmrRoomPullActivity getInstance(){
        return mInstance;
    }
    private Handler mHandler;

    /**
     * 拉流入口
     * @param context
     * @param roomExtra
     */
    public static void start(Context context, RoomExtra roomExtra){
        Intent intent=new Intent(context, AsmrRoomPullActivity.class);
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
        initRoomViews();
        if(!TextUtils.isEmpty(mRoomExtra.getFrontcover())&&null!=mPlayerManager){
            mPlayerManager.setVideoCover(mRoomExtra.getFrontcover(),true);
        }
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
        mController = new AsmrRoomControllerView(AsmrRoomPullActivity.this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mController.setLayoutParams(layoutParams);
        super.initRoomViews();
        if (null == mRoomExtra || null == mController) return;
        mController.setIdentityType(VideoLiveControllerView.LIVE_SCENE_PULL);//设置交互控制器为直播间拉流场景
        mController.showLoadingView(null);
        //交互点击监听
        mController.setOnViewClickListener(new AsmrRoomControllerView.OnViewClickListener() {
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
//                //首次进入APP，自动打开礼物面板
//                if(null!=mController&&0== SharedPreferencesUtil.getInstance().getInt(Constant.SP_ROOM_FIRST_ENTER,0)){
//                    mController.showGiftBoardView();//显示礼物面板
//                    SharedPreferencesUtil.getInstance().putInt(Constant.SP_ROOM_FIRST_ENTER,1);
//                }
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
        LiveUtils.setLiveImage(AsmrRoomPullActivity.this, mRoomExtra.getAvatar(),((ImageView) findViewById(R.id.view_anchor_head)));
    }


    /**
     * 更新主播信息
     */
    private void setAnchorData() {
        if(null==mController||null==mRoomExtra) return;
        UserInfo userInfo=new UserInfo();
        userInfo.setAvatar(mRoomExtra.getAvatar());
        userInfo.setUserid(mRoomExtra.getUserid());
        userInfo.setNickname(mRoomExtra.getNickname());
        userInfo.setRoomID(mRoomExtra.getRoom_id());
        userInfo.setFrontcover(mRoomExtra.getFrontcover());
        //绑定主播信息
        mController.setAnchorUserData(userInfo);
    }

    /**
     * 直播间初始化
     */
    private void initData() {
        if(null==mRoomExtra) return;
        if(!TextUtils.isEmpty(mRoomExtra.getPlay_url_rtmp())||!TextUtils.isEmpty(mRoomExtra.getPlay_url_flv())) return;
        showProgressDialog("获取房间信息中",true);
        LiveRoomManager.getInstance().getLiveRoom().getQueryRoomData(mRoomExtra.getUserid(), mIsBuy,new RoomContract.OnRoomCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                closeProgressDialog();
                VideoApplication.getInstance().setIndexRefresh(true);
                if(isDestroy) return;
                if(!AsmrRoomPullActivity.this.isFinishing()){
                    if(null!=object&&object instanceof RoomExtra){
                        RoomExtra data= (RoomExtra) object;
                        AsmrRoomPullActivity.this.mRoomExtra=data;
                        mRoomExtra.setPull_steram(TextUtils.isEmpty(mRoomExtra.getPlay_url_rtmp())?mRoomExtra.getPlay_url_flv():mRoomExtra.getPlay_url_rtmp());
//                        setAnchorData();
                        enterRoom();
                    }else{
                        getRoomDataError("请求房间信息失败");
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                closeProgressDialog();
                if(isDestroy) return;
                if(!AsmrRoomPullActivity.this.isFinishing()){
                    if(NetContants.API_RESULT_BUY==code){
                        onBuyTips(errorMsg);
                        return;
                    }
                    if(NetContants.API_RESULT_ARREARAGE_CODE==code){
                        onRechgre(errorMsg);
                        return;
                    }
                    getRoomDataError(errorMsg);
                }
            }
        });
    }

    /**
     * 购买钻石
     * @param message
     */
    private void onRechgre(String message) {
        if(null==getContext()) return;
        QuireDialog.getInstance(((Activity) getContext()))
                .setTitleText("钻石不足")
                .setContentTextColor(getContext().getResources().getColor(R.color.tab_text_unselector_color))
                .showCloseBtn(true)
                .setContentText(message)
//                .setContentText("<font color='#FF6666'>观看需打赏15000钻石<br/></font><font color='#666666'>开通VIP海量ASMR资源</font><font color='#FF0000'>免费看</font>")
                .setSubmitTitleText("开通会员")
                .setSubmitTitleTextColor(getContext().getResources().getColor(R.color.app_style))
                .setCancelTitleText("充值钻石")
                .setCancelTitleTextColor(getContext().getResources().getColor(R.color.common_h33))
                .setDialogCancelable(false)
                .setDialogCanceledOnTouchOutside(false)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        VipActivity.startForResult(((Activity) getContext()),1, "amsr_video");
                        MobclickAgent.onEvent(getContext(), "amsr_recharge_tips_vip_click");
                    }

                    @Override
                    public void onRefuse() {
                        CallRechargeActivity.start(AsmrRoomPullActivity.this, 20, null);
                        MobclickAgent.onEvent(getContext(), "amsr_recharge_tips_diamonds_click");
                    }

                    @Override
                    public void onCloseDialog() {
                        MobclickAgent.onEvent(getContext(), "amsr_recharge_tips_close_click");
                        finish();
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
                .setContentTextColor(getContext().getResources().getColor(R.color.tab_text_unselector_color))
                .setContentText(msg)
                .setSubmitTitleText("确定")
                .setSubmitTitleTextColor(getContext().getResources().getColor(R.color.app_style))
                .setCancelTitleText("取消")
                .setCancelTitleTextColor(getContext().getResources().getColor(R.color.common_h33))
                .setDialogCancelable(false)
                .setDialogCanceledOnTouchOutside(false)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        mIsBuy ++;
                        initData();
                        MobclickAgent.onEvent(getContext(), "amsr_pay_tips_ok_click");
                    }

                    @Override
                    public void onRefuse() {
                        MobclickAgent.onEvent(getContext(), "amsr_pay_tips_cancel_click");
                        finish();
                    }
                }).show();
    }


    /**
     * 进入房间
     */
    private void enterRoom() {
        setAnchorData();
        LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(mRoomExtra.getRoom_id());

        if (!TextUtils.isEmpty(mRoomExtra.getHeadset_img())) {
            EarphoneTipsDialog dialog = EarphoneTipsDialog.newInstance(
                    AsmrRoomPullActivity.this, mRoomExtra.getHeadset_img());
            dialog.show();
        }
        //显示耳机提示的同时开始播放
        //初始化房间信息
        if(null!=mController){
            mController.showControllerView();
            mController.initData(mRoomExtra.getRoom_id());
        }
        if(Utils.isCheckNetwork()&&NetContants.NETWORK_STATE_WIFI!= Utils.getNetworkType()&& !VideoApplication.getInstance().isNetwork()){
            QuireDialog.getInstance(AsmrRoomPullActivity.this)
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

    }

    /**
     * 显示错误提示
     * @param message
     */
    private void getRoomDataError(String message) {
        QuireDialog quireDialog = QuireDialog.getInstance(AsmrRoomPullActivity.this);
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
                    if(!AsmrRoomPullActivity.this.isFinishing()){
                        showErrorAndQuit(getResources().getString(R.string.text_live_over));
                    }
                }else{
                    String errorMessage = VideoCallManager.getInstance().getErrorMessage(errCode);
                    ToastUtils.showCenterToast(errorMessage);
                    if(!AsmrRoomPullActivity.this.isFinishing()){
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
                if(!AsmrRoomPullActivity.this.isFinishing()){
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
        initData();
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
        if(!AsmrRoomPullActivity.this.isFinishing()&&null!=mRoomExtra&&null==mNewInstance){
            try {
                mNewInstance = LiveDetailsDialog.newInstance(
                        AsmrRoomPullActivity.this, mRoomExtra.getUserid(),
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
        mIsBuy=0;
        mInstance =null;
        if(null!=mPlayerManager){
            mPlayerManager.onDestroy();
            mPlayerManager=null;
        }
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mController) mController.onDestroy();
        if(null!=mNewInstance) mNewInstance.dismiss();

        MobclickAgent.onEvent(AsmrRoomPullActivity.this, "playing_out");
        mRoomExtra=null;mHandler=null;mNewInstance=null;mController=null;
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}
}