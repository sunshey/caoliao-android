package com.yc.liaolive.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/18
 * 会员套餐信息
 */

public class VipListInfo {
    //会员介绍
    private String vip_desp;
    //会员分类
    List<VipDataBean> list;

    public String getVip_desp() {
        return vip_desp;
    }

    public void setVip_desp(String vip_desp) {
        this.vip_desp = vip_desp;
    }

    public List<VipDataBean> getList() {
        return list;
    }

    public void setList(List<VipDataBean> list) {
        this.list = list;
    }
}
