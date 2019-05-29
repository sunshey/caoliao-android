package com.yc.liaolive.msg.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.UnReadMsg;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityConversationListBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.msg.adapter.ConversationAdapter;
import com.yc.liaolive.msg.iView.FriendInfoView;
import com.yc.liaolive.msg.iView.FriendshipMessageView;
import com.yc.liaolive.msg.manager.ChatManager;
import com.yc.liaolive.msg.manager.FriendManager;
import com.yc.liaolive.msg.model.FriendshipConversation;
import com.yc.liaolive.msg.model.MessageFactory;
import com.yc.liaolive.msg.model.NomalConversation;
import com.yc.liaolive.msg.model.bean.Conversation;
import com.yc.liaolive.msg.view.ListViewFooterView;
import com.yc.liaolive.ui.contract.ConversationView;
import com.yc.liaolive.ui.contract.GroupManageMessageView;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.ConversationPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.AttachFirendActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/21
 * 私信会话Activity
 */

public class ChatConversationActivity extends TopBaseActivity implements ConversationView, FriendshipMessageView, GroupManageMessageView, FriendInfoView {

    private ActivityConversationListBinding bindingView;
    private static final String TAG = "ChatConversationActivity";
    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationAdapter adapter;
    private ConversationPresenter presenter;
    private FriendshipConversation friendshipConversation;

    public static void start(Context context) {
        Intent intent=new Intent(AppEngine.getApplication().getApplicationContext(),ChatConversationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_conversation_list);
        setActivityLayoutParams();
        setFinishOnTouchOutside(true);//允许点击外部关闭Activity
        initViews();
    }

    private void initViews() {

        ViewGroup.LayoutParams layoutParams = bindingView.listView.getLayoutParams();
        layoutParams.height= ScreenUtils.getScreenHeight()/3*2;
        layoutParams.width=ScreenUtils.getScreenWidth();
        bindingView.listView.setLayoutParams(layoutParams);

        bindingView.titleView.setStatusBarHeight(0);
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }

            /**
             * 清空聊天记录
             * @param v
             */
            @Override
            public void onMenuClick1(View v) {
                super.onMenuClick1(v);
                if (null != conversationList && conversationList.size() == 0) {
                    ToastUtils.showCenterToast("没有可删除的聊天记录....");
                    return;
                }
                QuireDialog.getInstance(ChatConversationActivity.this).setTitleText("清空聊天记录提示").setContentText("清空所有聊天记录后将无法恢复，确定继续吗？").setSubmitTitleText("确定").setCancelTitleText("取消").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        if (null != conversationList) {
                            Iterator<Conversation> iterator = conversationList.iterator();
                            while (iterator.hasNext()) {
                                Conversation conversation = iterator.next();
                                boolean messageByIdentify = ChatManager.getInstance().removeMessageByIdentify(conversation.getIdentify());
                                FriendManager.getInstance().removeFriendShip(conversation.getIdentify());
                                iterator.remove();
                            }
                            if (null != adapter) adapter.notifyDataSetChanged();
                        }
                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_UPDATA_MESSAGE);
                    }

                    @Override
                    public void onRefuse() {

                    }
                }).show();
            }

            /**
             * 用户的好友列表
             * @param v
             */
            @Override
            public void onMenuClick2(View v) {
                super.onMenuClick2(v);
                AttachFirendActivity.start(ChatConversationActivity.this, 2, UserManager.getInstance().getUserId());
            }
        });
        adapter = new ConversationAdapter(ChatConversationActivity.this, R.layout.list_item_conversation, conversationList);
        adapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener() {
            //条目点击
            @Override
            public void onItemClick(int position) {
                try {
                    if (null != conversationList && conversationList.size() > position) {
                        Conversation conversation = conversationList.get(position);
                        if(null!=conversation){
                            if(conversation.getUnreadNum()>0){
                                conversation.readAllMessage();//已阅读当前会话所有消息
                                refresh();
                            }
                            conversation.navToDetail(ChatConversationActivity.this);
                        }
                    }
                } catch (RuntimeException e) {

                }
            }

            //侧滑删除
            @Override
            public void onItemDetele(int position) {
                try {
                    if (null != conversationList && null != presenter) {
                        NomalConversation conversation = (NomalConversation) conversationList.get(position);
                        if (conversation != null) {
                            if (presenter.delConversation(conversation.getType(), conversation.getIdentify())) {
                                conversationList.remove(conversation);
                                if (null != adapter) adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (RuntimeException e) {

                }
            }
        });
        bindingView.listView.setAdapter(adapter);
        presenter = new ConversationPresenter(this, this);
        //设置占位图层
        DataChangeView dataChangeView = new DataChangeView(this);
        dataChangeView.setHeight(ScreenUtils.getScreenHeight()/3*2);
        ((ViewGroup) bindingView.listView.getParent()).addView(dataChangeView);
        dataChangeView.showEmptyView("没有聊天记录", R.drawable.iv_message_empty);
        bindingView.listView.setEmptyView(dataChangeView);
        //添加一个占位的尾部
        ListViewFooterView msgFooterView = new ListViewFooterView(this);
        msgFooterView.setContent("没有更多了");
        bindingView.listView.addFooterView(msgFooterView);
        adapter.notifyDataSetChanged();
        getConversationMessages();
    }

    /**
     * 获取所有会话列表
     */
    @Override
    public void getConversationMessages() {
        if(null!=presenter&&!presenter.isGetConversation()){
            if(null!=bindingView) bindingView.titleView.startLoadingView();
            presenter.getConversation();
        }
    }

    /**
     * 所有会话列表回调
     * @param conversationList 所有的会话列表
     */
    @Override
    public void showConversations(List<TIMConversation> conversationList) {
        if(null!=conversationList){
            boolean isContain=false;
            for (TIMConversation conversation : conversationList) {
                if(conversation.getType()== TIMConversationType.C2C){
                    isContain=true;
                    break;
                }
            }
            if(!isContain&&!ChatConversationActivity.this.isFinishing()&&null!=bindingView){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=bindingView) bindingView.titleView.stopLoadingView();
                    }
                });
            }
        }else{
            if(!ChatConversationActivity.this.isFinishing()&&null!=bindingView){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=bindingView) bindingView.titleView.stopLoadingView();
                    }
                });
            }
        }
    }

    /**
     * 所有会话消息回调
     * @param messageList 所有过滤系统消息、群组消息、客服消息的会话列表
     */
    @Override
    public void showConversationMessages(List<TIMMessage> messageList) {
        if(null!=bindingView) bindingView.titleView.stopLoadingView();
        if(null!=messageList&&null!=conversationList){
            for (TIMMessage message : messageList) {
                NomalConversation conversation = new NomalConversation(message.getConversation());
                Iterator<Conversation> iterator = conversationList.iterator();
                while (iterator.hasNext()) {
                    Conversation c = iterator.next();
                    if (conversation.equals(c)) {
                        conversation = (NomalConversation) c;
                        iterator.remove();
                        break;
                    }
                }
                conversation.setLastMessage(MessageFactory.getMessage(message));
                conversationList.add(conversation);
            }
            refresh();
        }
    }

    /**
     * 新的会话或会话中新的消息
     * @param message 新消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
        if(null==message) return;
        if(null!=bindingView) bindingView.titleView.stopLoadingView();
        if(null==adapter) return;
        if (message.status() == TIMMessageStatus.HasDeleted) {
            adapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System) return;
        if (message.getConversation().getType() == TIMConversationType.Group) return;
        if(null==conversationList) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation c = iterator.next();
            if (conversation.equals(c)) {
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        refresh();
    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
        if(null!=presenter) presenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation conversation = iterator.next();
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(identify)) {
                iterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 更新群信息
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        for (Conversation conversation : conversationList) {
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(info.getGroupInfo().getGroupId())) {
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        Collections.sort(conversationList);
        if(null!=adapter) adapter.notifyDataSetChanged();
        getTotalUnreadNum();
    }

    /**
     * 获取好友关系链管理系统最后一条消息的回调
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
        if (friendshipConversation == null) {
            friendshipConversation = new FriendshipConversation(message);
            conversationList.add(friendshipConversation);
        } else {
            friendshipConversation.setLastMessage(message);
        }
        friendshipConversation.setUnreadCount(unreadCount);
        refresh();
    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {}

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetGroupManageLastMessage(TIMGroupPendencyItem message, long unreadCount) {}

    /**
     * 获取群管理系统消息的回调
     * @param message 分页的消息列表
     */
    @Override
    public void onGetGroupManageMessage(List<TIMGroupPendencyItem> message) {}

    private int getTotalUnreadNum() {
        if(null!=conversationList){
            int num = 0;
            VideoApplication.getInstance().getUnReadMsgMap().clear();
            for (int i = 0; i < conversationList.size(); i++) {
                Conversation conversation = conversationList.get(i);
                UnReadMsg unReadMsg = new UnReadMsg();
                unReadMsg.setIdentify(conversation.getIdentify());
                unReadMsg.setCount(conversation.getUnreadNum());
                VideoApplication.getInstance().getUnReadMsgMap().put(conversation.getIdentify(), unReadMsg);
                //总消息计数
                num += conversation.getUnreadNum();
            }
            VideoApplication.getInstance().setMsgCount(num);
            return num;
        }
        return 0;
    }

    @Override
    public void showUserInfo(List<TIMUserProfile> users) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bindingView) bindingView.titleView.onDestroy();
        if(null!=presenter) {
            presenter.onDestroy();
            presenter=null;
        }
        if(null!=conversationList){
            conversationList.clear();
        }
    }
}