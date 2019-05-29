package com.yc.liaolive.msg.model;

import android.content.Context;

import com.tencent.TIMMessage;
import com.yc.liaolive.msg.adapter.ChatAdapter;

/**
 * 非Vip 购买提示消息数据
 */
public class VipMessage extends Message {

    private static final String TAG = "VipMessage";

    public VipMessage(TIMMessage message){
        this.message = message;
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {

    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return "";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }
}
