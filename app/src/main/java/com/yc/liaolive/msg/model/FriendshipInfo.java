package com.yc.liaolive.msg.model;


import android.util.Log;

import com.tencent.TIMFriendGroup;
import com.tencent.TIMFriendshipProxy;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;


import com.yc.liaolive.msg.model.bean.FriendInfo;
import com.yc.liaolive.observer.FriendshipEvent;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.msg.manager.FriendManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 好友列表缓存数据结构
 */
public class FriendshipInfo extends Observable implements Observer {

    private final String TAG = "FriendshipInfo";


    private List<String> groups;
    private Map<String, List<FriendProfile>> friends;

    private static FriendshipInfo instance;

    private FriendshipInfo() {
        groups = new ArrayList<>();
        friends = new HashMap<>();
        FriendshipEvent.getInstance().addObserver(this);
        refresh();
    }

    public synchronized static FriendshipInfo getInstance() {
        if (instance == null) {
            instance = new FriendshipInfo();
        }
        return instance;
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        TIMManager.getInstance().getEnv();
        if (observable instanceof FriendshipEvent) {
            if (data instanceof FriendshipEvent.NotifyCmd) {
                FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
                Log.d(TAG, "get notify type:" + cmd.type);
                switch (cmd.type) {
                    case REFRESH:
                    case DEL:
                    case ADD:
                    case PROFILE_UPDATE:
                    case ADD_REQ:
                    case GROUP_UPDATE:
                        refresh();
                        break;

                }
            }
        }
    }

    /**
     * 更新用户信息
     * @param users
     */
    public void updateFriendShips(List<TIMUserProfile> users) {
        if (users == null) return;
        for (TIMUserProfile userProfile : users) {
            groups.add(userProfile.getIdentifier());
            List<FriendProfile> friendItemList = new ArrayList<>();
            FriendProfile friendProfile = new FriendProfile(userProfile);
            friendItemList.add(friendProfile);
            friends.put(userProfile.getIdentifier(), friendItemList);
            FriendInfo friendInfo = new FriendInfo(friendProfile.getAvatarRes(), friendProfile.getAvatarUrl(), friendProfile.getIdentify(), friendProfile.getName(), friendProfile.getRemark(), friendProfile.isSelected());
            FriendManager.getInstance().saveFriendShip(userProfile.getIdentifier(), friendInfo);
        }
        setChanged();
        notifyObservers();
    }


    private void refresh() {
        groups.clear();
        friends.clear();
        Log.d(TAG, "get friendship info id :" + UserManager.getInstance().getUserId());

        List<TIMFriendGroup> timFriendGroups = TIMFriendshipProxy.getInstance().getFriendsByGroups(null);
        if (timFriendGroups == null) return;
        for (TIMFriendGroup group : timFriendGroups) {
            groups.add(group.getGroupName());
            List<FriendProfile> friendItemList = new ArrayList<>();
            for (TIMUserProfile profile : group.getProfiles()) {
                friendItemList.add(new FriendProfile(profile));
            }
            friends.put(group.getGroupName(), friendItemList);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * 获取分组列表
     */
    public List<String> getGroups() {
        return groups;
    }

    public String[] getGroupsArray() {
        return groups.toArray(new String[groups.size()]);
    }


    /**
     * 获取好友列表摘要
     */
    public Map<String, List<FriendProfile>> getFriends() {
        return friends;
    }

    /**
     * 判断是否是好友
     *
     * @param identify 需判断的identify
     */
    public boolean isFriend(String identify) {
        for (String key : friends.keySet()) {
            for (FriendProfile profile : friends.get(key)) {
                if (identify.equals(profile.getIdentify())) return true;
            }
        }
        return false;
    }


    /**
     * 获取好友资料
     *
     * @param identify 好友id
     */
    public FriendProfile getProfile(String identify) {
        for (String key : friends.keySet()) {
            for (FriendProfile profile : friends.get(key)) {
                if (identify.equals(profile.getIdentify())) return profile;
            }
        }
        return null;
    }


    /**
     * 清除数据
     */
    public void clear() {
        if (instance == null) return;
        groups.clear();
        friends.clear();
        instance = null;
    }


}
