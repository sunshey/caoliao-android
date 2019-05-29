package com.yc.liaolive.user.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.HelpInfo;
import com.yc.liaolive.databinding.ActivityBlacklistBinding;
import com.yc.liaolive.mine.adapter.HelpAdapter;
import com.yc.liaolive.ui.activity.HelpDetailActivity;
import com.yc.liaolive.user.IView.HelpContract;
import com.yc.liaolive.user.manager.HelpPresenter;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * Created by wanglin  on 2018/7/5 14:50.
 */
public class HelpActivity extends BaseActivity<ActivityBlacklistBinding> implements HelpContract.View {

    private HelpAdapter helpAdapter;
    private HelpPresenter helpPresenter;
    private DataChangeView mEmptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        helpPresenter = new HelpPresenter(this);
        helpPresenter.attachView(this);
        helpPresenter.getHelpList();
    }

    @Override
    public void initViews() {
        bindingView.commentTitleView.setTitle(getString(R.string.mine_help));
        bindingView.swiperLayout.setPadding(0, Utils.dip2px(this, 5), 0, 0);
        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        helpAdapter = new HelpAdapter(null);
        mEmptyView = new DataChangeView(HelpActivity.this);
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=helpPresenter){
                    helpPresenter.getHelpList();
                }
            }
        });
        mEmptyView.showLoadingView();
        helpAdapter.setEmptyView(mEmptyView);
        bindingView.recyclerView.setAdapter(helpAdapter);
        initListener();
    }

    private void initListener() {
        bindingView.commentTitleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }
        });
        helpAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HelpInfo helpInfo = helpAdapter.getItem(position);
                Intent intent = new Intent(HelpActivity.this, HelpDetailActivity.class);
                intent.putExtra("id", helpInfo.getId());
                startActivity(intent);
            }
        });

        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                helpPresenter.getHelpList();
            }
        });
    }

    @Override
    public void initData() {
    }

    @Override
    public void showHelpList(List<HelpInfo> data) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=helpAdapter) helpAdapter.setNewData(data);
    }

    @Override
    public void shoFailView() {

    }

    @Override
    public void onDestroy() {
        if(null!=helpPresenter) helpPresenter.detachView();
        super.onDestroy();
    }

    @Override

    public void hide() {
        if(null!=mEmptyView) mEmptyView.stopLoading();
    }

    @Override
    public void showLoading() {
        if(null!=mEmptyView) mEmptyView.showLoadingView();
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

    public void showErrorView() {

    }

    @Override
    public void complete() {


    }
}
