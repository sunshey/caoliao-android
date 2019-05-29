package com.yc.liaolive.media.adapter;

import android.widget.ImageView;
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
 * TinyHung@outlook.com
 * 2017/10/7
 * 直播间在线观众列表
 */

public class MediaTopListAdapter extends BaseQuickAdapter<FansInfo,BaseViewHolder> {

    private int maxCount;//0:无限制

    @Override
    public int getItemCount() {
        if(maxCount>0&&null!=mData&&mData.size()>=maxCount) return maxCount;
        return super.getItemCount();
    }

    public MediaTopListAdapter(List<FansInfo> data) {
        super(R.layout.re_user_fans_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FansInfo item) {
        if(null==item) return;
        //用户封面
        Glide.with(mContext)
                .load(item.getAvatar())
                .placeholder(R.drawable.ic_default_user_head)
                .error(R.drawable.ic_default_user_head)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()//中心点缩放
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) helper.getView(R.id.iv_user_icon));
        //用户等级，显示用户的会员区间等级
        ImageView userGradle = (ImageView) helper.getView(R.id.item_user_gradle);
        if(item.getVip()>0){
            LiveUtils.setUserBlockVipGradle(userGradle,item.getVip());
        }else if(item.getLevel_integral()>0){
            LiveUtils.setUserGradle(userGradle,item.getLevel_integral());
        }else{
            userGradle.setImageResource(0);
        }

        helper.itemView.setTag(item);
    }

    /**
     * 设置适配器最大显示个数
     * @param maxCount
     */
    public void setCount(int maxCount) {
        this.maxCount=maxCount;
    }
}
