package com.yc.liaolive.recharge.adapter;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.recharge.model.bean.RechargeActivity;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/22
 * 充值活动
 */

public class RechargeActivityListAdapter extends BaseQuickAdapter<RechargeActivity,BaseViewHolder> {

    public RechargeActivityListAdapter(@Nullable List<RechargeActivity> data) {
        super(R.layout.re_item_recharge_activity_list, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, RechargeActivity item) {
        if(null!=item){
            ImageView imageView = (ImageView) helper.getView(R.id.view_item_activity_icon);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            int htight = Integer.valueOf(item.getHeight());
            int width = Integer.valueOf(item.getWidth());
            int height = (int) Math.ceil((float) ScreenUtils.getScreenWidth() * (float) htight / (float)  width);
            layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height= height;
            imageView.setLayoutParams(layoutParams);
            Glide.with(mContext)
                    .load(item.getImg_url())
                    .error(R.drawable.ic_default_live_icon)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .into(imageView);
            helper.itemView.setTag(item.getJump_url());
        }
    }
}