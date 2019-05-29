package com.yc.liaolive.msg.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tencent.TIMUserProfile;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.msg.model.FriendProfile;
import com.yc.liaolive.msg.model.bean.FriendInfo;

import java.util.List;

/**
 * Created by wanglin  on 2018/8/17 14:20.
 * 用于保存好友昵称图像的管理类
 */
public class FriendManager {

    private volatile static FriendManager instance;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;

    private FriendManager() {
        this.sp = AppEngine.getApplication().getSharedPreferences(AppEngine.getApplication().getPackageName() + "friend_ship", Context.MODE_PRIVATE);
        this.editor = sp.edit();
    }

    public static FriendManager getInstance() {
        synchronized (FriendManager.class) {
            if (instance == null) {
                synchronized (FriendManager.class) {
                    instance = new FriendManager();
                }
            }
        }
        return instance;
    }


    public void saveFriendShip(String key, FriendInfo friendItemList) {
        if (friendItemList == null) return;
//        LogUtil.msg("map:: saveFriendShip  " + key + "--" + friendItemList.toString());
        editor.putString(key, new Gson().toJson(friendItemList));
        editor.commit();
    }

    public FriendProfile getFriendShipsById(String id) {

        String friendResult = sp.getString(id, "");
        if (!TextUtils.isEmpty(friendResult)) {
            FriendInfo friendInfo = new Gson().fromJson(friendResult, FriendInfo.class);

            com.tencent.imcore.FriendProfile friendProfile = new com.tencent.imcore.FriendProfile();
            friendProfile.setSFaceURL(friendInfo.getAvatarUrl().getBytes());
            friendProfile.setSIdentifier(friendInfo.getIdentify());
            friendProfile.setSNickname(friendInfo.getName().getBytes());
            friendProfile.setSRemark(friendInfo.getRemark().getBytes());
            TIMUserProfile profile = new TIMUserProfile(friendProfile);

            FriendProfile friendshipInfo = new FriendProfile(profile);
            friendshipInfo.setIsSelected(friendInfo.isSelected());


            return friendshipInfo;
        }
        return null;
    }


    /**
     * 删除指定账号的聊天用户信息
     *
     * @param key
     */
    public void removeFriendShip(String key) {
//        LogUtil.msg("map::  " + getFriendShipsById(key).getIdentify() + "--" + getFriendShipsById(key).getTitle());
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清空所有的用户关系
     *
     * @param keys
     */
    public void clearFriendShips(List<String> keys) {
        if (keys == null) return;
        for (String key : keys) {
            editor.remove(key);
            editor.commit();
        }
    }
}
