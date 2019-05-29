package com.yc.liaolive.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.yc.liaolive.bean.ChatExtra;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.index.ui.MainActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.ui.activity.CallWakeActivity;
import com.yc.liaolive.videocall.ui.activity.LiveCallActivity;

/**
 * TinyHung@Outlook.com
 * 2018/8/4
 * 监听通知栏点击事件
 */

public class NotificationClickReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationClickReceiver";

    @Override
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getAction();
        //触发直播间推送消息
        if(action.equals(Constant.NOTICE_ACTION_CMD_ROOM)){
            if(null!=intent.getParcelableExtra(Constant.APP_START_EXTRA_ROOM)){
                RoomExtra roomExtra = intent.getParcelableExtra(Constant.APP_START_EXTRA_ROOM);
                startConsumeLiveRoom(context.getApplicationContext(),roomExtra);
            }
        //触发视频通话来电
        }else if(action.equals(Constant.NOTICE_ACTION_CMD_CALL)){
            if(null!=intent.getParcelableExtra(Constant.APP_START_EXTRA_CALL)){
                CallExtraInfo callExtraInfo = intent.getParcelableExtra(Constant.APP_START_EXTRA_CALL);
                startAcceptCall(context.getApplicationContext(),callExtraInfo);
            }
        //触发推送的虚拟视频通话
        }else if(action.equals(Constant.NOTICE_ACTION_CMD_CALL_FALSE)){
            if(null!=intent.getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE)){
                CustomMsgCall callExtraInfo = intent.getParcelableExtra(Constant.APP_START_EXTRA_CALL_FALSE);
                startConsumeCall(context.getApplicationContext(),callExtraInfo);
            }
        //私信
        }else if(action.equals(Constant.NOTICE_ACTION_CMD_CHAT_MSG)){
            if(null!=intent.getSerializableExtra(Constant.APP_START_EXTRA_CHAT)){
                ChatExtra chatExtra = (ChatExtra) intent.getSerializableExtra(Constant.APP_START_EXTRA_CHAT);
                startChatMsg(context.getApplicationContext(),chatExtra);
            }
        //其他通知类型,只是打开主页
        }else if(action.equals(Constant.NOTICE_ACTION_CMD_NOTICE)){
            startAppToTop(context.getApplicationContext());
        }
    }

    /**
     * 消费直播间消息推送
     * @param context
     * @param roomExtra
     */
    private void startConsumeLiveRoom(Context context, RoomExtra roomExtra) {
        //如果APP属于存活状态
        if(SystemUtils.isAppRunning(context, context.getPackageName())){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //启动LiveRoomPullActivity传入参数
            Intent livePlayer = new Intent(context, LiveRoomPullActivity.class);
            livePlayer.putExtra(Constant.APP_START_EXTRA_ROOM,roomExtra);
            final Intent[] intents = {mainIntent, livePlayer};
            LiveRoomPullActivity liveRoomPullActivity = LiveRoomPullActivity.getInstance();
            if(null!=liveRoomPullActivity){
                liveRoomPullActivity.finish();
            }
            context.startActivities(intents);
        }else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(Constant.APP_START_EXTRA_ROOM, roomExtra);
            context.startActivity(launchIntent);
        }
    }

    /**
     * 接受真实的视频通话来电请求
     * @param context
     * @param callExtraInfo
     */
    private  void startAcceptCall(final Context context, CallExtraInfo callExtraInfo) {
        if(null==callExtraInfo||null==context) return;
        //如果超过收到消息时起50秒后接听，视为过期通话
        long currentTimeMillis = System.currentTimeMillis();
        long durtion = currentTimeMillis - callExtraInfo.getSystemTime();
        if(durtion>50000){
            ToastUtils.showCenterToast("视频通话已过期");
            return;
        }
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
            //启动SplashActivity 传入参数
            launchIntent.putExtra(Constant.APP_START_EXTRA_CALL,callExtraInfo);
            context.startActivity(launchIntent);
        }
    }

    /**
     * 处理点击了推送视频事件
     * @param context
     * @param callExtraInfo
     */
    private  void startConsumeCall(final Context context, CustomMsgCall callExtraInfo) {
        if(null==callExtraInfo||null==context) return;
        //如果APP属于存活状态
        if(SystemUtils.isAppRunning(context, context.getPackageName())){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //启动CallWakeActivity传入参数
            Intent intent = new Intent(context, CallWakeActivity.class);
            intent.putExtra(Constant.APP_START_EXTRA_CALL_FALSE,callExtraInfo);
            final Intent[] intents = {mainIntent, intent};
            context.startActivities(intents);
        }else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(Constant.APP_START_EXTRA_CALL_FALSE, callExtraInfo);
            context.startActivity(launchIntent);
        }
    }

    /**
     * 处理接收到私信通知栏点击事件
     * @param context
     * @param chatExtra
     */
    private void startChatMsg(Context context, ChatExtra chatExtra) {
        if(null==chatExtra||null==context) return;
        //如果APP属于存活状态
        if(SystemUtils.isAppRunning(context, context.getPackageName())){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //启动ChatActivity传入参数
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("identify",chatExtra.getIdentify());
            intent.putExtra("type",chatExtra.getType());
            final Intent[] intents = {mainIntent, intent};
            context.startActivities(intents);
        }else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launchIntent.putExtra(Constant.APP_START_EXTRA_CHAT, chatExtra);
            context.startActivity(launchIntent);
        }
    }

    /**
     * 打开APP至主页
     * @param context
     */
    private void startAppToTop(Context context) {
        //如果APP属于存活状态
        if(SystemUtils.isAppRunning(context, context.getPackageName())){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(mainIntent);
        }else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
        }
    }
}
