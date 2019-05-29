package com.yc.liaolive.bean;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 */

public class TaskInfo {

    public static final int TASK_ACTION_MODIFY_NAME=8;//修改昵称
    public static final int TASK_ACTION_BIND_PHONE=9;//绑定手机号
    public static final int TASK_ACTION_BIND_QQ=11;//绑定QQ
    public static final int TASK_ACTION_BIND_WEXIN=10;//绑定微信
    public static final int TASK_ACTION_LOOK_LIVE=3;//观看直播
    public static final int TASK_ACTION_SEND_GIFT=5;//赠送礼物
    public static final int TASK_ACTION_SHARE=7;//分享
    public static final int TASK_ACTION_LOGIN=4;

    /**
     * addtime : 1528960389
     * coin : 0
     * count_num : 60
     * current_num : 60
     * desp : 累计观看60分钟直播领奖励
     * exp : 60
     * id : 3
     * interval_time : 86400
     * is_get : 0
     * name : 看直播
     * src : http://zb.6071.com/uploads/task/lALPBbCc1hW0XuxkaQ_105_100.png_620x10000q90g.jpg
     */
    private int itemID;//本地的ITEM id
    private int icon;//默认的icon
    private int addtime;
    private int coin;
    private int count_num;
    private int current_num;
    private String desp;
    private int exp;
    private int id;
    private int app_id;
    private int interval_time;
    private int is_get;
    private String name;
    private String but_title;//按钮标题
    private String src;
    private String activity;//动作意图
    private int complete;//是否已经领取过了
    private int automatic;
    private String type;//任务类分类类别
    private boolean lastPosition;//是否是该分类下的最后一个
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(boolean lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getCount_num() {
        return count_num;
    }

    public void setCount_num(int count_num) {
        this.count_num = count_num;
    }

    public int getCurrent_num() {
        return current_num;
    }

    public void setCurrent_num(int current_num) {
        this.current_num = current_num;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(int interval_time) {
        this.interval_time = interval_time;
    }

    public int getIs_get() {
        return is_get;
    }

    public void setIs_get(int is_get) {
        this.is_get = is_get;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public String getBut_title() {
        return but_title;
    }

    public void setBut_title(String but_title) {
        this.but_title = but_title;
    }

    public int getAutomatic() {
        return automatic;
    }

    public void setAutomatic(int automatic) {
        this.automatic = automatic;
    }
}
