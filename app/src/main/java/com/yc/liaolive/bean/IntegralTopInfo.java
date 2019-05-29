package com.yc.liaolive.bean;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/30
 */

public class IntegralTopInfo {

    public IntegralUser info;

    List<FansInfo> list;

    public IntegralUser getInfo() {
        return info;
    }

    public void setInfo(IntegralUser info) {
        this.info = info;
    }

    public List<FansInfo> getList() {
        return list;
    }

    public void setList(List<FansInfo> list) {
        this.list = list;
    }
}
