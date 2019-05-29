package com.yc.liaolive.ui.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.WeiXinVideo;
import com.yc.liaolive.util.DateUtil;
import com.yc.liaolive.util.ScreenUtils;
import java.io.File;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/8/15
 * 本地视频缩略图列表
 */

public class MoivesListAdapter extends BaseMultiItemQuickAdapter<WeiXinVideo, BaseViewHolder> {

    public static final int ITEM_TYPE_DEFAULT=0;
    public static final int ITEM_TYPE_ADD=1;
    private final int mItemHeight;

    public MoivesListAdapter(List<WeiXinVideo> data) {
        super(data);
        addItemType(0,R.layout.list_item_import_video);
        addItemType(1,R.layout.list_item_import_image_add);
        mItemHeight = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(12f)) /3;
    }

    /**
     * 全局刷新
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, WeiXinVideo item) {
        helper.getView(R.id.item_root_view).getLayoutParams().height=mItemHeight;
        switch (item.getItemType()) {
            //普通条目
            case ITEM_TYPE_DEFAULT:
                setItemList(helper,item);
                break;
            //添加按钮
            case ITEM_TYPE_ADD:
                setItemAdd(helper,item);
                break;
        }
    }


    /**
     * 局部刷新
     * @param helper
     * @param item
     * @param payloads
     */
    @Override
    protected void convert(BaseViewHolder helper, WeiXinVideo item, List<Object> payloads) {
        super.convert(helper, item, payloads);
        if(!payloads.isEmpty()){

        }else{
            convert(helper,item);
        }
    }
    /**
     * 普通按钮
     * @param helper
     * @param item
     */
    private void setItemList(final BaseViewHolder helper, WeiXinVideo item) {
        if (null == item) return;
        if (0 != item.getVideoDortion()) {
            helper.setText(R.id.tv_duration, DateUtil.getTimeLengthString(item.getVideoDortion() /1000));
        }
        ImageView icon = helper.getView(R.id.iv_item_icon);
        icon.setImageResource(R.drawable.iv_empty_bg_error);
        File file = new File(item.getVideoPath());
        Glide.with(mContext).load(Uri.fromFile(file)).placeholder(icon.getDrawable()).error(R.drawable.ic_default_item_cover).animate(R.anim.item_alpha_in).skipMemoryCache(true).into(icon);
        helper.itemView.setTag(item);
    }

    /**
     * 拍照按钮
     * @param helper
     * @param item
     */
    private void setItemAdd(BaseViewHolder helper, WeiXinVideo item) {
        if(null!=item){
            ImageView addIcon = (ImageView) helper.getView(R.id.item_add_icon);
            addIcon.setImageResource(R.drawable.make_record);
            addIcon.setColorFilter(Color.parseColor("#FF555555"));
            ((TextView) helper.getView(R.id.item_add_title)).setText("录制");
            helper.itemView.setTag(item);
        }
    }
}
