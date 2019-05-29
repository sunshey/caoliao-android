package com.yc.liaolive.msg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.msg.model.Message;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.view.CircleImageView;
import java.util.List;

/**
 * 私信会话聊天界面adapter
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private int resourceId;
    private View view;

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return view != null ? view.getId() : getCount() - 1;
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ChatAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == -1) {
            return setCustomLocalView(position, convertView, parent);
        } else {
            return setCustomView(position, convertView, parent);
        }
    }

    private View setCustomView (int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
            viewHolder.rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            viewHolder.item_left_user_icon_view = view.findViewById(R.id.item_left_user_icon_view);
            viewHolder.item_right_user_icon_view = view.findViewById(R.id.item_right_user_icon_view);
            viewHolder.leftAvatar = view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = view.findViewById(R.id.rightAvatar);
            view.setTag(viewHolder);
        }
        if (position < getCount()) {
            final Message data = getItem(position);
            data.showMessage(viewHolder, getContext());
            viewHolder.item_left_user_icon_view.setTag(data.getSender());
            viewHolder.item_right_user_icon_view.setTag(data.getSender());
//
            viewHolder.item_left_user_icon_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnUserClickListener)
                        mOnUserClickListener.onUserClick((String) v.getTag());
                }
            });
            viewHolder.item_right_user_icon_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnUserClickListener)
                        mOnUserClickListener.onUserClick((String) v.getTag());
                }
            });
            viewHolder.error.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnUserClickListener) {
                        mOnUserClickListener.onSendFail(data);
                    }
                }
            });
        }

        return view;
    }

    private View setCustomLocalView (int position, View convertView, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.chat_vip_tips_view, null);
        VipTipsViewHolder viewHolder = new VipTipsViewHolder();
        viewHolder.vipTips = view.findViewById(R.id.vip_text);
//        view.setTag(viewHolder);

        if (position < getCount()) {
            viewHolder.vipTips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaoliaoController.startActivity(VipActivity.class.getName(), "index", "1");
                }
            });
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        if(getCount()>position&&null!=getItem(position)){
            return getItem(position).getLocalType();
        }
        return 0;
    }

    public interface OnUserClickListener {
        void onUserClick(String userID);
        void onSendFail(Message message);
    }

    private OnUserClickListener mOnUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        mOnUserClickListener = onUserClickListener;
    }

    public class ViewHolder{
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;
        public View item_left_user_icon_view;
        public View item_right_user_icon_view;
        public CircleImageView leftAvatar;
        public CircleImageView rightAvatar;
    }

    public class VipTipsViewHolder{
        public TextView vipTips;
    }

}
