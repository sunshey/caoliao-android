package com.yc.liaolive.index.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentIndexOneListBinding;
import com.yc.liaolive.index.adapter.LiveListOneAdapter;
import com.yc.liaolive.index.model.bean.OneListBean;
import com.yc.liaolive.live.bean.RoomExtra;
import com.yc.liaolive.live.bean.RoomList;
import com.yc.liaolive.live.manager.LiveRoomManager;
import com.yc.liaolive.live.ui.activity.AsmrRoomPullActivity;
import com.yc.liaolive.live.ui.activity.LiveRoomPullActivity;
import com.yc.liaolive.live.view.FrequeControl;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.ui.activity.VerticalAnchorPlayerAvtivity;
import com.yc.liaolive.ui.contract.IndexListContract;
import com.yc.liaolive.ui.presenter.IndexListPresenter;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * TinyHung@Outlook.com
 * 2018/10/14
 * 主页 1对1 列表
 *
*/

public class IndexOneListFragment extends BaseFragment<FragmentIndexOneListBinding, IndexListPresenter> implements IndexListContract.View, Observer {

    private static final String TAG = "IndexOneListFragment";
    private String mLastUserID="";
    private int mPage;
    private LiveListOneAdapter mAdapter;
    private boolean isRefresh=true;
    private int mIndex;//当前片段的Index
    private DataChangeView mEmptyView;
    private IndexFragment mParentFragment;
    private FrequeControl mFrequeControl;
    private String type;//默认-1 1.获取1对1  2.获取一对多  -1.获取全部  3:只获取预设主播1v多 5:最新的

    public static IndexOneListFragment getInstance(int index, String type) {
        IndexOneListFragment fragment=new IndexOneListFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("index", index);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mIndex = arguments.getInt("index");
            type = arguments.getString("type","3");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_one_list;
    }

    @Override
    protected void initViews() {
        IndexGridLayoutManager gridLayoutManager = new IndexGridLayoutManager(getActivity(),2, IndexGridLayoutManager.VERTICAL,false);
        bindingView.recylerView.setLayoutManager(gridLayoutManager);
        initAdapter();
        //初始化占位布局
        mEmptyView = bindingView.loadingView;
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mEmptyView.showLoadingView();
                    mLastUserID="";
                    mPage=1;
                    mPresenter.getLiveLists(mLastUserID, type, mPage);
                }
            }
        });
//        mAdapter.setEmptyView(mEmptyView);
        //刷新
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        //销毁监听
        bindingView.recylerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                AutoBannerLayout bannerLayout = (AutoBannerLayout) view.findViewById(R.id.item_banner_view);
                if(null!=bannerLayout){
                    bannerLayout.onReset();
                    mAdapter.reset();
                }
            }
        });

        //滚动监听
        bindingView.recylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滚动过程中触发
                if(null!=mParentFragment&&recyclerView.getScrollState()==1){
                    //上滑
                    if(dy<0){
                        mParentFragment.showMainTabLayout(true);
                        //下滑
                    }else if(dy>0){
                        mParentFragment.showMainTabLayout(false);
                    }
                }
            }
        });
    }

    private void initAdapter () {
        mAdapter = new LiveListOneAdapter(null,type);
        mAdapter.showEmptyView(true);
        //加载更多监听
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    if(null!=mAdapter.getData()&&mAdapter.getData().size()>=10){
                        mLastUserID=mAdapter.getData().get(mAdapter.getData().size()-1).getUserid();
                        mPage++;
                        mPresenter.getLiveLists(mLastUserID, type, mPage);
                    }else{
                        mAdapter.loadMoreEnd();
                    }
                }else{
                    mAdapter.loadMoreFail();
                }
            }
        }, bindingView.recylerView);
        //条目监听
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    RoomList roomList = (RoomList) view.getTag();
                    startRoom(roomList,position);
                }
            }
        });
        //列表类型广告条目监听
        mAdapter.setOnMultiItemClickListener(new LiveListOneAdapter.OnMultiItemClickListener() {
            @Override
            public void onBannerClick(BannerInfo bannerInfo) {
                if(null==bannerInfo) return;
                if (!TextUtils.isEmpty(bannerInfo.getJump_url())) {
                    CaoliaoController.start(bannerInfo.getJump_url(),true,null);
                }
            }
        });
        bindingView.recylerView.setAdapter(mAdapter);
    }

    /**
     * 进入房间
     * @param roomList
     * @param position
     */
    private void startRoom(RoomList roomList, int position) {
        if(null==roomList||null==mAdapter) return;
        if(null==mFrequeControl){
            mFrequeControl = new FrequeControl();
            mFrequeControl.init(1, 2);
        }
        //ASMR主播
        if("1".equals(roomList.getAsmr())){
            if(Constant.INDEX_ITEM_TYPE_ROOM.equals(roomList.getItemCategory())){
                RoomExtra roomExtra=new RoomExtra();
                roomExtra.setUserid(roomList.getUserid());
                roomExtra.setNickname(roomList.getNickname());
                roomExtra.setAvatar(roomList.getAvatar());
                roomExtra.setRoom_id(roomList.getRoomid());
                if(null!=roomList&&null!=roomList.getMy_image_list()&&roomList.getMy_image_list().size()>0){
                    roomExtra.setFrontcover(roomList.getMy_image_list().get(0).getImg_path());
                }else if(!TextUtils.isEmpty(roomList.getFrontcover())){
                    roomExtra.setFrontcover(roomList.getFrontcover());
                }else{
                    roomExtra.setFrontcover(roomList.getAvatar());
                }
                AsmrRoomPullActivity.start(getActivity(),roomExtra);
                return;
            }
            PersonCenterActivity.start(getActivity(), roomList.getUserid());
            return;
        }
        //普通主播
        if(Constant.INDEX_ITEM_TYPE_ROOM.equals(roomList.getItemCategory())){
            RoomExtra roomExtra=new RoomExtra();
            roomExtra.setUserid(roomList.getUserid());
            roomExtra.setNickname(roomList.getNickname());
            roomExtra.setAvatar(roomList.getAvatar());
            roomExtra.setRoom_id(roomList.getRoomid());
            roomExtra.setPull_steram(TextUtils.isEmpty(roomList.getPush_stream_flv())?roomList.getPush_stream():roomList.getPush_stream_flv());
            //直播间
            LiveRoomPullActivity.start(getActivity(),roomExtra);
            return;
        }
        //在线主播
        if (roomList.getVideo_chat() != null && !TextUtils.isEmpty(roomList.getVideo_chat().getFile_path())) {
            LiveRoomManager.getInstance().setPosition(position).setType(type).setPage(mPage).setLastUserID(mLastUserID).setData(mAdapter.getData());
            VerticalAnchorPlayerAvtivity.start(getActivity(),type,position,mPage,null);
            return;
        }
        //在线用户
        PersonCenterActivity.start(getActivity(), roomList.getUserid());
    }

    private int currentOffset=0;
    private int scollOffset(int verticalOffset) {
        int abs = Math.abs(verticalOffset);
        if(abs>currentOffset){
            currentOffset=abs;
            return 1;
        }
        if(abs<currentOffset){
            currentOffset=abs;
            return -1;
        }
        currentOffset=abs;
        return 0;
    }

    /**
     * 来自主页的刷新事件
     */
    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
        if(null==bindingView )return;
        if(null!=mPresenter&&!mPresenter.isLoading()){
            mLastUserID="";
            mPage=1;
            if(null!=mAdapter){
                if(null==mAdapter.getData()||mAdapter.getData().size()<=0){
                    if(null!=mEmptyView) mEmptyView.showLoadingView();
                }else{
                    bindingView.swiperLayout.setRefreshing(true);
                }
            }
            bindingView.recylerView.scrollToPosition(0);
            mPresenter.getLiveLists(mLastUserID, type, mPage);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
        if(getParentFragment() instanceof IndexFragment){
            mParentFragment = (IndexFragment) getParentFragment();
        }
        mPresenter=new IndexListPresenter();
        mPresenter.attachView(this);
        //第一个界面初始就刷新
//        if(0 == mIndex && getUserVisibleHint()){
//            if(null!=mEmptyView) mEmptyView.showLoadingView();
//            mPresenter.getLiveLists(mLastUserID, type, mPage);
//        }
    }

    private void checkIsInitView () {
        rx.Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (getView() == null || bindingView == null || null == mPresenter || !isResumed()) {
                            checkIsInitView();
                        } else {
                            initData();
                        }
                    }
                });
    }

    private void initData () {
        if(null!=mAdapter) mAdapter.onResume();
        if(isRefresh && null!= bindingView && null != mPresenter
                && !mPresenter.isLoading()){
            mLastUserID="";
            mPage=1;
            if(mAdapter.getData().size()<=0){
                if(null!=mEmptyView) mEmptyView.showLoadingView();
            }
            mPresenter.getLiveLists(mLastUserID, type, mPage);
        }
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        if(null!=mAdapter) mAdapter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mAdapter) mAdapter.onResume();
        if(VideoApplication.getInstance().isIndexRefresh()&&null!=mPresenter&&!mPresenter.isLoading()){
            refreshData();
            VideoApplication.getInstance().setIndexRefresh(false);
        }
        if(null!=LiveRoomManager.getInstance().getType()&&type.equals(LiveRoomManager.getInstance().getType())&&null!=LiveRoomManager.getInstance().getData()) {
            if (LiveRoomManager.getInstance().getInstance().getPosition() > 0) {
                mPage = LiveRoomManager.getInstance().getPage();//还原当前加载到第几页了
                mAdapter.setNewData(LiveRoomManager.getInstance().getData());
                mLastUserID=LiveRoomManager.getInstance().getLastUserID();
                if (null != mAdapter && null != mAdapter.getData() && mAdapter.getData().size() > LiveRoomManager.getInstance().getPosition()) {
                    RecyclerView.LayoutManager layoutManager = bindingView.recylerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见item的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见item的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        boolean refresh = Utils.isRefresh(LiveRoomManager.getInstance().getPosition(), lastItemPosition, firstItemPosition);
                        if (refresh)
                            linearManager.scrollToPositionWithOffset(LiveRoomManager.getInstance().getPosition(), 0);
                    }
                }
            }
            LiveRoomManager.getInstance().setPosition(0);
            LiveRoomManager.getInstance().setType(null);
            LiveRoomManager.getInstance().setPage(0);
            LiveRoomManager.getInstance().setLastUserID(null);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            checkIsInitView();
            MobclickAgent.onPageStart("main_1v1_list_"+type);
            MobclickAgent.onEvent(getActivity(), "main_1v1_list_"+type);
        } else if (isResumed()) {
            MobclickAgent.onPageEnd("main_1v1_list_"+type);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mAdapter) mAdapter.onPause();
        if (getUserVisibleHint()) {
            MobclickAgent.onPageEnd("main_1v1_list_"+type);
        }
    }

    private void refreshData() {
        mLastUserID="";
        mPage=1;
        if(null!=mAdapter){
            if(null==mAdapter.getData()||mAdapter.getData().size()<=0){
                if(null!=mEmptyView) mEmptyView.showLoadingView();
            }
        }
        mPresenter.getLiveLists(mLastUserID, type, mPage);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mParentFragment=null;
    }

    @Override
    public void showErrorView() {
    }

    @Override
    public void complete() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mEmptyView) mEmptyView.onDestroy();
        isRefresh=true; mIndex=0;currentOffset=0;
    }

    @Override
    public void showLiveRooms(OneListBean data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) {
            mEmptyView.stopLoading();
            mEmptyView.setVisibility(View.GONE);
        }
        if(null!=mAdapter){
            mAdapter.loadMoreComplete();
            if(null==mLastUserID||mLastUserID.length()<=0){
                if(null!=mAdapter) {
                    initAdapter();
                    mAdapter.setImage_small_show(data.getImage_small_show());
                    mAdapter.setNewData(data.getList());
                }
            }else{
                mAdapter.addData(data.getList());
            }
        }
    }

    @Override
    public void showLiveRoomEmpty() {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter){
            mAdapter.loadMoreEnd();
            if(null==mLastUserID||mLastUserID.length()<=0){
                mAdapter.setNewData(null);//如果是第一页的话，直接摸空数据
                if(null!=mEmptyView) mEmptyView.showErrorView("暂无数据，稍后再来哦",R.drawable.ic_list_empty_icon);
            }
        }
    }

    @Override
    public void showLiveRoomError(int code, String errorMsg) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter){
            mAdapter.loadMoreFail();
            List<RoomList> data = mAdapter.getData();
            if(null==data||data.size()<=0){
                if(null!=mEmptyView) mEmptyView.showErrorView(errorMsg);
            } else {
                ToastUtils.showToast(errorMsg);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg && arg instanceof String){
            String cmd= (String) arg;
            if(TextUtils.equals(Constant.OBSERVER_HAS_REFRESH_PERMISSION,cmd)){
                if(null!=bindingView) bindingView.swiperLayout.setEnabled(true);
            }else if(TextUtils.equals(Constant.OBSERVER_DOTHAS_REFRESH_PERMISSION,cmd)){
                if(null!=bindingView) bindingView.swiperLayout.setEnabled(false);
            }
        }
    }
}