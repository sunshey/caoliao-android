package com.yc.liaolive.msg.manager;

import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMMessage;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.bean.ChatExtra;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.manager.CLNotificationManager;
import com.yc.liaolive.manager.ForegroundManager;
import com.yc.liaolive.msg.model.CustomMessage;
import com.yc.liaolive.msg.model.FriendshipInfo;
import com.yc.liaolive.msg.model.Message;
import com.yc.liaolive.msg.model.MessageFactory;
import com.yc.liaolive.msg.model.VoiceMessage;
import com.yc.liaolive.observer.MessageEvent;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.SharedPreferencesUtil;
import org.simple.eventbus.EventBus;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/12/12
 * 私信、私信多媒体等消息 在线消息在APP未在前台可见时推送
 */

public class ChatMessagePushManager implements Observer {

    private static final String TAG = "ChatMessagePushManager";
    private static ChatMessagePushManager mInstance;

    public static synchronized ChatMessagePushManager getInstance(){
        synchronized (ChatMessagePushManager.class){
            if(null==mInstance){
                mInstance=new ChatMessagePushManager();
            }
        }
        return mInstance;
    }

    /**
     * 初始化，登录成功调用
     */
    public void onInitPush(){
        MessageEvent.getInstance().addObserver(this);
    }

    /**
     * 作用不大
     */
    public void onDestroy(){
        MessageEvent.getInstance().deleteObserver(this);
        mInstance=null;
    }

    /**
     * 同步消息至通知栏
     * @param msg
     */
    private void sendNotication(TIMMessage msg) {
        //系统消息，自己发的消息，程序在前台的时候，其他中奖、直播、视频通话等自定义消息 不做通知
        if (msg == null || ForegroundManager.getInstance().isForeground() ||
                (msg.getConversation().getType() != TIMConversationType.Group && msg.getConversation().getType() != TIMConversationType.C2C)
                || msg.isSelf() || msg.getRecvFlag() == TIMGroupReceiveMessageOpt.ReceiveNotNotify)
            return;
        Message message = MessageFactory.getMessage(msg);
        if(null==message) return;
        boolean isPunsh=true;
        String contentStr=null, sendNickName,avatar = null;
        //这里只推送私信、多媒体(视频、图片) 消息
        if(message instanceof CustomMessage){
            TIMCustomElem customElem = (TIMCustomElem) msg.getElement(0);
            String result = new String(customElem.getData());
            if(!TextUtils.isEmpty(result)){
                CommonJson<Object> commonUserJson = new Gson().fromJson(result, new TypeToken<CommonJson<Object>>() {}.getType());
                if(!TextUtils.isEmpty(commonUserJson.cmd)&&commonUserJson.cmd.equals(Constant.MESSAGE_PRIVATE_CUSTOM_MEDIA)){
                    CommonJson<CustomMsgInfo> commonJson = new Gson().fromJson(result, new TypeToken<CommonJson<CustomMsgInfo>>() {}.getType());
                    contentStr=commonJson.data.getContent();
                }else{
                    isPunsh=false;
                }
            }
        }
        if(!isPunsh) return;
        //APP设定是否震动和声音
        boolean sound = SharedPreferencesUtil.getInstance().getBoolean(Constant.SOUND_SWITCH, false);
        boolean vibrate = SharedPreferencesUtil.getInstance().getBoolean(Constant.VIBRATE_SWITCH, false);
        //发送者用户昵称
        sendNickName = message.getSender();
        if(TextUtils.isEmpty(contentStr)){
            contentStr = message.getSummary();
        }
        if(null!=FriendshipInfo.getInstance().getProfile(message.getSender())){
            sendNickName=FriendshipInfo.getInstance().getProfile(message.getSender()).getName();
            avatar=FriendshipInfo.getInstance().getProfile(message.getSender()).getAvatarUrl();
        }
        ChatExtra chatExtra=new ChatExtra();
        chatExtra.setIdentify(message.getSender());
        chatExtra.setContent(contentStr);
        chatExtra.setSendNickName(sendNickName);
        chatExtra.setAvatar(avatar);
        chatExtra.setType(msg.getConversation().getType());
        //如果是客服
        if(TextUtils.equals(msg.getConversation().getPeer(), UserManager.getInstance().getServerIdentify())){
            chatExtra.setSendNickName("官方客服");
            chatExtra.setContent("您有新的客服消息，点此查看");
        }
        int defaultSetting= (sound||vibrate) ? NotificationCompat.DEFAULT_ALL: 4;
        CLNotificationManager.getInstance().sendCallNotification(AppEngine.getApplication().getApplicationContext(),chatExtra,defaultSetting);
    }

    /**
     * 处理新的消息
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        if(null!=data&&observable instanceof MessageEvent){
            TIMMessage msg = (TIMMessage) data;
            if (null!=msg) {
                sendNotication(msg);
                Message message = MessageFactory.getMessage(msg);
                if(null!=message&&message instanceof CustomMessage){
                    TIMCustomElem customElem = (TIMCustomElem) msg.getElement(0);
                    String result = new String(customElem.getData());
                    if(!TextUtils.isEmpty(result)){
                        CommonJson<Object> commonUserJson = new Gson().fromJson(result, new TypeToken<CommonJson<Object>>() {}.getType());
                        if(!TextUtils.isEmpty(commonUserJson.cmd) && commonUserJson.cmd.equals(Constant.MESSAGE_PRIVATE_CUSTOM_MEDIA)){
                            EventBus.getDefault().post(msg, "NEW_CHAT_MESSAGE");
                        }
                    }
                } else if(null!=message&&message instanceof VoiceMessage){
                    EventBus.getDefault().post(msg, "NEW_CHAT_MESSAGE");
                }
            }
        }
    }
}
