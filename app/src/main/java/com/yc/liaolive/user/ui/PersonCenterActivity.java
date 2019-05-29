package com.yc.liaolive.user.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tencent.TIMConversationType;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.IndexMineUserInfo;
import com.yc.liaolive.bean.PersonCenterInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.TabMineUserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityPersonCenterBinding;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.gift.view.CountdownGiftView;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.bean.MediaGiftInfo;
import com.yc.liaolive.media.ui.activity.PrivateMediaPhotoActivity;
import com.yc.liaolive.media.ui.activity.PrivateMediaVideoActivity;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.media.view.MediaGiftItemSingleManager;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.msg.view.ListEmptyFooterView;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.activity.IntegralTopListActivity;
import com.yc.liaolive.ui.adapter.PersonCenterAdapter;
import com.yc.liaolive.ui.contract.PersonCenterContract;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.ui.presenter.PersonCenterPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.PersonCenterManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;
import com.yc.liaolive.view.widget.PersonConterHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/18
 * 用户中心
 */

public class PersonCenterActivity extends BaseActivity<ActivityPersonCenterBinding> implements PersonCenterContract.View {

    private static final String TAG = "PersonCenterActivity";
    private PopupWindow popupWindow;
    private PersonCenterPresenter mPresenter;
    private String mToUserid;
    private int mComeFrom;//如果大于0，则直接返回上层
    private PersonConterHeaderView mHeaderView;
    private PersonCenterAdapter mAdapter;
    private DataChangeView mEmptyView;
    int mDistance = 0;//当前手势滚动阈值
    private IndexLinLayoutManager mLinLayoutManager;
    private int mHeaderHeight;//头部高度
    private MediaGiftItemSingleManager mLocSingleManager;

    public static void start(Context context, String userID) {
        Intent intent = new Intent(context, PersonCenterActivity.class);
        intent.putExtra("to_userid", userID);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param userID
     * @param from 从哪个页面跳转过来 默认等于0
     */
    public static void start(Context context, String userID, int from) {
        Intent intent = new Intent(context, PersonCenterActivity.class);
        intent.putExtra("to_userid", userID);
        intent.putExtra("from", from);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent()) {
            mToUserid = getIntent().getStringExtra("to_userid");
            mComeFrom = getIntent().getIntExtra("from", 0);
        }
        if(TextUtils.isEmpty(mToUserid)){
            ToastUtils.showCenterToast("用户信息错误");
            finish();
           return;
        }
        setContentView(R.layout.activity_person_center);
        mPresenter = new PersonCenterPresenter(this);
        mPresenter.attachView(this);
        mPresenter.getPersonCenterInfo(mToUserid);
    }

    @Override
    public void initViews() {
        //对自己隐藏MENU菜单功能
        if (TextUtils.isEmpty(mToUserid) || mToUserid.equals(UserManager.getInstance().getUserId())){
            bindingView.btnMenu.setVisibility(View.GONE);
        }
        mLinLayoutManager = new IndexLinLayoutManager(PersonCenterActivity.this, IndexLinLayoutManager.VERTICAL, false);
        bindingView.recyclerView.setLayoutManager(mLinLayoutManager);
        mAdapter = new PersonCenterAdapter( null);
        //子控件点击事件
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, final View view, final int position) {
                switch (view.getId()) {
                    case R.id.fans_root_item:
                        IntegralTopListActivity.start(PersonCenterActivity.this, mToUserid);
                        break;
                    case R.id.media_root_item:
                        if(null!=view.getTag()){
                            IndexMineUserInfo item = (IndexMineUserInfo) view.getTag();
                            if(Constant.MEDIA_TYPE_IMAGE==item.getMediaType()){
                                PrivateMediaPhotoActivity.start(PersonCenterActivity.this,mToUserid);
                            }else if(Constant.MEDIA_TYPE_VIDEO==item.getMediaType()){
                                PrivateMediaVideoActivity.start(PersonCenterActivity.this,mToUserid);
                            }
                        }
                        break;
                    case R.id.item_sub_title:
                        VipActivity.start(PersonCenterActivity.this,1);
                        break;
                    case R.id.btn_item_follow:
                        if(null!=view.getTag()){
                            final int followState = (int) view.getTag();
                            UserManager.getInstance().followUser(mToUserid, followState == 0 ? 1 : 0, new UserServerContract.OnNetCallBackListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    TextView followText= (TextView) view;
                                    int newFollowState = (followState == 0 ? 1 : 0);
                                    followText.setText(1==newFollowState?"已关注":"关注");
                                    followText.setBackgroundResource(1==newFollowState?R.drawable.full_room_gray_bg_pre_8:R.drawable.full_room_follow_selector);
                                    followText.setTag(newFollowState);
                                    //标记更新状态，前往直播间更新关注状态
                                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_FOLLOW_CHANGE);
                                    ApplicationManager.getInstance().observerUpdata(0==newFollowState ?Constant.OBSERVER_CMD_FOLLOW_FALSE:Constant.OBSERVER_CMD_FOLLOW_TRUE);
                                    VideoApplication.getInstance().setMineRefresh(true);
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            });
                        }
                        break;
                    case R.id.item_view_tab:
                        if(null!=view.getTag()){
                            String userID= (String) view.getTag();
                            Utils.copyString(PersonCenterActivity.this,userID);
                            ToastUtils.showCenterToast("ID已复制到粘贴板");
                        }
                        break;
                }
            }
        });
        //条目监听
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        //多媒体文件监听
        mAdapter.setOnMediaItemClickListener(new PersonCenterAdapter.OnMediaItemClickListener() {
            @Override
            public void onItemClick(PrivateMedia privateMedia, View view, int pisition) {
                if(null!=privateMedia){
                    if(Constant.MEDIA_TYPE_IMAGE==privateMedia.getFile_type()){
                        PrivateMediaPhotoActivity.start(PersonCenterActivity.this,mToUserid);
                    }else if(Constant.MEDIA_TYPE_VIDEO==privateMedia.getFile_type()){
                        PrivateMediaVideoActivity.start(PersonCenterActivity.this,mToUserid);
                    }
                }
            }
        });
        //占位状态
        mEmptyView = new DataChangeView(PersonCenterActivity.this);
        mEmptyView.showLoadingView();
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyView.showLoadingView();
                if(null!=mPresenter) mPresenter.getPersonCenterInfo(mToUserid);
            }
        });
        //添加占位布局
        mAdapter.setEmptyView(mEmptyView);
        bindingView.recyclerView.setAdapter(mAdapter);
        mHeaderView = new PersonConterHeaderView(PersonCenterActivity.this);
        //头部功能事件监听
        mHeaderView.setOnFunctionListener(new PersonConterHeaderView.onFunctionListener() {
            /**
             * 去预览照片
             * @param privateMedia
             * @param view
             */
            @Override
            public void onItemClick(PrivateMedia privateMedia, final View view,int count) {
                if(count>1){
                    //先关闭可能打开的照片预览界面
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_FINLISH_MEDIA_PLAYER);
                    VideoDataUtils.getInstance().setHostUrl(NetContants.getInstance().URL_FILE_LIST());
                    VideoDataUtils.getInstance().setFileType(0);
                    VideoDataUtils.getInstance().setIndex(-1);
                    List<PrivateMedia> imageData=new ArrayList<>();
                    imageData.add(privateMedia);
                    VideoDataUtils.getInstance().setVideoData(imageData,0);
                    new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 2018/11/5 在有些机型上面转场动画返回会报 NullPointerException 异常,可能是转场的ImageView被ViewPager生命周期回收
                            VerticalImagePreviewActivity.start(PersonCenterActivity.this,mToUserid,null);
                        }
                    }, SystemClock.uptimeMillis()+100);
                }
            }
        });
        //添加一个头部
        mAdapter.addHeaderView(mHeaderView);
        ListEmptyFooterView listEmptyFooterView = new ListEmptyFooterView(PersonCenterActivity.this);
        listEmptyFooterView.showEmptyView(true);
        listEmptyFooterView.setBackgroundColor(getResources().getColor(R.color.background_dark));
        //添加一个底部
        mAdapter.addFooterView(listEmptyFooterView);
        //界面事件处理
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_chat:
                        try {
                            if(null!=mHeaderView.getTag()){
                                PersonCenterInfo userInfo = (PersonCenterInfo) mHeaderView.getTag();
                                ChatActivity.navToChat(PersonCenterActivity.this, mToUserid, userInfo.getNickname(),TIMConversationType.C2C);
                            }else{
                                ChatActivity.navToChat(PersonCenterActivity.this, mToUserid, TIMConversationType.C2C);
                            }
                        } catch (Exception e) {

                        }
                        break;
                    case R.id.btn_back:
                        finish();
                        break;
                    case R.id.btn_menu:
                        initPopwindow(v);
                        break;
                    case R.id.btn_video:
                        MobclickAgent.onEvent(getContext(), "call_video_person_center");
                        if(null!=mHeaderView&&null!=mHeaderView.getTag()){
                            final PersonCenterInfo data = (PersonCenterInfo) mHeaderView.getTag();
                            if(mComeFrom>0){
                                finish();
                                return;
                            }
                            LiveRoomPullActivity activity = LiveRoomPullActivity.getInstance();
                            if(null!=activity){
                                activity.finish();
                            }
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //如果正在直播，去直播间
                                    if(2==data.getIdentity_audit()&&1==data.getIs_online()){
                                        RoomExtra roomExtra=new RoomExtra();
                                        roomExtra.setUserid(data.getUserid());
                                        roomExtra.setNickname(data.getNickname());
                                        roomExtra.setAvatar(data.getAvatar());
                                        roomExtra.setFrontcover(TextUtils.isEmpty(data.getFrontcover())?data.getAvatar():data.getFrontcover());
                                        LiveRoomPullActivity.start(PersonCenterActivity.this, roomExtra);
                                        return;
                                    }
                                    //视频通话请求
                                    CallExtraInfo callExtraInfo=new CallExtraInfo();
                                    callExtraInfo.setToUserID(data.getUserid());
                                    callExtraInfo.setToNickName(data.getNickname());
                                    callExtraInfo.setToAvatar(data.getAvatar());
                                    if (null!=data.getImage_list()&&data.getImage_list().size()>0 && data.getImage_list().get(0) != null
                                            && !TextUtils.isEmpty(data.getImage_list().get(0).getImg_path())) {
                                        callExtraInfo.setAnchorFront(data.getImage_list().get(0).getFile_path());
                                    }
                                    MakeCallManager.getInstance().attachActivity(PersonCenterActivity.this).mackCall(callExtraInfo, 1);
                                }
                            },200);
                        }
                        break;
                    case R.id.btn_suspend_view:
                        VipActivity.startForResult(PersonCenterActivity.this,1);
                        break;
                    case R.id.btn_close:
                        AnimationUtil.goneTransparentView(bindingView.btnSuspendView);
                        break;
                        //礼物
                    case R.id.btn_gift:
                        PusherInfo pusherInfo=new PusherInfo();
                        pusherInfo.setUserID(mToUserid);
                        if(null!=mHeaderView&&null!=mHeaderView.getTag()){
                            PersonCenterInfo personCenterInfo = (PersonCenterInfo) mHeaderView.getTag();
                            pusherInfo.setUserName(personCenterInfo.getNickname());
                        }
                        showGiftBoardView(pusherInfo);
                        break;
                }
            }
        };
        bindingView.btnMenu.setColorFilter(Color.parseColor("#FFFFFF"));
        bindingView.btnChat.setOnClickListener(onClickListener);
        bindingView.btnGift.setOnClickListener(onClickListener);
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnMenu.setOnClickListener(onClickListener);
        bindingView.btnVideo.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.toolBarView.setBackgroundResource(R.drawable.bg_black_shape_ungratien);
        // TODO: 2018/11/28 手势渐变
//        bindingView.toolBarView.setBackgroundResource(R.drawable.home_top_bar_bg_shape);
//        bindingView.toolBarView.getBackground().setAlpha(0);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mHeaderView.measure(width,width);
//        mHeaderHeight=mHeaderView.getMeasuredHeight();
//        bindingView.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(null!=mLinLayoutManager&&null!=bindingView){
//                    mDistance += dy;
//                    if(mDistance>mHeaderHeight) mDistance=mHeaderHeight;
//                    if(mDistance<0) mDistance=0;
//                    int position = mLinLayoutManager.findFirstVisibleItemPosition();
//                    if(position>0){
//                        bindingView.toolBarView.getBackground().setAlpha(255);
//                    }else{
//                        //标题栏渐变 1/2 速度
//                        if(mDistance<=mHeaderHeight){
//                            float scale = (float) mDistance / mHeaderHeight;
//                            float alpha = (scale * 255);
//                            Logger.d(TAG,"scrollY:"+mDistance+",scale:"+scale+",alpha:"+alpha);
//                            bindingView.toolBarView.getBackground().setAlpha((int) alpha);
//                        }
//                    }
//                }
//            }
//        });
        //下拉刷新监听
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter){
                    mPresenter.getPersonCenterInfo(mToUserid);
                }
            }
        });
        //本地赠送礼物处理
        bindingView.giftGroup.getLayoutParams().height=mHeaderView.getMeasuredHeight();
        mLocSingleManager = new MediaGiftItemSingleManager(getContext());
        bindingView.giftLocation.addView(mLocSingleManager);
        //礼物场景给定
        bindingView.countdownView.setApiMode(LiveGiftDialog.GIFT_MODE_PRIVATE_USER);
        bindingView.countdownView.setOnGiftSendListener(new CountdownGiftView.OnCountdownGiftSendListener() {
            @Override
            public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                startGiftAnimation(data,accepUserInfo,count);
            }
        });
    }

    /**
     * 显示礼物面板
     * 赠送礼物，本地负责先播放动画，远程端交由后台去推送礼物赠送消息
     * @param pusherInfo
     */
    private void showGiftBoardView(PusherInfo pusherInfo) {
        if(null==getContext()) return;
        FragmentActivity context = (FragmentActivity) getContext();
        if(!context.isFinishing()){
            LiveGiftDialog fragment = LiveGiftDialog.getInstance(context,pusherInfo, "",LiveGiftDialog.GIFT_MODE_PRIVATE_USER,true,-1);
            fragment.setOnGiftSelectedListener(new LiveGiftDialog.OnGiftSelectedListener() {
                @Override
                public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                    startGiftAnimation(data,accepUserInfo,count);
                }

                @Override
                public void onDissmiss() {

                }
                //礼物选择发生了变化
                @Override
                public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo) {
                    bindingView.countdownView.updataView(giftInfo,"",count,accepUserInfo);
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
            mediaGiftInfo.setGift_src(data.getSrc());
            mediaGiftInfo.setCmd(Constant.MSG_CUSTOM_GIFT);
            if(null!=mLocSingleManager) mLocSingleManager.addGiftToTask(mediaGiftInfo);
        }
    }

    @Override
    public void initData() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mHeaderView) mHeaderView.onResume();
        //销毁回显状态的数据
        if(-1==VideoDataUtils.getInstance().getIndex()){
            VideoDataUtils.getInstance().setPosition(0);
            VideoDataUtils.getInstance().setIndex(0);
            VideoDataUtils.getInstance().setFileType(0);
            VideoDataUtils.getInstance().setHostUrl(null);
        }
    }

    @Override
    protected void onPause() {
        if(null!=mHeaderView) mHeaderView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mHeaderView) mHeaderView.onDestroy();
        if(null!=bindingView) bindingView.countdownView.onDestroy();
        if(null!=mLocSingleManager) mLocSingleManager.onDestroy();
        super.onDestroy();
        MakeCallManager.getInstance().onDestroy();
        mComeFrom=0;
    }

    /**
     * 右上角菜单
     * @param v
     */
    private void initPopwindow(View v) {
        if(null == popupWindow){
            View view = View.inflate(this, R.layout.person_center_menu, null);
            TextView tvBlack = view.findViewById(R.id.tv_blacklist);
            TextView tvReport = view.findViewById(R.id.tv_report);
            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setFocusable(true);//获得焦点，才能让View里的点击事件生效
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);
                }
            });
            View.OnClickListener onClickListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_blacklist:
                            UserManager.getInstance().addBlackList(mToUserid, 1, new UserServerContract.OnNetCallBackListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    ToastUtils.showCenterToast("加入黑名单成功");
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {

                                }
                            });

                            break;
                        case R.id.tv_report:
                            UserManager.getInstance().reportUser(mToUserid, new UserServerContract.OnNetCallBackListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    if (!PersonCenterActivity.this.isFinishing())
                                        CommenNoticeDialog.getInstance(PersonCenterActivity.this).setTipsData("举报成功", getResources().getString(R.string.report_user_success), "确定").setOnSubmitClickListener(new CommenNoticeDialog.OnSubmitClickListener() {
                                            @Override
                                            public void onSubmit() {
                                                //处理确认点击事件
                                                if (popupWindow != null && popupWindow.isShowing()) {
                                                    popupWindow.dismiss();
                                                }
                                            }
                                        }).show();
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            });

                            break;
                        case R.id.tv_cancel:

                            break;
                    }
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            };
            tvBlack.setOnClickListener(onClickListener);
            tvReport.setOnClickListener(onClickListener);
            tvCancel.setOnClickListener(onClickListener);
        }
        popupWindow.showAtLocation(v, Gravity.TOP, ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(135f),bindingView.toolBarView.getMeasuredHeight()+ScreenUtils.dpToPxInt(10f));
        setBackgroundAlpha(0.5f);
    }

    @Override
    protected void buyVipSuccess() {
        super.buyVipSuccess();
        if(null!=bindingView) bindingView.btnSuspendView.setVisibility(View.GONE);
    }


    /**
     * 用户信息回调
     * @param data
     */
    @Override
    public void showPersonInfo(PersonCenterInfo data) {
        if(null==data) return;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=mAdapter) mAdapter.setNewData(null);
        List<IndexMineUserInfo> personItemList = PersonCenterManager.createPersonItemList(mToUserid, data);
        //主播身份 的粉丝贡献榜
        if(2==data.getIdentity_audit()){
            personItemList.get(4).setFansInfos(data.getPoints_list());
        //普通身份
        }else{
            personItemList.get(3).setFansInfos(data.getPoints_list());
        }
        if(null!=mAdapter) mAdapter.setNewData(personItemList);
        //相册
        if(null!=mHeaderView) {
            mHeaderView.setTag(data);
            mHeaderView.setUserData(data);
            List<PrivateMedia> headImages=new ArrayList<>();
            if(null!=data.getImage_list()&&data.getImage_list().size()>0){
                headImages.addAll(data.getImage_list());
            }else{
                PrivateMedia privateMedia=new PrivateMedia();
                privateMedia.setFile_type(0);
                privateMedia.setUserid(mToUserid);
                privateMedia.setNickname(data.getNickname());
                privateMedia.setAvatar(data.getAvatar());
                privateMedia.setImg_path(data.getAvatar());
                privateMedia.setFile_path(data.getAvatar());
                headImages.add(privateMedia);
            }
            mHeaderView.setUserHeads(headImages);
        }

        //查看对方且对方是经过认证的主播
        if(!TextUtils.equals(UserManager.getInstance().getUserId(),data.getUserid())&&2==data.getIdentity_audit()){
            bindingView.llFollowView.setVisibility(View.VISIBLE);
            //非会员提示悬浮层
//            if(!UserManager.getInstance().isVip()){
//                bindingView.tipsUserContent.setText(Html.fromHtml("小哥哥,开通VIP,1对1视频通话<font color='#ED5F82'>享受五折</font>哟.."));
//                AnimationUtil.visibTransparentView(bindingView.btnSuspendView);
//                Glide.with(PersonCenterActivity.this)
//                        .load(data.getAvatar())
//                        .placeholder(R.drawable.ic_user_head_default)
//                        .error(R.drawable.ic_user_head_default)
//                        .animate(R.anim.item_alpha_in)//加载中动画
//                        .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
//                        .centerCrop()//中心点缩放
//                        .skipMemoryCache(true)//跳过内存缓存
//                        .transform(new GlideCircleTransform(getContext()))
//                        .into(bindingView.tipsUserHead);
//            }
        }
    }

    @Override
    public void showPersonInfoError(int code, String msg) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter&&mAdapter.getData().size()==0){
            if(null!=mEmptyView) mEmptyView.showErrorView(msg);
        }
    }

    @Override
    public void showPersonList(List<TabMineUserInfo> data) {

    }

    @Override
    public void showPersonListError(int code, String msg) {

    }

    @Override
    public void showErrorView() {
    }

    @Override
    public void complete() {

    }
}