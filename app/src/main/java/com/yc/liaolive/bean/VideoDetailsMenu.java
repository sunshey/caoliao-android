package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2017/12/10
 */

public class VideoDetailsMenu implements Parcelable{

    private String itemName;
    private String textColor;
    private boolean blackExist;//是否已经在黑名单中了
    private int itemID;  //1:公开/私密  2：允许/不允许他人下载 3：删除视频 4:举报视频

    public VideoDetailsMenu(){
        super();
    }

    protected VideoDetailsMenu(Parcel in) {
        itemName = in.readString();
        textColor = in.readString();
        blackExist = in.readByte() != 0;
        itemID = in.readInt();
    }

    public static final Creator<VideoDetailsMenu> CREATOR = new Creator<VideoDetailsMenu>() {
        @Override
        public VideoDetailsMenu createFromParcel(Parcel in) {
            return new VideoDetailsMenu(in);
        }

        @Override
        public VideoDetailsMenu[] newArray(int size) {
            return new VideoDetailsMenu[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(textColor);
        dest.writeByte((byte) (blackExist ? 1 : 0));
        dest.writeInt(itemID);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public boolean isBlackExist() {
        return blackExist;
    }

    public void setBlackExist(boolean blackExist) {
        this.blackExist = blackExist;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
}
