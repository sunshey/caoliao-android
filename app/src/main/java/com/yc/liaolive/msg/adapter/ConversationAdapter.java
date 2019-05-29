package com.yc.liaolive.msg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.msg.model.bean.Conversation;
import com.yc.liaolive.util.TimeUtil;
import com.yc.liaolive.view.CircleImageView;
import com.yc.liaolive.view.widget.SwipeMenuLayout;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/12/20
 * 私信会话列表
 */

public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private final List<Conversation> mData;
    private Context mContext;
    private final int mResourceId;

    public ConversationAdapter(Context context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        this.mData = objects;
        this.mContext = context;
        mResourceId = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null== convertView){
            convertView = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemLine.setVisibility(position==getCount()-1?View.GONE:View.VISIBLE);
        final Conversation data = getItem(position);
        if(null!=data){
            //默认关闭动画
            viewHolder.viewItemSwipemenu.smoothClose();
            //条目单机
            viewHolder.llItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) mOnItemClickListener.onItemClick(position);
                }
            });
            //删除事件
            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) mOnItemClickListener.onItemDetele(position);
                }
            });
            viewHolder.tvConTitle.setText(data.getName());
            Glide.with(mContext)
                    .load(data.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(viewHolder.mAvatar);
            viewHolder.mLastMessage.setText(data.getLastMessageSummary());
            viewHolder.mMessageTime.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));

            long unRead = data.getUnreadNum();
            if(unRead<=0){
                viewHolder.mUnread.setText("");
                viewHolder.mUnread.setBackgroundResource(0);
            }else{
                viewHolder.mUnread.setText(String.valueOf(unRead));
                viewHolder.mUnread.setBackground(getContext().getResources().getDrawable(R.drawable.arl_round_red_dot));
            }
        }
        return convertView;
    }

    public List<Conversation> getData() {
        return mData;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemDetele(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public class ViewHolder {
        public CircleImageView mAvatar;
        public TextView tvConTitle;
        public TextView mMessageTime;
        public TextView mLastMessage;
        public TextView mUnread;
        public View llItemView;
        public View itemLine;

        public SwipeMenuLayout viewItemSwipemenu;
        public TextView btnDelete;

        public ViewHolder(View convertView) {
            mAvatar = (CircleImageView) convertView.findViewById(R.id.item_con_icon);
            tvConTitle = (TextView) convertView.findViewById(R.id.item_con_title);
            mMessageTime = (TextView) convertView.findViewById(R.id.item_con_message_time);
            mLastMessage = (TextView) convertView.findViewById(R.id.item_con_last_message);
            mUnread = (TextView) convertView.findViewById(R.id.item_unread_num);
            llItemView = convertView.findViewById(R.id.ll_item_view);
            itemLine = convertView.findViewById(R.id.item_line);
            viewItemSwipemenu = (SwipeMenuLayout) convertView.findViewById(R.id.view_item_swipemenu);
            btnDelete = (TextView) convertView.findViewById(R.id.item_btnDelete);
        }
    }
}