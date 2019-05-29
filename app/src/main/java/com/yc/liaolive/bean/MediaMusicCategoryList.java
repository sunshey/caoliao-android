package com.yc.liaolive.bean;

import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/11/10.
 */

public class MediaMusicCategoryList implements Serializable{

    /**
     * code : 1
     * data : [{"add_date":"20171109","add_time":"1510211025","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03fdd1aec03.jpg","id":"7285","sort":"500","title":"让爱重来","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03fdd1b0922.mp3"},{"add_date":"20171109","add_time":"1510211086","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03fe0e8c4fe.jpg","id":"7286","sort":"500","title":"想起","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03fe0e8e1a9.mp3"},{"add_date":"20171109","add_time":"1510211237","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03fea5723fa.jpg","id":"7288","sort":"500","title":"9月19日","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03fea574069.mp3"},{"add_date":"20171109","add_time":"1510211257","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03feb95344f.jpg","id":"7289","sort":"500","title":"春天的味道","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03feb955ab9.mp3"},{"add_date":"20171109","add_time":"1510211283","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03fed33889b.jpg","id":"7290","sort":"500","title":"烟雨烟雨","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03fed33a4d4.mp3"},{"add_date":"20171109","add_time":"1510211302","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03fee6c908b.jpg","id":"7291","sort":"500","title":"刺猬","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03fee6cac32.mp3"},{"add_date":"20171109","add_time":"1510211321","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03fef9af4d1.jpg","id":"7292","sort":"500","title":"后悔了吧","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03fef9b1038.mp3"},{"add_date":"20171109","add_time":"1510211341","cate_id":"7","cover":"http://sc.wk2.com/upload/music/2017-11-09/5a03ff0d44ffc.jpg","id":"7293","sort":"500","title":"怎样","url":"http://sc.wk2.com/upload/music/2017-11-09/5a03ff0d46c18.mp3"}]
     */

    private int code;
    /**
     * add_date : 20171109
     * add_time : 1510211025
     * cate_id : 7
     * cover : http://sc.wk2.com/upload/music/2017-11-09/5a03fdd1aec03.jpg
     * id : 7285
     * sort : 500
     * title : 让爱重来
     * url : http://sc.wk2.com/upload/music/2017-11-09/5a03fdd1b0922.mp3
     */

    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{

        /**
         * add_date : 20171109
         * add_time : 1510294632
         * cate_id : 7
         * cover : http://sc.wk2.com/upload/music/2017-11-09/5a03fdd1aec03.jpg
         * id : 7285
         * is_collect : 1
         * music_id : 7285
         * sort : 500
         * title : 让爱重来
         * url : http://sc.wk2.com/upload/music/2017-11-09/5a03fdd1b0922.mp3
         * user_id : 1060153
         */

        private String add_date;
        private String add_time;
        private String cate_id;
        private String cover;
        private String id;
        private int is_collect;
        private String music_id;
        private String sort;
        private String title;
        private String url;
        private String user_id;
        private boolean isPlayerIng;
        private String author;
        private String seconds;

        public boolean isPlayerIng() {
            return isPlayerIng;
        }

        public void setPlayerIng(boolean playIng) {
            isPlayerIng = playIng;
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

        public String getCate_id() {
            return cate_id;
        }

        public void setCate_id(String cate_id) {
            this.cate_id = cate_id;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIs_collect() {
            return is_collect;
        }

        public void setIs_collect(int is_collect) {
            this.is_collect = is_collect;
        }

        public String getMusic_id() {
            return music_id;
        }

        public void setMusic_id(String music_id) {
            this.music_id = music_id;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getSeconds() {
            return seconds;
        }

        public void setSeconds(String seconds) {
            this.seconds = seconds;
        }
    }
}
