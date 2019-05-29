package com.yc.liaolive.recharge.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.recharge.adapter.RechargeAwardListAdapter;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.TaskInfo;
import com.yc.liaolive.databinding.FragmentListLayoutBinding;
import com.yc.liaolive.ui.contract.TaskCenterContract;
import com.yc.liaolive.ui.presenter.TaskCenterPresenter;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.recharge.view.RechargeAwardHeadView;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/19
 * 充值奖励
 */

public class RechargeAwardFragment extends BaseFragment<FragmentListLayoutBinding,TaskCenterPresenter> implements TaskCenterContract.View {

    private RechargeAwardListAdapter mAdapter;
    private RechargeAwardHeadView mHeadView;
    private DataChangeView mDataChangeView;

    @Override
    protected void initViews() {
        bindingView.recylerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mAdapter = new RechargeAwardListAdapter(null);
        mAdapter.setOnGiftChangedListener(new RechargeAwardListAdapter.OnGiftChangedListener() {
            @Override
            public void onDraw(int position, final TaskInfo taskInfo) {
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mAdapter.loadMoreEnd();
            }
        },bindingView.recylerView);

        //占位布局
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter){
                    mDataChangeView.showLoadingView();
                    mPresenter.getRechargeTasks();
                }
            }
        });
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);

        //添加一个头部
        mHeadView = new RechargeAwardHeadView(getActivity());
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        mHeadView.setLayoutParams(layoutParams);
        mAdapter.addHeaderView(mHeadView);

        bindingView.recylerView.setAdapter(mAdapter);

        //刷新监听
        bindingView.swiperLayout.getResources().getColor(R.color.black);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter) mPresenter.getRechargeTasks();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new TaskCenterPresenter();
        mPresenter.attachView(this);
        mPresenter.getRechargeTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mHeadView) mHeadView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mHeadView) mHeadView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mHeadView) mHeadView.onDestroy();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showTasks(List<TaskInfo> data) {
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter) mAdapter.setNewData(data);
    }

    @Override
    public void showTaskEmpty() {
        if(null!=mDataChangeView) mDataChangeView.showEmptyView(false);
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
    }

    @Override
    public void showTaskError(int code, String errorMsg) {
        if(null!=mDataChangeView) mDataChangeView.showErrorView();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
    }

    @Override
    public void showGetTaskResult(String data) {

    }

    @Override
    public void showGetTaskError(int code, String errorMsg) {

    }
}
