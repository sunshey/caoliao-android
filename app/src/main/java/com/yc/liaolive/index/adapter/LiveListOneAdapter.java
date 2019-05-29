package com.yc.liaolive.index.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.view.AnchorStatusView;
import com.yc.liaolive.index.view.IndexPrivateItemLayout;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.model.BannerRoundImageLoader;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import com.yc.liaolive.view.widget.RoundImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/14
 * 直播间1v1 列表适配器
 */

public class LiveListOneAdapter extends BaseMultiItemQuickAdapter<RoomList,BaseViewHolder> {

    public static final int ITEM_TYPE_ROOM=0;
    public static final int ITEM_TYPE_PRIVATE=1;
    public static final int ITEM_TYPE_BANNERS=2;
    public static final int ITEM_TYPE_BANNER=3;
    public static final int ITEM_TYPE_RECOMMEND=4;
    public static final int ITEM_TYPE_EMPTY=5;
    public static final int ITEM_TYPE_UNKNOWN=101;
    private final String mType;
    private int mItemHeight;
    private AutoBannerLayout mBannerLayout;
    private String image_small_show = "0"; // 0 大图模式 1 小图模式

    /**
     * @param data
     */
    public LiveListOneAdapter(List<RoomList> data,String type) {
        super(data);
        addItemType(ITEM_TYPE_BANNERS,R.layout.recyler_index_one_list_banner_item);//广告
        addItemType(ITEM_TYPE_RECOMMEND,R.layout.recyler_index_recommend_item);//推荐栏位
        addItemType(ITEM_TYPE_EMPTY,R.layout.index_follow_header_layout);//关注列表为空布局
        addItemType(ITEM_TYPE_UNKNOWN,R.layout.recyler_item_empty);//识别不出来的占位符
        this.mType=type;
    }

    public void setImage_small_show(String image_small_show) {
        this.image_small_show = image_small_show;
        if ("0".equals(image_small_show)) {
            addItemType(ITEM_TYPE_ROOM,R.layout.recyler_index_one_list_private_item);//直播房间
            addItemType(ITEM_TYPE_PRIVATE,R.layout.recyler_index_one_list_private_item);//1v1通话
            mItemHeight = ScreenUtils.getScreenWidth() - Utils.dip2px(32);
        } else {
            addItemType(ITEM_TYPE_ROOM,R.layout.recyler_index_one_list_small_item);//直播房间
            addItemType(ITEM_TYPE_PRIVATE,R.layout.recyler_index_one_list_small_item);//1v1通话
            mItemHeight = (ScreenUtils.getScreenWidth() - ScreenUtils.dpToPxInt(40))/2;
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final RoomList item) {
        int itemViewType = helper.getItemViewType();
        switch (itemViewType) {
            //普通的直播
            case ITEM_TYPE_ROOM:
            //1v1通话
            case ITEM_TYPE_PRIVATE:
                setItemDataPrivate(helper,item,itemViewType);
                break;
            //Banner广告
            case ITEM_TYPE_BANNERS:
                setItemDataBanner(helper,item);
                break;
            //推荐标题
            case ITEM_TYPE_RECOMMEND:
                setItemDataRecommend(helper,item);
                break;
            //关注列表为空
            case ITEM_TYPE_EMPTY:
                setItemEmpty(helper,item);
                break;
        }
    }

    /**
     * 空的占位布局
     * @param helper
     * @param item
     */
    private void setItemEmpty(BaseViewHolder helper, RoomList item) {

    }

    /**
     * 私聊通话
     * @param helper
     * @param item
     * @param itemType 0：直播间 1：私有
     */
    private void setItemDataPrivate(BaseViewHolder helper, RoomList item,int itemType) {
        if(null==item) return;
        try {
            if ("0".equals(image_small_show)) {
                helper.itemView.setTag(item);
                IndexPrivateItemLayout itemLayout = helper.getView(R.id.coord_root_view);
                itemLayout.getLayoutParams().height = mItemHeight + Utils.dip2px(12);
                itemLayout.setData(itemType,item,mType);
            } else {
                helper.itemView.setTag(item);
                helper.getView(R.id.item_small_rootview).getLayoutParams().height = mItemHeight + Utils.dip2px(48);
                helper.getView(R.id.item_small_item).getLayoutParams().height = mItemHeight;
                helper.setText(R.id.item_small_name,item.getNickname());
                helper.setText(R.id.item_small_title_desp,item.getSignature());
                AnchorStatusView offlineState = helper.getView(R.id.small_offline_state);
                //价格
                TextView priceTV = helper.getView(R.id.item_small_price_name);
                //观看人数
                LinearLayout numLy = helper.getView(R.id.small_live_num);
                // 1v1
                if (1 == itemType) {
                    offlineState.setVisibility(View.VISIBLE);
                    offlineState.setData(item.getItemCategory(), item.getChat_time());//设置用户在线状态
//                    itemState.setVisibility(View.GONE);
                    numLy.setVisibility(View.GONE);
                    priceTV.setVisibility(View.VISIBLE);
                    priceTV.setText(String.format("%d钻石/分钟", item.getChat_deplete()));
                } else {
                    offlineState.setVisibility(View.INVISIBLE);
                    priceTV.setVisibility(View.GONE);
                    numLy.setVisibility(View.VISIBLE);
                    ImageView itemState = helper.getView(R.id.item_small_state);
                    if(1 == item.getIs_online()){
                        itemState.setImageResource(R.drawable.live_liveing);
                        AnimationDrawable animationDrawable = (AnimationDrawable) itemState.getDrawable();
                        if(null!=animationDrawable&&!animationDrawable.isRunning()) animationDrawable.start();
                    }else{
                        itemState.setImageResource(R.drawable.ic_offline);
                    }
                    helper.setText(R.id.item_small_look_num, String.format("%d人",item.getMember_total()));
                    ImageView payState = (ImageView) helper.getView(R.id.item_pay_state);
                    payState.setImageResource(1==item.getIs_pay()?R.drawable.ic_media_private:0);
                }
                //封面
                RoundImageView imageView = helper.getView(R.id.item_small_icon);
                Glide.with(mContext)
                        .load(item.getMy_image_list().get(0).getFile_path())
                        .asBitmap()
                        .placeholder(R.drawable.ic_default_live_min_icon)
                        .error(R.drawable.ic_default_live_min_icon)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                        .into(new BitmapImageViewTarget(imageView) {
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
    private void setItemDataBanner(BaseViewHolder helper, final RoomList item) {
        if(null==item) return;
        if(!TextUtils.equals(Constant.INDEX_ITEM_TYPE_BANNERS,item.getItemCategory())) return;
        mBannerLayout = (AutoBannerLayout) helper.getView(R.id.item_banner);
        int padding12 = Utils.dip2px(12);
        mBannerLayout.setPadding(0, 0, 0, padding12);
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
     * 推荐标题
     * @param helper
     * @param item
     */
    private void setItemDataRecommend(BaseViewHolder helper, RoomList item) {
        if(null==item) return;
        if(!TextUtils.equals(Constant.INDEX_ITEM_TYPE_RECOMMEND,item.getItemCategory())) return;
        helper.setText(R.id.item_tv_title,item.getTitle());
        helper.itemView.setTag(item);
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
                    if ("0".equals(image_small_show)) {
                        return 2;
                    } else {
                        int itemViewType = getItemViewType(position);
                        switch (itemViewType) {
                            //普通的直播
                            case ITEM_TYPE_ROOM:
                                //1v1通话
                            case ITEM_TYPE_PRIVATE:
                                return 1;
                            //Banner广告
                            case ITEM_TYPE_BANNERS:
                                return 2;
                            //推荐标题
                            case ITEM_TYPE_RECOMMEND:
                                return 2;
                        }
                        return 2;//Anapter 加载中、空布局、头部、尾部 等
                    }
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
