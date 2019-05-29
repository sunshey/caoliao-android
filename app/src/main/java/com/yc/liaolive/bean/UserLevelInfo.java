package com.yc.liaolive.bean;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/8 10:13.
 */
public class UserLevelInfo {


    /**
     * avatar : http://zbtest.6071.com/uploads/images/20180707/dab29af9d85097ccedd6e6b9ab8ff422.jpg
     * exp : 6000
     * icon : http://zb.6071.com/uploads/diamond_light_yellow.png
     * level : 3
     * name : level_3
     * next_exp : 16000
     * page : {"content":"1.观看直播可以获得经验值 2.在直播间发言,获得经验值 3.分享主播到微信、QQ、微博等社交平台获取经验值 4.赠送礼物、升级VIP是涨经验最快的方式","title":"如何升级"}
     * rank_privilege : [{"addtime":0,"desp":"敬请期待","icon":"http://zb.6071.com/uploads/more_right.png","id":4,"title":"更多特权"},{"addtime":0,"desp":"越来越土豪","icon":"http://zb.6071.com/uploads/come_hint.png","id":3,"title":"进场提示"},{"addtime":0,"desp":"越来越尊贵","icon":"http://zb.6071.com/uploads/level_flag.png","id":2,"title":"等级标识"},{"addtime":0,"desp":"越高越靠前","icon":"http://zb.6071.com/uploads/order_front.png","id":1,"title":"排名靠前"}]
     * userid : 44728079
     */

    private String avatar;
    private int exp;
    private String icon;
    private int level;
    private String name;
    private int next_exp;
    private PageBean page;
    private String userid;
    private List<RankPrivilegeBean> rank_privilege;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNext_exp() {
        return next_exp;
    }

    public void setNext_exp(int next_exp) {
        this.next_exp = next_exp;
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<RankPrivilegeBean> getRank_privilege() {
        return rank_privilege;
    }

    public void setRank_privilege(List<RankPrivilegeBean> rank_privilege) {
        this.rank_privilege = rank_privilege;
    }

    public class PageBean {
        /**
         * content : 1.观看直播可以获得经验值 2.在直播间发言,获得经验值 3.分享主播到微信、QQ、微博等社交平台获取经验值 4.赠送礼物、升级VIP是涨经验最快的方式
         * title : 如何升级
         */

        private String content;
        private String title;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public class RankPrivilegeBean {
        /**
         * addtime : 0
         * desp : 敬请期待
         * icon : http://zb.6071.com/uploads/more_right.png
         * id : 4
         * title : 更多特权
         */

        private int addtime;
        private String desp;
        private String icon;
        private int id;
        private String title;

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public String getDesp() {
            return desp;
        }

        public void setDesp(String desp) {
            this.desp = desp;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
