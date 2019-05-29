package com.yc.liaolive.user.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.TIMMessage;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentOnlineUserBinding;
import com.yc.liaolive.msg.model.Message;
import com.yc.liaolive.msg.model.MessageFactory;
import com.yc.liaolive.user.IView.IOnlineUserView;
import com.yc.liaolive.user.adapter.OnlineUserFragmentAdapter;
import com.yc.liaolive.user.manager.OnlineUserPresenter;
import com.yc.liaolive.user.model.bean.OnlineUserBean;
import com.yc.liaolive.view.layout.DataChangeView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 用户列表
 * Created by yangxueqin on 2018/12/15.
 */

public class OnlineUserFragment extends BaseFragment<FragmentOnlineUserBinding, OnlineUserPresenter> implements IOnlineUserView{

    private List<String> userIds = new ArrayList<>();

    private int page = 1;

    private int type;

    private DataChangeView mEmptyView;

    private OnlineUserFragmentAdapter mAdapter;

    private String mLastUserID;

    private String mLastLoginTime;

    private TextView leftCount; //剩余数

    private LinearLayout leftTimeLy;
    private TextView leftTime; //剩余时间

    public static OnlineUserFragment newInstance (int type) {
        OnlineUserFragment fragment = new OnlineUserFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type", 0);
        } else {
            type = 0;
        }
        EventBus.getDefault().register(this);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new OnlineUserPresenter(getActivity());
        mPresenter.attachView(this);
        mPresenter.setType(type );
        mAdapter.setmPresenter(mPresenter);
    }

    @Override protected void initViews() {
        bindingView.listView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OnlineUserFragmentAdapter(null, getContext());
        mAdapter.showEmptyView(true);
        mAdapter.loadMoreEnd();
        //加载更多监听
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null != mPresenter && !mPresenter.isLoading()){
                    if(null != mAdapter.getData()){
                        mLastUserID = mAdapter.getData().get(mAdapter.getData().size()-1).getUserid();
                        mLastLoginTime = mAdapter.getData().get(mAdapter.getData().size() - 1).getLogin_time();
                        mPresenter.getOnlineUserListData(page, type, mLastUserID, mLastLoginTime);
                    }else{
                        mAdapter.loadMoreEnd();
                    }
                }else{
                    mAdapter.loadMoreFail();
                }
            }
        }, bindingView.listView);

        //初始化占位布局
        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null != mPresenter && !mPresenter.isLoading()){
                    mEmptyView.showLoadingView();
                    page = 1;
                    mPresenter.getOnlineUserListData(page, type, "", "");
                }
            }
        });
        mAdapter.setEmptyView(mEmptyView);

        bindingView.listView.setAdapter(mAdapter);
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                refreshData();
            }
        });
        if (type == 1) {
            initHeadView();
        }
    }

    private void initHeadView () {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.online_user_head, null);
        leftCount = headView.findViewById(R.id.left_count);
        leftTime = headView.findViewById(R.id.left_time);
        leftTimeLy = headView.findViewById(R.id.left_timeLy);
        leftTimeLy.setVisibility(View.GONE);
        mAdapter.addHeaderView(headView);
    }

    /**
     *
     * @param mTime 单位秒
     */
    public void setCallLeftTimeData (long mTime) {
        if (mTime > 0) {
            leftTimeLy.setVisibility(View.VISIBLE);
            mPresenter.startCountDown(mTime * 1000);
        } else {
            leftTimeLy.setVisibility(View.GONE);
        }
    }

    @Override protected int getLayoutId() {
        return R.layout.fragment_online_user;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
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
                        } else if (userIds.size() == 0) {
                            page = 1;
                            mPresenter.getOnlineUserListData(page, type, "", "");
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

    @Override public void setDataView(OnlineUserBean userBean) {
        if (type == 1) {
            mPresenter.initCallCountTimeListener();
        }

        bindingView.swiperLayout.setRefreshing(false);
        mAdapter.loadMoreComplete();
        if (page == 1) {
            mAdapter.setNewData(userBean.getList());
            this.userIds.clear();
        } else {
            mAdapter.addData(userBean.getList());
        }
        page ++;
        setUserIds(userBean);
    }

    /**
     * 存储用户列表用户id
     * @param userBean
     */
    private void setUserIds(OnlineUserBean userBean) {
        for (OnlineUserBean.OnlineUserItemBean itemBean : userBean.getList()) {
            userIds.add(itemBean.getUserid());
        }
    }

    public void setListEnd(boolean isEnd) {
        if (isEnd) {
            mAdapter.loadMoreEnd();
        } else {
            mAdapter.loadMoreEnd();
        }
    }

    @Override
    public void showError(String msg) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter){
            if(page == 1){
                if(null!=mEmptyView) mEmptyView.showErrorView(msg);
            } else {
                mAdapter.loadMoreFail();
            }
        }
    }

    @Override public void setLeftTimeView(String time) {
        if (leftTime != null) {
            leftTime.setText(time);
        }
    }

    @Override
    public void setCallLeftCountView(int count) {
        SpannableString spannableString = new SpannableString("剩余失败次数："+count + " 次");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#ff7575"));
        spannableString.setSpan(colorSpan, 7, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        leftCount.setText(spannableString);
    }

    @Override
    public void setLeftTimeComplete() {
        leftTimeLy.setVisibility(View.GONE);
    }

    @Override
    public void showListEmpty() {
        if(null != bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null != mEmptyView) mEmptyView.showEmptyView();
        if(null != mAdapter){
            mAdapter.loadMoreEnd();
            if(page == 1){
                mAdapter.setNewData(null);//如果是第一页的话，直接摸空数据
                if(null!=mEmptyView) mEmptyView.showErrorView("暂无数据",
                        R.drawable.ic_list_empty_icon);
            }
        }
    }

    /**
     * 扣除失败剩余次数
     */
    @Subscriber (tag = Constant.OBSERVER_CMD_CALL_EXCEPTION)
    public void callFailedLeftCount(String userid) {
        if (!TextUtils.isEmpty(userid) && mPresenter != null && mPresenter.getVideoChatUserId().equals(userid)) {
            int count = mPresenter.getCall_sum_num() - 1;
            if (count > 0) {
                mPresenter.setCall_sum_num(count);
                setCallLeftCountView(count);
            }
        }
    }

    /**
     * 通话成功，刷新列表
     */
    @Subscriber (tag = "call_end_success")
    public void callFailedFreshBtn(String userid) {
        if (!TextUtils.isEmpty(userid) && mPresenter.getVideoChatUserId().equals(userid)
                && mPresenter != null) {
            page = 1;
            mPresenter.getOnlineUserListData(page, type, "", "");
        }
    }

    private void refreshData() {
        page = 1;
        if(null == mAdapter && userIds.size() == 0){
            if(null!=mEmptyView) mEmptyView.showLoadingView();
        }
        mPresenter.getOnlineUserListData(page, type, "", "");
    }

    @Subscriber (tag = "NEW_CHAT_MESSAGE")
    public void receiveNewMessage (TIMMessage msg) {
        Message message = MessageFactory.getMessage(msg);
        if(null != message){
            String id = message.getSender();
            int index = userIds.indexOf(id);
            if (index >= 0) {
                OnlineUserBean.OnlineUserItemBean itemBean =  mAdapter.getData().get(index);
                int count = itemBean.getRedpoint_count();
                itemBean.setRedpoint_count(count + 1);
                mAdapter.notifyDataSetChanged();
//                mAdapter.notifyItemChanged(index, "update");
            }
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
