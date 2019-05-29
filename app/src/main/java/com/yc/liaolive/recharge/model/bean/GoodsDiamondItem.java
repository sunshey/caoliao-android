package com.yc.liaolive.recharge.model.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.ServerBean;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 */

public class GoodsDiamondItem implements MultiItemEntity {

    private int itemType;
    private List<BannerInfo> banners;//广告
    private List<RechargeGoodsInfo> list;//商品列表
    private PayConfigBean pay_config;//支付信息
    private ServerBean server;//服务账号信息
    private List<RechargeActivity> describe_list;//活动

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public List<BannerInfo> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerInfo> banners) {
        this.banners = banners;
    }

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

    public ServerBean getServer() {
        return server;
    }

    public void setServer(ServerBean server) {
        this.server = server;
    }

    public List<RechargeActivity> getDescribe_list() {
        return describe_list;
    }

    public void setDescribe_list(List<RechargeActivity> describe_list) {
        this.describe_list = describe_list;
    }
}
