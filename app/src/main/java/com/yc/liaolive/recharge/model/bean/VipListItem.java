package com.yc.liaolive.recharge.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2019/2/25
 */

public class VipListItem implements Parcelable{

    private String img_url;
    private String text;
    private String day_text;
    private String width;
    private String height;

    public VipListItem(){

    }

    protected VipListItem(Parcel in) {
        img_url = in.readString();
        text = in.readString();
        day_text = in.readString();
        width = in.readString();
        height = in.readString();
    }

    public static final Creator<VipListItem> CREATOR = new Creator<VipListItem>() {
        @Override
        public VipListItem createFromParcel(Parcel in) {
            return new VipListItem(in);
        }

        @Override
        public VipListItem[] newArray(int size) {
            return new VipListItem[size];
        }
    };

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDay_text() {
        return day_text;
    }

    public void setDay_text(String day_text) {
        this.day_text = day_text;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(img_url);
        dest.writeString(text);
        dest.writeString(day_text);
        dest.writeString(width);
        dest.writeString(height);
    }

    @Override
    public String toString() {
        return "VipListItem{" +
                "img_url='" + img_url + '\'' +
                ", text='" + text + '\'' +
                ", day_text='" + day_text + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}