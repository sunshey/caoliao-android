package com.yc.liaolive.msg.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.msg.view.ServerConversationLayout;
import com.yc.liaolive.user.manager.UserManager;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 主页消息头部适配器
 */

public class IndexMsgHeaderAdapter extends BaseMultiItemQuickAdapter<CallMessageInfo,BaseViewHolder> {

    public static final int ITEM_TYPE_DEFAULT=0;
    public static final int ITEM_TYPE_SERVER=1;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     * @param data A new list is created out of this one to avoid mutable list
     */
    public IndexMsgHeaderAdapter(List<CallMessageInfo> data) {
        super(data);
        addItemType(ITEM_TYPE_DEFAULT,R.layout.re_index_msg_head_item);
        addItemType(ITEM_TYPE_SERVER,R.layout.list_item_server_conversation);
    }

    @Override
    protected void convert(BaseViewHolder helper, CallMessageInfo item) {
        if(null!=item){
            switch (item.getItemType()) {
                case ITEM_TYPE_DEFAULT:
                    setDefaultItem(helper,item);
                    break;
                case ITEM_TYPE_SERVER:
                    setServerItem(helper,item);
                    break;
            }
        }
    }

    /**
     * 默认的条目
     * @param helper
     * @param item
     */
    private void setDefaultItem(BaseViewHolder helper, CallMessageInfo item) {
        helper.setText(R.id.item_name,item.getTitle());
        TextView messageText = helper.getView(R.id.item_last_message);
        if (TextUtils.isEmpty(item.getContent()))  {
            messageText.setVisibility(View.GONE);
        } else {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(item.getContent());
        }
        helper.getView(R.id.item_line).setVisibility(helper.getAdapterPosition()==(getData().size()-1)? View.GONE:View.VISIBLE);
        setMessageContent(((TextView) helper.getView(R.id.view_item_unread_num)),item.getNum());
        ImageView imageView = (ImageView) helper.getView(R.id.item_icon);
        //图标
        Glide.with(mContext)
                .load(item.getIcon())
                .error(R.drawable.index_msg_calllist)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()
                .dontAnimate()
                .transform(new GlideCircleTransform(mContext))
                .into(imageView);
        helper.itemView.setTag(item);
    }

    /**
     * 服务账号
     * @param helper
     * @param item
     */
    private void setServerItem(BaseViewHolder helper, CallMessageInfo item) {
        if(null!=item){
            if(null!=UserManager.getInstance().getServer()){
                ServerConversationLayout conversationLayout = (ServerConversationLayout) helper.getView(R.id.server_conversation);
                FansInfo fansInfo=new FansInfo();
                fansInfo.setNickname(UserManager.getInstance().getServer().getServer_nickname());
                fansInfo.setAvatar(UserManager.getInstance().getServer().getServer_avatar());
                fansInfo.setUserid(UserManager.getInstance().getServer().getServer_identify());
                fansInfo.setDesp(UserManager.getInstance().getServer().getServer_desc());
                conversationLayout.setServerUserData(fansInfo);
            }

        }
    }

    /**
     * 设置消息数量
     * @param textView
     * @param count
     */
    public void setMessageContent(TextView textView,int count) {
        if(null==textView||null==mContext) return;
        if(count<=0){
            textView.setText("");
            textView.setBackgroundResource(0);
            return;
        }
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.arl_round_red_dot));
        textView.setText(String.valueOf(count));
    }
}