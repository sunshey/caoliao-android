package com.yc.liaolive.bean;

import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/19
 * VIP种类
 */

public class VipDataBean {

    private String desp;
    private String name;
    private String icon;
    private int id;
    //会员套餐
    private List<RechargeGoodsInfo> goods_list;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RechargeGoodsInfo> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<RechargeGoodsInfo> goods_list) {
        this.goods_list = goods_list;
    }
}
