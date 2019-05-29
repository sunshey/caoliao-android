package com.yc.liaolive.videocall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/8/20
 * 视频通话呼叫自定义参数
 */

public class CallExtraInfo implements Parcelable {

    private String senderRoomToken;//呼叫人进入房间Token
    private String receiverRoomToken;//应答人进入房间Token
    private String recevierID;//预约ID
    private String price="0";//视频聊分钟价格
    private String roomID;//通话房间ID
    //相对于呼叫方的对方信息
    private String toUserID;
    private String toNickName;
    private String toAvatar;
    //接收到视频来电系统时间
    private long systemTime;
    private String content;//文本描述内容
    //视频通话实际发起人
    private String callUserID;
    //视频通话主播ID
    private String callAnchorID;
    //被呼叫人封面
    private String anchorFront;
    //视频地址
    private String videoPath;
    private int callType; //0:视频 1：语音
    private int enterIdentify;//0：发起 1：接听
    private long ctime;//发起呼叫时间戳

    public CallExtraInfo(){}

    protected CallExtraInfo(Parcel in) {
        senderRoomToken = in.readString();
        receiverRoomToken = in.readString();
        recevierID = in.readString();
        price = in.readString();
        roomID = in.readString();
        toUserID = in.readString();
        toNickName = in.readString();
        toAvatar = in.readString();
        systemTime = in.readLong();
        content = in.readString();
        callUserID = in.readString();
        callAnchorID = in.readString();
        anchorFront = in.readString();
        videoPath = in.readString();
        callType = in.readInt();
        enterIdentify = in.readInt();
        ctime = in.readLong();
    }

    public static final Creator<CallExtraInfo> CREATOR = new Creator<CallExtraInfo>() {
        @Override
        public CallExtraInfo createFromParcel(Parcel in) {
            return new CallExtraInfo(in);
        }

        @Override
        public CallExtraInfo[] newArray(int size) {
            return new CallExtraInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderRoomToken);
        dest.writeString(receiverRoomToken);
        dest.writeString(recevierID);
        dest.writeString(price);
        dest.writeString(roomID);
        dest.writeString(toUserID);
        dest.writeString(toNickName);
        dest.writeString(toAvatar);
        dest.writeLong(systemTime);
        dest.writeString(content);
        dest.writeString(callUserID);
        dest.writeString(callAnchorID);
        dest.writeString(anchorFront);
        dest.writeString(videoPath);
        dest.writeInt(callType);
        dest.writeInt(enterIdentify);
        dest.writeLong(ctime);
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

    public String getRecevierID() {
        return recevierID;
    }

    public void setRecevierID(String recevierID) {
        this.recevierID = recevierID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getToNickName() {
        return toNickName;
    }

    public void setToNickName(String toNickName) {
        this.toNickName = toNickName;
    }

    public String getToAvatar() {
        return toAvatar;
    }

    public void setToAvatar(String toAvatar) {
        this.toAvatar = toAvatar;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCallUserID() {
        return callUserID;
    }

    public void setCallUserID(String callUserID) {
        this.callUserID = callUserID;
    }

    public String getCallAnchorID() {
        return callAnchorID;
    }

    public void setCallAnchorID(String callAnchorID) {
        this.callAnchorID = callAnchorID;
    }

    public String getAnchorFront() {
        return anchorFront;
    }

    public void setAnchorFront(String anchorFront) {
        this.anchorFront = anchorFront;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getEnterIdentify() {
        return enterIdentify;
    }

    public void setEnterIdentify(int enterIdentify) {
        this.enterIdentify = enterIdentify;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        return "CallExtraInfo{" +
                "senderRoomToken='" + senderRoomToken + '\'' +
                ", receiverRoomToken='" + receiverRoomToken + '\'' +
                ", recevierID='" + recevierID + '\'' +
                ", price='" + price + '\'' +
                ", roomID='" + roomID + '\'' +
                ", toUserID='" + toUserID + '\'' +
                ", toNickName='" + toNickName + '\'' +
                ", toAvatar='" + toAvatar + '\'' +
                ", systemTime=" + systemTime +
                ", content='" + content + '\'' +
                ", callUserID='" + callUserID + '\'' +
                ", callAnchorID='" + callAnchorID + '\'' +
                ", anchorFront='" + anchorFront + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", callType=" + callType +
                ", enterIdentify=" + enterIdentify +
                ", ctime=" + ctime +
                '}';
    }
}