package com.yc.liaolive.videocall.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.music.player.lib.manager.MusicWindowManager;
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
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.NoticeContent;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CustomMsgExtra;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.constants.LiveConstant;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.live.view.SurfaceStateWindown;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.media.manager.VideoAudioManager;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallCloseExtra;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.listsner.LiveCallInterface;
import com.yc.liaolive.videocall.listsner.OnCallMessageListener;
import com.yc.liaolive.videocall.manager.AudioPlayerManager;
import com.yc.liaolive.videocall.manager.CallVibratorManager;
import com.yc.liaolive.videocall.manager.LiveCallHelper;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.view.LiveCallController;
import com.yc.liaolive.videocall.view.LiveCallInLayout;
import com.yc.liaolive.videocall.view.LiveCallOutLayout;
import com.yc.liaolive.videocall.view.VideoCallPerviewWindown;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/12/20
 * 视频通话
 */

public class LiveCallActivity extends TopBaseActivity implements  QNRTCEngineEventListener,OnCallMessageListener{

    private static final String TAG = "LiveCallActivity";
    public static final int CALL_TYPE_VIDEO=0;//视频聊天
    public static final int CALL_TYPE_VOICE=1;//语音聊天
    public static final int CALL_IDENTIFY_MAKE=0;//发起
    public static final int CALL_IDENTIFY_ACCEPT=1;//接听
    //呼出、呼入
    private LiveCallInterface mCallInterface;
    private boolean isCalling=false;//是否通话中
    private boolean mVideoMute = false;//本地视频流是否被禁用
    private boolean mAudioMute = false;//本地音频流是否被禁用
    private boolean mBeautyEnable=false;//美颜是否开启
    private int videoEncodeWidth =480;//编码输出分辨率
    private int videoEncodeHeight =848;
    private VideoCallPerviewWindown mLocatSurfaceView;
    private VideoCallPerviewWindown mRemoteSurfaceView;
    private SurfaceStateWindown mLocatWindown;//本地相机、语音状态
    private SurfaceStateWindown mRemoteWindown;//远端相机、语音状态
    //房间和流
    private QNRTCEngine mQnrtcEngine;
    private QNTrackInfo mLocalVideoTrack;//本地视频轨道
    private QNTrackInfo mLocalAudioTrack;//本地音频轨道
    private List<QNTrackInfo> mLocationTrackInfoList;//本端轨道
    private List<QNMergeTrackOption> mMergeTrackOptions;
    private boolean isDestroying;//是否正在销毁中
    private LiveCallController mCallController;//业务交互
    private CallExtraInfo mCallExtraInfo;//通话参数
    private QuireDialog mFinishTipsDialog;//结算
    private long mStartJoinRoomMillis;
//    private BeautyControlView mBeautyControlView; //美颜组件
    private List<QNTrackInfo> mRemoteTrackInfoList;
    private long mEnterRoomMillis;
    private FrameLayout mViewCallAction;
    private Handler mHandler;
    private int CONNETC_COUNT=0;//重试次数

    /**
     * 呼叫入口
     * @param context
     * @param callExtraInfo 呼叫信息
     */
    public static void makeCall(Context context,CallExtraInfo callExtraInfo){
        start(context,callExtraInfo,CALL_IDENTIFY_MAKE,CALL_TYPE_VIDEO);
    }

    /**
     * 接听入口
     */
    public static void acceptCall(Context context,CallExtraInfo callExtraInfo){
        start(context,callExtraInfo,CALL_IDENTIFY_ACCEPT,CALL_TYPE_VIDEO);
    }

    private static void start(Context context,CallExtraInfo callExtraInfo,int enterIdentify,int callType) {
        if(null==callExtraInfo){
            ToastUtils.showCenterToast("视频通话失败，通话信息为空");
            return;
        }
        callExtraInfo.setCallType(callType);
        callExtraInfo.setEnterIdentify(enterIdentify);
        LiveCallHelper.getInstance().setCallData(callExtraInfo);
        Intent intent=new Intent(context,LiveCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        VideoCallManager.getInstance().setBebusying(true);
        setContentView(R.layout.activity_live_call);
        VideoAudioManager.getInstance().getAudioManager(getApplicationContext()).requestAudioFocus();
        MusicWindowManager.getInstance().onInvisible();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if(null==getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL)){
            mCallExtraInfo=LiveCallHelper.getInstance().getCallData();
        }else{
            mCallExtraInfo = getIntent().getParcelableExtra(Constant.APP_START_EXTRA_CALL);
        }
        if(null==mCallExtraInfo||TextUtils.isEmpty(mCallExtraInfo.getSenderRoomToken())||TextUtils.isEmpty(mCallExtraInfo.getReceiverRoomToken())){
            ToastUtils.showCenterToast("参数错误");
            VideoCallManager.getInstance().setCallStatus(VideoCallManager.CallStatus.CALL_FREE);
            finish();
            return;
        }
        Logger.d(TAG,"onCreate--Params:"+mCallExtraInfo.toString());
        if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_ACCEPT){
            //上报统计数据
            long ctime = mCallExtraInfo.getCtime();//开始呼叫时间
            long conTime = System.currentTimeMillis()-ctime;//建立通话耗时
            LogRecordUtils.getInstance().postCallLogs("接听方弹出视频通话界面时间",mCallExtraInfo.getRoomID(),ctime,System.currentTimeMillis(),0,"通信耗时："+conTime);
        }
        //初始化视频通话状态监听
        VideoCallManager.getInstance()
                .setCallOutTimeOut(70000)//设置呼叫超时时间
                .setCallStatus(VideoCallManager.CallStatus.CALL_CONVERSE)//更新用户状态为通话中
                .addCallMessageListener(this);
        //UI初始化
        initViews();
        //权限校验
        requstPermissions();
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
        Logger.d(TAG,"onRequstPermissionResult:"+resultCode);
        if(PREMISSION_SUCCESS==resultCode){
            //美颜初始化
//            initBeauty();
            //连麦推流初始化
            initRtcEngine();
            mCallController.setCallUserInfo(mCallExtraInfo);
            mCallController.initView(mCallExtraInfo);
            initCallAction();
        }else if(PREMISSION_CANCEL==resultCode){
            ToastUtils.showCenterToast("拍照或录音权限被拒绝");
            onFinlish(false,true, "拍照或录音权限未授权");
        }
    }

    /**
     * 初始化呼叫、接通 通话逻辑
     */
    private void initCallAction() {
        if(null==mCallExtraInfo) return;
        mHandler = new Handler(Looper.getMainLooper());
        mViewCallAction = (FrameLayout) findViewById(R.id.view_call_action);
        mViewCallAction.removeAllViews();
        if(null!=mCallInterface){
            mCallInterface.onDestroy();
            mCallInterface=null;
        }
        //接听来电
        if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_ACCEPT){
            Logger.d(TAG,"接听来电");
            //交互处理
            mCallInterface=new LiveCallInLayout(LiveCallActivity.this);
            LiveCallInLayout callInLayout = (LiveCallInLayout) mCallInterface;
            callInLayout.setBackgroundResource(R.drawable.shape_mackcall_bg);
            callInLayout.showCallTipsView(1==getUserIndetity());
            mViewCallAction.addView(((LiveCallInLayout) mCallInterface));
        }else if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_MAKE){
            Logger.d(TAG,"呼叫通话");
            mCallInterface=new LiveCallOutLayout(LiveCallActivity.this);
            mViewCallAction.addView(((LiveCallOutLayout) mCallInterface));
            mStartJoinRoomMillis = System.currentTimeMillis();
            //开始呼叫流程
            mQnrtcEngine.joinRoom(mCallExtraInfo.getSenderRoomToken());
        }else{
            Logger.d(TAG,"未知视频通话");
            VideoCallManager.getInstance().onCancelTimeOutAction();
            ToastUtils.showCenterToast("未知的视频通话");
            finish();
            return;
        }
        //用户交互意图监听
        mCallInterface.setOnFunctionListener(new LiveCallInterface.OnFunctionListener() {
            //接听电话
            @Override
            public void onAcceptCall() {
                isCalling=true;
                mStartJoinRoomMillis = System.currentTimeMillis();
                VideoCallManager.getInstance().onCancelTimeOutAction();//取消超时
                if(null!=mHandler){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            destroyCallAction();
                            if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_ACCEPT){
                                Logger.d(TAG,"onAcceptCall--接听方同意视频通话");
                                VideoCallManager.getInstance().statePost(mCallExtraInfo.getToUserID(),"对方接通中,请稍后...");
                                //呼出界面不可见
                                mQnrtcEngine.joinRoom(mCallExtraInfo.getReceiverRoomToken());
                            }
                        }
                    });
                }
            }
            //拒绝、取消电话
            @Override
            public void onRejectCall() {
                if(null!=mHandler){
                    destroyCallAction();
                    if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_ACCEPT){
                        Logger.d(TAG,"onRejectCall--接听通话方拒绝视频通话");
                        VideoCallManager.getInstance().cancelCall(mCallExtraInfo.getToUserID(),"拒绝了通话邀请");
                        //上报拒接状态
                        MakeCallManager.getInstance().endCall(mCallExtraInfo.getCallUserID(),mCallExtraInfo.getCallAnchorID(),mCallExtraInfo.getRecevierID(),MakeCallManager.getInstance().getIdType(mCallExtraInfo.getCallUserID()),null);
                        stopVideoCall(false,null);
                    }else if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_MAKE){
                        Logger.d(TAG,"onRejectCall--请求通话方取消视频通话");
                        onFinlish(false,true, "取消了视频通话");
                    }
                }
            }
        });
        //初始化界面身份
        mCallInterface.onCreate(mCallExtraInfo,getUserIndetity());
    }

    /**
     * 呼叫、来电界面销毁
     */
    private void destroyCallAction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null!=mCallInterface){
                    mCallInterface.onDestroy();
                }
                if(null!=mViewCallAction){
                    mViewCallAction.removeAllViews();
                }
                mCallInterface=null;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d(TAG,"onNewIntent");
    }

    /**
     * 返回组件持有人的身份
     * @return  1:付费方  0：主播
     */
    public int getUserIndetity() {
        return null!=mCallExtraInfo&&TextUtils.equals(mCallExtraInfo.getCallUserID(),UserManager.getInstance().getUserId())?1:0;
    }

    /**
     * 返回用户身份 触发结束通话 等状态发起人的用户ID
     * @return
     */
    public int getIdType() {
        return null!=mCallExtraInfo&&UserManager.getInstance().getUserId().equals(mCallExtraInfo.getCallUserID())?1:2;
    }

    /**
     * 初始化连麦、画面预览
     */
    private void initRtcEngine() {
        QNVideoFormat previewFormat;
        if(ConfigSet.getInstance().isHWCodecEnabled()){
            previewFormat = new QNVideoFormat(1280, 720, QNRTCSetting.DEFAULT_FPS);
        }else{
            previewFormat = new QNVideoFormat(videoEncodeHeight, videoEncodeWidth, QNRTCSetting.DEFAULT_FPS);
        }
        QNVideoFormat encodeFormat = new QNVideoFormat(videoEncodeHeight, videoEncodeWidth, 15);
        QNRTCSetting setting = new QNRTCSetting();
        setting.setCameraID(QNRTCSetting.CAMERA_FACING_ID.FRONT)
                .setHWCodecEnabled(ConfigSet.getInstance().isHWCodecEnabled())//硬编是否开启
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
//                if(null!=mBeautyControlView&&null!=mBeautyControlView.getFURenderer()) mBeautyControlView.getFURenderer().onPreviewFrame(bytes,width,height,rotation,fmt,timestampNs);
//            }
//        });
        //创建Track，Track：用户发布流的通道
        // 设置视频码率
        mLocalVideoTrack = mQnrtcEngine.createTrackInfoBuilder()
                .setVideoEncodeFormat(encodeFormat)
                .setVideoPreviewFormat(previewFormat)
                .setSourceType(QNSourceType.VIDEO_CAMERA)
                .setBitrate(1536*1000)// 设置视频码率mBeautyControlView
                .setMaster(true)
                .create();
        //设置音频码率
        mLocalAudioTrack = mQnrtcEngine.createTrackInfoBuilder()
                .setSourceType(QNSourceType.AUDIO)
                .setBitrate(64*1000)
                .setMaster(true)
                .create();
        //默认开启美颜
        onBeautyEnable();
        mQnrtcEngine.setRenderWindow(mLocalVideoTrack,mLocatSurfaceView.getSurfaceView());
    }

    /**
     * 组件初始化
     */
    private void initViews() {
        LiveRoomManager.getInstance().getLiveRoom().setCurrRoomID(mCallExtraInfo.getRoomID());
        //UI初始化
        mLocatSurfaceView = (VideoCallPerviewWindown) findViewById(R.id.view_locat_surfaceview);
        mRemoteSurfaceView = (VideoCallPerviewWindown) findViewById(R.id.view_remote_surfaceview);
        //小窗口层级最高
        mLocatSurfaceView.getSurfaceView().setZOrderMediaOverlay(true);
        //本地小窗口初始化
        ViewGroup.LayoutParams layoutParams = mLocatSurfaceView.getSurfaceView().getLayoutParams();
        int width= ScreenUtils.getScreenWidth()/4;
        int height=width/9*16;
        layoutParams.width=width;
        layoutParams.height=height;
        mLocatSurfaceView.getSurfaceView().setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParamsGroup = mLocatSurfaceView.getLayoutParams();
        layoutParamsGroup.width=width;
        layoutParamsGroup.height=height;
        mLocatSurfaceView.setLayoutParams(layoutParamsGroup);
        Logger.d(TAG,"小窗口：width:"+width+",height:"+height);

        //本端相机窗口状态
        mLocatWindown = new SurfaceStateWindown(LiveCallActivity.this);
        mLocatWindown.setUserID(UserManager.getInstance().getUserId());
        mLocatSurfaceView.setWindownStateView(mLocatWindown);
        //远端相机窗口状态
        mRemoteWindown = new SurfaceStateWindown(LiveCallActivity.this);
        mRemoteWindown.setUserID(mCallExtraInfo.getToUserID());
        mRemoteSurfaceView.setWindownStateView(mRemoteWindown);

        //交互控制器
        mCallController = new LiveCallController(this);
        mCallController.setGroupID(mCallExtraInfo.getRoomID());
        //事件触发
        mCallController.setOnFunctionListener(new LiveCallController.OnFunctionListener() {
            //相机开关
            @Override
            public void onChangeCamera(ImageView button) {
                onMutedVideo(button);
            }
            //相机切换
            @Override
            public void onSwitchCamera() {
                if (mVideoMute) {//摄像头未打开，提示先打开摄像头
                    ToastUtils.showCenterToast("请先打开摄像头");
                } else{
                    switchCamera();
                }
            }
            //麦克风开关
            @Override
            public void onChangeMic(ImageView button) {
                super.onChangeMic(button);
                onMutedAudio(button);
            }
            //美颜
            @Override
            public void onBeauty() {
//                if (mBeautyControlView.isShown()) {
//                    mBeautyControlView.hideBottomLayoutAnimator();
//                } else {
//                    mBeautyControlView.showBottomLayoutAnimator();
//                }
                onBeautyEnable();
            }
            //挂断视频通话
            @Override
            public void onEndCall(boolean isShowTips,int idType,String message) {
                onFinlish(isShowTips,false,message);
            }
            //视频通话强制结束
            @Override
            public void onFinlishCall(int idTyp,String message) {
                //先立即结束通话状态(定时器)
                onFinlish(false,false,message);
            }
        });
        FrameLayout viewCallController = (FrameLayout) findViewById(R.id.view_call_controller);
        viewCallController.addView(mCallController);
        //在聊天列表中增加一条本地系统消息
        CustomMsgExtra sysMsg=new CustomMsgExtra();
        sysMsg.setCmd(Constant.MSG_CUSTOM_NOTICE);
        NoticeContent noticeMessage = UserManager.getInstance().getNoticeMessage(UserManager.NoticeType.PrivateLive);
        sysMsg.setMsgContent(noticeMessage.getContent());
        CustomMsgInfo customInfo = LiveUtils.packMessage(sysMsg, null);
        customInfo.setAccapGroupID(LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID());
        customInfo.setMsgContentColor(noticeMessage.getColor());
        mCallController.newSystemCustomMessage(customInfo,false);
    }

    /**
     * 美颜初始化
     */
//    private void initBeauty () {
//        mBeautyControlView = findViewById(R.id.fu_beauty_control);
//        mBeautyControlView.initBeauty();
//        mRemoteSurfaceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(null!=mBeautyControlView){
//                    mBeautyControlView.hideBottomLayoutAnimator();
//                }
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mCallController) mCallController.onResume();
//        if (mBeautyControlView != null) {
//            mBeautyControlView.onResume();
//        }
    }

    /**
     * 禁用本地视频流发布
     * @param button
     */
    private void onMutedVideo(ImageView button) {
        if(null!=mQnrtcEngine&&null!=mLocalVideoTrack&&null!=mLocalAudioTrack){
            mVideoMute = ! mVideoMute;
            Logger.d(TAG,"onMutedVideo--muted:"+ mVideoMute);
            mLocalVideoTrack.setMuted(mVideoMute);
            //本地UI及预览窗口显示
            if(null!=mLocatSurfaceView) mLocatSurfaceView.getSurfaceView().setVisibility(mVideoMute ? View.GONE : View.VISIBLE);
            if(null!=mLocatWindown) mLocatWindown.onRemoteMute(UserManager.getInstance().getUserId(), mAudioMute, mVideoMute,true);
            if(null!=button){
                button.setImageResource(mVideoMute ? R.drawable.video_call_record_open:R.drawable.video_call_record_close);
            }else{
                if(null!=mCallController){
                    ((ImageView)mCallController.findViewById(R.id.view_btn_camera)).setImageResource(mVideoMute ? R.drawable.video_call_record_open:R.drawable.video_call_record_close );
                }
            }
            //更新流发布状态
            mQnrtcEngine.muteTracks(Arrays.asList(mLocalVideoTrack, mLocalAudioTrack));
        }
    }

    /**
     * 禁用本地音频发布
     * @param button
     */
    private void onMutedAudio(ImageView button) {
        if(null!=mQnrtcEngine&&null!=mLocalVideoTrack&&null!=mLocalAudioTrack){
            mAudioMute =!mAudioMute;
            Logger.d(TAG,"onMutedAudio--muted:"+ mAudioMute);
            mLocalAudioTrack.setMuted(mAudioMute);
            //本地UI及预览窗口显示
            if(null!=button){
                button.setImageResource(mAudioMute ?R.drawable.video_call_sound_open: R.drawable.video_call_sound_close );
            }else{
                if(null!=mCallController){
                    ((ImageView)mCallController.findViewById(R.id.view_btn_mic)).setImageResource(mAudioMute ?R.drawable.video_call_sound_open: R.drawable.video_call_sound_close );
                }
            }
            if(null!=mLocatWindown) mLocatWindown.onRemoteMute(UserManager.getInstance().getUserId(), mAudioMute, mVideoMute,true);
            //更新流发布状态
            mQnrtcEngine.muteTracks(Arrays.asList(mLocalVideoTrack, mLocalAudioTrack));
        }
    }

    /**
     * 禁用本地美颜
     */
    private void onBeautyEnable() {
        if(mVideoMute){
            ToastUtils.showCenterToast("请先开启照相机");
            return;
        }
        if (null!=mQnrtcEngine) {
            mBeautyEnable = !mBeautyEnable;
            Logger.d(TAG,"onBeautyEnable--BeautyEnable"+mBeautyEnable);
            QNBeautySetting beautySetting = new QNBeautySetting(0.9f, 0.5f, 0.9f);
            beautySetting.setEnable(mBeautyEnable);
            mQnrtcEngine.setBeauty(beautySetting);
            if(null!=mCallController) mCallController.onBeautyEnable(mBeautyEnable);
        }
    }

    /**
     * 前后置镜头切换
     */
    private void switchCamera() {
        if(null!=mQnrtcEngine){
            mQnrtcEngine.switchCamera(new QNCameraSwitchResultCallback() {
                /**
                 * 摄像头切换状态
                 * @param isFrontCamera
                 */
                @Override
                public void onCameraSwitchDone(boolean isFrontCamera) {
                    Logger.d(TAG,"onCameraSwitchDone："+isFrontCamera);
//                    if(null!=mBeautyControlView&&null!=mBeautyControlView.getFURenderer()) mBeautyControlView.getFURenderer().onCameraChange(isFrontCamera);
                }

                @Override
                public void onCameraSwitchError(String s) {
                    Logger.d(TAG,"onCameraSwitchError-msg:"+s);
                }
            });
        }
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
//        if (mBeautyControlView != null && mBeautyControlView.isShown()) {
//            mBeautyControlView.hideBottomLayoutAnimator();
//            return;
//        }
        //是否已经接通了视频通话
        if(isCalling){
            onFinlish(true,false, "确定要结束视频通话吗？");
            return;
        }
        onFinlish(false,true, "结束了视频通话");
    }

    /**
     * 结束视频通话
     * @param isShowTips 是否弹窗提示结束视频通话?
     * @param isCancel 是否是未接通情况下取消、挂断、超时的通话
     * @param message 说明文字
     */
    private void onFinlish(boolean isShowTips,boolean isCancel, String message) {
        destroyCallAction();
        if(!isShowTips&&null!=mCallExtraInfo){
            LogRecordUtils.getInstance().postCallLogs("视频通话结束",mCallExtraInfo.getRoomID(),0,System.currentTimeMillis(),0,message);
        }
        //未接通就挂断
        if(isCancel){
            Logger.d(TAG,"onFinlish:取消电话");
            if(null!=mCallExtraInfo) VideoCallManager.getInstance().cancelCall(mCallExtraInfo.getToUserID(),message);
            stopVideoCall(false,null);
            return;
        }
        //是否提示用户结束视频通话
        if(isShowTips){
            Logger.d(TAG,"onFinlish:挂断电话");
            if(!LiveCallActivity.this.isFinishing()) {
                mFinishTipsDialog = QuireDialog.getInstance(LiveCallActivity.this);
                mFinishTipsDialog.setTitleText("结束通话提醒")
                        .setContentText(message)
                        .setSubmitTitleText("确定")
                        .setCancelTitleText("取消")
                        .setDialogCancelable(true)
                        .showCloseBtn(true)
                        .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent() {
                                stopVideoCall(true,null);
                            }

                            @Override
                            public void onRefuse() {

                            }
                        }).show();
            }
            return;
        }
        Logger.d(TAG,"onFinlish:强制结束视频通话");
        stopVideoCall(true,message);
    }

    /**
     * 结束视频通话
     * @param isShowDetails 是否弹出结束交互
     * @param message 结束描述
     */
    private void stopVideoCall(boolean isShowDetails,String message) {
        try {
            if(null!=mCallController){
                mCallController.stopReckonTime();
                MakeCallManager.getInstance().endCall(mCallController.getInitiatorUserID(),mCallController.getAnchorUserID(),mCallController.getReserveID(),mCallController.getIdType(),null);
            }
            destroyCallAction();
            //先结束界面，待退出到源界面再结算
            if(isShowDetails&&null!=mCallExtraInfo){
                CallCloseExtra callCloseExtra=new CallCloseExtra();
                callCloseExtra.setCloseMsg(message);
                callCloseExtra.setIdType(null!=mCallController?mCallController.getIdType():0);
                callCloseExtra.setRoomID(mCallExtraInfo.getRoomID());
                callCloseExtra.setToAvatar(mCallExtraInfo.getToAvatar());
                callCloseExtra.setToNickName(mCallExtraInfo.getToNickName());
                callCloseExtra.setToUserID(mCallExtraInfo.getToUserID());
                VideoApplication.getInstance().setCallCloseExtra(callCloseExtra);
            }
            destroyPublish();
        }catch (RuntimeException e){

        }finally {
            finish();
        }
    }

    //==========================================视频通话状态监听======================================

    /**
     * 通话交互信令发送成功
     */
    @Override
    public void onCallCmdSendOK(String cmd) {
        if(null!=mHandler){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null!=mCallExtraInfo){
                        if(LiveConstant.VIDEO_CALL_CMD_MACKCALL.equals(cmd)){
                            //呼叫信令发送成功
                            long millis = System.currentTimeMillis();
                            Logger.d(TAG,"onCallCmdSendOK--:发送信令耗时："+(millis-mEnterRoomMillis)+",建立桥接耗时："+(millis-mStartJoinRoomMillis));
                            if(null!=mCallExtraInfo){
                                LogRecordUtils.getInstance().postCallLogs("呼叫方时间",mCallExtraInfo.getRoomID(),(millis-mStartJoinRoomMillis),millis,0,"呼叫方视频通话准备通信总耗时");
                            }
                            startPublish();
                        }
                    }else{
                        if(null!=mCallController)onFinlish(false,false,"通话失败，缺少必要参数");
                    }
                }
            });
        }
    }

    /**
     * 礼物、关注等自定义消息下发
     * @param customMsgInfo
     */
    @Override
    public void onCustomMessage(final CustomMsgInfo customMsgInfo,final boolean isSystemPro) {
        if(null!=mHandler&&null!=mCallController){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallController.newSystemCustomMessage(customMsgInfo,isSystemPro);
                }
            });
        }
    }

    /**
     * 新的消息下发
     * @param code 内部消息类别
     * @param message 消息体
     * @param toUserID 对方用户ID
     */
    @Override
    public void onStatePost(int code, String message, String toUserID) {
        if(code==LiveConstant.CALL_STATE_POST){
            if(null!=mCallInterface) mCallInterface.setNewMessage(message);
        }
    }

    /**
     * 视频通话拒接、超时、其他错误等回调
     * @param errorCode 错误码 参考 LiveConstant 定义常量值
     * @param errorMsg 错误提示
     * @param toUserID 对方
     */
    @Override
    public void onCallError(int errorCode, String errorMsg,String toUserID) {
        Logger.d(TAG,"onCallError,errorCode:"+errorCode+",errorMsg:"+errorMsg+",toUserID:"+toUserID);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(toUserID, Constant.OBSERVER_CMD_CALL_EXCEPTION);
                if(errorCode==LiveConstant.CALL_STATE_CANCEL){
                    Logger.d(TAG,"通话被对方拒绝");
                    stopVideoCall(false,errorMsg);
                    return;
                }
                if(null!=mCallExtraInfo) LogRecordUtils.getInstance().postCallLogs("CMD",mCallExtraInfo.getRoomID(),0,0,errorCode,errorMsg);
                stopVideoCall(false,null);
            }
        });
    }

    /**
     * 开始发布流
     */
    private void startPublish() {
        isCalling=true;
        if(null!=mQnrtcEngine&&null!=mLocalVideoTrack&&null!=mLocalAudioTrack){
            Logger.d(TAG,"startPublish--开始发布流");
            mLocalAudioTrack.setMuted(false);
            mQnrtcEngine.publishTracks(Arrays.asList(mLocalVideoTrack, mLocalAudioTrack));
        }
    }

    //=========================================七牛云连麦状态监听=====================================

    @Override
    public void onError(int errorCode, String errorMsg) {
        Logger.d(TAG,"onError：errorCode："+errorCode+",errorMsg:"+errorMsg+",THREAD:"+Thread.currentThread().getName());
        if(null!=mCallExtraInfo) LogRecordUtils.getInstance().postCallLogs("视频通话七牛云返回错误",mCallExtraInfo.getRoomID(),0,0,errorCode,errorMsg);
        //加入房间失败，重连
        if(QNErrorCode.ERROR_SIGNAL_IO_EXCEPTION==errorCode){
            if(CONNETC_COUNT>=3){
                ToastUtils.showCenterToast("加入房间失败！");
                stopVideoCall(false,null);
                return;
            }
            if(null!=mCallExtraInfo&&null!=mQnrtcEngine){
                //呼叫
                if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_MAKE){
                    Logger.d(TAG,"onAcceptCall--呼叫方重试");
                    mQnrtcEngine.joinRoom(mCallExtraInfo.getSenderRoomToken());
                //接听
                }else if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_ACCEPT){
                    Logger.d(TAG,"onAcceptCall--接听方重试");
                    //呼出界面不可见
                    mQnrtcEngine.joinRoom(mCallExtraInfo.getReceiverRoomToken());
                }
            }
            CONNETC_COUNT++;
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //连接超时
                if(QNErrorCode.ERROR_SIGNAL_TIMEOUT==errorCode){
                    onFinlish(false,false,"视频通话超时");
                    return;
                }
                //相机未获取到权限或者相机被占用
                if(QNErrorCode.ERROR_DEVICE_CAMERA==errorCode){
                    ToastUtils.showCenterToast("照相机权限未授予或相机被占用！");
                    return;
                }
            }
        });
    }

    /**
     * 进入房间的状态
     * @param qnRoomState
     */
    @Override
    public void onRoomStateChanged(QNRoomState qnRoomState) {
        Logger.d(TAG,"onRoomStateChanged-THREAD:"+Thread.currentThread().getName()+",RoomState:"+qnRoomState);
        switch (qnRoomState) {
            case CONNECTING:
                break;
            case CONNECTED:
                isCalling=true;
                mEnterRoomMillis = System.currentTimeMillis();
                Logger.d(TAG,"onRoomStateChanged--,进入房间耗时："+(mEnterRoomMillis -mStartJoinRoomMillis));
                //开始呼叫、订阅、发布流
                if(null!=mCallExtraInfo){
                    if (null!=mCallController&&mCallController.getUserIndetity() == 0) {
                        //主播端默认显示用户端已关闭摄像头，如果呼叫人不是主播，则此时主播端收不到远端用户的onRemoteMute回调
                        if(null!=mRemoteWindown&&null!=mCallExtraInfo) mRemoteWindown.onRemoteMute(mCallExtraInfo.getToUserID(),false,true,false);
                    }
                    //呼叫方进入房间成功
                    if(mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_MAKE){
                        Logger.d(TAG,"onRoomStateChanged----呼叫方进入房间-开始发起呼叫");
                        VideoCallManager.getInstance().makeCall(mCallExtraInfo.getToUserID(),mCallExtraInfo);
                    }else{
                        Logger.d(TAG,"onRoomStateChanged----接听方进入房间成功-开始发布流");
                        startPublish();
                    }
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
        if(null!=mCallExtraInfo&&null!=mCallController&&mCallExtraInfo.getToUserID().equals(userid)){
            Logger.d(TAG,"onRemoteUserJoined--取消超时事件,开启接听超时事件");
            VideoCallManager.getInstance().onCancelTimeOutAction();
        }else{
            Logger.d(TAG,"onRemoteUserJoined--其他用户、网警");
        }
    }

    /**
     * 远端用户离开房间
     * @param userid
     */
    @Override
    public void onRemoteUserLeft(String userid) {
        Logger.d(TAG,"onRemoteUserLeft-userid:"+userid+"，THREAD:"+Thread.currentThread().getName());
        onUserExit(userid);
    }

    /**
     * 本地流发布成功
     * Android逻辑接听方：先收到呼叫方音视频轨道回调至 onSubscribed，本端 onLocalPublished 回调在 onSubscribed 之后回调,合流方法需要被调用两次
     * @param locationTrackInfoList
     */
    @Override
    public void onLocalPublished(List<QNTrackInfo> locationTrackInfoList) {
        Logger.d(TAG,"onLocalPublished--THREAD:"+Thread.currentThread().getName());
        if(null!=mQnrtcEngine) mQnrtcEngine.enableStatistics(6);//日志开启
        this.mLocationTrackInfoList =locationTrackInfoList;
        isCalling=true;
        if(null!=mCallExtraInfo&&mCallExtraInfo.getEnterIdentify()==CALL_IDENTIFY_ACCEPT){
            Logger.d(TAG,"onLocalPublished--接听方结束接听状态");
            VideoCallManager.getInstance().onCancelTimeOutAction();
        }
        if(null!=mCallController&&mCallController.getUserIndetity() == 1){
            onMutedVideo(null);
        }
        //接听人、非付费人合流
        if(null!=mCallController&&0==mCallController.getUserIndetity()){
            Logger.d(TAG,"onSubscribed：接听人、非付费人合流");
            //发起方开始合流配置
            setOutputStreamLayout(mCallExtraInfo.getCallAnchorID(),mCallExtraInfo.getCallUserID(),mLocationTrackInfoList,mRemoteTrackInfoList);
        }
    }

    /**
     * 收到远端发布的流
     * @param userid
     * @param trackInfoList
     */
    @Override
    public void onRemotePublished(String userid, List<QNTrackInfo> trackInfoList) {
        Logger.d(TAG,"onRemotePublished，userid："+userid+",THREAD:"+Thread.currentThread().getName());
    }

    /**
     * 远端用户流已关闭
     * @param userid
     * @param trackInfoList
     */
    @Override
    public void onRemoteUnpublished(String userid, List<QNTrackInfo> trackInfoList) {
        Logger.d(TAG,"onRemoteUnpublished:userid:"+userid+",THREAD:"+Thread.currentThread().getName());
        if(null!=mQnrtcEngine) mQnrtcEngine.unSubscribeTracks(trackInfoList);
    }

    /**
     * 远端用户的流发生变化
     * @param userId
     * @param trackInfoList
     */
    @Override
    public void onRemoteUserMuted(String userId, List<QNTrackInfo> trackInfoList) {
        Logger.d(TAG,"onRemoteUserMuted-userId:"+userId+",THREAD:"+Thread.currentThread().getName());
        if(null!=mCallExtraInfo&&mCallExtraInfo.getToUserID().equals(userId)){
            if(null!=mRemoteWindown){
                boolean isAudioMuted = false;
                boolean isVideoMuted = false;
                for (QNTrackInfo qnTrackInfo : trackInfoList) {
                    if(qnTrackInfo.isAudio()){
                        isAudioMuted=qnTrackInfo.isMuted();
                    }
                    if(qnTrackInfo.isVideo()){
                        isVideoMuted=qnTrackInfo.isMuted();
                    }
                }
                mRemoteWindown.onRemoteMute(userId,isAudioMuted,isVideoMuted,false);
            }
        }
    }

    /**
     * 自定订阅对方成功，开始渲染远端控件，此时认为双方建立通话成功
     * @param userid
     * @param remoteTrackInfoList 对方轨道
     */
    @Override
    public void onSubscribed(String userid, List<QNTrackInfo> remoteTrackInfoList) {
        this.mRemoteTrackInfoList=remoteTrackInfoList;
        isCalling=true;
        if(null!=mCallExtraInfo){
            if(mCallExtraInfo.getToUserID().equals(userid)){
                if(null!=mQnrtcEngine&&null!=mRemoteSurfaceView){
                    //渲染远端画面
                    for(QNTrackInfo track : remoteTrackInfoList) {
                        if (track.getTrackKind().equals(QNTrackKind.VIDEO)) {
                            mQnrtcEngine.setRenderWindow(track, mRemoteSurfaceView.getSurfaceView());
                        }
                    }
                    //接听人、非付费人合流
                    if(null!=mCallController&&0==mCallController.getUserIndetity()){
                        //发起方开始合流配置
                        setOutputStreamLayout(mCallExtraInfo.getCallAnchorID(),mCallExtraInfo.getCallUserID(),mLocationTrackInfoList,remoteTrackInfoList);
                    }
                    destroyCallAction();
                    //业务逻辑开始
                    if(null!=mCallController){
                        mCallController.startAutoTask();
                        mCallController.startPlayTask();
                    }
                }
            }else{
            }
        }else{
            ToastUtils.showCenterToast("通话错误");
            destroyPublish();
            finish();
        }
    }

    /**
     * 自己被T出了房间
     * @param userid
     */
    @Override
    public void onKickedOut(String userid) {
        if(UserManager.getInstance().getUserId().equals(userid)){
            onFinlish(false,false,"您已被T出房间");
        }
    }

    /**
     * 统计数据回调
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
            if(null!=mCallController){
                mCallController.changedSpeedState(packetLostRate);
            }
        }
    }

    /**
     * 本地音频改变
     * @param qnAudioDevice
     */
    @Override
    public void onAudioRouteChanged(QNAudioDevice qnAudioDevice) {
    }

    /**
     * 合流任务创建成功
     * @param userid
     */
    @Override
    public void onCreateMergeJobSuccess(String userid) {
    }

    /**
     * 用户退出，处理远端用户退出事件
     * @param userid
     */
    private void onUserExit(String userid) {
        if(null!=mCallExtraInfo){
            if(mCallExtraInfo.getToUserID().equals(userid)){
                isCalling=false;
                stopVideoCall(true,"对方已结束视频通话");
            }
        }else{
            //对方结束了流发布
            if(!UserManager.getInstance().getUserId().equals(userid)){
                isCalling=false;
                stopVideoCall(true,"对方已结束视频通话");
            }
        }
    }

    /**
     * 由视频通话发起人设置服务端输出合流画面，主播端合流 主播大画面 用户小画面位于右上角分辨率为主画面1/3
     * @param anchorid 主播ID
     * @param useruid 用户ID 付费人，并非一定是发起人
     * @param locationTrackInfoList 本端轨道
     * @param remoteTrackInfoList 远端轨道
     */
    private synchronized void setOutputStreamLayout(String anchorid, String useruid, List<QNTrackInfo> locationTrackInfoList, List<QNTrackInfo> remoteTrackInfoList) {
        if(null==mQnrtcEngine) return;
        if(null==locationTrackInfoList||null==remoteTrackInfoList) return;
        if(locationTrackInfoList.size()>0&&remoteTrackInfoList.size()>0){
            String anchorVideoTrackID=null;
            String userVideoTrackID=null;
            String anchorAudioTrackID=null;
            String userAudioTrackID=null;
            for (QNTrackInfo qnTrackInfo : locationTrackInfoList) {
                if(qnTrackInfo.getTrackKind().equals(QNTrackKind.VIDEO)){
                    if(UserManager.getInstance().getUserId().equals(anchorid)){
                        anchorVideoTrackID=  qnTrackInfo.getTrackId();
                    }
                    if(UserManager.getInstance().getUserId().equals(useruid)){
                        userVideoTrackID=  qnTrackInfo.getTrackId();
                    }
                }else if(qnTrackInfo.getTrackKind().equals(QNTrackKind.AUDIO)){
                    if(UserManager.getInstance().getUserId().equals(anchorid)){
                        anchorAudioTrackID=  qnTrackInfo.getTrackId();
                    }
                    if(UserManager.getInstance().getUserId().equals(useruid)){
                        userAudioTrackID=  qnTrackInfo.getTrackId();
                    }
                }
            }
            for (QNTrackInfo qnTrackInfo : remoteTrackInfoList) {
                if(qnTrackInfo.getTrackKind().equals(QNTrackKind.VIDEO)){
                    if(!TextUtils.isEmpty(qnTrackInfo.getUserId())&&!TextUtils.isEmpty(qnTrackInfo.getTrackId())){
                        if(qnTrackInfo.getUserId().equals(anchorid)){
                            anchorVideoTrackID=  qnTrackInfo.getTrackId();
                        }
                        if(qnTrackInfo.getUserId().equals(useruid)){
                            userVideoTrackID=  qnTrackInfo.getTrackId();
                        }
                    }
                }else if(qnTrackInfo.getTrackKind().equals(QNTrackKind.AUDIO)){
                    if(!TextUtils.isEmpty(qnTrackInfo.getUserId())&&!TextUtils.isEmpty(qnTrackInfo.getTrackId())){
                        if(qnTrackInfo.getUserId().equals(anchorid)){
                            anchorAudioTrackID=  qnTrackInfo.getTrackId();
                        }
                        if(qnTrackInfo.getUserId().equals(useruid)){
                            userAudioTrackID=  qnTrackInfo.getTrackId();
                        }
                    }
                }
            }

            if(!TextUtils.isEmpty(anchorVideoTrackID)&&!TextUtils.isEmpty(userVideoTrackID)){
                mMergeTrackOptions = new ArrayList<>();
                //主播画面
                QNMergeTrackOption anchorOption=new QNMergeTrackOption();
                anchorOption.setWidth(videoEncodeWidth);
                anchorOption.setHeight(videoEncodeHeight);
                anchorOption.setTrackId(anchorVideoTrackID);
                mMergeTrackOptions.add(anchorOption);
                //用户画面
                int width=videoEncodeWidth/3;
                int height=width/9*16;
                //画面X、Y坐标
                int startX=videoEncodeWidth-20-width;
                int startY=20;
                QNMergeTrackOption userOption=new QNMergeTrackOption();
                userOption.setWidth(width);
                userOption.setHeight(height);
                userOption.setTrackId(userVideoTrackID);
                userOption.setX(startX);
                userOption.setY(startY);
                userOption.setZ(1);
                mMergeTrackOptions.add(userOption);
                mQnrtcEngine.setMergeStreamLayouts(mMergeTrackOptions,null);
            }

            if(!TextUtils.isEmpty(anchorAudioTrackID)&&!TextUtils.isEmpty(userAudioTrackID)){
                List<QNMergeTrackOption> audioTrackOptions=new ArrayList<>();
                //主播画面
                QNMergeTrackOption anchorOption=new QNMergeTrackOption();
                anchorOption.setWidth(videoEncodeWidth);
                anchorOption.setHeight(videoEncodeHeight);
                anchorOption.setZ(0);
                anchorOption.setTrackId(anchorAudioTrackID);
                audioTrackOptions.add(anchorOption);
                QNMergeTrackOption userOption=new QNMergeTrackOption();
                userOption.setTrackId(userAudioTrackID);
                audioTrackOptions.add(userOption);
                mQnrtcEngine.setMergeStreamLayouts(audioTrackOptions,null);
            }
        }
    }

    /**
     * 销毁推流所有状态
     */
    private synchronized void destroyPublish() {
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
    protected void onDestroy() {
        VideoAudioManager.getInstance().releaseAudioFocus();
        if (ConfigSet.getInstance().isAudioOpenWindown()) {
            MusicWindowManager.getInstance().onVisible();
        }
        if(null!=mFinishTipsDialog&&mFinishTipsDialog.isShowing()){
            mFinishTipsDialog.dismiss();
            mFinishTipsDialog=null;
        }
        destroyCallAction();
        if(null!=mLocatSurfaceView) mLocatSurfaceView.onDestroy();
        if(null!=mRemoteSurfaceView) mRemoteSurfaceView.onDestroy();
        if(null!=mRemoteWindown){
            mRemoteWindown.removeAllViews();
        }
        if(null!=mLocatWindown){
            mLocatWindown.removeAllViews();
        }
        super.onDestroy();
        isCalling=false;CONNETC_COUNT=0;
        VideoCallManager.getInstance().setBebusying(false);
        AudioPlayerManager.getInstance().stopPlayer();
        CallVibratorManager.getInstance().onStop();
        destroyPublish();
        LiveCallHelper.getInstance().onReset();
        mLocatSurfaceView=null;mRemoteSurfaceView=null;mLocatWindown=null;mRemoteWindown=null;
        mLocalVideoTrack=null;mLocalAudioTrack=null;
        VideoCallManager.getInstance().setCallStatus(VideoCallManager.CallStatus.CALL_FREE).onReset();
        if(null!=mCallController) mCallController.onDestroy();
        mCallController=null;mStartJoinRoomMillis=0;mCallExtraInfo=null;
        mVideoMute =false;mAudioMute =false;
    }
}