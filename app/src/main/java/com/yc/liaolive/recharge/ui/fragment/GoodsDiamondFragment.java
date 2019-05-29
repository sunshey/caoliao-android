package com.yc.liaolive.recharge.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.databinding.FragmentGoodsDiamondBinding;
import com.yc.liaolive.recharge.adapter.GoodsDiamondAdapter;
import com.yc.liaolive.recharge.listener.OnGoodsChangedListener;
import com.yc.liaolive.recharge.manager.GoodsPresenter;
import com.yc.liaolive.recharge.model.bean.GoodsDiamondItem;
import com.yc.liaolive.recharge.model.bean.RechargeBean;
import com.yc.liaolive.recharge.model.bean.RechargeGoodsInfo;
import com.yc.liaolive.recharge.model.bean.RechargeInfo;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.ui.contract.GoodsContract;
import com.yc.liaolive.ui.dialog.QuireServerDialog;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/31
 * 商品-钻石
 */

public class GoodsDiamondFragment extends BaseFragment<FragmentGoodsDiamondBinding, GoodsPresenter> implements BaseContract.BaseView, GoodsContract.View, OnGoodsChangedListener {

    private GoodsDiamondAdapter mAdapter;
    private DataChangeView mDataChangeView;
    private RechargeGoodsInfo mRechargeGoodsInfo;//充值的商品信息
    private int mPayChanl;//充值渠道

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goods_diamond;
    }

    @Override
    protected void initViews() {
        bindingView.recyclerView.setLayoutManager(new IndexLinLayoutManager(getActivity(),IndexLinLayoutManager.VERTICAL,false));
        mAdapter = new GoodsDiamondAdapter(null,this, getActivity());
        //占位布局
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (null != mPresenter && !mPresenter.isLoading()) {
                    mDataChangeView.showLoadingView();
                    mPresenter.getGoldGoods(11);
                }
            }
        });
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);
        bindingView.recyclerView.setAdapter(mAdapter);
        //开始支付
        bindingView.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mRechargeGoodsInfo&&null!=getActivity()&&getActivity() instanceof VipActivity){
                    RechargeInfo rechargeInfo = new RechargeInfo();
                    rechargeInfo.setGoodsInfo(mRechargeGoodsInfo);
                    rechargeInfo.setPayway(mPayChanl);
                    VipActivity vipActivity = (VipActivity) getActivity();
                    vipActivity.setGoodsInfo(rechargeInfo);
                    vipActivity.pay();
                }

            }
        });
        bindingView.btnPay.setEnabled(false);
    }

    @Override
    public void onGoodsChanged(RechargeGoodsInfo goodsInfo) {
        this.mRechargeGoodsInfo=goodsInfo;
    }

    @Override
    public void onServer() {
        QuireServerDialog.getInstance(getActivity())
                .setTitle("请添加以下客服进行充值")
                .show();
    }

    @Override
    public void onPayChanlChanged(int chanl) {
        this.mPayChanl=chanl;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new GoodsPresenter();
        mPresenter.attachView(this);
        mPresenter.getGoldGoods(11);//获取充值套餐
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MobclickAgent.onPageStart("recharge_diamonds");
            MobclickAgent.onEvent(getActivity(), "recharge_diamonds");
        } else if (isResumed()) {
            MobclickAgent.onPageEnd("recharge_diamonds");
        }
    }

    public void refreshDiamons() {
        if(null!=mAdapter) mAdapter.refreshDiamons();
    }

    //========================================网路数据获取回调=======================================

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void showGoldInfo(RechargeBean data) {
        if (null != mDataChangeView) mDataChangeView.stopLoading();
        if(null!=bindingView&&null!= mAdapter){
            List<GoodsDiamondItem> itemList=new ArrayList<>();
            if(null!=data.getAd_list()&&data.getAd_list().size()>0&&null!=data.getAd_list().get(0)){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setBanners(data.getAd_list().get(0).getBanners());
                goodsDiamondItem.setItemType(GoodsDiamondAdapter.ITEM_AD);
                itemList.add(goodsDiamondItem);
            }
            if(null!=data.getList()){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setList(data.getList());
                goodsDiamondItem.setItemType(GoodsDiamondAdapter.ITEM_GOODS);
                itemList.add(goodsDiamondItem);
            }
            if(null!=data.getPay_config()){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setPay_config(data.getPay_config());
                goodsDiamondItem.setItemType(GoodsDiamondAdapter.ITEM_PAY);
                itemList.add(goodsDiamondItem);
            }
            if(null!=UserManager.getInstance().getServer()){
                GoodsDiamondItem goodsDiamondItem=new GoodsDiamondItem();
                goodsDiamondItem.setServer(UserManager.getInstance().getServer());
                goodsDiamondItem.setItemType(GoodsDiamondAdapter.ITEM_SERVER);
                itemList.add(goodsDiamondItem);
            }
            mAdapter.setNewData(itemList);
            bindingView.btnPay.setBackgroundResource(R.drawable.pay_btn_selecter);
            bindingView.btnPay.setEnabled(true);
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
    public void onResume() {
        super.onResume();
        if(null!=mAdapter) mAdapter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mAdapter) mAdapter.onPause();
        if (getUserVisibleHint()) {
            MobclickAgent.onPageEnd("recharge_diamonds");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null!=mAdapter) mAdapter.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDataChangeView) mDataChangeView.stopLoading();
        mDataChangeView = null;
        if (null != mAdapter) mAdapter.setNewData(null);
        mAdapter = null;
    }
}