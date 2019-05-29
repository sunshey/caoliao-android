package com.yc.liaolive.index.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import com.igexin.sdk.PushManager;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.kaikai.securityhttp.utils.LogUtil;
import com.music.player.lib.bean.MusicStatus;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.manager.MusicWindowManager;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUserStatusListener;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.ChatExtra;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.bean.NoticeInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityMainBinding;
import com.yc.liaolive.index.manager.IndexFragController;
import com.yc.liaolive.index.ui.fragment.IndexFragment;
import com.yc.liaolive.index.ui.fragment.IndexWebViewFragment;
import com.yc.liaolive.index.view.MainTabItem;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.constants.TCConstants;
import com.yc.liaolive.live.manager.OfflineManager;
import com.yc.liaolive.live.manager.SettingsManager;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.manager.ActivityCollectorManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.CLNotificationManager;
import com.yc.liaolive.manager.ForegroundManager;
import com.yc.liaolive.manager.LocationHelper;
import com.yc.liaolive.mine.ui.fragment.IndexMineFragment;
import com.yc.liaolive.msg.manager.ChatMessagePushManager;
import com.yc.liaolive.msg.model.GroupInfo;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.msg.ui.fragment.IndexMessageFragment;
import com.yc.liaolive.observer.MessageEvent;
import com.yc.liaolive.recharge.model.bean.VipRechargePoppupBean;
import com.yc.liaolive.recharge.ui.VipRewardDialogActivity;
import com.yc.liaolive.service.GeTuiIntentServer;
import com.yc.liaolive.service.GeTuiMessageServer;
import com.yc.liaolive.service.GiftResourceServer;
import com.yc.liaolive.service.VideoCallListenerService;
import com.yc.liaolive.start.manager.AppManager;
import com.yc.liaolive.start.manager.StartManager;
import com.yc.liaolive.start.manager.VersionCheckManager;
import com.yc.liaolive.start.model.bean.ConfigBean;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.start.ui.BuildManagerActivity;
import com.yc.liaolive.start.ui.SplashActivity;
import com.yc.liaolive.ui.activity.OfflineTipsActivity;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.ui.business.LoginBusiness;
import com.yc.liaolive.ui.contract.InitContract;
import com.yc.liaolive.ui.dialog.CommenNoticeDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.SettingActivity;
import com.yc.liaolive.user.ui.VipRewardActivity;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.StatusUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.listsner.OnCallStateListener;
import com.yc.liaolive.videocall.manager.AudioPlayerManager;
import com.yc.liaolive.videocall.manager.CallInWindowManager;
import com.yc.liaolive.videocall.manager.CallVibratorManager;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.ui.activity.CallWakeActivity;
import com.yc.liaolive.videocall.ui.activity.LiveCallActivity;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.webview.util.WebviewUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import rx.functions.Action1;

/**
 * TinyHung@outlook.com
 * 2017/5/20 10:20
 * 主页
 */

public class MainActivity extends TopBaseActivity implements InitContract.View, Observer, OnCallStateListener {

    private static final String TAG = "MainActivity";
    private List<Fragment> mFragments=null;
    private ActivityMainBinding bindingView;
    private long currentMillis=0;
    private NoticeInfo mData;
    private Handler mHandler;
    private DataChangeView dataChangeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        //填充布局
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if(TextUtils.isEmpty(UserManager.getInstance().getSDKAppID())
                ||TextUtils.isEmpty(UserManager.getInstance().getAccountType())
                ||TextUtils.isEmpty(UserManager.getInstance().getUserId())
                ||TextUtils.isEmpty(UserManager.getInstance().getUserSig())){
            startActivity(new Intent(MainActivity.this,SplashActivity.class));
            ActivityCollectorManager.finlishAllActivity();
            finish();
            return;
        }
        dataChangeView = findViewById(R.id.loading_view);
        StatusUtils.setStatusTextColor1(true,this);//白色背景，黑色字体
        //添加观察者刷新
        ApplicationManager.getInstance().addObserver(this);
        VideoApplication.mUuid= GoagalInfo.get().getUUID(AppEngine.getApplication().getApplicationContext());
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (mHandler == null) {
                    mHandler = new Handler();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initAPPConfigListener();
                    }
                });
            }
        });
        MusicPlayerManager.getInstance().bindService(MainActivity.this);
        //携带参数启动APP
        startIntent();
        //初始化视频通话
        VideoCallManager.getInstance().setCallInTimeOut(60000).addCallStateListener(this).setCallStatus(VideoCallManager.CallStatus.CALL_FREE);
        //启动领取话费弹窗
        VipRechargePoppupBean bean = UserManager.getInstance().getPopup_page();
        if (bean != null && bean.getList() != null && bean.getList().size() > 0) {
            VipRewardDialogActivity.startRewardDialog(1, bean);
        }
        //弹出升级提示
        VersionCheckManager.getInstance().setMainInit(true);
        //是否需要弹出升级提示弹框
        if (VersionCheckManager.getInstance().isShowUpdataView()) {
            UpdataApkInfo updataApkInfo = (UpdataApkInfo) ApplicationManager.getInstance().getCacheExample().getAsObject("updata_apk_info");
            if (updataApkInfo != null) {
                BuildManagerActivity.start(updataApkInfo);
            }
        }
        //自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiMessageServer.class);
        //消息透传
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentServer.class);
        //登录
        login();
        if(null!=AppEngine.getInstance().getListCoin()){
            VipRewardActivity.start();
        }
    }

    private void initAPPConfigListener () {
        StartManager.getInstance().getConfigInfo();
        dataChangeView.showLoadingView();
        dataChangeView.setVisibility(View.VISIBLE);
        StartManager.getInstance().getBehaviorProcessor()
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .filter(new Predicate<ConfigBean>() {
                    @Override
                    public boolean test(ConfigBean configBean) throws Exception {
                        ConfigBean bean = (ConfigBean) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.APP_CONFIG);
                        if (!StartManager.getInstance().isInitSuccess() && bean == null) {
                            //配置加载失败 显示错误页面，点击刷新重新获取
                            dataChangeView.showNoNet(new DataChangeView.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    dataChangeView.showLoadingView();
                                    dataChangeView.setVisibility(View.VISIBLE);
                                    StartManager.getInstance().getConfigInfo();
                                }
                            });
                            return false;
                        }
                        dataChangeView.setVisibility(View.GONE);
                        return true;
                    }
                })
                .subscribe(new Consumer<ConfigBean>() {
                    @Override
                    public void accept(ConfigBean configBean) throws Exception {
                        dataChangeView.stopLoading();
                        dataChangeView.setVisibility(View.GONE);
                        //配置加载完毕,显示界面
                        initFragment();
                        StartManager.getInstance().getBehaviorProcessor().onComplete();
                        StartManager.getInstance().setBehaviorProcessor(null);
                    }
                });
    }

    @Override
    public void requstPermissions() {
        super.requstPermissions();
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
        if(PREMISSION_SUCCESS==resultCode){
            if(null!=mFragments&&mFragments.size()>0){
                for (int i = 0; i < mFragments.size(); i++) {
                    Fragment fragment = mFragments.get(i);
                    if(fragment instanceof IndexMineFragment){
                        ((IndexMineFragment)fragment).startCameraPreview();
                    }
                }
            }
        }else{
            ToastUtils.showCenterToast("拍照权限被拒绝");
        }
    }

    /**
     * 处理携带意图的访问
     */
    private void startIntent() {
        if(null==getIntent()) return;
            //直播间入参
        if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_ROOM)){
            RoomExtra roomExtra = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_ROOM);
            LiveRoomPullActivity.start(MainActivity.this,roomExtra);
            //视频通话入参
        }else if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL)){
            CallExtraInfo callExtraInfo = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL);
            LiveCallActivity.acceptCall(MainActivity.this,callExtraInfo);
            //虚拟视频通话推送入参
        }else if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE)){
            CustomMsgCall customMsgCall = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE);
            CallWakeActivity.start(getApplicationContext(),customMsgCall);
            //私信入参
        }else if(null!=getIntent().getSerializableExtra(Constant.APP_START_EXTRA_CHAT)){
            ChatExtra chatExtra = (ChatExtra) getIntent().getSerializableExtra(Constant.APP_START_EXTRA_CHAT);
            ChatActivity.navToChat(MainActivity.this,chatExtra.getIdentify(),chatExtra.getType());
            //视频通话，无悬浮窗权限
        }else if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_DOALOG_CHAT)){
            if(!VideoCallManager.getInstance().isBebusying()){
                CallExtraInfo callExtraInfo = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_DOALOG_CHAT);
                LiveCallActivity.acceptCall(MainActivity.this,callExtraInfo);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(null!=intent){
            if(null!=getIntent().getParcelableExtra(Constant.APP_START_EXTRA_DOALOG_CHAT)){
                CallExtraInfo callExtraInfo = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_DOALOG_CHAT);
                if(null!=callExtraInfo){
                    onNewCall(callExtraInfo);
                }
            }
        }
    }

    /**
     * 初始化
     */
    private void initMain() {
        //百度定位SDK
        if(checkLocationPermission()){
            LocationHelper.getInstance().start(getApplicationContext(),Build.VERSION.SDK_INT);
        }
        //资源下载管理
        try {
            startService(new Intent(MainActivity.this, GiftResourceServer.class));
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化控件
     */
    private void initFragment() {
        if(null==mFragments) mFragments = new ArrayList<>();
        mFragments.clear();
        mFragments = IndexFragController.getInstance().getBottomFragment();
        if (mFragments .size() == 0) {
            mFragments.add(new IndexFragment());
            mFragments.add(new IndexFragment());
            mFragments.add(new IndexMessageFragment());
            mFragments.add(new IndexMineFragment());
        }
        bindingView.vpView.setOffscreenPageLimit(mFragments .size());
        bindingView.vpView.setAdapter(new AppFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
        bindingView.llBottomMenu.initViews();
        bindingView.llBottomMenu.setDoubleRefresh(true);//启用双击刷新
        bindingView.llBottomMenu.setOnTabChangeListene(new MainTabItem.OnTabChangeListene() {
            //界面切换
            @Override
            public void onChangeed(int index) {
                bindingView.vpView.setCurrentItem(index,false);
            }
            //刷新
            @Override
            public void onRefresh(int index) {
                updataChildView(index);
            }
            //1v1聊
            @Override
            public void onTabVideo(int mainIndex) {
                changedIndex(mainIndex,"6");
            }
        });
        StatusUtils.setStatusTextColor1(true,this);//默认是黑色字体白色背景
        String pisition=IndexFragController.getInstance().getMainIndex();
        if(TextUtils.isEmpty(pisition)){
            pisition="0";
        }
        try {
            bindingView.llBottomMenu.setCurrentIndex(Integer.parseInt(pisition));
            bindingView.llBottomMenu.setIndex(Integer.parseInt(pisition));
        }catch (RuntimeException e){

        }
    }

    /**
     * 切换至首页1v1模块
     * @param groupIndex 需要切换到的底部tab index
     * @param targetChildIndex 需要切换到的首页group中的子targetId
     */
    private void changedIndex(int groupIndex, final String targetChildIndex) {
        bindingView.vpView.setCurrentItem(groupIndex,false);
        bindingView.llBottomMenu.setIndex(groupIndex);
        if(null!=mFragments&&mFragments.size()>0){
            for (int i = 0; i < mFragments.size(); i++) {
                Fragment fragment = mFragments.get(groupIndex);
                if(fragment instanceof IndexFragment){
                    ((IndexFragment) fragment).changeToTarget(targetChildIndex);
                    break;
                }
            }
        }
    }

    /**
     * IM账号登入
     */
    private void login() {
        //互踢下线逻辑
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                offlineSetting();
            }

            @Override
            public void onUserSigExpired() {
            }
        });
        //登录至IM
        loginIM();
    }

    /**
     * 登录至IM
     */
    private void loginIM() {
        //登录到IM
        TIMManager.getInstance().init(getApplicationContext(),Integer.parseInt(UserManager.getInstance().getSDKAppID()), UserManager.getInstance().getAccountType());
        LoginBusiness.getInstance().loginIm(UserManager.getInstance().getUserId(), UserManager.getInstance().getUserSig(), new TIMCallBack() {
            @Override
            public void onError(int errCode, String errMsg) {
                Logger.d(TAG,"登录失败,errCode+"+errCode+",errMsg+"+errMsg);
                switch (errCode) {
                    case 6208:
                        //离线状态下被其他终端踢下线
                        offlineSetting();
                        break;
                    case 6200:
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loginIM();
                            }
                        },1000);
                        break;
                    default:
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loginIM();
                            }
                        },1000);
                        break;
                }
            }

            @Override
            public void onSuccess() {
                Logger.d(TAG,"登录至IM成功");
                MessageEvent.getInstance();
                ChatMessagePushManager.getInstance().onInitPush();
                if(null!=mFragments&&mFragments.size()>2){
                    try {
                        ((IndexMessageFragment) mFragments.get(2)).getConversationMessages();
                        UserManager.getInstance().syncUserInfoToIM();
                    }catch (RuntimeException e){

                    }catch (Exception e){

                    }finally {
                        //其他业务初始化
                        if(null==mHandler) mHandler = new Handler(Looper.myLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initMain();
                            }
                        },3000);
                    }
                }
            }
        });
    }

    /**
     * 账号登出
     */
    public void logout(TIMCallBack callBack){
        LoginBusiness.getInstance().logout(callBack);
    }

    /**
     * 账号互踢、下线
     */
    private void offlineSetting() {
        OfflineManager.getInstance().startOfflineSetting().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                OfflineManager.getInstance().onDestroy();
                //强制重新登录
                if(integer== OfflineTipsActivity.ACTION_RESET_LOGIN){
                    showProgressDialog("登录中...",true);
                    LoginBusiness.getInstance().loginIm(UserManager.getInstance().getUserId(),UserManager.getInstance().getUserSig(),new TIMCallBack() {
                        @Override
                        public void onError(int errCode, String errMsg) {
                            Logger.d(TAG,"重试登录IM失败，errCode："+errCode+",errMsg"+errMsg);
                            ToastUtils.showCenterToast(errMsg);
                            ActivityCollectorManager.finlishAllActivity();
                            closeProgressDialog();
                            loginOut();
                        }

                        @Override
                        public void onSuccess() {
                            ToastUtils.showCenterToast("登录成功");
                            //互踢下线逻辑
                            TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
                                @Override
                                public void onForceOffline() {
                                    offlineSetting();
                                }

                                @Override
                                public void onUserSigExpired() {
                                }
                            });

                            closeProgressDialog();
                            MessageEvent.getInstance();
                            ChatMessagePushManager.getInstance().onInitPush();
                            if(null!=mFragments&&mFragments.size()>2){
                                try {
                                    ((IndexMessageFragment) mFragments.get(2)).getConversationMessages();
                                }catch (RuntimeException e){

                                }catch (Exception e){

                                }
                            }
                        }
                    });
                    //账号切换
                }else if(integer== OfflineTipsActivity.ACTION_CLEAN_LOGIN){
                    loginOut();
                }
            }
        });
    }


    /**
     * 前往设置中心
     */
    public void startSetting() {
        SettingsManager.getInstance().startSetting().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                SettingsManager.getInstance().onDestroy();
                if(integer>0&&integer== SettingActivity.LOGOUT_SUCCESS){
                    showProgressDialog("登出中...",true);
                    loginOut();
                }
            }
        });
    }

    /**
     * 账号登出
     */
    private void loginOut() {
        if(!MainActivity.this.isFinishing()){
            logout(new TIMCallBack() {
                @Override
                public void onError(int errCode, String errMsg) {
                    closeProgressDialog();
                    exit(true);
                }

                @Override
                public void onSuccess() {
                    closeProgressDialog();
                    exit(true);
                }
            });
        }
    }

    /**
     * 是否重启
     * @param isRestart
     */
    private void exit(boolean isRestart) {
        UserManager.getInstance().cleanUserInfo();//先调用此方法清空用户中心数据
        MessageEvent.getInstance().clear();
        GroupInfo.getInstance().clear();
        if(isRestart){
            ActivityCollectorManager.finlishAllActivity();
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
            return;
        }else{
            finish();
        }
    }

    /**
     * 显示、隐藏TAB
     * @param flag
     */
    public void showMainTabLayout(boolean flag) {
        // TODO: 2018/8/22 新版需开启
        if(null!=bindingView) bindingView.llBottomMenu.showMainTabLayout(flag);
    }

    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(final int newMessageCount){
        if(null!=bindingView){
            bindingView.llBottomMenu.post(new Runnable() {
                @Override
                public void run() {
                    bindingView.llBottomMenu.setMessageContent(newMessageCount);
                }
            });
        }
    }

    /**
     * 检查权限
     * @return
     */
    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, TCConstants.LOCATION_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    //请求权限结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //位置获取失败
        if(requestCode==TCConstants.LOCATION_PERMISSION_REQ_CODE){
            LogUtil.msg("位置权限已获取");
            if(null!=grantResults&&grantResults.length>0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationHelper.getInstance().start(getApplicationContext(),Build.VERSION.SDK_INT);
                }
            }
        }
    }

    /**
     * 拦截的刷新事件
     * @param poistion
     */
    private void updataChildView(int poistion) {
        if(null!=mFragments&&mFragments.size()>poistion){
            Fragment fragment = mFragments.get(poistion);
            if(null!=fragment){
                if(fragment instanceof IndexFragment){
                    ((IndexFragment) fragment).fromMainUpdata();
                }else if(fragment instanceof IndexMessageFragment){
                    ((IndexMessageFragment) fragment).fromMainUpdata();
                }else if(fragment instanceof IndexMineFragment){
                    ((IndexMineFragment) fragment).fromMainUpdata();
                }else if(fragment instanceof IndexWebViewFragment){
                    ((IndexWebViewFragment) fragment).fromMainUpdata();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if(null!=bindingView) {
            bindingView.llBottomMenu.onResume();
        }
        if(null!=bindingView&&0==bindingView.vpView.getCurrentItem()){
            StatusUtils.setStatusTextColor1(true,this);//默认是黑色字体白色背景
        }
        CallVibratorManager.getInstance().onStop();
        AudioPlayerManager.getInstance().stopPlayer();
        //检查本地日志，如果有
        LogRecordUtils.getInstance().upload();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(null!=bindingView) bindingView.llBottomMenu.onPause();
    }

    @Override
    public void showErrorView() {}
    @Override
    public void complete() {}

    /**
     * 拦截返回和菜单事件
     * @param keyCode
     * @param event
     * @return
     */
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
        //如果在首页的其他界面，应当是切换至首页而不是立即退出
        if(null!=bindingView&&0!=bindingView.vpView.getCurrentItem()){
            bindingView.llBottomMenu.setCurrentIndex(0);
            StatusUtils.setStatusTextColor1(true,this);//默认是黑色字体白色背景
            return;
        }
        long millis = System.currentTimeMillis();
        if(0 == currentMillis | millis-currentMillis > 2000){
            ToastUtils.showToast("再按一次离开");
            currentMillis=millis;
            return;
        }
        currentMillis=millis;
        super.onBackPressed();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg){
            if(arg instanceof NoticeInfo){
                mData = (NoticeInfo) arg;
                //账号被封禁了
                if(TextUtils.equals(Constant.NOTICE_CMD_ACCOUNT_CLOSE, mData.getCmd())){
                    logout(null);//账号先强制登出
                    new Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=mData){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ActivityCollectorManager.finlishAllActivity();//结束所有Activity活动
                                        CommenNoticeDialog commenNoticeDialog = CommenNoticeDialog.getInstance(MainActivity.this).setTipsData("账号已被封禁", mData.getContent(), "确定");
                                        commenNoticeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                exit(true);
                                            }
                                        });
                                        commenNoticeDialog.show();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        exit(true);
                                    }
                                });
                            }
                        }
                    }, SystemClock.uptimeMillis()+500);
                }
            } else if (arg instanceof MusicStatus){
                MusicStatus musicStatus= (MusicStatus) arg;
                if(MusicStatus.PLAYER_STATUS_DESTROY == musicStatus.getPlayerStatus() ||
                        MusicStatus.PLAYER_STATUS_STOP == musicStatus.getPlayerStatus() ||
                        MusicStatus.PLAYER_STATUS_PAUSE == musicStatus.getPlayerStatus()){
                    VideoCallManager.getInstance().setBebusying(false);
                } else if (MusicStatus.PLAYER_STATUS_START == musicStatus.getPlayerStatus() ||
                        MusicStatus.PLAYER_STATUS_PREPARED  == musicStatus.getPlayerStatus()) {
                    VideoCallManager.getInstance().setBebusying(true);
                }
            }
        }
    }

    //===========================================视频通话相关回调=====================================
    /**
     * 新的视频通话邀请
     * @param callExtraInfo
     */
    @Override
    public void onNewCall(CallExtraInfo callExtraInfo) {
//        if(null==callExtraInfo) return;
//        LogRecordUtils.getInstance().postCallLogs("新的视频电话",callExtraInfo.getRoomID(),0,System.currentTimeMillis(),0,VideoCallManager.getInstance().isBebusying()?"主播忙碌中":"主播空闲中");
//        final Activity runActivity = ForegroundManager.getInstance().getRunActivity();
//        //结束可能正在显示的结算页
//        if(null!=runActivity&&runActivity instanceof TopBaseActivity){
//            Logger.d(TAG,"onNewCall--结束结算页");
//            ((TopBaseActivity) runActivity).resetCallState();
//        }
//        Logger.d(TAG,"--onNewCall--isBebusy:"+VideoCallManager.getInstance().isBebusying()+",Params:"+callExtraInfo.toString());
//        //正在忙碌
//        if(VideoCallManager.getInstance().isBebusying()) {
//            Logger.d(TAG,"onNewCall--正在忙碌");
//            return;
//        }
//        //屏幕已上锁
//        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(android.content.Context.KEYGUARD_SERVICE);
//        if(km.inKeyguardRestrictedInputMode()){
//            Logger.d(TAG,"onNewCall--屏幕已上锁");
//            CLNotificationManager.getInstance().sendCallNotification(getApplicationContext(),callExtraInfo,CLNotificationManager.NOTIFICATION_ID_CALL);
//            return;
//        }
//        Logger.d(TAG,"onNewCall--新来电");
//        if(null!=runActivity&&!runActivity.isFinishing()){
//            //接听来电
//            LiveCallActivity.acceptCall(runActivity,callExtraInfo);
//        }else{
//            //接听来电
//            LiveCallActivity.acceptCall(MainActivity.this,callExtraInfo);
//        }
    }

    /**
     * 新的视频通话推送
     * @param customMsgInfo
     */
    @Override
    public void onNewWakeCall(CustomMsgCall customMsgInfo) {
        if (null != customMsgInfo) {
            resetCallState();
            //用户正在忙碌
            if (VideoCallManager.getInstance().isBebusying()) {
                return;
            }
            //消息是否过期
            if(!TextUtils.isEmpty(customMsgInfo.getExprie_time())&& Utils.isExprie(Long.parseLong(customMsgInfo.getExprie_time()))){
                Logger.d(TAG,"推送消息已过期");
                return;
            }
            final Activity runActivity = ForegroundManager.getInstance().getRunActivity();
            if (null != runActivity && !runActivity.isFinishing()) {
                CallWakeActivity.start(MainActivity.this, customMsgInfo);
            } else if (null != customMsgInfo && !MainActivity.this.isFinishing()) {
                CLNotificationManager.getInstance().sendCallNotification(AppEngine.getApplication().getApplicationContext(), customMsgInfo);
            }
        }
    }

    /**
     * 视频来电 接听方取消、超时未接 或其他错误
     * @param callID 通话唯一标识 发起人ID_接听人ID
     * @param errorCode 错误码 参见LiveConstant 定义常量
     * @param errorMsg
     */
    @Override
    public void onCallError(String callID,int errorCode, String errorMsg) {
        ToastUtils.showCenterToast(errorMsg);
        VideoCallManager.getInstance().setCallStatus(VideoCallManager.CallStatus.CALL_FREE).onCancelTimeOutAction();
        CLNotificationManager.getInstance().cancelNotification(CLNotificationManager.NOTIFICATION_ID_CALL);//取消当前视频通话邀请
        CallInWindowManager.getInstance().onDestroy();
        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_CALL_EXCEPTION);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mHandler) {
            mHandler.removeMessages(0);
            mHandler=null;
        }
        if(null!=bindingView) bindingView.llBottomMenu.onDestroy();
        CLNotificationManager.getInstance().cancelAllNotification();
        ChatMessagePushManager.getInstance().onDestroy();

        MusicPlayerManager.getInstance().removeObservers();
        MusicPlayerManager.getInstance().removeAllPlayerListener();
        MusicWindowManager.getInstance().onDestroy();
        MusicPlayerManager.getInstance().unBindService(MainActivity.this);
        MusicPlayerManager.getInstance().onDestroy();

        WebviewUtil.getInstance().clearCache();
        logout(null);//账号登出
        ApplicationManager.getInstance().removeObserver(this);
        LocationHelper.getInstance().stop();
        stopService(new Intent(MainActivity.this, GiftResourceServer.class));
        stopService(new Intent(MainActivity.this, VideoCallListenerService.class));
        if(null!=bindingView) bindingView.llBottomMenu.onDestroy();
        SharedPreferencesUtil.getInstance().putBoolean(Constant.KEY_MAIN_INSTANCE,false);
        ApplicationManager.getInstance().onDestory();
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_FIRST_START_GRADE, true);//已经启动过App了
        mData=null;
        AppManager.getInstance().setUrlBlackList(null);
        VideoCallManager.getInstance().onDestroy();
        System.exit(0);
    }
}