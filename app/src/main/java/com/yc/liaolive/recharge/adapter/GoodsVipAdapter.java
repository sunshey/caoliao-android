package com.yc.liaolive.recharge.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.yc.liaolive.R;
import com.yc.liaolive.base.adapter.BaseMultiItemQuickAdapter;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.base.adapter.BaseViewHolder;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.model.BannerImageLoader;
import com.yc.liaolive.model.ItemMiddleSpaceDecoration;
import com.yc.liaolive.msg.view.ServerConversationLayout;
import com.yc.liaolive.recharge.listener.OnGoodsChangedListener;
import com.yc.liaolive.recharge.model.bean.GoodsDiamondItem;
import com.yc.liaolive.recharge.view.PayChanlSelectedLayout;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 * VIP商品列表
 */

public class GoodsVipAdapter extends BaseMultiItemQuickAdapter<GoodsDiamondItem,BaseViewHolder> {

    public static final int ITEM_AD=0;
    public static final int ITEM_GOODS=1;
    public static final int ITEM_PAY=2;
    public static final int ITEM_SERVER=3;
    public static final int ITEM_ACTIVITY=4;
    private final OnGoodsChangedListener mListener;
    private AutoBannerLayout mBannerLayout;
    private TextView mTextView;
    private Activity mActivity;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public GoodsVipAdapter(List<GoodsDiamondItem> data, OnGoodsChangedListener listener, Activity mActivity) {
        super(data);
        addItemType(ITEM_AD, R.layout.re_item_goods_ad);
        addItemType(ITEM_GOODS, R.layout.re_item_goods_vip);
        addItemType(ITEM_PAY, R.layout.re_item_goods_pay);
        addItemType(ITEM_SERVER, R.layout.re_item_goods_server);
        addItemType(ITEM_ACTIVITY, R.layout.re_item_goods_activity);
        this.mListener=listener;
        this.mActivity = mActivity;
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsDiamondItem item) {
        switch (item.getItemType()) {
            case ITEM_AD:
                setItemDataAD(helper,item);
                break;
            case ITEM_GOODS:
                setItemDataGoods(helper,item);
                break;
            case ITEM_PAY:
                setItemDataPay(helper,item);
                break;
            case ITEM_SERVER:
                setItemDataServer(helper,item);
                break;
            case ITEM_ACTIVITY:
                setItemActivity(helper,item);
                break;
        }
    }

    /**
     * 广告
     * @param helper
     * @param item
     */
    private void setItemDataAD(BaseViewHolder helper, GoodsDiamondItem item) {
        if(null!=item&&null!=item.getBanners()){
            mBannerLayout = (AutoBannerLayout) helper.getView(R.id.item_view_banner);
            List<BannerInfo> banners = item.getBanners();
            mBannerLayout.setImageLoader(new BannerImageLoader()).setAutoRoll(true).setOnItemClickListener(new AutoBannerLayout.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if(null!= banners&&banners.size()>position){
                        BannerInfo bannerInfo = banners.get(position);
                        if(null!=bannerInfo&&!TextUtils.isEmpty(bannerInfo.getJump_url())){
                            CaoliaoController.start(bannerInfo.getJump_url());
                        }
                    }
                }
            });
            List<String> list=new ArrayList<>();
            //图片的宽度根据 充满组件的宽度 计算
            BannerInfo bannerInfo = banners.get(0);
            if(bannerInfo.getWidth()==0){
                bannerInfo.setWidth(1080);
                bannerInfo.setHeight(333);
            }
            int height = (int) Math.ceil((float) ScreenUtils.getScreenWidth() * (float) bannerInfo.getHeight() / (float)  bannerInfo.getWidth());
            mBannerLayout.getLayoutParams().height=height;
            mBannerLayout.setLayoutParams(ScreenUtils.getScreenWidth(),height);
            for (int i = 0; i < banners.size(); i++) {
                list.add(banners.get(i).getImg());
            }
            mBannerLayout.setData(list);
        }
    }

    /**
     * 商品列表
     * @param helper
     * @param item
     */
    private void setItemDataGoods(BaseViewHolder helper, GoodsDiamondItem item) {
        if(null!=item&&null!=item.getList()){
            RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.item_recycler_view);
            LinearLayoutManager goodsLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(goodsLayoutManager);
            recyclerView.addItemDecoration(new ItemMiddleSpaceDecoration(Utils.dip2px(12)));
            recyclerView.setHasFixedSize(true);
            VipGoodsAdapter vipItemAdapter = new VipGoodsAdapter(item.getList());
            vipItemAdapter.setOnItemClickListener(new OnItemClickListener() {
                public int mPosition=0;
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    if(mPosition!=position){
//                        vipItemAdapter.getData().get(mPosition).setSelected(false);
//                        vipItemAdapter.notifyItemChanged(mPosition,"update");
//                        RechargeGoodsInfo newVipInfo = vipItemAdapter.getData().get(position);
//                        newVipInfo.setSelected(true);
//                        vipItemAdapter.notifyItemChanged(position,"update");
//                        if(null!=mListener) mListener.onGoodsChanged(newVipInfo);
//                    }else{
//                        if(null!=mListener) mListener.onGoodsChanged(vipItemAdapter.getData().get(position));
//                    }
                    mPosition=position;
                    if(null!=mListener) mListener.onGoodsChanged(vipItemAdapter.getData().get(position));
                }
            });
            recyclerView.setAdapter(vipItemAdapter);
        }
    }

    /**
     * 支付渠道
     * @param helper
     * @param item
     */
    private void setItemDataPay(BaseViewHolder helper, GoodsDiamondItem item) {
        if(null!=item&&null!=item.getPay_config()){
            PayChanlSelectedLayout selectedLayout = (PayChanlSelectedLayout) helper.getView(R.id.view_pay_channel);
            selectedLayout.setOnPayChanlChangedListener(new PayChanlSelectedLayout.OnPayChanlChangedListener() {
                @Override
                public void onPayChanlChanged(int chanl) {
                    if(null!=mListener) mListener.onPayChanlChanged(chanl);
                }
            });
            selectedLayout.setPayListConfig(item.getPay_config(), mActivity);
        }
    }

    /**
     * 客服信息
     * @param helper
     * @param item
     */
    private void setItemDataServer(BaseViewHolder helper, GoodsDiamondItem item) {
        if(null!=item&&null!=item.getServer()){
            ServerConversationLayout conversationLayout = (ServerConversationLayout) helper.getView(R.id.server_conversation);
            //客服
            FansInfo fansInfo=new FansInfo();
            fansInfo.setUserid(item.getServer().getServer_identify());
            fansInfo.setNickname(item.getServer().getServer_nickname());
            fansInfo.setDesp(item.getServer().getServer_desc());
            fansInfo.setAvatar(item.getServer().getServer_avatar());
            conversationLayout.setServerUserData(fansInfo);
        }
    }

    /**
     * 活动
     * @param helper
     * @param item
     */
    private void setItemActivity(BaseViewHolder helper, GoodsDiamondItem item) {
        if(null!=item&&null!=item.getDescribe_list()){
            RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.activity_recycler_view);
            //活动
            LinearLayoutManager activityLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(activityLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            activityLayoutManager.setSmoothScrollbarEnabled(true);
            recyclerView.setHasFixedSize(true);
            RechargeActivityListAdapter activityListAdapter = new RechargeActivityListAdapter(item.getDescribe_list());
            activityListAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if(null!=view.getTag()){
                        String  jumpUrl = (String) view.getTag();
                        CaoliaoController.start(jumpUrl);
                    }
                }
            });
            recyclerView.setAdapter(activityListAdapter);
        }
    }

    public void refreshDiamons() {
        if(null!=mTextView) mTextView.setText(Html.fromHtml("我的钻石：<font color='#E3575B'>" + UserManager.getInstance().getDiamonds() + "</font>"));
    }

    public void onResume() {
        if(null!=mBannerLayout) mBannerLayout.onResume();
    }

    public void onPause() {
        if(null!=mBannerLayout) mBannerLayout.onPause();
    }

    public void onDestroy() {
        if(null!=mBannerLayout) mBannerLayout.onDestroy();
    }
}