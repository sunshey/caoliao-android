package com.yc.liaolive.live.im;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.bean.CommonJson;
import com.yc.liaolive.live.bean.MessageUser;
import com.yc.liaolive.live.bean.PushMessage;
import com.yc.liaolive.live.listener.GroupIMMessageListener;
import com.yc.liaolive.msg.model.CustomMessage;
import com.yc.liaolive.msg.model.Message;
import com.yc.liaolive.util.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/5
 * 基于腾讯云 直播间消息中转
 */

public class IMMessageMgr implements TIMMessageListener {

    private static final String TAG = IMMessageMgr.class.getSimpleName();
    private Context mContext;
    private Handler mHandler;
    private String mGroupID;//组件绑定的群组ID
    private IMMessageCallback mMessageListener;

    /**
     * 函数级公共Callback定义
     */
    public interface Callback {
        void onError(int code, String errInfo);

        void onSuccess(Object... args);
    }

    public IMMessageMgr(final Context context) {
        this.mContext = context.getApplicationContext();
        this.mHandler = new Handler(this.mContext.getMainLooper());
        this.mMessageListener = new IMMessageCallback(null);
    }

    /**
     * 设置群组ID
     * @param groupID
     */
    public void setGroupID(String groupID){
        this.mGroupID=groupID;
    }


    /**
     * 群组消息接收注册
     * @param listener
     */
    public void setIMMessageListener(GroupIMMessageListener listener) {
        if(null==mMessageListener) return;
        if(null!=listener){
            this.mMessageListener.setListener(listener);
            TIMManager.getInstance().addMessageListener(IMMessageMgr.this);
        }else{
            if (mMessageListener != null) mMessageListener.setListener(null);
            TIMManager.getInstance().removeMessageListener(IMMessageMgr.this);
        }
    }


    /**
     * 创建IM群组
     * @param groupId   群ID
     * @param groupName 群名称
     * @param callback
     */
    public void createGroup(final String groupId, final String groupName, final Callback callback) {
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                final long timeMillis = System.currentTimeMillis();
                TIMGroupManager.CreateGroupParam groupGroupParam = TIMGroupManager.getInstance().new CreateGroupParam();
                groupGroupParam.setGroupId(groupId);
                groupGroupParam.setGroupName("AVChatRoom");
                TIMGroupManager.getInstance().createGroup(groupGroupParam, new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("创建群 \"%s(%s)\" 失败: %s(%d)", groupName, groupId, s, i);
                        if(null!=callback) callback.onError(i, s);
                    }

                    @Override
                    public void onSuccess(String s) {
                        final long timeMillis2 = System.currentTimeMillis();
                        Logger.d(TAG,"创建群组耗时："+(timeMillis2-timeMillis));
                        printDebugLog("创建群 \"%s(%s)\" 成功", groupName, groupId);
                        mGroupID = groupId;
                        if(null!=callback) callback.onSuccess();
                    }
                });
            }
        });
    }

    /**
     * 加入IM群组
     *
     * @param groupId  群ID
     * @param callback
     */
    public void jionGroup(final String groupId, final Callback callback) {
        final long timeMillis = System.currentTimeMillis();
        TIMGroupManager.getInstance().applyJoinGroup(groupId, "who care?", new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                printDebugLog("加入群 {%s} 失败:%s(%d)", groupId, s, i);
                if (i == 10010) {
                    s = "该房间已被解散";
                }else{
                    s="";
                }
                if(null!=callback) callback.onError(i, s);
            }

            @Override
            public void onSuccess() {
                final long timeMillis2 = System.currentTimeMillis();
                Logger.d(TAG,"加入群组耗时："+(timeMillis2-timeMillis)+",groupId:"+groupId);
                printDebugLog("加入群 {%s} 成功", groupId);
                mGroupID = groupId;
                if(null!=callback) callback.onSuccess();
            }
        });
    }

    /**
     * 退出IM群组
     * @param groupId  群ID
     * @param callback
     */
    public void quitGroup(final String groupId, final Callback callback) {
        if(null==groupId) {
            if(null!=callback) callback.onError(-1,"位退出群失败，未知的群！");
            return;
        }
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        if (i == 10010) {
                            printDebugLog("群 {%s} 已经解散了", groupId);
                            onSuccess();
                        } else {
                            printDebugLog("退出群 {%s} 失败： %s(%d)", groupId, s, i);
                            if(null!=callback) callback.onError(i, s);
                        }
                        mGroupID = null;
                    }

                    @Override
                    public void onSuccess() {
                        printDebugLog("退出群 {%s} 成功", groupId);
                        mGroupID = null;
                        if(null!=callback) callback.onSuccess();
                    }
                });
            }
        });
    }

    /**
     * 销毁IM群组
     *
     * @param groupId  群ID
     * @param callback
     */
    public void destroyGroup(final String groupId, final Callback callback) {
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMGroupManager.getInstance().deleteGroup(groupId, new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        mGroupID=null;
                        printDebugLog("解散群 {%s} 失败：%s(%d)", groupId, s, i);
                        if(null!=callback) callback.onError(i, s);
                    }

                    @Override
                    public void onSuccess() {
                        printDebugLog("解散群 {%s} 成功", groupId);
                        mGroupID = null;
                        if(null!=callback) callback.onSuccess();
                    }
                });
            }
        });
    }

    /**
     * 发送纯文本消息
     * @param content
     * @param isDanmu 是否弹幕消息
     * @param callback
     */
    public void sendTextMessage(final @NonNull String content, boolean isDanmu,final Callback callback) {
        if(TextUtils.isEmpty(mGroupID)){
            if (callback != null) callback.onError(-1, "发送失败，未知的群组，请重新进入房间重试！");
            return;
        }
        if(TextUtils.isEmpty(content)){
            if (callback != null) callback.onError(-1, "发送消息失败，聊天内容为空！");
            return;
        }
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMMessage message=new TIMMessage();
                TIMTextElem elem = new TIMTextElem();
                elem.setText(content);
                message.addElement(elem);
                TIMManager.getInstance().getConversation(TIMConversationType.Group, mGroupID).sendMessage(message, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("[sendGroupCustomMessage] 发送群文本{%s}消息失败: %s(%d)", mGroupID, s, i);
                        if (callback != null) callback.onError(i, s);
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        printDebugLog("[sendGroupCustomMessage] 发送群文本消息成功");
                        if (callback != null) callback.onSuccess(timMessage);
                    }
                });
            }
        });
    }

    /**
     * 发送自定义群消息
     * @param content  自定义消息的内容
     * @param callback
     */
    public void sendGroupCustomMessage(final @NonNull String content, final Callback callback) {
        if(null==mGroupID){
            if (callback != null)
                callback.onError(-1, "发送消息失败，群信息未知！");
            return;
        }
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                Message custMessage = new CustomMessage(CustomMessage.Type.AV_GROUP, content);
                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mGroupID);
                conversation.sendMessage(custMessage.getMessage(), new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("[sendGroupCustomMessage] 发送自定义群{%s}消息失败: %s(%d)", mGroupID, s, i);
                        if (callback != null)
                            callback.onError(i, s);
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        printDebugLog("[sendGroupCustomMessage] 发送自定义群消息成功");
                        if (callback != null)
                            callback.onSuccess();
                    }
                });
            }
        });
    }

    /**
     * 新的消息下发
     * @param list
     * @return
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        for (TIMMessage message : list) {
            if(null!=message&&message.getElementCount()>0){
                for (int i = 0; i < message.getElementCount(); i++) {
                    TIMElem element = message.getElement(i);
                    if(null!=element){
                        switch (element.getType()) {
                            //群组系统消息
                            case GroupSystem:
                                TIMGroupSystemElemType systemElemType = ((TIMGroupSystemElem) element).getSubtype();
                                switch (systemElemType) {
                                    //群组销毁
                                    case TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE:
                                        Logger.d(TAG,"onNewMessage--群销毁"+systemElemType);
                                        if (mMessageListener != null) mMessageListener.onGroupDestroyed(((TIMGroupSystemElem) element).getGroupId());
                                        return true;
                                    //群组自定义消息
                                    case TIM_GROUP_SYSTEM_CUSTOM_INFO:
                                        byte[] userData = ((TIMGroupSystemElem) element).getUserData();
                                        if (userData == null || userData.length == 0) {
                                            printDebugLog("userData == null");
                                            return true;
                                        }
                                        String data = new String(userData);
                                        CommonJson<Object> commonJson = new Gson().fromJson(data, new TypeToken<CommonJson<Object>>() {}.getType());
                                        String getMsgGroupId = ((TIMGroupSystemElem) element).getGroupId();
                                        String json = new Gson().toJson(commonJson.data);
                                        if(!TextUtils.isEmpty(commonJson.cmd)){
                                            Logger.d(TAG,"GROUP_SYSTEM----HOST_GROUP_ID："+mGroupID+",RECEIVE_GROUP_ID:"+getMsgGroupId);
                                            //直播间礼物赠送、中奖、关注、点赞、成员进出 等系统消息
                                            if (commonJson.cmd.equals(Constant.MSG_CUSTOM_ROOM_SYSTEM)) {
                                                if(null!=mMessageListener) mMessageListener.onGroupSystemMsg(getMsgGroupId, message.getSender(), json);
                                            //人数变化,过滤本群消息
                                            } else if(commonJson.cmd.equals(Constant.MSG_CUSTOM_ROOM_SYSTEM_NUMBER)){
                                                if(null!=mMessageListener&&!TextUtils.isEmpty(mGroupID)&&TextUtils.equals(getMsgGroupId,mGroupID)){
                                                    mMessageListener.onGroupSystemMsgNumber(getMsgGroupId, message.getSender(), json);
                                                }
                                            }
                                        }
                                        return true;
                                }
                                return true;
                            //自定义消息
                            case Custom:
                                byte[] userData = ((TIMCustomElem) element).getData();
                                if (userData == null || userData.length == 0) {
                                    return true;
                                }
                                if(null!=message.getConversation()&&message.getConversation().getType()==TIMConversationType.Group){
                                    String groupID = message.getConversation().getPeer();
                                    Logger.d(TAG,"GROUP_CUSTOM_MESSAGE----HOST_GROUP_ID："+mGroupID+",RECEIVE_GROUP_ID:"+groupID);
                                    if(null!=mMessageListener&&!TextUtils.isEmpty(mGroupID)&&TextUtils.equals(groupID,mGroupID)){
                                        String data = new String(userData);
                                        CommonJson<Object> commonJson = new Gson().fromJson(data, new TypeToken<CommonJson<Object>>() {}.getType());
                                        if (!TextUtils.isEmpty(commonJson.cmd)) {
                                            Logger.d(TAG,"GROUP_CUSTOM_MESSAGE----CMD："+commonJson.cmd);
                                            //群组聊天文本消息
                                            if (commonJson.cmd.equals(Constant.MSG_CUSTOM_GROUP_TEXT)) {
                                                ++i;
                                                MessageUser userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), MessageUser.class);
                                                if (userInfo != null && i < message.getElementCount()) {
                                                    TIMElem nextElement = message.getElement(i);
                                                    TIMTextElem textElem = (TIMTextElem) nextElement;
                                                    String text = textElem.getText();
                                                    mMessageListener.onGroupTextMessage(groupID, message.getSender(), userInfo.nickName, userInfo.headPic,0,0,0, text);
                                                }
                                                //群组内其它自定义消息
                                            }else if (commonJson.cmd.equals(Constant.MSG_CUSTOM_GROUP_CUSTOM_CMD)) {
                                                JSONObject jsonObject;
                                                try {
                                                    jsonObject = new JSONObject(new Gson().toJson(commonJson.data));
                                                    //主播APP前后切换
                                                    if(null!=jsonObject&&jsonObject.getString("cmd").equals(Constant.MSG_CUSTOM_ROOM_PUSH_SWITCH_CHANGED)){
                                                        Logger.d(TAG,"主播APP前后切换:MESSAGE:"+commonJson.data.toString());
                                                        PushMessage pushMessage = new Gson().fromJson(commonJson.data.toString(),PushMessage.class);
                                                        if(null!=mMessageListener) mMessageListener.onRoomPushMessage(pushMessage);
                                                        //其他自定义消息
                                                    }else if(null!=jsonObject){
                                                        if(null!=mMessageListener) mMessageListener.onGroupCustomMessage(groupID, message.getSender(), (new Gson().toJson(commonJson.data)));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                                return false;
                            //纯文本消息
                            case Text:
                                String content= ((TIMTextElem) element).getText();
                                if (content == null || content.length() == 0) {
                                    return true;
                                }
                                if(null!=message.getConversation()&&message.getConversation().getType()==TIMConversationType.Group&&!TextUtils.isEmpty(mGroupID)&&mGroupID.equals(message.getConversation().getPeer())){
                                    TIMUserProfile senderProfile = message.getSenderProfile();
                                    if(null!=senderProfile){
                                        Logger.d(TAG,"房间纯文本聊天信息---:" +"\n"
                                                +"USER_IDENTY:"+senderProfile.getIdentifier()+"\n"
                                                +"USER_NICKNAME:"+senderProfile.getNickName()+"\n"
                                                +"USER_LOCATION:"+senderProfile.getLocation()+"\n"
                                                +"USER_HEAD:"+senderProfile.getFaceUrl()+"\n"
                                                +"USER_SEX:"+senderProfile.getGender()+"\n"
                                                +"USER_VIP:"+senderProfile.getLevel()+"\n"
                                                +"USER_ROLE:"+senderProfile.getRole()+"\n"
                                                +"接收人："+message.getConversation().getPeer()+"\n"
                                                +"发送人："+message.getSender()+"\n"
                                                +"所在群组:"+mGroupID
                                        );
                                        int sex=senderProfile.getGender()==TIMFriendGenderType.Male?0:1;
                                        if(null!=mMessageListener) mMessageListener.onGroupTextMessage(message.getConversation().getPeer(), message.getSender(), senderProfile.getNickName(), senderProfile.getFaceUrl(),senderProfile.getLevel(),sex,senderProfile.getRole(),content);
                                    }
                                }
                                return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void printDebugLog(String format, Object... args) {
        String log;
        try {
            log = String.format(format, args);
            Log.e(TAG, log);
            if (mMessageListener != null) {
                mMessageListener.onDebugLog(log);
            }
        } catch (FormatFlagsConversionMismatchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 辅助类 IM Message Listener
     */
    private class IMMessageCallback implements GroupIMMessageListener {
        private GroupIMMessageListener listener;

        public IMMessageCallback(GroupIMMessageListener listener) {
            this.listener = listener;
        }

        public void setListener(GroupIMMessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void onConnected() {
            if(null!=mHandler)  mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onConnected();
                }
            });
        }

        @Override
        public void onDisconnected() {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onDisconnected();
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupID) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupDestroyed(groupID);
                }
            });
        }

        @Override
        public void onDebugLog(final String line) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onDebugLog("[IM] " + line);
                }
            });
        }

        @Override
        public void onGroupSystemMsg(final String groupID, final String sender, final String message) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupSystemMsg(groupID, sender, message);
                }
            });
        }


        @Override
        public void onGroupTextMessage(String groupID, String senderID, String sendNickname, String sendhead,long sendVipLeve ,long sendGender,long sendUserType,String messageContent) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupTextMessage(groupID, senderID, sendNickname, sendhead, sendVipLeve,sendGender,sendUserType,messageContent);
                }
            });
        }

        @Override
        public void onGroupCustomMessage(final String groupID, final String senderID, final String message) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupCustomMessage(groupID, senderID, message);
                }
            });
        }

        @Override
        public void onC2CCustomMessage(final String senderID, final String message) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onC2CCustomMessage(senderID, message);
                }
            });
        }

        @Override
        public void onGroupSystemMsgNumber(final String groupId, final String sender, final String toJson) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupSystemMsgNumber(groupId, sender,toJson);
                }
            });
        }

        @Override
        public void onRoomPushMessage(final PushMessage pushMessage) {
            if(null!=mHandler) mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onRoomPushMessage(pushMessage);
                }
            });
        }
    }
}