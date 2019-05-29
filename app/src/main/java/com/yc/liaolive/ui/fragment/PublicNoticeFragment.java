package com.yc.liaolive.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.ui.adapter.PublicNoticeListAdapter;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.HomeNoticeInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentListLayoutBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.contract.PublicNoticeContract;
import com.yc.liaolive.ui.presenter.PublicNoticPresenter;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;
import java.io.Serializable;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/6
 * 系统公告
 */

public class PublicNoticeFragment extends BaseFragment<FragmentListLayoutBinding,PublicNoticPresenter> implements PublicNoticeContract.View {

    public static final String TAG = "PublicNoticeFragment";
    private PublicNoticeListAdapter mAdapter;
    private int mPage=0;
    private DataChangeView mDataChangeView;

    @Override
    protected void initViews() {
        bindingView.recylerView.setLayoutManager(new IndexLinLayoutManager(getActivity(),IndexLinLayoutManager.VERTICAL,false));
        mAdapter = new PublicNoticeListAdapter(null);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<HomeNoticeInfo> data = mAdapter.getData();
                if(null!=data&&data.size()>position){
                    HomeNoticeInfo publicNoticeInfo = data.get(position);
                    takeList(publicNoticeInfo);
                    //通知详情
                    ContentFragmentActivity.start(getActivity(),Constant.FRAGMENT_TYPE_NOTICE_DETAILS,publicNoticeInfo.getTitle(),String.valueOf(publicNoticeInfo.getAnnounce_id()),null);
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    List<HomeNoticeInfo> data = mAdapter.getData();
                    if(null!=data&&data.size()>=10){
                        mPage++;
                        mPresenter.getPublicNotices(mPage);
                    }else{
                        mAdapter.loadMoreEnd();
                    }
                }
            }
        },bindingView.recylerView);
        //占位布局
        mDataChangeView = new DataChangeView(getActivity());
        mDataChangeView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter){
                    mDataChangeView.showLoadingView();
                    mPage=0;
                    mPage++;
                    mPresenter.getPublicNotices(mPage);
                }
            }
        });
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);

        bindingView.recylerView.setAdapter(mAdapter);
        //刷新监听
        bindingView.swiperLayout.getResources().getColor(R.color.black);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage=0;
                    mPage++;
                    mPresenter.getPublicNotices(mPage);
                }
            }
        });
    }

    /**
     * 记录阅读记录
     * @param noticeInfo
     */
    private void takeList(HomeNoticeInfo noticeInfo) {
        if(null==noticeInfo) return;
        List<HomeNoticeInfo> cacheNotices = (List<HomeNoticeInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOME_NOTICE);
        if(null!=cacheNotices){
            for (HomeNoticeInfo cacheNotice : cacheNotices) {
                if(noticeInfo.getAnnounce_id()==cacheNotice.getAnnounce_id()){
                    cacheNotice.setRead(true);
                    break;
                }
            }
            ApplicationManager.getInstance().getCacheExample().remove(Constant.CACHE_HOME_NOTICE);
            ApplicationManager.getInstance().getCacheExample().put(Constant.CACHE_HOME_NOTICE, (Serializable) cacheNotices);
            boolean isExieNoRead=true;
            for (HomeNoticeInfo homeNoticeInfo : cacheNotices) {
                if(!homeNoticeInfo.isRead()){
                    isExieNoRead=false;
                    break;
                }
            }
            //更新首页消息状态
            ApplicationManager.getInstance().observerUpdata(isExieNoRead?Constant.OBSERVER_CMD_ALLREAD_MESSAGE :Constant.OBSERVER_CMD_HAS_NEW_MESSAGE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new PublicNoticPresenter();
        mPresenter.attachView(this);
        List<HomeNoticeInfo> cacheNotices = (List<HomeNoticeInfo>) ApplicationManager.getInstance().getCacheExample().getAsObject(Constant.CACHE_HOME_NOTICE);
        if(null!=cacheNotices&&cacheNotices.size()>0){
            if(null!=mDataChangeView) mDataChangeView.showEmptyView(false);
            if(null!=mAdapter) mAdapter.setNewData(cacheNotices);
        }
        mPage++;
        mPresenter.getPublicNotices(mPage);
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showPublicNotices(List<HomeNoticeInfo> data) {
        if(null!=mDataChangeView) mDataChangeView.stopLoading();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter) mAdapter.setNewData(data);
    }

    @Override
    public void showPublicNoticeEmpty() {
        if(null!=mDataChangeView) mDataChangeView.showEmptyView(false);
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(mPage>0) mPage--;
    }

    @Override
    public void showPublicNoticeError(int code, String errorMsg) {
        if(null!=mDataChangeView) mDataChangeView.showErrorView();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(mPage>0) mPage--;
    }

    @Override
    public void showNoticeDetails(HomeNoticeInfo data) {

    }

    @Override
    public void showNoticeDetailError(int code, String errorMsg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mDataChangeView) mDataChangeView.onDestroy();
    }
}
