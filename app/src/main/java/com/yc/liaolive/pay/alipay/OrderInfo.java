package com.yc.liaolive.pay.alipay;

import java.io.Serializable;

/**
 * Created by zhangkai on 2017/3/17.
 */

public class OrderInfo implements Serializable {

    private static final long serialVersionUID = -7060210533610464481L;
    private String state;//支付状态 6001：支付取消
    private float money; //价格 单位元

    private String title; //会员类型名 也即商品名

    private String payurl;//H5支付地址

    private String charge_order_sn; //订单号
    private int payWay;//支付方式  0:阿里 1：微信
    private String message;

    private String action_msg;

    private PaywayInfoBean payway_info;

    public PaywayInfoBean getPayway_info() {
        return payway_info;
    }

    public void setPayway_info(PaywayInfoBean payway_info) {
        this.payway_info = payway_info;
    }

    public int getPayWay() {
        return payWay;
    }

    public void setPayWay(int payWay) {
        this.payWay = payWay;
    }

    public String getPayurl() {
        return payurl;
    }

    public void setPayurl(String payurl) {
        this.payurl = payurl;
    }

    private String type;//支付类型

    private PayInfo params;

    public OrderInfo() {
    }

    public OrderInfo(float money, String title, String order_sn) {
        this.money = money;
        this.title = title;
        this.charge_order_sn = order_sn;
    }


    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCharge_order_sn() {
        return charge_order_sn;
    }

    public void setCharge_order_sn(String charge_order_sn) {
        this.charge_order_sn = charge_order_sn;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public PayInfo getParams() {
        return params;
    }

    public void setParams(PayInfo params) {
        this.params = params;
    }

    public String getAction_msg() {
        return action_msg;
    }

    public void setAction_msg(String action_msg) {
        this.action_msg = action_msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "money=" + money +
                ", title='" + title + '\'' +
                ", payurl='" + payurl + '\'' +
                ", charge_order_sn='" + charge_order_sn + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", params=" + params +
                '}';
    }

    public static class PaywayInfoBean implements Serializable {
        private int trade_type = 1; //1原生 2：H5

        private String auth_domain;

        public int getTrade_type() {
            return trade_type;
        }

        public void setTrade_type(int trade_type) {
            this.trade_type = trade_type;
        }

        public String getAuth_domain() {
            return auth_domain;
        }

        public void setAuth_domain(String auth_domain) {
            this.auth_domain = auth_domain;
        }
    }
}
