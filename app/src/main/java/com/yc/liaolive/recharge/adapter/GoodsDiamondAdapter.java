package com.yc.liaolive.recharge.adapter;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
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
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.msg.view.ServerConversationLayout;
import com.yc.liaolive.recharge.listener.OnGoodsChangedListener;
import com.yc.liaolive.recharge.model.bean.GoodsDiamondItem;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.recharge.view.PayChanlSelectedLayout;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 * 钻石商品列表
 */

public class GoodsDiamondAdapter extends BaseMultiItemQuickAdapter<GoodsDiamondItem,BaseViewHolder> {

    public static final int ITEM_AD=0;
    public static final int ITEM_GOODS=1;
    public static final int ITEM_PAY=2;
    public static final int ITEM_SERVER=3;
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
    public GoodsDiamondAdapter(List<GoodsDiamondItem> data, OnGoodsChangedListener listener, Activity mActivity) {
        super(data);
        addItemType(ITEM_AD, R.layout.re_item_goods_ad);
        addItemType(ITEM_GOODS, R.layout.re_item_goods_goods);
        addItemType(ITEM_PAY, R.layout.re_item_goods_pay);
        addItemType(ITEM_SERVER, R.layout.re_item_goods_server);
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
            mTextView = (TextView) helper.getView(R.id.user_goods);
            mTextView.setText(Html.fromHtml("我的钻石：<font color='#E3575B'>" + UserManager.getInstance().getDiamonds() + "</font>"));
            RecyclerView recyclerView = (RecyclerView) helper.getView(R.id.view_recyler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(5f)));
            RechargeGoldItemAdapter goldItemAdapter = new RechargeGoldItemAdapter(item.getList());
            goldItemAdapter.setOnItemClickListener(new OnItemClickListener() {
                public int mPosition=1;
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if(null!=view.getTag()){
                        RechargeGoodsInfo item = (RechargeGoodsInfo) view.getTag();
                        if(item.getItemType()==RechargeGoldItemAdapter.ITEM_TYPE_GOLD){
                            if(mPosition!=position){
                                goldItemAdapter.getData().get(mPosition).setSelected(false);
                                goldItemAdapter.notifyItemChanged(mPosition,"update");
                                RechargeGoodsInfo rechargeGoodsInfo = goldItemAdapter.getData().get(position);
                                rechargeGoodsInfo.setSelected(true);
                                goldItemAdapter.notifyItemChanged(position,"update");
                                if(null!=mListener) mListener.onGoodsChanged(rechargeGoodsInfo);
                            }
                            mPosition=position;
                        }else if(item.getItemType()==RechargeGoldItemAdapter.ITEM_TYPE_MORE){
                            if(null!=mListener) mListener.onServer();
                        }
                    }
                }
            });
            recyclerView.setAdapter(goldItemAdapter);
            if(null!=item.getList()&&item.getList().size()>1){
                RechargeGoodsInfo rechargeGoodsInfo = item.getList().get(1);
                if(null!=mListener) mListener.onGoodsChanged(rechargeGoodsInfo);
            }
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
     * 客服
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