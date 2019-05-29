package com.yc.liaolive.media.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.VideoEditInfo;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 本地视频截图预览
 */

public class VideoExtractAdapter extends BaseQuickAdapter<VideoEditInfo,BaseViewHolder>{

    private final int mItemWidth;

    public VideoExtractAdapter(@Nullable List<VideoEditInfo> data) {
        super(R.layout.re_location_video_extract_item, data);
        mItemWidth = (int) (ScreenUtils.getScreenWidth()/5);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoEditInfo item) {
        View rootView = helper.getView(R.id.item_root_view);
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        layoutParams.width=mItemWidth;
        layoutParams.height=mItemWidth/9*16;//9:16布局，主要适配长方形封面显示
        rootView.setLayoutParams(layoutParams);
        helper.getView(R.id.view_broder).setBackgroundResource(item.isSelected()?R.drawable.middle_border:0);
        ImageView itemCover = (ImageView) helper.getView(R.id.view_item_cover);
        Glide.with(mContext)
                .load("file://" + item.getPath())
                .placeholder(itemCover.getDrawable())
                .error(R.drawable.ic_default_item_cover)
                .dontAnimate()
                .skipMemoryCache(true)
                .into(itemCover);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoEditInfo item, List<Object> payloads) {
        super.convert(helper, item, payloads);
        if(!payloads.isEmpty()){
            helper.getView(R.id.view_broder).setBackgroundResource(item.isSelected()?R.drawable.middle_border:0);
        }else{
            convert(helper,item);
        }
    }
}
