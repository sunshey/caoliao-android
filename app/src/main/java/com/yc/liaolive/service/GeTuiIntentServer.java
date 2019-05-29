package com.yc.liaolive.service;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.NoticeInfo;
import com.yc.liaolive.bean.NotifaceInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.CLNotificationManager;
import com.yc.liaolive.ui.presenter.UserServerPresenter;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ChannelUtls;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/7/19
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */

public class GeTuiIntentServer extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int i) {
    }

    /**
     * 初始化成功
     * @param context
     * @param s
     */
    @Override
    public void onReceiveClientId(Context context, String s) {
        //在此设定用户TAG
        String[] tags = new String[] {ChannelUtls.getInstance().getChannel()};
        Tag[] tagParam = new Tag[tags.length];

        for (int i = 0; i < tags.length; i++) {
            Tag t = new Tag();
            //name 字段只支持：中文、英文字母（大小写）、数字、除英文逗号和空格以外的其他特殊符号, 具体请看代码示例
            t.setName(tags[i]);
            tagParam[i] = t;
        }
        //设定标签
        int result = PushManager.getInstance().setTag(context,tagParam, System.currentTimeMillis() +"");
        //绑定别名
        boolean bindAlias = PushManager.getInstance().bindAlias(context, UserManager.getInstance().getUserId());
        //绑定CID
        new UserServerPresenter().bindUserToGetui(s, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
            }

            @Override
            public void onFailure(int code, String errorMsg) {
            }
        });
    }

    /**
     * 新的消息透传
     * @param context
     * @param gtTransmitMessage
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        if(null!=gtTransmitMessage&&null!=gtTransmitMessage.getPayload()){
            byte[] payload = gtTransmitMessage.getPayload();
            NotifaceInfo<NoticeInfo> notifaceInfo = new Gson().fromJson(new String(payload), new TypeToken<NotifaceInfo<NoticeInfo>>(){}.getType());
            //身份证审核结果
            if(TextUtils.equals(Constant.NOTICE_CMD_ATTEST,notifaceInfo.getCmd())){
                if(null!=notifaceInfo.getData()){
                    try {
                        //更改本地审核状态
                        UserManager.getInstance().setIdentity_audit(notifaceInfo.getData().getIdentity_audit());
                        VideoApplication.getInstance().setMineRefresh(true);
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_IDENTITY_AUTHENTICATION_SUCCESS);
                        CLNotificationManager.getInstance().sendNotification(context,notifaceInfo.getData().getTitle(),notifaceInfo.getData().getContent(),"身份审核通知");
                    }catch (RuntimeException e){

                    }
                }
            //活动推送--直播间相关
            }else if(TextUtils.equals(Constant.NOTICE_CMD_ROOM,notifaceInfo.getCmd())){
                showRoomNotifica(notifaceInfo.getData());
             //主播直播间被封禁通知
            }else if(TextUtils.equals(Constant.NOTICE_CMD_ROOM_CLOSE,notifaceInfo.getCmd())){
                if(null!=notifaceInfo.getData()){
                    NoticeInfo data = notifaceInfo.getData();
                    data.setCmd(notifaceInfo.getCmd());
                    ApplicationManager.getInstance().observerUpdata(data);
                }
             //账号被封禁了
            }else if(TextUtils.equals(Constant.NOTICE_CMD_ACCOUNT_CLOSE,notifaceInfo.getCmd())){
                if(null!=notifaceInfo.getData()){
                    NoticeInfo data = notifaceInfo.getData();
                    data.setCmd(notifaceInfo.getCmd());
                    ApplicationManager.getInstance().observerUpdata(data);
                }
            //直播间新任务可领取
            }else if(TextUtils.equals(Constant.NOTICE_CMD_ROOM_TASK_FINLISH,notifaceInfo.getCmd())){
                if(null!=notifaceInfo.getData()){
                    NoticeInfo data = notifaceInfo.getData();
                    data.setCmd(notifaceInfo.getCmd());
                    ApplicationManager.getInstance().observerUpdata(data);
                }
            }
        }
    }

    /**
     * 在线状态
     * @param context
     * @param b
     */
    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
    }

    /**
     * 新的通知下发
     * @param context
     * @param gtNotificationMessage
     */
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
    }

    //========================================自定义消息推送处理======================================

    /**
     * 来自后台推送的直播间相关的消息
     * @param data
     */
    private void showRoomNotifica(NoticeInfo data) {
        if(null!=data){
            RoomExtra roomExtra=new RoomExtra();
            roomExtra.setUserid(data.getUserid());
            roomExtra.setNoticaTitle(data.getTitle());
            roomExtra.setNoticaContent(data.getContent());
            CLNotificationManager.getInstance().sendCallNotification(getApplicationContext(),roomExtra);
        }
    }
}
