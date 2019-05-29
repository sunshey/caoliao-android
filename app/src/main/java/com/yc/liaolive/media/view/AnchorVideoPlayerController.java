package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.flexbox.FlexboxLayout;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.index.view.AnchorStatusView;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.bean.VideoChatInfo;
import com.yc.liaolive.live.constants.IVideoPlayerView;
import com.yc.liaolive.live.presenter.VideoPlayerPresenter;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.CircleImageView;
import com.yc.liaolive.view.widget.MarqueeTextView;
import java.text.MessageFormat;

/**
 * TinyHung@Outlook.com
 * 2019/1/14
 * 主播视频交互控制器
 */

public class AnchorVideoPlayerController extends FrameLayout implements IVideoPlayerView {

    private static final String TAG = "AnchorVideoPlayerController";
    private VideoPlayerPresenter mPresenter;
    private CircleImageView mUserHeader;
    private MarqueeTextView mNickName;
    private ImageView mFollowState;
    private RoomList mAnchorData;
    private int mAttent;//关注状态 0:未关注 1：关注
    private View mBtnVideo;
    private RelativeLayout mViewTopTab;

    public AnchorVideoPlayerController(@NonNull Context context) {
        this(context,null);
    }

    public AnchorVideoPlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_controller_anchor_video_player,this);
        mUserHeader = (CircleImageView) findViewById(R.id.view_head_icon);
        mNickName = (MarqueeTextView) findViewById(R.id.view_title);
        mFollowState = (ImageView) findViewById(R.id.view_add_follow);
        mViewTopTab = (RelativeLayout) findViewById(R.id.view_top_tab);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_to_video:
                        if(null!=mAnchorData&&null!=mControllerFunctionListener) mControllerFunctionListener.onStartVideoCall(mAnchorData);
                        break;
                    case R.id.view_btn_close:
                        if(null!=mControllerFunctionListener) mControllerFunctionListener.onBack();
                        break;
                    case R.id.view_add_follow:
                        if(null!=mAnchorData) UserManager.getInstance().followUser(mAnchorData.getUserid(), mAttent==0?1:0, new UserServerContract.OnNetCallBackListener() {
                            @Override
                            public void onSuccess(Object object) {
                                mAttent=(mAttent==0?1:0);
                                ToastUtils.showCenterToast("关注成功");
                                if(null!=mFollowState) mFollowState.setVisibility(1==mAttent?INVISIBLE: VISIBLE);
                            }

                            @Override
                            public void onFailure(int code, String errorMsg) {
                                ToastUtils.showCenterToast(errorMsg);
                            }
                        });
                        break;
                    case R.id.view_head_icon:
                        if(null!=mAnchorData){
                            PersonCenterActivity.start(getContext(),mAnchorData.getUserid());
                        }
                        break;
                }
            }
        };
        mBtnVideo = findViewById(R.id.btn_to_video);
        mBtnVideo.setOnClickListener(onClickListener);
        mUserHeader.setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_close).setOnClickListener(onClickListener);
        findViewById(R.id.view_add_follow).setOnClickListener(onClickListener);
    }

    /**
     * 绑定用户信息
     * @param anchorData
     */
    public void setAnchorData(RoomList anchorData) {
        if(null==anchorData) return;
        this.mAnchorData=anchorData;
        updataAnchorData(anchorData.getNickname(),anchorData.getAvatar());
        if(null==mPresenter){
            mPresenter = new VideoPlayerPresenter();
            mPresenter.attachView(this);
        }
        mPresenter.getVideoData(mAnchorData.getUserid());
    }

    private void updataAnchorData(String nickname, String avatar) {
        if(null!=mUserHeader){
            Glide.with(getContext())
                    .load(avatar)
                    .error(R.drawable.ic_default_user_head)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getContext()))
                    .into(mUserHeader);
        }
        if(null!=mNickName) mNickName.setText(nickname);
    }

    /**
     * 改变控制器的View透明度
     * @param alpha
     */
    public void setTabAlpha(float alpha) {
        if(null!=mBtnVideo) mBtnVideo.setAlpha(alpha);
        if(null!=mViewTopTab) mViewTopTab.setAlpha(alpha);
    }

    public void onStart() {

    }

    public void onStop() {

    }

    public void setAttent(int attent) {
        this.mAttent=attent;
        updataFollowState();
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void setVideoData(VideoChatInfo info) {
        if(null!=findViewById(R.id.user_offline_state)){
            AnchorStatusView anchorStatusView = (AnchorStatusView) findViewById(R.id.user_offline_state);
            MarqueeTextView videoDesp = (MarqueeTextView) findViewById(R.id.view_video_desp);
            FlexboxLayout userFlags = (FlexboxLayout) findViewById(R.id.user_flags);
            TextView videoPrice = (TextView) findViewById(R.id.tv_video_price);
            //在线状态
            anchorStatusView.setData(info.getUser_state(), 0);
            videoDesp.setText(info.getSignature());
            videoPrice.setText(MessageFormat.format("（{0}钻石/分钟）", info.getChat_deplete()));
            mAttent=info.getIs_attention();
            updataFollowState();
            userFlags.removeAllViews();
            if(null!=userFlags&&null!=getContext()){
                for (String label : info.getLabel()) {
                    TextView flagTv = new TextView(getContext());
                    flagTv.setTextSize(10);
                    flagTv.setTextColor(getResources().getColor(R.color.white));
                    flagTv.setText(label);
                    flagTv.setGravity(Gravity.CENTER);
                    flagTv.setBackground(ContextCompat.getDrawable(AppEngine.getApplication(), R.drawable.anchor_flags_bg));
                    userFlags.addView(flagTv);
                }
            }
        }
    }


    /**
     * 刷新关注状态
     */
    private void updataFollowState() {
        if(null != mFollowState){
            if(null != mAnchorData && TextUtils.equals(mAnchorData.getUserid(), UserManager.getInstance().getUserId())){
                mFollowState.setVisibility(View.INVISIBLE);
            } else {
                mFollowState.setVisibility(1==mAttent?INVISIBLE:VISIBLE);
            }
        }
    }

    public void onReset() {
        if(null!=mPresenter&&null!=mAnchorData) mPresenter.getVideoData(mAnchorData.getUserid());
    }

    public void onDestroy(){
        if(null!=mPresenter){
            mPresenter.detachView();
            mPresenter=null;
        }
    }

    public interface OnControllerFunctionListener{
        void onStartVideoCall(RoomList roomList);
        void onBack();
    }

    private OnControllerFunctionListener mControllerFunctionListener;

    public void setControllerFunctionListener(OnControllerFunctionListener controllerFunctionListener) {
        mControllerFunctionListener = controllerFunctionListener;
    }
}