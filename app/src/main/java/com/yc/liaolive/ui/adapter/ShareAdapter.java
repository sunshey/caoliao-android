package com.yc.liaolive.ui.adapter;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ShareMenuItemInfo;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017-06-22 12:29
 * @des 分享弹窗适配器
 */

public class ShareAdapter extends BaseQuickAdapter<ShareMenuItemInfo,BaseViewHolder> {

    public ShareAdapter(List<ShareMenuItemInfo> homeItemInfos) {
        super(R.layout.share_item,homeItemInfos);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareMenuItemInfo item) {
        if(null!=item){
            helper.setText(R.id.tv_item_title,item.getItemName());
            Glide.with(mContext)
                    .load(item.getItemLogo())
                    .error(R.drawable.error_big)
                    .crossFade()//渐变
                    .fitCenter()
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .into((ImageView) helper.getView(R.id.iv_item_icon));
        }
    }
}
