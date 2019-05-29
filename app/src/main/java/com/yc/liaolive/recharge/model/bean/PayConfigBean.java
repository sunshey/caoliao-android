package com.yc.liaolive.recharge.model.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 */

public class PayConfigBean {

    private List<PayConfigItemBean> pay_list;

    private String default_pay;

    private List<ZhifubaoTipsBean> zhifubao_tips;

    public List<PayConfigItemBean> getPay_list() {
        return pay_list;
    }

    public void setPay_list(List<PayConfigItemBean> pay_list) {
        this.pay_list = pay_list;
    }

    public String getDefault_pay() {
        return default_pay;
    }

    public void setDefault_pay(String default_pay) {
        this.default_pay = default_pay;
    }

    @Override
    public String toString() {
        return "PayConfigBean{" +
                "pay_list=" + pay_list +
                ", default_pay='" + default_pay + '\'' +
                ", zhifubao_tips='"+zhifubao_tips +"'}";
    }

    public List<ZhifubaoTipsBean> getZhifubao_tips() {
        return zhifubao_tips;
    }

    public void setZhifubao_tips(List<ZhifubaoTipsBean> zhifubao_tips) {
        this.zhifubao_tips = zhifubao_tips;
    }

    public static class PayConfigItemBean {

        private String pay_title;
        private String description;//支付文案
        private String item;

        public String getPay_title() {
            return pay_title;
        }

        public void setPay_title(String pay_title) {
            this.pay_title = pay_title;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "PayConfigItemBean{" +
                    "pay_title='" + pay_title + '\'' +
                    "description='" + description + '\'' +
                    ", item='" + item + '\'' +
                    '}';
        }
    }

    public static class ZhifubaoTipsBean {
        /**
         * state : 0   0未启用 1启用
         * txt : 确定放弃支付宝支付额外赠送</br>5%钻石的优惠？
         */

        private String state;
        private String txt;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }
}
