package com.yc.liaolive.msg.model;

import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupPendencyItem;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.msg.manager.GroupManagerPresenter;
import com.yc.liaolive.msg.model.bean.Conversation;

/**
 * 群管理会话
 */
public class GroupManageConversation extends Conversation {

    private final String TAG = "GroupManageConversation";

    private TIMGroupPendencyItem lastMessage;

    private long unreadCount;


    public GroupManageConversation(TIMGroupPendencyItem message){
        lastMessage = message;
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        return lastMessage.getAddTime();
    }

    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        return unreadCount;
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {
        //不能传入最后一条消息时间，由于消息时间戳的单位是秒
        GroupManagerPresenter.readGroupManageMessage(Calendar.getInstance().getTimeInMillis(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "read all message error,code " + i);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "read all message succeed");
            }
        });
    }

    /**
     * 获取头像
     * R.drawable.ic_news
     */
    @Override
    public String getAvatar() {
        return "";
    }

    @Override
    public boolean isLive() {
        return false;
    }


    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        readAllMessage();
//        Intent intent = new Intent(context, GroupManageMessageActivity.class);
//        context.startActivity(intent);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (lastMessage == null) return "";
        String from = lastMessage.getFromUser();
        String to = lastMessage.getToUser();

        boolean isSelf = from.equals(UserManager.getInstance().getUserId());
        switch (lastMessage.getPendencyType()){
            case INVITED_BY_OTHER:
                if (isSelf){
                    return AppEngine.getApplication().getResources().getString(R.string.summary_me)+
                            AppEngine.getApplication().getResources().getString(R.string.summary_group_invite)+
                            to+
                            AppEngine.getApplication().getResources().getString(R.string.summary_group_add);
                }else{
                    if (to.equals(UserManager.getInstance().getUserId())){
                        return from+
                                AppEngine.getApplication().getResources().getString(R.string.summary_group_invite)+
                                AppEngine.getApplication().getResources().getString(R.string.summary_me)+
                                AppEngine.getApplication().getResources().getString(R.string.summary_group_add);
                    }else{
                        return from+
                                AppEngine.getApplication().getResources().getString(R.string.summary_group_invite)+
                                to+
                                AppEngine.getApplication().getResources().getString(R.string.summary_group_add);
                    }

                }
            case APPLY_BY_SELF:
                if (isSelf){
                    return AppEngine.getApplication().getResources().getString(R.string.summary_me)+
                            AppEngine.getApplication().getResources().getString(R.string.summary_group_apply)+
                            GroupInfo.getInstance().getGroupName(lastMessage.getGroupId());
                }else{
                    return from+AppEngine.getApplication().getResources().getString(R.string.summary_group_apply)+GroupInfo.getInstance().getGroupName(lastMessage.getGroupId());
                }

            default:
                return "";
        }
    }

    /**
     * 获取名称
     */
    @Override
    public String getName() {
        return AppEngine.getApplication().getString(R.string.conversation_system_group);
    }



    /**
     * 设置最后一条消息
     */
    public void setLastMessage(TIMGroupPendencyItem message){
        lastMessage = message;
    }


    /**
     * 设置未读数量
     *
     * @param count 未读数量
     */
    public void setUnreadCount(long count){
        unreadCount = count;
    }


}
