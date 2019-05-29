package com.yc.liaolive.user.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.recharge.model.bean.VipListItem;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/2/22
 * VIP签到奖励
 */

public class VipRewardListAdapter extends BaseQuickAdapter<VipListItem,BaseViewHolder> {

    private final int mItemWidth;

    public VipRewardListAdapter(@Nullable List<VipListItem> data, int layoutWidth) {
        super(R.layout.re_item_vip_reward_list, data);
        mItemWidth = (layoutWidth- ScreenUtils.dpToPxInt(48f))/4;
    }

    @Override
    protected void convert(final BaseViewHolder helper, VipListItem item) {
        if(null!=item){
            if(TextUtils.isEmpty(item.getWidth())) item.setWidth("180");
            if(TextUtils.isEmpty(item.getHeight())) item.setHeight("210");
            FrameLayout itemRootView = (FrameLayout) helper.getView(R.id.item_root_view);
            int height=mItemWidth*Integer.parseInt(item.getHeight())/Integer.parseInt(item.getWidth());
            itemRootView.getLayoutParams().width=mItemWidth;
            itemRootView.getLayoutParams().height=height;
            helper.setText(R.id.item_title,item.getDay_text()).setText(R.id.item_desp,item.getText());
            Glide.with(mContext)
                    .load(item.getImg_url())
                    .placeholder(R.drawable.ic_reward_has_false)
                    .error(R.drawable.ic_reward_has_false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) helper.getView(R.id.item_icon));
        }
    }
}