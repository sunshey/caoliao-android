package com.yc.liaolive.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.qiniu.droid.rtc.QNBeautySetting;
import com.qiniu.droid.rtc.QNCameraSwitchResultCallback;
import com.qiniu.droid.rtc.QNErrorCode;
import com.qiniu.droid.rtc.QNRTCEngine;
import com.qiniu.droid.rtc.QNRTCEngineEventListener;
import com.qiniu.droid.rtc.QNRTCSetting;
import com.qiniu.droid.rtc.QNRoomState;
import com.qiniu.droid.rtc.QNSourceType;
import com.qiniu.droid.rtc.QNStatisticsReport;
import com.qiniu.droid.rtc.QNTrackInfo;
import com.qiniu.droid.rtc.QNTrackKind;
import com.qiniu.droid.rtc.QNVideoFormat;
import com.qiniu.droid.rtc.model.QNAudioDevice;
import com.qiniu.droid.rtc.model.QNMergeTrackOption;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseLiveActivity;
import com.yc.liaolive.bean.CreateRoomInfo;
import com.yc.liaolive.bean.NoticeContent;
import com.yc.liaolive.bean.NoticeInfo;
import com.yc.liaolive.bean.UserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.room.BaseRoom;
import com.yc.liaolive.live.room.LiveRoom;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.view.VideoLiveControllerView;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.msg.ui.activity.ChatConversationActivity;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.view.VideoCallPerviewWindown;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/5/15
 * 直播间-推流
 */

public class LiveRoomPusherActivity extends BaseLiveActivity<VideoLiveControllerView> implements  Observer, QNRTCEngineEventListener {

    private CreateRoomInfo mRoomInfoExtra;
    private boolean mBeautyEnable;
    private boolean mTurnLight;//闪光灯状态
    private VideoCallPerviewWindown mLocatSurfaceView;
    private QNRTCEngine mQnrtcEngine;
    private QNTrackInfo mLocalVideoTrack;
    private QNTrackInfo mLocalAudioTrack;
    private List<QNMergeTrackOption> mMergeTrackOptions;
    private boolean isDestroying;//是否正在销毁中
    private List<QNTrackInfo> mLocationTrackInfoList;
    private boolean isSuccess=false;
//    private BeautyControlView mBeautyControlView;

    public static void statrPublish(android.content.Context context,CreateRoomInfo roomInfo){
        Intent intent=new Intent(context,LiveRoomPusherActivity.class);
        intent.putExtra("roomInfoExtra",roomInfo);
        context.startActivity(intent);
    }

    /**
     * 告诉父类继承人身份
     * @return
     */
    @Override
    protected int getExtendsClassIdentify() {
        return BaseRoom.USER_IDENTITY_PUSH;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_pusher);
        mRoomInfoExtra = getIntent().getParcelableExtra("roomInfoExtra");
        if(null==mRoomInfoExtra|| null==mRoomInfoExtra.getRoom_token()){
            ToastUtils.showCenterToast("创建直播间失败，房间信息错误");
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(mRoomInfoExtra.getRoom_id());//绑定当前Room群组ID
        LiveRoomManager.getInstance().getLiveRoom().setUserIdentity(LiveRoom.USER_IDENTITY_PUSH);//绑定Room为推流身份
        LiveRoomManager.getInstance().getLiveRoom().onCreate();
        LiveRoomManager.getInstance().getLiveRoom().setLiveRoomListener(this);//直播间消息监听
        LiveRoomManager.getInstance().getLiveRoom().setPublishMediaType(BaseRoom.PUBLISH_MODE_VIDEO);
        ApplicationManager.getInstance().addObserver(this);
        requstPermissions();
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
        if(PREMISSION_SUCCESS==resultCode){
            initRoomViews();
//            initBeauty();
            initRtcEngine();
            showProgressDialog("直播准备中...",true);
            mQnrtcEngine.joinRoom(mRoomInfoExtra.getRoom_token().getTokens());
        }else{
            ToastUtils.showCenterToast("拍照或录音权限被拒绝");
            exitPublishState("拍照或录音权限被拒绝");
        }
    }

    @Override
    protected synchronized void initRoomViews() {
        mController = new VideoLiveControllerView(LiveRoomPusherActivity.this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mController.setLayoutParams(layoutParams);
        super.initRoomViews();
        if(null==mRoomInfoExtra) return;
        //设置为音视频同时推流
        UserInfo userInfo=new UserInfo();
        userInfo.setAvatar(UserManager.getInstance().getAvatar());
        userInfo.setUserid(UserManager.getInstance().getUserId());
        userInfo.setNickname(UserManager.getInstance().getNickname());
        userInfo.setFrontcover(TextUtils.isEmpty(UserManager.getInstance().getFrontCover())?UserManager.getInstance().getAvatar():UserManager.getInstance().getFrontCover());
        //设置主播信息
        mController.setIdentityType(VideoLiveControllerView.LIVE_SCENE_PUSH);//设置此房间为视频直播推流模式
        mController.setAnchorUserData(userInfo);
        mController.setHeadData(String.format(Locale.US, "%s", "00:00:00"),"0",UserManager.getInstance().getAvatar());
        mController.setOnViewClickListener(new VideoLiveControllerView.OnViewClickListener() {
            @Override
            public void onClickMenu(int position) {
                switch (position) {
                    case 0:
                        showInputMsgDialog();
                        break;
                    case 1:
                        ChatConversationActivity.start(AppEngine.getApplication().getApplicationContext());
                        break;
                    //美颜
                    case 4:
                        onBeautyEnable(4);
//                        if (mBeautyControlView.isShown()) {
//                            mBeautyControlView.hideBottomLayoutAnimator();
//                        } else {
//                            mBeautyControlView.showBottomLayoutAnimator();
//                        }
                        break;
                    //闪光灯
                    case 7:
                        if(null!=mQnrtcEngine){
                            if(mTurnLight){
                                mQnrtcEngine.turnLightOff();
                                mTurnLight=false;
                            }else{
                                mQnrtcEngine.turnLightOn();
                                mTurnLight=true;
                            }
                        }
                        break;
                    //相机切换
                    case 8:
                        if(null!=mQnrtcEngine){
                            mTurnLight=false;
                            mQnrtcEngine.turnLightOff();
                            mQnrtcEngine.switchCamera(new QNCameraSwitchResultCallback() {
                                /**
                                 * 摄像头是否被禁用
                                 * @param mute
                                 */
                                @Override
                                public void onCameraSwitchDone(boolean mute) {
//                                    if(null!=mBeautyControlView&&null!=mBeautyControlView.getFURenderer()) mBeautyControlView.getFURenderer().onCameraChange(mute);
                                }

                                @Override
                                public void onCameraSwitchError(String s) {
                                }
                            });
                        }
                        break;
                }
            }
            @Override
            public void onBack() {
                showComfirmDialog("正在直播，确定要结束并退出直播间吗？","直播已结束",true);
            }
        });
        mLocatSurfaceView = (VideoCallPerviewWindown) findViewById(R.id.view_locat_surfaceview);
        ViewGroup.LayoutParams surFaceLayoutParams = mLocatSurfaceView.getSurfaceView().getLayoutParams();
        surFaceLayoutParams.width= ScreenUtils.getScreenWidth();
        surFaceLayoutParams.height= ScreenUtils.getScreenHeight();
        mLocatSurfaceView.getSurfaceView().setLayoutParams(surFaceLayoutParams);
    }
    
    /**
     * 美颜初始化
     */
//    private void initBeauty () {
//        mBeautyControlView = findViewById(R.id.fu_beauty_control);
//        mBeautyControlView.initBeauty();
//        mLocatSurfaceView.setOnClickListener(new android.view.View.OnClickListener() {
//            @Override
//            public void onClick(android.view.View v) {
//                if(null!=mBeautyControlView) mBeautyControlView.hideBottomLayoutAnimator();
//            }
//        });
//    }

    /**
     * 初始化连麦、画面预览
     */
    private void initRtcEngine() {
        QNVideoFormat previewFormat;
        if(ConfigSet.getInstance().isHWCodecEnabled()){
            previewFormat = new QNVideoFormat(1280, 720, QNRTCSetting.DEFAULT_FPS);
        }else{
            previewFormat = new QNVideoFormat( videoEncodeHeight,videoEncodeWidth, QNRTCSetting.DEFAULT_FPS);
        }
        QNVideoFormat encodeFormat = new QNVideoFormat(videoEncodeHeight, videoEncodeWidth,15);
        QNRTCSetting setting = new QNRTCSetting();
        setting.setCameraID(QNRTCSetting.CAMERA_FACING_ID.FRONT)
                .setHWCodecEnabled(ConfigSet.getInstance().isHWCodecEnabled())//是否开启硬编
                .setMaintainResolution(true);
        mQnrtcEngine = QNRTCEngine.createEngine(getApplicationContext(),setting,this);
        //美颜接入
//        mQnrtcEngine.setCaptureVideoCallBack(new QNCaptureVideoCallback() {
//            @Override
//            public int onRenderingFrame(int textureId, int width, int height, VideoFrame.TextureBuffer.Type type, long timestampNs) {
//                try {
//                    if(null!=mBeautyControlView&&null!=mBeautyControlView.getFURenderer()){
//                        return mBeautyControlView.getFURenderer().onRenderingFrame(textureId,width,height,timestampNs);
//                    }
//                }catch (RuntimeException e){
//
//                }catch (Exception e){
//
//                }finally {
//
//                }
//                return 0;
//            }
//
//            @Override
//            public void onPreviewFrame(byte[] bytes, int width, int height, int rotation, int fmt, long timestampNs) {
//                if(null!=mBeautyControlView&&null!=mBeautyControlView.getFURenderer())  mBeautyControlView.getFURenderer().onPreviewFrame(bytes,width,height,rotation,fmt,timestampNs);
//            }
//        });
        //创建Track，Track：用户发布流的通道
        // 设置视频码率
        mLocalVideoTrack = mQnrtcEngine.createTrackInfoBuilder()
                .setVideoEncodeFormat(encodeFormat)
                .setVideoPreviewFormat(previewFormat)
                .setSourceType(QNSourceType.VIDEO_CAMERA)
                .setBitrate(1536*1000)// 设置视频码率
                .setMaster(true)
                .create();
        //设置音频码率
        mLocalAudioTrack = mQnrtcEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.AUDIO)
                .setBitrate(64*1000)
                .setMaster(true)
                .create();
        onBeautyEnable(4);
        //设置预览窗口
        mQnrtcEngine.setRenderWindow(mLocalVideoTrack,mLocatSurfaceView.getSurfaceView());
    }

    /**
     * 创建房间成功后初始化直播间
     */
    protected synchronized void onCreateRoomSucess() {
        if(null==mController) return;
        closeProgressDialog();
        if(isSuccess){
            return;
        }
        isSuccess=true;
        mController.hideLoadingView();
        mController.initData(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
        //更新所在的房间
        mController.startReckonTime();
        mController.showControllerView();
        //自动发送一条消息给主播自己看
        CustomMsgExtra sysMsg=new CustomMsgExtra();
        NoticeContent noticeMessage = UserManager.getInstance().getNoticeMessage(UserManager.NoticeType.Live);
        sysMsg.setCmd(Constant.MSG_CUSTOM_NOTICE);
        sysMsg.setMsgContent(noticeMessage.getContent());
        CustomMsgInfo customInfo = LiveUtils.packMessage(sysMsg, null);
        customInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
        customInfo.setMsgContentColor(noticeMessage.getColor());
        newSystemCustomMessage(customInfo,false);
    }

    /**
     * 禁用本地美颜
     */
    private void onBeautyEnable(int tabIndex) {
        if (null!=mQnrtcEngine) {
            mBeautyEnable = !mBeautyEnable;
            QNBeautySetting beautySetting = new QNBeautySetting(0.9f, 0.5f, 0.9f);
            beautySetting.setEnable(mBeautyEnable);
            mQnrtcEngine.setBeauty(beautySetting);
            if(null!=mController) mController.onBeautyEnable(mBeautyEnable,tabIndex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LiveRoomManager.getInstance().getLiveRoom().onResume();
        if (null!=mController) mController.onResume();
//        if (mBeautyControlView != null) {
//            mBeautyControlView.onResume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null!=mController) mController.onPause();
        LiveRoomManager.getInstance().getLiveRoom().onPause();
    }

    /**
     * 配置主播合流的
     * @param locationTrackInfoList 轨道信息
     */
    private synchronized void setOutputStreamLayout(List<QNTrackInfo> locationTrackInfoList) {
        if(null==mQnrtcEngine||null==locationTrackInfoList) return;
        String anchorVideoTrackID=null;
        String anchorAudioTrackID=null;
        for (QNTrackInfo qnTrackInfo : locationTrackInfoList) {
            if(qnTrackInfo.getTrackKind().equals(QNTrackKind.VIDEO)){
                anchorVideoTrackID=  qnTrackInfo.getTrackId();
            }else if(qnTrackInfo.getTrackKind().equals(QNTrackKind.AUDIO)){
                anchorAudioTrackID=  qnTrackInfo.getTrackId();
            }
        }
        if(!TextUtils.isEmpty(anchorVideoTrackID)){
            //视频合流
            mMergeTrackOptions = new ArrayList<>();
            QNMergeTrackOption videoOption=new QNMergeTrackOption();
            videoOption.setWidth(videoEncodeWidth);
            videoOption.setHeight(videoEncodeHeight);
            videoOption.setTrackId(anchorVideoTrackID);
            mMergeTrackOptions.add(videoOption);
            mQnrtcEngine.setMergeStreamLayouts(mMergeTrackOptions,null);
        }
        if(!TextUtils.isEmpty(anchorAudioTrackID)){
            //音频合流
            List<QNMergeTrackOption> audioTrackOptions=new ArrayList<>();
            QNMergeTrackOption audioOption=new QNMergeTrackOption();
            audioOption.setTrackId(anchorAudioTrackID);
            audioTrackOptions.add(audioOption);
            mQnrtcEngine.setMergeStreamLayouts(audioTrackOptions,null);
        }
    }

    /**
     * 结束推流
     */
    @Override
    protected void exitRoom(LiveRoom.ExitRoomCallback callback) {
        destroyPublish();
        if(null!= mController) mController.stopReckonTime();
        super.exitRoom(callback);
    }

    /**
     * 销毁推流所有状态
     */
    private void destroyPublish() {
        if(null!=mQnrtcEngine){
            if(isDestroying) return;
            isDestroying=true;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    if(null!=mLocationTrackInfoList) mQnrtcEngine.unPublishTracks(mLocationTrackInfoList);
                    if(null!=mMergeTrackOptions) mQnrtcEngine.removeMergeStreamLayouts(mMergeTrackOptions,null);
                    mQnrtcEngine.setPreviewEnabled(false);
                    mQnrtcEngine.stopMergeStream(null);
                    mQnrtcEngine.unPublish();
                    mQnrtcEngine.leaveRoom();
                    mQnrtcEngine.destroy();
                    mQnrtcEngine=null;
                    if(null!=mLocationTrackInfoList) mLocationTrackInfoList.clear();
                    if(null!=mMergeTrackOptions) mMergeTrackOptions.clear();
                    mMergeTrackOptions=null;mLocationTrackInfoList=null;
//                    if(null!=mBeautyControlView){
//                        mBeautyControlView.onDestroy();
//                        mBeautyControlView=null;
//                    }
                    isDestroying=false;
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyPublish();
        mTurnLight=false;isSuccess=false;
        if(null!= mController) mController.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        mLocatSurfaceView=null;mLocalVideoTrack=null;mLocalAudioTrack=null;mRoomInfoExtra=null;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (null != arg && arg instanceof NoticeInfo) {
            final NoticeInfo noticeInfo = (NoticeInfo) arg;
            //关闭直播房间
            if (TextUtils.equals(Constant.NOTICE_CMD_ROOM_CLOSE, noticeInfo.getCmd()) ||
                    //账号被封禁
                    TextUtils.equals(Constant.NOTICE_CMD_ACCOUNT_CLOSE, noticeInfo.getCmd())) {
                if (null != mController && !LiveRoomPusherActivity.this.isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showComfirmDialog(null, noticeInfo.getContent(), false);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if(errorCode== QNErrorCode.ERROR_PLAYER_ALREADY_EXIST||errorCode==QNErrorCode.ERROR_SUBSCRIBE_STREAM_NOT_EXIST){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showCenterToast(errorMsg);
                }
            });
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String errorCodeMsg = VideoCallManager.getInstance().getErrorCodeMsg(errorCode,1);
                exitPublishState(errorCodeMsg);
            }
        });
    }

    //========================================房间状态监控============================================

    /**
     * 房间内状态
     * @param qnRoomState
     */
    @Override
    public void onRoomStateChanged(QNRoomState qnRoomState) {
        switch (qnRoomState) {
            case CONNECTING:
                break;
            case CONNECTED:
                if(null!=mQnrtcEngine&&null!=mLocalVideoTrack&&null!=mLocalAudioTrack){
                    mLocalVideoTrack.setMuted(false);
                    mLocalAudioTrack.setMuted(false);
                    mQnrtcEngine.publishTracks(Arrays.asList(mLocalVideoTrack, mLocalAudioTrack));
                    mQnrtcEngine.publishAudio();
                }
                break;
            case RECONNECTING:
                break;
            case RECONNECTED:
                break;
            case IDLE:
                break;
        }
    }

    /**
     * 远端用户进入房间
     * @param userid
     * @param userData 携带的自定义消息
     */
    @Override
    public void onRemoteUserJoined(String userid, String userData) {
    }

    /**
     * 远端用户离开房间
     * @param userid
     */
    @Override
    public void onRemoteUserLeft(String userid) {
    }

    /**
     * 本地流发布成功
     * @param locationTrackInfoList 轨道
     */
    @Override
    public void onLocalPublished(List<QNTrackInfo> locationTrackInfoList) {
        this.mLocationTrackInfoList =locationTrackInfoList;
        if(null!=mQnrtcEngine) mQnrtcEngine.enableStatistics(6);//日志开启
        setOutputStreamLayout(locationTrackInfoList);
        onCreateRoomSucess();
    }

    /**
     * 收到远端发布的流
     * @param userid
     * @param trackInfoList
     */
    @Override
    public void onRemotePublished(String userid, List<QNTrackInfo> trackInfoList) {
    }

    /**
     * 远端用户流已关闭
     * @param userid
     * @param trackInfoList
     */
    @Override
    public void onRemoteUnpublished(String userid, List<QNTrackInfo> trackInfoList) {
        if(null!=mQnrtcEngine) mQnrtcEngine.unSubscribeTracks(trackInfoList);
    }

    /**
     * 远端用户的流发生变化
     * @param userId
     * @param trackInfoList
     */
    @Override
    public void onRemoteUserMuted(String userId, List<QNTrackInfo> trackInfoList) {
    }

    /**
     * 自定订阅对方成功，开始渲染远端控件，此时认为双方建立通话成功
     * @param userid
     * @param toTrackInfoList
     */
    @Override
    public void onSubscribed(String userid, List<QNTrackInfo> toTrackInfoList) {
    }

    /**
     * 自己被T出房间
     * @param userid
     */
    @Override
    public void onKickedOut(String userid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(userid.equals(UserManager.getInstance().getUserId())){
//                    if (mBeautyControlView != null && mBeautyControlView.isShown()) {
//                        mBeautyControlView.hideBottomLayoutAnimator();
//                    }
                    exitPublishState("已被超管T出房间");
                }
            }
        });
    }

    /**
     * 网络抖动监听
     * @param report
     */
    @Override
    public void onStatisticsUpdated(QNStatisticsReport report) {
        if(null!=report){
            String log = null;
            int packetLostRate=0;
            if (QNTrackKind.AUDIO.equals(report.trackKind)) {
                packetLostRate=report.audioPacketLostRate;
                log= "音频码率:" + report.audioBitrate / 1000 + "kbps \n" + "音频丢包率:" + report.audioPacketLostRate+"\n";
            } else if (QNTrackKind.VIDEO.equals(report.trackKind)) {
                log="视频码率:" + report.videoBitrate / 1000 + "kbps \n" +
                        "视频的宽:" + report.width + " \n" +
                        "视频的高:" + report.height + " \n" +
                        "视频帧率:" + report.frameRate+ " \n"+
                        "视频丢包率:" + report.videoPacketLostRate ;
                packetLostRate=report.videoPacketLostRate;
            }
            Logger.d(TAG,"onStatisticsUpdated-:"+log);
            if(packetLostRate>0){
                ToastUtils.showCenterToast("你的网络环境为弱");
            }
        }
    }

    @Override
    public void onAudioRouteChanged(QNAudioDevice qnAudioDevice) {
    }

    @Override
    public void onCreateMergeJobSuccess(String userid) {
    }

    @Override
    public void onBackPressed() {
//        if (mBeautyControlView != null && mBeautyControlView.isShown()) {
//            mBeautyControlView.hideBottomLayoutAnimator();
//            return;
//        }
        super.onBackPressed();
    }
}