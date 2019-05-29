package com.yc.liaolive.live.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.RoomBaseController;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.NumberChangedInfo;
import com.yc.liaolive.bean.OlderExtra;
import com.yc.liaolive.bean.RoomTaskDataInfo;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.bean.VipListInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.common.ControllerConstant;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.gift.listener.AnimatorPlayListener;
import com.yc.liaolive.gift.listener.OnFunctionListener;
import com.yc.liaolive.gift.manager.RoomAwardGroupManager;
import com.yc.liaolive.gift.manager.RoomDanmuManager;
import com.yc.liaolive.gift.manager.RoomGiftGroupManager;
import com.yc.liaolive.gift.manager.RoomSuperAwardAnimatorGroupManager;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.gift.view.AnimatorSvgaPlayerManager;
import com.yc.liaolive.gift.view.CountdownGiftView;
import com.yc.liaolive.gift.view.RoomSuperAwardAnimaorView;
import com.yc.liaolive.live.adapter.LiveFansListAdapter;
import com.yc.liaolive.live.bean.AwardInfo;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.bean.RoomInitInfo;
import com.yc.liaolive.live.bean.VideoCallOlder;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.live.listener.OnExceptionListener;
import com.yc.liaolive.live.manager.GiftManager;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.manager.RoomTaskManager;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.live.ui.activity.RoomTaskActivity;
import com.yc.liaolive.live.ui.contract.LiveControllerInterface;
import com.yc.liaolive.live.ui.contract.LiveRoomControllerContract;
import com.yc.liaolive.live.ui.presenter.LiveRoomControllerPresenter;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.util.RoomDataCache;
import com.yc.liaolive.live.view.like.like.TCHeartLayout;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.BannerImageLoader;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.model.ItemSpacesItemDecoration;
import com.yc.liaolive.msg.ui.activity.ChatConversationActivity;
import com.yc.liaolive.pay.PayConfig;
import com.yc.liaolive.pay.PayUtils;
import com.yc.liaolive.pay.alipay.IPayCallback;
import com.yc.liaolive.pay.alipay.OrderInfo;
import com.yc.liaolive.pay.model.bean.CheckOrderBean;
import com.yc.liaolive.recharge.manager.BuyVipPresenter;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.activity.IntegralTopListActivity;
import com.yc.liaolive.ui.contract.BuyVipContract;
import com.yc.liaolive.ui.dialog.FirstChargeDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.dialog.RedAnimationDialog;
import com.yc.liaolive.ui.fragment.LiveUserDetailsFragment;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.MarqueeTextView;
import com.yc.liaolive.view.widget.PayWebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.IDanmakuView;
import rx.functions.Action1;

/**
 * TinyHung@Outlook.com
 * 2018/5/31
 * 直播房间 推、拉 控制器
 * 必须给定应用场景 mMode
 */

public class VideoLiveControllerView extends RoomBaseController implements View.OnClickListener, LiveRoomControllerContract.View, Observer, BuyVipContract.View, LiveControllerInterface {

    private static final String TAG = "VideoLiveControllerView";
    //功能交互的应用场景
    public static final int LIVE_SCENE_PULL = 0;//直播间 拉
    public static final int LIVE_SCENE_PUSH = 1;//直播间 推
    private LiveFansListAdapter mAvatarListAdapter;//在线观众
    private BrightConversationListView mConversationListView;//会话列表
    //主播端
    private long SECOND=0;//直播、观看时长
    private Timer mBroadcastTimer;//主播端计时器
    private BroadcastTimerTask mBroadcastTimerTask;//主播端计时器
    private ObjectAnimator mObjAnim;//直播状态中红点动画
    private long oldTotalPoint;//主播上一次的总积分
    private UserInfo mAnchorUserData;//主播的用户信息
    private TextView mOnlineNumber;//直播间在线人数
    private MarqueeTextView mAnchorName;//用户端显示主播昵称、主播端显示计时时间
    private LiveRoomControllerPresenter mPresenter;
    private int mIdentifyType= LIVE_SCENE_PULL;//应用场景，默认视频拉流
    private ImageView[] mBtnMenus;//底部菜单
    private int mIsFollow;//是否已关注此用户
    private QuireDialog mQuireDialog;
    private Handler mHandler;
    private AnimationSet mAnimationSet;//积分增加动画
    private TCHeartLayout mHeartLayout;//飘心
    private CountdownGiftView mCountdownGiftView;//连击赠送礼物
    private RoomGiftGroupManager mGiftGroupManager;//普通礼物
    private AnimatorSvgaPlayerManager mSvgaPlayerManager;//豪华礼物
    private RoomAwardGroupManager mAwardGroupManager;//中奖
    private VipUserEnterManager mVipUserEnterManager;//会员进场
    private RoomDanmuManager mDrawDanmuManager;//飘屏弹幕
    private RoomSuperAwardAnimatorGroupManager mAwardAnimatorPlayManager;//全屏超级大奖
    private LoadingIndicatorView mLoadingView;//加载中
    //直播间错误信息
    private RoomErrorLayout mRoomErrorLayout;
    private OnExceptionListener mExceptionListener;
    //支付相关
    private BuyVipPresenter mRechargePresenter;//充值
    private int payway;
    private PayWebView mPayWebView;
    //任务可领取动画
    private AnimationDrawable mAnimationDrawable;
    private View mEmptyView;
    private LiveGiftDialog mFragment;
    private AutoBannerLayout mBannerLayout;//直播间活动
    private View mTopBar;
    private View mBottobBar;

    public VideoLiveControllerView(Context context) {
        super(context);
        init(context,null);
    }

    public VideoLiveControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    /**
     * 显示、隐藏软键盘
     * @param flag 输入法是否显示
     * @param keyBordHeight 软键盘高度
     * 采坑：room_gift_manager；礼物展示空间高度挤压了屏幕的剩余空间，导致empty_view设置高度无效
     */
    @Override
    public void showInputKeyBord(boolean flag, int keyBordHeight) {
        if(null==mEmptyView) return;
        if(flag){
            if(null!=mGiftGroupManager) mGiftGroupManager.setVisibility(GONE);
            if(mEmptyView.getLayoutParams().height!=(keyBordHeight+30)){
                mEmptyView.getLayoutParams().height=(keyBordHeight+30);
            }
            mEmptyView.setVisibility(VISIBLE);//显示输入法将聊天列表顶上去
            AnimationUtil.goneTransparentView(mTopBar);//输入框弹起隐藏头部
        }else{
            if(mEmptyView.getVisibility()!=GONE) mEmptyView.setVisibility(GONE);//输入法隐藏占位布局不可见
            if(null!=mGiftGroupManager&&mGiftGroupManager.getVisibility()!=VISIBLE) mGiftGroupManager.setVisibility(VISIBLE);
            if(findViewById(R.id.tool_bar_view).getVisibility()==VISIBLE) return;
            AnimationUtil.visibTransparentView(mTopBar);//输入框消失显示头部
        }
    }

    /**
     * 系统发送的自定义消息、系统通知、礼物赠送、成员的进出场、聊天列表消息、中奖信息 等
     * @param customMsgInfo 消息的封装体
     * @param isSystemPro 是否来自系统推送
     */
    @Override
    public void newSystemCustomMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null==customMsgInfo||null==mAnchorUserData||null==customMsgInfo.getCmd()) return;
        for (String cmd : customMsgInfo.getCmd()) {
            customMsgInfo.setChildCmd(cmd);//聊天列表所需
            GiftInfo giftInfo = customMsgInfo.getGift();
            Logger.d(TAG, "---GROUP_MESSAGE---CMD:" + cmd
                    + ",MINE_GROUPID:" + LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID()
                    + ",SOURCE_GROUP_ID:" +(null== customMsgInfo.getGift()?"":customMsgInfo.getGift().getSource_room_id())+",CONTENT:"+customMsgInfo.getMsgContent());
            if(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID().equals(customMsgInfo.getAccapGroupID())){
                //如果总积分变化了
                if (customMsgInfo.getRoomTotalPoints() > 0 &&null!=giftInfo&&!TextUtils.isEmpty(giftInfo.getSource_room_id())
                        &&giftInfo.getSource_room_id().equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID())) {
                    if(null!=mAnchorUserData&&mAnchorUserData.getUserid().equals(customMsgInfo.getAccapUserID())){
                        //本群积分亲密度发生变化
                        long tempPoint=(0 == oldTotalPoint ? 0 : customMsgInfo.getRoomTotalPoints() - oldTotalPoint);
                        setIntegralData("亲密度:" + Utils.formatWan(customMsgInfo.getRoomTotalPoints(), true)+" >", tempPoint);
                        oldTotalPoint = customMsgInfo.getRoomTotalPoints();//更新总积分
                    }
                }
                //如果在线人数发生变化了
                if(customMsgInfo.getOnlineNumer() >0){
                    if(null!= mOnlineNumber) mOnlineNumber.setText(Utils.formatWan(customMsgInfo.getOnlineNumer(),true)+"人");
                }
                //自定义消息、系统消息、成员进场
                switch (cmd) {
                    case Constant.MSG_CUSTOM_TEXT://文本消息
                    case Constant.MSG_CUSTOM_NOTICE://系统公告
                    case Constant.MSG_CUSTOM_ADD_USER://新增用户
                    case Constant.MSG_CUSTOM_FOLLOW_ANCHOR://关注主播
                        boolean isShowEnter = true;
                        //主播端过滤机器人所有消息
                        if(getIdentifyType()==LIVE_SCENE_PUSH && Constant.USER_TYPE_ROBOT == customMsgInfo.getUser_type()){
                            isShowEnter = false;
                        }
                        //拦截来自网络的自己发送的消息和进入房间消息
                        if (isSystemPro && TextUtils.equals(UserManager.getInstance().getUserId(),customMsgInfo.getSendUserID())) {
                            isShowEnter = false;
                        }
                        if (isShowEnter && null != mConversationListView) {
                            mConversationListView.addConversation(customMsgInfo,isSystemPro);
                        }
                        //会员，飘心处理
                        if (cmd.equals(Constant.MSG_CUSTOM_ADD_USER)) {
                            if(isSystemPro&&UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID())){
                                return;
                            }
                            //过滤机器人进场
                            clickHeart();
                            if(customMsgInfo.getSendUserVIP()> 0&&null!=mVipUserEnterManager){
                                FansInfo fansInfo=new FansInfo();
                                fansInfo.setUserid(customMsgInfo.getSendUserID());
                                fansInfo.setVip(customMsgInfo.getSendUserVIP());
                                fansInfo.setNickname(customMsgInfo.getSendUserName());
                                fansInfo.setAvatar(customMsgInfo.getSendUserHead());
                                mVipUserEnterManager.addUserToTask(fansInfo);
                            }
                        }
                        continue;
                        //点赞消息
                    case Constant.MSG_CUSTOM_PRICE:
                        clickHeart();
                        continue;
                        //观众离开,人数发生了变化
                    case Constant.MSG_CUSTOM_REDUCE_USER:
                        if (null != mOnlineNumber)
                            mOnlineNumber.setText(Utils.formatWan(customMsgInfo.getOnlineNumer(),true)+"人");
                        continue;
                        //观众列表发生了变化
                    case Constant.MSG_CUSTOM_TOP_USER:
                        setAudienceList(customMsgInfo.getGift_member_top(), false);
                        continue;
                        //礼物信息
                    case Constant.MSG_CUSTOM_GIFT:
                        if(null!=giftInfo){
                            //过滤自己发送的礼物消息
                            if(isSystemPro && UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID())){
                                continue;
                            }
                            //主播端
                            if(getIdentifyType()==LIVE_SCENE_PUSH){
                                if(null!=mAnchorUserData&&TextUtils.equals(mAnchorUserData.getUserid(), UserManager.getInstance().getUserId())){
                                    if(TextUtils.equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),giftInfo.getSource_room_id())){
                                        //非幸运礼物提示
                                        if (1!=giftInfo.getGift_category()&&TextUtils.equals(UserManager.getInstance().getUserId(),customMsgInfo.getAccapUserID())&&null!=mConversationListView) {
                                            mConversationListView.addConversation(customMsgInfo,isSystemPro);
                                        }
                                        if (null != mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
                                        //豪华礼物
                                        if (null != giftInfo.getBigSvga() && giftInfo.getBigSvga().endsWith(".svga")) {
                                            if(null!=mSvgaPlayerManager) mSvgaPlayerManager.addAnimationToTask(customMsgInfo);
                                        }
                                    }
                                }
                                //用户端
                            }else{
                                if (null != mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
                                //豪华礼物
                                if (null != giftInfo.getBigSvga() && giftInfo.getBigSvga().endsWith(".svga")) {
                                    if(null!=mSvgaPlayerManager) mSvgaPlayerManager.addAnimationToTask(customMsgInfo);
                                }
                            }
                        }
                        continue;
                        //中奖信息
                    case Constant.MSG_CUSTOM_ROOM_DRAW:
                        if(null!=giftInfo){
                            // TODO: 2018/12/17 模拟超级大奖
//                                giftInfo.setPrize_level(3);
//                                giftInfo.setDrawTimes(500);
                            //自己中奖了
                            if(customMsgInfo.getSendUserID().equals(UserManager.getInstance().getUserId())){
                                UserManager.getInstance().setDiamonds(UserManager.getInstance().getDiamonds()+giftInfo.getDrawIntegral());
                                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED);
                                //标记用户中心需要刷新
                                VideoApplication.getInstance().setMineRefresh(true);
                            }
                            //主播端、用户端 都显示中奖公示信息
                            if(null!=mAwardGroupManager) mAwardGroupManager.addAwardItemToLayout(customMsgInfo);
                            //主播端
                            if(getIdentifyType()==LIVE_SCENE_PUSH){
                                if(null!=mAnchorUserData&&TextUtils.equals(mAnchorUserData.getUserid(), UserManager.getInstance().getUserId())){
                                    if(TextUtils.equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),giftInfo.getSource_room_id())){
                                        if(null!=mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
                                        if(giftInfo.getPrize_level()>=3){
                                            //飘屏
                                            if(null!=mDrawDanmuManager) mDrawDanmuManager.addRoomDanmu(customMsgInfo,RoomDanmuManager.DanmuType.AWARD);
                                            addAwardToTask(customMsgInfo,mAwardAnimatorPlayManager,UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID()));
                                        }
                                    }else{
                                        //其他房间只播放超级中奖动画
                                        if(giftInfo.getPrize_level()>=3){
                                            //飘屏
                                            if(null!=mDrawDanmuManager) mDrawDanmuManager.addRoomDanmu(customMsgInfo,RoomDanmuManager.DanmuType.AWARD);
                                            //超级大奖
                                            addAwardToTask(customMsgInfo,mAwardGroupManager.getAwardAnimatorMinPlayManager(),false);
                                        }
                                    }
                                }
                                //用户端
                            }else{
                                if(null!=mGiftGroupManager) mGiftGroupManager.addGiftAnimationItem(customMsgInfo);
                                //直播间中了超级大奖
                                if(giftInfo.getPrize_level()>=3){
                                    //所有房间弹幕飘屏
                                    if(null!=mDrawDanmuManager) mDrawDanmuManager.addRoomDanmu(customMsgInfo,RoomDanmuManager.DanmuType.AWARD);
                                    //本房间超级大奖动画
                                    if(TextUtils.equals(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),giftInfo.getSource_room_id())){
                                        addAwardToTask(customMsgInfo,mAwardAnimatorPlayManager,UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID()));
                                    }else{
                                        addAwardToTask(customMsgInfo,mAwardGroupManager.getAwardAnimatorMinPlayManager(),false);
                                    }
                                }
                            }
                            continue;
                        }
                }
            }
        }
    }

    /**
     * 群组纯文本消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    @Override
    public void onNewTextMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null!=customMsgInfo&&!TextUtils.isEmpty(customMsgInfo.getChildCmd())){
            if(null!=mConversationListView) mConversationListView.addConversation(customMsgInfo,isSystemPro);
        }
    }

    /**
     * 小型的迷你消息
     * @param groupId
     * @param sender
     * @param changedInfo 人数变化
     */
    @Override
    public void onNewMinMessage(String groupId, String sender, NumberChangedInfo changedInfo) {
        if(null==changedInfo) return;
        //在线人数更新
        if(null!= mOnlineNumber) mOnlineNumber.setText(Utils.formatWan(changedInfo.getOnlineNumer(),true)+"人");
    }

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
    public void startReckonTime(){
        stopReckonTime();
        if(mIdentifyType== LIVE_SCENE_PUSH){
            mObjAnim = ObjectAnimator.ofFloat(findViewById(R.id.view_live_reckon), "alpha", 1f, 0f, 1f);
            mObjAnim.setDuration(1000);
            mObjAnim.setRepeatCount(-1);
            mObjAnim.start();
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
    @Override
    public void stopReckonTime() {
        if(null!=mObjAnim) mObjAnim.cancel();mObjAnim=null;
        if(null!=mBroadcastTimer) mBroadcastTimer.cancel(); mBroadcastTimer=null;
        if(null!=mBroadcastTimerTask) mBroadcastTimerTask.cancel(); mBroadcastTimerTask=null;
    }

    @Override
    public void onResume() {
        if(null!=mDrawDanmuManager) mDrawDanmuManager.onResume();
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onResume();
        if(null!=mBannerLayout) mBannerLayout.onResume();
        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onResume();
        if(null!=mAwardGroupManager) mAwardGroupManager.onResume();
        //查询订单,H5支付下生效
        if(null!=mPayWebView){
            String olderSn = mPayWebView.getOlderSn();
            if(!TextUtils.isEmpty(olderSn)){
                VideoApplication.getInstance().setMineRefresh(true);
                if(null!=mRechargePresenter) {
                    mRechargePresenter.setCount(3);//还原查询次数
                    mRechargePresenter.checkOrder(olderSn);

                }
            }
        }
        UserManager.getInstance().getRoomTasks(new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object && object instanceof List){
                    List<RoomTaskDataInfo> roomTaskDataInfos= (List<RoomTaskDataInfo>) object;
                    boolean exitGetTask=false;
                    for (RoomTaskDataInfo task : roomTaskDataInfos) {
                        if(null!=task){
                            for (TaskInfo taskInfo : task.getList()) {
                                if(taskInfo.getComplete()==0&&0==taskInfo.getIs_get()){
                                    exitGetTask=true;
                                    break;
                                }
                            }
                        }
                    }
                    showTaskBtn(exitGetTask);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
            }
        });
    }

    @Override
    public void onPause() {
        if(null!=mDrawDanmuManager) mDrawDanmuManager.onPause();
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onPause();
        if(null!=mBannerLayout) mBannerLayout.onPause();
        if(null!=mVipUserEnterManager) mVipUserEnterManager.onDestroy();
        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onPause();
        if(null!=mAwardGroupManager) mAwardGroupManager.onPause();
    }

    /**
     * 伪onDestroy()，一定要调用
     */
    @Override
    public void onDestroy(){
        stopReckonTime();
        if(null!=mFragment) mFragment.dismiss();
        RoomDataCache.getInstance().onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        if(null != mRechargePresenter){
            mRechargePresenter.dissmis();
            mRechargePresenter.detachView();
        }
        if(null!=mConversationListView) mConversationListView.onDestroy();
        if(null != mPayWebView) mPayWebView.onDestroy();
        if(null!=mGiftGroupManager) mGiftGroupManager.onDestroy();
        if(null!=mAwardGroupManager) mAwardGroupManager.onDestroy();
        if(null!=mCountdownGiftView) mCountdownGiftView.onDestroy();
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onDestroy();
        if(null!=mVipUserEnterManager) mVipUserEnterManager.onDestroy();
        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onDestroy();
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        if(null!=mBannerLayout) mBannerLayout.onDestroy();
        if(null!=mLoadingView) mLoadingView.hide();
        if(null != mPresenter) mPresenter.detachView();
        if(null!=mAvatarListAdapter) mAvatarListAdapter.setNewData(null);
        if(null!=mQuireDialog&&mQuireDialog.isShowing()) mQuireDialog.dismiss();
        if(null!=mHandler) mHandler.removeMessages(0);
        mBtnMenus=null;mAnimationDrawable=null;
        mOnViewClickListener=null; mHandler=null;mFragment=null;
        mAvatarListAdapter=null; mQuireDialog=null;mPresenter = null;
        mCountdownGiftView=null; mPayWebView=null;mRechargePresenter=null;
        mSvgaPlayerManager=null;mGiftGroupManager=null;mVipUserEnterManager=null;
        mAwardAnimatorPlayManager=null;mAwardGroupManager=null;mConversationListView=null;
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        long millis = System.currentTimeMillis();
        View.inflate(context, R.layout.view_live_controller_layout,this);
        //Tab
        mBtnMenus =new ImageView[]{
                ((ImageView) findViewById(R.id.view_btn_menu0)),
                ((ImageView) findViewById(R.id.view_btn_menu1)),
                ((ImageView) findViewById(R.id.view_btn_menu2)),
                ((ImageView) findViewById(R.id.view_btn_menu3)),
                ((ImageView) findViewById(R.id.view_btn_menu4)),
                ((ImageView) findViewById(R.id.view_btn_menu5)),
                ((ImageView) findViewById(R.id.view_btn_menu6)),
                ((ImageView) findViewById(R.id.view_btn_menu7)),
                ((ImageView) findViewById(R.id.view_btn_menu8)),
        };
        //设置点击事件
        for (int i = 0; i < mBtnMenus.length; i++) {
            mBtnMenus[i].setOnClickListener(this);
        }
        mTopBar = findViewById(R.id.tool_bar_view);
        mBottobBar = findViewById(R.id.tool_bottom_bar);
        findViewById(R.id.view_anchor_head).setOnClickListener(this);//头像
        findViewById(R.id.view_add_follow).setOnClickListener(this);//关注
        findViewById(R.id.view_integral).setOnClickListener(this);//积分榜
        findViewById(R.id.view_btn_close).setOnClickListener(this);//关闭
        RoomDataCache.getInstance().registerHostView(this);
        //房间内会话列表
        mConversationListView = (BrightConversationListView) findViewById(R.id.view_bright_conversation);
        mConversationListView.initConversation();
        mConversationListView.setConversationFunctionListener(new BrightConversationListView.OnConversationFunctionListener() {
            @Override
            public void onSendGift(PusherInfo pusherInfo) {
                if(null!=pusherInfo){
                    showGiftBoardView(pusherInfo);
                }
            }
        });
        //初始化在线观众列表
        initMemberAdapter();
        //飘心
        mHeartLayout = (TCHeartLayout) findViewById(R.id.heart_layout);
        //普通礼物
        mGiftGroupManager = (RoomGiftGroupManager) findViewById(R.id.room_gift_manager);
        mGiftGroupManager.setOnFunctionListener(new OnFunctionListener() {
            @Override
            public void onSendGift(FansInfo userInfo) {
                //接收对象
                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
            }
        });
        //会员进场
        mVipUserEnterManager = (VipUserEnterManager) findViewById(R.id.view_vip_enter);
        mVipUserEnterManager.setOnFunctionListener(new OnFunctionListener() {
            @Override
            public void onSendGift(FansInfo userInfo) {
                //接收对象
                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
            }
        });
        //中奖信息展示
        mAwardGroupManager = (RoomAwardGroupManager) findViewById(R.id.room_award_manager);
        mAwardGroupManager.setOnFunctionListener(new OnFunctionListener() {
            @Override
            public void onSendGift(FansInfo userInfo) {
                //接收对象
                showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
            }
        });
        //超级大奖
        mAwardAnimatorPlayManager = (RoomSuperAwardAnimatorGroupManager) findViewById(R.id.award_animator_manager);
        mAwardAnimatorPlayManager.setWindowMode(RoomSuperAwardAnimaorView.WindownMode.FULL);
        mAwardAnimatorPlayManager.setAutoCleanMillis(10000);//动画延续时长，毫秒
        mAwardAnimatorPlayManager.setAnimatorPlayListener(new AnimatorPlayListener() {
            @Override
            public void onStart(AwardInfo awardInfo) {
                if(null!=awardInfo&&awardInfo.isMine()){
                    if(null!=mCountdownGiftView&&!mCountdownGiftView.isRunning()){
                        //如果是自己中奖并且倒计时已经消失了
                        GiftInfo giftInfo=new GiftInfo();
                        giftInfo.setId(awardInfo.getId());
                        giftInfo.setTitle(awardInfo.getTitle());
                        giftInfo.setSrc(awardInfo.getSrc());
                        giftInfo.setPrice(awardInfo.getPrice());
                        //接收人信息
                        PusherInfo pusherInfo=new PusherInfo();
                        pusherInfo.setUserID(awardInfo.getAccaptUserID());
                        mCountdownGiftView.updataView(giftInfo,LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),awardInfo.getCount(),pusherInfo);
                    }
                }
            }

            @Override
            public void onEnd() {

            }
        });
        //奢侈礼物、中奖飘屏弹幕
        IDanmakuView drawDanmakuView = (IDanmakuView) findViewById(R.id.draw_danmakuView);
        mDrawDanmuManager = new RoomDanmuManager(getContext());
        mDrawDanmuManager.bindDanmakuView(drawDanmakuView);
        //奢侈礼物动画
        mSvgaPlayerManager = (AnimatorSvgaPlayerManager) findViewById(R.id.svga_animator);
        mSvgaPlayerManager.bindDanmakuView(mDrawDanmuManager);
        //活动轮播广告
        mBannerLayout = (AutoBannerLayout) findViewById(R.id.item_banner);
        mBannerLayout.setImageLoader(new BannerImageLoader()).setAutoRoll(true).showIndicator(false).setOnItemClickListener(new AutoBannerLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(null!=mBannerLayout.getTag()&& mBannerLayout.getTag() instanceof List){
                    List<BannerInfo> taskInfos = (List<BannerInfo>) mBannerLayout.getTag();
                    if(taskInfos.size()>position){
                        executeTask(taskInfos.get(position));
                    }
                }
            }
        });
        //异常情况
        mRoomErrorLayout = (RoomErrorLayout) findViewById(R.id.error_layout);
        mRoomErrorLayout.setOnExceptionListener(new OnExceptionListener() {
            @Override
            public void onTautology() {
                if(null!=mExceptionListener) mExceptionListener.onTautology();
            }
        });
        //H5支付
        mPayWebView = (PayWebView) findViewById(R.id.pay_web_view);
        //缓冲中
        mLoadingView = (LoadingIndicatorView) findViewById(R.id.loading_view);
        //H5支付意图监听
        mPayWebView.setOnFunctionListener(new PayWebView.OnFunctionListener() {
            @Override
            public void weXinPay(String url) {
                openWxpay(url);
            }

            @Override
            public void aliPay(String url) {
                openAlipay(url);
            }
        });
        mPresenter = new LiveRoomControllerPresenter();
        mPresenter.attachView(this);
        mHandler = new Handler(Looper.getMainLooper());
        //积分增加动画
        mAnimationSet = (AnimationSet) AnimationUtils.loadAnimation(getContext(), R.anim.gift_integra_ani);
        //连击赠送礼物
        mCountdownGiftView = (CountdownGiftView) findViewById(R.id.view_countdown_view);
        mCountdownGiftView.setOnGiftSendListener(new CountdownGiftView.OnCountdownGiftSendListener() {
            @Override
            public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                //本地播放礼物动画，远程端交由服务器推送
                CustomMsgExtra customMsgExtra = new CustomMsgExtra();
                customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
                if(null!=accepUserInfo){
                    customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
                    customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
                    customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
                }
                data.setCount(count);
                data.setSource_room_id(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
                customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                newSystemCustomMessage(customMsgInfo,false);
            }
        });
        mRechargePresenter = new BuyVipPresenter(((Activity) getContext()));
        mRechargePresenter.attachView(this);
        mEmptyView = findViewById(R.id.empty_view);
        ApplicationManager.getInstance().addObserver(this);
        GiftManager.getInstance().setShowWhiteTips("");
        long millis1 = System.currentTimeMillis();
        Logger.d(TAG,"初始化耗时："+(millis1-millis));
    }

    /**
     * 绑定用户的身份场景
     * @param type  0：观众拉流  1：主播推流
     */
    public void setIdentityType(int type){
        this.mIdentifyType=type;
        if(null!=mCountdownGiftView) mCountdownGiftView.setApiMode(LiveGiftDialog.GIFT_MODE_ROOM);
        if(null!=mConversationListView) mConversationListView.setIdentityType(type);
        switch (type) {
            //直播间 拉
            case LIVE_SCENE_PULL:
                mBtnMenus[0].setImageResource(R.drawable.btn_live_msg);//群聊
                mBtnMenus[1].setImageResource(R.drawable.btn_live_private_chat);//私信
                mBtnMenus[2].setImageResource(R.drawable.task_anim);//任务
                mBtnMenus[3].setImageResource(0);//占位
                mBtnMenus[4].setImageResource(R.drawable.room_gift_icon);//礼物
                mBtnMenus[5].setImageResource(0);//占位
                mBtnMenus[6].setImageResource(0);//占位
                mBtnMenus[7].setImageResource(0);//占位
                mBtnMenus[8].setImageResource(0);//占位
                findViewById(R.id.view_live_reckon).setVisibility(INVISIBLE);
                return;
            //直播间 推
            case LIVE_SCENE_PUSH:
                mBtnMenus[0].setImageResource(R.drawable.btn_live_msg);//群聊
                mBtnMenus[1].setImageResource(R.drawable.btn_live_private_chat);//私信
                mBtnMenus[2].setImageResource(0);//占位
                mBtnMenus[3].setImageResource(0);//占位
                mBtnMenus[4].setImageResource(R.drawable.room_anchor_fair);//美颜
                mBtnMenus[5].setImageResource(0);//占位
                mBtnMenus[6].setImageResource(0);//占位
                mBtnMenus[7].setImageResource(R.drawable.btn_live_flash_switch);//闪光灯
                mBtnMenus[8].setImageResource(R.drawable.btn_live_camera_switch);//照相机切换
                if(null!=mTopBar) mTopBar.setVisibility(GONE);
                findViewById(R.id.view_live_reckon).setVisibility(VISIBLE);
                return;
        }
        if(null!=mAwardGroupManager) mAwardGroupManager.setIdentityType(mIdentifyType);
        if(null!=mVipUserEnterManager) mVipUserEnterManager.setIdentityType(mIdentifyType);
    }

    /**
     * 初始化在线观众列表
     */
    private void initMemberAdapter() {
        FrameLayout userContent = (FrameLayout) findViewById(R.id.fans_root_view);
        if(null==userContent) return;
        userContent.removeAllViews();
        mAvatarListAdapter=null;
        RecyclerView recyclerView=new RecyclerView(getContext());
        FrameLayout.LayoutParams layoutParams=new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setHorizontalFadingEdgeEnabled(true);
        recyclerView.setFadingEdgeLength(ScreenUtils.dpToPxInt(20f));
        ScrollSpeedLinearLayoutManger linearLayoutManger=new ScrollSpeedLinearLayoutManger(getContext());
        linearLayoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManger);
        recyclerView.addItemDecoration(new ItemSpacesItemDecoration(ScreenUtils.dpToPxInt(8f)));
        mAvatarListAdapter = new LiveFansListAdapter(null);
        mAvatarListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    FansInfo userInfo = (FansInfo) view.getTag();
                    try {
                        LiveUserDetailsFragment.newInstance(userInfo,mIdentifyType,
                                LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID()).setOnFunctionClickListener(
                                new LiveUserDetailsFragment.OnFunctionClickListener() {
                                    //送礼物
                                    @Override
                                    public void onSendGift(FansInfo userInfo) {
                                        //接收对象
                                        showGiftBoardView(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
                                    }
                                }).show(((FragmentActivity) getContext()).getSupportFragmentManager(),"userinfo");
                    }catch (RuntimeException e){

                    }catch (Exception e){

                    }
                }
            }
        });
        recyclerView.setAdapter(mAvatarListAdapter);
        userContent.addView(recyclerView);
    }

    /**
     * 在线成员列表重生
     */
    @Override
    public void onMemberReset() {
        initMemberAdapter();
    }

    //========================================直播间任务相关==========================================
    /**
     * 观众端检查自己是否有任务可领取
     */
    public void checkedTaskAward(){
        if(mIdentifyType==LIVE_SCENE_PULL){
            UserManager.getInstance().getRoomTasks(new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if(null!=object && object instanceof List){
                        List<RoomTaskDataInfo> roomTaskDataInfos= (List<RoomTaskDataInfo>) object;
                        boolean exitGetTask=false;
                        for (RoomTaskDataInfo task : roomTaskDataInfos) {
                            if(null!=task.getList()){
                                for (TaskInfo taskInfo : task.getList()) {
                                    if(0==taskInfo.getIs_get()){
                                        exitGetTask=true;
                                        break;
                                    }
                                }
                            }
                        }
                        showTaskBtn(exitGetTask);
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {
                }
            });
        }
    }

    /**
     * 改变任务领取状态
     * @param exitGetTask 是否存在已完成但未领取的任务
     */
    public void showTaskBtn(boolean exitGetTask) {
        if(mIdentifyType==LIVE_SCENE_PULL){
            if(null==mAnimationDrawable){
                ImageView imageView = (ImageView) findViewById(R.id.view_btn_menu2);
                mAnimationDrawable= (AnimationDrawable) imageView.getDrawable();
            }
            if(exitGetTask){
                if(!mAnimationDrawable.isRunning())mAnimationDrawable.start();
            }else{
                if(mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
                mAnimationDrawable.selectDrawable(0);//停留至第一帧
            }
        }
    }

    /**
     * 检查Banner任务
     */
    public void checkedBannerTask() {
        if(null==mBannerLayout) return;
        if(getIdentifyType()== LIVE_SCENE_PULL){
            UserManager.getInstance().getRoomPopupTasks(new UserServerContract.OnNetCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    if(null!=object&& object instanceof List){
                        List<BannerInfo> bannerInfoLits= (List<BannerInfo>) object;
                        updataTaskBanners(bannerInfoLits);
                    }
                }

                @Override
                public void onFailure(int code, String errorMsg) {

                }
            });
        }
    }

    private void updataTaskBanners(List<BannerInfo> data) {
        if(null!=mBannerLayout){
            if(null!=data&&data.size()>0){
                List<String> roomTasks=new ArrayList<>();
                for (BannerInfo roomTask : data) {
                    roomTasks.add(roomTask.getImg());
                }
                mBannerLayout.setLayoutParams(ScreenUtils.dpToPxInt(98f),data.get(0).getWidth(),data.get(0).getHeight()).setData(roomTasks).setTag(data);
            }else{
                mBannerLayout.setData(null).setTag(null);
            }
        }
    }

    /**
     * 直播间任务触发
     * @param taskInfo
     */
    private void executeTask(BannerInfo taskInfo) {
        if(null==taskInfo) return;
        //会员任务
        if(BannerInfo.TASK_ACTION_VIP==taskInfo.getTaskid()){
            if(null!=getContext())  VipActivity.startForResult(((Activity) getContext()),1);
        //每日首充
        }else if(BannerInfo.APP_TASK_FIRST_RECHGRE==taskInfo.getTaskid()){
            FirstChargeDialog.getInstance(((Activity) getContext())).setOnSelectedListener(new FirstChargeDialog.OnSelectedListener() {
                @Override
                public void onSelected(int payType) {
                    createRecharge(payType);
                }
            }).show();
            //绑定手机号
        }else if(BannerInfo.TASK_ACTION_BIND_PHONE==taskInfo.getTaskid()){
            CaoliaoController.startActivity(ControllerConstant.BindPhoneTaskActivity);
        }
    }

    /**
     * 领取任务奖励
     * @param taskInfo
     */
    private void drawTaskAward(TaskInfo taskInfo) {
        UserManager.getInstance().drawTaskAward(taskInfo, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=mBannerLayout) mBannerLayout.setTag(null);
                //首充任务已完成
                if(null!=getContext()) FirstChargeDialog.getInstance(((Activity) getContext()),1).show();
                checkedBannerTask();//检查最新的任务
            }

            @Override
            public void onFailure(int code, String errorMsg) {}
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_btn_menu0:
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(0);
                break;
            //直播间拉流，拦截 消息点击事件
            case R.id.view_btn_menu1:
                ChatConversationActivity.start(AppEngine.getApplication().getApplicationContext());
                break;
            //直播推流是镜像
            case R.id.view_btn_menu2:
                if(mIdentifyType== LIVE_SCENE_PULL){
                    if(null!=getContext()) {
                        MobclickAgent.onEvent(getContext(), "click_room_task");
                        RoomTaskManager.getInstance().startTask(AppEngine.getApplication().getApplicationContext()).subscribe(new Action1<Integer>() {
                            @Override
                            public void call(final Integer integer) {
                                RoomTaskManager.getInstance().onDestroy();
                                if(RoomTaskActivity.ACTION_GIFT==integer){
                                    if(null!=mAnchorUserData){
                                        PusherInfo pusherInfo=new PusherInfo();
                                        pusherInfo.setUserName(mAnchorUserData.getNickname());
                                        pusherInfo.setUserID(mAnchorUserData.getUserid());
                                        pusherInfo.setUserAvatar(mAnchorUserData.getAvatar());
                                        showGiftBoardView(pusherInfo);
                                    }
                                }else if(RoomTaskActivity.ACTION_SHARE==integer){
                                    if(null!=getContext()){
                                        if(getContext() instanceof LiveRoomPullActivity){
                                            LiveRoomPullActivity activity= (LiveRoomPullActivity) getContext();
                                            activity.onShare();
                                        }
                                    }
                                }else if(integer>0){
                                    if(null!=getContext()){
                                        //检测任务
                                        checkedTaskAward();
                                        RedAnimationDialog.getInstance(((Activity) getContext()),integer).show();
                                    }
                                }
                            }
                        });
                    }
                    return;
                }
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(2);
                break;
            case R.id.view_btn_menu4:
                if(null!=mAnchorUserData&&null!=getContext()&&mIdentifyType== LIVE_SCENE_PULL){
                    if(null!=getContext()) MobclickAgent.onEvent(getContext(), "click_gift");
                    PusherInfo pusherInfo=new PusherInfo();
                    pusherInfo.setUserName(mAnchorUserData.getNickname());
                    pusherInfo.setUserID(mAnchorUserData.getUserid());
                    pusherInfo.setUserAvatar(mAnchorUserData.getAvatar());
                    showGiftBoardView(pusherInfo);
                    return;
                }
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(4);
                break;
            case R.id.view_btn_menu5:
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(5);
                break;
            case R.id.view_btn_menu6:
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(6);
                break;
            case R.id.view_btn_menu7:
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(7);
                break;
            case R.id.view_btn_menu8:
                if(null!=mOnViewClickListener) mOnViewClickListener.onClickMenu(8);
                break;
            //头像
            case R.id.view_anchor_head:
                if(null!=mAnchorUserData&&null!=getContext()){
                    FansInfo fansInfo=new FansInfo();
                    fansInfo.setUserid(mAnchorUserData.getUserid());
                    fansInfo.setNickname(mAnchorUserData.getNickname());
                    fansInfo.setVip(mAnchorUserData.getVip());
                    fansInfo.setSex(mAnchorUserData.getSex());
                    LiveUserDetailsFragment.newInstance(fansInfo,
                            mIdentifyType,
                            LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),
                            mAnchorUserData.getUserid())
                            .setOnFunctionClickListener(new LiveUserDetailsFragment.OnFunctionClickListener() {
                        @Override
                        public void onSendGift(FansInfo userInfo) {
                            PusherInfo pusherInfo=new PusherInfo();
                            pusherInfo.setUserName(userInfo.getNickname());
                            pusherInfo.setUserID(userInfo.getUserid());
                            pusherInfo.setUserAvatar(userInfo.getAvatar());
                            showGiftBoardView(pusherInfo);
                        }

                        @Override
                        public void onFollowChanged(int status) {
                            super.onFollowChanged(status);
                            //是否已经关注此用户
                            updataFollowState(status,true);
                        }
                    }).show(((FragmentActivity) getContext()).getSupportFragmentManager(),"userinfo");
                }
                break;
            //关注
            case R.id.view_add_follow:
                if(null==mAnchorUserData) return;
                UserManager.getInstance().followUser(mAnchorUserData.getUserid(), mIsFollow==0?1:0, new UserServerContract.OnNetCallBackListener() {
                    @Override
                    public void onSuccess(Object object) {
                        mIsFollow=(mIsFollow==0?1:0);
                        VideoApplication.getInstance().setMineRefresh(true);
                        ToastUtils.showCenterToast("关注成功");
                        updataFollowState(mIsFollow,true);
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        ToastUtils.showCenterToast(errorMsg);
                    }
                });
                break;
            //积分榜
            case R.id.view_integral:
                if(null!=mAnchorUserData){
                    IntegralTopListActivity.start(getContext(),mAnchorUserData.getUserid());
                }
                break;
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
    public void setConntrollerAlpha(float alpha) {
        if(null!=mTopBar) mTopBar.setAlpha(alpha);
        if(null!=mBottobBar) mBottobBar.setAlpha(alpha);
    }

    /**
     * 美颜是否打开
     * @param beautyEnable 美颜是否开关
     * @param tabIndex 按钮所在的位置
     */
    public void onBeautyEnable(boolean beautyEnable,int tabIndex) {
        if(null!=mBtnMenus&&mBtnMenus.length>tabIndex){
            mBtnMenus[tabIndex].setImageResource(beautyEnable?R.drawable.video_call_beauty_true:R.drawable.video_call_beauty);
        }
    }

    /**
     * 触发飘心
     */
    public void clickHeart() {
        if(null!=mHeartLayout) mHeartLayout.addFavor();
    }

    /**
     * 仅当第一次进入直播间,自动显示礼物面板
     */
    public void showGiftBoardView() {
        if(null==mAnchorUserData) return;
        PusherInfo pusherInfo=new PusherInfo();
        pusherInfo.setUserName(mAnchorUserData.getNickname());
        pusherInfo.setUserID(mAnchorUserData.getUserid());
        pusherInfo.setUserAvatar(mAnchorUserData.getAvatar());
        showGiftBoardView(pusherInfo);
    }

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

    /**
     * 开始动画播放处理线程
     */
    public void startPlayTask() {
        if(null!=mGiftGroupManager) mGiftGroupManager.startPlayTask();
        if(null!=mAwardGroupManager) mAwardGroupManager.startPlayTask();
    }

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
        findViewById(R.id.tool_bottom_bar).setVisibility(VISIBLE);
        if(null!=mTopBar) mTopBar.setVisibility(VISIBLE);
        startPlayTask();
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
                Logger.d(TAG,"--initData--主播端初始化");
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
                Logger.d(TAG,"--initData--用户端初始化");
                mPresenter.roomInit(mAnchorUserData.getUserid(),roomID);
            }
        }
    }

    /**
     * 更新在线列表
     * 自己永远在最前面
     * @param data 观众列表
     * @param isImRefresh 是否需要即时刷新
     */
    public synchronized void setAudienceList(List<FansInfo> data, boolean isImRefresh) {
        if(isImRefresh){
            if(null!=mAvatarListAdapter) mAvatarListAdapter.setNewData(data);
            return;
        }
        RoomDataCache.getInstance().putOnlineToCache(data);
    }


    /**
     * 更新是否已关注
     * @param state
     * @param isBroadcast 是否广播通知
     */
    public void updataFollowState(int state,boolean isBroadcast) {
        this.mIsFollow=state;
        if(1==mIsFollow&&isBroadcast){
            //发送一条群消息
            if(null!=mAnchorUserData&&null!=getContext()){
                CustomMsgExtra customMsgExtra=new CustomMsgExtra();
                customMsgExtra.setCmd(Constant.MSG_CUSTOM_FOLLOW_ANCHOR);
                customMsgExtra.setMsgContent("关注了主播");
                customMsgExtra.setTanmu(false);
                CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, null);
                customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                if(getContext() instanceof LiveRoomPullActivity){
                    LiveRoomPullActivity activity = (LiveRoomPullActivity) getContext();
                    activity.sendFollowMsg(customMsgInfo);
                }
            }
        }
        if(getIdentifyType()==LIVE_SCENE_PULL){
            ImageView followImage = (ImageView) findViewById(R.id.view_add_follow);
            followImage.setImageResource(1==mIsFollow?0:R.drawable.view_btn_follow);
        }
    }

    /**
     * 设置头部数据
     * @param anchorNam 主播昵称
     * @param onlineNumber 在线人数
     * @param headUrl 用户头像
     */
    public void setHeadData(String anchorNam,String onlineNumber,String headUrl){
        //主播昵称
        if(null==mAnchorName) mAnchorName = (MarqueeTextView) findViewById(R.id.view_anchor_name);
        mAnchorName.setText(anchorNam);
        if(null== mOnlineNumber) mOnlineNumber = (TextView) findViewById(R.id.view_online_number);//直播间在线人数
        mOnlineNumber.setText(Utils.formatWan(Long.parseLong(onlineNumber),true)+"人");
        //用户端显示的今日日期
        if(getIdentifyType()==LIVE_SCENE_PULL){
            TextView durtion = (TextView) findViewById(R.id.view_live_time);
            durtion.setBackgroundResource(R.drawable.full_room_user_data_bg);
            durtion.setText(DateUtil.durtionFormatDian(System.currentTimeMillis()));
        }

        if(null!=headUrl){
            Glide.with(getContext())
                    .load(headUrl)
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getContext()))
                    .into(((ImageView) findViewById(R.id.view_anchor_head)));
        }
    }

    /**
     * 设置积分信息，每次更新值大于0，执行积分增加动画
     * @param data
     * @param addIntegral 本次增加的积分变化
     */
    public void setIntegralData(String data,long addIntegral) {
        TextView viewIntegral = (TextView) findViewById(R.id.view_integral);
        if(null!=viewIntegral) viewIntegral.setText(data);
        final TextView integralAni = (TextView) findViewById(R.id.view_integral_ani);
        if(addIntegral>0){
            integralAni.setText("+"+addIntegral);
            if(null!=mAnimationSet){
                mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        integralAni.setText("");
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            integralAni.startAnimation(mAnimationSet);
        }
    }


    /**
     * 显示礼物面板
     * 赠送礼物，本地负责先播放动画，远程端交由后台去推送礼物赠送消息
     * @param pusherInfo
     */
    private void showGiftBoardView(PusherInfo pusherInfo) {
        if(null==getContext()) return;
        FragmentActivity context = (FragmentActivity) getContext();
        if(null== mAnchorUserData)  return;
        if(!context.isFinishing()){
            mFragment = LiveGiftDialog.getInstance(context,pusherInfo,
                    LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),
                    LiveGiftDialog.GIFT_MODE_ROOM,true,-1);
            mFragment.setOnGiftSelectedListener(new LiveGiftDialog.OnGiftSelectedListener() {
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
                    data.setSource_room_id(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                    CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
                    customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
                    newSystemCustomMessage(customMsgInfo,false);
                }

                @Override
                public void onDissmiss() {
                    super.onDissmiss();
                    mFragment=null;
                }

                //礼物选择发生了变化
                @Override
                public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo) {
                    if(null!=mCountdownGiftView) mCountdownGiftView.updataView(giftInfo,LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID(),count,accepUserInfo);
                }
            });
            mFragment.show();
        }
    }

    /**
     * 添加超级中奖信息至队列
     * @param customMsgInfo
     * @param animatorPlayManager
     * @param isMine
     */
    private void addAwardToTask(CustomMsgInfo customMsgInfo, RoomSuperAwardAnimatorGroupManager animatorPlayManager, boolean isMine) {
        if(null==customMsgInfo||null==animatorPlayManager) return;
        if(null==customMsgInfo.getGift()) return;
        GiftInfo giftInfo = customMsgInfo.getGift();
        //超级大奖
        AwardInfo awardInfo=new  AwardInfo();
        awardInfo.setNickName(customMsgInfo.getSendUserName());
        awardInfo.setUserid(customMsgInfo.getSendUserID());
        awardInfo.setMonery(giftInfo.getDrawIntegral());
        awardInfo.setMine(UserManager.getInstance().getUserId().equals(customMsgInfo.getSendUserID()));//是否是自己中奖了
        awardInfo.setCount(1);
        awardInfo.setId(giftInfo.getId());
        awardInfo.setPrice(giftInfo.getPrice());
        awardInfo.setTitle(giftInfo.getTitle());
        awardInfo.setSrc(giftInfo.getSrc());
        awardInfo.setMine(isMine);
        awardInfo.setAccaptUserID(customMsgInfo.getAccapUserID());
        if(null!=animatorPlayManager) animatorPlayManager.addAwardToTask(awardInfo);
    }

    //=============================================其他业务==========================================

    /**
     * 准备充值
     * @param payType
     */
    private void createRecharge(int payType) {
        if(null!=mRechargePresenter){
            payway=payType;
            RechargeGoodsInfo rechargeGoodsInfo=new RechargeGoodsInfo();
            rechargeGoodsInfo.setId(3);
            rechargeGoodsInfo.setName("每日充值");
            rechargeGoodsInfo.setPrice("30.00");
            List<OlderExtra> olderExtras = new ArrayList<>();
            OlderExtra extra = new OlderExtra();
            extra.setGood_id(String.valueOf(3));
            extra.setNum(1);
            olderExtras.add(extra);
            String s1 = new Gson().toJson(olderExtras, new TypeToken<List<OlderExtra>>(){}.getType());
            mRechargePresenter.createOrder(0 == payType ? PayConfig.ali_pay : PayConfig.wx_pay, s1, rechargeGoodsInfo);
        }
    }



    /**
     * H5微信支付
     * @param url
     */
    protected void openWxpay(String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
            String content="H5微信支付唤起微信客户端失败，可能原因：未安装微信客户端、系统未识别到Scheme头协议，errorCode:"+0+",errorMsg:"+e.getMessage()+",appSign:"+appSign;
            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
        }
    }

    /**
     * H5支付宝支付
     * @param url
     */
    protected void openAlipay(String url) {
        try {
            Intent intent;
            intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setComponent(null);
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
            String content="H5支付宝支付唤起支付宝客户端失败，可能原因：未安支付宝客户端、系统未识别到Scheme头协议，errorCode:"+0+",errorMsg:"+e.getMessage()+",appSign:"+appSign;
            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
        }
    }

    //=========================================网络数据交互回调=======================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    /**
     * 自动续费成功
     * @param data
     */
    @Override
    public void showSettlementResult(VideoCallOlder data) {

    }

    @Override
    public void showSettlementError(int code, String data) {

    }

    /**
     * 房间初始化信息回调
     object * @param data
     */
    @Override
    public void showInitResult(RoomInitInfo data) {
        //房间初始化状态更新
        if(null!=data.getRoominfo()){
            VideoLiveControllerView.this.mIsFollow= data.getRoominfo().getAttent();
            updataFollowState(mIsFollow,false);
            setIntegralData("亲密度:"+ Utils.formatWan(data.getRoominfo().getTotal_jifen(),true)+" >",0);
            if(null!= mOnlineNumber) mOnlineNumber.setText(Utils.formatWan(data.getRoominfo().getOnline_num(),true)+"人");
        }
        //在线人数更新
        if(null!=data.getMembers()){
            setAudienceList(data.getMembers(),true);
        }
        //直播间Banner任务
        updataTaskBanners(data.getPopup_page());
    }

    @Override
    public void showInitResultError(int code, String data) {}

    @Override
    public void showOrderSuccess(OrderInfo order, String rechargeGoodsInfo) {
        if (order.getPayway_info() != null && 2 == order.getPayway_info().getTrade_type()) {
            //H5支付
            if(null != mPayWebView) {
                mPayWebView.starPlay(order.getCharge_order_sn(),order.getPayurl(), order.getPayway_info().getAuth_domain());
            }
        } else  if(!TextUtils.isEmpty(order.getPayurl()) && !order.getPayurl().startsWith("alipay_sdk")){
            //微信支付
            if(order.getPayurl().startsWith("weixin://")){
                openWxpay(order.getPayurl());
                if(null!=mPayWebView) mPayWebView.setOlderSn(order.getCharge_order_sn());

            }
        } else {
            if(null!=mPayWebView) mPayWebView.setOlderSn("");
            PayUtils.getInstance().get(((Activity) getContext())).pay(payway, order, new IPayCallback() {
                @Override
                public void onSuccess(OrderInfo orderInfo) {
                    VideoApplication.getInstance().setMineRefresh(true);
                    if(null!=mRechargePresenter) mRechargePresenter.checkOrder(orderInfo.getCharge_order_sn());
                }

                @Override
                public void onFailure(OrderInfo orderInfo) {
                    if(null!=mRechargePresenter) mRechargePresenter.dissmis();
                }

                @Override
                public void onCancel(OrderInfo orderInfo) {
                    if(null!=mRechargePresenter) mRechargePresenter.dissmis();
                }
            });
        }
    }

    @Override
    public void showCreateOlderError(int code, String errorMsg) {
        ToastUtils.showCenterToast(errorMsg);
    }

    @Override
    public void showCantPayError(int code, String msg) {
        QuireDialog.getInstance(((Activity) getContext())).showCloseBtn(false)
                .showTitle(false)
                .setSubmitTitleText("确定")
                .setContentText(msg)
                .setCancelTitleVisible(View.GONE)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        super.onConsent();
                        if(null!=mRechargePresenter) mRechargePresenter.dissmis();
                    }
                }).setDialogCanceledOnTouchOutside(false).setDialogCancelable(false).show();
    }

    @Override
    public void showRechardeResult(CheckOrderBean data) {
        ToastUtils.showCenterToast("交易成功");
        if(null!=mRechargePresenter) mRechargePresenter.dissmis();
        if(null==mBannerLayout) return;
        TaskInfo tag = (TaskInfo) mBannerLayout.getTag();
        if(null==tag) return;
        //自动任务领取
        drawTaskAward(tag);
    }

    @Override
    public void hide() {}
    @Override
    public void showNoNet() {}
    @Override
    public void showLoading() {}
    @Override
    public void showNoData() {}
    @Override
    public void showRechardeError(int code, String msg) {}
    @Override
    public void showVipLits(VipListInfo vipListInfo) {}

    /**
     * 定时器任务，单位 1 秒 循环
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            VideoLiveControllerView.this.post(new Runnable() {
                @Override
                public void run() {
                    if(mIdentifyType==LIVE_SCENE_PUSH){
                        if(null!= mAnchorName) mAnchorName.setText(LiveUtils.formattedTime(SECOND));
                    }
                    SECOND++;
                    if(SECOND%2==0){
                        //更新排行榜列表
                        RoomDataCache.getInstance().updataOnlines(mAvatarListAdapter,mIdentifyType);
                    }
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
            //新的消息
            if(TextUtils.equals(Constant.OBSERVER_LIVE_MESSAGE_CHANGED, (String) arg)){
                if(null!=mBtnMenus&&mBtnMenus.length>0&&mIdentifyType==LIVE_SCENE_PULL){
                    int msgCount = VideoApplication.getInstance().getMsgCount();
                    mBtnMenus[1].setImageResource(msgCount>0?R.drawable.btn_live_private_chat_true:R.drawable.btn_live_private_chat);
                }
                //任务已经被领取了，重新检查任务状态
            }else if(TextUtils.equals(Constant.OBSERVER_LIVE_ROOM_TASK_GET, (String) arg)){
                checkedTaskAward();
            } else if (TextUtils.equals(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED, (String) arg)) {
                //更新礼物面板钻石数
                if (mFragment != null) {
                    mFragment.setMoney(UserManager.getInstance().getDiamonds());
                }
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
     * 清空主播信息
     */
    private void resetHeadData() {
        //主播昵称
        if(null==mAnchorName) mAnchorName = (MarqueeTextView) findViewById(R.id.view_anchor_name);
        mAnchorName.setText("--");
        if(null== mOnlineNumber) mOnlineNumber = (TextView) findViewById(R.id.view_online_number);//直播间在线人数
        mOnlineNumber.setText("0人");
        //积分
        TextView viewIntegral = (TextView) findViewById(R.id.view_integral);
        viewIntegral.setText("亲密度");
        //用户端显示的今日日期
        TextView durtion = (TextView) findViewById(R.id.view_live_time);
        durtion.setBackgroundResource(0);
        durtion.setText("");
        ((ImageView) findViewById(R.id.view_anchor_head)).setImageResource(R.drawable.ic_default_user_head);
    }

    /**
     * 清屏还原房间内所有状态
     */
    public void onReset() {
        stopReckonTime();
        resetHeadData();
        if(null!=mFragment){
            mFragment.dismiss();
            mFragment=null;
        }
        if(null!=mRechargePresenter){
            mRechargePresenter.dissmis();
        }
        if(null!=mQuireDialog) {
            mQuireDialog.dismiss();
            mQuireDialog=null;
        }
        if(null!=mHandler) mHandler.removeMessages(0);
        if(null!=mConversationListView) mConversationListView.onResrt();
        if(null!=mGiftGroupManager) mGiftGroupManager.onReset();
        if(null!=mAwardGroupManager) mAwardGroupManager.onReset();
        if(null!=mCountdownGiftView) mCountdownGiftView.onReset();
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onReset();
        if(null!=mAwardAnimatorPlayManager) mAwardAnimatorPlayManager.onReset();
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        if(null!=mBannerLayout) mBannerLayout.onReset();
        if(null!=mLoadingView) mLoadingView.hide();
        if(null!=mAvatarListAdapter) mAvatarListAdapter.setNewData(null);
    }
}