package com.yc.liaolive.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.yc.liaolive.recharge.model.bean.VipRechargePoppupBean;

/**
 * 用户数据
 */
public class UserInfo implements Parcelable {

    //是否已经实名认证
    private int authentication;
    private int id;
    private String userid;
    private String roomID;
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
    private int user_type;
    private long pintai_coin;
    private long rmb_coin;
    private long pintai_money;
    private long rmb_money;
    private int chat_deplete;
    private int chat_minute;
    private int addtime;
    private int edittime;
    private long pintai_coin_total;
    private long pintai_money_total;
    private long rmb_coin_total;
    private long rmb_money_total;
    private String position;
    private double distance;
    private double latitude;
    private double longitude;
    private int identity_audit;//认证状态 0：未认证 1：待审核 2：审核通过 3：审核失败
    private String province;
    private String signature;//个性签名
    private int anchor_exp;
    private String birthday;
    private String occupation;
    private String password;
    private String speciality;
    private int user_exp;
    private String city;
    private int attent_number;
    private long consume_coin;
    private long consume_point;
    private int fans_number;
    private int is_online;
    private String money;
    private int robot;
    private long vip_end_time;
    private long vip_start_time;
    private int wawa;
    //用户是否购买了话费充值vip 0：未购买 1：已购买
    private int vip_phone = 1;
    private int quite;//勿扰：0：关闭 1：开启
    private int is_zhima;//是否已完成芝麻认证 0：未认证 1：已成功认证

    //用户领取话费弹窗
    private VipRechargePoppupBean popup_page;

    public UserInfo(){
        super();
    }

    protected UserInfo(Parcel in) {
        authentication = in.readInt();
        id = in.readInt();
        userid = in.readString();
        roomID = in.readString();
        nickname = in.readString();
        sex = in.readInt();
        avatar = in.readString();
        frontcover = in.readString();
        create_time = in.readString();
        state = in.readInt();
        lock_state = in.readInt();
        phone = in.readString();
        level_integral = in.readInt();
        vip = in.readInt();
        white_list = in.readInt();
        user_type = in.readInt();
        pintai_coin = in.readLong();
        rmb_coin = in.readLong();
        pintai_money = in.readLong();
        rmb_money = in.readLong();
        chat_deplete = in.readInt();
        chat_minute = in.readInt();
        addtime = in.readInt();
        edittime = in.readInt();
        pintai_coin_total = in.readLong();
        pintai_money_total = in.readLong();
        rmb_coin_total = in.readLong();
        rmb_money_total = in.readLong();
        position = in.readString();
        distance = in.readDouble();
        latitude = in.readDouble();
        longitude = in.readDouble();
        identity_audit = in.readInt();
        province = in.readString();
        signature = in.readString();
        anchor_exp = in.readInt();
        birthday = in.readString();
        occupation = in.readString();
        password = in.readString();
        speciality = in.readString();
        user_exp = in.readInt();
        city = in.readString();
        attent_number = in.readInt();
        consume_coin = in.readLong();
        consume_point = in.readLong();
        fans_number = in.readInt();
        is_online = in.readInt();
        money = in.readString();
        robot = in.readInt();
        vip_end_time = in.readLong();
        vip_start_time = in.readLong();
        wawa = in.readInt();
        vip_phone = in.readInt();
        popup_page = in.readParcelable(VipRechargePoppupBean.class.getClassLoader());
        quite = in.readInt();
        is_zhima = in.readInt();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public int getAuthentication() {
        return authentication;
    }

    public void setAuthentication(int authentication) {
        this.authentication = authentication;
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

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
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

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getEdittime() {
        return edittime;
    }

    public void setEdittime(int edittime) {
        this.edittime = edittime;
    }

    public long getPintai_coin_total() {
        return pintai_coin_total;
    }

    public void setPintai_coin_total(long pintai_coin_total) {
        this.pintai_coin_total = pintai_coin_total;
    }

    public long getPintai_money_total() {
        return pintai_money_total;
    }

    public void setPintai_money_total(long pintai_money_total) {
        this.pintai_money_total = pintai_money_total;
    }

    public long getRmb_coin_total() {
        return rmb_coin_total;
    }

    public void setRmb_coin_total(long rmb_coin_total) {
        this.rmb_coin_total = rmb_coin_total;
    }

    public long getRmb_money_total() {
        return rmb_money_total;
    }

    public void setRmb_money_total(long rmb_money_total) {
        this.rmb_money_total = rmb_money_total;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(int identity_audit) {
        this.identity_audit = identity_audit;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getAnchor_exp() {
        return anchor_exp;
    }

    public void setAnchor_exp(int anchor_exp) {
        this.anchor_exp = anchor_exp;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public int getUser_exp() {
        return user_exp;
    }

    public void setUser_exp(int user_exp) {
        this.user_exp = user_exp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAttent_number() {
        return attent_number;
    }

    public void setAttent_number(int attent_number) {
        this.attent_number = attent_number;
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

    public int getFans_number() {
        return fans_number;
    }

    public void setFans_number(int fans_number) {
        this.fans_number = fans_number;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
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

    public int getVip_phone() {
        return vip_phone;
    }

    public void setVip_phone(int vip_phone) {
        this.vip_phone = vip_phone;
    }

    public VipRechargePoppupBean getPopup_page() {
        return popup_page;
    }

    public void setPopup_page(VipRechargePoppupBean popup_page) {
        this.popup_page = popup_page;
    }

    public int getQuite() {
        return quite;
    }

    public void setQuite(int quite) {
        this.quite = quite;
    }

    public int getIs_zhima() {
        return is_zhima;
    }

    public void setIs_zhima(int is_zhima) {
        this.is_zhima = is_zhima;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(authentication);
        dest.writeInt(id);
        dest.writeString(userid);
        dest.writeString(roomID);
        dest.writeString(nickname);
        dest.writeInt(sex);
        dest.writeString(avatar);
        dest.writeString(frontcover);
        dest.writeString(create_time);
        dest.writeInt(state);
        dest.writeInt(lock_state);
        dest.writeString(phone);
        dest.writeInt(level_integral);
        dest.writeInt(vip);
        dest.writeInt(white_list);
        dest.writeInt(user_type);
        dest.writeLong(pintai_coin);
        dest.writeLong(rmb_coin);
        dest.writeLong(pintai_money);
        dest.writeLong(rmb_money);
        dest.writeInt(chat_deplete);
        dest.writeInt(chat_minute);
        dest.writeInt(addtime);
        dest.writeInt(edittime);
        dest.writeLong(pintai_coin_total);
        dest.writeLong(pintai_money_total);
        dest.writeLong(rmb_coin_total);
        dest.writeLong(rmb_money_total);
        dest.writeString(position);
        dest.writeDouble(distance);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(identity_audit);
        dest.writeString(province);
        dest.writeString(signature);
        dest.writeInt(anchor_exp);
        dest.writeString(birthday);
        dest.writeString(occupation);
        dest.writeString(password);
        dest.writeString(speciality);
        dest.writeInt(user_exp);
        dest.writeString(city);
        dest.writeInt(attent_number);
        dest.writeLong(consume_coin);
        dest.writeLong(consume_point);
        dest.writeInt(fans_number);
        dest.writeInt(is_online);
        dest.writeString(money);
        dest.writeInt(robot);
        dest.writeLong(vip_end_time);
        dest.writeLong(vip_start_time);
        dest.writeInt(wawa);
        dest.writeInt(vip_phone);
        dest.writeParcelable(popup_page, flags);
        dest.writeInt(quite);
        dest.writeInt(is_zhima);
    }
}