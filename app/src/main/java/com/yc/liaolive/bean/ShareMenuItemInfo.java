package com.yc.liaolive.bean;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @time 2017/3/6 18:11
 * @des $弹窗菜单的对象
 */
public class ShareMenuItemInfo {

    private String itemName;
    private int itemLogo;
    private SHARE_MEDIA platform;

    public ShareMenuItemInfo(){
        super();
    }

    public ShareMenuItemInfo(String itemName, int itemLogo) {
        this.itemName = itemName;
        this.itemLogo = itemLogo;
    }

    public ShareMenuItemInfo(String itemName, int itemLogo,SHARE_MEDIA platform) {
        this.itemName = itemName;
        this.itemLogo = itemLogo;
        this.platform=platform;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemLogo() {
        return itemLogo;
    }

    public void setItemLogo(int itemLogo) {
        this.itemLogo = itemLogo;
    }

    public SHARE_MEDIA getPlatform() {
        return platform;
    }

    public void setPlatform(SHARE_MEDIA platform) {
        this.platform = platform;
    }
}
