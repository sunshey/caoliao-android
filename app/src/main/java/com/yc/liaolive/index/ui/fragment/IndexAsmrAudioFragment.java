package com.yc.liaolive.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.music.player.lib.constants.MusicConstants;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.BannerInfo;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentAsmrAudioListBinding;
import com.yc.liaolive.index.adapter.IndexAsmrAudioAdapter;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.ui.activity.VerticalImagePreviewActivity;
import com.yc.liaolive.media.ui.activity.VerticalVideoPlayerAvtivity;
import com.yc.liaolive.model.GridSpaceItemDecorationComent;
import com.yc.liaolive.music.activity.MusicPlayerActivity;
import com.yc.liaolive.ui.contract.IndexVideoListContract;
import com.yc.liaolive.ui.presenter.IndexVideoListPresenter;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2019/3/11
 * ASMR 音频
*/

public class IndexAsmrAudioFragment extends BaseFragment<FragmentAsmrAudioListBinding, IndexVideoListPresenter> implements Observer, IndexVideoListContract.View {

    private static final String TAG = "IndexAsmrAudioFragment";
    private String mUrlHot;
    private int mIndex;
    private int mFileType;
    private String mSource;//0：时间排序 1：浏览排序 2：喜欢排序 3 加密多媒体文件 4 推荐的多媒体文件
    private int mPage;
    private IndexAsmrAudioAdapter mAdapter;
    private boolean isRefresh=true;
    private DataChangeView mEmptyView;

    public static IndexAsmrAudioFragment getInstance(int indexGroup,int index, String url,int fileType,String source) {
        IndexAsmrAudioFragment fragment=new IndexAsmrAudioFragment();
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
            mFileType = arguments.getInt("fileType",0);
            mSource = arguments.getString("source");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
        mPresenter=new IndexVideoListPresenter();
        mPresenter.attachView(this);
        //第一个界面初始就刷新
        if(0 == mIndex){
            if(null!=mEmptyView) mEmptyView.showLoadingView();
            mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_asmr_audio_list;
    }

    @Override
    protected void initViews() {
        //初始化适配器
        IndexGridLayoutManager gridLayoutManager = new IndexGridLayoutManager(getActivity(),3, IndexGridLayoutManager.VERTICAL,false);
        bindingView.recyclerView.addItemDecoration(new GridSpaceItemDecorationComent(Utils.dip2px(6)));
        bindingView.recyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new IndexAsmrAudioAdapter(null,mFileType);
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
        }, bindingView.recyclerView);
        //初始化占位布局
        mEmptyView=new DataChangeView(getActivity());
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

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MobclickAgent.onEvent(AppEngine.getApplication().getApplicationContext(),"asmr_click_music_count");
                if(null!=view.getTag()){
                    PrivateMedia privateMedia = (PrivateMedia) view.getTag();
                    if(Constant.MEDIA_TYPE_ASMR_AUDIO==privateMedia.getFile_type()){
                        List<PrivateMedia> data= mAdapter.getData();
                        int invalidCount=0;
                        //去掉除音频之外的其他不受支持类型，真实播放地址：点击位置-无效个数
                        List<PrivateMedia> mediaList=new ArrayList<>();
                        for (int i = 0; i < data.size(); i++) {
                            PrivateMedia item = data.get(i);
                            if(Constant.INDEX_ITEM_AUDIO.equals(item.getItemCategory())){
                                mediaList.add(item);
                            }else{
                                if(i<=position){
                                    invalidCount++;
                                }
                            }
                        }
                        if(null!=mediaList&&mediaList.size()>0){
                            Intent intent=new Intent(getActivity(), MusicPlayerActivity.class);
                            intent.putExtra(MusicConstants.KEY_MUSIC_LIST, (Serializable) mediaList);
                            intent.putExtra(MusicConstants.KEY_MUSIC_ID, privateMedia.getId());
                            startActivity(intent);
//                            getActivity().overridePendingTransition( R.anim.music_bottom_menu_enter,0);
                            MobclickAgent.onEvent(getContext(), "amsr_music_list_item_click");
                        }
                    }else if(Constant.MEDIA_TYPE_VIDEO==privateMedia.getFile_type()){
                        List<PrivateMedia> mediaList=new ArrayList<>();
                        mediaList.add(privateMedia);
                        VideoDataUtils.getInstance().setVideoData(mediaList,0);
                        VideoDataUtils.getInstance().setIndex(-1);
                        VerticalVideoPlayerAvtivity.start(getActivity(),null,0,0,0,null,0,null);
                    }else if(Constant.MEDIA_TYPE_IMAGE==privateMedia.getFile_type()){
                        List<PrivateMedia> mediaList=new ArrayList<>();
                        mediaList.add(privateMedia);
                        VideoDataUtils.getInstance().setVideoData(mediaList,0);
                        VideoDataUtils.getInstance().setIndex(-1);
                        VerticalImagePreviewActivity.start(getActivity(),null,0,0,0,null,0,null);
                    }
                }
            }
        });

        mAdapter.setOnMultiItemClickListener(new IndexAsmrAudioAdapter.OnMultiItemClickListener() {
            @Override
            public void onBannerClick(BannerInfo bannerInfo) {
                if(null==bannerInfo) return;
                if (!TextUtils.isEmpty(bannerInfo.getJump_url())) {
                    CaoliaoController.start(bannerInfo.getJump_url(),true,null);
                }
            }
        });
        mAdapter.setEmptyView(mEmptyView);
        //刷新
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        //销毁监听
        bindingView.recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {}

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                AutoBannerLayout bannerLayout = (AutoBannerLayout) view.findViewById(R.id.item_banner_view);
                if(null!=bannerLayout){
                    bannerLayout.onReset();
                    mAdapter.reset();
                }
            }
        });
        bindingView.recyclerView.setAdapter(mAdapter);
//        bindingView.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                //滚动过程中触发
//                if(null!=mParentFragment&&recyclerView.getScrollState()==1){
//                    //上滑
//                    if(dy<0){
//                        mParentFragment.showMainTabLayout(true);
//                        //下滑
//                    }else if(dy>0){
//                        mParentFragment.showMainTabLayout(false);
//                    }
//                }
//            }
//        });
    }

    /**
     * 来自主页的刷新事件
     */
    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
        if(null==bindingView )return;
        if(null!=mPresenter&&!mPresenter.isLoading()){
            mPage=1;
            if(null!=mAdapter){
                if(null==mAdapter.getData()||mAdapter.getData().size()<=0){
                    if(null!=mEmptyView) mEmptyView.showLoadingView();
                }else{
                    bindingView.swiperLayout.setRefreshing(true);
                }
            }
            bindingView.recyclerView.scrollToPosition(0);
            mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
        }
    }


    @Override
    protected void onVisible() {
        super.onVisible();
        if(null!=mAdapter) mAdapter.onResume();
        initData();
    }

    private void initData () {
        if(isRefresh && null!= bindingView && null != mPresenter
                && !mPresenter.isLoading()){
            mPage=1;
            if(mAdapter.getData().size()<=0){
                if(null!=mEmptyView) mEmptyView.showLoadingView();
            }
            mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MobclickAgent.onPageStart("main_1v1_list_"+mFileType);
            MobclickAgent.onEvent(getActivity(), "main_1v1_list_"+mFileType);
        } else if (isResumed()) {
            MobclickAgent.onPageEnd("main_1v1_list_"+mFileType);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!=mAdapter) mAdapter.onPause();
        if (getUserVisibleHint()) {
            MobclickAgent.onPageEnd("main_1v1_list_"+mFileType);
        }
    }

    private void refreshData() {
        mPage=1;
        if(null!=mAdapter){
            if(null==mAdapter.getData()||mAdapter.getData().size()<=0){
                if(null!=mEmptyView) mEmptyView.showLoadingView();
            }
        }
        mPresenter.getVideoLists(mUrlHot,mPage,mFileType,mSource);
    }

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
        if(null!=mEmptyView) mEmptyView.onDestroy();
        isRefresh=true; mIndex=0;
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
}