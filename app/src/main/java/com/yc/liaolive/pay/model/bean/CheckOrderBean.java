package com.yc.liaolive.pay.model.bean;

import com.yc.liaolive.recharge.model.bean.VipRechargePoppupBean;

/**
 * 支付完成同步支付结果
 * Created by yangxueqin on 2018/11/12.
 */
public class CheckOrderBean {

    /**
     * 是否弹出绑定手机 1 不弹出 2 弹出
     */
    private int bind_mobile = 0;

    private VipRechargePoppupBean popup_page;

    public int getBind_mobile() {
        return bind_mobile;
    }

    public void setBind_mobile(int bind_mobile) {
        this.bind_mobile = bind_mobile;
    }

    public VipRechargePoppupBean getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(VipRechargePoppupBean popup_page) {
        this.popup_page = popup_page;
    }
}
