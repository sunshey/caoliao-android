package com.yc.liaolive.media.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/20
 * 礼物榜单
 */

public class MediaGiftTopAdapter extends BaseQuickAdapter<FansInfo,BaseViewHolder>{

    public MediaGiftTopAdapter(@Nullable List<FansInfo> data) {
        super(R.layout.re_media_top_list_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FansInfo item) {
        if(null!=item){
            TextView itemTvNum = (TextView) helper.getView(R.id.item_tv_num);
            LiveUtils.setMediaGiftTopColor(itemTvNum,helper.getAdapterPosition());
            itemTvNum.setText("No."+String.valueOf(helper.getAdapterPosition()+1));
            helper.setText(R.id.tv_item_name,item.getNickname())
                    .setText(R.id.item_tv_points,String.valueOf(item.getTotal_points()));
            ImageView item_vip_gradle = (ImageView) helper.getView(R.id.item_vip_gradle);
            ImageView item_user_sex = (ImageView) helper.getView(R.id.item_user_sex);
            ImageView item_gift_top = (ImageView) helper.getView(R.id.item_gift_top);
            LiveUtils.setMediaGiftTop(item_gift_top,helper.getAdapterPosition());
            LiveUtils.setUserSex(item_user_sex,item.getSex());
            LiveUtils.setUserBlockVipGradle(item_vip_gradle,item.getVip());
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(mContext))
                    .into((ImageView) helper.getView(R.id.item_ic_icon));
            helper.itemView.setTag(item);
        }
    }
}
