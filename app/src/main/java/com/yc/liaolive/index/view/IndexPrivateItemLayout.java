package com.yc.liaolive.index.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.index.adapter.IndexPrivateHeadAdapter;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.model.BannerImageLoader;
import com.yc.liaolive.model.ItemSpacesItemDecoration;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;
import com.yc.liaolive.view.widget.RoundImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/14
 * 主页1v1、直播间 条目 支持主播相册预览
 */

public class IndexPrivateItemLayout extends FrameLayout{

    private static final String TAG = "IndexPrivateItemLayout";

    private IndexPrivateHeadAdapter mAdapter;
    private RoundImageView mLayoutCover;
    private int mPosition;//当前正被选中的项
    private int mItemType;
    private AutoBannerLayout mBannerLayout;

    public IndexPrivateItemLayout(@NonNull Context context) {
        this(context,null);
    }

    public IndexPrivateItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        View.inflate(context, R.layout.view_index_private_item,this);
        mLayoutCover = findViewById(R.id.view_item_iv_icon);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.view_item_recycler_view);
        recyclerView.setLayoutManager(new IndexLinLayoutManager(getContext(),IndexLinLayoutManager.HORIZONTAL,false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ItemSpacesItemDecoration(ScreenUtils.dpToPxInt(8f)));
        mAdapter = new IndexPrivateHeadAdapter(null);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(mPosition!=position){
                    mAdapter.getData().get(mPosition).setSelected(false);
                    mAdapter.notifyItemChanged(mPosition,"newData");
                    mAdapter.getData().get(position).setSelected(true);
                    mAdapter.notifyItemChanged(position,"newData");
                    setHeadCover(mAdapter.getData().get(position));
                }
                mPosition=position;
            }
        });
        recyclerView.setAdapter(mAdapter);
        //广告
        mBannerLayout = (AutoBannerLayout) findViewById(R.id.item_banner_view);
        mBannerLayout.setImageLoader(new BannerImageLoader()).setAutoRoll(true).setOnItemClickListener(new AutoBannerLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(null!=mBannerLayout.getTag()&&mBannerLayout.getTag() instanceof List){
                    List<BannerInfo> bannerInfos = (List<BannerInfo>) mBannerLayout.getTag();
                    if(bannerInfos.size()>position){
                        if (!TextUtils.isEmpty(bannerInfos.get(position).getJump_url())) {
                            CaoliaoController.start(bannerInfos.get(position).getJump_url(),true,null);
                        }
                    }
                }
            }
        });
    }
    /**
     * 给定数据
     * @param item itemCategory 状态
     * "type_videocall","type_free"
     */
    public void setData(int itemType,RoomList item,String type) {
        if(null==item) return;
        this.mItemType=itemType;
        //昵称
        ((TextView) findViewById(R.id.view_item_title_name)).setText(item.getNickname());
        //签名
        ((TextView) findViewById(R.id.view_item_desc)).setText(item.getSignature());
        //1v1视频聊价格
        TextView priceName = (TextView) findViewById(R.id.view_item_price_name);
        //主播在线状态
        AnchorStatusView offlineState = findViewById(R.id.user_offline_state);
        //观看人数
        LinearLayout numLy = findViewById(R.id.view_live_num);
        //大厅直播状态
        ImageView itemState = findViewById(R.id.item_state);
        if(0==mItemType){
            numLy.setVisibility(VISIBLE);//在线人数
            priceName.setVisibility(GONE);
            offlineState.setVisibility(INVISIBLE);
            itemState.setVisibility(VISIBLE);
            ((TextView) findViewById(R.id.item_look_num)).setText(String.format("%d人",item.getMember_total()));
            if(1==item.getIs_online()){
                itemState.setImageResource(R.drawable.live_liveing);
                AnimationDrawable animationDrawable = (AnimationDrawable) itemState.getDrawable();
                if(null!=animationDrawable&&!animationDrawable.isRunning()) animationDrawable.start();
            }else{
                itemState.setImageResource(R.drawable.ic_offline);
            }
            //是否是ASMR房间
            if("1".equals(item.getAsmr())){
                ImageView payState = (ImageView) findViewById(R.id.item_pay_state);
                payState.setImageResource(1==item.getIs_pay()?R.drawable.ic_media_private:0);
            }
        }else{
            priceName.setVisibility(VISIBLE);
            numLy.setVisibility(GONE);
            itemState.setVisibility(GONE);
            offlineState.setVisibility(VISIBLE);
            priceName.setText(Html.fromHtml("<font><big>"+item.getChat_deplete()+"</big></font> 钻石/分钟"));
            offlineState.setData(item.getItemCategory(), item.getChat_time());//设置用户在线状态
        }
        //封面
        if(null!=item.getMy_image_list()&&item.getMy_image_list().size()>0){
            setHeadCover(item.getMy_image_list().get(0));
            if(item.getMy_image_list().size()>1){
                //条目复用，还原集合数据没有被初始的情况
                for (int i = 0; i < item.getMy_image_list().size(); i++) {
                    item.getMy_image_list().get(i).setSelected(false);
                }
                item.getMy_image_list().get(0).setSelected(true);//默认第一个被选中
                mPosition=0;
                if(null!=mAdapter) mAdapter.setNewData(item.getMy_image_list());
            }else{
                if(null!=mAdapter) mAdapter.setNewData(null);
            }
        }else{
            if(null!=mAdapter) mAdapter.setNewData(null);
            List<PrivateMedia> privateMedia=new ArrayList<>();
            PrivateMedia media=new PrivateMedia();
            media.setFile_path(item.getFrontcover());
            media.setImg_path(item.getFrontcover());
            privateMedia.add(media);
            item.setMy_image_list(privateMedia);
            setHeadCover(item.getMy_image_list().get(0));
        }
        //广告
        if(null!=mBannerLayout){
            if(null!=item.getBanners()&&item.getBanners().size()>0){
                List<String> banners=new ArrayList<>();
                for (BannerInfo roomTask : item.getBanners()) {
                    banners.add(roomTask.getImg());
                }
                mBannerLayout.setData(banners).setLayoutParams(ScreenUtils.dpToPxInt(100f),item.getBanners().get(0).getWidth(),item.getBanners().get(0).getHeight()).setTag(item.getBanners());
            }else{
                mBannerLayout.setData(null).setTag(null);
            }
        }
    }

    /**
     * 设置主封面
     * @param privateMedia
     */
    private void setHeadCover(PrivateMedia privateMedia) {
        if(null!=mLayoutCover)
            Glide.with(getContext())
                    .load(privateMedia.getFile_path())
                    .asBitmap()
                    .placeholder(R.drawable.ic_default_live_icon)
                    .error(R.drawable.ic_default_live_icon)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget(mLayoutCover) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
    }
}
