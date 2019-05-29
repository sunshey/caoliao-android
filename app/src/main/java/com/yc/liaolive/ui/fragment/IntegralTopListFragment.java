package com.yc.liaolive.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseContract;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.bean.IntegralUser;
import com.yc.liaolive.databinding.FragmentIntegralTopListRecylerBinding;
import com.yc.liaolive.live.util.LiveUtils;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.model.ItemBothEndsSpacesItemDecoration;
import com.yc.liaolive.ui.adapter.TopTableListAdapter;
import com.yc.liaolive.ui.contract.TopTableContract;
import com.yc.liaolive.ui.presenter.TopTablePresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.TopTableHeadView;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * TinyHung@Outlook.com
 * 2018/6/30
 * 积分排行榜单
 * mType: 0 总榜单 1：今日榜单
 */

public class IntegralTopListFragment extends BaseFragment<FragmentIntegralTopListRecylerBinding, TopTablePresenter> implements BaseContract.BaseView, TopTableContract.View {

    private int mType;
    private TopTableListAdapter mAdapter;
    private boolean isRefresh = true;
    private TopTableHeadView mTableHeadView;
    private String mHomeUserid;

    public static IntegralTopListFragment newInstance(String homeUserid, int type) {
        IntegralTopListFragment fragment = new IntegralTopListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("to_userid", homeUserid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mType = arguments.getInt("type");
            mHomeUserid = arguments.getString("to_userid");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new TopTablePresenter();
        mPresenter.attachView(this);
        if(0==mType){
            mPresenter.getTopTables(mHomeUserid, String.valueOf(mType));
        }
    }

    @Override
    protected void initViews() {
        bindingView.recyerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bindingView.recyerView.addItemDecoration(new ItemBothEndsSpacesItemDecoration(ScreenUtils.dpToPxInt(8f)));
        mAdapter = new TopTableListAdapter(null, mType);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mAdapter.loadMoreEnd();
            }
        }, bindingView.recyerView);
        //占位布局
        bindingView.emptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLists();
            }
        });
        bindingView.emptyView.showLoadingView();
        //点击监听
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<FansInfo> data = mAdapter.getData();
                if (null != data && data.size() > position) {
                    FansInfo topTableInfo = data.get(position);
                    PersonCenterActivity.start(getActivity(), topTableInfo.getUserid());
                }
            }
        });
        bindingView.recyerView.setAdapter(mAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_integral_top_list_recyler;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView&&null!=mPresenter&&null!=mAdapter){
            loadLists();
        }
    }

    private void chechIsInit () {
        rx.Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (bindingView == null || !isRefresh || isResumed()) {
                            chechIsInit();
                        } else {
                            loadLists();
                        }
                    }
                });
    }

    /**
     * 添加一个头部
     *
     * @param headerData
     */
    public void addHeaderData(List<FansInfo> headerData) {
        if(null!=mTableHeadView&&null!=mAdapter){
            mAdapter.removeHeaderView(mTableHeadView);
            mTableHeadView=null;
        }
        mTableHeadView = new TopTableHeadView(getActivity());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mTableHeadView.setLayoutParams(layoutParams);
        mTableHeadView.setType(mType);
        mTableHeadView.setData(headerData);
        mTableHeadView.setOnUserClickListener(new TopTableHeadView.OnUserClickListener() {
            @Override
            public void onClickHead(String userID) {
                PersonCenterActivity.start(getActivity(), userID);
            }
        });
        mAdapter.addHeaderView(mTableHeadView);
        bindingView.emptyView.stopLoading();
        bindingView.emptyView.setVisibility(View.GONE);
    }

    /**
     * 加载排行榜
     */
    private void loadLists() {
        mPresenter.getTopTables(mHomeUserid, String.valueOf(mType));
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showTopTbaleList(List<FansInfo> data, IntegralUser userData) {
        isRefresh = false;
        if (null != bindingView) {
            bindingView.emptyView.stopLoading();
            bindingView.emptyView.setVisibility(View.GONE);
        }
        if (null != mAdapter) {
            mAdapter.loadMoreComplete();//加载完成
            if (null != mTableHeadView) {
                mAdapter.removeHeaderView(mTableHeadView);
                mTableHeadView = null;
            }
            if (data.size() > 3) {
                List<FansInfo> headerData = data.subList(0, 3);
                addHeaderData(headerData);
                mAdapter.setNewData(data.subList(3,data.size()));
            } else {
                addHeaderData(data);
            }
            setTneselfTopTable(userData);
        }
    }

    /**
     * 更新自己的榜单
     *
     * @param userData
     */
    private void setTneselfTopTable(IntegralUser userData) {
        if (null == userData) return;
        bindingView.oneselfTopView.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(UserManager.getInstance().getAvatar())
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(bindingView.oneselfIcIcon);
        bindingView.oneselfTvNum.setText(userData.getIndex() == -1 ? "未上榜" : String.valueOf(userData.getIndex()));
        bindingView.oneselfTvNum.setTextColor(userData.getIndex() == -1 ? Color.parseColor("#939393") : Color.parseColor("#F35263"));
        bindingView.oneselfTvNum.setText(userData.getIndex()==-1?"未上榜":String.valueOf(userData.getIndex()));
        bindingView.oneselfTvNum.setTextColor(userData.getIndex()==-1?Color.parseColor("#939393"):Color.parseColor("#F35263"));
        bindingView.oneselfNickname.setText(UserManager.getInstance().getNickname());
        bindingView.oneselfTvTotalPoints.setText(0 == mType ? String.format(Locale.CHINA, "%d亲密度", userData.getTotal_points()) : String.format(Locale.CHINA, "%d亲密度", userData.getDay_points()));
        try {
            LiveUtils.setUserGradle(bindingView.oneselfUserGradle, UserManager.getInstance().getLevel_integral());
            LiveUtils.setUserBlockVipGradle(bindingView.oneselfVipGradle,UserManager.getInstance().getUserVip());//设置用户vip等级
//            LiveUtils.setUserVipGradle(bindingView.oneselfVipGradle, VideoApplication.getInstance().getUserInfo().getVip());
            LiveUtils.setUserSex(bindingView.oneselfUserSex,UserManager.getInstance().getSex());
        } catch (Exception e) {

        }
    }

    @Override
    public void showTopTbaleEmpty() {
        isRefresh = false;
        if (null != bindingView)
            bindingView.emptyView.showEmptyView("排行榜为空", R.drawable.ic_list_empty_icon);
        if (null != mAdapter) {
            mAdapter.loadMoreEnd();//没有更多的数据了
            //如果当前用户在第一页的时候获取视频为空，表示该用户没有关注用户
            mAdapter.setNewData(null);
        }
    }

    @Override
    public void showTopTbaleError(int code, String errorMsg) {
        if (null != mAdapter) {
            mAdapter.loadMoreFail();
            if (null != bindingView) bindingView.emptyView.showErrorView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bindingView) bindingView.emptyView.onDestroy();
    }
}