package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.adapter.LiveRoomRecyclerViewChatAdapter;
import com.yc.liaolive.live.bean.CustomMsgInfo;
import com.yc.liaolive.live.bean.PusherInfo;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.ui.fragment.LiveUserDetailsFragment;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

/**
 * TinyHung@Outlook.com
 * 2019/1/16
 * 直播大厅会话列表
 */

public class BrightConversationListView extends FrameLayout {

    private static final String TAG = "BrightConversationListView";
    private IndexLinLayoutManager mMsgLayoutManager;
    private LiveRoomRecyclerViewChatAdapter mChatMsgListAdapter;
    private int mIdentifyType=0;//0：观众 1：主播
    private TextView mBtnMessage;
    //未读消息计数
    private int unReadMsgCount=0;
    private boolean mEnable;

    public BrightConversationListView(@NonNull Context context) {
        this(context,null);
    }

    public BrightConversationListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_bright_conversation_layout,this);
        mBtnMessage=(TextView) findViewById(R.id.view_btn_newmsg);
        mBtnMessage.getBackground().setAlpha(230);
        mBtnMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mMsgLayoutManager&&null!=mChatMsgListAdapter){
                    mBtnMessage.setVisibility(GONE);
                    mBtnMessage.setText("");
                    unReadMsgCount=0;
                    mMsgLayoutManager.smoothScrollToPosition(null,null,mChatMsgListAdapter.getItemCount()-1);
                }
            }
        });
    }

    /**
     * 初始化
     */
    public void initConversation(){
        if(null==findViewById(R.id.view_recycler_msg)) return;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.view_recycler_msg);
        //消息列表
        mMsgLayoutManager = new IndexLinLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        mMsgLayoutManager.setStackFromEnd(true);
        mMsgLayoutManager.setMsgRecyclerView(recyclerView);
        recyclerView.setLayoutManager(mMsgLayoutManager);
        mChatMsgListAdapter=new LiveRoomRecyclerViewChatAdapter(null);
        mChatMsgListAdapter.setOnItemClickListener(new LiveRoomRecyclerViewChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, CustomMsgInfo customMsgInfo) {
                if(mEnable) return;
                if(null!=customMsgInfo){
                    FansInfo fansInfo=new FansInfo();
                    fansInfo.setAvatar(customMsgInfo.getSendUserHead());
                    fansInfo.setNickname(customMsgInfo.getSendUserName());
                    fansInfo.setLevel_integral(customMsgInfo.getSendUserGradle());
                    fansInfo.setVip(customMsgInfo.getSendUserVIP());
                    fansInfo.setUserid(customMsgInfo.getSendUserID());
                    LiveUserDetailsFragment.newInstance(fansInfo,mIdentifyType, LiveRoomManager.getInstance().getLiveRoom().getCurrRoomID()).setOnFunctionClickListener(new LiveUserDetailsFragment.OnFunctionClickListener() {
                        @Override
                        public void onSendGift(FansInfo userInfo) {
                            if(null!=mConversationFunctionListener) mConversationFunctionListener.onSendGift(new PusherInfo(userInfo.getUserid(),userInfo.getNickname(),userInfo.getAvatar(),null));
                        }
                    }).show(((AppCompatActivity) getContext()).getSupportFragmentManager(),"userinfo");
                }
            }
        });
        recyclerView.setAdapter(mChatMsgListAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    if(null!=mBtnMessage&&mBtnMessage.getVisibility()==View.VISIBLE){
                        if(null!=mMsgLayoutManager&&null!=mChatMsgListAdapter){
                            int lastVisibleItemPosition = mMsgLayoutManager.findLastVisibleItemPosition();
                            if((mChatMsgListAdapter.getItemCount()-1)-lastVisibleItemPosition<=1){//unReadMsgCount:如果是小于未读消息数，则是滑动到未读消息数的第一条即不显示未读消息标记
                                unReadMsgCount=0;
                                mBtnMessage.setVisibility(GONE);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 绑定用户的身份场景
     * @param type  0：观众拉流  1：主播推流
     */
    public void setIdentityType(int type){
        this.mIdentifyType=type;
    }

    /**
     * 添加一条会话,智能滚动至底部
     * @param customMsgInfo
     * @param isSystemPro 是否来自远端推送
     */
    public void addConversation(CustomMsgInfo customMsgInfo,boolean isSystemPro){
        addConversation(customMsgInfo,isSystemPro,false);
    }

    /**
     * 添加一条会话
     * @param customMsgInfo
     * @param isSystemPro 是否来自远端推送
     * @param isFixedBottom 是否固定在底部
     */
    public void addConversation(CustomMsgInfo customMsgInfo,boolean isSystemPro,boolean isFixedBottom){
        if(null!=mChatMsgListAdapter){
            mChatMsgListAdapter.addData(customMsgInfo);
            smoothScrollToBottom(isSystemPro,isFixedBottom);
        }
    }

    /**
     * 根据用户阅读情况滚动至底部
     * @param isSystemPro 是否来自远程推送的消息
     * @param isFixedBottom 是否固定在底部
     */
    private synchronized void smoothScrollToBottom(boolean isSystemPro,boolean isFixedBottom) {
        if(null!=mMsgLayoutManager&&null!=mChatMsgListAdapter){
            if(isFixedBottom){
                 mMsgLayoutManager.scrollToPositionWithOffset(mChatMsgListAdapter.getItemCount()-1,0);
                return;
            }
            //获取最后一个可见item的位置
            int lastItemPosition = mMsgLayoutManager.findLastVisibleItemPosition();
            //本地客户端产生的消息，如果存在未读，直接全部已读并定位至屏幕底部
            if(!isSystemPro){
                //最后一条消息距离列表的最近位置大于已经显示的条目个数，这里可能因各个设备的屏幕分辨率而有差异
                if(mChatMsgListAdapter.getItemCount()-lastItemPosition>6){
                    mMsgLayoutManager.smoothScrollToPosition(null,null,mChatMsgListAdapter.getItemCount()-1);
                }else{
                    mMsgLayoutManager.scrollToPositionWithOffset(mChatMsgListAdapter.getItemCount()-1,0);
                }
                if(null!=mBtnMessage&&mBtnMessage.getVisibility()!=GONE){
                    mBtnMessage.setVisibility(GONE);
                    mBtnMessage.setText("");
                    unReadMsgCount=0;
                }
                return;
            }
            //最后一条消息不可见情况下，这里定性未用户手动滑动列表过
            if((mChatMsgListAdapter.getItemCount()-1)-lastItemPosition>1){
                unReadMsgCount++;
                if(null!=mBtnMessage&&mBtnMessage.getVisibility()!=VISIBLE){
                    mBtnMessage.setVisibility(VISIBLE);
                    mBtnMessage.setText(unReadMsgCount+"条新消息");
                }
            }else{
                mMsgLayoutManager.scrollToPositionWithOffset(mChatMsgListAdapter.getItemCount()-1,0);
                if(null!=mBtnMessage&&mBtnMessage.getVisibility()!=GONE){
                    mBtnMessage.setVisibility(GONE);
                    mBtnMessage.setText("");
                    unReadMsgCount=0;
                }
            }
        }
    }

    public void setUserCenterEnable(boolean enable) {
        this.mEnable=enable;
    }

    public interface OnConversationFunctionListener{
        void onSendGift(PusherInfo pusherInfo);
    }

    private OnConversationFunctionListener mConversationFunctionListener;

    public void setConversationFunctionListener(OnConversationFunctionListener conversationFunctionListener) {
        mConversationFunctionListener = conversationFunctionListener;
    }

    public void onResrt(){
        if(null!=mChatMsgListAdapter) mChatMsgListAdapter.setNewData(null);
    }

    public void onDestroy(){
        mMsgLayoutManager=null;mConversationFunctionListener=null;unReadMsgCount=0;mBtnMessage=null;
        if(null!=mChatMsgListAdapter){
            mChatMsgListAdapter.setNewData(null);
            mChatMsgListAdapter=null;
        }
    }
}