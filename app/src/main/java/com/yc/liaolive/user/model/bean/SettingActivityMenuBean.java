package com.yc.liaolive.user.model.bean;

import java.util.List;

/**
 * 设置页活动菜单
 * Created by yangxueqin on 2018/11/26.
 */

public class SettingActivityMenuBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String content;

        private String url;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
