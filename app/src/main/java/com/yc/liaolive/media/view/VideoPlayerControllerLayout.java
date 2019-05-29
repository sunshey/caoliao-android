package com.yc.liaolive.media.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.TIMConversationType;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.gift.view.CountdownGiftView;
import com.yc.liaolive.index.view.AnchorStatusView;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.adapter.MediaTopListAdapter;
import com.yc.liaolive.media.bean.MediaGiftInfo;
import com.yc.liaolive.media.ui.activity.MediaGiftTopActivity;
import com.yc.liaolive.media.ui.contract.MediaPreviewContract;
import com.yc.liaolive.media.ui.presenter.MediaPreviewPresenter;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.model.ItemSpacesItemDecoration;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.videocall.ui.dialog.QuireVideoDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 视频、照片 预览交互控制器
 *  1：预览人数，  2：举报， 3：点赞，  4：礼物， 5：私信 ，6：与她视频
 * 对应显示和隐藏ID
 */

public class VideoPlayerControllerLayout extends FrameLayout implements MediaPreviewContract.View {

    private static final String TAG = "VideoPlayerControllerLayout";
    private VideoPlayerTabLayout mBtnLike;
    private VideoPlayerTabLayout mBtnPreviewCount;
    private PrivateMedia mData;
    private View mRightTabView;
    private View mBottomUserView;
    private View mTopBarView;
    private int mIsFollow;//是否已关注此用户
    private VideoPlayerTabLayout mBtnGift;
    private VideoPlayerTabLayout mBtnPrivateChat;
    private View mRightLiveState;
    private TextView mTvNum;
    private int mMediaType;//控制器所属多媒体文件类别
    //礼物模块处理
    private MediaTopListAdapter mAdapter;//礼物榜单
    private MediaPreviewPresenter mPresenter;
    private CountdownGiftView mCountdownGiftView;
    private MediaGiftItemSingleManager mNetSingleManager;
    private MediaGiftItemSingleManager mLocSingleManager;
    private FrameLayout mFrameLayoutNet;
    private FrameLayout mFrameLayoutLoc;
    //底部用户区域是否可用
    private boolean userViewEnable;

    public VideoPlayerControllerLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerControllerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_video_player_controller_layout,this);
        mBtnLike = (VideoPlayerTabLayout) findViewById(R.id.view_btn_like);
        mBtnPreviewCount = (VideoPlayerTabLayout) findViewById(R.id.view_btn_preview);
        mBtnGift = (VideoPlayerTabLayout) findViewById(R.id.view_btn_gift);
        mBtnPrivateChat = (VideoPlayerTabLayout) findViewById(R.id.view_btn_private_chat);
        //预览图片角标位置
        mTvNum = (TextView) findViewById(R.id.view_tv_num);
        mRightTabView = findViewById(R.id.right_tab_view);
        mBottomUserView = findViewById(R.id.bottom_user_view);
        mTopBarView = findViewById(R.id.controller_layout_top);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                        //点赞
                    case R.id.view_btn_like:
                        if(null==mData) return;
                        AnimationUtil.playTextCountAnimation2(mBtnLike.mBtnIcon);
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.onLike(mData);
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }
                        break;
                        //预览量
                    case R.id.view_btn_preview:
                        AnimationUtil.playTextCountAnimation2(mBtnPreviewCount.mBtnIcon);
                        break;
                        //赠送礼物
                    case R.id.view_btn_gift:
                        if(null==mData) return;
                        AnimationUtil.playTextCountAnimation2(mBtnGift.mBtnIcon);
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            if(null!=mData&&null!=getContext()){
                                PusherInfo pusherInfo=new PusherInfo();
                                pusherInfo.setUserName(mData.getNickname());
                                pusherInfo.setUserID(mData.getUserid());
                                pusherInfo.setUserAvatar(mData.getAvatar());
                                showGiftBoardView(pusherInfo);
                            }
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }
                        break;
                        //私信
                    case R.id.view_btn_private_chat:
                        if(null==mData) return;
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            if(null!=getContext()) ChatActivity.navToChat(getContext(), mData.getUserid(),mData.getNickname(), TIMConversationType.C2C);//单独会话
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }
                        break;
                        //举报
                    case R.id.view_btn_report:
                        if(null==mData) return;
                        AnimationUtil.playTextCountAnimation2(findViewById(R.id.iv_btn_report));
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            UserManager.getInstance().reportMediaFile(mData.getUserid(),mData.getId(),mData.getFile_type(),new UserServerContract.OnNetCallBackListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    try {
                                        if (null!=getContext())
                                            QuireVideoDialog.getInstance(((Activity) getContext())).showCloseBtn(false).setTipsData("举报成功", getResources().getString(R.string.report_user_success), "确定").show();
                                    } catch (Exception e) {

                                    }
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            });
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }
                        break;
                        //返回
                    case R.id.view_btn_back:
                        AnimationUtil.playTextCountAnimation2(findViewById(R.id.view_btn_back));
                        if(null!=mControllerFunctionListener) mControllerFunctionListener.onBack();
                        break;
                        //点击用户头像
                    case R.id.view_head_icon:
                        if(null==mData) return;
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            PersonCenterActivity.start(getContext(),mData.getUserid());
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }

                        break;
                        //添加关注
                    case R.id.view_add_follow:
                        if(null==mData) return;
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            UserManager.getInstance().followUser(mData.getUserid(), mIsFollow==0?1:0, new UserServerContract.OnNetCallBackListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    mIsFollow=(mIsFollow==0?1:0);
                                    if(null!=mData) mData.setAttent(mIsFollow);//更新关注状态
                                    if(1==mData.getAttent()) ToastUtils.showCenterToast("已关注");
                                    VideoApplication.getInstance().setMineRefresh(true);
                                    updataFollowState();
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            });
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }

                        break;
                        //直播间
                    case R.id.btn_live_state:
                        String clickMsg=mMediaType==Constant.MEDIA_TYPE_IMAGE?"call_video_image_player":"call_video_video_player";
                        MobclickAgent.onEvent(getContext(), clickMsg);
                        if(null==mData) return;
                        if(mData.getUserid().equals(UserManager.getInstance().getUserId())){
                            ToastUtils.showCenterToast("自己不能与自己视频通话");
                            return;
                        }
                        //直接进入直播间
                        if(!TextUtils.isEmpty(mData.getFile_path())){
                            LiveRoomPullActivity activity = LiveRoomPullActivity.getInstance();
                            if(null!=activity){
                                activity.finish();
                            }
                            if(null!=getContext()&&null!=mData.getRoomInfo()&&1==mData.getIs_online()){
                                RoomExtra roomExtra=new RoomExtra();
                                roomExtra.setUserid(mData.getUserid());
                                roomExtra.setNickname(mData.getNickname());
                                roomExtra.setAvatar(mData.getAvatar());
                                roomExtra.setFrontcover(mData.getImg_path());
                                roomExtra.setPull_steram(mData.getRoomInfo().getPlay_url_rtmp());
                                roomExtra.setRoom_id(mData.getRoomInfo().getRoomid());
                                LiveRoomPullActivity.start(getContext(), roomExtra);
                            }else{
                                if(null!=getContext()){
                                    //尝试与其视频通话
                                    CallExtraInfo callExtraInfo=new CallExtraInfo();
                                    callExtraInfo.setToUserID(mData.getUserid());
                                    callExtraInfo.setToNickName(mData.getNickname());
                                    callExtraInfo.setToAvatar(mData.getAvatar());
                                    callExtraInfo.setAnchorFront(mData.getImg_path());
                                    callExtraInfo.setVideoPath(mData.getFile_path());
                                    MakeCallManager.getInstance().attachActivity(((Activity) getContext())).mackCall(callExtraInfo, 1);
                                }
                            }
                        }else{
                            if(null!=mControllerFunctionListener) mControllerFunctionListener.buyMediaFile(mData);
                        }
                        break;
                    case R.id.view_top_layout:
                        if(null!=mData) MediaGiftTopActivity.start(getContext(),mData.getId(),mData.getUserid(),mMediaType);
                        break;
                }
            }
        };
        mBtnLike.setOnClickListener(onClickListener);
        mBtnPreviewCount.setOnClickListener(onClickListener);
        mBtnGift.setOnClickListener(onClickListener);
        mBtnPrivateChat.setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_back).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_report).setOnClickListener(onClickListener);
        findViewById(R.id.view_head_icon).setOnClickListener(onClickListener);
        findViewById(R.id.view_add_follow).setOnClickListener(onClickListener);
        findViewById(R.id.view_top_layout).setOnClickListener(onClickListener);
        mRightLiveState = findViewById(R.id.btn_live_state);
        mRightLiveState.setOnClickListener(onClickListener);
        //初始化礼物模块
        mPresenter = new MediaPreviewPresenter();
        mPresenter.attachView(this);
        RecyclerView headRecyclerView = (RecyclerView) findViewById(R.id.recycler_head_view);
        headRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        headRecyclerView.addItemDecoration(new ItemSpacesItemDecoration(ScreenUtils.dpToPxInt(8f)));
        mAdapter = new MediaTopListAdapter(null);
        mAdapter.setCount(3);
        //虚位占位
        MediaTopEmptyLayout emptyLayout=new MediaTopEmptyLayout(getContext());
        emptyLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mData) MediaGiftTopActivity.start(getContext(),mData.getId(),mData.getUserid(),mMediaType);
            }
        });
        mAdapter.setEmptyView(emptyLayout);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=mData) MediaGiftTopActivity.start(getContext(),mData.getId(),mData.getUserid(),mMediaType);
            }
        });
        headRecyclerView.setAdapter(mAdapter);
        //连击
        mCountdownGiftView = (CountdownGiftView) findViewById(R.id.view_countdown_view);
        mCountdownGiftView.setApiMode(LiveGiftDialog.GIFT_MODE_PRIVATE_MEDIA);
        mCountdownGiftView.setMediaType(mMediaType);
        mCountdownGiftView.setOnGiftSendListener(new CountdownGiftView.OnCountdownGiftSendListener() {
            @Override
            public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                startGiftAnimation(data, accepUserInfo, count);
            }
        });
    }

    public int getMediaType() {
        return mMediaType;
    }

    /**
     * 给定类别，任何实例化此控制器的组件建议给定
     * @param mediaType
     *  1：预览人数，  2：举报， 3：点赞，  4：礼物， 5：私信 ，6：与她视频 7：ASMR进度条
     */
    public void setMediaType(int mediaType) {
        mMediaType = mediaType;
        //预览图片，没有礼物排行榜和礼物赠送
        List<String> imageController = null;
        if(mMediaType==Constant.MEDIA_TYPE_IMAGE){
            imageController = UserManager.getInstance().getImageController();
        }else if(mMediaType==Constant.MEDIA_TYPE_VIDEO){
            imageController = UserManager.getInstance().getVideoController();
        }else if(mMediaType==Constant.MEDIA_TYPE_ASMR_VIDEO){
            imageController = UserManager.getInstance().getAsmrController();
        }
        if(null!=imageController&&imageController.size()>0){
            for (String id : imageController) {
                if(id.equals("1")){
                    findViewById(R.id.view_btn_preview).setVisibility(VISIBLE);
                }else if(id.equals("2")){
                    findViewById(R.id.view_btn_report).setVisibility(VISIBLE);
                }else if(id.equals("3")){
                    findViewById(R.id.view_btn_like).setVisibility(VISIBLE);
                }else if(id.equals("4")){
                    findViewById(R.id.view_top_layout).setVisibility(VISIBLE);
                    findViewById(R.id.view_btn_gift).setVisibility(VISIBLE);
                    findViewById(R.id.view_gift_view).setVisibility(VISIBLE);
                }else if(id.equals("5")){
                    findViewById(R.id.view_btn_private_chat).setVisibility(VISIBLE);
                }else if(id.equals("6")){
                    userViewEnable =true;
                    findViewById(R.id.bottom_user_view).setVisibility(VISIBLE);
                }
            }
        }
    }

    /**
     * 改变控制器的View透明度
     * @param alpha
     */
    public void setTabAlpha(float alpha) {
        if(null!=mRightTabView) mRightTabView.setAlpha(alpha);
        if(null!=mBottomUserView) mBottomUserView.setAlpha(alpha);
        if(null!=mTopBarView) mTopBarView.setAlpha(alpha);
    }

    /**
     * 更新视频信息
     * @param data
     */
    public void setVideoData(PrivateMedia data){
        if(null==data) return;
        long loveNumber=0;
        long shareNumber=0;
        long browseNumber=0;
        //如果当前控制器在预览照片模式下被实例化，每次赋值，检查预览量、点赞量、分享量的最大值，只能增不能减
        if(null!=mData){
            loveNumber=mData.getLove_number();
            shareNumber=mData.getShare_number();
            browseNumber=mData.getBrowse_number();
        }
        this.mData=data;
        if(Constant.MEDIA_TYPE_IMAGE==mMediaType){
            if(mData.getLove_number()<loveNumber) mData.setLove_number(loveNumber);
            if(mData.getShare_number()<shareNumber) mData.setShare_number(shareNumber);
            if(mData.getBrowse_number()<browseNumber) mData.setBrowse_number(browseNumber);
        }
        if(null!=mBtnLike) {
            mBtnLike.setIcon(1==data.getIs_love()?R.drawable.ic_video_price_true:R.drawable.ic_video_price_false);
            try {
                mBtnLike.setTitle(Utils.formatWan(data.getLove_number(),true));
            }catch (NumberFormatException e){

            }catch (RuntimeException e){

            }
        }
        try {
            if(null!=mBtnPreviewCount) mBtnPreviewCount.setTitle(Utils.formatWan(data.getBrowse_number(),true));
        }catch (NumberFormatException e){

        }catch (RuntimeException e){

        }finally {
            mIsFollow=data.getAttent();
            updataFollowState();
            initState();
        }
    }

    /**
     * 初始化
     */
    public void initState(){
        if(null==mData) return;
        //设置用户头像
        ImageView userAvatar = (ImageView) findViewById(R.id.view_head_icon);
        if(null!=userAvatar&&null!=getContext()&&!TextUtils.isEmpty(mData.getAvatar()))Glide.with(getContext())
                .load(mData.getAvatar())
                .placeholder(userAvatar.getDrawable())
                .error(R.drawable.ic_default_user_head)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(userAvatar);
        //昵称
        TextView titleTextView = (TextView) findViewById(R.id.view_title);
        //默认是非主播身份
        int titleWidth= ScreenUtils.getScreenWidth();
        //如果是主播，则固定其昵称控件宽度
        if(2==mData.getIdentity_audit()){
            titleWidth= ScreenUtils.dpToPxInt(40f);
            if(!TextUtils.isEmpty(mData.getNickname())&&mData.getNickname().length()>=5){
                titleWidth=ScreenUtils.dpToPxInt(58f);
            }
        }
        titleTextView.getLayoutParams().width=titleWidth;
        if(!TextUtils.isEmpty(mData.getNickname())) titleTextView.setText(mData.getNickname());
        //签名
        TextView despTextView = (TextView) findViewById(R.id.view_video_desp);
        //默认是占满全屏
        int despWidth= ScreenUtils.getScreenWidth();
        //如果是主播，限制宽度
        if(2==mData.getIdentity_audit()){
            despWidth= ScreenUtils.dpToPxInt(88f);
        }
        despTextView.getLayoutParams().width=despWidth;
        String desp=mData.getVideo_desp();
        if(TextUtils.equals("暂无描述",desp)){
            desp="";
        }
        despTextView.setVisibility(TextUtils.isEmpty(desp)?GONE:VISIBLE);
        if(!TextUtils.isEmpty(mData.getVideo_desp())) despTextView.setText(desp);
    }

    /**
     * 直播间状态
     */
    public void updateRoomOffline(){
        if(null!=mData&&null!=mRightLiveState){
            //左侧用户底部直播状态
            AnchorStatusView offlineState = findViewById(R.id.user_offline_state);
            if(userViewEnable){
                mRightLiveState.setVisibility(2==mData.getIdentity_audit()?VISIBLE:GONE);
            }
            if (2 == mData.getIdentity_audit()) {//是否是主播身份?
                offlineState.setVisibility(VISIBLE);
                //主播、用户的在线状态
                offlineState.setData(mData.getUser_state(), 0);
            } else {
                offlineState.setVisibility(GONE);
            }
        }
    }

    /**
     * 刷新关注状态
     */
    public void updataFollowState() {
        View addFollow = findViewById(R.id.view_add_follow);
        if(null!=addFollow){
            if(null!=mData&&TextUtils.equals(mData.getUserid(),UserManager.getInstance().getUserId())){
                addFollow.setVisibility(INVISIBLE);
            }else{
                addFollow.setVisibility(1==mData.getAttent()?INVISIBLE:VISIBLE);
            }
        }
    }

    /**
     * 刷新排行榜单
     * @param giftRankInfos
     */
    public void onUpdataMediaTops(List<FansInfo> giftRankInfos) {
        if(null!=mAdapter){
            List<FansInfo> fansInfos=new ArrayList<>();
            fansInfos.addAll(giftRankInfos);
            mAdapter.setNewData(fansInfos);
        }
    }

    /**
     * 礼物初始化
     * @param giftInfos 礼物赠送历史记录
     * @param giftRankInfos 礼物赠送榜单记录
     */
    public void onCreateGift(List<MediaGiftInfo> giftInfos, List<FansInfo> giftRankInfos) {
        if(null!=mAdapter) mAdapter.setNewData(giftRankInfos);
        if(null!=mNetSingleManager) mNetSingleManager.addGiftListsToTask(giftInfos);
    }

    /**
     * 显示礼物面板
     * 赠送礼物，本地负责先播放动画，远程端交由后台去推送礼物赠送消息
     * @param pusherInfo
     */
    private void showGiftBoardView(PusherInfo pusherInfo) {
        if(null==getContext()||null==mData) return;
        FragmentActivity context = (FragmentActivity) getContext();
        if(!context.isFinishing()){
            LiveGiftDialog fragment = LiveGiftDialog.getInstance(context,pusherInfo, String.valueOf(mData.getId()), LiveGiftDialog.GIFT_MODE_PRIVATE_MEDIA,true,mMediaType);
            fragment.setOnGiftSelectedListener(new LiveGiftDialog.OnGiftSelectedListener() {
                @Override
                public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                    startGiftAnimation(data, accepUserInfo, count);
                }

                @Override
                public void onDissmiss() {

                }
                //礼物选择发生了变化
                @Override
                public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo) {
                    if(null!=mCountdownGiftView) mCountdownGiftView.updataView(giftInfo,String.valueOf(mData.getId()),count,accepUserInfo);
                }
            });
            fragment.show();
        }
    }
    /**
     * 本地礼物动画处理
     * @param data
     * @param accepUserInfo
     * @param count
     */
    private void startGiftAnimation(GiftInfo data, PusherInfo accepUserInfo, int count) {
        if(null!=data&&null!=accepUserInfo){
            MediaGiftInfo mediaGiftInfo=new MediaGiftInfo();
            mediaGiftInfo.setUserid(UserManager.getInstance().getUserId());
            mediaGiftInfo.setNikcname(UserManager.getInstance().getNickname());
            mediaGiftInfo.setAvatar(UserManager.getInstance().getAvatar());
            mediaGiftInfo.setAccept_userid(accepUserInfo.getUserID());
            mediaGiftInfo.setAccept_nikcname(accepUserInfo.getUserName());
            mediaGiftInfo.setGift_count(String.valueOf(count));
            mediaGiftInfo.setGift_id(String.valueOf(data.getId()));
            mediaGiftInfo.setGift_title(data.getTitle());
            mediaGiftInfo.setGift_big_svga(data.getBigSvga());
            mediaGiftInfo.setGift_src(data.getSrc());
            mediaGiftInfo.setCmd(Constant.MSG_CUSTOM_GIFT);
            if(null!=mLocSingleManager) mLocSingleManager.addGiftToTask(mediaGiftInfo);
        }
    }

    /**
     * 预览图片时顶部数字/指示器
     * @param numContext
     */
    public void setNumText(String numContext) {
        if(null!=mTvNum) mTvNum.setText(numContext);
    }

    /**
     * 消息图标图片
     * @param resId
     */
    public void setMsgIcon(int resId) {
        if(null!=mBtnPrivateChat) mBtnPrivateChat.mBtnIcon.setImageResource(resId);
    }

    /**
     * 是否显示顶部的数字指示器
     * @param flag
     */
    public void showTopNumTextView(boolean flag) {
        if(null!=mTvNum){
            if(flag){
                AnimationUtil.visibTransparentView(mTvNum,500);
            }else{
                if(null!=mTvNum) mTvNum.setVisibility(INVISIBLE);
            }
        }
    }

    /**
     * 返回按钮是否可见
     * @param visibility
     */
    public void setBackVisibility(int visibility) {
        try {
            findViewById(R.id.view_btn_back).setVisibility(visibility);
        }catch (RuntimeException e){

        }
    }

    /**
     * 改变控制的显示状态
     */
    public void changedTabBarVisibility() {
        if(null==mRightTabView||null==mTopBarView||null==mBottomUserView) return;
        if(mRightTabView.getVisibility()==View.VISIBLE){
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_YES);//禁止PrantView手势活动
            AnimationUtil.goneTransparentView(mRightTabView);
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewTop();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(null!=mTopBarView) mTopBarView.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mTopBarView.startAnimation(translateAnimation);
            TranslateAnimation translateAnimation1 = AnimationUtil.moveToViewBottom2();
            translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(null!=mBottomUserView) mBottomUserView.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mBottomUserView.startAnimation(translateAnimation1);
        }else{
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_NO);//解除父容器手势
            AnimationUtil.visibTransparentView(mRightTabView);
            mTopBarView.setVisibility(VISIBLE);
            if(userViewEnable){
                mBottomUserView.setVisibility(VISIBLE);
                mBottomUserView.startAnimation(AnimationUtil.moveToViewLocation());
            }
            mTopBarView.startAnimation(AnimationUtil.moveToViewTopLocation());
        }
    }

    /**
     * 还原TAB为初始状态
     */
    public void resetControllerTabBar() {
        if(null==mRightTabView||null==mTopBarView||null==mBottomUserView) return;
        if(mRightTabView.getVisibility()!=VISIBLE) mRightTabView.setVisibility(VISIBLE);
        if(mTopBarView.getVisibility()!=VISIBLE) mTopBarView.setVisibility(VISIBLE);
        if(userViewEnable){
            if(mBottomUserView.getVisibility()!=VISIBLE) mBottomUserView.setVisibility(VISIBLE);
        }
    }

    /**
     * 是否允许直接离开
     * @return
     */
    public boolean isBack() {
        if(null==mRightTabView||null==mTopBarView||null==mBottomUserView) return false;
        if(mRightTabView.getVisibility()!=VISIBLE){
            AnimationUtil.visibTransparentView(mRightTabView);
            mTopBarView.setVisibility(VISIBLE);
            if(userViewEnable){
                mBottomUserView.setVisibility(VISIBLE);
                mBottomUserView.startAnimation(AnimationUtil.moveToViewLocation());
            }
            mTopBarView.startAnimation(AnimationUtil.moveToViewTopLocation());
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_NO);//解除上下左右滑动手势
            return true;
        }
        return false;
    }

    /**
     * 隐藏菜单
     */
    public void hideTabView() {
        if(null==mRightTabView||null==mTopBarView||null==mBottomUserView) return;
        if(mRightTabView.getVisibility()==View.VISIBLE){
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_YES);//禁止PrantView手势活动
            AnimationUtil.goneTransparentView(mRightTabView);
            TranslateAnimation translateAnimation = AnimationUtil.moveToViewTop();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(null!=mTopBarView) mTopBarView.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mTopBarView.startAnimation(translateAnimation);
            TranslateAnimation translateAnimation1 = AnimationUtil.moveToViewBottom2();
            translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(null!=mBottomUserView) mBottomUserView.setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mBottomUserView.startAnimation(translateAnimation1);
        }
    }

    //==============================================交互回调=========================================

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showMediaTopList(List<FansInfo> data) {
        if(null!=mAdapter) mAdapter.setNewData(data);
        ((ImageView) findViewById(R.id.view_top_more)).setImageResource(R.drawable.ic_expand_hander);
    }

    @Override
    public void showMediaError(int code, String errorMsg) {

    }

    /**
     * 界面重新可见
     */
    public void onStart() {
        if(null==mFrameLayoutNet) mFrameLayoutNet= (FrameLayout) findViewById(R.id.gift_net);
        if(null==mFrameLayoutLoc) mFrameLayoutLoc= (FrameLayout) findViewById(R.id.gift_location);
        mFrameLayoutNet.removeAllViews();
        mFrameLayoutLoc.removeAllViews();
        //动画、本地预览
        mNetSingleManager = new MediaGiftItemSingleManager(getContext());
        mNetSingleManager.setDisabledScroll(true);//禁用礼物数量文字滚动效果
        mFrameLayoutNet.addView(mNetSingleManager);
        mLocSingleManager = new MediaGiftItemSingleManager(getContext());
        mFrameLayoutLoc.addView(mLocSingleManager);
    }

    /**
     * 界面处于不可见状态，销毁所有礼物动画资源 及榜单信息
     */
    public void onStop() {
        if(null!=mCountdownGiftView) mCountdownGiftView.onReset();
        if(null!=mLocSingleManager) mLocSingleManager.onReset();
        if(null!=mNetSingleManager) mNetSingleManager.onReset();
        if(null!=mFrameLayoutNet) mFrameLayoutNet.removeAllViews();
        if(null!=mFrameLayoutLoc) mFrameLayoutLoc.removeAllViews();
        mLocSingleManager=null;mNetSingleManager=null;
        if(null!=mAdapter) mAdapter.setNewData(null);
    }

    public interface OnControllerFunctionListener{
        void onLike(PrivateMedia privateMedia);
        void onShare(PrivateMedia privateMedia);
        void onBack();
        void buyMediaFile(PrivateMedia privateMedia);
    }

    private OnControllerFunctionListener mControllerFunctionListener;

    public void setControllerFunctionListener(OnControllerFunctionListener controllerFunctionListener) {
        mControllerFunctionListener = controllerFunctionListener;
    }

    public void onDestroy(){
        mControllerFunctionListener=null;
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mLocSingleManager) mLocSingleManager.onDestroy();
        if(null!=mNetSingleManager) mNetSingleManager.onDestroy();
        mLocSingleManager=null;mNetSingleManager=null;
    }
}