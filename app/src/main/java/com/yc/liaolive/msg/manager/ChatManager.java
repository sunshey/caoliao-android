package com.yc.liaolive.msg.manager;

import com.tencent.TIMConversationType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.yc.liaolive.util.Logger;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/19
 * 聊天好友关系辅助
 */

public class ChatManager {

    private static final String TAG = "ChatManager";

    public static ChatManager mInstance;

    private List<String> blackList;//黑名单列表

    public static synchronized ChatManager getInstance() {
        synchronized (ChatManager.class){
            if(null==mInstance){
                mInstance=new ChatManager();
            }
        }
        return mInstance;
    }

    /**
     * 更新最新的黑名单列表
     */
    public void uploadBlackList(){
        TIMFriendshipManager.getInstance().getBlackList(new TIMValueCallBack<List<String>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<String> strings) {
                blackList=strings;
            }
        });
    }

    /**
     * 获取黑名单列表
     * @return
     */
    public List<String> getBlackList(){
        if(null!=blackList) {
            return blackList;
        }
        TIMFriendshipManager.getInstance().getBlackList(new TIMValueCallBack<List<String>>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(List<String> strings) {
                blackList=strings;
            }
        });
       return null;
    }

    /**
     * 将用户从黑名单踢除
     * @param identifiers
     * @param cb
     */
    public void delBlackList(List<String> identifiers, TIMValueCallBack<List<TIMFriendResult>> cb){
        TIMFriendshipManager.getInstance().delBlackList(identifiers,cb);
    }

    /**
     * 添加用户至黑名单中
     * @param identifiers
     * @param cb
     */
    public void addBlackList(List<String> identifiers, TIMValueCallBack<List<TIMFriendResult>> cb){
        TIMFriendshipManager.getInstance().addBlackList(identifiers,cb);
    }

    /**
     * 删除本地消息
     * @param identify
     */
    public boolean removeMessageByIdentify(String identify) {
        return TIMManager.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, identify);
    }
}
