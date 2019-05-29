package com.yc.liaolive.recharge.model.bean;

/**
 * Created by wanglin  on 2018/8/14 21:51.
 */
public class RechargeInfo {

    private RechargeGoodsInfo goodsInfo;
    private int payway;
    private int position;


    public RechargeGoodsInfo getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(RechargeGoodsInfo goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public int getPayway() {
        return payway;
    }

    public void setPayway(int payway) {
        this.payway = payway;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
