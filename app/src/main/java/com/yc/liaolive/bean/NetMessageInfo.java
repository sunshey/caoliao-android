package com.yc.liaolive.bean;

import android.support.annotation.NonNull;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/12/21.
 */

public class NetMessageInfo implements Serializable{

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable{
        private int count;
        /**
         * action :
         * add_date : 20171221
         * add_time : 1513840498
         * cover : http://video.nq6.com/Cover/1073450/5eb34cb630d2790077e40f4781c6bad0
         * desp : 也许这就是爱情，平平淡淡的浪漫 #动漫#
         * end_time : 1514618098
         * id : 3
         * intro : 我是测试的视频推送内容，介绍内容。。。
         * logo : http://q.qlogo.cn/qqapp/1106176094/4B48CE32783C06663472E083697460F3/100
         * nickname : 仙
         * start_time : 1513754098
         * type : 2
         * url :
         * user_id : 1073450
         * video_id : 8850
         */

        private List<ListBean> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Serializable ,Comparable<NetMessageInfo.DataBean.ListBean>{
            private String action;
            private String add_date;
            private String add_time;
            private String cover;
            private String desp;
            private String end_time;
            private String id;
            private String intro;
            private String logo;
            private String nickname;
            private String start_time;
            private String type;
            private String url;
            private String user_id;
            private String video_id;

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getAdd_date() {
                return add_date;
            }

            public void setAdd_date(String add_date) {
                this.add_date = add_date;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getDesp() {
                return desp;
            }

            public void setDesp(String desp) {
                this.desp = desp;
            }

            public String getEnd_time() {
                return end_time;
            }

            public void setEnd_time(String end_time) {
                this.end_time = end_time;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIntro() {
                return intro;
            }

            public void setIntro(String intro) {
                this.intro = intro;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getStart_time() {
                return start_time;
            }

            public void setStart_time(String start_time) {
                this.start_time = start_time;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getVideo_id() {
                return video_id;
            }

            public void setVideo_id(String video_id) {
                this.video_id = video_id;
            }

            @Override
            public int compareTo(@NonNull ListBean o) {
                NetMessageInfo.DataBean.ListBean data= (NetMessageInfo.DataBean.ListBean) o;
                return Long.parseLong(add_time)>Long.parseLong(data.getAdd_date())?1:0;
            }
        }
    }
}
