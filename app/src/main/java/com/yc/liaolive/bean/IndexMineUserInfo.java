package com.yc.liaolive.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/5/29
 * 首页-个人中心列表
 */

public class IndexMineUserInfo implements MultiItemEntity {

    public static final int ITEM_0 = 0;
    public static final int ITEM_1 = 1;
    public static final int ITEM_2 = 2;
    public static final int ITEM_3 = 3;
    public static final int ITEM_4 = 4;
    public static final int ITEM_5 = 5;
    private int type;//0:默认的黑色样式 1：粉色样式
    private int itemType;//0：m默认的全背景元素，1：上 2：中 3：下
    private int icon;
    private int itemID;//ItemID
    private int chat_deplete;//视频聊价格
    private String title;
    private String subTitle;
    private String subTitleColor;
    private String desp;
    private String moreText;
    private boolean isMore;//是否显示更多
    private boolean isSpace;//是否显示粗体分割线
    private boolean isLine;//是否显示细体分割线
    private boolean isExcuse;//是否处于勿扰模式下
    private boolean showBottomSpace;//是否显示底部间距
    private boolean showTopSpace;//是否显示顶部间距
    private String activityName;//Activity名称
    private List<FansInfo> fansInfos;//粉丝图像
    private int is_follow;//是否已经关注此用户
    private List<PrivateMedia> mediaList;//多媒体文件
    //照片个数
    private int imageCount;
    //视频个数
    private int videoCount;
    //粉丝个数
    private int fansCount;
    //关注个数
    private int followCount;
    //位置
    private String location;
    //多媒体个数
    private int mediaCount;
    //多媒体类型
    private int mediaType;

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<PrivateMedia> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<PrivateMedia> mediaList) {
        this.mediaList = mediaList;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitleColor() {
        return subTitleColor;
    }

    public void setSubTitleColor(String subTitleColor) {
        this.subTitleColor = subTitleColor;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getMoreText() {
        return moreText;
    }

    public void setMoreText(String moreText) {
        this.moreText = moreText;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public boolean isSpace() {
        return isSpace;
    }

    public void setSpace(boolean space) {
        isSpace = space;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public List<FansInfo> getFansInfos() {
        return fansInfos;
    }

    public void setFansInfos(List<FansInfo> fansInfos) {
        this.fansInfos = fansInfos;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public boolean isLine() {
        return isLine;
    }

    public void setLine(boolean line) {
        isLine = line;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getChat_deplete() {
        return chat_deplete;
    }

    public void setChat_deplete(int chat_deplete) {
        this.chat_deplete = chat_deplete;
    }

    public boolean isExcuse() {
        return isExcuse;
    }

    public void setExcuse(boolean excuse) {
        isExcuse = excuse;
    }

    public boolean isShowBottomSpace() {
        return showBottomSpace;
    }

    public void setShowBottomSpace(boolean showBottomSpace) {
        this.showBottomSpace = showBottomSpace;
    }

    public boolean isShowTopSpace() {
        return showTopSpace;
    }

    public void setShowTopSpace(boolean showTopSpace) {
        this.showTopSpace = showTopSpace;
    }
}
