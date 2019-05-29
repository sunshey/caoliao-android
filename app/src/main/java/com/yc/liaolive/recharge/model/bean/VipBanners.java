package com.yc.liaolive.recharge.model.bean;

import com.yc.liaolive.bean.BannerInfo;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/10
 */

public class VipBanners {

    private List<BannerInfo> banners;
    private String itemCategory;

    public List<BannerInfo> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerInfo> banners) {
        this.banners = banners;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    @Override
    public String toString() {
        return "VipBanners{" +
                "banners=" + banners +
                ", itemCategory='" + itemCategory + '\'' +
                '}';
    }
}
