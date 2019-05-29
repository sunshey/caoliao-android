package com.yc.liaolive.msg.model;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.CustomMsgCall;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.msg.manager.FriendManager;
import com.yc.liaolive.msg.model.bean.ChatCommentMessage;
import com.yc.liaolive.msg.model.bean.ChatGiftMessage;
import com.yc.liaolive.msg.model.bean.Conversation;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.user.manager.UserManager;

/**
 * 好友或群聊的会话
 */
public class NomalConversation extends Conversation {


    private TIMConversation conversation;


    //最后一条消息
    private Message lastMessage;

    public NomalConversation(TIMConversation conversation) {
        this.conversation = conversation;
        type = conversation.getType();
        identify = conversation.getPeer();

    }


    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String getAvatar() {
        switch (type) {
            case C2C:
                FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
                if (profile == null) {
                    profile = FriendManager.getInstance().getFriendShipsById(identify);
                }
                avatar = profile == null ? "" : profile.getAvatarUrl();

                return avatar;
            case Group:
                return "";
        }
        return "";
    }

    @Override
    public boolean isLive() {
        if (lastMessage == null) return false;
        if (lastMessage instanceof CustomMessage) {
            CustomMessage customMessage = (CustomMessage) lastMessage;
            return customMessage.getType() == CustomMessage.Type.LIVE;
        }
        return false;
    }


    /**
     * 跳转到聊天界面或会话详情
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        try {
            if (TextUtils.equals(UserManager.getInstance().getServerIdentify(), identify)) {
                ChatActivity.navToChat(context, identify, true, type);
                return;
            }
            ChatActivity.navToChat(context, identify, type);
        } catch (RuntimeException e) {

        } catch (Exception e) {

        }
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (conversation.hasDraft()) {
            TextMessage textMessage = new TextMessage(conversation.getDraft());

            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()) {
                return AppEngine.getApplication().getString(R.string.conversation_draft) + textMessage.getSummary();
            } else {
                return lastMessage.getSummary();
            }
        } else {
            if (lastMessage == null) return "";
            if (lastMessage instanceof CustomMessage) {
                CustomMessage customMessage = (CustomMessage) lastMessage;
                //未知
                if (customMessage.getType() == CustomMessage.Type.INVALID) {
                    return "[新消息]";
                }
                //视频通话
                if (customMessage.getType() == CustomMessage.Type.CALL) {
                    ChatCommentMessage commentMessage = customMessage.getCommentMessage();
                    if(null!=commentMessage){
                        return commentMessage.getContent();
                    }
                    return "[视频通话]";
                }
                //礼物
                if (customMessage.getType() == CustomMessage.Type.CHAT_GIFT) {
                    ChatGiftMessage chatGiftMessage = customMessage.getChatGiftMessage();
                    if(null!=chatGiftMessage){
                        String content;
                        if(customMessage.isSelf()) {
                            content = "送出" + chatGiftMessage.getCount() + "个" + chatGiftMessage.getName();
                        } else {
                            content = "收到" + chatGiftMessage.getCount() + "个" + chatGiftMessage.getName();
                        }
                        return content;
                    }
                    return "[礼物消息]";
                }
                //中奖
                if (customMessage.getType() == CustomMessage.Type.AWARD) {
                    return "[礼物中奖啦]";
                }
                //直播
                if (customMessage.getType() == CustomMessage.Type.LIVE) {
                    if(customMessage.isSelf()){
                        return "[发出一个直播邀请]";
                    }
                    return "[收到一个直播邀请]";
                }
                //私有多媒体文件
                if (customMessage.getType() == CustomMessage.Type.MEDIA) {
                    PrivateMedia privateMedia = customMessage.getPrivateMedia();
                    if(null!=privateMedia){
                        //纯文字
                        if(0==privateMedia.getAnnex_type()){
                            return privateMedia.getContent();
                        }
                        //照片
                        if(0==privateMedia.getFile_type()){
                            return customMessage.isSelf()?"[发送一张私密照片]":"[收到一张私密照片]";
                        }
                        if(1==privateMedia.getFile_type()){
                            return customMessage.isSelf()?"[发送一部私密视频]":"[收到一部私密视频]";
                        }
                    }
                    return "[收到一张私密照片]";
                }
                //通话结算
                if(customMessage.getType() == CustomMessage.Type.CALL_CUSTOM){
                    ChatCommentMessage commentMessage = customMessage.getCommentMessage();
                    if(null!=commentMessage){
                        return commentMessage.getContent();
                    }
                    return "[通话结算信息]";
                }
                //小视频推送 视频唤醒
                if(customMessage.getType()==CustomMessage.Type.CALL_WAKEUP){
                    CustomMsgCall customMsgCall = customMessage.getCustomMsgCall();
                    if(null!=customMsgCall){
                        return "["+customMsgCall.getDesc()+"]";
                    }
                    return "[发来一个视频邀请]";
                }
                //语音消息
                if(customMessage.getType()==CustomMessage.Type.CHAT_VOICE){
                    if(customMessage.isSelf()){
                        return "[发出一条语音消息]";
                    }
                    return "[收到一条语音消息]";
                }
                //对方正在输入消息
                if(customMessage.getType()==CustomMessage.Type.INPUTING){
                    ChatCommentMessage commentMessage = customMessage.getCommentMessage();
                    if(null!=commentMessage){
                        return "";//commentMessage.getContent()
                    }
                    return "";
                }
            }
            return lastMessage.getSummary();
        }
    }


    /**
     * 获取名称
     */
    @Override
    public String getName() {
        if (type == TIMConversationType.Group) {
            name = GroupInfo.getInstance().getGroupName(identify);
            if (name.equals("")) name = identify;
        } else {
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            if (profile == null) {
                profile = FriendManager.getInstance().getFriendShipsById(identify);
            }
            name = profile == null ? identify : profile.getName();
        }
        return name;
    }


    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        if (conversation == null) return 0;
        if (lastMessage instanceof CustomMessage) {
            CustomMessage customMessage = (CustomMessage) lastMessage;
            if (customMessage.getType() == CustomMessage.Type.INVALID
                    || customMessage.getType() == CustomMessage.Type.AV_GROUP) {
                return 0;
            }
        }
        return conversation.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {
        if (conversation != null) {
            conversation.setReadMessage();
        }
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (conversation.hasDraft()) {
            if (lastMessage == null || lastMessage.getMessage().timestamp() < conversation.getDraft().getTimestamp()) {
                return conversation.getDraft().getTimestamp();
            } else {
                return lastMessage.getMessage().timestamp();
            }
        }
        if (lastMessage == null) return 0;
        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    public TIMConversationType getType() {
        return conversation.getType();
    }
}
