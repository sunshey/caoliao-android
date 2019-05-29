package com.yc.liaolive.index.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentIndexVideoListBinding;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.media.ui.activity.VerticalVideoPlayerAvtivity;
import com.yc.liaolive.ui.contract.IndexVideoListContract;
import com.yc.liaolive.ui.presenter.IndexVideoListPresenter;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
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
 * 2018/9/25
 * 视频、图片
 */
public class IndexVideoListFragment extends BaseFragment<FragmentIndexVideoListBinding,IndexVideoListPresenter> implements IndexVideoListContract.View, Observer {

    private static final String TAG = "IndexVideoListFragment";
    private String mUrlHot;
    private int mIndex;
    private int mFileType;
    private String mSource;
    private int mIndexGroup;
    private DataChangeView mEmptyView;
    private int mPage;
    private IndexVideoListAdapter mAdapter;
    private boolean isRefresh=true;
    private IndexVideoGroupFragment mParentFragment;

    /**
     * 视频、相册
     * @param indexGroup  父Fragment在Activity中所在的位置
     * @param index  当前Fragment在父Fragment中所在的位置
     * @param url
     * @param fileType 0 image 1 video
     * @param source 0：时间排序 1：浏览排序 2：喜欢排序 3加密多媒体文件 4 推荐的多媒体文件
     * @return
     */
    public static Fragment newInstance(int indexGroup,int index, String url,int fileType,String source) {
        IndexVideoListFragment fragment=new IndexVideoListFragment();
        Bundle bundle=new Bundle();
        bundle.putString("url",url);
        bundle.putInt("index",index);
        bundle.putInt("indexGroup",indexGroup);
        bundle.putInt("fileType",fileType);
        bundle.putString("source",source);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mUrlHot = arguments.getString("url");
            mIndex = arguments.getInt("index");
            mIndexGroup = arguments.getInt("indexGroup");
            mFileType = arguments.getInt("fileType",0);
            mSource = arguments.getString("source");
        }
    }

    @Override
    protected void initViews() {
        //初始化适配器
        IndexGridLayoutManager gridLayoutManager = new IndexGridLayoutManager(getActivity(),2, IndexGridLayoutManager.VERTICAL,false);
//        bindingView.recylerView.addItemDecoration(new ItemMiddleSpaceDecoration(Utils.dip2px(12)));
        bindingView.recylerView.setLayoutManager(gridLayoutManager);
        bindingView.recylerView.setHasFixedSize(true);
        mAdapter = new IndexVideoListAdapter(null,mIndex,mFileType);
        mAdapter.showEmptyView(true);
        //加载更多监听
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
                }
            }
        }, bindingView.recylerView);
        //条目监听
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //给定回显数据
                VideoDataUtils.getInstance().setVideoData(mAdapter.getData(),position);
                VideoDataUtils.getInstance().setIndex(mIndex);
                //给定回显参数,主页区分小视频、照片类型
                VideoDataUtils.getInstance().setFileType(mFileType);
                //预览照片
                if(Constant.MEDIA_TYPE_IMAGE==mFileType){
                    VerticalImagePreviewActivity.start(getActivity(),mUrlHot,mIndex,position,mFileType,mSource,mPage,null);
                //预览ASMR视频
                }else if(Constant.MEDIA_TYPE_ASMR_VIDEO==mFileType){
                    VerticalVideoPlayerAvtivity.start(getActivity(),mUrlHot,mIndex,position,mFileType,mSource,mPage,null);
                //预览视频
                }else{
                    VerticalVideoPlayerAvtivity.start(getActivity(),mUrlHot,mIndex,position,mFileType,mSource,mPage,null);
                }
            }
        });
        //多类型条目点击事件
        mAdapter.setOnMultiItemClickListener(new IndexVideoListAdapter.OnMultiItemClickListener() {
            @Override
            public void onBannerClick(BannerInfo bannerInfo) {
                if(null==bannerInfo) return;
                if (!TextUtils.isEmpty(bannerInfo.getJump_url())) {
                    CaoliaoController.start(bannerInfo.getJump_url(),true,null);
                }
            }
        });
        //初始化占位布局
        mEmptyView = new DataChangeView(getActivity());
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mEmptyView.showLoadingView();
                    mPage=1;
                    mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
                }
            }
        });

        mAdapter.setEmptyView(mEmptyView);
        bindingView.recylerView.setAdapter(mAdapter);
        //刷新监听
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(false);
            }
        });
        //销毁监听
        bindingView.recylerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                AutoBannerLayout bannerLayout = (AutoBannerLayout) view.findViewById(R.id.item_banner);
                if(null!=bannerLayout){
                    bannerLayout.onReset();
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_video_list;
    }

    public void showVisible () {
        if (getUserVisibleHint()) {
            checkIsInitView ();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            checkIsInitView ();
        }
    }

    private void checkIsInitView () {
        rx.Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (getView() == null || bindingView == null || isResumed()) {
                            checkIsInitView();
                        } else {
                            initData();
                        }
                    }
                });
    }

    private void initData () {
        if(isRefresh&&null!= bindingView&&null!=mAdapter&&null!=mPresenter&&!mPresenter.isLoading()){
            mPage=1;
            if(mAdapter.getData().size()<=0){
                if(null!=mEmptyView) mEmptyView.showLoadingView();
            }
            mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
        }
        if(null!=mAdapter) mAdapter.onResume();
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        if(null!=mAdapter) mAdapter.onPause();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
        mPresenter=new IndexVideoListPresenter();
        mPresenter.attachView(this);
        mParentFragment = (IndexVideoGroupFragment) getParentFragment();
        if(0==mIndex){
            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mAdapter) mAdapter.onResume();
        if(mIndex==VideoDataUtils.getInstance().getIndex()&&mFileType==VideoDataUtils.getInstance().getFileType()
                && mSource != null && mSource.equals(VideoDataUtils.getInstance().getSource())){
            if(null!=VideoDataUtils.getInstance().getVideoData()&&VideoDataUtils.getInstance().getPosition()>0){
                mPage=VideoDataUtils.getInstance().getPage();//还原当前加载到第几页了
                mAdapter.setNewData(VideoDataUtils.getInstance().getVideoData());
                if(null!=mAdapter&&null!=mAdapter.getData()&&mAdapter.getData().size()>VideoDataUtils.getInstance().getPosition()){
                    RecyclerView.LayoutManager layoutManager = bindingView.recylerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager linearManager = (GridLayoutManager) layoutManager;
                        //获取最后一个可见item的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见item的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        boolean refresh=Utils.isRefresh(VideoDataUtils.getInstance().getPosition(),lastItemPosition,firstItemPosition);
                        if(refresh) linearManager.scrollToPositionWithOffset(VideoDataUtils.getInstance().getPosition(),0);
                    }
                }
            }
            VideoDataUtils.getInstance().setPosition(0);
            VideoDataUtils.getInstance().setIndex(-1);
            VideoDataUtils.getInstance().setFileType(0);
            VideoDataUtils.getInstance().setHostUrl(null);
            //再次检查是否需要强制更新
            if(VideoApplication.getInstance().isIndexMediaRefresh()){
                VideoApplication.getInstance().setIndexMediaRefresh(false);
                mPage=1;
                mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mAdapter) mAdapter.onPause();
    }

    private void refreshData(boolean flag) {
        if(null!=mAdapter){
            if(null==mAdapter.getData()||mAdapter.getData().size()<=0){
                if(null!=mEmptyView) mEmptyView.showLoadingView();
            }else{
                if(flag&&null!=bindingView) bindingView.swiperLayout.setRefreshing(true);
            }
        }
        mPage=1;
        mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
    }

    @Override
    protected void fromMainUpdata() {
        super.fromMainUpdata();
        if(null!=mPresenter&&!mPresenter.isLoading()){
            refreshData(true);
        }
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showLiveVideos(List<PrivateMedia> data) {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) {
            mEmptyView.showEmptyView();
        }
        if(null!=mAdapter){
            mAdapter.loadMoreComplete();
            if(1==mPage){
                mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void showVideoEmpty() {
        isRefresh=false;
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.showEmptyView();
        if(null!=mAdapter){
            mAdapter.loadMoreEnd();
            if(1==mPage){
                mAdapter.setNewData(null);//如果是第一页的话，直接抹空数据
            }
        }
    }

    @Override
    public void showVideoError(int code, String errorMsg) {
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=mAdapter){
            mAdapter.loadMoreFail();
            List<PrivateMedia> data = mAdapter.getData();
            if(null==data||data.size()<=0){
                if(null!=mEmptyView) mEmptyView.showErrorView(errorMsg);
            } else {
                ToastUtils.showToast(errorMsg);
            }
        }
        if(mPage>0) mPage--;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mParentFragment=null;
    }

    @Override
    public void onDestroy() {
        ApplicationManager.getInstance().removeObserver(this);
        super.onDestroy();
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
