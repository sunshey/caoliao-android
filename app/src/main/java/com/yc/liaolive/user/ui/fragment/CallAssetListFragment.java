package com.yc.liaolive.user.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.bean.DiamondInfo;
import com.yc.liaolive.databinding.FragmentIndexVideoListBinding;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.ui.adapter.CallAssetListAdapter;
import com.yc.liaolive.ui.adapter.CallNotesListAdapter;
import com.yc.liaolive.ui.contract.AssetContract;
import com.yc.liaolive.ui.presenter.AssetPresenter;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/20
 * 我的资产、积分
 */

public class CallAssetListFragment extends BaseFragment <FragmentIndexVideoListBinding,AssetPresenter> implements AssetContract.View {

    private static final String TAG = "CallAssetListFragment";

    private CallAssetListAdapter mAdapter;
    private DataChangeView mEmptyView;
    private int mPage;
    private String mTypeID;
    private int mItemType;

    public static CallAssetListFragment newInstance(String typeID, int itemType) {
        CallAssetListFragment fragment=new CallAssetListFragment();
        Bundle bundle=new Bundle();
        bundle.putString("typeID",typeID);
        bundle.putInt("itemType",itemType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mTypeID = arguments.getString("typeID");
            mItemType = arguments.getInt("itemType", CallNotesListAdapter.ITEM_TYPE_LET);
        }
    }

    @Override
    protected void initViews() {
        bindingView.recylerView.setLayoutManager(new IndexLinLayoutManager(getActivity(), IndexLinLayoutManager.VERTICAL, false));
        bindingView.recylerView.setHasFixedSize(true);
        mAdapter = new CallAssetListAdapter(null,mTypeID);
        //加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()) {
                    loadData();
                }
            }
        },bindingView.recylerView);
        //局部点击事件
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    CallMessageInfo item = (CallMessageInfo) view.getTag();
                    switch (view.getId()) {
                        //我的钻石、钻石，关系人
                        case R.id.item_ll_user_item:
                            PersonCenterActivity.start(getActivity(),item.getUserid());
                            break;
                    }
                }
            }
        });
        //条目点击事件
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        //占位
        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()) {
                    mPage=0;
                    loadData();
                }
            }
        });
        mEmptyView.showLoadingView();
        mAdapter.setEmptyView(mEmptyView);
        bindingView.recylerView.setAdapter(mAdapter);
        //下拉刷新监听
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage =0;
                loadData();
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if(null!=mPresenter&&!mPresenter.isLoading()){
            mPage++;
            mPresenter.getAssetsList(mTypeID,mItemType, mPage);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_video_list;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new AssetPresenter();
        mPresenter.attachView(this);
        mPage = 0;
        loadData();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showListResult(List<DiamondInfo> data) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=mAdapter){
            mAdapter.loadMoreComplete();
            if(1==mPage){
                if(null!=mAdapter) mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void showListResultEmpty() {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView){
            mEmptyView.stopLoading();
            if(1==mPage){
                mEmptyView.showEmptyView();
            }
        }
        if(null!=mAdapter) mAdapter.loadMoreEnd();
    }

    @Override
    public void showListResultError(int code, String errorMsg) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView&&mAdapter.getData().size()==0){
            mEmptyView.showErrorView(errorMsg);
        }
        if(null!=mAdapter) mAdapter.loadMoreFail();
    }
}
