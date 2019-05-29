package com.yc.liaolive.index.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.index.model.bean.NearbyUserBean;
import com.yc.liaolive.model.BannerRoundImageLoader;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 附近的人
 * Created by yangxueqin on 2019/1/8.
 */

public class NearbyUserFragmentAdapter extends BaseMultiItemQuickAdapter<NearbyUserBean.ListBean, BaseViewHolder> {

    public static final int ITEM_TYPE_USERS = 0;
    public static final int ITEM_TYPE_BANNERS=1;

    private AutoBannerLayout mBannerLayout;

    public NearbyUserFragmentAdapter(@Nullable List<NearbyUserBean.ListBean> data) {
        super(data);
        addItemType(ITEM_TYPE_BANNERS,R.layout.recyler_index_one_list_banner_item);//广告
        addItemType(ITEM_TYPE_USERS,R.layout.nearby_user_list_item);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final NearbyUserBean.ListBean item) {
        int itemViewType = helper.getItemViewType();
        switch (itemViewType) {
            case ITEM_TYPE_USERS:
                setUsersData(helper, item);
                break;
            case ITEM_TYPE_BANNERS:
                setBannersData(helper, item);
                break;
        }
    }

    private void setUsersData (final BaseViewHolder helper, final NearbyUserBean.ListBean item) {
        if(null != item){
            helper.itemView.setTag(item);
            TextView username = helper.getView(R.id.user_name);
            username.setText(item.getNickname());
            if ("1".equals(item.getVip())) {
                username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_nearby_vip, 0);
            } else {
                username.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            helper.setText(R.id.user_desc, item.getSignature());
            String location = UserManager.getInstance().getPosition();
            location = TextUtils.isEmpty(location) ? "北京" : location;
            helper.setText(R.id.user_address,
                    String.format(Locale.CHINA, "%s岁·%s %s",  item.getAge(), location, item.getNearby()));

//            helper.getView(R.id.item_line).setVisibility(helper.getAdapterPosition()==(getData().size()-1)? View.GONE:View.VISIBLE);
            RoundImageView imageView = helper.getView(R.id.user_avatar);
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .asBitmap()
                    .placeholder(R.drawable.ic_default_user_head)
                    .error(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
            helper.itemView.setTag(item);
        }

    }

    private void setBannersData (final BaseViewHolder helper, final NearbyUserBean.ListBean item) {
        if(null==item) return;
        if(!TextUtils.equals(Constant.INDEX_ITEM_TYPE_BANNERS, item.getItemCategory())) return;
        mBannerLayout = (AutoBannerLayout) helper.getView(R.id.item_banner);
        int padding12 = Utils.dip2px(12);
        mBannerLayout.setPadding(padding12, 0, padding12, 0);
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
