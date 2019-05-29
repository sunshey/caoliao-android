package com.yc.liaolive.index.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentIndexOneListBinding;
import com.yc.liaolive.index.adapter.NearbyUserFragmentAdapter;
import com.yc.liaolive.index.contract.INearbyUserView;
import com.yc.liaolive.index.manager.NearbyUserPresenter;
import com.yc.liaolive.index.model.bean.NearbyUserBean;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.layout.DataChangeView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 附近的人
 * Created by yangxueqin on 2019/1/8.
 */

public class NearbyUserFragment extends BaseFragment<FragmentIndexOneListBinding, NearbyUserPresenter> implements INearbyUserView {

    private int page = 1;

    private DataChangeView mEmptyView;

    private NearbyUserFragmentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new NearbyUserPresenter(getActivity());
        mPresenter.attachView(this);
    }

    @Override protected void initViews() {
        bindingView.recylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bindingView.recylerView.setPadding(0, Utils.dip2px(12), 0, 0);
        mAdapter = new NearbyUserFragmentAdapter(null);
        mAdapter.showEmptyView(true);
        mAdapter.loadMoreEnd();
        //加载更多监听
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null != mPresenter && !mPresenter.isLoading()){
                    if(null != mAdapter.getData()){
                        mPresenter.getUserListData(page);
                    }else{
                        mAdapter.loadMoreEnd();
                    }
                }else{
                    mAdapter.loadMoreFail();
                }
            }
        }, bindingView.recylerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NearbyUserBean.ListBean bean = (NearbyUserBean.ListBean) view.getTag();
                if (bean != null) {
                    if (TextUtils.equals(bean.getItemCategory(), Constant.INDEX_ITEM_TYPE_BANNERS)) {
                        String jumpUrl = bean.getBanners().get(position).getJump_url();
                        if (TextUtils.isEmpty(jumpUrl)) {
                            CaoliaoController.start(jumpUrl,true,null);
                        }
                    } else {
                        PersonCenterActivity.start(getActivity(), bean.getUserid());
                    }
                }

            }
        });
        mAdapter.setOnMultiItemClickListener(new NearbyUserFragmentAdapter.OnMultiItemClickListener() {
            @Override
            public void onBannerClick(BannerInfo bannerInfo) {
                if(null==bannerInfo) return;
                if (!TextUtils.isEmpty(bannerInfo.getJump_url())) {
                    CaoliaoController.start(bannerInfo.getJump_url(),true,null);
                }
            }
        });

        //初始化占位布局
        mEmptyView = bindingView.loadingView;
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null != mPresenter && !mPresenter.isLoading()){
                    mEmptyView.showLoadingView();
                    page = 1;
                    mPresenter.getUserListData(page);
                }
            }
        });
//        mAdapter.setEmptyView(mEmptyView);

        bindingView.recylerView.setAdapter(mAdapter);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                refreshData();
            }
        });
    }

    @Override protected int getLayoutId() {
        return R.layout.fragment_index_one_list;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MobclickAgent.onPageStart("main_group_26");
            MobclickAgent.onEvent(getActivity(), "main_group_26");
            checkIsInitView ();
        }
    }

    private void checkIsInitView () {
        Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (getView() == null) {
                            checkIsInitView();
                        } else if (mAdapter != null && mAdapter.getData().isEmpty()) {
                            page = 1;
                            mEmptyView.showLoadingView();
                            mPresenter.getUserListData(page);
                        }
                    }
                });
    }

    @Override public void showErrorView() {

    }

    @Override public void complete() {

    }

    @Override public Activity getDependType() {
        return getActivity();
    }

    @Override public void setDataView(NearbyUserBean userBean) {
        bindingView.swiperLayout.setRefreshing(false);
        mAdapter.loadMoreComplete();
        mEmptyView.stopLoading();
        if (page == 1) {
            mAdapter.setNewData(userBean.getList());
        } else {
            mAdapter.addData(userBean.getList());
        }
        page ++;
    }

    public void setListEnd(boolean isEnd) {
        if (isEnd) {
            mAdapter.loadMoreEnd();
        } else {
            mAdapter.loadMoreEnd();
        }
    }

    @Override
    public void showError(String message) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter){
            if(page == 1){
                if(null!=mEmptyView && (mAdapter.getData() == null || mAdapter.getData().isEmpty())) {
                    mEmptyView.showErrorView(message);
                } else {
                    ToastUtils.showToast(message);
                }
            } else {
                mAdapter.loadMoreFail();
            }
        }
    }

    @Override
    public void showListEmpty() {
        if(null != bindingView) bindingView.swiperLayout.setRefreshing(false);
//        if(null != mEmptyView) mEmptyView.showEmptyView();
        if(null != mAdapter){
            mAdapter.loadMoreEnd();
            if(page == 1){
                mAdapter.setNewData(null);//如果是第一页的话，直接摸空数据
                if(null!=mEmptyView) mEmptyView.showErrorView("暂无数据",
                        R.drawable.ic_list_empty_icon);
            }
        }
    }

    private void refreshData() {
        page = 1;
        if(null == mAdapter || mAdapter.getData().isEmpty()){
            if(null!=mEmptyView) mEmptyView.showLoadingView();
        }
        mPresenter.getUserListData(page);
    }

    @Override
    protected void fromMainUpdata() {
        super.fromMainUpdata();
        if(null==bindingView )return;
        if(null!=mPresenter&&!mPresenter.isLoading()){
            bindingView.recylerView.scrollToPosition(0);
            refreshData();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }
}
