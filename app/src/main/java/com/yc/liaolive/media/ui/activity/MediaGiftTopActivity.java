package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.FansInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityGiftTopBinding;
import com.yc.liaolive.media.adapter.MediaGiftTopAdapter;
import com.yc.liaolive.media.ui.contract.MediaPreviewContract;
import com.yc.liaolive.media.ui.presenter.MediaPreviewPresenter;
import com.yc.liaolive.user.ui.PersonCenterActivity;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import com.yc.liaolive.view.widget.IndexLinLayoutManager;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/20
 * 多媒体文件礼物榜单
 */

public class MediaGiftTopActivity extends BaseActivity<ActivityGiftTopBinding> implements MediaPreviewContract.View {

    private MediaPreviewPresenter mPresenter;
    private String mUserid;
    private long mFileId;
    private int mPage=1;
    private DataChangeView mEmptyView;
    private MediaGiftTopAdapter mAdapter;

    public static void start(android.content.Context context, long file_id, String userid,int mediaType) {
        Intent intent=new Intent(context,MediaGiftTopActivity.class);
        intent.putExtra("file_id",file_id);
        intent.putExtra("userid",userid);
        intent.putExtra("mediaType",mediaType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileId = getIntent().getLongExtra("file_id", 0);
        if(0== mFileId){
            ToastUtils.showCenterToast("文件不存在");
            return;
        }
        mUserid = getIntent().getStringExtra("userid");
        setContentView(R.layout.activity_gift_top);
        int mediaType = getIntent().getIntExtra("mediaType", Constant.MEDIA_TYPE_VIDEO);
        String title="小视频礼物榜";
        if(mediaType==Constant.MEDIA_TYPE_IMAGE){
            title="图片礼物榜";
        }else if(mediaType==Constant.MEDIA_TYPE_ASMR_AUDIO){
            title="ASMR音频礼物榜";
        }else if(mediaType==Constant.MEDIA_TYPE_ASMR_VIDEO){
            title="ASMR视频礼物榜";
        }
        bindingView.titleView.setTitle(title);
        mPresenter = new MediaPreviewPresenter();
        mPresenter.attachView(this);
        mPresenter.getMediaTop(mFileId,mUserid,mPage);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                onBackPressed();
            }
        });
        //初始化适配器
        IndexLinLayoutManager linLayoutManager = new IndexLinLayoutManager(MediaGiftTopActivity.this, IndexGridLayoutManager.VERTICAL,false);
        bindingView.recyclerView.setLayoutManager(linLayoutManager);
        bindingView.recyclerView.setHasFixedSize(true);
        //初始化占位布局
        mEmptyView = new DataChangeView(MediaGiftTopActivity.this);
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mEmptyView.showLoadingView();
                    mPage=1;
                    mPresenter.getMediaTop(mFileId,mUserid,mPage);
                }
            }
        });
        mAdapter = new MediaGiftTopAdapter(null);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    FansInfo fansInfo = (FansInfo) view.getTag();
                    PersonCenterActivity.start(MediaGiftTopActivity.this,fansInfo.getUserid());
                }
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage++;
                    mPresenter.getMediaTop(mFileId,mUserid,mPage);
                }
            }
        },bindingView.recyclerView);
        mEmptyView.showLoadingView();
        mAdapter.setEmptyView(mEmptyView);
        bindingView.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {

    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showMediaTopList(List<FansInfo> data) {
        if(null!=mEmptyView) mEmptyView.showEmptyView();
        if(null!=mAdapter){
            mAdapter.loadMoreComplete();
            if(1==mPage){
                VideoApplication.getInstance().addMediaTops(data);
                mAdapter.setNewData(data);
            }else{
                mAdapter.addData(data);
            }
        }
    }

    @Override
    public void showMediaError(int code, String errorMsg) {
        if(-2==code){
            mAdapter.loadMoreEnd();
            if(null!=mEmptyView) mEmptyView.showEmptyView("送礼物即可上榜",R.drawable.ic_media_tops_empty);
        }else{
            if(null!=mEmptyView) mEmptyView.stopLoading();
            if(null!=mAdapter){
                mAdapter.loadMoreFail();
                List<FansInfo> data = mAdapter.getData();
                if(null==data||data.size()<=0){
                    if(null!=mEmptyView) mEmptyView.showErrorView(errorMsg);
                }
            }
            if(mPage>1) mPage--;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mAdapter) mAdapter.setNewData(null);
        if(null!=mPresenter) mPresenter.detachView();
    }
}
