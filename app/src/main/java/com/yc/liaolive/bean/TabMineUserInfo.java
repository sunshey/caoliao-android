package com.yc.liaolive.bean;

import android.text.TextUtils;
import com.yc.liaolive.base.adapter.entity.MultiItemEntity;

/**
 * TinyHung@Outlook.com
 * 2018/5/29
 * 首页-个人中心列表
 */

public class TabMineUserInfo implements MultiItemEntity {

    public static final int ITEM_DEFAULT = 0;
    public static final int ITEM_1 = 1;
    public static final int ITEM_2 = 2;
    public static final int ITEM_3 = 3;//小额贷SDK等
    public static final int ITEM_5 = 5;//客服、会话

    /**
     * icon : http://t.tn990.com/upload/friends-check.png
     * title : 我的钱包
     * sub_title :
     * show_line : 0
     * type : 0
     * jump_url : com.yc.liaolive.user.ui.NotecaseActivity
     */

    private String icon;
    private String title;
    private String sub_title;//副标题
    private String show_line;//是否显示下划线
    private String type;//1：原生 2：本地开关设置（在线勿扰） 3:小额贷sdk ,4：网页
    private String jump_url;
    //客户端定义
    private String quite="1";
    private String identity_audit;

    public String getIdentity_audit() {
        return identity_audit;
    }

    public void setIdentity_audit(String identity_audit) {
        this.identity_audit = identity_audit;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getShow_line() {
        return show_line;
    }

    public void setShow_line(String show_line) {
        this.show_line = show_line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getQuite() {
        return quite;
    }

    public void setQuite(String quite) {
        this.quite = quite;
    }

    @Override
    public int getItemType() {
        if(TextUtils.isEmpty(type)){
            return ITEM_DEFAULT;
        }
        if(type.equals("2")){
            return ITEM_2;
        }
        if(type.equals("3")){
            return ITEM_3;
        }
        if(type.equals("5")){
            return ITEM_5;
        }
        return ITEM_1;
    }
}
