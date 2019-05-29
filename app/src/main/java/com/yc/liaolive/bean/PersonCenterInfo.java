package com.yc.liaolive.bean;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/9 16:04.
 */
public class PersonCenterInfo{

    /**
     * id : 93
     * userid : 44728079
     * password :
     * nickname : 格鲁特
     * sex : null
     * avatar : http://zbtest.6071.com/uploads/images/20180707/dab29af9d85097ccedd6e6b9ab8ff422.jpg
     * frontcover : http://zb.6071.com/uploads/20180629/e86296ea09c955e9f899b749a17c294b.jpg
     * create_time : 2018-06-28 10:52:04
     * state : 0
     * lock_state : 0
     * phone : 18872922735
     * level_integral : 0
     * vip : 1
     * white_list : 0
     * user_type : 0
     * birthday :
     * signature :
     * identity_audit : 0
     * occupation :
     * speciality :
     * position :
     * user_exp : 6000
     * anchor_exp : 0
     * chat_deplete : 5000
     * chat_minute : 1
     * pintai_coin : 1000000
     * rmb_coin : 980400
     * pintai_money : 0
     * rmb_money : 25200
     * pintai_coin_total : 0
     * rmb_coin_total : 0
     * pintai_money_total : 0
     * rmb_money_total : 0
     * addtime : 0
     * edittime : 0
     * is_online : 0
     * fans_number : 4
     * attent_number : 0
     * params : ["44728079","44728079"]
     * is_zhima : 0
     */


    private int id;
    private String userid;
    private String password;
    private String nickname;
    private int sex;
    private String avatar;
    private String frontcover;
    private String create_time;
    private int state;
    private int lock_state;
    private String phone;
    private int level_integral;
    private int vip;
    private int white_list;
    private int user_type;//用户类型 0普通用户 1主播 2机器人 3游客 4客服
    private String birthday;
    private String signature;
    private int identity_audit;
    private String occupation;
    private String speciality;
    private String position;
    private long user_exp;
    private long anchor_exp;
    private int chat_deplete;
    private int chat_minute;
    private long pintai_coin;
    private long rmb_coin;
    private long pintai_money;
    private long rmb_money;
    private long pintai_coin_total;
    private long rmb_coin_total;
    private long pintai_money_total;
    private long rmb_money_total;
    private long addtime;
    private long edittime;
    private int is_online;
    private int fans_number;
    private int attent_number;
    private long points;//用户积分
    private List<String> params;
    private long consume_coin;//消费
    private long consume_point;//积分
    private String money;
    private int robot;
    private long vip_end_time;
    private long vip_start_time;
    private int wawa;
    private int image_count;//第二人称私密照片
    private int video_count;//第二人称私密视频
    private int my_image_count;//第一人称私密照片
    private int my_video_count;//第一人称私密视频
    private int image_max_length;//最大的图片文件上传数量
    private String user_state;//用户状态 offline 离线、live 正在直播、 videocall 视频聊天、disturbed  防止打扰、 free 空闲
    private String height;
    private String label;
    private boolean online;
    private int send_chat;
    private String star;
    private String weight;
    private int quite;
    private int is_attention;//是否已关注 0：未关注 1：已关注
    private int is_white;//是否是白名单用户
    private String telephone_rate_url; //充值活动链接
    private int chat_time;//主播已经视频通话的时长
    //用户的相册
    private List<PrivateMedia> image_list;
    //用户的视频
    private List<PrivateMedia> video_list;
    //亲密度
    private List<FansInfo> points_list;
    //是否认证成功
    private int is_zhima;//0：未认证 1：认证成功

    private String user_seting_ad;//用户广告

    public PersonCenterInfo(){

    }

    public String getTelephone_rate_url() {
        return telephone_rate_url;
    }

    public void setTelephone_rate_url(String telephone_rate_url) {
        this.telephone_rate_url = telephone_rate_url;
    }

    public int getIs_white() {
        return is_white;
    }

    public void setIs_white(int is_white) {
        this.is_white = is_white;
    }


    public List<FansInfo> getPoints_list() {
        return points_list;
    }

    public void setPoints_list(List<FansInfo> points_list) {
        this.points_list = points_list;
    }

    public String getUser_state() {
        return user_state;
    }

    public void setUser_state(String user_state) {
        this.user_state = user_state;
    }

    public int getImage_max_length() {
        return image_max_length;
    }

    public void setImage_max_length(int image_max_length) {
        this.image_max_length = image_max_length;
    }

    public List<PrivateMedia> getImage_list() {
        return image_list;
    }

    public void setImage_list(List<PrivateMedia> image_list) {
        this.image_list = image_list;
    }

    public List<PrivateMedia> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<PrivateMedia> video_list) {
        this.video_list = video_list;
    }

    public int getImage_count() {
        return image_count;
    }

    public void setImage_count(int image_count) {
        this.image_count = image_count;
    }

    public int getVideo_count() {
        return video_count;
    }

    public void setVideo_count(int video_count) {
        this.video_count = video_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFrontcover() {
        return frontcover;
    }

    public void setFrontcover(String frontcover) {
        this.frontcover = frontcover;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getLock_state() {
        return lock_state;
    }

    public void setLock_state(int lock_state) {
        this.lock_state = lock_state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLevel_integral() {
        return level_integral;
    }

    public void setLevel_integral(int level_integral) {
        this.level_integral = level_integral;
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

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(int identity_audit) {
        this.identity_audit = identity_audit;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public long getUser_exp() {
        return user_exp;
    }

    public void setUser_exp(long user_exp) {
        this.user_exp = user_exp;
    }

    public long getAnchor_exp() {
        return anchor_exp;
    }

    public void setAnchor_exp(long anchor_exp) {
        this.anchor_exp = anchor_exp;
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

    public long getPintai_coin() {
        return pintai_coin;
    }

    public void setPintai_coin(long pintai_coin) {
        this.pintai_coin = pintai_coin;
    }

    public long getRmb_coin() {
        return rmb_coin;
    }

    public void setRmb_coin(long rmb_coin) {
        this.rmb_coin = rmb_coin;
    }

    public long getPintai_money() {
        return pintai_money;
    }

    public void setPintai_money(long pintai_money) {
        this.pintai_money = pintai_money;
    }

    public long getRmb_money() {
        return rmb_money;
    }

    public void setRmb_money(long rmb_money) {
        this.rmb_money = rmb_money;
    }

    public long getPintai_coin_total() {
        return pintai_coin_total;
    }

    public void setPintai_coin_total(long pintai_coin_total) {
        this.pintai_coin_total = pintai_coin_total;
    }

    public long getRmb_coin_total() {
        return rmb_coin_total;
    }

    public void setRmb_coin_total(long rmb_coin_total) {
        this.rmb_coin_total = rmb_coin_total;
    }

    public long getPintai_money_total() {
        return pintai_money_total;
    }

    public void setPintai_money_total(long pintai_money_total) {
        this.pintai_money_total = pintai_money_total;
    }

    public long getRmb_money_total() {
        return rmb_money_total;
    }

    public void setRmb_money_total(long rmb_money_total) {
        this.rmb_money_total = rmb_money_total;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getEdittime() {
        return edittime;
    }

    public void setEdittime(long edittime) {
        this.edittime = edittime;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public int getFans_number() {
        return fans_number;
    }

    public void setFans_number(int fans_number) {
        this.fans_number = fans_number;
    }

    public int getAttent_number() {
        return attent_number;
    }

    public void setAttent_number(int attent_number) {
        this.attent_number = attent_number;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public long getConsume_coin() {
        return consume_coin;
    }

    public void setConsume_coin(long consume_coin) {
        this.consume_coin = consume_coin;
    }

    public long getConsume_point() {
        return consume_point;
    }

    public void setConsume_point(long consume_point) {
        this.consume_point = consume_point;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getRobot() {
        return robot;
    }

    public void setRobot(int robot) {
        this.robot = robot;
    }

    public long getVip_end_time() {
        return vip_end_time;
    }

    public void setVip_end_time(long vip_end_time) {
        this.vip_end_time = vip_end_time;
    }

    public long getVip_start_time() {
        return vip_start_time;
    }

    public void setVip_start_time(long vip_start_time) {
        this.vip_start_time = vip_start_time;
    }

    public int getWawa() {
        return wawa;
    }

    public void setWawa(int wawa) {
        this.wawa = wawa;
    }

    public int getMy_image_count() {
        return my_image_count;
    }

    public void setMy_image_count(int my_image_count) {
        this.my_image_count = my_image_count;
    }

    public int getMy_video_count() {
        return my_video_count;
    }

    public void setMy_video_count(int my_video_count) {
        this.my_video_count = my_video_count;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getSend_chat() {
        return send_chat;
    }

    public void setSend_chat(int send_chat) {
        this.send_chat = send_chat;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getQuite() {
        return quite;
    }

    public void setQuite(int quite) {
        this.quite = quite;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    public int getChat_time() {
        return chat_time;
    }

    public void setChat_time(int chat_time) {
        this.chat_time = chat_time;
    }

    public int getIs_zhima() {
        return is_zhima;
    }

    public void setIs_zhima(int is_zhima) {
        this.is_zhima = is_zhima;
    }

    public String getUser_seting_ad() {
        return user_seting_ad;
    }

    public void setUser_seting_ad(String user_seting_ad) {
        this.user_seting_ad = user_seting_ad;
    }
}