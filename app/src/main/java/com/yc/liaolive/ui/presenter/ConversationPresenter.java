package com.yc.liaolive.ui.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMValueCallBack;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.msg.iView.FriendInfoView;
import com.yc.liaolive.msg.manager.FriendshipManagerPresenter;
import com.yc.liaolive.msg.model.Message;
import com.yc.liaolive.msg.model.MessageFactory;
import com.yc.liaolive.msg.model.NomalConversation;
import com.yc.liaolive.msg.model.bean.RefreshServer;
import com.yc.liaolive.observer.FriendshipEvent;
import com.yc.liaolive.observer.GroupEvent;
import com.yc.liaolive.observer.MessageEvent;
import com.yc.liaolive.observer.RefreshEvent;
import com.yc.liaolive.ui.contract.ConversationView;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.videocall.manager.VideoCallManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 会话界面逻辑
 */
public class ConversationPresenter implements Observer {

    private static final String TAG = "ConversationPresenter";
    private ConversationView view;
    private List<String> messagePeerList;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private final Handler mHandler;
    private UpdataMessages mRunnable;

    public ConversationPresenter(ConversationView view, FriendInfoView infoView) {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册好友关系链监听
        FriendshipEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
        friendshipManagerPresenter = new FriendshipManagerPresenter(infoView);
        messagePeerList = new ArrayList<>();
        this.view = view;
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 新的消息下发
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if(UserManager.getInstance().isChatAvailable()){
                try {
                    if(null!=data&&data instanceof TIMMessage){
                        TIMMessage message = (TIMMessage) data;
                        if(null!=message.getConversation()){
                            if(message.getConversation().getType()== TIMConversationType.System||message.getConversation().getType() == TIMConversationType.Group){
                                return;
                            }
                            //拦截客服会话单独处理
                            if(TextUtils.equals(message.getConversation().getPeer(), UserManager.getInstance().getServerIdentify())){
                                NomalConversation conversation = new NomalConversation(message.getConversation());
                                Message messageF = MessageFactory.getMessage(message);
                                conversation.setLastMessage(messageF);
                                //未读消息处理
                                int count = SharedPreferencesUtil.getInstance().getInt(Constant.KET_SERVER_MSG_COUNT, 0);
                                count++;
                                SharedPreferencesUtil.getInstance().putInt(Constant.KET_SERVER_MSG_COUNT, count);
                                if(null!=messageF.getMessage()){
                                    SharedPreferencesUtil.getInstance().putLong(Constant.KET_SERVER_MSG_TIME,messageF.getMessage().timestamp());
                                }
                                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_MSG_UNREAD_COUND);
                                //同步更新主页消息数量
                                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_MSG_UNREAD_COUND_MAIN);
                                return;
                            }
                            if(message.getElementCount()>0){
                                try {
                                    messagePeerList.add(message.getConversation().getPeer());
                                    getUserInfo(messagePeerList);
                                    if(null!=view) view.updateMessage(message);
                                }catch (RuntimeException e){

                                }finally {
                                    //视频通话可能关心的消息
                                    VideoCallManager.getInstance().onNewMessage(message);
                                }
                            }
                        }
                    }
                }catch (RuntimeException e){

                }
            }
        } else if (observable instanceof FriendshipEvent) {
            FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
            switch (cmd.type) {
                case ADD_REQ:
                case READ_MSG:
                case ADD:
                    if(null!=view) view.updateFriendshipMessage();
                    break;
            }
        } else if (observable instanceof GroupEvent) {
            //这里做个判断,直播的群消息不同步至主页
            GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
            switch (cmd.type) {
                case UPDATE:
                case ADD:
                    if(null!=view) view.updateGroupInfo((TIMGroupCacheInfo) cmd.data);
                    break;
                case DEL:
                    if(null!=view) view.removeConversation((String) cmd.data);
                    break;
            }
        } else if (observable instanceof RefreshEvent) {
            if(null!=view) view.refresh();
            ApplicationManager.getInstance().observerUpdata(new RefreshServer());
        }
    }

    private boolean isGetConversation=false;

    public boolean isGetConversation() {
        return isGetConversation;
    }

    /**
     * 获取本地的会话历史记录，不允许短时间内重复调用
     */
    public void getConversation() {
        if(UserManager.getInstance().isChatAvailable()){
            if(isGetConversation) return;
            isGetConversation=true;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    //会话列表
                    List<TIMConversation> list = TIMManager.getInstance().getConversionList();
                    //回调到UI处理的会话列表
                    final List<TIMConversation> result = new ArrayList<>();
                    //最终过滤完成的会话列表
                    List<TIMMessage> timMessageLists=new ArrayList<>();
                    if(null!= messagePeerList) messagePeerList.clear();
                    if(null!=list&&list.size()>0){
                        for (final TIMConversation conversation : list) {
                            //过滤群组消息和系统消息
                            if (conversation.getType() == TIMConversationType.System || conversation.getType() == TIMConversationType.Group)
                                continue;
                            if(null!= messagePeerList) messagePeerList.add(conversation.getPeer());
                            conversation.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
                                @Override
                                public void onError(int i, String s) {
                                }

                                @Override
                                public void onSuccess(List<TIMMessage> timMessages) {
                                    if(null!=timMessages&&timMessages.size()>0){
                                        try {
                                            TIMMessage message = timMessages.get(0);
                                            if(null!=message&&null!=message.getConversation()){
                                                //拦截客服会话单独处理
                                                if(TextUtils.equals(message.getConversation().getPeer(), UserManager.getInstance().getServerIdentify())){
                                                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CMD_MSG_UNREAD_COUND);
                                                }else{
                                                    if(message.getElementCount()>0){
                                                        result.add(conversation);
                                                        if(message.status() != TIMMessageStatus.HasDeleted&&message.getConversation().getType()!= TIMConversationType.System){
                                                            timMessageLists.add(message);
                                                            //if(null!=view) view.updateMessage(message);
                                                            updataMessage(timMessageLists);
                                                        }
                                                    }
                                                }
                                            }
                                        }catch (RuntimeException e){

                                        }
                                    }
                                }
                            });
                        }
                        getUserInfo(messagePeerList);
                        if(null!=view) view.showConversations(list);
                    }else{
                        updataMessage(null);
                    }
                    isGetConversation=false;
                }
            }.start();
        }else{
            if(null!=view) view.showConversations(null);
        }
    }

    private int mCount=0;
    private void updataMessage(List<TIMMessage> messageList) {
        mCount++;
        if(null!=mHandler){
            if(null==mRunnable) {
                mRunnable = new UpdataMessages();
            }
            mHandler.removeCallbacks(mRunnable);
            mRunnable.setMessages(messageList);
            mHandler.postDelayed(mRunnable,1000);
        }
    }

    public void onDestroy() {
        if(null!=mHandler&&null!=mRunnable) mHandler.removeCallbacks(mRunnable);
        view=null;
    }

    /**
     * 消息界面刷新
     */
    private class UpdataMessages implements Runnable {

        private List<TIMMessage> mMessageList;
        @Override
        public void run() {
            if(null!=view) view.showConversationMessages(mMessageList);
        }
        public void setMessages(List<TIMMessage> messageList) {
            this.mMessageList=messageList;
        }
    }


    private void getUserInfo(List<String> users) {
        friendshipManagerPresenter.getUserInfo(users);
    }

    /**
     * 删除会话
     *
     * @param type 会话类型
     * @param id   会话对象id
     */
    public boolean delConversation(TIMConversationType type, String id) {
        return TIMManager.getInstance().deleteConversationAndLocalMsgs(type, id);
    }


    public void getFriendshipLastMessage() {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }
}
