package com.yc.liaolive.media.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.ScreenUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/8/15
 * 本地视频缩略图列表
 */

public class ImagesListAdapter extends BaseMultiItemQuickAdapter<ImageInfo, BaseViewHolder> {

    private final int mItemHeight;
    public static final int ITEM_TYPE_DEFAULT=0;
    public static final int ITEM_TYPE_ADD=1;
    private final int isMultipleSelected;//是否允许多选 0：允许多选 1：不允许多选

    /**
     * @param data
     * @param isMultipleSelected 是否允许多选
     */
    public ImagesListAdapter(List<ImageInfo> data,int isMultipleSelected) {
        super(data);
        this.isMultipleSelected=isMultipleSelected;
        addItemType(0,R.layout.list_item_import_image);
        addItemType(1,R.layout.list_item_import_image_add);
        mItemHeight = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(12f)) /3;
    }

    /**
     * 全局刷新
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder helper, ImageInfo item) {
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
    protected void convert(BaseViewHolder helper, ImageInfo item, List<Object> payloads) {
        super.convert(helper, item, payloads);
        if(!payloads.isEmpty()){
            View emptyView = helper.getView(R.id.item_empty);
            emptyView.setVisibility(item.isSelector()?View.VISIBLE:View.GONE);
            ImageView selectedState = (ImageView) helper.getView(R.id.btn_selected_state);
            selectedState.setImageResource(item.isSelector()?R.drawable.ic_image_selected_true:R.drawable.ic_image_selected_false);
            AnimationUtil.playTextCountAnimation2(selectedState);
            helper.itemView.setTag(item);
        }else{
            convert(helper,item);
        }
    }

    /**
     * 普通按钮
     * @param helper
     * @param item
     */
    private void setItemList(final BaseViewHolder helper, ImageInfo item) {
        if (null == item) return;
        try {
            ImageView selectedState = (ImageView) helper.getView(R.id.btn_selected_state);
            if(0==isMultipleSelected){
                selectedState.setVisibility(View.VISIBLE);
                View emptyView = helper.getView(R.id.item_empty);
                item.setPosition(helper.getAdapterPosition());
                emptyView.setVisibility(item.isSelector()?View.VISIBLE:View.GONE);
                selectedState.setImageResource(item.isSelector()?R.drawable.ic_image_selected_true:R.drawable.ic_image_selected_false);
                selectedState.setTag(item);
                selectedState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null!=mOnSelectedListener) mOnSelectedListener.onSelectedChanged(helper.itemView,v,(ImageInfo) v.getTag(),helper.getAdapterPosition());
                    }
                });
            }else{
                selectedState.setVisibility(View.GONE);
            }

            //图片封面
            ImageView imageViewCover = (ImageView) helper.getView(R.id.item_cover);
            //Uri.fromFile(new File(item.getFilePath()))
            Glide.with(mContext)
                    .load("file://"+item.getFilePath())
                    .error(R.drawable.ic_default_item_cover)
                    .override(200, 200)
                    .dontAnimate()
                    .skipMemoryCache(true)//跳过内存缓存
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .into(imageViewCover);
            helper.itemView.setTag(item);
        }catch (RuntimeException e){

        }
    }

    /**
     * 拍照按钮
     * @param helper
     * @param item
     */
    private void setItemAdd(BaseViewHolder helper, ImageInfo item) {
        if(null!=item){
            item.setPosition(helper.getAdapterPosition());
            ImageView addIcon = (ImageView) helper.getView(R.id.item_add_icon);
            addIcon.setImageResource(R.drawable.make_picture);
            addIcon.setColorFilter(Color.parseColor("#FF555555"));
            ((TextView) helper.getView(R.id.item_add_title)).setText("拍照");
            helper.itemView.setTag(item);
        }
    }

    /**
     * 选中事件监听
     */
    public interface OnSelectedListener{
        void onSelectedChanged(View itemView,View view,ImageInfo weiXinVideo, int position);
    }
    private OnSelectedListener mOnSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }
}
