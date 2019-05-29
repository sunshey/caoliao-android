package com.yc.liaolive.msg.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.TIMConversationType;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.msg.ui.activity.ChatActivity;
import com.yc.liaolive.observer.SubjectObservable;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.util.TimeUtil;
import com.yc.liaolive.view.CircleImageView;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/12/20
 * 客服会话
 */

public class ServerConversationLayout extends LinearLayout implements Observer {

    private TextView mUnReadNum;
    private TextView mLastMessage;
    private TextView mMessageTime;

    public ServerConversationLayout(Context context) {
        this(context,null);
    }

    public ServerConversationLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        android.view.View.inflate(context, R.layout.view_server_conversation_layout,this);
        ApplicationManager.getInstance().addObserver(this);
        mUnReadNum = (TextView) findViewById(R.id.view_unread_num);
        mLastMessage = (TextView) findViewById(R.id.view_last_message);
        mMessageTime = (TextView) findViewById(R.id.view_message_time);
        updataUnReadCount();
    }

    /**
     * 设置客服资料
     * @param fansInfo
     */
    public void setServerUserData(final FansInfo fansInfo) {
        if(null==fansInfo) return;
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.view_item_icon);
        //客服头像
        Glide.with(getContext())
                .load(fansInfo.getAvatar())
                .dontAnimate()
                .placeholder(circleImageView.getDrawable())
                .error(R.drawable.ic_chat_server)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .skipMemoryCache(true)
                .into(circleImageView);
        ((TextView) findViewById(R.id.view_item_name)).setText(fansInfo.getNickname());
        ((TextView) findViewById(R.id.view_item_tag)).setText("官方");
        if(null!=mLastMessage) mLastMessage.setText(fansInfo.getDesp());
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.navToChat(getContext(), UserManager.getInstance().getServerIdentify(), fansInfo.getNickname(),TIMConversationType.C2C);
            }
        });
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
        //消息接收时间
        if(null!=mMessageTime){
            mMessageTime.setText(TimeUtil.getTimeStr(lastMessageTime));
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
        mUnReadNum=null;mLastMessage=null;
    }
}