package com.yc.liaolive.videocall.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.music.player.lib.manager.MusicWindowManager;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityCallWakeBinding;
import com.yc.liaolive.manager.ConfigSet;
import com.yc.liaolive.media.manager.LiveVideoPlayerManager;
import com.yc.liaolive.media.manager.VideoAudioManager;
import com.yc.liaolive.media.view.VideoPlayerStatusController;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.listsner.OnVideoCallBackListener;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.ui.dialog.QuireAnchorDialog;
import com.yc.liaolive.videocall.view.LiveCallInLayout;

/**
 * TinyHung@Outlook.com
 * 2018/11/22
 * 视频通话 唤醒
 */

public class CallWakeActivity extends BaseActivity<ActivityCallWakeBinding> implements MakeCallManager.OnActionListener {

    private CustomMsgCall mCustomMsgCall;
    private LiveCallInLayout mCallWakeLayout;
    private Handler mHandler;
    private LiveVideoPlayerManager mPlayerManager;

    public static void start(Context context, CustomMsgCall customMsgCall) {
        Intent intent=new Intent(context,CallWakeActivity.class);
        intent.putExtra(Constant.APP_START_EXTRA_CALL_FALSE,customMsgCall);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mCustomMsgCall = intent.getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE);
        if(null==mCustomMsgCall){
            ToastUtils.showCenterToast("参数错误");
            return;
        }
        MusicWindowManager.getInstance().onInvisible();
        VideoAudioManager.getInstance().getAudioManager(getApplicationContext()).requestAudioFocus();
        VideoCallManager.getInstance().setBebusying(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);//禁止用户截屏
        setContentView(R.layout.activity_call_wake);
    }

    @Override
    public void initViews() {
        if(null==mCustomMsgCall) return;
        //虚拟呼叫初始化
        mCallWakeLayout = new LiveCallInLayout(this);
        mCallWakeLayout.setOnFunctionListener(new LiveCallInLayout.OnFunctionListener() {
            //接听
            @Override
            public void onAcceptCall() {
                mCallWakeLayout.onReset();
                if(null!=mHandler){
                    mHandler.removeMessages(0);
                    mHandler=null;
                }
                if(null!=mCustomMsgCall){
                    UserManager.getInstance().sendHangupReceiptMsg(mCustomMsgCall.getAnchorId(),UserManager.getInstance().getUserId(),1,null);
                    CallExtraInfo callExtraInfo=new CallExtraInfo();
                    callExtraInfo.setToUserID(mCustomMsgCall.getAnchorId());
                    callExtraInfo.setToNickName(mCustomMsgCall.getAnchorNickName());
                    callExtraInfo.setToAvatar(mCustomMsgCall.getAnchorAvatar());
                    callExtraInfo.setAnchorFront(mCustomMsgCall.getFile_img());
                    callExtraInfo.setVideoPath(mCustomMsgCall.getFile_path());
                    MakeCallManager.getInstance()
                            .attachActivity(CallWakeActivity.this)
                            .interceptAction(true)
                            .registerListener(CallWakeActivity.this)
                            .mackCall(callExtraInfo, 2);
                }
            }
            //拒绝
            @Override
            public void onRejectCall() {
                if(null!=mCustomMsgCall) UserManager.getInstance().sendHangupReceiptMsg(mCustomMsgCall.getAnchorId(),UserManager.getInstance().getUserId(),2,null);
                if (ConfigSet.getInstance().isAudioOpenWindown()) {
                    MusicWindowManager.getInstance().onVisible();
                }
                VideoAudioManager.getInstance().releaseAudioFocus();
                VideoCallManager.getInstance().setBebusying(false);
                finish();
            }
        });
        bindingView.rootView.addView(mCallWakeLayout);
        mCallWakeLayout.setHeadImg(mCustomMsgCall.getAnchorAvatar());
        mCallWakeLayout.setNickname(mCustomMsgCall.getAnchorNickName());
        mCallWakeLayout.setNickNameTextSize(18);
        mCallWakeLayout.setDesc(TextUtils.isEmpty(mCustomMsgCall.getDesc()) ? "对方正在等待..." : mCustomMsgCall.getDesc());
        boolean ismine=TextUtils.equals(UserManager.getInstance().getUserId(),mCustomMsgCall.getCallUserID());
        mCallWakeLayout.setTips("邀请你视频聊天");
        mCallWakeLayout.showCallTipsView(ismine);
        mCallWakeLayout.onStart();
        mPlayerManager = (LiveVideoPlayerManager) findViewById(R.id.video_view);
        mPlayerManager.setLooping(true);
        mPlayerManager.setStatusController(new VideoPlayerStatusController(CallWakeActivity.this));
        mPlayerManager.setMuteMode(true);
    }

    @Override
    public void initData() {
        //小视频、封面
        if(null!=mCustomMsgCall){
            mHandler=new Handler();
            timeOutClose();
            if(null!=mPlayerManager){
                mPlayerManager.setVideoCover(mCustomMsgCall.getFile_img(),true);
                mPlayerManager.startPlay(mCustomMsgCall.getFile_path(),true);
            }
        }
    }

    /**
     * 呼叫、来电界面销毁
     */
    private void destroyCallAction() {
        if(null!= mPlayerManager) {
            mPlayerManager.onDestroy();
            mPlayerManager =null;
        }
        if(null!=mCallWakeLayout){
            mCallWakeLayout.onDestroy();
            mCallWakeLayout=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=mPlayerManager) {
            mPlayerManager.onStart();
            mPlayerManager.onInForeground();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!= mPlayerManager){
            mPlayerManager.onStop();
            mPlayerManager.onInBackground(true);
        }
    }

    /**
     * 定时关闭
     */
    private void timeOutClose() {
        if(null!=mCustomMsgCall&&mCustomMsgCall.getTime_out()>0){
            if(null!=mHandler) mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, SystemClock.uptimeMillis()+(mCustomMsgCall.getTime_out()*1000));
        }
    }

    /**
     * 发起视频通话事件回调
     * @param code
     * @param msg
     */
    @Override
    public void onCallAction(int code, String msg) {
        //通话权限检查通过
        if(Constant.CHEKCED_REQUST_CALL_SECCESS==code){
            finish();
            return;
        }
        //金币不足
        if(NetContants.API_RESULT_ARREARAGE_CODE==code){
            showRechgre();
            return;
        }
        //可预约主播
        if(NetContants.API_RESULT_USER_OFLINE==code){
            makeOnUser();
            return;
        }
        //用户需要芝麻认证
        if(NetContants.API_RESULT_NO_BIND_ZHIMA==code){
            showVerificationZhima();
            return;
        }
        ToastUtils.showCenterToast(msg);
    }


    /**
     * 购买钻石
     */
    private void showRechgre() {
        CallWakeRechargeActivity.start(CallWakeActivity.this);
    }

    /**
     * 预约主播
     */
    private void makeOnUser(){
        if(null==mCustomMsgCall||this.isFinishing()) return;
        //去邀请上线
        QuireAnchorDialog.getInstance(CallWakeActivity.this)
                .setTipsData("预约TA",getResources().getString(R.string.make_call_tips))
                .setTitle(Html.fromHtml(getResources().getString(R.string.call_wake_tips)))
                .setAuthorAvatar(mCustomMsgCall.getAnchorAvatar())
                .setOnSubmitClickListener(new QuireAnchorDialog.OnSubmitClickListener() {
                    /**
                     * 用户确定预约
                     */
                    @Override
                    public void onSubmit() {
                        super.onSubmit();
                        if(null!=mCustomMsgCall){
                            showProgressDialog("预约中，请稍后..",true);
                            MakeCallManager.getInstance().subscribeAnchor(UserManager.getInstance().getUserId(),mCustomMsgCall.getAnchorId(), new OnVideoCallBackListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    closeProgressDialog();
                                    ToastUtils.showCenterToast("已预约");
                                    finish();
                                }

                                @Override
                                public void onFailure(int code, String errorMsg) {
                                    closeProgressDialog();
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            });
                        }
                    }

                    /**
                     * 前往用户中心
                     */
                    @Override
                    public void onStartUserCenter() {
                        super.onStartUserCenter();
                        if(null!=mCustomMsgCall) PersonCenterActivity.start(AppEngine.getApplication().getApplicationContext(),mCustomMsgCall.getAnchorId());

                    }
                })
                .show();
    }

    /**
     * 芝麻认证对话框
     */
    private void showVerificationZhima() {
        ToastUtils.showCenterToast("需要芝麻认证");
    }

    @Override
    public void finish() {
        super.finish();
        destroyCallAction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mHandler){
            mHandler.removeMessages(0);
            mHandler=null;
        }
        destroyCallAction();
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
    public void onBackPressed() {}
}