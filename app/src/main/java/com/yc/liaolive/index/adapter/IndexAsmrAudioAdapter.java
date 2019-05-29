package com.yc.liaolive.index.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.view.MusicRoundImageView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.model.BannerRoundImageLoader;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/3/11
 * ASMR 音频适配器
 "itemCategory": "type_audio",//音频
 "itemCategory": "type_video",//视频
 "itemCategory": "type_image",//照片
 "itemCategory": "type_room",//直播间
 "itemCategory":"type_banners",//广告
 "itemCategory":"type_free",//空闲
 "itemCategory":"type_room",//直播
 "itemCategory":"type_videocall",//视频对话
 "itemCategory":"type_quite",//勿扰
 "itemCategory":"type_offline",//离线
 */

public class IndexAsmrAudioAdapter extends BaseMultiItemQuickAdapter<PrivateMedia,BaseViewHolder> {

    private final int mType;
    private int mItemHeight;
    private AutoBannerLayout mBannerLayout;

    /**
     * @param data
     */
    public IndexAsmrAudioAdapter(List<PrivateMedia> data, int type) {
        super(data);
        addItemType(IndexVideoListAdapter.ITEM_TYPE_AUDIO,R.layout.music_item_index_music_list);//音频
        addItemType(IndexVideoListAdapter.ITEM_TYPE_VIDEO,R.layout.music_item_index_music_list);//视频
        addItemType(IndexVideoListAdapter.ITEM_TYPE_IMAGE,R.layout.music_item_index_music_list);//图片
        addItemType(IndexVideoListAdapter.ITEM_TYPE_BANNERS,R.layout.recyler_index_asmr_audio_list_banner_item);//广告
        addItemType(IndexVideoListAdapter.ITEM_TYPE_BANNER,R.layout.recyler_index_asmr_audio_list_banner_item);//广告
        addItemType(IndexVideoListAdapter.ITEM_TYPE_UNKNOWN,R.layout.recyler_item_empty);//识别不出来的占位符
        mItemHeight = (ScreenUtils.getScreenWidth() - ScreenUtils.dpToPxInt(56f)) /3;
        this.mType=type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PrivateMedia item) {
        int itemViewType = helper.getItemViewType();
        switch (itemViewType) {
            //普通多媒体
            case IndexVideoListAdapter.ITEM_TYPE_AUDIO:
            case IndexVideoListAdapter.ITEM_TYPE_VIDEO:
            case IndexVideoListAdapter.ITEM_TYPE_IMAGE:
                setItemAudioData(helper,item,itemViewType);
                break;
            //Banner广告
            case IndexVideoListAdapter.ITEM_TYPE_BANNERS:
            case IndexVideoListAdapter.ITEM_TYPE_BANNER:
                setItemDataBanner(helper,item);
                break;
        }
    }

    /**
     * 音频
     * @param helper
     * @param item
     * @param itemType 0：直播间 1：私有
     */
    private void setItemAudioData(BaseViewHolder helper, PrivateMedia item, int itemType) {
        if(null==item) return;
        try {
            helper.itemView.setTag(item);
            MusicRoundImageView musicRoundImageView = helper.getView(R.id.view_item_cover);
            musicRoundImageView.getLayoutParams().height=mItemHeight;
            helper.setText(R.id.view_item_anchor,item.getNickname());
            helper.setText(R.id.view_item_title,item.getVideo_desp());
            ((ImageView) helper.getView(R.id.view_item_type)).setImageResource(3==item.getFile_type()?R.drawable.ic_music_audio:0);
            ((ImageView) helper.getView(R.id.view_item_private)).setImageResource(1==item.getIs_private()?R.drawable.ic_music_private:0);
            String cover=TextUtils.isEmpty(item.getImg_path())?item.getAvatar():item.getImg_path();
            if(!TextUtils.isEmpty(cover)){
                Glide.with(mContext)
                        .load(cover)
                        .asBitmap()
                        .placeholder(R.drawable.ic_default_live_min_icon)
                        .error(R.drawable.ic_default_live_min_icon)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .centerCrop()
                        .into(new BitmapImageViewTarget(musicRoundImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                            }
                        });
            }
        }catch (RuntimeException e){

        }
    }

    /**
     * Banner
     * @param helper
     * @param item
     */
    private void setItemDataBanner(BaseViewHolder helper, final PrivateMedia item) {
        if(null==item) return;
        if(!TextUtils.equals(Constant.INDEX_ITEM_TYPE_BANNERS,item.getItemCategory())) return;
        mBannerLayout = (AutoBannerLayout) helper.getView(R.id.item_banner);
        mBannerLayout.setImageLoader(new BannerRoundImageLoader(4)).setAutoRoll(true).setOnItemClickListener(new AutoBannerLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    if(null!=mOnMultiItemClickListener) mOnMultiItemClickListener.onBannerClick(item.getBanners().get(position));
                }catch (RuntimeException e){

                }
            }
        });
        List<String> list=new ArrayList<>();
        if(null!=item.getBanners()&&item.getBanners().size()>0){
            //图片的宽度根据 充满组件的宽度 计算
            BannerInfo bannerInfo = item.getBanners().get(0);
            if(bannerInfo.getWidth()==0){
                bannerInfo.setWidth(1080);
                bannerInfo.setHeight(333);
            }
            int width = Utils.getScreenWidth() - Utils.dip2px(32);
            int height = width * bannerInfo.getHeight() / bannerInfo.getWidth();
            //banner区域实际宽高
            mBannerLayout.setLayoutParams(width, height);
            for (int i = 0; i < item.getBanners().size(); i++) {
                list.add(item.getBanners().get(i).getImg());
            }
        }
        mBannerLayout.setData(list);
    }

    public void onResume(){
        if(null!=mBannerLayout) mBannerLayout.onResume();
    }

    public void onPause(){
        if(null!=mBannerLayout) mBannerLayout.onPause();
    }


    public void reset() {
        mBannerLayout=null;
    }

    /**
     * 动态给定权重 这里的SpanSize是两列 2 表示占据一整行
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(null!=layoutManager&&layoutManager instanceof IndexGridLayoutManager){
            IndexGridLayoutManager gridLayoutManager= (IndexGridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemViewType = getItemViewType(position);
                    switch (itemViewType) {
                        //普通的直播
                        case IndexVideoListAdapter.ITEM_TYPE_AUDIO:
                        case IndexVideoListAdapter.ITEM_TYPE_VIDEO:
                        case IndexVideoListAdapter.ITEM_TYPE_IMAGE:
                            return 1;
                        //Banner广告
                        case IndexVideoListAdapter.ITEM_TYPE_BANNERS:
                        case IndexVideoListAdapter.ITEM_TYPE_BANNER:
                        case IndexVideoListAdapter.ITEM_TYPE_UNKNOWN:
                            return 3;
                    }
                    return 3;//Anapter 加载中、空布局、头部、尾部 等
                }
            });
        }
    }

    /**
     * 复杂条目监听事件
     */
    public interface OnMultiItemClickListener{
        void onBannerClick(BannerInfo bannerInfo);
    }

    private OnMultiItemClickListener mOnMultiItemClickListener;

    public void setOnMultiItemClickListener(OnMultiItemClickListener onMultiItemClickListener) {
        mOnMultiItemClickListener = onMultiItemClickListener;
    }
}