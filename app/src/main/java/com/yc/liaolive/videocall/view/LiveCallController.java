package com.yc.liaolive.videocall.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.TrafficStats;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.AttentInfo;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.gift.manager.RoomAwardGroupManager;
import com.yc.liaolive.gift.manager.RoomGiftGroupManager;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.gift.view.AnimatorSvgaPlayerManager;
import com.yc.liaolive.gift.view.CountdownGiftView;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.view.BrightConversationListView;
import com.yc.liaolive.live.view.FrequeControl;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.bean.CallResultInfo;
import com.yc.liaolive.videocall.listsner.OnCustomMessageListener;
import com.yc.liaolive.videocall.listsner.OnVideoCallBackListener;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.ui.activity.CallRechargeActivity;
import com.yc.liaolive.view.widget.MarqueeTextView;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2018/8/20
 * 视频通话控制器
 * 区分四种身份 视频通话由发起人付费而并不是呼叫人
 * 此组件根据呼叫状态绑定了发起人、接收人，呼叫人，接听人 的身份
 */

public class LiveCallController extends FrameLayout implements Observer {

    private static final String TAG = "LiveCallController";
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;
    private TextView mUnreckonTime;
    private TextView mReckonTime;
    public static final long HEART_POST_DURTION = 10;//心跳间隔上报时间 单位秒
    private long mReckonDurtion=0;//正计时 单位秒
    private String chatPaidFee="0";//累计消耗、获得的积分
    private long mCountdownDurtion=60;//发起人剩余可通话时长 单位秒
    private boolean timerIsRuning;//计时器是否正在运行
    private long  second=0;//分钟轮回
    private long  heartSecond;//决定心跳的秒数
    private String mGroupID;//视频通话所在的群组ID
    private String toUserAvater;//对方用户头像
    private String toUserNickname;//对方用户昵称
    private String toUserUserID;//对方用户ID
    //礼物模块
    private RoomGiftGroupManager mGiftGroupManager;//普通礼物管理模块
    private RoomAwardGroupManager mAwardGroupManager;
    private AnimatorSvgaPlayerManager mSvgaPlayerManager;//豪华礼物
    private CountdownGiftView mCountdownGiftView;//连击赠送礼物
    private QuireDialog mRechargeTipsDialog;//充值金币弹窗
    //用户身份信息
    private CallExtraInfo mCallUserInfo;
    //直播间通话质量，https://github.com/zhaoyang21cn/iLiveSDK_Android_LiveDemo/blob/master/doc/ILiveSDK/quality.m
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    private ImageView mTvNetIcon;
    private TextView mTvNetContent;
    private TextView mTvNetTips;
    private int mCallID;//视频通话ID
    private BrightConversationListView mConversationListView;//会话列表
    private Integer mIsFollow;//对主播的关注状态
    private FrequeControl mFrequeControl;//规定时间内间隔多久触发一次
    private int repostCount=0;//重试查询、付费次数

    public LiveCallController(@NonNull Context context) {
        this(context,null);
    }

    public LiveCallController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //监听本地余额变化
        ApplicationManager.getInstance().addObserver(this);
        View.inflate(context, R.layout.view_call_controller_layout,this);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //美颜
                    case R.id.view_btn_beauty:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onBeauty();
                        break;
                    //切换前后摄像头
                    case R.id.view_btn_switch_camera:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onSwitchCamera();
                        break;
                    //礼物赠送
                    case R.id.view_btn_gift:
                        PusherInfo pusherInfo=new PusherInfo();
                        pusherInfo.setUserName(getToUserNickname());
                        pusherInfo.setUserID(getToUserUserID());
                        pusherInfo.setUserAvatar(getToUserAvater());
                        showGiftBoardView(pusherInfo);
                        break;
                    //关闭、打开摄像头
                    case R.id.view_btn_camera:
                        if(1== getUserIndetity()){
                            if(null!=mOnFunctionListener) mOnFunctionListener.onChangeCamera(((ImageView) v));
                        }else{
                            ToastUtils.showCenterToast("主播不允许关闭照相机");
                        }
                        break;
                    //关闭、打开语音
                    case R.id.view_btn_mic:
                        if(1== getUserIndetity()){
                            if(null!=mOnFunctionListener) mOnFunctionListener.onChangeMic(((ImageView) v));
                        }else{
                            ToastUtils.showCenterToast("主播不允许关闭麦克风");
                        }
                        break;
                    //充值
                    case R.id.view_btn_recharge:
                        if(null!=getContext()&&getContext() instanceof Activity) CallRechargeActivity.start(((Activity) getContext()),18,null);
                        break;
                }
            }
        };
        findViewById(R.id.view_btn_camera).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_switch_camera).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_mic).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_beauty).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_recharge).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_gift).setOnClickListener(onClickListener);
        //结束通话
        findViewById(R.id.view_btn_exit).setOnClickListener(new PerfectClickListener(1000) {
            @Override
            protected void onNoDoubleClick(View v) {
                if(null!=mOnFunctionListener) mOnFunctionListener.onEndCall(true,getIdType(),"确定要结束视频通话吗？");
            }
        });

        //倒计时
        mUnreckonTime = (TextView) findViewById(R.id.view_unreckon_time);
        //正计时
        mReckonTime = (TextView) findViewById(R.id.view_reckon_time);
        //普通礼物播放管理者构造
        mGiftGroupManager = (RoomGiftGroupManager) findViewById(R.id.room_gift_manager);
        mGiftGroupManager.setOnFunctionListener(new com.yc.liaolive.gift.listener.OnFunctionListener() {
            @Override
            public void onSendGift(FansInfo userInfo) {
                //接收对象
                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
            }
        });

        //中奖信息展示
        mAwardGroupManager = (RoomAwardGroupManager) findViewById(R.id.room_award_manager);
        mAwardGroupManager.setIdentityType(0);
        mAwardGroupManager.setOnFunctionListener(new com.yc.liaolive.gift.listener.OnFunctionListener() {
            @Override
            public void onSendGift(FansInfo userInfo) {
                //接收对象
                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
            }
        });
        //连击赠送礼物
        mCountdownGiftView = (CountdownGiftView) findViewById(R.id.view_countdown_view);
        mCountdownGiftView.setApiMode(LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM);
        mCountdownGiftView.setOnGiftSendListener(new CountdownGiftView.OnCountdownGiftSendListener() {
            @Override
            public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                //本地播放礼物动画，远程端交由服务器推送
                CustomMsgExtra customMsgExtra=new CustomMsgExtra();
                customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
                if(null!=accepUserInfo){
                    customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
                    customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
                    customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
                }
                data.setCount(count);
                data.setSource_room_id(mGroupID);
                CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
                customMsgInfo.setAccapGroupID(mGroupID);
                sendCustomGiftMessage(customMsgInfo);
            }
        });
        //奢侈礼物动画
        mSvgaPlayerManager = (AnimatorSvgaPlayerManager) findViewById(R.id.svga_animator);
        //网络状态监听
        mTvNetIcon = (ImageView) findViewById(R.id.view_tv_net_icon);
        mTvNetContent = (TextView) findViewById(R.id.view_tv_net_content);
        mTvNetTips = (TextView) findViewById(R.id.view_tv_net_tips);
        mConversationListView = (BrightConversationListView) findViewById(R.id.view_bright_conversation);
        mConversationListView.initConversation();
        mConversationListView.setUserCenterEnable(false);
        mFrequeControl = new FrequeControl();
        mFrequeControl.init(1, 5);//5秒内触发一次
    }

    /**
     * 设置用户身份信息
     * @param callExtraInfo
     */
    public void setCallUserInfo(CallExtraInfo callExtraInfo) {
        this.mCallUserInfo=callExtraInfo;
        if(null==mCallUserInfo) return;
        //绑定对方用户信息
        setToUserUserID(mCallUserInfo.getToUserID());
        setToUserAvater(mCallUserInfo.getToAvatar());
        setToUserNickname(mCallUserInfo.getToNickName());
    }

    /**
     * 初始化组件使用场景，区分是主播端还是用户端
     * @param callExtraInfo  对方的信息，如果是预约回拨，则用户昵称和头像是主播端设定的anchorFront和anchorNickName字段
     */
    public void initView(final CallExtraInfo callExtraInfo){

        findViewById(R.id.view_durtion_bg).setBackgroundResource(TextUtils.equals(mCallUserInfo.getCallAnchorID(),UserManager.getInstance().getUserId())?R.drawable.shape_mackcall_hear_bg:0);
        //主播端显示倒计时控件
        findViewById(R.id.view_unreckon_time).setVisibility(TextUtils.equals(mCallUserInfo.getCallAnchorID(),UserManager.getInstance().getUserId())?VISIBLE:GONE);
        //用户端显示正计时控件
        findViewById(R.id.view_reckon_time).setVisibility(TextUtils.equals(mCallUserInfo.getCallAnchorID(),UserManager.getInstance().getUserId())?GONE:VISIBLE);
        //用户端显示主播信息及关注按钮
        if(null!=callExtraInfo&&TextUtils.equals(mCallUserInfo.getCallUserID(),UserManager.getInstance().getUserId())){
            findViewById(R.id.rl_anchorInfo).setVisibility(VISIBLE);
            ImageView anchorHead = (ImageView) findViewById(R.id.view_anchor_head);
            Glide.with(getContext()).load(callExtraInfo.getToAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getContext()))
                    .into(anchorHead);
            ((MarqueeTextView) findViewById(R.id.view_anchor_name)).setText(callExtraInfo.getToNickName());
            //主播头像事件
//            anchorHead.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LiveUserDetailsFragment.newInstance(callExtraInfo.getCallAnchorID(),
//                            0,
//                            LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID())
//                            .setOnFunctionClickListener(new LiveUserDetailsFragment.OnFunctionClickListener() {
//                                @Override
//                                public void onSendGift(FansInfo userInfo) {
//                                    PusherInfo pusherInfo=new PusherInfo();
//                                    pusherInfo.setUserName(userInfo.getNickname());
//                                    pusherInfo.setUserID(userInfo.getUserid());
//                                    pusherInfo.setUserAvatar(userInfo.getAvatar());
//                                    showGiftBoardView(pusherInfo);
//                                }
//
//                                @Override
//                                public void onFollowChanged(int status) {
//                                    super.onFollowChanged(status);
//                                    //是否已经关注此用户
//                                    mIsFollow=status;
//                                    ((ImageView) findViewById(R.id.view_add_follow)).setImageResource(1==mIsFollow?0:R.drawable.view_btn_follow);
//                                }
//                            }).show(((FragmentActivity) getContext()).getSupportFragmentManager(),"userinfo");
//                }
//            });
            //检查关注状态
            UserManager.getInstance().followUser(callExtraInfo.getCallAnchorID(), 2, new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if(null!=object&& object instanceof AttentInfo){
                        AttentInfo attentInfo= (AttentInfo) object;
                        mIsFollow=attentInfo.getIs_attent();
                        ((ImageView) findViewById(R.id.view_add_follow)).setImageResource(1==mIsFollow?0:R.drawable.view_btn_follow);
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                    ToastUtils.showCenterToast(errorMsg);
                }
            });
            //关注意图处理
            findViewById(R.id.view_add_follow).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserManager.getInstance().followUser(callExtraInfo.getCallAnchorID(), mIsFollow==0?1:0, new UserServerContract.OnNetCallBackListener() {
                        @Override
                        public void onSuccess(Object object) {
                            mIsFollow=(mIsFollow==0?1:0);
                            ToastUtils.showCenterToast("关注成功");
                            ((ImageView) findViewById(R.id.view_add_follow)).setImageResource(1==mIsFollow?0:R.drawable.view_btn_follow);
                            CustomMsgExtra customMsgExtra=new CustomMsgExtra();
                            customMsgExtra.setCmd(Constant.MSG_CUSTOM_FOLLOW_ANCHOR);
                            customMsgExtra.setMsgContent("关注了主播");
                            customMsgExtra.setTanmu(false);
                            CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, null);
                            customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                            sendCustomMessage(customMsgInfo);
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                            ToastUtils.showCenterToast(errorMsg);
                        }
                    });
                }
            });
        }else{
            findViewById(R.id.rl_anchorInfo).setVisibility(GONE);
        }
    }

    /**
     * 美颜是否已开启
     * @param beautyEnable
     */
    public void onBeautyEnable(boolean beautyEnable) {
        ((ImageView) findViewById(R.id.view_btn_beauty)).setImageResource(beautyEnable?R.drawable.video_call_beauty_true:R.drawable.video_call_beauty);
    }

    /**
     * 返回组件持有人的身份
     * @return  1:用户  0：主播
     */
    public int getUserIndetity() {
        return null!=mCallUserInfo&&TextUtils.equals(mCallUserInfo.getCallUserID(),UserManager.getInstance().getUserId())?1:0;
    }

    /**
     * 礼物动画任务开启
     */
    public void startPlayTask(){
        if(null!=mGiftGroupManager) mGiftGroupManager.startPlayTask();
        if(null!=mAwardGroupManager) mAwardGroupManager.startPlayTask();
    }

    /**
     * 开始倒计时、预付费逻辑、礼物动画播放
     */
    public void startAutoTask(){
        if(timerIsRuning) return;
        stopReckonTime();
        timerIsRuning=true;
        //第一分钟，只能检测用户余额
        if(null!=mCallUserInfo){
            int parseInt = Integer.parseInt(mCallUserInfo.getPrice());
            if(UserManager.getInstance().getDiamonds()<(parseInt*2)){
                showMoneryError();
            }
        }
        if (mBroadcastTimer == null) {
            mBroadcastTimer = new Timer(true);
        }
        mBroadcastTimerTask = new BroadcastTimerTask();
        mBroadcastTimer.schedule(mBroadcastTimerTask, 0, 1000);
    }

    /**
     * 结束计时
     */
    public void stopReckonTime(){
        timerIsRuning=false;
        if(null!=mBroadcastTimer) mBroadcastTimer.cancel(); mBroadcastTimer=null;
        if(null!=mBroadcastTimerTask) mBroadcastTimerTask.cancel(); mBroadcastTimerTask=null;
        if(null!=mRechargeTipsDialog){
            mRechargeTipsDialog.dismiss();
            mRechargeTipsDialog=null;
        }
    }

    /**
     * 返回用户身份 触发结束通话 等状态发起人的用户ID
     * @return
     */
    public int getIdType() {
        return null!=mCallUserInfo&&UserManager.getInstance().getUserId().equals(mCallUserInfo.getCallUserID())?1:2;
    }

    public long getNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }

    //getApplicationInfo().uid
    public long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(getContext().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String){
            String cmd= (String) arg;
            if(TextUtils.equals(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED, cmd)){
                if(null!=mCallUserInfo){
                    if(UserManager.getInstance().getDiamonds()<Integer.parseInt(mCallUserInfo.getPrice())){
                        showMoneryError();
                    }else{
                        View moneryTips = findViewById(R.id.view_monery_tips);
                        if(null!=moneryTips&&moneryTips.getVisibility()!=GONE) moneryTips.setVisibility(GONE);
                    }
                }
            }else if(TextUtils.equals(Constant.OBSERVER_CMD_PRIVATE_RECHARGE_SUCCESS, cmd)){
                if(1==getUserIndetity()){
                    if(mCountdownDurtion<60){
                        mCountdownDurtion+=10;
                    }
                    View moneryTips = findViewById(R.id.view_monery_tips);
                    if(null!=moneryTips&&moneryTips.getVisibility()!=GONE) moneryTips.setVisibility(GONE);
                }
            }
        }
    }

    /**
     * 计时器与预付费、预查询 等操作
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            LiveCallController.this.post(new Runnable() {
                @Override
                public void run() {
                    //发起方正计时
                    mReckonDurtion=(mReckonDurtion+1);
                    //接收方倒计时
                    mCountdownDurtion=(mCountdownDurtion-1);
                    //设置正计时，接收方、发起方同时设置
                    if(null!=mReckonTime) mReckonTime.setText(DateUtil.minuteFormat(mReckonDurtion*1000));
                    //扣费循环计时器
                    if(second==60){
                        second=0;
                    }
                    second++;//当前一分钟内实时秒数
                    //发起方每分钟第一秒立即扣费
                    if(1==second&&1== getUserIndetity()){
                        getCallDurtion(0,true);
                    }
                    //接收方在第二秒检查发起方用户剩余的通话时长
                    if(2==second&&0== getUserIndetity()){
                        getCallDurtion(1,false);
                    }
                    //双方心跳状态上报
                    heartSecond++;
                    //每隔15秒，上报一次心跳事件
                    if(heartSecond== HEART_POST_DURTION){
                        //如遇对方已下载线，强行结束
                        MakeCallManager.getInstance().postCallHeartState(getInitiatorUserID(), getAnchorUserID(), getIdType(), new OnVideoCallBackListener() {
                            @Override
                            public void onSuccess(Object object) {

                            }
                            @Override
                            public void onFailure(int code, String errorMsg) {
                                if(code==2003){
                                    if(null!=mOnFunctionListener) mOnFunctionListener.onFinlishCall(getIdType(),"用户网络差，已掉线");
                                }
                            }
                        });
                        heartSecond=0;
                    }
                    //主播端
                    if(0== getUserIndetity()){
                        if(mCountdownDurtion<0) mCountdownDurtion=0;
                        if(null!=mUnreckonTime) mUnreckonTime.setText(DateUtil.minuteFormat(mCountdownDurtion>=0?mCountdownDurtion*1000:0));//呼叫方的剩余时长
                        if(mCountdownDurtion==0){
                            //最后一刻，主播检查用户剩余通话时长，不足则结束视频通话
                            getCallDurtion(1,true);
                        }
                    //发起方
                    }else{
                        if(mCountdownDurtion<=0){
                            if(null!= mRechargeTipsDialog) {
                                mRechargeTipsDialog.dismiss();
                                mRechargeTipsDialog=null;
                            }
                            if(null!=mOnFunctionListener) mOnFunctionListener.onFinlishCall(getIdType(),"钻石余额不足，请充值后使用");//结束视频通话
                            return;
                        }
                        //更新充值提示倒计时时间
                        if(mCountdownDurtion<15){
                            if(null!=mRechargeTipsDialog&&mRechargeTipsDialog.isShowing()){
                                mRechargeTipsDialog.setCancelTitleText("取消("+mCountdownDurtion+")");
                            }
                            return;
                        }
                        //提前15秒弹窗通知用户剩余通话时长不足
                        if(mCountdownDurtion==15){
                            showRechgreTipaDialog();
                            return;
                        }
                        //提前一分钟告知用户余额不足
                        if(mCountdownDurtion==60){
                            showMoneryError();
                        }
                    }
                    //每隔5秒，检查一次通话质量，如不稳定，上报状态
//                    CallLogInfo callLogInfo=new CallLogInfo();
//                    callLogInfo.setUserid(mCallUserInfo.getCallUserID());
//                    callLogInfo.setAnchorid(mCallUserInfo.getCallAnchorID());
//                    callLogInfo.setSendKbps(sendKbps);
//                    callLogInfo.setRecvKbps(recvKbps);
//                    callLogInfo.setSendRate(sendLossRate);
//                    callLogInfo.setRecvRate(recvLossRate);
//                    callLogInfo.setAppCPURate(appCPURate);
//                    callLogInfo.setSysCPURate(sysCPURate);
//                    callLogInfo.setNetSpeedDown(netSpeedDownkbs);
//                    callLogInfo.setNetSpeedUp(netSpeedUpkbs);
//                    ActionLogInfo<CallLogInfo> actionLogInfo=new ActionLogInfo();
//                    actionLogInfo.setData(callLogInfo);
//                    UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_CALL,actionLogInfo,null);
                }
            });
        }
    }

    /**
     * 提前一分钟向用户提示余额不足
     */
    public void showMoneryError(){
        if(1==getUserIndetity()){
            final View moneryTipsView = findViewById(R.id.view_monery_tips);
            if(null==moneryTipsView) return;
            if(moneryTipsView.getVisibility()==VISIBLE) return;
            moneryTipsView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallRechargeActivity.start(((Activity) getContext()));
                }
            });
            findViewById(R.id.view_btn_tips_close).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    moneryTipsView.setVisibility(GONE);
                }
            });
            ((MarqueeTextView) findViewById(R.id.view_monery_tips_text)).setText(Html.fromHtml("<font color='#FFFCAC'>提示： </font><font color='#FFFFFF'>通话时长不足一分钟，请尽快充值。</font>"));
            TranslateAnimation translateAnimation = AnimationUtil.moveLeftToViewLocation();
            moneryTipsView.setVisibility(VISIBLE);
            moneryTipsView.startAnimation(translateAnimation);
        }
    }

    /**
     * 获取剩余通话时长
     * @param isSelect 0:付费 1：查询
     * @param errorFinlish 如果失败了，是否立即结束
     */
    private synchronized void getCallDurtion(final int isSelect, final boolean errorFinlish) {
        MakeCallManager.getInstance().buyCallDuration(getInitiatorUserID(), getAnchorUserID(), null==mCallUserInfo?"":mCallUserInfo.getRecevierID(),isSelect, new OnVideoCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                repostCount=0;
                if(null!=object && object instanceof CallResultInfo){
                    CallResultInfo resultInfo= (CallResultInfo) object;
                    //更新剩余时间
                    LiveCallController.this.mCountdownDurtion=resultInfo.getLimit_time();
                    chatPaidFee=resultInfo.getChat_paid_fee();
                    if(0==isSelect&&1==resultInfo.getIs_paid()){
                        //用户扣费成功且剩余通话时长充裕
                        if(null!=mCallUserInfo&&mCountdownDurtion>60&&UserManager.getInstance().getDiamonds()>Integer.parseInt(mCallUserInfo.getPrice())){
                            View moneryTips = findViewById(R.id.view_monery_tips);
                            if(null!=moneryTips&&moneryTips.getVisibility()!=GONE) moneryTips.setVisibility(GONE);
                        }
                        if(null!=mRechargeTipsDialog){
                            mRechargeTipsDialog.dismiss();
                            mRechargeTipsDialog=null;
                        }
                        return;
                    }
                    if(0==isSelect&&0==resultInfo.getIs_paid()&&null!=mFrequeControl&&mFrequeControl.canTrigger()){
                        Logger.d(TAG,"发起方付费失败,重试付费");
                        getCallDurtion(isSelect,errorFinlish);
                        return;
                    }
                    if(1==isSelect&&resultInfo.getLimit_time()<=0){
                        if(null!=mOnFunctionListener) mOnFunctionListener.onFinlishCall(getIdType(),"对方钻石余额不足");//结束视频通话
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                repostCount++;
                if(repostCount>=12){
                    if(errorFinlish&&null!=mOnFunctionListener){
                        mOnFunctionListener.onFinlishCall(getIdType(),errorMsg);
                    }
                    return;
                }
                if(1==isSelect&&null!=mFrequeControl&&mFrequeControl.canTrigger()){
                    getCallDurtion(isSelect,errorFinlish);
                    return;
                }
                if(errorFinlish&&null!=mOnFunctionListener){
                    mOnFunctionListener.onFinlishCall(getIdType(),errorMsg);
                }
            }
        });
    }

    /**
     * 改变信号状态
     * @param packetLostRate 丢包率 理论上>0 即为网络抖动
     */
    public void changedSpeedState(long packetLostRate) {
        if(null!=mTvNetIcon) mTvNetIcon.setImageResource(packetLostRate>0?R.drawable.ic_network_warn :R.drawable.ic_network_normal);
        if(null!=mTvNetContent){
            mTvNetContent.setText(packetLostRate>0?"信号弱":"信号强");
            mTvNetContent.setTextColor(packetLostRate>0?getContext().getResources().getColor(R.color.red_ff7575):getContext().getResources().getColor(R.color.white));
        }
        if(null!=mTvNetTips){
            mTvNetTips.removeCallbacks(hideNetRunnable);
            if(packetLostRate>0){
                mTvNetTips.setVisibility(VISIBLE);
                mTvNetTips.postDelayed(hideNetRunnable,3000);
            }else{
                AnimationUtil.goneTransparentView(mTvNetTips);
            }
        }
    }

    /**
     * 定时关闭网络提示
     */
    private Runnable hideNetRunnable=new Runnable() {
        @Override
        public void run() {
            AnimationUtil.goneTransparentView(mTvNetTips);
        }
    };

    /**
     * 购买钻石,避免重复或者不可见状态下弹出
     */
    private void showRechgreTipaDialog() {
        if(null!=mRechargeTipsDialog) return;
        if(null!=getContext()){
            final Activity activity= (Activity) getContext();
            mRechargeTipsDialog = QuireDialog.getInstance(activity);
            mRechargeTipsDialog.setTitleText("钻石不足")
                    .setContentText("是否充值钻石?")
                    .setSubmitTitleText("充值")
                    .setCancelTitleText("取消("+mCountdownDurtion+")")
                    .setDialogCancelable(false)
                    .setDialogCanceledOnTouchOutside(false)
                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent() {
                            if(null!=getContext()){
                                CallRechargeActivity.start(((Activity) getContext()));
                            }
                        }

                        @Override
                        public void onRefuse() {

                        }
                    });
            mRechargeTipsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mRechargeTipsDialog=null;
                }
            });
            mRechargeTipsDialog.show();
        }
    }

//=============================================礼物模块==============================================
    /**
     * 显示礼物面板
     * 赠送礼物，本地负责先播放动画，远程端交由后台去推送礼物赠送消息
     * @param pusherInfo
     */
    private void showGiftBoardView(PusherInfo pusherInfo) {
        if(null==getContext()) return;
        FragmentActivity context = (FragmentActivity) getContext();
        if(!context.isFinishing()){
            LiveGiftDialog fragment = LiveGiftDialog.getInstance(context,pusherInfo, mGroupID, LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM,true,-1);
            fragment.setOnGiftSelectedListener(new LiveGiftDialog.OnGiftSelectedListener() {
                @Override
                public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                    //本地播放礼物动画，远程端交由服务器推送
                    CustomMsgExtra customMsgExtra=new CustomMsgExtra();
                    customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
                    if(null!=accepUserInfo){
                        customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
                        customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
                        customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
                    }
                    data.setCount(count);
                    CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
                    data.setSource_room_id(mGroupID);
                    customMsgInfo.setAccapGroupID(mGroupID);
                    //避免本地数量对不上，先开启倒计时赠送礼物按钮，再走本地动画播放逻辑
                    if(null!=mCountdownGiftView) mCountdownGiftView.updataView(data,mGroupID,count,accepUserInfo);
                    sendCustomGiftMessage(customMsgInfo);
                }

                @Override
                public void onDissmiss() {
                }
                //礼物选择发生了变化
                @Override
                public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo) {
                    if(null!=mCountdownGiftView) mCountdownGiftView.updataView(giftInfo,mGroupID,count,accepUserInfo);
                }
            });
            fragment.show();//context.getSupportFragmentManager(),"gift"
        }
    }

    /**
     * 发送一条自定义礼物消息,使用通知代替
     * @param customMsgInfo
     */
    private void sendCustomMessage(CustomMsgInfo customMsgInfo){
        if(null==customMsgInfo) return;
        if(null!=mCallUserInfo){
            VideoCallManager.getInstance().sendCustomMessage(customMsgInfo, mCallUserInfo.getToUserID(), new OnCustomMessageListener() {
                @Override
                public void onSendOk(Object object) {
                }

                @Override
                public void onError(int code, String msg) {
                }
            });
        }
    }

    /**
     * 发送一条自定义礼物消息,使用通知代替
     * @param customMsgInfo
     */
    private void sendCustomGiftMessage(CustomMsgInfo customMsgInfo){
        if(null==customMsgInfo) return;
        if(null!=mCallUserInfo){
            VideoCallManager.getInstance().sendCustomGiftMessage(customMsgInfo, mCallUserInfo.getToUserID(), new OnCustomMessageListener() {
                @Override
                public void onSendOk(Object object) {
                }

                @Override
                public void onError(int code, String msg) {
                }
            });
        }
    }

    /**
     * 礼物赠送、关注事件等自定义消息
     * @param customMsgInfo 消息的封装体
     * @param isSystemPro 是否来自系统推送
     */
    public void newSystemCustomMessage(CustomMsgInfo customMsgInfo,boolean isSystemPro) {
        if(null==customMsgInfo) return;
        if(null==customMsgInfo.getCmd()) return;
        try {
            for (String cmd : customMsgInfo.getCmd()) {
                customMsgInfo.setChildCmd(cmd);//给适配器用的字段,聊天列表多条目用到，必须设定
                Logger.d(TAG, "---GROUP_MESSAGE---CMD:" + cmd
                        + ",MINE_GROUPID:" + LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID()
                        + ",SOURCE_GROUP_ID:" +(null== customMsgInfo.getGift()?"":customMsgInfo.getGift().getSource_room_id())+",CONTENT:"+customMsgInfo.getMsgContent());
                //礼物动画处理
                GiftInfo giftInfo = customMsgInfo.getGift();
                if(cmd.equals(Constant.MSG_CUSTOM_GIFT)){
                    if(null!=giftInfo){
                        //过滤自己发送的礼物消息
                        if(isSystemPro&& UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID())){
                            continue;
                        }
                        //非幸运礼物提示
                        if (isSystemPro&&null!=mConversationListView) {
                            mConversationListView.addConversation(customMsgInfo,isSystemPro,true);
                        }
                        //所有的赠送礼物动画先播放普通动画效果
                        if(null!=mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
                        //豪华礼物
                        if(null!=giftInfo.getBigSvga()&&giftInfo.getBigSvga().endsWith(".svga")){
                            if(null!=mSvgaPlayerManager) mSvgaPlayerManager.addAnimationToTask(customMsgInfo);
                        }
                    }
                    continue;
                    //中奖消息,在当前礼物View上面追加显示,若当前礼物View没有创建，则重新初始化View
                }else if(cmd.equals(Constant.MSG_CUSTOM_ROOM_DRAW)){
                    if(null!=giftInfo){
                        //如果是自己中奖了，通知礼物面板，用户本地积分发生了变化
                        if(customMsgInfo.getSendUserID().equals(UserManager.getInstance().getUserId())){
                            UserManager.getInstance().setDiamonds(UserManager.getInstance().getDiamonds()+giftInfo.getDrawIntegral());
                            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
                            //标记用户中心需要刷新
                            VideoApplication.getInstance().setMineRefresh(true);
                        }
                        //普通礼物动画
                        if(null!=mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
                        if(null!=mAwardGroupManager) mAwardGroupManager.addAwardItemToLayout(customMsgInfo);
                    }
                    continue;
                }else if(cmd.equals(Constant.MSG_CUSTOM_NOTICE)
                        ||cmd.equals(Constant.MSG_CUSTOM_FOLLOW_ANCHOR)){
                    if(null!=mConversationListView) mConversationListView.addConversation(customMsgInfo,false,true);
                    continue;
                }
            }
        }catch (RuntimeException e){

        }
    }

    public abstract static class OnFunctionListener{
        //相机开关
        public void onChangeCamera(ImageView button){}
        //相机切换
        public void onSwitchCamera(){}
        //麦克风开关
        public void onChangeMic(ImageView button){}
        //美颜
        public void onBeauty(){}
        //挂断视频呼叫
        public void onEndCall(boolean isShowTips,int idType,String message){}
        //强制结束视频通话
        public void onFinlishCall(int idType,String message){}
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }

    public String getToUserAvater() {
        return toUserAvater;
    }

    public void setToUserAvater(String avater) {
        this.toUserAvater = avater;
    }

    public String getToUserNickname() {
        return toUserNickname;
    }

    public void setToUserNickname(String nickname) {
        this.toUserNickname = nickname;
    }

    public String getToUserUserID() {
        return toUserUserID;
    }

    public void setToUserUserID(String userID) {
        this.toUserUserID = userID;
    }

    public String getAnchorUserID() {
        return null==mCallUserInfo?UserManager.getInstance().getUserId():mCallUserInfo.getCallAnchorID();
    }

    public String getInitiatorUserID() {
        return null==mCallUserInfo?UserManager.getInstance().getUserId():mCallUserInfo.getCallUserID();
    }

    /**
     * 持续通话时间
     * @return 单位秒
     */
    public long getReckonDurtion() {
        return mReckonDurtion;
    }

    public void setReckonDurtion(long reckonDurtion) {
        mReckonDurtion = reckonDurtion;
    }

    public String getReserveID() {
        return null==mCallUserInfo?"":mCallUserInfo.getRecevierID();
    }

    public void setGroupID(String groupID) {
        mGroupID = groupID;
    }

    public void onResume(){
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onResume();
        if(null!=mAwardGroupManager) mAwardGroupManager.onResume();
    }

    public void onPause(){
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onPause();
        if(null!=mAwardGroupManager) mAwardGroupManager.onPause();
    }

    /**
     * 对应生命周期调用
     */
    public void onDestroy(){
        stopReckonTime();
        if(null!=mConversationListView) mConversationListView.onDestroy();
        if(null!=mTvNetTips) mTvNetTips.setVisibility(GONE);
        if(null!=mTvNetTips) mTvNetTips.removeCallbacks(hideNetRunnable);
        if(null!=mAwardGroupManager) mAwardGroupManager.onDestroy();
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onDestroy();
        if(null!=mGiftGroupManager) mGiftGroupManager.onDestroy();
        if(null!=mCountdownGiftView) mCountdownGiftView.onDestroy();
        mReckonDurtion=0;mCountdownDurtion=0;heartSecond=0;second=9;
        mSvgaPlayerManager=null;mGiftGroupManager=null;mCountdownGiftView=null;
        mAwardGroupManager=null;mOnFunctionListener=null;mConversationListView=null;
    }
}