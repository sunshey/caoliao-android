package com.yc.liaolive;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.view.View;
import com.antfortune.freeline.FreelineCore;
import com.kaikai.securityhttp.domain.GoagalInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicWindowClickListener;
import com.music.player.lib.manager.MusicWindowManager;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.FragmentMenu;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.bean.TagInfo;
import com.yc.liaolive.bean.UnReadMsg;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.manager.AppBackgroundManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ForegroundManager;
import com.yc.liaolive.music.activity.MusicPlayerActivity;
import com.yc.liaolive.start.manager.AppManager;
import com.yc.liaolive.util.DataFactory;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.videocall.bean.CallCloseExtra;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * TinyHung@outlook.com
 * 2017/5/20 10:53
 */

public class VideoApplication extends MultiDexApplication {

    private static final String TAG = "VideoApplication";
    private static VideoApplication mInstance;
    private static Context cContext;
    public static String mUuid;
    private UploadObjectInfo mUploadObjectInfo;
    public static boolean TEST = true;//是否是测试包
    private boolean indexRefresh;//首页是否需要刷新
    private boolean indexMediaRefresh;//主页多媒体是否需要刷新
    private boolean mineRefresh;//个人中心是否需要刷新
    private boolean getPhoneTask;//是否领取了绑定手机号的任务
    private int taskCoin = 0;//任务奖励，配合 getPhoneTask
    private boolean isNetwork;//移动网络环境下使用是否提示过
    private boolean isVideoNetwork;//视频播放移动网络环境下使用是否提示过
    public boolean downloadAPK;//是否正在下载APK
    private int msgCount;
    private HashMap<String,UnReadMsg> mUnReadMsgMap;//未读的消息集合
    private List<ImageInfo> mImages;
    private List<TagInfo> mTags;
    private long giveNum;//充值成功赠送的钻石
    private int mFollowState=-1;//关注状态,-1 未发生改变 1：已关注 0：已取关
    private List<FansInfo> mMediaTops;
    private boolean vipSuccess;//会员充值是否完成
    private String olderID;//订单ID
    private CallCloseExtra mCallCloseExtra;//视频通话结算信息
    private List<FragmentMenu> mIndexFragmentMenus;//主页显示模块
    private boolean zmAuthentResult=false;//芝麻认证结果
    //登录界面的ICON是否显示
    private boolean loginIcon=false;
    //APP签名
    private String mAppSignToMd5=null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static VideoApplication getInstance() {
        return mInstance;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        //通知消息数量发生了变化
        this.msgCount = msgCount;
        try {
            ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_LIVE_MESSAGE_CHANGED);
            ShortcutBadger.applyCount(getApplicationContext(), msgCount); //for 1.1.4+
        }catch (RuntimeException e){
        }
    }

    public HashMap<String, UnReadMsg> getUnReadMsgMap() {
        if(null==mUnReadMsgMap) mUnReadMsgMap=new HashMap<>();
        return mUnReadMsgMap;
    }

    public void setUnReadMsgMap(HashMap<String, UnReadMsg> unReadMsgMap) {
        mUnReadMsgMap = unReadMsgMap;
    }

    //使用多进程需要注意
    @Override
    public void onCreate() {
        super.onCreate();

        AppEngine.setApplication(this);
        mInstance = VideoApplication.this;
        cContext = this;
        TEST = !BuildConfig.BUILD_TYPE.equals("release");
        //初始化配置，请确保最先处理
        initCons();

        if (TEST) {
            FreelineCore.init(this);
        }
        GoagalInfo.get().init(getApplicationContext());
        mUuid = GoagalInfo.get().uuid;
        if (TextUtils.isEmpty(VideoApplication.mUuid)) {
            mUuid = SystemUtils.getLocalIpAddress();
        }
        if (MsfSdkUtils.isMainProcess(getApplicationContext())) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.drawable.ic_launcher);
                    }
                }
            });
        }
        AppManager.getInstance().onCreate();
        //未读消息容器
        mUnReadMsgMap=new HashMap<>();
        // APP前台监测 注册活动监听
        ForegroundManager.getInstance().init(this);
        //APP前后台监测,悬浮窗的处理
        AppBackgroundManager.getInstance().setAppStateListener(new AppBackgroundManager.IAppStateChangeListener() {
            @Override
            public void onAppStateChanged(boolean isAppForceground) {
                if(isAppForceground){
                    MusicWindowManager.getInstance().onVisible();
                }else{
                    MusicWindowManager.getInstance().onInvisible();
                }
            }
        });
        //全局迷你悬浮窗单击事件
        MusicWindowManager.getInstance().setOnMusicWindowClickListener(new MusicWindowClickListener() {

            @Override
            public void onWindownClick(View view, long musicID) {
                if(musicID>0){
                    Intent intent=new Intent(getApplicationContext(), MusicPlayerActivity.class);
                    intent.putExtra(MusicConstants.KEY_MUSIC_ID, musicID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            }

            @Override
            public void onWindownCancel(View view) {}
        });
    }

    /**
     * 初始化配置
     */
    private void initCons () {
        TEST = !BuildConfig.BUILD_TYPE.equals("release");
    }

    public static Context getContext() {
        return cContext;
    }

    public void setFollowState(int followState) {
        this.mFollowState=followState;
    }

    public int getFollowState() {
        return mFollowState;
    }

    public long getGiveNum() {
        return giveNum;
    }

    public void setGiveNum(long giveNum) {
        this.giveNum = giveNum;
    }

    /**
     * 是否正在下载APK
     * @return
     */
    public boolean isDownloadAPK() {
        return downloadAPK;
    }

    public void setDownloadAPK(boolean downloadAPK) {
        this.downloadAPK = downloadAPK;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    public void setNetwork(boolean network) {
        isNetwork = network;
    }

    public boolean isVideoNetwork() {
        return isVideoNetwork;
    }

    public void setVideoNetwork(boolean videoNetwork) {
        isVideoNetwork = videoNetwork;
    }

    /**
     * 首页是否已需要刷新
     * @return
     */
    public boolean isIndexRefresh() {
        return indexRefresh;
    }

    public void setIndexRefresh(boolean indexRefresh) {
        this.indexRefresh = indexRefresh;
    }

    /**
     * 任务获得的奖励
     * @return
     */
    public int getTaskCoin() {
        return taskCoin;
    }

    public void setTaskCoin(int taskCoin) {
        this.taskCoin = taskCoin;
    }

    /**
     * 是否完成了绑定手机号的任务
     * @return
     */
    public boolean isGetPhoneTask() {
        return getPhoneTask;
    }

    public void setGetPhoneTask(boolean getPhoneTask) {
        this.getPhoneTask = getPhoneTask;
    }

    /**
     * 充值是否成功
     * @param flag
     */
    public void setVipSuccess(boolean flag) {
        this.vipSuccess=flag;
    }

    public boolean isVipSuccess() {
        return vipSuccess;
    }

    /**
     * 用户中心是否需要刷新
     * @return
     */
    public boolean isMineRefresh() {
        return mineRefresh;
    }

    public void setMineRefresh(boolean mineRefresh) {
        this.mineRefresh = mineRefresh;
    }

    public boolean isIndexMediaRefresh() {
        return indexMediaRefresh;
    }

    public void setIndexMediaRefresh(boolean indexMediaRefresh) {
        this.indexMediaRefresh = indexMediaRefresh;
    }

    public boolean isLoginIcon() {
        return loginIcon;
    }

    public void setLoginIcon(boolean loginIcon) {
        this.loginIcon = loginIcon;
    }

    public void addMediaTops(List<FansInfo> data) {
        if(null!=mMediaTops) mMediaTops.clear();
        mMediaTops=new ArrayList<>();
        mMediaTops.addAll(data);
    }

    public void cleanMediaTops() {
        if(null!=mMediaTops) mMediaTops.clear();
    }

    public List<FansInfo> getMediaTops() {
        return mMediaTops;
    }

    /**
     * 是否有编辑完成待上传的视频
     * @return
     */

    public UploadObjectInfo getUploadObjectInfo() {
        return mUploadObjectInfo;
    }

    public void setUploadObjectInfo(UploadObjectInfo uploadObjectInfo) {
        mUploadObjectInfo = uploadObjectInfo;
    }

    public List<ImageInfo> getImages() {
        return mImages;
    }

    public void setImages(List<ImageInfo> images) {
        if(null==mImages) mImages=new ArrayList<>();
        mImages.clear();
        if(null==images) return;
        mImages.addAll(images);
    }

    public void setTags(List<TagInfo> tags) {
        if(null!=mTags) mTags.clear();
        this.mTags=tags;
    }

    public List<TagInfo> getTags() {
        return mTags;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ForegroundManager.getInstance().onDestroy(this);
    }

    public CallCloseExtra getCallCloseExtra() {
        return mCallCloseExtra;
    }

    public void setCallCloseExtra(CallCloseExtra callCloseExtra) {
        mCallCloseExtra = callCloseExtra;
    }

    public List<FragmentMenu> getIndexFragmentMenus() {
        if(null!=mIndexFragmentMenus){
            return mIndexFragmentMenus;
        }
        return DataFactory.createIndexFragments();
    }

    public void setIndexFragmentMenus(List<FragmentMenu> indexFragmentMenus) {
        mIndexFragmentMenus = indexFragmentMenus;
    }

    public boolean isZmAuthentResult() {
        return zmAuthentResult;
    }

    public void setZmAuthentResult(boolean zmAuthentResult) {
        this.zmAuthentResult = zmAuthentResult;
    }

    public void setAppSignToMd5(String appSignToMd5) {
        mAppSignToMd5 = appSignToMd5;
    }

    /**
     * APP签名
     * @return
     */
    public String getAppSignToMd5() {
        return mAppSignToMd5;
    }
}