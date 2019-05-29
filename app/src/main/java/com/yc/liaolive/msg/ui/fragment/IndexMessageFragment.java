package com.yc.liaolive.msg.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.UnReadMsg;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentIndexMessageBinding;
import com.yc.liaolive.index.ui.MainActivity;
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
import com.yc.liaolive.msg.view.IndexMsgHeaderView;
import com.yc.liaolive.observer.SubjectObservable;
import com.yc.liaolive.ui.contract.ConversationView;
import com.yc.liaolive.ui.contract.GroupManageMessageView;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.ui.presenter.ConversationPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.AttachFirendActivity;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * TinyHung@Outlook.com
 * 2018/5/24
 * 主页-消息
 */

public class IndexMessageFragment extends BaseFragment<FragmentIndexMessageBinding, RxBasePresenter> implements ConversationView, FriendshipMessageView, GroupManageMessageView, Observer, FriendInfoView {

    private static final String TAG = "IndexMessageFragment";
    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationAdapter mAdapter;
    private ConversationPresenter presenter;
    private FriendshipConversation friendshipConversation;
    private boolean isRefresh = true;
    private IndexMsgHeaderView mHeaderView;

    @Override
    protected void initViews() {
        mAdapter = new ConversationAdapter(getActivity(), R.layout.list_item_conversation, conversationList);
        //添加头部View
        mHeaderView = new IndexMsgHeaderView(getActivity());
        mHeaderView.setOnRefereshListener(new IndexMsgHeaderView.OnRefereshListener() {
            @Override
            public void onRereshFinish() {
                if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
            }
        });
        //条目事件监听
        mAdapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener() {
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
                            conversation.navToDetail(getActivity());
                        }
                    }
                } catch (RuntimeException e) {

                }
            }

            @Override
            public void onItemDetele(int position) {
                if(position<0) return;
                try {
                    NomalConversation conversation = (NomalConversation) conversationList.get(position);
                    if (null != conversation && null != presenter) {
                        if (presenter.delConversation(conversation.getType(), conversation.getIdentify())) {
                            conversationList.remove(conversation);
                            refresh();
                        }
                    }
                } catch (RuntimeException e) {

                }
            }
        });

        bindingView.listView.addHeaderView(mHeaderView);
        bindingView.listView.setAdapter(mAdapter);
        presenter = new ConversationPresenter(this, this);
        //暂时禁用下拉刷新
        bindingView.swiperLayout.setEnabled(false);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mHeaderView) mHeaderView.onResume();
            }
        });
        bindingView.toolBar.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {

            @Override
            public void onTitleClick(View v, boolean doubleClick) {
                if(doubleClick){
                    if(null==conversationList) return;
                    if(conversationList.size() == 0){
                        ToastUtils.showCenterToast("没有可删除的会话记录....");
                    }
                    QuireDialog.getInstance(getActivity()).setTitleText("清空会话记录提示").setContentText("清空所有会话记录后将无法恢复，确定继续吗？").setSubmitTitleText("确定").setCancelTitleText("取消").setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent() {
                            if (null != conversationList && conversationList.size() > 0) {
                                Iterator<Conversation> iterator = conversationList.iterator();
                                while (iterator.hasNext()) {
                                    Conversation conversation = iterator.next();
                                    boolean messageByIdentify = ChatManager.getInstance().removeMessageByIdentify(conversation.getIdentify());
                                    FriendManager.getInstance().removeFriendShip(conversation.getIdentify());
                                    iterator.remove();
                                }
                                refresh();
                            }
                        }

                        @Override
                        public void onRefuse() {}
                    }).show();
                }
            }

            @Override
            public void onMenuClick1(View v) {
                AttachFirendActivity.start(getActivity(), 0, UserManager.getInstance().getUserId());
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_message;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
    }

    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
//        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(true);
//        if(null!=mHeaderView) mHeaderView.onResume();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (isRefresh) {
            fromMainUpdata();
        }
        isRefresh = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(UserManager.getInstance().isAuthenState()){
            if(null!=mHeaderView) mHeaderView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mHeaderView) mHeaderView.onPause();
    }

    /**
     * 获取所有会话列表
     */
    @Override
    public void getConversationMessages() {
        if(null!=presenter&&!presenter.isGetConversation()){
            if(null!=bindingView) bindingView.toolBar.startLoadingView();
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
                if(conversation.getType()==TIMConversationType.C2C){
                    isContain=true;
                    break;
                }
            }
            if(!isContain&&null!=getActivity()&&null!=bindingView){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=bindingView) bindingView.toolBar.stopLoadingView();
                    }
                });
            }
        }else{
            if(null!=getActivity()&&null!=bindingView){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=bindingView) bindingView.toolBar.stopLoadingView();
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
        if(null!=bindingView) bindingView.toolBar.stopLoadingView();
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
        if(null!=bindingView) bindingView.toolBar.stopLoadingView();
        if (message.status() == TIMMessageStatus.HasDeleted) {
            if(null!=mAdapter) mAdapter.notifyDataSetChanged();
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
                ChatManager.getInstance().removeMessageByIdentify(conversation.getIdentify());
                iterator.remove();
                if(null!=mAdapter) mAdapter.notifyDataSetChanged();
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
                if(null!=mAdapter) mAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        //避免滚动过程刷新，出现角标越界
        Collections.sort(conversationList);
        if(null!=mAdapter) mAdapter.notifyDataSetChanged();
        if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).setMsgUnread(getTotalUnreadMessageCount());
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
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {}

    /**
     * 获取群管理最后一条系统消息的回调
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

    /**
     * 长按删除消息
     */
    private void showActionMenu(View view, final int position) {
        if(null!=conversationList){
            try {
                SystemUtils.startVibrator(100);
                final NomalConversation conversation = (NomalConversation) conversationList.get(position);
                if (conversation != null) {
                    View conentView= View.inflate(getActivity(),R.layout.index_delete_msg,null);
                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    conentView.measure(measureSpec, measureSpec);
                    final PopupWindow popupWindow= new PopupWindow(conentView, ScreenUtils.dpToPxInt(200f), ViewGroup.LayoutParams.WRAP_CONTENT, false);
                    popupWindow.setClippingEnabled(false);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));
                    popupWindow.setAnimationStyle(R.style.CenterDialogAnimationStyle);
                    popupWindow.setFocusable(true);
                    conentView.findViewById(R.id.btn_menu_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            try {
                                if (null!=presenter&&presenter.delConversation(conversation.getType(), conversation.getIdentify())) {
                                    conversationList.remove(conversation);
                                    FriendManager.getInstance().removeFriendShip(conversation.getIdentify());
                                    refresh();
                                }
                            }catch (Exception e){
                                ToastUtils.showCenterToast("删除失败!"+e.getMessage());
                            }
                        }
                    });
                    popupWindow.showAtLocation(conentView, Gravity.CENTER,0,0);
                }
            } catch (RuntimeException e) {

            }
        }
    }

    /**
     * 统计未读消息数量
     * @return
     */
    private int getTotalUnreadMessageCount() {
        if(null!=conversationList){
            int num = 0;
            VideoApplication.getInstance().getUnReadMsgMap().clear();
            for (int i = 0; i < conversationList.size(); i++) {
                Conversation conversation = conversationList.get(i);
                //包装未读消息至内存，视频播放界面用到
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

    /**
     * 收到来自其他消息界面通知的刷新事件
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SubjectObservable && null != arg && arg instanceof String ) {
            String cmd= (String) arg;
            //更新新消息
            if(cmd.equals(Constant.OBSERVER_CMD_UPDATA_MESSAGE)){
                getConversationMessages();
            //检查新的未读消息数量
            }else if(cmd.equals(Constant.OBSERVER_CHECKED_NEW_MSG)){
                getTotalUnreadMessageCount();
            //刷新未读消息
            }else if(cmd.equals(Constant.OBSERVER_UPDATA_NEW_MSG)){
                refresh();
            //主页未读消息更新
            }else if(cmd.equals(Constant.OBSERVER_CMD_MSG_UNREAD_COUND_MAIN)){
                if (null!=getActivity()&&getActivity() instanceof MainActivity) ((MainActivity) getActivity()).setMsgUnread(getTotalUnreadMessageCount());
            }
        }
    }

    /**
     * 查询会话列表中的用户信息回调
     * @param users 资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=bindingView) bindingView.toolBar.stopLoadingView();
        if(null!=presenter) presenter.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mHeaderView) mHeaderView.onDestroy();
    }
}