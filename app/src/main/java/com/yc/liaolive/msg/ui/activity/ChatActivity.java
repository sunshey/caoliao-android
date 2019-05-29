package com.yc.liaolive.msg.ui.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaikai.securityhttp.utils.LogUtil;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.AttentInfo;
import com.yc.liaolive.bean.NoticeContent;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.gift.ui.dialog.LiveGiftDialog;
import com.yc.liaolive.gift.view.AnimatorSvgaPlayerManager;
import com.yc.liaolive.gift.view.CountdownGiftView;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.GiftInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.live.util.FileUtil;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ScreenLayoutChangedHelp;
import com.yc.liaolive.msg.adapter.ChatAdapter;
import com.yc.liaolive.msg.iView.FriendInfoView;
import com.yc.liaolive.msg.iView.FriendshipMessageView;
import com.yc.liaolive.msg.manager.ChatManager;
import com.yc.liaolive.msg.manager.ChatPresenter;
import com.yc.liaolive.msg.manager.FriendManager;
import com.yc.liaolive.msg.manager.VoiceModelManager;
import com.yc.liaolive.msg.model.CustomMessage;
import com.yc.liaolive.msg.model.FileMessage;
import com.yc.liaolive.msg.model.FriendProfile;
import com.yc.liaolive.msg.model.FriendshipInfo;
import com.yc.liaolive.msg.model.GroupInfo;
import com.yc.liaolive.msg.model.ImageMessage;
import com.yc.liaolive.msg.model.Message;
import com.yc.liaolive.msg.model.MessageFactory;
import com.yc.liaolive.msg.model.TextMessage;
import com.yc.liaolive.msg.model.VideoMessage;
import com.yc.liaolive.msg.model.VipMessage;
import com.yc.liaolive.msg.model.VoiceMessage;
import com.yc.liaolive.msg.model.bean.ChatGiftMessage;
import com.yc.liaolive.msg.model.bean.ChatParams;
import com.yc.liaolive.msg.view.ChatInput;
import com.yc.liaolive.msg.view.TemplateTitle;
import com.yc.liaolive.msg.view.VoiceSendingView;
import com.yc.liaolive.observer.RefreshEvent;
import com.yc.liaolive.permissions.RXPermissionManager;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.activity.IntegralTopListActivity;
import com.yc.liaolive.ui.activity.LiteBindPhoneActivity;
import com.yc.liaolive.ui.contract.ChatView;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.ui.dialog.CommonMenuDialog;
import com.yc.liaolive.ui.dialog.LoginAwardDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.InputTools;
import com.yc.liaolive.util.MaterialUtils;
import com.yc.liaolive.util.MediaUtil;
import com.yc.liaolive.util.RecorderUtil;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.StatusUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * 私信、群聊会话界面
 */

public class ChatActivity extends TopBaseActivity implements ChatView, FriendshipMessageView, FriendInfoView{

    private static final String TAG = "ChatActivity";
    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;
    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;
    private Handler handler = new Handler();
    private int mIsFollow;//是否已关注此用户
    private int mIsBlack;//是否在黑名单中 0:不在 1：在
    private TemplateTitle title;
    //任务相关
    private CountdownGiftView mGiftView;
    private ScreenLayoutChangedHelp mLayoutChangedListener;
    private AnimatorSvgaPlayerManager mSvgaPlayerManager;//豪华礼物
    private ImageView btnGift, btnCall;
    //视频通话功能
    private boolean callEnable=false;

    public static void navToChat(Context context, String identify, String nickName,TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("nickName", nickName);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void navToChat(String identify, String nickName,TIMConversationType type) {
        Intent intent = CaoliaoController.createIntent(ChatActivity.class.getName());
        intent.putExtra("identify", identify);
        intent.putExtra("nickName", nickName);
        intent.putExtra("type", type);
        CaoliaoController.startActivity(intent);
    }

    public static void navToChat(Context context, String identify, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void navToChat(Context context, String identify, boolean isService, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        intent.putExtra("isService", isService);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            MaterialUtils.assistActivity(this);
        }
        StatusUtils.setStatusTextColor1(true,ChatActivity.this);//白色背景，黑色字体
        identify = getIntent().getStringExtra("identify");
        if(TextUtils.equals(identify,UserManager.getInstance().getUserId())){
            ToastUtils.showCenterToast("对自己只能说悄悄话~");
            finish();
            return;
        }
        String nickName = getIntent().getStringExtra("nickName");
        boolean isService = getIntent().getBooleanExtra("isService", false);
        if (null == identify) {
            ToastUtils.showCenterToast("用户信息错误");
            finish();
            return;
        }
        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        //检查是否关注此用户,是否在自己的黑名单，对方是否是主播
        UserManager.getInstance().followUser(identify, 2, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if (null != object && object instanceof AttentInfo) {
                    AttentInfo attentInfo = (AttentInfo) object;
                    mIsFollow = attentInfo.getIs_attent();
                    mIsBlack = Integer.parseInt(attentInfo.getIs_black());
                    if(callEnable){
                        btnCall.setVisibility(TextUtils.equals("2",attentInfo.getIdentity_audit())?View.VISIBLE:View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(int code, String errorMsg) {
                ToastUtils.showCenterToast(errorMsg);
            }
        });
        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        input.setIdentify(identify);
        mSvgaPlayerManager = (AnimatorSvgaPlayerManager) findViewById(R.id.svga_animator);
        mSvgaPlayerManager.onReset();
        listView = (ListView) findViewById(R.id.list);
        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        adapter.setOnUserClickListener(new ChatAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(String userID) {
                //拦截客服
                if (TextUtils.equals(UserManager.getInstance().getServerIdentify(), userID)) return;
                PersonCenterActivity.start(ChatActivity.this, userID);
            }

            @Override
            public void onSendFail(Message message) {
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
            }
        });
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int firstItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    input.hideInput();
                }
                return false;
            }
        });
        registerForContextMenu(listView);
        title = (TemplateTitle) findViewById(R.id.chat_title);
        title.setBackImgColor(Color.parseColor("#666666"));
        title.setMoreImgColor(Color.parseColor("#666666"));
        title.setTitleColor(Color.parseColor("#333333"));
        if (isService) {
            title.setMoreImgVisable(View.GONE);
            input.isMoreShow(false);
        }
        switch (type) {
            //单聊界面
            case C2C:
                title.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<VideoDetailsMenu> chatC2CMenu = Utils.createChatC2CMenu(mIsFollow, mIsBlack);//菜单列表
                        showActionMenu(chatC2CMenu);
                    }
                });
                //设置标题栏

                FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);

                if (profile == null) {
                    profile = FriendManager.getInstance().getFriendShipsById(identify);
                }
                if(!TextUtils.isEmpty(nickName)){
                    titleStr = nickName;
                }else{
                    titleStr = TextUtils.equals(UserManager.getInstance().getServerIdentify(), identify) ? "官方客服" : profile == null ? identify : profile.getName();
                }
                title.setTitleText(titleStr);
                break;
            //群聊天界面
            case Group:
                title.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<VideoDetailsMenu> chatC2CMenu = Utils.createChatC2CMenu(1, mIsBlack);//菜单
                        showActionMenu(chatC2CMenu);
                    }
                });
                title.setTitleText(GroupInfo.getInstance().getGroupName(identify));
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        voiceSendingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        presenter.start();

        //进入客服会话
        if (TextUtils.equals(UserManager.getInstance().getServerIdentify(), identify)) {
            UserManager.getInstance().enterConversation(identify, null);
        }
        //倒计时赠送按钮
        mGiftView = (CountdownGiftView) findViewById(R.id.view_countdown_view);
        mGiftView.setApiMode(LiveGiftDialog.GIFT_MODE_PRIVATE_CHAT);
        mGiftView.setOnGiftSendListener(new CountdownGiftView.OnCountdownGiftSendListener() {
            @Override
            public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                //本端推送礼物消息
                CustomMsgExtra customMsgExtra=new CustomMsgExtra();
                customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
                if(null!=accepUserInfo){
                    customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
                    customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
                    customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
                }
                data.setCount(count);
                data.setSource_room_id(identify);
                CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
                customMsgInfo.setMsgContent("赠送" + count + "个" + data.getTitle());
                customMsgInfo.setAccapGroupID(identify);
                sendCustomMessage(customMsgInfo);
            }
        });
        mLayoutChangedListener=ScreenLayoutChangedHelp.get(ChatActivity.this).setOnSoftKeyBoardChangeListener(new ScreenLayoutChangedHelp.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {

            }

            @Override
            public void keyBoardHide(int height) {
                setSolveNavigationBar();
            }
        });
        //公告
        NoticeContent noticeMessage = UserManager.getInstance().getNoticeMessage(UserManager.NoticeType.Chat);
        ((TextView) findViewById(R.id.btn_notice_content)).setText(noticeMessage.getContent());
        findViewById(R.id.btn_notice_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.ll_notice_layout).setVisibility(View.GONE);
            }
        });
        init();
        EventBus.getDefault().register(this);
    }

    private void init () {
        btnGift = (ImageView) findViewById(R.id.btn_send_gift);
        btnGift.setOnClickListener(new PerfectClickListener(300) {
            @Override
            protected void onNoDoubleClick(View v) {
                FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
                PusherInfo pusherInfo = new PusherInfo();
                pusherInfo.setUserID(identify);
                pusherInfo.setUserName(null == profile ? identify : profile.getName());

                LiveGiftDialog fragment = LiveGiftDialog.getInstance(ChatActivity.this,pusherInfo, true);
                fragment.setOnGiftSelectedListener(new LiveGiftDialog.OnGiftSelectedListener() {
                    @Override
                    public void onSendEvent(GiftInfo data, int count, int totalPrice, PusherInfo accepUserInfo) {
                        super.onSendEvent(data, count, totalPrice, accepUserInfo);
                        //本端推送礼物消息
                        CustomMsgExtra customMsgExtra=new CustomMsgExtra();
                        customMsgExtra.setCmd(Constant.MSG_CUSTOM_GIFT);
                        if(null!=accepUserInfo){
                            customMsgExtra.setAccapUserID(accepUserInfo.getUserID());
                            customMsgExtra.setAccapUserName(accepUserInfo.getUserName());
                            customMsgExtra.setAccapUserHeader(accepUserInfo.getUserAvatar());
                        }
                        data.setCount(count);
                        data.setSource_room_id(identify);
                        CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, data);
                        customMsgInfo.setMsgContent("赠送" + count + "个" + data.getTitle());
                        customMsgInfo.setAccapGroupID(identify);
                        //避免本地数量对不上，先开启倒计时赠送礼物按钮，再走本地动画播放逻辑
                        if(null!=mGiftView) mGiftView.updataView(data,"",count,accepUserInfo);
                        sendCustomMessage(customMsgInfo);
                    }
                    //礼物选择个数发生了变化
                    @Override
                    public void selectedCountChanged(GiftInfo giftInfo,int count,PusherInfo accepUserInfo) {
                        if(null!=mGiftView) mGiftView.updataView(giftInfo,"",count,accepUserInfo);
                    }
                });
                fragment.show();
            }
        });

        btnCall = (ImageView) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveRoomPullActivity activity = LiveRoomPullActivity.getInstance();
                if(null!=activity){
                    activity.finish();
                }
                MobclickAgent.onEvent(ChatActivity.this, "call_video_chat");
                FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
                CallExtraInfo callExtraInfo=new CallExtraInfo();
                callExtraInfo.setToUserID(identify);
                callExtraInfo.setToNickName(null == profile ? identify : profile.getName());
                callExtraInfo.setToAvatar(null == profile ? "" : profile.getAvatarUrl());
                MakeCallManager.getInstance().attachActivity(ChatActivity.this).mackCall(callExtraInfo, 1);
            }
        });

        input.setmBtnTouchListener(new ChatInput.OnVoiceBtnTouchListener() {
            @Override
            public boolean touchBtn() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hasPermission = ChatActivity.this.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        requestAudioPermission();
                    }
                    return hasPermission == PackageManager.PERMISSION_GRANTED;
                }
                return true;
            }
        });
        List<String> chatController = UserManager.getInstance().getChatController();
        if(null!=chatController&&chatController.size()>0){
            for (String id : chatController) {
                if(id.equals("1")){
                    btnGift.setVisibility(View.VISIBLE);
                }else if(id.equals("2")){
                    callEnable=true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //销毁从会话界面跳转至视频播放界面的列表数据
        if(-2==VideoDataUtils.getInstance().getIndex()){
            VideoDataUtils.getInstance().setVideoData(null,0);
            VideoDataUtils.getInstance().setPosition(0);
            VideoDataUtils.getInstance().setIndex(0);
            VideoDataUtils.getInstance().setHostUrl(null);
        }
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onResume();
        setSolveNavigationBar();
    }

    public void setSolveNavigationBar(){
        if(InputTools.checkDeviceHasNavigationBar(ChatActivity.this)){
            InputTools.solveNavigationBar(getWindow());
        }
    }

    /**
     * 显示消息,网络的
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            final Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                //自定义消息特殊处理
                if (mMessage instanceof CustomMessage) {
                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
                    switch (messageType) {
                        //对方正在输入的消息
                        case INPUTING:
                            title.setTitleText(getString(R.string.chat_typing));
                            if(null!=handler){
                                handler.removeCallbacks(resetTitle);
                                handler.postDelayed(resetTitle, 3000);
                            }
                            break;
                        //礼物、中奖、视频通话、多媒体消息推送
                        case CHAT_GIFT:
                        case AWARD:
                        case CALL:
                        case MEDIA:
                            refreshAdapter(mMessage);
                            break;
                        //直播邀请
                        case LIVE:
                            title.setTitleText(getString(R.string.chat_typing));
                            if(null!=handler){
                                Runnable liveRunnable = liveRunable(mMessage);
                                handler.removeCallbacks(resetTitle);
                                handler.postDelayed(resetTitle, 1500);
                                handler.removeCallbacks(liveRunnable);
                                handler.postDelayed(liveRunnable, 1500);
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    refreshAdapter(mMessage);
                }
            }
        }
    }

    /**
     * 显示菜单
     *
     * @param chatC2CMenu
     */
    private void showActionMenu(final List<VideoDetailsMenu> chatC2CMenu) {
        CommonMenuDialog.getInstance(ChatActivity.this)
                .setData(chatC2CMenu)
                .setCancleText("取消")
                .setCancleTextColor(getResources().getColor(R.color.app_red_style))
                .setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int itemID, VideoDetailsMenu videoDetailsMenu) {
                        switch (itemID) {
                            //粉丝贡献榜
                            case 1:
                                IntegralTopListActivity.start(ChatActivity.this, identify);
                                break;
                            //查看资料
                            case 2:
                                PersonCenterActivity.start(ChatActivity.this, identify);
                                break;
                            //添加关注
                            case 3:
                                UserManager.getInstance().followUser(identify, 0 == mIsFollow ? 1 : 0, new UserServerContract.OnNetCallBackListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        ToastUtils.showCenterToast("关注成功");
                                        mIsFollow = (0 == mIsFollow ? 1 : 0);
                                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_FOLLOW_CHANGE);
                                        VideoApplication.getInstance().setMineRefresh(true);
                                    }

                                    @Override
                                    public void onFailure(int code, String errorMsg) {
                                        ToastUtils.showCenterToast(errorMsg);
                                    }
                                });
                                break;

                            //添加好友至黑名单
                            case 4:
                                QuireDialog.getInstance(ChatActivity.this).setTitleText("添加黑名单提示").setContentText("将好友添加至黑名单后，他(她)将无法向您发送消息，确定继续？").setSubmitTitleText("确定").setCancelTitleText("取消").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                                    @Override
                                    public void onConsent() {
                                        UserManager.getInstance().addBlackList(identify, 0 == mIsBlack ? 1 : 0, new UserServerContract.OnNetCallBackListener() {
                                            @Override
                                            public void onSuccess(Object object) {
                                                mIsBlack = (0 == mIsBlack ? 1 : 0);
                                                ToastUtils.showCenterToast("加入黑名单成功");
                                            }

                                            @Override
                                            public void onFailure(int code, String errorMsg) {
                                                ToastUtils.showCenterToast(errorMsg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onRefuse() {

                                    }
                                }).show();
                                break;
                            //清空聊天记录
                            case 5:
                                QuireDialog.getInstance(ChatActivity.this).setTitleText("聊天记录删除提示").setContentText("清空与此用户的聊天记录将无法恢复，确定继续吗？").setSubmitTitleText("确定").setCancelTitleText("取消").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                                    @Override
                                    public void onConsent() {
                                        boolean messageByIdentify = ChatManager.getInstance().removeMessageByIdentify(identify);
                                        presenter.deleteMessage(messageList);
                                        FriendManager.getInstance().removeFriendShip(identify);
                                        if (messageByIdentify) {
                                            if (null != messageList)
                                                messageList.clear();
                                            if (null != adapter)
                                                adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onRefuse() {

                                    }
                                }).show();
                                break;
                            //举报
                            case 6:
                                UserManager.getInstance().reportUser(identify, new UserServerContract.OnNetCallBackListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        try {
                                            if (!ChatActivity.this.isFinishing())
                                                CommenNoticeDialog.getInstance(ChatActivity.this).setTipsData("举报成功", getResources().getString(R.string.report_user_success), "确定").setOnSubmitClickListener(new CommenNoticeDialog.OnSubmitClickListener() {
                                                    @Override
                                                    public void onSubmit() {
                                                        //处理确认点击事件
                                                    }
                                                }).show();
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(int code, String errorMsg) {
                                        ToastUtils.showCenterToast(errorMsg);
                                    }
                                });
                                break;
                            //将好友从黑名单中移除
                            case 7:
                                UserManager.getInstance().removeBlackList(identify, new UserServerContract.OnNetCallBackListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        mIsBlack = 0;
                                        ToastUtils.showCenterToast("已从黑名单中移除");
                                    }

                                    @Override
                                    public void onFailure(int code, String errorMsg) {
                                        ToastUtils.showCenterToast(errorMsg);
                                    }
                                });
                                break;
                        }
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VoiceModelManager.getInstance().onPause();
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        if(null!=input&&null!=presenter){
            if (input.getText().length() > 0) {
                TextMessage message = new TextMessage(input.getText());
                presenter.saveDraft(message.getMessage());
            } else {
                presenter.saveDraft(null);
            }
            MediaUtil.getInstance().stop();
            presenter.readMessages();
        }
        //刷新未读消息等
        RefreshEvent.getInstance().onRefresh();
        //客服会话消息置为全部已读并更新界面
        if(!TextUtils.isEmpty(identify)&&TextUtils.equals(identify,UserManager.getInstance().getServerIdentify())){
            SharedPreferencesUtil.getInstance().putInt(Constant.KET_SERVER_MSG_COUNT, 0);
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_MSG_UNREAD_COUND);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(null!=presenter) presenter.stop();
        if(null!=mLayoutChangedListener) mLayoutChangedListener.setOnSoftKeyBoardChangeListener(null);
        if(null!=mGiftView) mGiftView.onDestroy();
        mLayoutChangedListener=null;mGiftView=null;
        MakeCallManager.getInstance().onDestroy();
        if(null!=handler&&null!=resetTitle) handler.removeCallbacks(resetTitle);
        if(null!=mSvgaPlayerManager) mSvgaPlayerManager.onDestroy(); mSvgaPlayerManager = null;
    }


    /**
     * VIP充值成功
     */
    @Subscriber (tag = "VIP_RECHARGE_SUCCESS")
    private void vipRechargeSuccess (boolean success) {
        if (adapter != null) {
            if (messageList.size() > 0) {
                Message lastMsg = messageList.get(messageList.size() - 1);
                if (lastMsg != null && lastMsg.getLocalType() == -1) {
                    messageList.remove(lastMsg);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 刷新数据
     * @param mMessage
     */
    private void refreshAdapter(Message mMessage) {
        if (messageList.size() == 0) {
            mMessage.setHasTime(null);
        } else {
            mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
        }
        if (messageList.size() > 0) {
            Message lastMsg = messageList.get(messageList.size() - 1);
            if (lastMsg != null && lastMsg.getLocalType() == -1) {
                messageList.add(messageList.size() - 1, mMessage);
            } else {
                addVipMessage(mMessage);
                messageList.add(mMessage);
            }
        } else {
            addVipMessage(mMessage);
            messageList.add(mMessage);
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);
    }

    private void addVipMessage(Message message) {
        if(null==message) return;
        if (!UserManager.getInstance().isVip()) {
            message.setLocalType(-1);
            messageList.add(message);
        }
    }

    private void playGiftAnimation(CustomMessage customMessage) {
        ChatGiftMessage chatGiftMessage = customMessage.getChatGiftMessage();
        if(null!=chatGiftMessage&&!TextUtils.isEmpty(chatGiftMessage.getUrl())){
            CustomMsgInfo extra = new CustomMsgInfo();
            GiftInfo giftInfo = new GiftInfo();
            giftInfo.setBigSvga(chatGiftMessage.getUrl());
            extra.setGift(giftInfo);
            if(null!=mSvgaPlayerManager) mSvgaPlayerManager.addAnimationToTask(extra);
        }
    }


    /**
     * 显示消息 ，本地的历史消息
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        if(null != adapter && messages.size() > 0){
            int newMsgNum = 0;
            for (int i = 0; i < messages.size(); ++i) {
                Message mMessage = MessageFactory.getMessage(messages.get(i));
                if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                    continue;
                ++newMsgNum;
                if (i != messages.size() - 1) {
                    if (i == 0 && mMessage instanceof CustomMessage && ((CustomMessage) mMessage).getType() == CustomMessage.Type.CHAT_GIFT) {
                        playGiftAnimation((CustomMessage) mMessage);
                    }
                    mMessage.setHasTime(messages.get(i + 1));
                    messageList.add(0, mMessage);
                } else {
                    mMessage.setHasTime(null);
                    messageList.add(0, mMessage);
                }
            }
            addVipMessage(MessageFactory.getMessage(messages.get(0)));
            adapter.notifyDataSetChanged();
            listView.setSelection(newMsgNum);
        } else if (null != adapter && messageList.size() == 0){
            if (!UserManager.getInstance().isVip()) {
                Message vipMssage = new VipMessage(new TIMMessage());
                vipMssage.setLocalType(-1);
                messageList.add(vipMssage);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage
            message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        if(null==input) return;
        //<font>发送消息，需要打赏1000钻石<br/>开通会员<font color='#FF6666'>可免费畅聊</font></font>
        if (TextUtils.isEmpty(input.getText().toString().trim())) {
            ToastUtils.showCenterToast("请输入发送内容");
            return;
        }
        //对方是客服身份直接端对端
        if(TextUtils.equals(identify,UserManager.getInstance().getServerIdentify())){
            if(null!=presenter){
                Message message = new TextMessage(input.getText());
                presenter.sendMessage(message.getMessage());
                input.setText("");
            }
            return;
        }
        //自己是客服身份直接端对端
        if(UserManager.getInstance().getUserId().equals(UserManager.getInstance().getServerIdentify())){
            if(null!=presenter){
                Message message = new TextMessage(input.getText());
                presenter.sendMessage(message.getMessage());
                input.setText("");
            }
            return;
        }
        //交由服务端发送纯文本消息
        UserManager.getInstance().sendMsg(identify, input.getText().toString(), 0, 0, new UserServerContract.OnSendMessageCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                VideoApplication.getInstance().setMineRefresh(true);
            }

            @Override
            public void onFailure(int code, String content,String errorMsg) {
                if(null!=input) input.setText(content);
                if(NetContants.API_RESULT_ARREARAGE_CODE==code){
                    showVipTipsDialog(errorMsg);
                    return;
                }
                if(NetContants.API_RESULT_NO_BIND_PHONE==code){
                    LiteBindPhoneActivity.start(AppEngine.getApplication().getApplicationContext());
                    return;
                }
                ToastUtils.showCenterToast(errorMsg);
            }
        });
        input.setText("");
    }

    /**
     * 显示非VIP不可发送消息的提示
     * @param errorMsg 服务端返回错误文案
     */
    private void showVipTipsDialog (String errorMsg) {
        QuireDialog.getInstance(ChatActivity.this)
                .setTitleText("钻石不足")
                .setContentText(errorMsg)
                .setSubmitTitleText("开通会员")
                .setCancelTitleText("充值钻石")
                .setContentTextColor(Color.parseColor("#333333"))
                .setCancelTitleTextColor(Color.parseColor("#999999"))
                .setSubmitTitleTextColor(Color.parseColor("#ff6666"))
                .setDialogCancelable(true)
                .showCloseBtn(true)
                .setDialogCanceledOnTouchOutside(false)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        VipActivity.startForResult(ChatActivity.this,1);
                    }

                    @Override
                    public void onRefuse() {
                        VipActivity.startForResult(ChatActivity.this,0);
                    }
                }).show();
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * 开始录音
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        recorder.startRecording();
    }

    @Override
    public void showStartVoiceView() {
        voiceSendingView.showRecording();
    }

    /**
     * 结束录音，开始发送语音
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            showProgressDialog("发送中...",true);
            //交由服务端扣费
            UserManager.getInstance().sendMsg(identify, input.getText().toString(), 3, 0, new UserServerContract.OnSendMessageCallBackListener() {
                @Override
                public void onSuccess(Object object) {
                    closeProgressDialog();
                    VideoApplication.getInstance().setMineRefresh(true);
                    if(null!=presenter){
                        if(null!=object&&object instanceof ChatParams){
                            ChatParams chatParams= (ChatParams) object;
                            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath(),chatParams);
                            presenter.sendMessage(message.getMessage());
                        }else{
                            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
                            presenter.sendMessage(message.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(int code, String content,String errorMsg) {
                    closeProgressDialog();
                    if(NetContants.API_RESULT_ARREARAGE_CODE==code){
                        if(null!=input) input.setText(content);
                        showVipTipsDialog(errorMsg);
                        return;
                    }
                    if(NetContants.API_RESULT_NO_BIND_PHONE==code){
                        LiteBindPhoneActivity.start(AppEngine.getApplication().getApplicationContext());
                        return;
                    }
                    ToastUtils.showCenterToast(errorMsg);
                }
            });
        }
    }

    /**
     * 发送小视频消息
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {
        voiceSendingView.setVisibility(View.GONE);
        recorder.cancelRecording();
    }

    @Override
    public void showCancelVoiceView() {
        voiceSendingView.showCancel();
    }

    /**
     * 正在输入
     */
    @Override
    public void sending() {
//        if (type == C2C) {
//            Message message = new CustomMessage(CustomMessage.Type.INPUTING,"");
//            presenter.sendOnlineMessage(message.getMessage());
//        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }

    /**
     * 发送礼物消息
     * @param customMsgInfo
     */
    @Override
    public void sendCustomMessage(CustomMsgInfo customMsgInfo) {
        if(null!=customMsgInfo&&null!=presenter){
            CommonJson<CustomMsgInfo> request = new CommonJson<>();
            request.cmd = LiveConstant.MSG_CUSTOM_ROOM_PRIVATE_GIFT;
            request.data = customMsgInfo;
            String content = new Gson().toJson(request, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
            //构造一条消息
            Message msg = new CustomMessage(CustomMessage.Type.CHAT_GIFT, content);
            presenter.sendMessage(msg.getMessage());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        menu.add(0, 4, Menu.NONE, getString(R.string.chat_copy));
        if (message.isSendFail()) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            case 4:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", message.getSummary());
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtils.showCenterToast("复制成功");
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    if (file.length() > 1024 * 1024 * 10) {
                        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    } else {
                        Message message = new ImageMessage(path, isOri);
                        presenter.sendMessage(message.getMessage());
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        }
        //充值界面返回
        else if(resultCode==Constant.RECHARGE_RESULT_CODE&&null!=data&&TextUtils.equals(Constant.VIP_SUCCESS,data.getStringExtra("vip"))){
            try{
                if(UserManager.getInstance().isVip()){
                    UserManager.getInstance().getTasks("2", new UserServerContract.OnNetCallBackListener() {
                        @Override
                        public void onSuccess(Object object) {
                            if(null!=object && object instanceof List){
                                List<TaskInfo> taskInfos= (List<TaskInfo>) object;
                                LogUtil.msg("查询成功");
                                for (TaskInfo task : taskInfos) {
                                    if(task.getApp_id()==Constant.APP_TASK_VIP){
                                        UserManager.getInstance().drawTaskAward(task, new UserServerContract.OnNetCallBackListener() {
                                            @Override
                                            public void onSuccess(Object object) {
                                                if(null!=object&& object instanceof TaskInfo){
                                                    TaskInfo taskInfo= (TaskInfo) object;
                                                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_USER_LOCATION_INTEGRAL_CHANGED_NET);
                                                    if(!ChatActivity.this.isFinishing()){
                                                        LoginAwardDialog.getInstance(ChatActivity.this,"今日的"+taskInfo.getCoin()+"钻石已送达！",taskInfo.getCoin()).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(int code, String errorMsg) {
                                                LogUtil.msg("领取失败：code："+code+",errorMsg:"+errorMsg);
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(int code, String errorMsg) {
                        }
                    });
                }
            }catch (RuntimeException e){

            }
        }
    }

    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 10) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new FileMessage(path);
                presenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            title.setTitleText(titleStr);
        }
    };

    @NonNull
    private Runnable liveRunable(final Message mMessage) {
        return new Runnable() {
            @Override
            public void run() {
                refreshAdapter(mMessage);
            }
        };
    }

    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {}

    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {}

    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        if(null==users) return;
        for (TIMUserProfile user : users) {
            if (user.getIdentifier().equals(identify)) {
                if (type == TIMConversationType.C2C) {
                    title.setTitleText(user.getNickName());
                }
                break;
            }
        }
    }

    private void requestAudioPermission () {
        RXPermissionManager.getInstance(this)
                .requestForPermission(RXPermissionManager.PERMISSION_MICROPHONE)
                .compose(RXPermissionManager.getInstance(this)
                        .defultHandler(this))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            RXPermissionManager.getInstance(ChatActivity.this).showRejectDialog(ChatActivity.this,
                                    "没有获取到录音权限，无法使用语音发送功能。请在应用权限中打开权限", true);
                        }
                    }
                });
    }
}