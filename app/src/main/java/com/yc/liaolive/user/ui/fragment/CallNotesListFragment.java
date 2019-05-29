package com.yc.liaolive.user.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.databinding.FragmentDiamondDetailBinding;
import com.yc.liaolive.msg.model.bean.CallMessageInfo;
import com.yc.liaolive.ui.adapter.CallNotesListAdapter;
import com.yc.liaolive.ui.contract.IndexMsgContract;
import com.yc.liaolive.ui.presenter.IndexMsgPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.videocall.bean.CallExtraInfo;
import com.yc.liaolive.videocall.manager.MakeCallManager;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/19
 * 视频通话记录、我的预约
 */

public class CallNotesListFragment extends BaseFragment <FragmentDiamondDetailBinding,IndexMsgPresenter> implements IndexMsgContract.View {

    private static final String TAG = "CallNotesListFragment";
    private CallNotesListAdapter mAdapter;
    private DataChangeView mEmptyView;
    private long lastID;
    private String mHostUrl;
    private int mItemType;
    private int mPosition;
    private int state;
    private boolean isRefresh=true;

    /**
     *
     * @param hostUrl
     * @param itemType
     * @param position
     * @param state 0：预约中\未接来电 1：预约成功 2：预约失败 -1：全部预约\通话记录
     * @return
     */
    public static CallNotesListFragment newInstance(String hostUrl, int itemType,int position,int state) {
        CallNotesListFragment fragment=new CallNotesListFragment();
        Bundle bundle=new Bundle();
        bundle.putString("hostUrl",hostUrl);
        bundle.putInt("itemType",itemType);
        bundle.putInt("position",position);
        bundle.putInt("state",state);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mHostUrl = arguments.getString("hostUrl");
            mItemType = arguments.getInt("itemType", CallNotesListAdapter.ITEM_TYPE_LET);
            mPosition = arguments.getInt("position", 0);
            state = arguments.getInt("state", -1);
        }
    }

    @Override
    protected void initViews() {
        bindingView.recyclerView.setLayoutManager(new IndexLinLayoutManager(getActivity(), IndexLinLayoutManager.VERTICAL, false));
        bindingView.recyclerView.setHasFixedSize(true);
        mAdapter = new CallNotesListAdapter(null);
        //加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    if(null!=mAdapter.getData()&&mAdapter.getData().size()>=10){
                        lastID =mAdapter.getData().get(mAdapter.getData().size()-1).getId();
                        mPresenter.getCallNotesList(mHostUrl,mItemType, lastID,state);
                    }else{
                        mAdapter.loadMoreEnd();
                    }
                }else{
                    mAdapter.loadMoreEnd();
                }
            }
        },bindingView.recyclerView);
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
                        //预约，回拨
                        case R.id.ll_anchor_make:
                            if(item.getId()>0&&item.getType()==2){
                                MobclickAgent.onEvent(getActivity(), "call_video_make");
                                CallExtraInfo callExtraInfo=new CallExtraInfo();
                                callExtraInfo.setToUserID(item.getUserid());
                                callExtraInfo.setToNickName(item.getNickname());
                                callExtraInfo.setToAvatar(item.getAvatar());
                                callExtraInfo.setRecevierID(String.valueOf(item.getId()));
                                MakeCallManager.getInstance().attachActivity(getActivity()).mackCall(callExtraInfo, 1);
                            }else{
                                if(!UserManager.getInstance().isAuthenState()){
                                    ToastUtils.showCenterToast("请等待主播回拨");
                                }
                            }
                            break;
                        //用户头像
                        case R.id.re_user_view:
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
                if(null!=view.getTag()){
                    CallMessageInfo item = (CallMessageInfo) view.getTag();
                    if(CallNotesListAdapter.ITEM_TYPE_LET==item.getItemType()){
                        PersonCenterActivity.start(getActivity(),item.getUserid());
                    }else if(CallNotesListAdapter.ITEM_TYPE_MAKE==item.getItemType()){
                        PersonCenterActivity.start(getActivity(),item.getUserid());
                    }
                }
            }
        });
        //占位
        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    lastID =0;
                    loadData();
                }
            }
        });
        mAdapter.setEmptyView(mEmptyView);
        bindingView.recyclerView.setAdapter(mAdapter);
        //下拉刷新监听
        bindingView.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastID =0;
                loadData();
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
        mPresenter = new IndexMsgPresenter();
        mPresenter.attachView(this);
        if(0==mPosition&&!mPresenter.isLoading()){
            lastID=0;
            if(null!=mEmptyView) mEmptyView.showLoadingView();
            mPresenter.getCallNotesList(mHostUrl,mItemType, lastID,state);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mPresenter&&!mPresenter.isLoading()){
            lastID=0;
            if(null!=mEmptyView) mEmptyView.showLoadingView();
            mPresenter.getCallNotesList(mHostUrl,mItemType, lastID,state);
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if(null!=mPresenter&&!mPresenter.isLoading()){
            mPresenter.getCallNotesList(mHostUrl,mItemType, lastID,state);
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showListResult(List<CallMessageInfo> data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swipeRefreshLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=mAdapter){
            mAdapter.loadMoreComplete();
            if(lastID==0){
                if(null!=mAdapter) mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void showListResultEmpty() {
        isRefresh=false;
        if(null!=bindingView) bindingView.swipeRefreshLayout.setRefreshing(false);
        if(null!=mEmptyView){
            mEmptyView.stopLoading();
            if(lastID==0){
                mEmptyView.showEmptyView();
            }
        }
        if(null!=mAdapter) mAdapter.loadMoreEnd();
    }

    @Override
    public void showListResultError(int code, String errorMsg) {
        if(null!=bindingView) bindingView.swipeRefreshLayout.setRefreshing(false);
        if(null!=mEmptyView&&mAdapter.getData().size()==0){
            mEmptyView.showErrorView(errorMsg);
        }
        if(null!=mAdapter) mAdapter.loadMoreFail();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        state=-1;
        MakeCallManager.getInstance().onDestroy();
    }
}