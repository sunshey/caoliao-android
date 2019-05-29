package com.yc.liaolive.index.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.view.widget.RoundImageView;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/14
 * 主页1v1聊天条目用户封面
 */

public class IndexPrivateHeadAdapter  extends BaseQuickAdapter<PrivateMedia,BaseViewHolder>{

    public IndexPrivateHeadAdapter(@Nullable List<PrivateMedia> data) {
        super(R.layout.re_idnex_private_head_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateMedia item) {
        if(null!=item){
            helper.getView(R.id.view_border).setBackgroundResource(item.isSelected()?R.drawable.index_head_border:0);
            Glide.with(mContext)
                    .load(item.getImg_path())
                    .asBitmap()
                    .error(R.drawable.ic_default_live_min_icon)
                    .placeholder(R.drawable.ic_default_live_min_icon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget((RoundImageView) helper.getView(R.id.item_head_cover)) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
            helper.itemView.setTag(item);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateMedia item, List<Object> payloads) {
        super.convert(helper, item, payloads);
        if(payloads.isEmpty()){
            convert(helper,item);
        }else{
            helper.getView(R.id.view_border).setBackgroundResource(item.isSelected()?R.drawable.index_head_border:0);
            helper.itemView.setTag(item);
        }
    }
}
