package com.yc.liaolive.live.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.live.adapter.LiveRoomRecyclerViewChatAdapter;
import com.yc.liaolive.user.manager.UserManager;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/21
 * 直播间的自定义消息发送和接收封装
 */

public class CustomMsgInfo implements MultiItemEntity {

    private List<String> cmd;
    private String childCmd;
    private String sendUserID;
    private String sendUserName;
    private int sendUserVIP;//发送人的等级
    private String sendUserHead;
    private int sendUserGradle;
    private int itemType;
    private int user_type;//用户身份 2：机器人 4：客服
    private String accapGroupID;//接收者群ID
    private String msgContent;
    private String msgContentColor;//文本颜色
    private String accapUserID;
    private String accapUserName;
    private String accapUserHead;
    private String roomid;
    private String groupid;
    private long roomTotalPoints;
    private long roomDayPoints;
    private long onlineNumer;
    private boolean isTanmu;
    private int totalPrice;
    //私有多媒体文件消息
    private int annex_type;//附件类型 0：文本 1：图片、视频
    private String content;//附件为0，纯文本消息
    private String file_path;
    private int file_type;
    private long id;
    private String img_path;
    private int is_private;
    private int price;
    private long video_durtion;
    private int chat_price;//扣除的价格
    private int sendUserType;//发送人身份  4:客服 2：机器人
    private int cameraState;//摄像头状态 0：打开 1：关闭
    private GiftInfo gift;//礼物信息
    private List<FansInfo> gift_member_top;//顶部排行榜单

    public CustomMsgInfo(){

    }

    /**
     * 构造方法
     * @param init
     */
    public CustomMsgInfo(int init){
        //初始构造自己的基本信息
        if(0==init){
            setSendUserID(UserManager.getInstance().getUserId());
            setSendUserName(UserManager.getInstance().getNickname());
            setSendUserHead(UserManager.getInstance().getAvatar());
            setSendUserGradle(UserManager.getInstance().getUserGradle());
            setSendUserVIP(UserManager.getInstance().getUserVip());
            setSendUserType(UserManager.getInstance().getUserType());
        }
    }

    public int getCameraState() {
        return cameraState;
    }

    public void setCameraState(int cameraState) {
        this.cameraState = cameraState;
    }

    public int getSendUserType() {
        return sendUserType;
    }

    public void setSendUserType(int sendUserType) {
        this.sendUserType = sendUserType;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getChat_price() {
        return chat_price;
    }

    public void setChat_price(int chat_price) {
        this.chat_price = chat_price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAnnex_type() {
        return annex_type;
    }

    public void setAnnex_type(int annex_type) {
        this.annex_type = annex_type;
    }

    public long getVideo_durtion() {
        return video_durtion;
    }

    public void setVideo_durtion(long video_durtion) {
        this.video_durtion = video_durtion;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private String fontCover;//主播的封面图

    public String getAccapGroupID() {
        return accapGroupID;
    }

    public void setAccapGroupID(String accapGroupID) {
        this.accapGroupID = accapGroupID;
    }

    public int getSendUserVIP() {
        return sendUserVIP;
    }

    public void setSendUserVIP(int sendUserVIP) {
        this.sendUserVIP = sendUserVIP;
    }


    public String getChildCmd() {
        return childCmd;
    }

    public void setChildCmd(String childCmd) {
        this.childCmd = childCmd;
    }


    public List<String> getCmd() {
        return cmd;
    }

    public void setCmd(List<String> cmd) {
        this.cmd = cmd;
    }

    public String getSendUserID() {
        return sendUserID;
    }

    public void setSendUserID(String sendUserID) {
        this.sendUserID = sendUserID;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getSendUserHead() {
        return sendUserHead;
    }

    public void setSendUserHead(String sendUserHeader) {
        this.sendUserHead = sendUserHeader;
    }

    public int getSendUserGradle() {
        return sendUserGradle;
    }

    public void setSendUserGradle(int sendUserGradle) {
        this.sendUserGradle = sendUserGradle;
    }

    public String getAccapUserID() {
        return accapUserID;
    }

    public void setAccapUserID(String accapUserID) {
        this.accapUserID = accapUserID;
    }

    public String getAccapUserName() {
        return accapUserName;
    }

    public void setAccapUserName(String accapUserName) {
        this.accapUserName = accapUserName;
    }

    public String getAccapUserHead() {
        return accapUserHead;
    }

    public void setAccapUserHead(String accapUserHeader) {
        this.accapUserHead = accapUserHeader;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public boolean isTanmu() {
        return isTanmu;
    }
    public void setTanmu(boolean tanmu) {
        isTanmu = tanmu;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgContentColor() {
        return msgContentColor;
    }

    public void setMsgContentColor(String msgContentColor) {
        this.msgContentColor = msgContentColor;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public long getRoomTotalPoints() {
        return roomTotalPoints;
    }

    public void setRoomTotalPoints(long roomTotalPoints) {
        this.roomTotalPoints = roomTotalPoints;
    }

    public long getRoomDayPoints() {
        return roomDayPoints;
    }

    public void setRoomDayPoints(long roomDayPoints) {
        this.roomDayPoints = roomDayPoints;
    }

    public GiftInfo getGift() {
        return gift;
    }

    public void setGift(GiftInfo gift) {
        this.gift = gift;
    }

    public long getOnlineNumer() {
        return onlineNumer;
    }

    public void setOnlineNumer(long onlineNumer) {
        this.onlineNumer = onlineNumer;
    }

    public List<FansInfo> getGift_member_top() {
        return gift_member_top;
    }

    public void setGift_member_top(List<FansInfo> gift_member_top) {
        this.gift_member_top = gift_member_top;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getFontCover() {
        return fontCover;
    }

    public void setFontCover(String fontCover) {
        this.fontCover = fontCover;
    }

    @Override
    public String toString() {
        return "CustomMsgInfo{" +
                "cmd=" + cmd +
                ", childCmd='" + childCmd + '\'' +
                ", sendUserID='" + sendUserID + '\'' +
                ", sendUserName='" + sendUserName + '\'' +
                ", sendUserVIP=" + sendUserVIP +
                ", sendUserHead='" + sendUserHead + '\'' +
                ", sendUserGradle=" + sendUserGradle +
                ", itemType=" + itemType +
                ", user_type=" + user_type +
                ", accapGroupID='" + accapGroupID + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", accapUserID='" + accapUserID + '\'' +
                ", accapUserName='" + accapUserName + '\'' +
                ", accapUserHead='" + accapUserHead + '\'' +
                ", roomid='" + roomid + '\'' +
                ", groupid='" + groupid + '\'' +
                ", roomTotalPoints=" + roomTotalPoints +
                ", roomDayPoints=" + roomDayPoints +
                ", onlineNumer=" + onlineNumer +
                ", isTanmu=" + isTanmu +
                ", totalPrice=" + totalPrice +
                ", annex_type=" + annex_type +
                ", content='" + content + '\'' +
                ", file_path='" + file_path + '\'' +
                ", file_type=" + file_type +
                ", id=" + id +
                ", img_path='" + img_path + '\'' +
                ", is_private=" + is_private +
                ", price=" + price +
                ", video_durtion=" + video_durtion +
                ", chat_price=" + chat_price +
                ", fontCover='" + fontCover + '\'' +
                ", gift=" + gift +
                ", gift_member_top=" + gift_member_top +
                ", msgContentColor=" + msgContentColor +
                '}';
    }

    @Override
    public int getItemType() {
        if(null==childCmd){
            itemType= LiveRoomRecyclerViewChatAdapter.ITEM_MESSAGE_DEFAULT;
            return itemType;
        }
        if(childCmd.equals(Constant.MSG_CUSTOM_NOTICE)){
            itemType= LiveRoomRecyclerViewChatAdapter.ITEM_MESSAGE_SYSTEM;//系统消息
        }else if(childCmd.equals(Constant.MSG_CUSTOM_GIFT)){
            itemType= LiveRoomRecyclerViewChatAdapter.ITEM_MESSAGE_GIFT;//礼物消息
        }else{
            itemType= LiveRoomRecyclerViewChatAdapter.ITEM_MESSAGE_CONTENT;//欢迎消息、聊天消息等
        }
        return itemType;
    }
}
