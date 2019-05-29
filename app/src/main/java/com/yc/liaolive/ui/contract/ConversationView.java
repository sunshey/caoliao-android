package com.yc.liaolive.ui.contract;

import com.tencent.TIMConversation;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;
import java.util.List;

/**
 * 会话列表界面的接口
 */
public interface ConversationView extends ChatMessageView {

    /**
     * 获取所有会话列表
     */
    void getConversationMessages();

    /**
     * 会话列表
     */
    void showConversations(List<TIMConversation> conversationList);

    /**
     * 本地消息一次性刷新
     * @param messageList
     */
    void showConversationMessages(List<TIMMessage> messageList);

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    void updateMessage(TIMMessage message);

    /**
     * 更新好友关系链消息
     */
    void updateFriendshipMessage();

    /**
     * 删除会话
     */
    void removeConversation(String identify);

    /**
     * 更新群信息
     */
    void updateGroupInfo(TIMGroupCacheInfo info);

    /**
     * 刷新
     */
    void refresh();
}
