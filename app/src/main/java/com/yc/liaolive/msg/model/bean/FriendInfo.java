package com.yc.liaolive.msg.model.bean;

/**
 * Created by wanglin  on 2018/8/17 16:59.
 */
public class FriendInfo {
    private int avatarRes;
    private String avatarUrl;
    private String identify;
    private String name;
    private String remark;
    private boolean selected;
    private String groupName;

    public FriendInfo() {
    }

    public FriendInfo(int avatarRes, String avatarUrl, String identify, String name, String remark, boolean selected) {
        this.avatarRes = avatarRes;
        this.avatarUrl = avatarUrl;
        this.identify = identify;
        this.name = name;
        this.remark = remark;
        this.selected = selected;
    }

    public void setAvatarRes(int avatarRes) {
        this.avatarRes = avatarRes;
    }


    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


    public void setIdentify(String identify) {
        this.identify = identify;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getAvatarRes() {
        return avatarRes;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getIdentify() {
        return identify;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
