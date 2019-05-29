package com.yc.liaolive.index.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.model.BannerRoundImageLoader;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import com.yc.liaolive.view.widget.MarqueeTextView;
import com.yc.liaolive.view.widget.RoundImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/25.
 * 主页视频列表适配器
 */

public class IndexVideoListAdapter extends BaseMultiItemQuickAdapter<PrivateMedia,BaseViewHolder> {

    public static final int ITEM_TYPE_VIDEO=0;
    public static final int ITEM_TYPE_AUDIO=1;
    public static final int ITEM_TYPE_IMAGE=2;
    public static final int ITEM_TYPE_ADD=3;
    public static final int ITEM_TYPE_BANNERS=4;
    public static final int ITEM_TYPE_BANNER = 5;
    public static final int ITEM_TYPE_UNKNOWN = 6;//占位
    public static final int ITEM_TYPE_ASMR_VIDEO=7;//ASMR视频
    private final int mItemHeight;
    private final int mIndex;
    private final int mFileType;
    private AutoBannerLayout mBannerLayout;

    /**
     * @param data
     * @param index
     * @param fileType 0: 图片 1：视频
     */
    public IndexVideoListAdapter(List<PrivateMedia> data, int index, int fileType) {
        super(data);
        this.mIndex=index;
        this.mFileType=fileType;
        addItemType(ITEM_TYPE_UNKNOWN,R.layout.recyler_item_empty);//占位
        addItemType(ITEM_TYPE_VIDEO,R.layout.recyler_index_video_list_item);//普通的视频条目
        addItemType(ITEM_TYPE_AUDIO,R.layout.recyler_index_video_list_item);//普通的音频条目
        addItemType(ITEM_TYPE_IMAGE,R.layout.recyler_index_video_list_item);//普通的照片条目
        addItemType(ITEM_TYPE_ASMR_VIDEO,R.layout.recyler_index_video_list_item);//ASMR视频
        addItemType(ITEM_TYPE_BANNERS,R.layout.recyler_index_video_banners);//轮播横屏广告
        addItemType(ITEM_TYPE_BANNER,R.layout.recyler_index_video_banner);//流广告
        //图片和ASMR视频，正方形
        if(Constant.MEDIA_TYPE_IMAGE == mFileType||Constant.MEDIA_TYPE_ASMR_VIDEO == mFileType){
            mItemHeight = (ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(40))/2;
        }else{
            //168:208比例显示
            mItemHeight = ((ScreenUtils.getScreenWidth()-ScreenUtils.dpToPxInt(40))/2) * 208 /168;
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PrivateMedia item) {
        int itemViewType = helper.getItemViewType();
        switch (itemViewType) {
            //普通的视频条目
            case ITEM_TYPE_VIDEO:
            case ITEM_TYPE_AUDIO:
            case ITEM_TYPE_IMAGE:
            case ITEM_TYPE_ASMR_VIDEO:
                setItemDataVideo(helper,item);
                break;
            case ITEM_TYPE_BANNERS:
                setItemDataBanners(helper,item);
                break;
            case ITEM_TYPE_BANNER:
                setItemDataBanner(helper,item);
                break;
        }
    }

    /**
     * 视频、相册数据
     * @param helper
     * @param item
     */
    private void setItemDataVideo(BaseViewHolder helper, PrivateMedia item) {
        if(null==item) return;
        try {
            helper.getView(R.id.item_index_rootview).getLayoutParams().height = mItemHeight + Utils.dip2px(48);
//            IndexItemLayout itemLayout = (IndexItemLayout) helper.getView(R.id.item_index_item);
//            itemLayout.setFileType(mFileType);
            helper.getView(R.id.item_index_item).getLayoutParams().height = mItemHeight;

            MarqueeTextView despView = (MarqueeTextView) helper.getView(R.id.item_title_desp);
            despView.setText(Constant.MEDIA_TYPE_IMAGE == mFileType?item.getSignature():item.getVideo_desp());
            helper.setText(R.id.item_user_name,item.getNickname())
                .setText(R.id.item_look_num, Utils.formatWan(item.getBrowse_number(),true));
            ImageView slantedTextView = (ImageView) helper.getView(R.id.item_media_tag);
            int placeholderImg=R.drawable.ic_default_live_min_icon;
            if(Constant.MEDIA_TYPE_IMAGE == mFileType){
                //预览照片
                slantedTextView.setVisibility(View.GONE);
            } else {
                //预览视频
                placeholderImg=R.drawable.ic_default_item_cover;
                slantedTextView.setVisibility(1==item.getIs_private() ? View.VISIBLE : View.GONE);
            }
            //封面
            RoundImageView imageView = helper.getView(R.id.item_iv_icon);
            Glide.with(mContext)
                    .load(item.getImg_path())
                    .asBitmap()
                    .placeholder(placeholderImg)
                    .error(placeholderImg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
        }catch (RuntimeException e){

        }
    }

    /**
     * Banner
     * @param helper
     * @param item
     */
    private void setItemDataBanners(BaseViewHolder helper, final PrivateMedia item) {
        if(null==item) return;
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

    /**
     * 单个广告条目
     * @param helper
     * @param item
     */
    private void setItemDataBanner(BaseViewHolder helper, PrivateMedia item) {
        View itemBanner = helper.getView(R.id.coord_root_view);
        itemBanner.getLayoutParams().height = mItemHeight  + Utils.dip2px(48);
        RoundImageView roundImageView = (RoundImageView) helper.getView(R.id.item_iv_icon);
        if(Constant.MEDIA_TYPE_IMAGE==mFileType){
            roundImageView.setRoundRadius(0f);
        }else{
            roundImageView.setRoundRadius(ScreenUtils.dpToPx(10f));
        }
        if (null != item && null != item.getBanners() && item.getBanners().size() > 0) {
            final BannerInfo bannerInfo = item.getBanners().get(0);
            Glide.with(mContext)
                    .load(bannerInfo.getImg())
                    .asBitmap()
                    .placeholder(R.drawable.ic_default_item_cover)
                    .error(R.drawable.ic_default_item_cover)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget(roundImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
            itemBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mOnMultiItemClickListener) mOnMultiItemClickListener.onBannerClick(bannerInfo);
                }
            });
        }
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
                        //视频、图片条目按照 IndexGridLayoutManager 的 spanCount均分
                        case ITEM_TYPE_VIDEO:
                        case ITEM_TYPE_IMAGE:
                        case ITEM_TYPE_BANNER:
                        case ITEM_TYPE_ASMR_VIDEO:
                            return 1;
                        //广告、未识别出来的类型 权重撑满全屏
                        case ITEM_TYPE_BANNERS:
                        case ITEM_TYPE_UNKNOWN:
                            return 2;
                    }
                    return 2;//Anapter 加载中、空布局、头部、尾部 等
                }
            });
        }
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
