package com.yc.liaolive.recharge.model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * vip充值成功活动弹窗、登录后领取弹窗对象
 * Created by yangxueqin on 2018/11/24.
 */

public class VipRechargePoppupBean implements Parcelable{

    private String title;
    private List<VipListItem> list;
    private List<VipListItem> list_coin;

    public VipRechargePoppupBean() {

    }

    protected VipRechargePoppupBean(Parcel in) {
        title = in.readString();
        list = in.createTypedArrayList(VipListItem.CREATOR);
        list_coin = in.createTypedArrayList(VipListItem.CREATOR);
    }

    public static final Creator<VipRechargePoppupBean> CREATOR = new Creator<VipRechargePoppupBean>() {
        @Override
        public VipRechargePoppupBean createFromParcel(Parcel in) {
            return new VipRechargePoppupBean(in);
        }

        @Override
        public VipRechargePoppupBean[] newArray(int size) {
            return new VipRechargePoppupBean[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VipListItem> getList() {
        return list;
    }

    public void setList(List<VipListItem> list) {
        this.list = list;
    }

    public List<VipListItem> getList_coin() {
        return list_coin;
    }

    public void setList_coin(List<VipListItem> list_coin) {
        this.list_coin = list_coin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(list);
        dest.writeTypedList(list_coin);
    }

    @Override
    public String toString() {
        return "VipRechargePoppupBean{" +
                "title='" + title + '\'' +
                ", list=" + list +
                ", list_coin=" + list_coin +
                '}';
    }
}