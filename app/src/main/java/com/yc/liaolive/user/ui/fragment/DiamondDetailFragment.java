package com.yc.liaolive.user.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.DiamondInfo;
import com.yc.liaolive.databinding.FragmentDiamondDetailBinding;
import com.yc.liaolive.mine.adapter.DiamondDetailsAdapter;
import com.yc.liaolive.ui.contract.DiamondDetailsContact;
import com.yc.liaolive.ui.presenter.DiamondDetailsPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.IntegralDetailsActivity;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 钻石详情-分片
 */

public class DiamondDetailFragment extends BaseFragment<FragmentDiamondDetailBinding, DiamondDetailsPresenter> implements DiamondDetailsContact.View {

    private DiamondDetailsAdapter mAdapter;
    private int mPage = 1;
    private DataChangeView mEmptyView;
    private boolean isRefresh=true;
    private int mAssetsType;
    private String mTypeID;

    /**
     * 入口
     * @param assetsType 0：全部 1：支出 2：收入
     * @param typeID 3：积分 4：钻石
     * @return
     */
    public static DiamondDetailFragment newInstance(int assetsType, String typeID) {
        DiamondDetailFragment fragment=new DiamondDetailFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("assetsType",assetsType);
        bundle.putString("typeID",typeID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null!=getArguments()){
            Bundle arguments = getArguments();
            mAssetsType = arguments.getInt("assetsType");
            mTypeID = arguments.getString("typeID");
        }
    }

    @Override
    protected void initViews() {
        bindingView.recyclerView.setLayoutManager(new IndexLinLayoutManager(getActivity(),IndexLinLayoutManager.VERTICAL,false));
        mAdapter = new DiamondDetailsAdapter(null,mTypeID);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    mPresenter.getDaimondDetails(UserManager.getInstance().getUserId(),mTypeID,mAssetsType,mPage);
                }
            }
        }, bindingView.recyclerView);

        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter){
                    mPage=1;
                    mPresenter.getDaimondDetails(UserManager.getInstance().getUserId(),mTypeID,mAssetsType,mPage);
                }
            }
        });
        mAdapter.setEmptyView(mEmptyView);
        bindingView.recyclerView.setAdapter(mAdapter);

        bindingView.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.black));
        bindingView.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                mPresenter.getDaimondDetails(UserManager.getInstance().getUserId(),mTypeID,mAssetsType,mPage);
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_diamond_detail;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!TextUtils.isEmpty(mTypeID)){
            mPresenter = new DiamondDetailsPresenter();
            mPresenter.attachView(this);
            if(0==mAssetsType&&!mPresenter.isLoading()){
                mPage=1;
                if(null!=mEmptyView) mEmptyView.showLoadingView();
                mPresenter.getDaimondDetails(UserManager.getInstance().getUserId(),mTypeID,mAssetsType,mPage);
            }
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mPresenter&&!mPresenter.isLoading()&&!TextUtils.isEmpty(mTypeID)){
            if(null!=mEmptyView) mEmptyView.showLoadingView();
            mPresenter.getDaimondDetails(UserManager.getInstance().getUserId(),mTypeID,mAssetsType,mPage);
        }
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mEmptyView){
            mEmptyView.stopLoading();
            mEmptyView=null;
        }
        mPresenter=null;
    }

    @Override
    public void showDiamondInfo(DiamondInfo info) {
        isRefresh=false;
        if(null!=mEmptyView) mEmptyView.showEmptyView();
        if(null==bindingView) return;
        bindingView.swipeRefreshLayout.setRefreshing(false);
        if(null==info){
            if(null!=mAdapter) mAdapter.loadMoreEnd();
            return;
        }
        if(null==getActivity()) return;
        if(getActivity() instanceof IntegralDetailsActivity){
            ((IntegralDetailsActivity) getActivity()).setDesp(info);
        }
    }

    @Override
    public void showDiamondDetails(List<DiamondInfo> data) {
        isRefresh=false;
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=bindingView)bindingView.swipeRefreshLayout.setRefreshing(false);
        if(null!= mAdapter){
            mAdapter.loadMoreComplete();
            if(1==mPage){
                mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void showDiamondError(int code, String data) {
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=bindingView)bindingView.swipeRefreshLayout.setRefreshing(false);
        if(null!= mAdapter){
            mAdapter.loadMoreFail();
            if(null== mAdapter.getData()|| mAdapter.getData().size()==0){
                mEmptyView.showErrorView();
            }
        }
    }
}