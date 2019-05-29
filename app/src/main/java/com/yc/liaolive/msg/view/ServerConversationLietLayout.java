package com.yc.liaolive.msg.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.TabMineUserInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.observer.SubjectObservable;
import com.yc.liaolive.util.SharedPreferencesUtil;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/3/29
 * 客服会话,精简版
 */

public class ServerConversationLietLayout extends LinearLayout implements Observer {

    private TextView mUnReadNum;

    public ServerConversationLietLayout(Context context) {
        this(context,null);
    }

    public ServerConversationLietLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_server_conversation_lite_layout,this);
        ApplicationManager.getInstance().addObserver(this);
        mUnReadNum = (TextView) findViewById(R.id.view_more_title);
        updataUnReadCount();
    }

    /**
     * 设置客服资料
     * @param itemData
     */
    public void setItemData(final TabMineUserInfo itemData) {
        if(null==itemData) return;
        ImageView iconImage = (ImageView) findViewById(R.id.view_item_icon);
        ((TextView) findViewById(R.id.view_item_title)).setText(itemData.getTitle());
        Glide.with(getContext())
                .load(itemData.getIcon())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(iconImage);
    }

    /**
     * 更新未读消息数量
     */
    public void updataUnReadCount(){
        int unReadCount = SharedPreferencesUtil.getInstance().getInt(Constant.KET_SERVER_MSG_COUNT, 0);
        long lastMessageTime = SharedPreferencesUtil.getInstance().getLong(Constant.KET_SERVER_MSG_TIME, System.currentTimeMillis());
        if(null!=mUnReadNum){
            if(unReadCount<=0){
                mUnReadNum.setText("");
                mUnReadNum.setBackgroundResource(0);
                return;
            }
            mUnReadNum.setBackground(getContext().getResources().getDrawable(R.drawable.arl_round_red_dot));
            mUnReadNum.setText(String.valueOf(unReadCount));
        }
    }

    /**
     * 监听未读消息、最后一条消息等
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof SubjectObservable){
            if(null!=arg && arg instanceof String && ((String) arg).equals(Constant.OBSERVER_CMD_MSG_UNREAD_COUND)){
                updataUnReadCount();
            }
        }
    }

    public void onDestroy(){
        ApplicationManager.getInstance().removeObserver(this);
        mUnReadNum=null;
    }
}