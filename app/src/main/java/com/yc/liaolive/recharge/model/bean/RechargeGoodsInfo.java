package com.yc.liaolive.recharge.model.bean;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;

/**
 * TinyHung@Outlook.com
 * 2018/6/13
 * 商品信息
 */

public class RechargeGoodsInfo implements MultiItemEntity {

    /**
     * id : 10
     * name : 5888000
     * price : 5888
     * use_number : 5888000
     * unit : 金币
     * give_use_number : 520000
     * give_unit : 金币
     * pay_price : 5888
     */

    private String sub_title;
    private String gift_info;
    private String desc;
//    private int is_show_xs;
//    private int is_show_hn;
    private int is_show_yh; //1最划算 2不显示 3无优惠 最优惠标签 默认2
    private long give_num;//赠送金币
    private int id;
    private String name;
    private String price;
    private int use_number;
    private String unit;
    private int give_use_number;
    private String give_unit;
    private String pay_price;
    private boolean selected;
    private String desp;
    private int itemType;

    private String text_show_xs;//限时标签显示文本

    private String text_show_hn; //红娘标签显示文本

    private String text_show_yh; //最优惠标签显示文本

    public long getGive_num() {
        return give_num;
    }

    public void setGive_num(long give_num) {
        this.give_num = give_num;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getUse_number() {
        return use_number;
    }

    public void setUse_number(int use_number) {
        this.use_number = use_number;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getGive_use_number() {
        return give_use_number;
    }

    public void setGive_use_number(int give_use_number) {
        this.give_use_number = give_use_number;
    }

    public String getGive_unit() {
        return give_unit;
    }

    public void setGive_unit(String give_unit) {
        this.give_unit = give_unit;
    }

    public String getPay_price() {
        return pay_price;
    }

    public void setPay_price(String pay_price) {
        this.pay_price = pay_price;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getGift_info() {
        return gift_info;
    }

    public void setGift_info(String gift_info) {
        this.gift_info = gift_info;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getText_show_xs() {
        return text_show_xs;
    }

    public void setText_show_xs(String text_show_xs) {
        this.text_show_xs = text_show_xs;
    }

    public String getText_show_hn() {
        return text_show_hn;
    }

    public void setText_show_hn(String text_show_hn) {
        this.text_show_hn = text_show_hn;
    }

    public String getText_show_yh() {
        return text_show_yh;
    }

    public void setText_show_yh(String text_show_yh) {
        this.text_show_yh = text_show_yh;
    }

    public int getIs_show_yh() {
        return is_show_yh;
    }

    public void setIs_show_yh(int is_show_yh) {
        this.is_show_yh = is_show_yh;
    }

    @Override
    public String toString() {
        return "RechargeGoodsInfo{" +
                "sub_title='" + sub_title + '\'' +
                ", gift_info='" + gift_info + '\'' +
                ", desc='" + desc + '\'' +
                ", is_show_yh=" + is_show_yh +
                ", give_num=" + give_num +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", use_number=" + use_number +
                ", unit='" + unit + '\'' +
                ", give_use_number=" + give_use_number +
                ", give_unit='" + give_unit + '\'' +
                ", pay_price='" + pay_price + '\'' +
                ", selected=" + selected +
                ", desp='" + desp + '\'' +
                ", itemType=" + itemType +
                ", text_show_xs='" + text_show_xs + '\'' +
                ", text_show_hn='" + text_show_hn + '\'' +
                ", text_show_yh='" + text_show_yh + '\'' +
                '}';
    }
}
