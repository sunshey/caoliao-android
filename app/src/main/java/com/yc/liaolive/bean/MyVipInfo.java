package com.yc.liaolive.bean;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/10 11:17.
 */
public class MyVipInfo {


    /**
     * info : {"anchor_exp":0,"avatar":"http://thirdqq.qlogo.cn/qqapp/1106846629/7F945930442E43C560FE3FC885D9682B/100","birthday":"","chat_deplete":-1,"chat_minute":-1,"create_time":"2018-06-28 10:49:35","frontcover":"http://zbtest.6071.com/uploads/images/20180701/288bc21ecc9e0d56bb780dde4355536d.jpg","id":92,"identity_audit":0,"level_integral":0,"lock_state":0,"nickname":"sdafds","occupation":"","password":"","phone":"","position":"","sex":1,"signature":"","speciality":"","state":0,"user_exp":3000,"user_type":0,"userid":"39542373","vip":1,"white_list":0}
     * vips : [{"desp":"","icon":"http://zb.6071.com/uploads/diamond_light_yellow.png","id":1,"level":1,"name":"v1","privilege":[{"addtime":0,"desp":"尊贵头衔","icon":"http://zb.6071.com/uploads/vip/honourable_title.png","id":5,"title":"尊贵头衔","type":1},{"addtime":0,"desp":"排名靠前","icon":"http://zb.6071.com/uploads/vip/ranking_front.png","id":6,"title":"排名靠前","type":1},{"addtime":0,"desp":"红色特权","icon":"http://zb.6071.com/uploads/vip/red_right.png","id":7,"title":"红色特权","type":1}],"sort":0},{"desp":"","icon":"http://zb.6071.com/uploads/diamond_yellow.png","id":2,"level":2,"name":"v2","privilege":[{"addtime":0,"desp":"尊贵头衔","icon":"http://zb.6071.com/uploads/vip/honourable_title.png","id":5,"title":"尊贵头衔","type":1},{"addtime":0,"desp":"排名靠前","icon":"http://zb.6071.com/uploads/vip/ranking_front.png","id":6,"title":"排名靠前","type":1},{"addtime":0,"desp":"红色特权","icon":"http://zb.6071.com/uploads/vip/red_right.png","id":7,"title":"红色特权","type":1}],"sort":0},{"desp":"","icon":"http://zb.6071.com/uploads/diamond_light_red.png","id":3,"level":3,"name":"v3","privilege":[{"addtime":0,"desp":"尊贵头衔","icon":"http://zb.6071.com/uploads/vip/honourable_title.png","id":5,"title":"尊贵头衔","type":1},{"addtime":0,"desp":"排名靠前","icon":"http://zb.6071.com/uploads/vip/ranking_front.png","id":6,"title":"排名靠前","type":1},{"addtime":0,"desp":"红色特权","icon":"http://zb.6071.com/uploads/vip/red_right.png","id":7,"title":"红色特权","type":1}],"sort":0},{"desp":"","icon":"http://zb.6071.com/uploads/diamond_red.png","id":4,"level":4,"name":"v4","privilege":[{"addtime":0,"desp":"尊贵头衔","icon":"http://zb.6071.com/uploads/vip/honourable_title.png","id":5,"title":"尊贵头衔","type":1},{"addtime":0,"desp":"排名靠前","icon":"http://zb.6071.com/uploads/vip/ranking_front.png","id":6,"title":"排名靠前","type":1},{"addtime":0,"desp":"红色特权","icon":"http://zb.6071.com/uploads/vip/red_right.png","id":7,"title":"红色特权","type":1}],"sort":0},{"desp":"","icon":"http://zb.6071.com/uploads/diamond_violet.png","id":5,"level":5,"name":"v5","privilege":[{"addtime":0,"desp":"尊贵头衔","icon":"http://zb.6071.com/uploads/vip/honourable_title.png","id":5,"title":"尊贵头衔","type":1},{"addtime":0,"desp":"排名靠前","icon":"http://zb.6071.com/uploads/vip/ranking_front.png","id":6,"title":"排名靠前","type":1},{"addtime":0,"desp":"红色特权","icon":"http://zb.6071.com/uploads/vip/red_right.png","id":7,"title":"红色特权","type":1}],"sort":0}]
     */

    private InfoBean info;
    private List<VipsBean> vips;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public List<VipsBean> getVips() {
        return vips;
    }

    public void setVips(List<VipsBean> vips) {
        this.vips = vips;
    }

    public  class InfoBean {
        /**
         * anchor_exp : 0
         * avatar : http://thirdqq.qlogo.cn/qqapp/1106846629/7F945930442E43C560FE3FC885D9682B/100
         * birthday :
         * chat_deplete : -1
         * chat_minute : -1
         * create_time : 2018-06-28 10:49:35
         * frontcover : http://zbtest.6071.com/uploads/images/20180701/288bc21ecc9e0d56bb780dde4355536d.jpg
         * id : 92
         * identity_audit : 0
         * level_integral : 0
         * lock_state : 0
         * nickname : sdafds
         * occupation :
         * password :
         * phone :
         * position :
         * sex : 1
         * signature :
         * speciality :
         * state : 0
         * user_exp : 3000
         * user_type : 0
         * userid : 39542373
         * vip : 1
         * white_list : 0
         */

        private int anchor_exp;
        private String avatar;
        private String birthday;
        private int chat_deplete;
        private int chat_minute;
        private String create_time;
        private String frontcover;
        private int id;
        private int identity_audit;
        private int level_integral;
        private int lock_state;
        private String nickname;
        private String occupation;
        private String password;
        private String phone;
        private String position;
        private int sex;
        private String signature;
        private String speciality;
        private int state;
        private int user_exp;
        private int user_type;
        private String userid;
        private int vip;
        private int white_list;

        public int getAnchor_exp() {
            return anchor_exp;
        }

        public void setAnchor_exp(int anchor_exp) {
            this.anchor_exp = anchor_exp;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getChat_deplete() {
            return chat_deplete;
        }

        public void setChat_deplete(int chat_deplete) {
            this.chat_deplete = chat_deplete;
        }

        public int getChat_minute() {
            return chat_minute;
        }

        public void setChat_minute(int chat_minute) {
            this.chat_minute = chat_minute;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getFrontcover() {
            return frontcover;
        }

        public void setFrontcover(String frontcover) {
            this.frontcover = frontcover;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIdentity_audit() {
            return identity_audit;
        }

        public void setIdentity_audit(int identity_audit) {
            this.identity_audit = identity_audit;
        }

        public int getLevel_integral() {
            return level_integral;
        }

        public void setLevel_integral(int level_integral) {
            this.level_integral = level_integral;
        }

        public int getLock_state() {
            return lock_state;
        }

        public void setLock_state(int lock_state) {
            this.lock_state = lock_state;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getSpeciality() {
            return speciality;
        }

        public void setSpeciality(String speciality) {
            this.speciality = speciality;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getUser_exp() {
            return user_exp;
        }

        public void setUser_exp(int user_exp) {
            this.user_exp = user_exp;
        }

        public int getUser_type() {
            return user_type;
        }

        public void setUser_type(int user_type) {
            this.user_type = user_type;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public int getVip() {
            return vip;
        }

        public void setVip(int vip) {
            this.vip = vip;
        }

        public int getWhite_list() {
            return white_list;
        }

        public void setWhite_list(int white_list) {
            this.white_list = white_list;
        }
    }

    public class VipsBean {
        /**
         * desp :
         * icon : http://zb.6071.com/uploads/diamond_light_yellow.png
         * id : 1
         * level : 1
         * name : v1
         * privilege : [{"addtime":0,"desp":"尊贵头衔","icon":"http://zb.6071.com/uploads/vip/honourable_title.png","id":5,"title":"尊贵头衔","type":1},{"addtime":0,"desp":"排名靠前","icon":"http://zb.6071.com/uploads/vip/ranking_front.png","id":6,"title":"排名靠前","type":1},{"addtime":0,"desp":"红色特权","icon":"http://zb.6071.com/uploads/vip/red_right.png","id":7,"title":"红色特权","type":1}]
         * sort : 0
         */

        private String desp;
        private String icon;
        private int id;
        private int level;
        private String name;
        private int sort;
        private List<VipInfoBean> privilege;

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

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public List<VipInfoBean> getPrivilege() {
            return privilege;
        }

        public void setPrivilege(List<VipInfoBean> privilege) {
            this.privilege = privilege;
        }


    }

}
