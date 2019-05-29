package com.yc.liaolive.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.databinding.ActivityBlacklistBinding;
import com.yc.liaolive.mine.adapter.BlackListAdapter;
import com.yc.liaolive.ui.contract.BlackListContract;
import com.yc.liaolive.ui.dialog.RemoveBlackListDialog;
import com.yc.liaolive.ui.presenter.BlackListPresenter;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;

import java.util.List;

/**
 * Created by wanglin  on 2018/7/5 10:06.
 */
public class BlacklistActivity extends BaseActivity<ActivityBlacklistBinding> implements BlackListContract.View {

    private BlackListAdapter blackListAdapter;
    private BlackListPresenter mPresenter;

    private int page = 1;
    private int PAGE_SIZE = 10;
    private DataChangeView mEmptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        mPresenter = new BlackListPresenter(this);
        mPresenter.attachView(this);
        mPresenter.getBlackList(page, PAGE_SIZE);
    }

    @Override
    public void initViews() {
        bindingView.commentTitleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }
        });

        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        blackListAdapter = new BlackListAdapter(null);
        mEmptyView = new DataChangeView(BlacklistActivity.this);
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter){
                    page = 1;
                    mPresenter.getBlackList(page, PAGE_SIZE);
                }
            }
        });
        mEmptyView.showLoadingView();
        blackListAdapter.setEmptyView(mEmptyView);
        bindingView.recyclerView.setAdapter(blackListAdapter);
        initListener();
    }

    private void initListener() {
        blackListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final FansInfo fansInfo = blackListAdapter.getItem(position);
                RemoveBlackListDialog blackListDialog = new RemoveBlackListDialog(BlacklistActivity.this, fansInfo.getNickname());
                blackListDialog.show();
                blackListDialog.setOnConfirmListener(new RemoveBlackListDialog.onConfirmListener() {
                    @Override
                    public void onConfirm() {
                        mPresenter.removeBlackList(fansInfo);
                    }
                });
                return false;
            }
        });

        blackListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.getBlackList(page, PAGE_SIZE);
            }
        }, bindingView.recyclerView);

        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mPresenter.getBlackList(page, PAGE_SIZE);
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void showBlackList(List<FansInfo> list) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if (list != null&&null!=blackListAdapter) {
            if (page == 1) {
                blackListAdapter.setNewData(list);
            } else {
                blackListAdapter.addData(list);
            }

            if (list.size() == PAGE_SIZE) {
                page++;
                blackListAdapter.loadMoreComplete();
            } else {
                blackListAdapter.loadMoreEnd();
            }
        }
    }

    @Override
    public void showRemoveResult(FansInfo fansInfo) {
        try {
            if(null!=blackListAdapter) blackListAdapter.remove(blackListAdapter.getData().indexOf(fansInfo));
        }catch (RuntimeException e){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mEmptyView) mEmptyView.onDestroy();
        if(null!=mPresenter) mPresenter.detachView();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void hide() {
        if(null!=mEmptyView) mEmptyView.stopLoading();
    }

    @Override
    public void showNoData() {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.showEmptyView();
    }

    @Override
    public void showNoNet() {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.showErrorView();
    }

    @Override
    public void showLoading() {
        if(null!=mEmptyView) mEmptyView.showLoadingView();
    }

    public void complete() {
    }
}
