package com.yc.liaolive.msg.model.bean;

import android.content.Context;

import com.tencent.TIMConversationType;
import com.yc.liaolive.base.adapter.entity.MultiItemEntity;
import com.yc.liaolive.user.manager.UserManager;

/**
 * 会话数据
 */
public abstract class Conversation implements Comparable, MultiItemEntity {

    protected static final String TAG = "Conversation";
    //会话对象id
    protected String identify;

    //会话类型
    protected TIMConversationType type;

    //会话对象名称
    protected String name;

    //会话对象头像
    protected String avatar;

    protected int itemType=0;//消息类型0：普通会话消息 1：自定义客服会话消息

    /**
     * 获取最后一条消息的时间
     */
    abstract public long getLastMessageTime();

    /**
     * 获取未读消息数量
     */
    abstract public long getUnreadNum();


    /**
     * 将所有消息标记为已读
     */
    abstract public void readAllMessage();


    /**
     * 获取头像
     */
    abstract public String getAvatar();

    /**
     * 判断当前最后一条消息是否是直播消息
     */
    abstract public boolean isLive();

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    abstract public void navToDetail(Context context);

    /**
     * 获取最后一条消息摘要
     */
    abstract public String getLastMessageSummary();

    /**
     * 获取名称
     */
    abstract public String getName();


    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public int getItemType() {
        if(null!=identify&&identify.equals(UserManager.getInstance().getServerIdentify())){
            itemType=1;
        }
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        if (!identify.equals(that.identify)) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = identify.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }


    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Object another) {
        switch (itemType) {
            case 0:
                if (another instanceof Conversation) {
                    Conversation anotherConversation = (Conversation) another;
                    long timeGap = anotherConversation.getLastMessageTime() - getLastMessageTime();
                    if (timeGap > 0) return 1;
                    else if (timeGap < 0) return -1;
                    return 0;
                } else {
                    throw new ClassCastException();
                }
                //自定义客服消息优先级最高，置顶显示
            case 1:
                return 1;
        }
        return 0;
    }
}
