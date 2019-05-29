package com.yc.liaolive.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.manager.CLNotificationManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.CallInWindowManager;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import com.yc.liaolive.videocall.ui.activity.LiveCallActivity;

/**
 * TinyHung@Outlook.com
 * 2018/12/10
 * 视频来电 辅助监听服务
 */
public class VideoCallListenerService extends Service {

    private static final String TAG = "VideoCallListenerService";
    public static final long TIME_OUT=60*1000;
    private CallExtraInfo mCallExtraInfo;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(null!=intent){
            mCallExtraInfo = intent.getParcelableExtra("callExtra");
        }
        mHandler=new Handler();
        startCallToWindown();
        return START_NOT_STICKY;
    }

    /**
     * 来电响铃处理
     */
    private void startCallToWindown() {
        if(null==mCallExtraInfo) return;
        onReset();
        //直播间、视频通话、视频通话推送交互、无悬浮窗权限交互等界面正在运行允许弹窗
        if(VideoCallManager.getInstance().isBebusying()) return;
        //普通用户
        if(!UserManager.getInstance().isAuthenState()){
            CLNotificationManager.getInstance().sendCallNotification(getApplicationContext(),mCallExtraInfo,CLNotificationManager.NOTIFICATION_ID_CALL);
            return;
        }
        KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            CLNotificationManager.getInstance().sendCallNotification(getApplicationContext(),mCallExtraInfo,CLNotificationManager.NOTIFICATION_ID_CALL);
            return;
        }
        CallInWindowManager layoutToWindow = CallInWindowManager.getInstance()
                .init(getApplicationContext())
                .createFullCallInLayoutToWindow(mCallExtraInfo);
        if(null!=layoutToWindow){
                layoutToWindow.setOnFunctionListener(new CallInWindowManager.OnFunctionListener() {
                @Override
                public void onAcceptCall() {
                    onReset();
                    startAcceptCall(getApplicationContext(),mCallExtraInfo);
                }

                @Override
                public void onRejectCall() {
                    onReset();
                    if(null!=mCallExtraInfo){
                        MakeCallManager.getInstance().endCall(mCallExtraInfo.getCallUserID(),mCallExtraInfo.getCallAnchorID(),mCallExtraInfo.getRecevierID(),MakeCallManager.getInstance().getIdType(mCallExtraInfo.getCallUserID()),null);
                    }
                }
            }).onStart(mCallExtraInfo);
            //超时关闭自身
            timingClose();
        }
    }


    /**
     * 接受来电请求
     * @param context
     * @param callExtraInfo
     */
    private void startAcceptCall(final Context context, CallExtraInfo callExtraInfo) {
        if(null==callExtraInfo||null==context) return;
        //如果APP属于存活状态
        if(SystemUtils.isAppRunning(context, context.getPackageName())){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //启动LiveCallActivity传入参数
            Intent intent = new Intent(context, LiveCallActivity.class);
            intent.putExtra(Constant.APP_START_EXTRA_CALL,callExtraInfo);
            final Intent[] intents = {mainIntent, intent};
            context.startActivities(intents);
        }else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(Constant.APP_START_EXTRA_CALL, callExtraInfo);
            context.startActivity(launchIntent);
        }
    }

    /**
     * 定时关闭响铃
     */
    private void timingClose() {
        if(null==mHandler) mHandler=new Handler();
        mHandler.removeCallbacks(cleanCloseNoticRunnable);
        mHandler.postDelayed(cleanCloseNoticRunnable,TIME_OUT);
    }

    private Runnable cleanCloseNoticRunnable =new Runnable() {
        @Override
        public void run() {
            ToastUtils.showCenterToast("超时未接听");
            if(null!=mCallExtraInfo){
                //上报未接听状态
                MakeCallManager.getInstance().endCall(mCallExtraInfo.getCallUserID(),mCallExtraInfo.getCallAnchorID(),mCallExtraInfo.getRecevierID(),MakeCallManager.getInstance().getIdType(mCallExtraInfo.getCallUserID()),null);
            }
            CLNotificationManager.getInstance().cancelNotification(CLNotificationManager.NOTIFICATION_ID_CALL);
            CallInWindowManager.getInstance().onDestroy();
        }
    };

    /**
     * 还原
     */
    private void onReset() {
        CLNotificationManager.getInstance().cancelNotification(CLNotificationManager.NOTIFICATION_ID_CALL);
        if(null!=mHandler) mHandler.removeCallbacks(cleanCloseNoticRunnable);
        CallInWindowManager.getInstance().onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallExtraInfo=null;
        if(null!=mHandler) mHandler.removeMessages(0);
        CallInWindowManager.getInstance().onDestroy();
    }
}