package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2018/8/21
 * 家族信息
 */

public class FamilyInfo implements Parcelable {

    private String frontcover;
    private int level_integral;
    private String name;

    public FamilyInfo(){

    }

    protected FamilyInfo(Parcel in) {
        frontcover = in.readString();
        level_integral = in.readInt();
        name = in.readString();
    }

    public static final Creator<FamilyInfo> CREATOR = new Creator<FamilyInfo>() {
        @Override
        public FamilyInfo createFromParcel(Parcel in) {
            return new FamilyInfo(in);
        }

        @Override
        public FamilyInfo[] newArray(int size) {
            return new FamilyInfo[size];
        }
    };

    public String getFrontcover() {
        return frontcover;
    }

    public void setFrontcover(String frontcover) {
        this.frontcover = frontcover;
    }

    public int getLevel_integral() {
        return level_integral;
    }

    public void setLevel_integral(int level_integral) {
        this.level_integral = level_integral;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(frontcover);
        dest.writeInt(level_integral);
        dest.writeString(name);
    }
}
