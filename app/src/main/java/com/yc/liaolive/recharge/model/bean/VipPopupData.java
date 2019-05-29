package com.yc.liaolive.recharge.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TinyHung@Outlook.com
 * 2019/2/26
 */

public class VipPopupData implements Parcelable{

    private VipRechargePoppupBean popup_page;
    private String bind_mobile;

    protected VipPopupData(Parcel in) {
        popup_page = in.readParcelable(VipRechargePoppupBean.class.getClassLoader());
        bind_mobile = in.readString();
    }

    public static final Creator<VipPopupData> CREATOR = new Creator<VipPopupData>() {
        @Override
        public VipPopupData createFromParcel(Parcel in) {
            return new VipPopupData(in);
        }

        @Override
        public VipPopupData[] newArray(int size) {
            return new VipPopupData[size];
        }
    };

    public VipRechargePoppupBean getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(VipRechargePoppupBean popup_page) {
        this.popup_page = popup_page;
    }

    public String getBind_mobile() {
        return bind_mobile;
    }

    public void setBind_mobile(String bind_mobile) {
        this.bind_mobile = bind_mobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(popup_page, flags);
        dest.writeString(bind_mobile);
    }
}
