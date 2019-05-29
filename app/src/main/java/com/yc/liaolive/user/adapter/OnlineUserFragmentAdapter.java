package com.yc.liaolive.user.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.user.manager.OnlineUserPresenter;
import com.yc.liaolive.user.model.bean.OnlineUserBean;
import com.yc.liaolive.view.CircleImageView;

import java.util.List;

/**
 * 在线用户列表
 * Created by yangxueqin on 18/12/14.
 */

public class OnlineUserFragmentAdapter extends BaseQuickAdapter<OnlineUserBean.OnlineUserItemBean,BaseViewHolder>{

    private OnlineUserPresenter mPresenter;

    private Drawable vipDraw;

    public OnlineUserFragmentAdapter(@Nullable List<OnlineUserBean.OnlineUserItemBean> data,
                                     Context mContext) {
        super(R.layout.online_user_list_item, data);
        vipDraw = mContext.getResources().getDrawable(R.drawable.ic_vip_icon);
    }

    public void setmPresenter(OnlineUserPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final OnlineUserBean.OnlineUserItemBean item) {
        if(null != item){
            TextView username = helper.getView(R.id.user_name);
            username.setText(item.getNickname());
            if ("1".equals(item.getVip())) {
                username.setCompoundDrawablesWithIntrinsicBounds(null, null, vipDraw, null);
            } else {
                username.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

            helper.setText(R.id.user_damion, "钻石数量：" + item.getMoney());

//            helper.getView(R.id.item_line).setVisibility(helper.getAdapterPosition()==(getData().size()-1)? View.GONE:View.VISIBLE);
            CircleImageView imageView = helper.getView(R.id.user_avatar);
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .placeholder(imageView.getDrawable())
                    .error(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .dontAnimate()
//                    .transform(new GlideCircleTransform(mContext))
                    .into(imageView);
            helper.itemView.setTag(item);

            //私信聊天 0不显示 1可点击 2不可点击
            TextView talkMsg = helper.getView(R.id.user_talk_msg);
            if (!"0".equals(item.getMsg_chat_but())) {
                talkMsg.setVisibility(View.VISIBLE);
                talkMsg.setEnabled("1".equals(item.getMsg_chat_but()));
            } else {
                talkMsg.setVisibility(View.GONE);
            }
            talkMsg.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (mPresenter != null) {
                        mPresenter.clickTalkMsg(item.getUserid(), item.getNickname());
                    }
                    item.setRedpoint_count(0);
                    setMessageContent(helper.getView(R.id.user_talk_redpoint), 0);
                }
            });
            setMessageContent(helper.getView(R.id.user_talk_redpoint), item.getRedpoint_count());

            //视频聊天 0不显示 1可点击 2不可点击
            final TextView talkVideo = helper.getView(R.id.user_talk_video);
            //单个用户可呼叫次数
            int callNum = 0;
            try {
                callNum = Integer.parseInt(item.getCall_num());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (!"0".equals(item.getVideo_chat_but())) {
                talkVideo.setVisibility(View.VISIBLE);
                talkVideo.setEnabled("1".equals(item.getVideo_chat_but()));
            } else {
                talkVideo.setVisibility(View.GONE);
            }
            final int finalCallNum = callNum;
            talkVideo.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (mPresenter != null) {
                        if (mPresenter.isCanCallOut() && mPresenter.getCall_sum_num() > 0) {
                            mPresenter.clickTalkVideo(item.getUserid(), item.getNickname(), item.getAvatar());
                            mPresenter.startCallCountDown();
                            if (finalCallNum - 1 <= 0) {
                                item.setVideo_chat_but("2");
                                talkVideo.setEnabled(false);
                            }
                        }
                    }
                }
            });

        }
    }


    /**
     * 设置消息数量
     * @param pointView
     * @param count
     */
    private void setMessageContent(View pointView, int count) {
        if(null == pointView) return;
        pointView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
//        String unReadStr = String.valueOf(count);
//        if (count < 10) {
//            textView.setBackground(mContext.getResources().getDrawable(R.drawable.point1));
//        } else {
//            textView.setBackground(mContext.getResources().getDrawable(R.drawable.point2));
//            if (count > 99) {
//                unReadStr = mContext.getResources().getString(R.string.time_more);
//            }
//        }
//        textView.setText(unReadStr);
    }

}
