package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityPrivateMediaVideoBinding;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.UploadFileToOSSManager;
import com.yc.liaolive.media.adapter.PrivateMediaVideoAdapter;
import com.yc.liaolive.model.GridSpacesItemDecoration;
import com.yc.liaolive.ui.contract.PrivateMediaContract;
import com.yc.liaolive.ui.presenter.PrivateMediaPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.util.VideoSelectedUtil;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/13
 * 第一、第二 人称 小视频
 */

public class PrivateMediaVideoActivity extends BaseActivity<ActivityPrivateMediaVideoBinding> implements PrivateMediaContract.View {

    private static final String TAG = "PrivateMediaActivity";
    private PrivateMediaVideoAdapter mAdapter;
    private PrivateMediaPresenter mPresenter;
    private DataChangeView mEmptyView;
    private String mHomeUserID;
    private int mPage;
    private String mCompressOutPath;

    /**
     * 入口 默认相册
     * @param context
     * @param homeUserID 主播ID
     */
    public static void start(android.content.Context context,String homeUserID) {
        Intent intent=new Intent(context,PrivateMediaVideoActivity.class);
        intent.putExtra("homeUserID",homeUserID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mHomeUserID = intent.getStringExtra("homeUserID");
        if(TextUtils.isEmpty(mHomeUserID)){
            ToastUtils.showCenterToast("参数不合法！");
            finish();
            return;
        }
        setContentView(R.layout.activity_private_media_video);
        mPresenter = new PrivateMediaPresenter();
        mPresenter.attachView(this);
        mPage=0;
        loadData();
        //临时存放压缩文件的路径
        mCompressOutPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "CaoLiaoTemp" + File.separator;
    }

    @Override
    public void initViews() {
        bindingView.toolBar.setTitle(getResources().getString(R.string.media_video));
        //作者自己
        if(mHomeUserID.equals(UserManager.getInstance().getUserId())){
            bindingView.toolBar.setMenu1Res(R.drawable.btn_nav_close_add);
            bindingView.toolBar.setMenu2Res(R.drawable.ic_private_media_edit);
        }
        bindingView.toolBar.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            //返回
            @Override
            public void onBack(View v) {
                finish();
            }
            //选择图片并上传
            @Override
            public void onMenuClick1(View v) {
                if(null==mAdapter) return;
                //正在编辑状态，拦截上传按钮操作
                if(mAdapter.isEdit()){
                    ToastUtils.showCenterToast("编辑状态下无法上传");
                    return;
                }
                if(mHomeUserID.equals(UserManager.getInstance().getUserId())) selectedMediaVideo();
            }
            //编辑模式
            @Override
            public void onMenuClick2(View v) {
                //只有自己访问自己的相册允许编辑
                if(null!=mAdapter&&mHomeUserID.equals(UserManager.getInstance().getUserId())) {
                    List<PrivateMedia> data = mAdapter.getData();
                    //只有作者作品大于0才允许编辑
                    if(null!=data&&data.size()>0){
                        mAdapter.changedEditMode();
                        bindingView.toolBar.setMenu2Res(mAdapter.isEdit()?R.drawable.ic_private_media_finlish:R.drawable.ic_private_media_edit);
                    }else{
                        ToastUtils.showCenterToast("暂无"+getResources().getString(R.string.media_video)+"可编辑");
                    }
                }
            }
        });

        //适配器初始化
        IndexGridLayoutManager gridLayoutManager = new IndexGridLayoutManager(PrivateMediaVideoActivity.this,2, GridLayoutManager.VERTICAL,false);
        bindingView.recylerView.setLayoutManager(gridLayoutManager);
        bindingView.recylerView.addItemDecoration(new GridSpacesItemDecoration(ScreenUtils.dpToPxInt(1f)));

        mAdapter = new PrivateMediaVideoAdapter(null,mHomeUserID);
        //设定加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(null!= mAdapter){
                    List<PrivateMedia> data = mAdapter.getData();
                    if(null!=data&&data.size()>=10&&null!= mPresenter &&!mPresenter.isLoading()){
                        bindingView.swiperLayout.setRefreshing(false);
                        loadData();
                    }else{
                        bindingView.recylerView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(!Utils.isCheckNetwork()){
                                    mAdapter.loadMoreFail();//加载失败
                                }else{
                                    mAdapter.loadMoreEnd();//加载为空
                                }
                            }
                        });
                    }
                }
            }
        },bindingView.recylerView);
        //初始化占位布局
        mEmptyView = new DataChangeView(PrivateMediaVideoActivity.this);
        //刷新监听
        mEmptyView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bindingView.swiperLayout.setRefreshing(false);
                mEmptyView.showLoadingView();
                mPage=0;
                loadData();
            }
        });
        mEmptyView.showLoadingView();
        mAdapter.setEmptyView(mEmptyView);
        //点击事件
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    PrivateMedia privateMedia = (PrivateMedia) view.getTag();
                    //编辑模式下拦截点击事件
                    if(mAdapter.isEdit()){
                        return;
                    }
                    //拦截点击头部事件
                    if(privateMedia.getItemType()== PrivateMediaVideoAdapter.ITEM_TYPE_ADD){
                        selectedMediaVideo();
                        return;
                    }
                    startPreviewMedia(privateMedia,view,position);
                }
            }
        });
        //状态监听
        mAdapter.setOnMediaStateListener(new PrivateMediaVideoAdapter.OnMediaStateListener() {
            //删除多媒体文件
            @Override
            public void onDeleteMedia(PrivateMedia mediaInfo, int position) {
                if(null!=mHomeUserID&&mHomeUserID.equals(UserManager.getInstance().getUserId())){
                    if(null!=mPresenter){
                        showProgressDialog("删除中，请稍后...",false);
                        mPresenter.deleteMediaFile(mediaInfo,position);
                    }
                }
            }

            //改变多媒体文件访问权限
            @Override
            public void onChangedPrivateState(PrivateMedia mediaInfo, int position) {
                if(null!=mHomeUserID&&mHomeUserID.equals(UserManager.getInstance().getUserId())){
                    if(null!=mPresenter){
                        showProgressDialog("操作中，请稍后...",false);
                        mPresenter.modifyMediaFilePrivatePermission(mediaInfo,position);
                    }
                }
            }
        });

        bindingView.recylerView.setAdapter(mAdapter);
        //刷新
        bindingView.swiperLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(null!=mPresenter&&!mPresenter.isLoading()){
                    mPage=0;
                    loadData();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //预览视频回显
        if(-1==VideoDataUtils.getInstance().getIndex()){
            //只回显视频
            if(Constant.MEDIA_TYPE_VIDEO==VideoDataUtils.getInstance().getFileType()&&TextUtils.equals(NetContants.getInstance().URL_FILE_LIST(),VideoDataUtils.getInstance().getHostUrl())&&null!=VideoDataUtils.getInstance().getVideoData()&&VideoDataUtils.getInstance().getPosition()>0){
                mPage=VideoDataUtils.getInstance().getPage();
                mAdapter.setNewData(VideoDataUtils.getInstance().getVideoData());
                if(null!=mAdapter&&null!=mAdapter.getData()&&mAdapter.getData().size()>VideoDataUtils.getInstance().getPosition()){
                    RecyclerView.LayoutManager layoutManager = bindingView.recylerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager linearManager = (GridLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见view的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        boolean refresh = Utils.isRefresh(VideoDataUtils.getInstance().getPosition(), lastItemPosition, firstItemPosition);
                        if (refresh) linearManager.scrollToPositionWithOffset(VideoDataUtils.getInstance().getPosition(), 0);
                    }
                }
            }
            VideoDataUtils.getInstance().setPosition(0);
            VideoDataUtils.getInstance().setIndex(0);
            VideoDataUtils.getInstance().setFileType(0);
            VideoDataUtils.getInstance().setHostUrl(null);
        }
        //视频编辑完成
        if(null!=VideoApplication.getInstance().getUploadObjectInfo()){
            UploadFileToOSSManager.get(PrivateMediaVideoActivity.this).addUploadListener(new OnUploadObjectListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(long progress) {
                }

                @Override
                public void onSuccess(UploadObjectInfo data, String msg) {
                    ToastUtils.showCenterToast(msg);
                    VideoApplication.getInstance().setMineRefresh(true);
                    mPage=1;
                    if(null!=mPresenter) mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_VIDEO,mPage);
                }

                @Override
                public void onFail(int code, String errorMsg) {
                    ToastUtils.showCenterToast(errorMsg);
                }
            }).createAsyncUploadTask(VideoApplication.getInstance().getUploadObjectInfo());
            VideoApplication.getInstance().setUploadObjectInfo(null);
        }
    }

    /**
     * 预览多媒体文件
     */
    private void startPreviewMedia(PrivateMedia privateMedia, final View view, final int position) {
        try {
            if(mAdapter.getData().size()>position){
                //先关闭可能打开的照片预览界面
                ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_FINLISH_MEDIA_PLAYER);
                VideoDataUtils.getInstance().setHostUrl(NetContants.getInstance().URL_FILE_LIST());
                VideoDataUtils.getInstance().setFileType(Constant.MEDIA_TYPE_VIDEO);
                VideoDataUtils.getInstance().setIndex(-1);
                if(Constant.MEDIA_TYPE_IMAGE==privateMedia.getFile_type()){
                    List<PrivateMedia> imageData=new ArrayList<>();
                    imageData.add(mAdapter.getData().get(position));
                    VideoDataUtils.getInstance().setVideoData(imageData,position);
                    new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            VerticalImagePreviewActivity.start(PrivateMediaVideoActivity.this,mHomeUserID,null);
                        }
                    }, SystemClock.uptimeMillis()+100);
                    return;
                }else if(Constant.MEDIA_TYPE_VIDEO==privateMedia.getFile_type()){
                    VideoDataUtils.getInstance().setVideoData(mAdapter.getData(),position);
                    //先关闭可能打开的视频预览界面
                    new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            VerticalVideoPlayerAvtivity.start(PrivateMediaVideoActivity.this,mHomeUserID, NetContants.getInstance().URL_FILE_LIST(),-1,position,mPage,0,null);
                        }
                    }, SystemClock.uptimeMillis()+100);
                    return;
                }
            }
        }catch (RuntimeException e){

        }catch (Exception e){

        }
    }

    @Override
    public void initData() {

    }

    /**
     * 选择视频
     */
    private void selectedMediaVideo() {
        Intent intent=new Intent(PrivateMediaVideoActivity.this,MediaLocationVideoListActivity.class);
        startActivityForResult(intent,Constant.SELECT_VIDEO_REQUST);
    }

    /**
     * 加载私密相册、视频
     */
    private void loadData() {
        if(null!=mPresenter&&!mPresenter.isLoading()) {
            mPage++;
            mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_VIDEO,mPage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //视频文件
        if(requestCode==Constant.SELECT_VIDEO_REQUST&&resultCode==Constant.SELECT_VIDEO_RESULT){
            if(null!=data.getStringExtra("selected_video")){
                UploadFileToOSSManager.get(PrivateMediaVideoActivity.this).addUploadListener(new OnUploadObjectListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onProgress(long progress) {

                    }

                    @Override
                    public void onSuccess(UploadObjectInfo data, String msg) {
                        ToastUtils.showCenterToast(msg);
                        VideoApplication.getInstance().setMineRefresh(true);
                        mPage=1;
                        if(null!=mPresenter) mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_VIDEO,mPage);
                    }

                    @Override
                    public void onFail(int code, String errorMsg) {
                        ToastUtils.showCenterToast(errorMsg);
                    }
                }).createAsyncUploadTask(data.getStringExtra("selected_video"));
            }
        }
        VideoSelectedUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        VideoSelectedUtil.getInstance().onDestroy();
        VideoApplication.getInstance().setImages(null);
        if(null!=mPresenter) mPresenter.detachView();
        if(null!=mEmptyView) mEmptyView.stopLoading();
        mAdapter=null;
        if(null!=mCompressOutPath) FileUtils.deleteFile(mCompressOutPath);
        super.onDestroy();
    }

    //============================================数据交互===========================================

    @Override
    public void showErrorView() {}

    @Override
    public void complete() {}

    /**
     * 用名下上传的多媒体文件
     * @param data
     */
    @Override
    public void showPrivateMedias(List<PrivateMedia> data) {
        if(null!=mEmptyView){
            mEmptyView.showEmptyView(false);
            mEmptyView.removeMediaEmpty();
        }
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
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
    public void showPrivateMediaEmpty() {
        if(null!=mEmptyView) mEmptyView.stopLoading();
        if(null!=bindingView) bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter){
            mAdapter.loadMoreEnd();
            if(1==mPage) mAdapter.setOnLoadMoreListener(null,bindingView.recylerView);//禁止加载更多
        }
        //第一人称关系(作者自己)
        if(null!=mHomeUserID&&mHomeUserID.equals(UserManager.getInstance().getUserId())){
            mAdapter.setEdit(false);
            if(null!=bindingView) bindingView.toolBar.setMenu2Res(R.drawable.ic_private_media_edit);//还原编辑状态
            mEmptyView.showMediaEmpty(Constant.MEDIA_TYPE_VIDEO, new DataChangeView.OnFuctionListener() {
                @Override
                public void onSubmit() {
                    selectedMediaVideo();
                }
            });
        //第二人称关系
        }else{
            if(null!=mEmptyView) mEmptyView.showEmptyView(getResources().getString(R.string.media_video_empty2),R.drawable.ic_media_empty_video);
        }
    }

    @Override
    public void showPrivateMediaError(int code, String errorMsg) {
        bindingView.swiperLayout.setRefreshing(false);
        if(null!=mAdapter) mAdapter.loadMoreFail();
        if(null!=mAdapter){
            List<PrivateMedia> data = mAdapter.getData();
            if(null==data||data.size()<=0){
                if(null!=mEmptyView) mEmptyView.showErrorView(errorMsg);
            }
        }
    }

    /**
     * 修改访问权限回执
     * @param media
     * @param position
     * @param code
     * @param msg
     */
    @Override
    public void showModifyMediaFilePermissionResult(PrivateMedia media, int position, int code, String
        msg) {
        closeProgressDialog();
        ToastUtils.showCenterToast(msg);
        if(1==code){
            try {
                mAdapter.notifyItemChanged(position,"item_upload");
            }catch (RuntimeException e){

            }
        }
    }

    /**
     * 删除文件回执
     * @param media
     * @param position
     * @param code
     * @param msg
     */
    @Override
    public void showDeleteMediaFileResult(PrivateMedia media, int position, int code, String msg) {
        closeProgressDialog();
        ToastUtils.showCenterToast(msg);
        if(1==code){
            try {
                if(null!=mAdapter) mAdapter.remove(position);
                VideoApplication.getInstance().setMineRefresh(true);
                List<PrivateMedia> data = mAdapter.getData();
                if(null==data||data.size()<=0){
                    if(null!=bindingView) bindingView.toolBar.setMenu2Res(R.drawable.ic_private_media_edit);//还原编辑状态
                    if(null!=mPresenter&&!mPresenter.isLoading()){
                        mPage=0;
                        loadData();
                    }
                }
            }catch (RuntimeException e){

            }
        }
    }

    @Override
    public void showSetImageFrontResult(int code, String msg) {

    }
}