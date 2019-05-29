package com.yc.liaolive.index.model.bean;

import android.text.TextUtils;

import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.adapter.NearbyUserFragmentAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * 附近的人
 * Created by yangxueqin on 2019/1/8.
 */

public class NearbyUserBean implements Serializable{

    private String has_more_data;

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public String getHas_more_data() {
        return has_more_data;
    }

    public void setHas_more_data(String has_more_data) {
        this.has_more_data = has_more_data;
    }

    public static class ListBean implements Serializable, MultiItemEntity {
        /**
         * userid : 10000138
         * nickname : 孤妄
         * avatar : http://a.tnxxjs.com/uploads/head_img/a844e1ad08fc4108abf754634935327b!400x400!40x40.jpeg
         * signature : 我的兄弟叫顺溜
         * age : 18
         * nearby : 1.5公里
         */

        private String userid;
        private String nickname;
        private String avatar;
        private String signature;
        private String age;
        private String nearby;
        private String vip; //0不是 1是

        //banners
        private String itemCategory;
        //广告
        private List<BannerInfo> banners;

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getNearby() {
            return nearby;
        }

        public void setNearby(String nearby) {
            this.nearby = nearby;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public void setItemCategory(String itemCategory) {
            this.itemCategory = itemCategory;
        }

        public List<BannerInfo> getBanners() {
            return banners;
        }

        public void setBanners(List<BannerInfo> banners) {
            this.banners = banners;
        }


        @Override
        public int getItemType() {
            int itemType;
            if(TextUtils.equals(itemCategory, Constant.INDEX_ITEM_TYPE_BANNERS)) {
                itemType = NearbyUserFragmentAdapter.ITEM_TYPE_BANNERS;
            } else {
                itemType = NearbyUserFragmentAdapter.ITEM_TYPE_USERS;
            }
            return itemType;
        }

    }

}
