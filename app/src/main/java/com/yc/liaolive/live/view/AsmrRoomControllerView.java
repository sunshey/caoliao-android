package com.yc.liaolive.live.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.RoomBaseController;
import com.yc.liaolive.bean.NumberChangedInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.RoomInitInfo;
import com.yc.liaolive.live.bean.VideoCallOlder;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.live.listener.OnExceptionListener;
import com.yc.liaolive.live.manager.GiftManager;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.ui.contract.LiveControllerInterface;
import com.yc.liaolive.live.ui.contract.LiveRoomControllerContract;
import com.yc.liaolive.live.ui.presenter.LiveRoomControllerPresenter;
import com.yc.liaolive.live.util.RoomDataCache;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;

import java.util.Observable;
import java.util.Observer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2019/2/22
 * 直播房间 推、拉 控制器
 * 必须给定应用场景 mMode
 */

public class AsmrRoomControllerView extends RoomBaseController implements View.OnClickListener, LiveRoomControllerContract.View, Observer, LiveControllerInterface {

    private static final String TAG = "VideoLiveControllerView";
    //功能交互的应用场景
    public static final int LIVE_SCENE_PULL = 0;//直播间 拉
    public static final int LIVE_SCENE_PUSH = 1;//直播间 推
    private int SECOND;
    private UserInfo mAnchorUserData;//主播的用户信息
    private LiveRoomControllerPresenter mPresenter;
    private int mIdentifyType= LIVE_SCENE_PULL;//应用场景，默认视频拉流
    private QuireDialog mQuireDialog;
    private Handler mHandler;
//    private TCHeartLayout mHeartLayout;//飘心
//    private CountdownGiftView mCountdownGiftView;//连击赠送礼物
//    private RoomGiftGroupManager mGiftGroupManager;//普通礼物
//    private AnimatorSvgaPlayerManager mSvgaPlayerManager;//豪华礼物
//    private RoomAwardGroupManager mAwardGroupManager;//中奖
//    private VipUserEnterManager mVipUserEnterManager;//会员进场
//    private RoomDanmuManager mDrawDanmuManager;//飘屏弹幕
//    private RoomSuperAwardAnimatorGroupManager mAwardAnimatorPlayManager;//全屏超级大奖
    private LoadingIndicatorView mLoadingView;//加载中
    //直播间错误信息
    private RoomErrorLayout mRoomErrorLayout;
    private OnExceptionListener mExceptionListener;
//    private LiveGiftDialog mFragment;


    public AsmrRoomControllerView(Context context) {
        super(context);
        init(context,null);
    }

    public AsmrRoomControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_asmr_live_controller_layout,this);
//        findViewById(R.id.view_btn_gift).setOnClickListener(this);//礼物
        findViewById(R.id.view_btn_close).setOnClickListener(this);//关闭
        RoomDataCache.getInstance().registerHostView(this);
        //飘心
//        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);
//        //普通礼物
//        mGiftGroupManager = (RoomGiftGroupManager) findViewById(R.id.room_gift_manager);
//        mGiftGroupManager.setOnFunctionListener(new OnFunctionListener() {
//            @Override
//            public void onSendGift(FansInfo userInfo) {
//                //接收对象
//                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
//            }
//        });
//        //会员进场
//        mVipUserEnterManager = (VipUserEnterManager) findViewById(R.id.view_vip_enter);
//        mVipUserEnterManager.setOnFunctionListener(new OnFunctionListener() {
//            @Override
//            public void onSendGift(FansInfo userInfo) {
//                //接收对象
//                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
//            }
//        });
//        //中奖信息展示
//        mAwardGroupManager = (RoomAwardGroupManager) findViewById(R.id.room_award_manager);
//        mAwardGroupManager.setOnFunctionListener(new OnFunctionListener() {
//            @Override
//            public void onSendGift(FansInfo userInfo) {
//                //接收对象
//                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
//            }
//        });
//        //超级大奖
//        mAwardAnimatorPlayManager = (RoomSuperAwardAnimatorGroupManager) findViewById(R.id.award_animator_manager);
//        mAwardAnimatorPlayManager.setWindowMode(RoomSuperAwardAnimaorView.WindownMode.FULL);
//        mAwardAnimatorPlayManager.setAutoCleanMillis(10000);//动画延续时长，毫秒
//        mAwardAnimatorPlayManager.setAnimatorPlayListener(new AnimatorPlayListener() {
//            @Override
//            public void onStart(AwardInfo awardInfo) {
//                if(null!=awardInfo&&awardInfo.isMine()){
//                    if(null!=mCountdownGiftView&&!mCountdownGiftView.isRunning()){
//                        //如果是自己中奖并且倒计时已经消失了
//                        GiftInfo giftInfo=new GiftInfo();
//                        giftInfo.setId(awardInfo.getId());
//                        giftInfo.setTitle(awardInfo.getTitle());
//                        giftInfo.setSrc(awardInfo.getSrc());
//                        giftInfo.setPrice(awardInfo.getPrice());
//                        //接收人信息
//                        PusherInfo pusherInfo=new PusherInfo();
//                        pusherInfo.setUserID(awardInfo.getAccaptUserID());
//                        mCountdownGiftView.updataView(giftInfo,LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),awardInfo.getCount(),pusherInfo);
//                    }
//                }
//            }
//
//            @Override
//            public void onEnd() {
//
//            }
//        });
//        //奢侈礼物、中奖飘屏弹幕
//        IDanmakuView drawDanmakuView = (IDanmakuView) findViewById(R.id.draw_danmakuView);
//        mDrawDanmuManager = new RoomDanmuManager(getContext());
//        mDrawDanmuManager.bindDanmakuView(drawDanmakuView);
//        //奢侈礼物动画
//        mSvgaPlayerManager = (AnimatorSvgaPlayerManager) findViewById(R.id.svga_animator);
//        mSvgaPlayerManager.bindDanmakuView(mDrawDanmuManager);
        //异常情况
        mRoomErrorLayout = (RoomErrorLayout) findViewById(R.id.error_layout);
        mRoomErrorLayout.setOnExceptionListener(new OnExceptionListener() {
            @Override
            public void onTautology() {
                if(null!=mExceptionListener) mExceptionListener.onTautology();
            }
        });
        //缓冲中
        mLoadingView = (LoadingIndicatorView) findViewById(R.id.loading_view);
        mPresenter = new LiveRoomControllerPresenter();
        mPresenter.attachView(this);
        mHandler = new Handler(Looper.getMainLooper());
        //连击赠送礼物
//        mCountdownGiftView = (CountdownGiftView) findViewById(R.id.view_countdown_view);
//        mCountdownGiftView.setOnGiftSendListener(new CountdownGiftView.OnCountdownGiftSendListener() {
//            @Override
//            public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
//                //本地播放礼物动画，远程端交由服务器推送
//                CustomMsgExtra customMsgExtra = new CustomMsgExtra();
//                customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
//                if(null!=accepUserInfo){
//                    customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
//                    customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
//                    customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
//                }
//                data.setCount(count);
//                data.setSource_room_id(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
//                CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
//                customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
//                newSystemCustomMessage(customMsgInfo,false);
//            }
//        });
        ApplicationManager.getInstance().addObserver(this);
        GiftManager.getInstance().setShowWhiteTips("");
    }

    /**
     * 显示、隐藏软键盘
     * @param flag 输入法是否显示
     * @param keyBordHeight 软键盘高度
     * 采坑：room_gift_manager；礼物展示空间高度挤压了屏幕的剩余空间，导致empty_view设置高度无效
     */
    @Override
    public void showInputKeyBord(boolean flag, int keyBordHeight) {}

    /**
     * 系统发送的自定义消息、系统通知、礼物赠送、成员的进出场、聊天列表消息、中奖信息 等
     * @param customMsgInfo 消息的封装体
     * @param isSystemPro 是否来自系统推送
     */
    @Override
    public void newSystemCustomMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null==customMsgInfo||null==mAnchorUserData||null==customMsgInfo.getCmd()) return;
//        for (String cmd : customMsgInfo.getCmd()) {
//            customMsgInfo.setChildCmd(cmd);//聊天列表所需
//            GiftInfo giftInfo = customMsgInfo.getGift();
//            Logger.d(TAG, "---GROUP_MESSAGE---CMD:" + cmd
//                    + ",MINE_GROUPID:" + LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID()
//                    + ",SOURCE_GROUP_ID:" +(null== customMsgInfo.getGift()?"":customMsgInfo.getGift().getSource_room_id())+",CONTENT:"+customMsgInfo.getMsgContent());
//            if(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID().equals(customMsgInfo.getAccapGroupID())){
//                //自定义消息、系统消息、成员进场
//                switch (cmd) {
//                    case Constant.MSG_CUSTOM_TEXT://文本消息
//                    case Constant.MSG_CUSTOM_NOTICE://系统公告
//                    case Constant.MSG_CUSTOM_ADD_USER://新增用户
//                    case Constant.MSG_CUSTOM_FOLLOW_ANCHOR://关注主播
//                        Logger.d(TAG,"列表消息："+cmd+",CONTENT:"+customMsgInfo.getMsgContent());
//                        //会员，飘心处理
//                        if (cmd.equals(Constant.MSG_CUSTOM_ADD_USER)) {
//                            //过滤机器人进场
//                            clickHeart();
//                            if(customMsgInfo.getSendUserVIP()> 0&&null!=mVipUserEnterManager){
//                                Logger.d(TAG,"进场消息--会员进场");
//                                FansInfo fansInfo=new FansInfo();
//                                fansInfo.setUserid(customMsgInfo.getSendUserID());
//                                fansInfo.setVip(customMsgInfo.getSendUserVIP());
//                                fansInfo.setNickname(customMsgInfo.getSendUserName());
//                                fansInfo.setAvatar(customMsgInfo.getSendUserHead());
//                                mVipUserEnterManager.addUserToTask(fansInfo);
//                            }
//                        }
//                        continue;
//                        //点赞消息
//                    case Constant.MSG_CUSTOM_PRICE:
//                        Logger.d(TAG,"点赞消息");
//                        clickHeart();
//                        continue;
//                        //观众离开,人数发生了变化
//                    case Constant.MSG_CUSTOM_REDUCE_USER:
//                        Logger.d(TAG,"人数发生了变化");
//                        continue;
//                        //观众列表发生了变化
//                    case Constant.MSG_CUSTOM_TOP_USER:
//                        Logger.d(TAG,"观众列表发生了变化");
//                        continue;
//                        //礼物信息
//                    case Constant.MSG_CUSTOM_GIFT:
//                        Logger.d(TAG,"--礼物消息--");
//                        if(null!=giftInfo){
//                            //过滤自己发送的礼物消息
//                            if(isSystemPro && UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID())){
//                                Logger.d(TAG,"礼物消息--过滤自己发送的在线礼物消息");
//                                continue;
//                            }
//                            //主播端
//                            if(getIdentifyType()==LIVE_SCENE_PUSH){
//                                Logger.d(TAG,"礼物消息--主播端");
//                                if(null!=mAnchorUserData&&TextUtils.equals(mAnchorUserData.getUserid(), UserManager.getInstance().getUserId())){
//                                    if(TextUtils.equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),giftInfo.getSource_room_id())){
//                                        //非幸运礼物提示
//                                        if (1!=giftInfo.getGift_category()&&TextUtils.equals(UserManager.getInstance().getUserId(),customMsgInfo.getAccapUserID())) {
//                                            Logger.d(TAG,"礼物消息--主播端奢侈礼物消息");
//                                            //产生一条会话消息
//                                        }
//                                        if (null != mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
//                                        //豪华礼物
//                                        if (null != giftInfo.getBigSvga() && giftInfo.getBigSvga().endsWith(".svga")) {
//                                            if(null!=mSvgaPlayerManager) mSvgaPlayerManager.addAnimationToTask(customMsgInfo);
//                                        }
//                                    }
//                                }
//                                //用户端
//                            }else{
//                                Logger.d(TAG,"礼物消息--用户端");
//                                if (null != mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
//                                //豪华礼物
//                                if (null != giftInfo.getBigSvga() && giftInfo.getBigSvga().endsWith(".svga")) {
//                                    if(null!=mSvgaPlayerManager) mSvgaPlayerManager.addAnimationToTask(customMsgInfo);
//                                }
//                            }
//                        }
//                        continue;
//                        //中奖信息
//                    case Constant.MSG_CUSTOM_ROOM_DRAW:
//                        Logger.d(TAG,"--中奖消息--：ROOM:"+giftInfo.getSource_room_id());
//                        if(null!=giftInfo){
//                            // TODO: 2018/12/17 模拟超级大奖
////                                giftInfo.setPrize_level(3);
////                                giftInfo.setDrawTimes(500);
//                            //自己中奖了
//                            if(customMsgInfo.getSendUserID().equals(UserManager.getInstance().getUserId())){
//                                Logger.d(TAG,"--直播间内自己中奖了--");
//                                UserManager.getInstance().setDiamonds(UserManager.getInstance().getDiamonds()+giftInfo.getDrawIntegral());
//                                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
//                                //标记用户中心需要刷新
//                                VideoApplication.getInstance().setMineRefresh(true);
//                            }
//                            //主播端、用户端 都显示中奖公示信息
//                            if(null!=mAwardGroupManager) mAwardGroupManager.addAwardItemToLayout(customMsgInfo);
//                            //主播端
//                            if(getIdentifyType()==LIVE_SCENE_PUSH){
//                                Logger.d(TAG,"中奖消息--主播端");
//                                if(null!=mAnchorUserData&&TextUtils.equals(mAnchorUserData.getUserid(), UserManager.getInstance().getUserId())){
//                                    if(TextUtils.equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),giftInfo.getSource_room_id())){
//                                        Logger.d(TAG,"中奖消息--主播端自己房间中奖消息");
//                                        if(null!=mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
//                                        if(giftInfo.getPrize_level()>=3){
//                                            //飘屏
//                                            Logger.d(TAG,"中奖消息--主播端自己房间超级大奖");
//                                            if(null!=mDrawDanmuManager) mDrawDanmuManager.addRoomDanmu(customMsgInfo,RoomDanmuManager.DanmuType.AWARD);
//                                            addAwardToTask(customMsgInfo,mAwardAnimatorPlayManager,UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID()));
//                                        }
//                                    }else{
//                                        //其他房间只播放超级中奖动画
//                                        if(giftInfo.getPrize_level()>=3){
//                                            //飘屏
//                                            Logger.d(TAG,"中奖消息--主播端其他房间超级大奖");
//                                            if(null!=mDrawDanmuManager) mDrawDanmuManager.addRoomDanmu(customMsgInfo,RoomDanmuManager.DanmuType.AWARD);
//                                            //超级大奖
//                                            addAwardToTask(customMsgInfo,mAwardGroupManager.getAwardAnimatorMinPlayManager(),false);
//                                        }
//                                    }
//                                }
//                                //用户端
//                            }else{
//                                Logger.d(TAG,"中奖消息--用户端");
//                                if(null!=mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
//                                //直播间中了超级大奖
//                                if(giftInfo.getPrize_level()>=3){
//                                    Logger.d(TAG,"中奖消息--用户端超级大奖");
//                                    //所有房间弹幕飘屏
//                                    if(null!=mDrawDanmuManager) mDrawDanmuManager.addRoomDanmu(customMsgInfo,RoomDanmuManager.DanmuType.AWARD);
//                                    //本房间超级大奖动画
//                                    if(TextUtils.equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),giftInfo.getSource_room_id())){
//                                        Logger.d(TAG,"中奖消息--用户端自己所在房间超级大奖");
//                                        addAwardToTask(customMsgInfo,mAwardAnimatorPlayManager,UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID()));
//                                    }else{
//                                        Logger.d(TAG,"中奖消息--用户端其他房间超级大奖");
//                                        addAwardToTask(customMsgInfo,mAwardGroupManager.getAwardAnimatorMinPlayManager(),false);
//                                    }
//                                }
//                            }
//                            continue;
//                        }
//                }
//            }
//        }
    }

    /**
     * 群组纯文本消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    @Override
    public void onNewTextMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
    }

    /**
     * 小型的迷你消息
     * @param groupId
     * @param sender
     * @param changedInfo 人数变化
     */
    @Override
    public void onNewMinMessage(String groupId, String sender, NumberChangedInfo changedInfo) {}

    /**
     * 返回当前直播间推流的时长
     * @return
     */
    @Override
    public long getSecond() {
        return SECOND;
    }

    /**
     * 开始计时
     */
    @Override
    public void startReckonTime(){}

    /**
     * 结束计时
     */
    @Override
    public void stopReckonTime() {}

    @Override
    public void onResume() {
//        if(null!=mDrawDanmuManager) mDrawDanmuManager.onResume();
//        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onResume();
//        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onResume();
//        if(null!=mAwardGroupManager) mAwardGroupManager.onResume();
    }

    @Override
    public void onPause() {
//        if(null!=mDrawDanmuManager) mDrawDanmuManager.onPause();
//        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onPause();
//        if(null!=mVipUserEnterManager) mVipUserEnterManager.onDestroy();
//        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onPause();
//        if(null!=mAwardGroupManager) mAwardGroupManager.onPause();
    }

    /**
     * 伪onDestroy()，一定要调用
     */
    @Override
    public void onDestroy(){
        stopReckonTime();
//        if(null!=mFragment) mFragment.dismiss();
        RoomDataCache.getInstance().onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
//        if(null!=mGiftGroupManager) mGiftGroupManager.onDestroy();
//        if(null!=mAwardGroupManager) mAwardGroupManager.onDestroy();
//        if(null!=mCountdownGiftView) mCountdownGiftView.onDestroy();
//        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onDestroy();
//        if(null!=mVipUserEnterManager) mVipUserEnterManager.onDestroy();
//        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onDestroy();
        if(null!=mLoadingView) mLoadingView.hide();
        if(null != mPresenter) mPresenter.detachView();
        if(null!=mQuireDialog&&mQuireDialog.isShowing()) mQuireDialog.dismiss();
        if(null!=mHandler) mHandler.removeMessages(0);
        mOnViewClickListener=null; mHandler=null;
        mQuireDialog=null;mPresenter = null;
//        mCountdownGiftView=null;mFragment=null;
//        mSvgaPlayerManager=null;mGiftGroupManager=null;
//        mVipUserEnterManager=null;mAwardAnimatorPlayManager=null;mAwardGroupManager=null;
    }

    /**
     * 绑定用户的身份场景
     * @param type  0：观众拉流  1：主播推流
     */
    public void setIdentityType(int type){
        this.mIdentifyType=type;
//        if(null!=mCountdownGiftView) mCountdownGiftView.setApiMode(LiveGiftDialog.GIFT_MODE_ROOM);
//        if(null!=mAwardGroupManager) mAwardGroupManager.setIdentityType(mIdentifyType);
//        if(null!=mVipUserEnterManager) mVipUserEnterManager.setIdentityType(mIdentifyType);
    }

    /**
     * 在线成员列表重生
     */
    @Override
    public void onMemberReset() {}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.view_btn_gift:
//                if(null!=mAnchorUserData&&null!=getContext()&&mIdentifyType== LIVE_SCENE_PULL){
//                    if(null!=getContext()) MobclickAgent.onEvent(getContext(), "click_gift");
//                    PusherInfo pusherInfo=new PusherInfo();
//                    pusherInfo.setUserName(mAnchorUserData.getNickname());
//                    pusherInfo.setUserID(mAnchorUserData.getUserid());
//                    pusherInfo.setUserAvatar(mAnchorUserData.getAvatar());
//                    showGiftBoardView(pusherInfo);
//                    return;
//                }
//                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(4);
//                break;
            case R.id.view_btn_close:
                if(null!=mOnViewClickListener) mOnViewClickListener.onBack();
                break;
        }
    }

    public void errorStateReset() {
        if(null!=mRoomErrorLayout) mRoomErrorLayout.errorStateReset();
    }

    public void showPullError(int leave, String content) {
        if(null!=mRoomErrorLayout) mRoomErrorLayout.showPullError(leave,content);
    }

    public void switchAppState(int state) {
        if(null!=mRoomErrorLayout) mRoomErrorLayout.switchAppState(state);
    }

    /**
     * 控制器的透明度渐变
     * @param alpha
     */
    public void setConntrollerAlpha(float alpha) {}

//    /**
//     * 触发飘心
//     */
//    public void clickHeart() {
//        if(null!=mHeartLayout) mHeartLayout.addFavor();
//    }
//
//    /**
//     * 仅当第一次进入直播间,自动显示礼物面板
//     */
//    public void showGiftBoardView() {
//        if(null==mAnchorUserData) return;
//        PusherInfo pusherInfo=new PusherInfo();
//        pusherInfo.setUserName(mAnchorUserData.getNickname());
//        pusherInfo.setUserID(mAnchorUserData.getUserid());
//        pusherInfo.setUserAvatar(mAnchorUserData.getAvatar());
//        showGiftBoardView(pusherInfo);
//    }

    /**
     * 显示加载中状态
     * @param tips 提示语
     */
    public void showLoadingView(String tips){
        if(null!=mLoadingView){
            mLoadingView.setVisibility(VISIBLE);
            mLoadingView.smoothToShow();
        }
    }

    /**
     * 隐藏加载中状态
     */
    public void hideLoadingView(){
        if(null!=mLoadingView) mLoadingView.smoothToHide();
        if(null!=mRoomErrorLayout) mRoomErrorLayout.errorStateReset();
    }

    /**
     * 返回控制器绑定的用户身份
     * @return
     */
    public int getIdentifyType() {
        return mIdentifyType;
    }

//    /**
//     * 开始动画播放处理线程
//     */
//    public void startPlayTask() {
//        if(null!=mGiftGroupManager) mGiftGroupManager.startPlayTask();
//        if(null!=mAwardGroupManager) mAwardGroupManager.startPlayTask();
//    }

    /**
     * 绑定主播信息
     * @param userData
     */
    public void setAnchorUserData(UserInfo userData){
        this.mAnchorUserData =userData;
        if(null!=mRoomErrorLayout) mRoomErrorLayout.setAnchorAvatar(mAnchorUserData.getFrontcover());
    }

    /**
     * 显示交互组件
     */
    public void showControllerView(){
//        startPlayTask();
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_LIVE_MESSAGE_CHANGED);//刷新下消息数量
    }

    /**
     * 调用此方法，初始化是否已关注、拉取在线观众列表
     * 调用前需先设置setIdentityType();
     */
    public void initData(String roomID) {
        if(null!=mAnchorUserData&&null!=mPresenter){
            //主播端
            if(mIdentifyType== LIVE_SCENE_PUSH) {
                //主播先上报一次心跳事件
                UserManager.getInstance().postRoomHeartState(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),LiveConstant.LIVE_SCENE_MODE_CHAT,1,null);
                if(null!=mHandler){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.roomInit(mAnchorUserData.getUserid(),LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                        }
                    },3000);
                }
            }else{
                mPresenter.roomInit(mAnchorUserData.getUserid(),roomID);
            }
        }
    }

//    /**
//     * 显示礼物面板
//     * 赠送礼物，本地负责先播放动画，远程端交由后台去推送礼物赠送消息
//     * @param pusherInfo
//     */
//    private void showGiftBoardView(PusherInfo pusherInfo) {
//        if(null==getContext()) return;
//        FragmentActivity context = (FragmentActivity) getContext();
//        if(null== mAnchorUserData)  return;
//        if(!context.isFinishing()){
//            mFragment = LiveGiftDialog.getInstance(context,pusherInfo,
//                    LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),
//                    LiveGiftDialog.GIFT_MODE_ROOM,true);
//            mFragment.setOnGiftSelectedListener(new LiveGiftDialog.OnGiftSelectedListener() {
//                @Override
//                public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
//                    //本地播放礼物动画，远程端交由服务器推送
//                    CustomMsgExtra customMsgExtra=new CustomMsgExtra();
//                    customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
//                    if(null!=accepUserInfo){
//                        customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
//                        customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
//                        customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
//                    }
//                    data.setCount(count);
//                    data.setSource_room_id(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
//                    CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
//                    customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
//                    newSystemCustomMessage(customMsgInfo,false);
//                }
//
//                @Override
//                public void onDissmiss() {
//                    super.onDissmiss();
//                    mFragment=null;
//                }
//
//                //礼物选择发生了变化
//                @Override
//                public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo) {
//                    if(null!=mCountdownGiftView) mCountdownGiftView.updataView(giftInfo,LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),count,accepUserInfo);
//                }
//            });
//            mFragment.show();
//        }
//    }
//
//    /**
//     * 添加超级中奖信息至队列
//     * @param customMsgInfo
//     * @param animatorPlayManager
//     * @param isMine
//     */
//    private void addAwardToTask(CustomMsgInfo customMsgInfo, RoomSuperAwardAnimatorGroupManager animatorPlayManager, boolean isMine) {
//        if(null==customMsgInfo||null==animatorPlayManager) return;
//        if(null==customMsgInfo.getGift()) return;
//        GiftInfo giftInfo = customMsgInfo.getGift();
//        //超级大奖
//        AwardInfo awardInfo=new  AwardInfo();
//        awardInfo.setNickName(customMsgInfo.getSendUserName());
//        awardInfo.setUserid(customMsgInfo.getSendUserID());
//        awardInfo.setMonery(giftInfo.getDrawIntegral());
//        awardInfo.setMine(UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID()));//是否是自己中奖了
//        awardInfo.setCount(1);
//        awardInfo.setId(giftInfo.getId());
//        awardInfo.setPrice(giftInfo.getPrice());
//        awardInfo.setTitle(giftInfo.getTitle());
//        awardInfo.setSrc(giftInfo.getSrc());
//        awardInfo.setMine(isMine);
//        awardInfo.setAccaptUserID(customMsgInfo.getAccapUserID());
//        if(null!=animatorPlayManager) animatorPlayManager.addAwardToTask(awardInfo);
//    }


    //=========================================网络数据交互回调=======================================

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}
    @Override
    public void showSettlementResult(VideoCallOlder data) {}
    @Override
    public void showSettlementError(int code, String data) {}

    /**
     * 房间初始化信息回调
     object * @param data
     */
    @Override
    public void showInitResult(RoomInitInfo data) {
    }

    @Override
    public void showInitResultError(int code, String data) {}

    /**
     * 定时器任务，单位 1 秒 循环
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            AsmrRoomControllerView.this.post(new Runnable() {
                @Override
                public void run() {
                    SECOND++;
                    //心跳
                    if(SECOND%21==0) UserManager.getInstance().postRoomHeartState(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),LiveConstant.LIVE_SCENE_MODE_CHAT,mIdentifyType== LIVE_SCENE_PUSH?1:2,null);
                }
            });
        }
    }

    /**
     * 订阅消息刷新
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String){
            if (TextUtils.equals(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED, (String) arg)) {
                //更新礼物面板钻石数
//                if (mFragment != null) {
//                    mFragment.setMoney(UserManager.getInstance().getDiamonds());
//                }
            }
        }
    }

    /**
     * 所有组件的监听交互事件
     */
    public abstract static class OnViewClickListener{
        public void onClickMenu(int position){}//底部菜单
        public void onBack(){}//关闭
    }
    private OnViewClickListener mOnViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    /**
     * 异常交互监听
     * @param listsner
     */
    public void setOnExceptionListener(OnExceptionListener listsner) {
        this.mExceptionListener = listsner;
    }

    /**
     * 清屏还原房间内所有状态
     */
    public void onReset() {
        stopReckonTime();
//        if(null!=mFragment){
//            mFragment.dismiss();
//            mFragment=null;
//        }
        if(null!=mQuireDialog) {
            mQuireDialog.dismiss();
            mQuireDialog=null;
        }
        if(null!=mHandler) mHandler.removeMessages(0);
//        if(null!=mGiftGroupManager) mGiftGroupManager.onReset();
//        if(null!=mAwardGroupManager) mAwardGroupManager.onReset();
//        if(null!=mCountdownGiftView) mCountdownGiftView.onReset();
//        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onReset();
//        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onReset();
        if(null!=mLoadingView) mLoadingView.hide();
    }
}