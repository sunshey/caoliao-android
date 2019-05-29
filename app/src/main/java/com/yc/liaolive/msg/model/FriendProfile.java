package com.yc.liaolive.msg.model;

import android.content.Context;

import com.tencent.TIMUserProfile;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;

/**
 * 好友资料
 */
public class FriendProfile implements ProfileSummary {


    private TIMUserProfile profile;
    private boolean isSelected;

    public FriendProfile(TIMUserProfile profile) {
        this.profile = profile;
    }

    /**
     * 获取头像资源
     */
    @Override
    public int getAvatarRes() {
        return R.drawable.ic_default_user_head;
    }

    /**
     * 获取头像地址
     */
    @Override
    public String getAvatarUrl() {
        if (null!=profile&&!profile.getFaceUrl().equals("")) {
            return profile.getFaceUrl();
        }
        return "";
    }

    /**
     * 获取名字
     */
    @Override
    public String getName() {
        if(null!=profile){
            if (!profile.getRemark().equals("")) {
                return profile.getRemark();
            } else if (!profile.getNickName().equals("")) {
                return profile.getNickName();
            }
            return profile.getIdentifier();
        }
        return "";
    }

    /**
     * 获取描述信息
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * 显示详情
     *
     * @param context 上下文
     */
    @Override
    public void onClick(Context context) {
        if (FriendshipInfo.getInstance().isFriend(profile.getIdentifier())) {
            //查看好友信息
//            ProfileActivity.navToProfile(context, profile.getIdentifier());
        } else {
            //添加好友
//            Intent person = new Intent(context,AddFriendActivity.class);
//            person.putExtra("id",profile.getIdentifier());
//            person.putExtra("name",getTitle());
//            context.startActivity(person);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * 获取用户ID
     */
    @Override
    public String getIdentify() {
        if(null!=profile){
            return profile.getIdentifier();
        }
        return "";
    }


    /**
     * 获取用户备注名
     */
    public String getRemark() {
        if(null!=profile){
            return profile.getRemark();
        }
       return "";
    }


    /**
     * 获取好友分组
     */
    public String getGroupName() {
        if(null!=profile){
            if (profile.getFriendGroups().size() == 0) {
                return VideoApplication.getContext().getString(R.string.default_group_name);
            } else {
                return profile.getFriendGroups().get(0);
            }
        }
        return "";
    }
}
