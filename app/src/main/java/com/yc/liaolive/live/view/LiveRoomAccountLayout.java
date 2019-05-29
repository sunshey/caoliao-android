package com.yc.liaolive.live.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.bean.RoomOutBean;
import com.yc.liaolive.live.ui.contract.LiveUserContract;
import com.yc.liaolive.live.ui.presenter.LiveUserPresenter;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.MarqueeTextView;
import com.yc.liaolive.view.widget.TouchFilterImageView;

import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * TinyHung@Outlook.com
 * 2019/1/24
 * 直播房间结算界面
 */

public class LiveRoomAccountLayout extends FrameLayout implements View.OnClickListener, LiveUserContract.View {

    //调用者身份
    public static final int SCENE_MODE_PLAY=0;//播放端
    public static final int SCENE_MODE_PLAYER=1;//推流端
    private LiveUserPresenter mPresenter;
    private int mIdentify=SCENE_MODE_PLAY;
    private String mToUserID;
    private int mIsFollow;//默认是未关注此用户的

    public LiveRoomAccountLayout(@NonNull Context context) {
        this(context,null);
    }

    public LiveRoomAccountLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_live_room_account_layout, this);
        mPresenter = new LiveUserPresenter();
        mPresenter.attachView(this);
    }

    /**
     * 调用者用户身份绑定
     * @param identify
     */
    public void setIdentify(int identify){
        this.mIdentify=identify;
        if (mIdentify == SCENE_MODE_PLAYER) {
            //主播端
            findViewById(R.id.ll_player_view).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_play_view).setVisibility(View.GONE);
        } else {
            //用户端
            findViewById(R.id.ll_play_view).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_player_view).setVisibility(View.GONE);
            findViewById(R.id.btn_follow).setOnClickListener(this);
        }
        findViewById(R.id.btn_close).setOnClickListener(this);
    }

    /**
     * 结算信息，主播端调用
     * @param data
     */
    public void setAccountData(RoomOutBean data){
        if (data == null){
            data = new RoomOutBean("0", "0", "0", "00:00:00");
        }
        MarqueeTextView textView1 = (MarqueeTextView) findViewById(R.id.title_action1);
        MarqueeTextView textView2 = (MarqueeTextView) findViewById(R.id.title_action2);
        MarqueeTextView textView3 = (MarqueeTextView) findViewById(R.id.title_action3);
        MarqueeTextView textView4 = (MarqueeTextView) findViewById(R.id.title_action4);
        textView1.setText(String.format(Locale.CHINA,"收获积分：%s", data.getPoints()));
        textView2.setText(String.format(Locale.CHINA,"收获钻石：%s", data.getDiamond()));
        textView3.setText(String.format(Locale.CHINA,"获得亲密度：%s", data.getIntimacy()));
        textView4.setText(String.format(Locale.CHINA,"直播时长：%s",data.getDuration()));
    }

    /**
     * 绑定主播身份
     * @param toUserID
     */
    public void setToUserID(String toUserID){
        this.mToUserID=toUserID;
        //检查关注状态
        if(!TextUtils.isEmpty(mToUserID)&&null!=mPresenter) {
            mPresenter.followUser(UserManager.getInstance().getUserId(), toUserID,2);//检查是否已关注此用户
            mPresenter.getUserDetsils(toUserID);
        }
    }

    /**
     * 绑定主播封面
     * @param front
     */
    public void setToUserFront(String front){
        if(null==findViewById(R.id.view_anchor_front)) return;
        ImageView frontImage = findViewById(R.id.view_anchor_front);
        Glide.with(getContext())
                .load(front)
                .error(R.drawable.bg_live_transit)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .bitmapTransform(new BlurTransformation(getContext(), 25))
                .into(frontImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                if(null!=mOnFunctionListener) mOnFunctionListener.onClose();
                break;
            case R.id.btn_follow:
                if(TextUtils.isEmpty(mToUserID)) return;
                if(!Utils.isCheckNetwork()) return;
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mIsFollow=(mIsFollow==0?1:0);
                    mPresenter.followUser(UserManager.getInstance().getUserId(), mToUserID,mIsFollow);
                }
                break;
            //主页
            case R.id.re_user_icon:
                if(TextUtils.isEmpty(mToUserID)) return;
                //内部处理
                PersonCenterActivity.start(((Activity) getContext()),mToUserID);
                break;
        }
    }

    /**
     * 设置用户信息
     * @param userInfo
     */
    private void setUserData(FansInfo userInfo) {
        if(null==userInfo) return;
        if(null==findViewById(R.id.oneself_nickname)) return;
        MarqueeTextView nickName = (MarqueeTextView) findViewById(R.id.oneself_nickname);
        nickName.setText(userInfo.getNickname());
        ImageView oneselfUserGradle = (ImageView) findViewById(R.id.oneself_user_gradle);
        ImageView oneselfVipGradle = (ImageView) findViewById(R.id.oneself_vip_gradle);
        ImageView oneselfUserSex = (ImageView) findViewById(R.id.oneself_user_sex);
        TouchFilterImageView icUserIcon = (TouchFilterImageView) findViewById(R.id.ic_user_icon);
        //设置用户等级
        LiveUtils.setUserGradle(oneselfUserGradle, userInfo.getLevel_integral());
        LiveUtils.setUserBlockVipGradle(oneselfVipGradle,userInfo.getVip());//设置用户vip等级
        LiveUtils.setUserSex(oneselfUserSex,userInfo.getSex());
        Glide.with(getContext())
                .load(userInfo.getAvatar())
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(icUserIcon);
    }

    /**
     * 对应方法调用
     */
    public void onDestroy(){
        if(null!=mPresenter) mPresenter.detachView();
        mPresenter=null;mOnFunctionListener=null;
    }

    /**
     * 更新提示语
     * @param message
     */
    public void setTipsContent(String message) {
        TextView topTitle = (TextView) findViewById(R.id.tv_top_title);
        topTitle.setText(message);
    }

    /**
     * 更新按钮文字
     * @param content
     */
    public void setButtonContent(String content) {
        TextView btnClose = (TextView) findViewById(R.id.btn_close);
        btnClose.setText(content);
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showFollowUserResult(String data) {
        if(null!=findViewById(R.id.btn_follow)){
            TextView btnFollow = (TextView) findViewById(R.id.btn_follow);
            btnFollow.setText(mIsFollow==1?"已关注":"关注");
            btnFollow.setBackgroundResource(mIsFollow==1?R.drawable.full_room_gray_bg_pre_8:R.drawable.full_room_follow_selector);
            findViewById(R.id.tv_play_state).setVisibility(mIsFollow==1?View.INVISIBLE:View.VISIBLE);//关注提示
        }
    }

    @Override
    public void showFollowUserError(int code, String data) {

    }

    @Override
    public void showIsFollow(int status) {
        this.mIsFollow=status;
        if(null!=findViewById(R.id.btn_follow)){
            TextView btnFollow = (TextView) findViewById(R.id.btn_follow);
            btnFollow.setText(status==1?"已关注":"关注");
            btnFollow.setBackgroundResource(mIsFollow==1?R.drawable.full_room_gray_bg_pre_8:R.drawable.full_room_follow_selector);
            findViewById(R.id.tv_play_state).setVisibility(status==1?View.INVISIBLE:View.VISIBLE);//关注提示
        }
    }

    @Override
    public void showIsFollowError(int code, String data) {

    }

    @Override
    public void showUserDetsilsResult(FansInfo userInfo) {
        setUserData(userInfo);
    }

    @Override
    public void showUserDetsilsError(int code, String msg) {

    }

    public interface OnFunctionListener{
        void onClose();
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}