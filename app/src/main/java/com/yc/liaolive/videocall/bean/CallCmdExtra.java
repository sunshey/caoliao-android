package com.yc.liaolive.videocall.bean;

/**
 * Created by hty_Yuye@Outlook.com
 * 2018/12/21
 * 视频通话信令参数有
 */

public class CallCmdExtra {
    
    //通话信令交互参数
    private String cmd;//CMD
    private String uid;//用户uid
    private String name;//用户昵称
    private String avatar;//用户头像
    private String senderRoomToken;//呼叫人进入房间Token
    private String receiverRoomToken;//应答人进入房间Token
    private String rid;//预约ID
    private boolean isRecall;//是否回拨
    private String price;//视频聊分钟价格
    private String roomid;//房间ID
    private String content;//文本描述内容
    private long ctime;//呼叫发起时间
    private String extraParams;//自定义参数
    
    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSenderRoomToken() {
        return senderRoomToken;
    }

    public void setSenderRoomToken(String senderRoomToken) {
        this.senderRoomToken = senderRoomToken;
    }

    public String getReceiverRoomToken() {
        return receiverRoomToken;
    }

    public void setReceiverRoomToken(String receiverRoomToken) {
        this.receiverRoomToken = receiverRoomToken;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public boolean isRecall() {
        return isRecall;
    }

    public void setRecall(boolean recall) {
        isRecall = recall;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        return "CallCmdExtra{" +
                "cmd='" + cmd + '\'' +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", senderRoomToken='" + senderRoomToken + '\'' +
                ", receiverRoomToken='" + receiverRoomToken + '\'' +
                ", rid='" + rid + '\'' +
                ", isRecall=" + isRecall +
                ", price='" + price + '\'' +
                ", roomid='" + roomid + '\'' +
                ", content='" + content + '\'' +
                ", ctime=" + ctime +
                ", extraParams='" + extraParams + '\'' +
                '}';
    }
}
