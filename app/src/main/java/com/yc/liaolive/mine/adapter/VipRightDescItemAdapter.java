package com.yc.liaolive.mine.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.VipInfoBean;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/4 16:26.
 */
public class VipRightDescItemAdapter extends BaseQuickAdapter<VipInfoBean, BaseViewHolder> {
    public VipRightDescItemAdapter(@Nullable List<VipInfoBean> data) {
        super(R.layout.vip_right_item_detail, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipInfoBean item) {
        helper.setText(R.id.item_title, item.getTitle())
                .setText(R.id.item_desc, item.getDesp());
        Glide.with(mContext).load(item.getIcon()).error(R.drawable.ic_default_gift_icon).into((ImageView) helper.getView(R.id.item_icon));
    }
}
