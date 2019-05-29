package com.yc.liaolive.recharge.model.bean;

import java.util.List;

/**
 * 充值接口数据
 * 钻石及VIP通用
 * Created by yangxueqin on 2018/12/19.
 */

public class RechargeBean {
    //广告列表
    private List<VipBanners> ad_list;
    //商品列表
    private List<RechargeGoodsInfo> list;
    //支付配置参数
    private PayConfigBean pay_config;
    //活动列表
    private List<RechargeActivity> describe_list;

    private String show_server;//0:不显示客服 1：显示客服

    public List<RechargeGoodsInfo> getList() {
        return list;
    }

    public void setList(List<RechargeGoodsInfo> list) {
        this.list = list;
    }

    public PayConfigBean getPay_config() {
        return pay_config;
    }

    public void setPay_config(PayConfigBean pay_config) {
        this.pay_config = pay_config;
    }

    public List<VipBanners> getAd_list() {
        return ad_list;
    }

    public void setAd_list(List<VipBanners> ad_list) {
        this.ad_list = ad_list;
    }

    public List<RechargeActivity> getDescribe_list() {
        return describe_list;
    }

    public void setDescribe_list(List<RechargeActivity> describe_list) {
        this.describe_list = describe_list;
    }

    public String getShow_server() {
        return show_server;
    }

    public void setShow_server(String show_server) {
        this.show_server = show_server;
    }

    @Override
    public String toString() {
        return "RechargeBean{" +
                "ad_list=" + ad_list +
                ", list=" + list +
                ", pay_config=" + pay_config +
                ", describe_list=" + describe_list +
                ", show_server='" + show_server + '\'' +
                '}';
    }
}
