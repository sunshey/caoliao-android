package com.yc.liaolive.recharge.model.bean;

/**
 * TinyHung@Outlook.com
 * 2019/1/22
 * 充值模块列表活动配置
 */

public class RechargeActivity {

    /**
     * height : 100
     * img_url : http://a.197754.com/uploads/images/20181128/ee679dc8c7bb055ecdf6f653cee5e690.png
     * jump_url : caoliao://
     * width : 200
     */

    private String height;
    private String img_url;
    private String width;
    private String jump_url;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    @Override
    public String toString() {
        return "RechargeActivity{" +
                "height='" + height + '\'' +
                ", img_url='" + img_url + '\'' +
                ", width='" + width + '\'' +
                ", jump_url='" + jump_url + '\'' +
                '}';
    }
}
