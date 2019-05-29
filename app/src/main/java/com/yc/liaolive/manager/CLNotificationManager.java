package com.yc.liaolive.manager;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ChatExtra;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.service.NotificationClickReceiver;
import com.yc.liaolive.videocall.bean.CallExtraInfo;

/**
 * TinyHung@Outlook.com
 * 2018/12/11
 * 状态栏通知发送管理者
 */

public class CLNotificationManager {

    private static final String TAG = "NotificationManager";
    public static final String CHANNEL_ID="caoliao_notification";
    private static CLNotificationManager mInstance;
    private NotificationManager mManager;
    private int mNotionID=1000;
    //通知栏消息ID
    public static final int NOTIFICATION_ID_CALL = 2001;//视频通话ID
    public static final int NOTIFICATION_ID_CHAT = 2002;//私信

    public static CLNotificationManager getInstance(){
        synchronized (CLNotificationManager.class){
            if(null==mInstance){
                mInstance=new CLNotificationManager();
            }
        }
        return mInstance;
    }

    /**
     * 发送一条视频来电通知
     * @param context
     * @param callExtraInfo
     */
    public synchronized void sendCallNotification(Context context, CallExtraInfo callExtraInfo, int nitificationID) {
        if(null==callExtraInfo) return;
        sendNotification(context,callExtraInfo,nitificationID,NotificationCompat.DEFAULT_ALL);
    }

    /**
     * 发送一条推送的视频来电通知
     * @param context
     * @param callExtraInfo
     */
    public synchronized void sendCallNotification(Context context, CustomMsgCall callExtraInfo) {
        if(null==callExtraInfo) return;
        mNotionID++;
        sendNotification(context,callExtraInfo,mNotionID,NotificationCompat.DEFAULT_ALL);
    }

    /**
     * 发送一条私信的通知
     * @param context
     * @param chatExtra
     * @param defaultSetting 用户状态，如果声音或震动有一个开启
     */
    public synchronized void sendCallNotification(Context context, ChatExtra chatExtra,int defaultSetting) {
        if(null==chatExtra|| TextUtils.isEmpty(chatExtra.getIdentify())) return;
        sendNotification(context,chatExtra, NOTIFICATION_ID_CHAT,defaultSetting);
    }

    /**
     * 发送一条直播间活动推送通知
     * @param context
     * @param roomExtra
     */
    public synchronized void sendCallNotification(Context context, RoomExtra roomExtra){
        mNotionID++;
        sendNotification(context,roomExtra,mNotionID,NotificationCompat.DEFAULT_ALL);
    }

    /**
     * 发送所媒体、私信事件等通知
     * @param context
     * @param object  真实的视频通话、虚拟视频通话推送、私信
     * @param noticationID 标识的ID
     * @param defaultSetting 响铃、震动、呼吸灯等配置
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public synchronized void sendNotification(Context context, Object object, int noticationID, int defaultSetting) {
        if(null==object) return;
        mManager = (android.app.NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        //点击意图
        Intent intentClick = new Intent(context, NotificationClickReceiver.class);
        String title="视频通话邀请";
        String content="给您发来了一个通话邀请";
        //视频通话
        if(object instanceof CallExtraInfo){
            //设置点击意图
            CallExtraInfo callExtraInfo = (CallExtraInfo) object;
            intentClick.setAction(Constant.NOTICE_ACTION_CMD_CALL);
            intentClick.putExtra(Constant.APP_START_EXTRA_CALL, callExtraInfo);
            title=callExtraInfo.getToNickName();
            content="给您发来了一个视频通话邀请";
        //虚拟视频推送
        }else if(object instanceof CustomMsgCall){
            //设置点击意图
            CustomMsgCall customMsgCall = (CustomMsgCall) object;
            intentClick.setAction(Constant.NOTICE_ACTION_CMD_CALL_FALSE);
            intentClick.putExtra(Constant.APP_START_EXTRA_CALL_FALSE,customMsgCall);
            title=customMsgCall.getAnchorNickName();
            content=customMsgCall.getAnchorNickName()+"给您发来了一个视频通话邀请";
        //私信
        }else if(object instanceof ChatExtra){
            ChatExtra chatExtra = (ChatExtra) object;
            intentClick.setAction(Constant.NOTICE_ACTION_CMD_CHAT_MSG);
            intentClick.putExtra(Constant.APP_START_EXTRA_CHAT,chatExtra);
            title=chatExtra.getSendNickName();
            content=chatExtra.getContent();
        //直播间相关活动
        }else if(object instanceof RoomExtra){
            RoomExtra roomExtra= (RoomExtra) object;
            intentClick.setAction(Constant.NOTICE_ACTION_CMD_ROOM);
            intentClick.putExtra(Constant.APP_START_EXTRA_ROOM,roomExtra);
            title=roomExtra.getNoticaTitle();
            content=roomExtra.getNoticaContent();
        }
        //兼容 O系统
        final NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setNotificationChannelID(CHANNEL_ID);
            builder= new NotificationCompat.Builder(context,CHANNEL_ID);
        }else{
            builder= new NotificationCompat.Builder(context);
        }
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        //设置大通知栏样式
        android.support.v4.app.NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);
        style.bigText(content);
        builder.setStyle(style);
        builder.setPriority(Notification.PRIORITY_MAX);//优先级
        builder.setDefaults(defaultSetting);//默认使用闪光灯，铃声，震动
        builder.setAutoCancel(true);//点击后消失
        builder.setCategory(Notification.CATEGORY_CALL);//系统级别通知处理
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntentCancel);
        //ICON,使用网络图片，需要先行下载抓换成Bitmap位图
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        //使用用户头像
//        Glide.with(context)
//                .load(avatar)
//                .asBitmap()
//                .placeholder(R.drawable.ic_launcher)
//                .error(R.drawable.ic_launcher)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
//                .into(new SimpleTarget<Bitmap>(200,200) {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        Logger.d(TAG,"setResource1");
//                        if(null!=resource){
//                            Logger.d(TAG,"setResource2");
//                            builder.setLargeIcon(resource);
//                        }else{
//                            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
//                        }
//                        notification = builder.build();
//                        mManager.notify(noticationID, notification);
//                    }
//                });
        mManager.notify(noticationID, builder.build());
    }

    /**
     * 发送一条普通消息通知
     * @param context
     * @param title
     * @param content
     * @param summaryText
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public synchronized void sendNotification(Context context,String title,String content,String summaryText) {
        mNotionID++;
        mManager = (android.app.NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        //设置基本参数
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setNotificationChannelID(CHANNEL_ID);
            builder= new NotificationCompat.Builder(context,CHANNEL_ID);
        }else{
            builder= new NotificationCompat.Builder(context);
        }
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        //设置大通知栏样式
        android.support.v4.app.NotificationCompat.BigTextStyle style = new android.support.v4.app.NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);
        style.bigText(content);
        style.setSummaryText(summaryText);
        builder.setStyle(style);
        builder.setPriority(Notification.PRIORITY_MAX);//优先级
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);//默认使用闪光灯，铃声，震动
        builder.setAutoCancel(true);//点击后消失
        builder.setCategory(Notification.CATEGORY_CALL);//系统级别通知处理
        //设置点击意图
        Intent intentClick = new Intent(context, NotificationClickReceiver.class);
        intentClick.setAction(Constant.NOTICE_ACTION_CMD_NOTICE);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntentCancel);
        mManager.notify(mNotionID, builder.build());
    }

    /**
     * 兼容Android 8.0
     * @param channelID
     * VISIBILITY_PRIVATE : 显示基本信息，如通知的图标，但隐藏通知的全部内容
     * VISIBILITY_PUBLIC : 显示通知的全部内容
     * VISIBILITY_SECRET : 不显示任何内容，包括图标
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotificationChannelID(String channelID) {
        if(null==mManager) return;
        NotificationChannel chan = new NotificationChannel(channelID, "CAOLIAO_NOTIC", android.app.NotificationManager.IMPORTANCE_DEFAULT);
        //锁屏的时候是否展示通知
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mManager.createNotificationChannel(chan);
    }

    /**
     * 清除当前可能展示的通知
     */
    public void cancelNotification(int notionID) {
        if(null!=mManager){
            mManager.cancel(notionID);
            mManager=null;
        }
    }

    /**
     * 清除所有通知
     */
    public void cancelAllNotification() {
        if(null!=mManager){
            mManager.cancelAll();
            mManager=null;
        }
    }
}
