package com.yc.liaolive.gift.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.gift.manager.GiftHelpManager;
import com.yc.liaolive.gift.manager.RoomGiftGroupManager;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.GiveGiftResultInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.manager.GiftManager;
import com.yc.liaolive.live.ui.contract.LiveGiftContact;
import com.yc.liaolive.live.ui.presenter.LiveGiftPresenter;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.ui.activity.CallRechargeActivity;
import com.yc.liaolive.view.widget.CircleTextProgressbar;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/7/26
 * 直播间倒计时赠送礼物按钮
 */

public class CountdownGiftView extends LinearLayout implements LiveGiftContact.View, Observer {

    private static final String TAG = "CountdownGiftView";
    private CircleTextProgressbar mProgressbar;
    private ImageView mGiftImageView;
    private TextView mViewCount;
    private FrameLayout mProgressView;
    private GiftInfo mGiftInfo;
    private String mRoomID;//所在的房间ID
    private PusherInfo mAccaptUser;//接收人
    private int mCount;//礼物的个数
    private LiveGiftPresenter mPresenter;
    private QuireDialog mQuireDialog;
    private long currentGiftID=0;//当前正在接受倒计时的礼物ID，用来表
    private int mApiMode;//API场景模式
    private TextView mViewTvMonery;
    private boolean isRunning;//是否正在倒计时
    private int[] mLocationPosition;
    private int mMediaType=-1;

    public CountdownGiftView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public CountdownGiftView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    /**
     * 用户本地积分发生了变化
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String && TextUtils.equals(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED, (String) arg)){
            setMoney(UserManager.getInstance().getDiamonds());
        }
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_countdown_gift_layout,this);
        ApplicationManager.getInstance().addObserver(this);
        mProgressbar = findViewById(R.id.view_circle_progress);
        mProgressView = findViewById(R.id.view_progress_group);
        mGiftImageView = findViewById(R.id.view_circle_gift_icon);
        mViewCount = findViewById(R.id.view_circle_count);
        mViewTvMonery = (TextView) findViewById(R.id.view_tv_monery);
        int screenDensity = ScreenUtils.getScreenDensity();
        if(screenDensity<=300){
            mProgressbar.setProgressLineWidth(6);//进度条宽度
        }else{
            mProgressbar.setProgressLineWidth(12);//进度条宽度
        }
        mProgressbar.setTimeMillis(1000*60);//一分钟的连击
        mProgressbar.setProgressColor(Color.parseColor("#FB6665"));//进度条颜色
        //监听进度
        mProgressbar.setCountdownProgressListener(0, new CircleTextProgressbar.OnCountdownProgressListener() {
            @Override
            public void onProgress(int what, int progress) {
                if(0==progress){
                    // TODO: 2018/6/25 在这里发送通知给GiftAnimationPlayerManager类，清空缓存60秒之内存在的礼物Cache个数
                    ApplicationManager.getInstance().observerUpdata(RoomGiftGroupManager.CANCLE_GIFT_CACHE);
                    onReset();
                }
            }
        });
        //连击监听
        mGiftImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==mLocationPosition){
                    mLocationPosition = new int[2];
                    v.getLocationInWindow(mLocationPosition);
                    GiftHelpManager.getInstance().setAwardEndLocation(mLocationPosition);
                }
                if(null!=mProgressbar){
                    if(mCount <= 0){
                        return;
                    }
                    if (!TextUtils.isEmpty(GiftManager.getInstance().getShowWhiteTips())) {
                        ToastUtils.showCenterToast(GiftManager.getInstance().getShowWhiteTips());
                    } else {
                        AnimationUtil.playAnimation(mGiftImageView);//点击动画
                        mProgressbar.setTimeMillis(1000*60);//一分钟的连击
                        mProgressbar.reStart();
                        postGift(mGiftInfo);
                    }

                }
            }
        });
        mPresenter = new LiveGiftPresenter();
        mPresenter.attachView(this);
        setMoney(UserManager.getInstance().getDiamonds());
    }

    /**
     * 礼物提交
     * @param giftInfo
     */
    private void postGift(GiftInfo giftInfo) {
        if(null==giftInfo) {
            ToastUtils.showCenterToast("未选中任何礼物");
            return;
        }
        if(null==mAccaptUser){
            ToastUtils.showCenterToast("接收对象不存在");
            return;
        }

        //除测试环境绕过购买
        if(mApiMode==LiveGiftDialog.GIFT_MODE_TEST){
            //跳过支付场景，直接赠送礼物
            if(null!=mOnCountdownGiftSendListener) mOnCountdownGiftSendListener.onSendEvent(mGiftInfo,mCount,mCount*mGiftInfo.getPrice(),mAccaptUser);
            return;
        }
        if(null==mPresenter) return;
        long diamonds = UserManager.getInstance().getDiamonds();
        //拦截余额不足,非白名单用户
        if(0==UserManager.getInstance().getIs_white()&&diamonds<(mCount*giftInfo.getPrice())){
            showRechargeTips();
            return;
        }

        if(null != mPresenter){
            mPresenter.givePresentGift(mGiftInfo, mAccaptUser.getUserID(),
                    String.valueOf(mGiftInfo.getId()), mCount, mRoomID, false, mApiMode);
        }
        if(null!=mOnCountdownGiftSendListener&&mApiMode!=LiveGiftDialog.GIFT_MODE_PRIVATE_CHAT&&mApiMode!=LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM){
            mOnCountdownGiftSendListener.onSendEvent(mGiftInfo,mCount,mCount*mGiftInfo.getPrice(),mAccaptUser);
        }
    }

    /**
     * 更新余额显示
     * @param diamonds
     */
    private void setMoney(long diamonds) {
        if(null!=mViewTvMonery) mViewTvMonery.setText(Utils.formatWan(diamonds,true));
    }


    /**
     * 设置使用的场景模式
     */
    public void setApiMode(int apiMode){
        this.mApiMode = apiMode;
    }

    /**
     * 充值询问对话框
     */
    private void showRechargeTips() {
        if(null==getContext()) return;
        //直播间启用快捷充值
        if(mApiMode==LiveGiftDialog.GIFT_MODE_ROOM||mApiMode==LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM){
            MobclickAgent.onEvent(getContext(), "send_gift_type_-1");
            if(!CallRechargeActivity.isRunning()) CallRechargeActivity.start(((Activity) getContext()),18,"赠送礼物失败\n您的钻石不足");
            return;
        }
        if(null!=mQuireDialog) return;
        mQuireDialog = QuireDialog.getInstance(((Activity) getContext()));
        mQuireDialog.setTitleText("赠送礼物失败")
                .setContentText(getResources().getString(R.string.money_name)+getResources().getString(R.string.gift_monery_error))
                .setContentTextColor(getResources().getColor(R.color.app_red_style))
                .setSubmitTitleText("充值")
                .setSubmitTitleTextColor(getResources().getColor(R.color.app_red_style))
                .setCancelTitleText("放弃")
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        MobclickAgent.onEvent(getContext(), "send_gift_type_"+mMediaType);
                        if(mMediaType==Constant.MEDIA_TYPE_ASMR_VIDEO){
                            CallRechargeActivity.start(((Activity) getContext()), 20, null);
                        }else{
                            VipActivity.startForResult(((Activity) getContext()),0);
                        }
                    }

                    @Override
                    public void onRefuse() {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mQuireDialog=null;
            }
        });
        mQuireDialog.show();
    }



    /**
     * 更新最新的连击倒计时礼物信息,不管是赠送了，还是选中了
     * @param giftInfo 礼物信息
     * @param roomID 所在的房间ID
     * @param count 个数
     * @param accaptUser 接收人信息
     */
    public void updataView(GiftInfo giftInfo,String roomID,int count,PusherInfo accaptUser){
        this.mGiftInfo=giftInfo;
        this.mRoomID=roomID;
        this.mAccaptUser=accaptUser;
        this.mCount=count;
        if(null==mGiftInfo) return;
        if(null==mProgressbar) return;
        if(null== mGiftImageView) return;
        if(null!=mProgressView&&mProgressView.getVisibility()!=VISIBLE) mProgressView.setVisibility(VISIBLE);
        if(null!=mViewTvMonery&&mViewTvMonery.getVisibility()!=VISIBLE) mViewTvMonery.setVisibility(VISIBLE);
        isRunning=true;
        //重复
        if(currentGiftID==giftInfo.getId()){
            if(null!=mViewCount) {
                SpannableStringBuilder stringBuilder = LiveChatUserGradleSpan.giftSendNumFromat(String.valueOf(count));
                mViewCount.setText(stringBuilder);
            }
            mProgressbar.setTimeMillis(1000*60);//一分钟的连击
            mProgressbar.reStart();//重新开始
            return;
        }
        //清空本地连续赠送记录个数
        ApplicationManager.getInstance().observerUpdata(RoomGiftGroupManager.CANCLE_GIFT_CACHE);
        //绑定新身份
        currentGiftID=giftInfo.getId();
        try {
            Glide.with(getContext()).load(giftInfo.getSrc())
                    .placeholder(R.drawable.ic_default_gift_icon)
                    .error(R.drawable.ic_default_gift_icon)
                    .crossFade()//渐变
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getContext()))
                    .into(mGiftImageView);
        }catch (Exception e){

        }finally {
            if(null!=mViewCount) {
                SpannableStringBuilder stringBuilder = LiveChatUserGradleSpan.giftSendNumFromat(String.valueOf(count));
                mViewCount.setText(stringBuilder);
            }
            if(null!=mViewTvMonery&&mViewTvMonery.getVisibility()!=VISIBLE) mViewTvMonery.setVisibility(VISIBLE);
            mProgressbar.setTimeMillis(1000*60);//重新开始一分钟的连击倒计时
            mProgressbar.reStart();//重新开始
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setMediaType(int mediaType) {
        this.mMediaType=mediaType;
    }

    /**
     * 传递礼物赠送事件
     */
    public interface OnCountdownGiftSendListener{
        void onSendEvent(GiftInfo data,int count,int totalPrice,PusherInfo mAccepUserInfo);
    }
    private OnCountdownGiftSendListener mOnCountdownGiftSendListener;

    public void setOnGiftSendListener(OnCountdownGiftSendListener onGiftSendListener) {
        mOnCountdownGiftSendListener = onGiftSendListener;
    }

    /**
     * 还原
     */
    public void onReset() {
        if(null!=mProgressView) mProgressView.setVisibility(INVISIBLE);
        if(null!=mViewTvMonery) mViewTvMonery.setVisibility(INVISIBLE);
        mRoomID=null;mAccaptUser=null;mCount=0;
        mGiftInfo=null;
        if(null!=mProgressbar) mProgressbar.stop();
        if(null!=mViewCount) mViewCount.setText("");
        isRunning=false;
    }

    /**
     * 对应方法调用
     */
    public void onDestroy(){
        onReset();
        mApiMode =0;
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mQuireDialog&&mQuireDialog.isShowing()) mQuireDialog.dismiss();
        mLocationPosition=null;
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showGifts(List<GiftInfo> data, String type) {

    }

    @Override
    public void showGiftEmpty(String type) {

    }

    @Override
    public void showGiftError(int code, String errMsg) {

    }

    /**
     * 结算成功
     * @param data
     * @param isDoubleClick
     */
    @Override
    public void showGivePresentSuccess(GiftInfo giftInfo, int giftCount, GiveGiftResultInfo data, boolean isDoubleClick) {
        VideoApplication.getInstance().setMineRefresh(true);
        //一对一视频通话、私信
        if(mApiMode==LiveGiftDialog.GIFT_MODE_PRIVATE_CHAT||mApiMode == LiveGiftDialog.GIFT_MODE_PRIVATE_ROOM){
            if(null!=mOnCountdownGiftSendListener&&null!=mGiftInfo) mOnCountdownGiftSendListener.onSendEvent(mGiftInfo,mCount,mCount*mGiftInfo.getPrice(),mAccaptUser);
        }
        if(null!=data.getUserinfo()){
            //刷新本地余额
            UserManager.getInstance().setDiamonds((data.getUserinfo().getPintai_coin()+data.getUserinfo().getRmb_coin()));
            setMoney(UserManager.getInstance().getDiamonds());
        }
    }


    @Override
    public void showGivePresentError(int code, String data) {
        if(-1!=code)ToastUtils.showCenterToast(data);
    }

    /**
     * 余额不足
     */
    @Override
    public void onRecharge() {
        showRechargeTips();
    }
}
