package com.yc.liaolive.live.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.google.gson.Gson;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.bean.NumberChangedInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityVerticalRoomPullBinding;
import com.yc.liaolive.index.model.bean.OneListBean;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.PushMessage;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.im.IMMessageMgr;
import com.yc.liaolive.live.listener.LiveRoomActionListener;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.presenter.RoomControllerInstance;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.live.ui.pager.VerticalRoomPager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ScreenLayoutChangedHelp;
import com.yc.liaolive.media.adapter.VerticalPagerAdapter;
import com.yc.liaolive.media.view.VerticalViewPager;
import com.yc.liaolive.ui.activity.LiteBindPhoneActivity;
import com.yc.liaolive.ui.contract.IndexListContract;
import com.yc.liaolive.ui.dialog.InputKeyBoardDialog;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.IndexListPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/1/24
 * 可垂直滑动切换直播间
 */

public class VerticalRoomPullAvtivity extends TopBaseActivity implements IndexListContract.View,RoomControllerInstance, LiveRoomActionListener, Observer {

    private static final String TAG = "VerticalRoomPullAvtivity";
    //生命周期
    private static final int CHANGE_ODE_START= 1;//实例化
    private static final int CHANGE_ODE_RESUME = 2;//可见
    private static final int CHANGE_ODE_PAUSE = 3;//不可见
    private static final int CHANGE_ODE_STOP = 4;//进入销毁
    private static final int CHANGE_ODE_BACK = 5;//返回
    private static final int CHANGE_ODE_DESTROY= 6;//销毁
    private int mScrollOffsetY;//当前滚动的Y轴偏移量
    private ActivityVerticalRoomPullBinding bindingView;
    protected InputKeyBoardDialog mInputTextMsgDialog;
    private int mPosition;//要进入的房间位置、正在观看直播的房间位置
    private int mPage;//加载到了第几页
    private String mLastUserID;
    private int mVideoListSize;
    private VerticalFragmentPagerAdapter mVerticalPagerAdapter;
    private Handler mHandler;
    private Map<Integer,VerticalRoomPager> mFragments =new HashMap<>();//存放片段的集合
    private IndexListPresenter mPresenter;
    protected ScreenLayoutChangedHelp mLayoutChangedListener;//屏幕监听
    private String mType;

    public Handler getHandler(){
        if(null==mHandler){
            mHandler=new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 入口
     * @param context
     * @param type 数据类型
     * @param page 页数
     * @param position 预览的位置
     */
    public static void start(android.content.Context context, String type,int page,int position) {
        Intent intent=new Intent(context,VerticalRoomPullAvtivity.class);
        intent.putExtra("position",position);
        intent.putExtra("type",type);
        intent.putExtra("page",page);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullScreen(true);//全屏
        super.onCreate(savedInstanceState);
        bindingView= DataBindingUtil.setContentView(this, R.layout.activity_vertical_room_pull);
        VideoCallManager.getInstance().setBebusying(true);
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mPage=intent.getIntExtra("page",0);
        mType=intent.getStringExtra("type");

        List<RoomList> data = LiveRoomManager.getInstance().getData();
        mLastUserID = data.get(data.size()-1).getUserid();

        if(null== LiveRoomManager.getInstance().getData()|| LiveRoomManager.getInstance().getData().size()<=0){
            ToastUtils.showCenterToast("打开直播间失败");
            finish();
            return;
        }
        init();
        //适配器初始化
        mVerticalPagerAdapter = new VerticalFragmentPagerAdapter();
        bindingView.verticalViewPager.setOnPageChangeListener(onPageChangeListener);
        bindingView.verticalViewPager.setOffscreenPageLimit(1);
        bindingView.verticalViewPager.setAdapter(mVerticalPagerAdapter);
        bindingView.verticalViewPager.setCurrentItem(mPosition);
        onPageChangeListener.onPageSelected(mPosition);//切换至目标预览
        if(mVideoListSize==1){
            //立即加载分页数据
            if(null!=mPresenter&&!mPresenter.isLoading()){
                mPage++;
                mPresenter.getLiveLists(mLastUserID,mType,mPage);
            }
        }
        waitPlayVideo(350,mPosition);
    }

    private void init() {
        mVideoListSize= LiveRoomManager.getInstance().getData().size();
        mPresenter = new IndexListPresenter();
        mPresenter.attachView(this);
        LiveRoomManager.getInstance().getLiveRoom().setUserIdentity(LiveRoom.USER_IDENTITY_PULL);//绑定观众身份到Room
        LiveRoomManager.getInstance().getLiveRoom().onCreate();
        LiveRoomManager.getInstance().getLiveRoom().setLiveRoomListener(this);//注册房间事件监听
        ApplicationManager.getInstance().addObserver(this);
        //软键盘高度检测
        mLayoutChangedListener= ScreenLayoutChangedHelp.get(VerticalRoomPullAvtivity.this).setOnSoftKeyBoardChangeListener(new ScreenLayoutChangedHelp.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                onRoomShowInputKeyBord(true,height);
            }

            @Override
            public void keyBoardHide(int height) {
                onRoomShowInputKeyBord(false,height);
            }
        });

    }

    /**
     * 当前可见的房间实例
     * @param verticalRoomPager
     */
    @Override
    public void newRoomInstance(VerticalRoomPager verticalRoomPager) {

    }

    /**
     * 聊天
     */
    @Override
    public void onInputChatText() {
        if(null==mInputTextMsgDialog){
            //输入框
            mInputTextMsgDialog = InputKeyBoardDialog.getInstance(this)
                    .setBackgroundWindown(0.0f)
                    .setHintText("请输入聊天内容")
                    .setMode(1)
                    .setOnActionFunctionListener(new InputKeyBoardDialog.OnActionFunctionListener() {
                        //提交发射
                        @Override
                        public void onSubmit(String content, boolean tanmuOpen) {
                            sendTextMessage(content,tanmuOpen);
//                            CustomMsgExtra customMsgExtra = new CustomMsgExtra();
//                            customMsgExtra.setCmd(Constant.MSG_CUSTOM_TEXT);
//                            customMsgExtra.setMsgContent(content);
//                            customMsgExtra.setTanmu(tanmuOpen);
//                            CustomMsgInfo customMsgInfo = LiveUtils.packMessage(customMsgExtra, null);
//                            customMsgInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
//                            sendCustomMessage(customMsgInfo);
                        }
                        //告知输入框是否可发射状态
                        @Override
                        public boolean isAvailable() {
                            if(UserManager.getInstance().isVerificationPhone()&& TextUtils.isEmpty(UserManager.getInstance().getPhone())){
                                LiteBindPhoneActivity.start(AppEngine.getApplication().getApplicationContext());
                                return false;
                            }
                            return true;
                        }
                    });
            mInputTextMsgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onRoomShowInputKeyBord(false,0);
                }
            });
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth()); //设置宽度
            mInputTextMsgDialog.getWindow().setAttributes(lp);
            mInputTextMsgDialog.setCancelable(true);
            mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        mInputTextMsgDialog.show();
    }

    /**
     * 显示加载中弹窗
     * @param message
     */
    @Override
    public void showLoadingDialog(String message) {
        showProgressDialog(message,true);
    }

    /**
     * 关闭加载中弹窗
     */
    @Override
    public void hideLoadingDialog() {
        closeProgressDialog();
    }

    /**
     * 预览下一个主播
     */
    @Override
    public void onNextRoom() {
        if(null!=mVerticalPagerAdapter&&null!=bindingView){
            if(mPosition<(mVerticalPagerAdapter.getCount()-1)){
                mPosition++;
                bindingView.verticalViewPager.setCurrentItem(mPosition,true);
            }else{
                onFinish();
            }
        }
    }

    /**
     * 界面关闭
     */
    @Override
    public void onFinish() {
        onBackPressed();
    }

    /**
     * 返回房间被解散下的 按钮提示 文字
     * @return
     */
    @Override
    public String getButtonText() {
        if(null!=mVerticalPagerAdapter){
            if(mPosition<(mVerticalPagerAdapter.getCount()-1)){
                return "观看下一个";
            }
            return "退出";
        }
        return "观看下一个";
    }

    /**
     * 监听预览的位置，在松手后500毫秒之后开始预览
     */
    private VerticalViewPager.OnPageChangeListener onPageChangeListener=new VerticalViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(positionOffsetPixels>mScrollOffsetY){
                setConntrollerAlpha(position,(1.0f-positionOffset));
                setConntrollerAlpha(position+1,positionOffset);
            }else if(positionOffsetPixels<mScrollOffsetY){
                setConntrollerAlpha(position+1,positionOffset);
                setConntrollerAlpha(position,(1.0f-positionOffset));
            }
            mScrollOffsetY=positionOffsetPixels;
        }

        @Override
        public void onPageSelected(int position) {
            //回收上一个片段持有的播放器
            onLifeChange(mPosition,CHANGE_ODE_STOP);
            mPosition=position;
            waitPlayVideo(300,mPosition);//创建延时播放任务
            //加载更多
            if(null!=mVerticalPagerAdapter&&mPosition>=(mVerticalPagerAdapter.getCount()-1)){
                //立即加载分页数据
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    List<RoomList> data = LiveRoomManager.getInstance().getData();
                    mLastUserID = data.get(data.size()-1).getUserid();
                    mPresenter.getLiveLists(mLastUserID,mType,mPage);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 设置延缓任务
     * @param misTime 需要延缓多久
     * @param waitPoistion 延缓播放视频的目标Poistion
     */
    private void waitPlayVideo(long misTime,int waitPoistion) {
        getHandler().removeMessages(0);
        getHandler().postAtTime(new PlayVideoRunnable(waitPoistion), SystemClock.uptimeMillis()+misTime);//设置延缓任务
    }

    /**
     * 这个Runnable用来执行延缓任务，waitPlayPoistin是记录要执行的延缓任务，只有当前显示的viewPager cureenItem与当时提交的cureenItem相等才允许播放
     * 防止用户手速过快
     */
    private class PlayVideoRunnable implements Runnable{
        private final int waitPlayPoistin;

        public PlayVideoRunnable(int waitPoistion){
            this.waitPlayPoistin=waitPoistion;
        }

        @Override
        public void run() {
            if(this.waitPlayPoistin!=mPosition){
                return;
            }
            onLifeChange(mPosition,CHANGE_ODE_START);
        }
    }
    
    /**
     * 控制器的透明度渐变
     * @param position 要渐变直播间索引
     * @param alpha 渐变值
     */
    private void setConntrollerAlpha(int position, float alpha) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalRoomPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalRoomPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalRoomPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.setConntrollerAlpha(alpha);
                    }
                }
            }
        }
    }

    /**
     * 生命周期调度
     * @param position
     * @param CHANGE_MODE
     */
    private void onLifeChange(int position,int CHANGE_MODE){
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalRoomPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalRoomPager> next = iterator.next();
                if(position==next.getKey()){
                    VerticalRoomPager viewPager = next.getValue();
                    if(null!=viewPager){
                        if(CHANGE_ODE_START==CHANGE_MODE){
                            viewPager.onStart();
                            return;
                        }else if(CHANGE_ODE_RESUME==CHANGE_MODE){
                            viewPager.onResume();
                            return;
                        }else if(CHANGE_ODE_PAUSE==CHANGE_MODE){
                            viewPager.onPause();
                            return;
                        }else if(CHANGE_ODE_STOP==CHANGE_MODE){
                            viewPager.onStop();
                            return;
                        }else if(CHANGE_ODE_BACK==CHANGE_MODE){
                            viewPager.onBackPressed();
                            return;
                        }else if(CHANGE_ODE_DESTROY==CHANGE_MODE){
                            viewPager.onDestroy();
                            return;
                        }
                    }
                    return;
                }
            }
        }
    }

    /**
     * 垂直列表适配器
     */
    private class VerticalFragmentPagerAdapter extends VerticalPagerAdapter {

        @Override
        public int getCount() {
            return null==LiveRoomManager.getInstance().getData()?0:LiveRoomManager.getInstance().getData().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RoomList roomInfo = LiveRoomManager.getInstance().getData().get(position);
            if(null!=roomInfo){
                RoomExtra roomExtra=new RoomExtra();
                roomExtra.setUserid(roomInfo.getUserid());
                roomExtra.setNickname(roomInfo.getNickname());
                roomExtra.setAvatar(roomInfo.getAvatar());
                roomExtra.setFrontcover(roomInfo.getFrontcover());
                roomExtra.setPull_steram(TextUtils.isEmpty(roomInfo.getPush_stream())?roomInfo.getPush_stream_flv():roomInfo.getPush_stream());
                roomExtra.setRoom_id(roomInfo.getRoomid());
                roomExtra.setItemCategory(roomInfo.getItemCategory());
                roomExtra.setBanners(roomInfo.getBanners());
                VerticalRoomPager videpPlayerViewPager = new VerticalRoomPager(VerticalRoomPullAvtivity.this,VerticalRoomPullAvtivity.this,roomExtra,position);
                View view = videpPlayerViewPager.getView();
                view.setId(position);
                if(null!= mFragments) mFragments.put(position, videpPlayerViewPager);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(null!=container){
                container.removeView(container.findViewById(position));
                if(null!= mFragments) mFragments.remove(position);
            }
        }
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}


    @Override
    public void showLiveRooms(OneListBean data) {
        if(null!=mVerticalPagerAdapter&&null!=data.getList()){
            LiveRoomManager.getInstance().addData(data.getList());
            mVideoListSize=LiveRoomManager.getInstance().getData().size();
            mLastUserID=data.getList().get(data.getList().size()-1).getUserid();
            mVerticalPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLiveRoomEmpty() {}

    @Override
    public void showLiveRoomError(int code, String errorMsg) {
        if(mPage>0) mPage--;
    }

    /**
     * 观察者关注的消息
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {

    }

    /**
     * 发送自定义消息
     * @param msgInfo
     */
    protected void sendCustomMessage(final CustomMsgInfo msgInfo){
        if(null==msgInfo.getCmd()) return;
        if(null!=msgInfo){
            //本地自定义礼物消息体
            if(TextUtils.equals(Constant.MSG_CUSTOM_GIFT,msgInfo.getCmd().get(0))){
                newSystemCustomMessage(msgInfo,false);
                return;
            }
            String content=new Gson().toJson(msgInfo);
            LiveRoomManager.getInstance().getLiveRoom().sendRoomCustomMsg(msgInfo.getCmd().get(0), content, new IMMessageMgr.Callback() {
                @Override
                public void onError(int code, String errInfo) {
                    LogApi logInfo=new LogApi();
                    logInfo.setRequstUrl("发送群组自定义消息");
                    logInfo.setErrMessage(errInfo);
                    logInfo.setErrCode(code);
                    ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
                    actionLogInfo.setData(logInfo);
                    UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_GROUP_MESSAGE,actionLogInfo,null);
                    if(!VerticalRoomPullAvtivity.this.isFinishing()){
                        String errorMsg="";
                        if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_ROOM){
                            errorMsg="你已被群管理员禁止发言";
                        }else if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_APP){
                            errorMsg="你已被管理员禁止全平台发言";
                        }
                        if(!TextUtils.isEmpty(errorMsg)){
                            QuireDialog.getInstance(VerticalRoomPullAvtivity.this).setTitleText("发送失败").setContentText(errorMsg).setSubmitTitleText("确定").setCancelTitleText("关闭").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                                @Override
                                public void onConsent() {
                                }

                                @Override
                                public void onRefuse() {

                                }
                            }).show();
                        }
                    }
                }

                @Override
                public void onSuccess(Object... args) {
                    //渲染至本地消息弹幕列表
                    if(!VerticalRoomPullAvtivity.this.isFinishing()){
                        newSystemCustomMessage(msgInfo,false);
                    }
                }
            });
        }
    }

    /**
     * 发送纯文本消息
     * @param content 纯文本内容
     * @param isDanmu 是否是弹幕消息
     * 敏感过滤示例：自定义：$ 官方 |
     * 足球玩法$$足球玩法|习近平|习近平   MESSAGE:我们的足球玩法是习近平教的
     */
    protected void sendTextMessage(String content, boolean isDanmu){
        if(null==content) return;
        LiveRoomManager.getInstance().getLiveRoom().sendGroupTextMessage(content, isDanmu,new IMMessageMgr.Callback() {
            @Override
            public void onError(int code, String errInfo) {
                if(!VerticalRoomPullAvtivity.this.isFinishing()){
                    if(code==Constant.ROOM_CUSTOMMSG_CODE_DANGER){
                        CustomMsgInfo customMsgInfo=new CustomMsgInfo(0);
                        customMsgInfo.setChildCmd(Constant.MSG_CUSTOM_ERROR);
                        customMsgInfo.setMsgContent("因消息包含敏感信息未发送成功");
                        onNewTextMessage(customMsgInfo,false);
                        return;
                    }
                    String errorMsg=errInfo;
                    if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_ROOM){
                        errorMsg="你已被群管理员禁止本房间发言！";
                    }else if(code==Constant.ROOM_CUSTOMMSG_CODE_SPEECH_TO_APP){
                        errorMsg="你已被管理员禁止全平台发言！";
                    }else if(code==Constant.ROOM_CUSTOMMSG_CODE_DANGER){
                        errorMsg="发送的文本内容包含敏感词汇！";
                    }
                    LogApi logInfo=new LogApi();
                    logInfo.setRequstUrl("发送群组自定义消息");
                    logInfo.setErrMessage(errorMsg);
                    logInfo.setErrCode(code);
                    ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
                    actionLogInfo.setData(logInfo);
                    UserManager.getInstance().postActionState(NetContants.POST_ACTION_TYPE_GROUP_MESSAGE,actionLogInfo,null);
                    if(!TextUtils.isEmpty(errorMsg)){
                        QuireDialog.getInstance(VerticalRoomPullAvtivity.this).setTitleText("发送失败").setContentText(errorMsg).setSubmitTitleText("确定").setCancelTitleText("关闭").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent() {
                            }

                            @Override
                            public void onRefuse() {

                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onSuccess(Object... args) {
                //渲染至本地消息弹幕列表
                if(null!=args&&args.length>0){
                    Object arg = args[0];
                    if(arg instanceof TIMMessage&&!VerticalRoomPullAvtivity.this.isFinishing()){
                        TIMMessage timMessage= (TIMMessage) arg;
                        TIMElem element = timMessage.getElement(0);
                        if(element.getType()== TIMElemType.Text){
                            TIMTextElem timTextElem= (TIMTextElem) element;
                            onNewTextMessage(timTextElem.getText());
                        }
                    }
                }
            }
        });
    }

    /**
     * 收到新的消息交给控制器统一处理刷新
     * 播放礼物动画
     * @param customMsgInfo
     * @param isSystemPro   是否来系统推送的
     */
    protected void newSystemCustomMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if (null == customMsgInfo) return;
        onRoomNewSystemCustomMessage(customMsgInfo, isSystemPro);
    }



    /**
     * 纯文本消息
     */
    protected synchronized void onNewTextMessage(String content) {
        CustomMsgInfo customMsgInfo=new CustomMsgInfo(0);
        customMsgInfo.setChildCmd(Constant.MSG_CUSTOM_TEXT);
        customMsgInfo.setMsgContent(content);
        onNewTextMessage(customMsgInfo,false);
    }

    /**
     * 新的自定义消息
     * @param customMsgInfo
     * @param isSystemPro 是否来自远端
     */
    private void onNewTextMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro){
        onRoomNewTextMessage(customMsgInfo,isSystemPro);
    }

    /**
     * 输入框高度发生了变化
     * @param flag
     * @param keyBordHeight
     */
    private void onRoomShowInputKeyBord(boolean flag, int keyBordHeight) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalRoomPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalRoomPager> next = iterator.next();
                if(mPosition==next.getKey()){
                    VerticalRoomPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.onRoomShowInputKeyBord(flag,keyBordHeight);
                    }
                    return;
                }
            }
        }
    }

    /**
     * 直播间新的系统消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    private void onRoomNewSystemCustomMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalRoomPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalRoomPager> next = iterator.next();
                if(mPosition==next.getKey()){
                    VerticalRoomPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.onRoomNewSystemCustomMessage(customMsgInfo,isSystemPro);
                    }
                    return;
                }
            }
        }
    }

    /**
     * 房间新的纯文本消息
     * @param customMsgInfo
     * @param isSystemPro
     */
    private void onRoomNewTextMessage(CustomMsgInfo customMsgInfo, boolean isSystemPro) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalRoomPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalRoomPager> next = iterator.next();
                if(mPosition==next.getKey()){
                    VerticalRoomPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.onRoomNewTextMessage(customMsgInfo,isSystemPro);
                    }
                    return;
                }
            }
        }
    }

    /**
     * 房间精简消息,人数变化
     * @param groupId
     * @param sender
     * @param changedInfo
     */
    private void onRoomNewMinMessage(String groupId, String sender, NumberChangedInfo changedInfo) {
        if(null!= mFragments && mFragments.size()>0){
            Iterator<Map.Entry<Integer, VerticalRoomPager>> iterator = mFragments.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, VerticalRoomPager> next = iterator.next();
                if(mPosition==next.getKey()){
                    VerticalRoomPager viewPager = next.getValue();
                    if(null!=viewPager){
                        viewPager.onRoomNewMinMessage(groupId,sender,changedInfo);
                    }
                    return;
                }
            }
        }
    }

    //=======================================直播房间的事件消息=======================================

    /**
     * 群组纯文本消息
     * @param groupID
     * @param senderID 发送人信息
     * @param sendNickname
     * @param sendhead
     * @param sendLeve
     * @param sendGender
     * @param sendUserType
     * @param messageContent 消息内容
     */
    @Override
    public void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendLeve ,long sendGender,long sendUserType,String messageContent) {
        if(null!=messageContent){
            CustomMsgInfo customMsgInfo=new CustomMsgInfo();
            customMsgInfo.setChildCmd(Constant.MSG_CUSTOM_TEXT);
            customMsgInfo.setSendUserID(senderID);
            customMsgInfo.setSendUserName(sendNickname);
            customMsgInfo.setSendUserHead(sendhead);
            customMsgInfo.setSendUserGradle(0);
            customMsgInfo.setSendUserVIP((int) sendLeve);
            customMsgInfo.setSendUserGradle((int) sendGender);
            customMsgInfo.setSendUserType((int) sendUserType);
            customMsgInfo.setMsgContent(messageContent);
            onNewTextMessage(customMsgInfo,true);
        }
    }

    /**
     * 群组中，用户发送的自定义消息
     * @param roomID 房间ID
     * @param userID 发送者ID
     * @param userName 发送者昵称
     * @param userAvatar 发送者头像
     * @param cmd 自定义cmd
     * @param message 自定义消息内容
     */
    @Override
    public void onRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, String message) {
        try {
            CustomMsgInfo liveChatMsgInfo = new Gson().fromJson(message, CustomMsgInfo.class);
            liveChatMsgInfo.setAccapGroupID(roomID);
            newSystemCustomMessage(liveChatMsgInfo, true);
        } catch (RuntimeException e) {}
    }

    /**
     * 新的系统消息
     * @param sender
     * @param message
     * @param groupID
     */
    @Override
    public void onRoomSystemMsg(String groupID, String sender, String message) {
        try {
            CustomMsgInfo liveChatMsgInfo = new Gson().fromJson(message, CustomMsgInfo.class);
            liveChatMsgInfo.setAccapGroupID(groupID);
            newSystemCustomMessage(liveChatMsgInfo, true);
        } catch (RuntimeException e) {}
    }

    /**
     * 房间人数变化
     * @param groupId 裙子ID
     * @param sender 发送人
     * @param toJson 消息体
     */
    @Override
    public void onRoomNumberSystemMsg(String groupId, String sender, String toJson) {
        if(!TextUtils.isEmpty(toJson)){
            NumberChangedInfo changedInfo = new Gson().fromJson(toJson, NumberChangedInfo.class);
            onRoomNewMinMessage(groupId,sender,changedInfo);
        }
    }

    @Override
    public void onDebugLog(String log) {}
    @Override
    public void onRoomClosed(String roomID) {}
    @Override
    public void onError(final int errorCode, final String errorMessage) {}
    @Override
    public void onRoomPushMessage(PushMessage pushMessage) {}
    @Override
    public void onC2CCustomMessage(String sendID, String message) {}

    @Override
    protected void onResume() {
        super.onResume();
        onLifeChange(mPosition,CHANGE_ODE_RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LiveRoomManager.getInstance().setPosition(mPosition).setType(mType).setPage(mPage).setLastUserID(mLastUserID);
        onLifeChange(mPosition,CHANGE_ODE_PAUSE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        onLifeChange(mPosition,CHANGE_ODE_BACK);
        finish();
    }

    @Override
    protected void onDestroy() {
        onLifeChange(mPosition,CHANGE_ODE_DESTROY);
        if(null!=mPresenter) mPresenter.detachView();
        LiveRoomManager.getInstance().getLiveRoom().setLiveRoomListener(null);
        LiveRoomManager.getInstance().getLiveRoom().onDestroy();
        if(null!= mLayoutChangedListener) {
            mLayoutChangedListener.onDestroy();
            mLayoutChangedListener=null;
        }
        if(null!= mFragments) mFragments.clear(); mFragments =null;
        if(null!=mHandler) mHandler.removeMessages(0); mHandler=null;
        if(null!=bindingView) bindingView.verticalViewPager.removeAllViews();
        super.onDestroy();
        VideoCallManager.getInstance().setBebusying(false);
    }
}