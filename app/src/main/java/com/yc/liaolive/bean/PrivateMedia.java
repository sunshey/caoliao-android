package com.yc.liaolive.bean;

import android.text.TextUtils;
import com.music.player.lib.bean.BaseMediaInfo;
import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/14
 * 私有多媒体
 */

public class PrivateMedia extends BaseMediaInfo implements MultiItemEntity,Serializable{

    private int annex_type;//附件类型 0：文本 1：图片、视频
    private String content;//附件类型为0，纯文本
    private int is_private;//1：私密 0：公开
    private long addtime;
    private int state;//审核状态 0 : 未通过 1：通过 2：关闭
    public long browse_number;//浏览人数
    public long love_number;//点赞人数
    public long share_number;//分享人数
    private String isPost;//浏览记录是否上报
    private int attent;//关注状态 1：已关注 0：未关注
    private int is_online;//主播是否正在直播
    private int is_love;//是否对该视频点赞
    private String signature;//用户签名
    private int identity_audit;//主播身份  2:主播 其他状态不是
    private String itemCategory="";//多条目类型
    private String action;
    private int icon;//多条目类别
    private int itemType;
    private int chat_price;//扣除的单价，文本消息
    private String user_state;//用户状态 offline 离线、live 正在直播、 videocall 视频聊天、disturbed  防止打扰、 free 空闲
    private String android_price;
    private List<BannerInfo> banners;
    //直播间信息
    private VideoRoomInfo roomInfo;
    private String image_small_show;


    public VideoRoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(VideoRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    @Override
    public int getItemType() {
        if(TextUtils.isEmpty(itemCategory)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_VIDEO;
            return IndexVideoListAdapter.ITEM_TYPE_VIDEO;
        }
        //多广告
        if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_BANNERS)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_BANNERS;
            //头部添加按钮
        }else if(TextUtils.equals(Constant.ITEM_ACTION_ADD, itemCategory)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_ADD;
            //单个广告
        }else if(TextUtils.equals(itemCategory,Constant.INDEX_ITEM_TYPE_BANNER)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_BANNER;
            //音频
        }else if(TextUtils.equals(itemCategory,Constant.INDEX_ITEM_AUDIO)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_AUDIO;
            //视频
        }else if(TextUtils.equals(itemCategory,Constant.INDEX_ITEM_VIDEO)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_VIDEO;
            //图片
        }else if(TextUtils.equals(itemCategory,Constant.INDEX_ITEM_IMAGE)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_IMAGE;
            //直播间
        }else if(TextUtils.equals(itemCategory,Constant.INDEX_ITEM_IMAGE)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_IMAGE;
            //ASMR视频
        }else if(TextUtils.equals(itemCategory,Constant.INDEX_ITEM_ASMR_VIDEO)){
            itemType= IndexVideoListAdapter.ITEM_TYPE_ASMR_VIDEO;
        }else{
            //单个视频、音频
            itemType= IndexVideoListAdapter.ITEM_TYPE_VIDEO;
        }
        return itemType;
    }

    public int getAnnex_type() {
        return annex_type;
    }

    public void setAnnex_type(int annex_type) {
        this.annex_type = annex_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getBrowse_number() {
        return browse_number;
    }

    public void setBrowse_number(long browse_number) {
        this.browse_number = browse_number;
    }

    public long getLove_number() {
        return love_number;
    }

    public void setLove_number(long love_number) {
        this.love_number = love_number;
    }

    public long getShare_number() {
        return share_number;
    }

    public void setShare_number(long share_number) {
        this.share_number = share_number;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }

    public int getAttent() {
        return attent;
    }

    public void setAttent(int attent) {
        this.attent = attent;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public int getIs_love() {
        return is_love;
    }

    public void setIs_love(int is_love) {
        this.is_love = is_love;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(int identity_audit) {
        this.identity_audit = identity_audit;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getChat_price() {
        return chat_price;
    }

    public void setChat_price(int chat_price) {
        this.chat_price = chat_price;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public List<BannerInfo> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerInfo> banners) {
        this.banners = banners;
    }

    public String getImage_small_show() {
        return image_small_show;
    }

    public void setImage_small_show(String image_small_show) {
        this.image_small_show = image_small_show;
    }

    public String getAndroid_price() {
        return android_price;
    }

    public void setAndroid_price(String android_price) {
        this.android_price = android_price;
    }
}