package com.yc.liaolive.recharge.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.databinding.FragmentGoodsVipBinding;
import com.yc.liaolive.recharge.adapter.GoodsVipAdapter;
import com.yc.liaolive.recharge.listener.OnGoodsChangedListener;
import com.yc.liaolive.recharge.manager.GoodsPresenter;
import com.yc.liaolive.recharge.model.bean.GoodsDiamondItem;
import com.yc.liaolive.recharge.model.bean.PayConfigBean;
import com.yc.liaolive.recharge.model.bean.RechargeBean;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.recharge.model.bean.RechargeInfo;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.contract.GoodsContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 * 商品-会员
 */
public class GoodsVipFragment extends BaseFragment<FragmentGoodsVipBinding, GoodsPresenter> implements GoodsContract.View, OnGoodsChangedListener {

    private static final String TAG = "GoodsVipFragment";
    private GoodsVipAdapter mAdapter;
    private DataChangeView mDataChangeView;
    private PayConfigBean payCofingBean; //支付列表配置

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goods_vip;
    }

    @Override
    protected void initViews() {
        bindingView.recyclerView.setLayoutManager(new IndexLinLayoutManager(getActivity(),IndexLinLayoutManager.VERTICAL,false));
        mAdapter = new GoodsVipAdapter(null,this, getActivity());
        //占位布局
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);
        bindingView.recyclerView.setAdapter(mAdapter);
    }

    public void refresh () {
        if (null != mPresenter && !mPresenter.isLoading()) {
            mDataChangeView.showLoadingView();
            mPresenter.getVipGoogsList();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new GoodsPresenter();
        mPresenter.attachView(this);
        mPresenter.getVipGoogsList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MobclickAgent.onEvent(getActivity(), "recharge_vip");
            MobclickAgent.onPageStart("recharge_vip");
        } else if (isResumed()) {
            MobclickAgent.onPageEnd("recharge_vip");
        }
    }

    /**
     * 绑定宿主支付数据
     * @param info
     */
    public void setShowBuyTips(RechargeGoodsInfo info) {
        if(null!=getActivity()&&null!=info&&null!=payCofingBean){
            RechargeInfo rechargeInfo = new RechargeInfo();
            rechargeInfo.setGoodsInfo(info);
            ((VipActivity) getActivity()).setShowBuyTips(rechargeInfo, payCofingBean);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mAdapter) mAdapter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mAdapter) mAdapter.onPause();
        if (getUserVisibleHint()) {
            MobclickAgent.onPageEnd("recharge_vip");
        }
    }

    @Override
    public void showGoldInfo(RechargeBean data) {
        if (null != mDataChangeView) mDataChangeView.stopLoading();
        if(null!=bindingView&&null!= mAdapter){
            this.payCofingBean=data.getPay_config();
            List<GoodsDiamondItem> itemList=new ArrayList<>();
            if(null!=data.getAd_list()&&data.getAd_list().size()>0&&null!=data.getAd_list().get(0)){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setBanners(data.getAd_list().get(0).getBanners());
                goodsDiamondItem.setItemType(GoodsVipAdapter.ITEM_AD);
                itemList.add(goodsDiamondItem);
            }
            if(null!=data.getList()){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setList(data.getList());
                goodsDiamondItem.setItemType(GoodsVipAdapter.ITEM_GOODS);
                itemList.add(goodsDiamondItem);
            }
            if(null!=data.getDescribe_list()){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setDescribe_list(data.getDescribe_list());
                goodsDiamondItem.setItemType(GoodsVipAdapter.ITEM_ACTIVITY);
                itemList.add(goodsDiamondItem);
            }
            if(null!= UserManager.getInstance().getServer()){
                //客服是否可用
                if("1".equals(data.getShow_server())){
                    if(null!= getActivity()) ((VipActivity) getActivity()).showServer();
                    GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                    goodsDiamondItem.setServer(UserManager.getInstance().getServer());
                    goodsDiamondItem.setItemType(GoodsVipAdapter.ITEM_SERVER);
                    itemList.add(goodsDiamondItem);
                }
            }
            mAdapter.setNewData(itemList);
        }
    }

    @Override
    public void showGoldEmpty() {
        if (null != mDataChangeView) mDataChangeView.showEmptyView(false);
    }

    @Override
    public void showGoldError(int code, String errorMsg) {
        if (null != mDataChangeView) mDataChangeView.showErrorView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=mAdapter) mAdapter.onDestroy();
    }

    @Override
    public void onGoodsChanged(RechargeGoodsInfo goodsInfo) {
        setShowBuyTips(goodsInfo);
    }

    @Override
    public void onServer() {}

    @Override
    public void onPayChanlChanged(int chanl) { }
}