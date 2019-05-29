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
import android.text.TextUtils;
import android.view.View;
import com.google.gson.Gson;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.bean.ImageParams;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.bean.VideoDetailsMenu;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityPrivateMediaPhotoBinding;
import com.yc.liaolive.index.adapter.IndexVideoListAdapter;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.UploadFileToOSSManager;
import com.yc.liaolive.media.adapter.PrivateMediaPhotoAdapter;
import com.yc.liaolive.media.adapter.PrivateMediaVideoAdapter;
import com.yc.liaolive.model.GridSpacesItemDecoration;
import com.yc.liaolive.ui.contract.PrivateMediaContract;
import com.yc.liaolive.ui.dialog.CommonMenuDialog;
import com.yc.liaolive.ui.presenter.PrivateMediaPresenter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.SystemUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoDataUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/13
 * 第一、第二 人称 相册
 */

public class PrivateMediaPhotoActivity extends BaseActivity<ActivityPrivateMediaPhotoBinding> implements PrivateMediaContract.View {

    private static final String TAG = "PrivateMediaActivity";
    private PrivateMediaPhotoAdapter mAdapter;
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
        Intent intent=new Intent(context,PrivateMediaPhotoActivity.class);
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
        setContentView(R.layout.activity_private_media_photo);
        mPresenter = new PrivateMediaPresenter();
        mPresenter.attachView(this);
        mPage=0;
        loadData();
        //临时存放压缩文件的路径
        mCompressOutPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "CaoLiaoTemp" + File.separator;
    }

    @Override
    public void initViews() {
        bindingView.toolBar.setTitle(getResources().getString(R.string.media_image));
        //作者自己
        if(mHomeUserID.equals(UserManager.getInstance().getUserId())){
            bindingView.toolBar.setMenu1Res(R.drawable.btn_nav_close_add);
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
                if(mHomeUserID.equals(UserManager.getInstance().getUserId())) selectedMediaImage();
            }
        });
        //适配器初始化
        IndexGridLayoutManager gridLayoutManager = new IndexGridLayoutManager(PrivateMediaPhotoActivity.this,3, GridLayoutManager.VERTICAL,false);
        bindingView.recylerView.setLayoutManager(gridLayoutManager);
        bindingView.recylerView.addItemDecoration(new GridSpacesItemDecoration(ScreenUtils.dpToPxInt(2f)));

        mAdapter = new PrivateMediaPhotoAdapter(null,mHomeUserID);
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
        mEmptyView = new DataChangeView(PrivateMediaPhotoActivity.this);
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
                    //拦截点击头部事件
                    if(privateMedia.getItemType()== PrivateMediaVideoAdapter.ITEM_TYPE_ADD){
                        selectedMediaImage();
                        return;
                    }
                    startPreviewMedia(privateMedia,view,position);
                }
            }
        });
        //预览自己的视频 ，长按事件监听
        if(null!=mHomeUserID&&mHomeUserID.equals(UserManager.getInstance().getUserId())){
            mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    if(null!=view){
                        PrivateMedia privateMedia = (PrivateMedia) view.getTag();
                        showEditMenu(privateMedia,position);
                    }
                    return false;
                }
            });
        }
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

    /**
     * 长按触发了编辑
     * @param privateMedia
     * @param position
     */
    private void showEditMenu(final PrivateMedia privateMedia, final int position) {
        if(IndexVideoListAdapter.ITEM_TYPE_ADD==privateMedia.getItemType()) return;//拦截上传照片的长按事件
        List<VideoDetailsMenu> list=new ArrayList<>();
        if(0==position){
            //如果第一个条目是审核未通过状态则允许删除,其他状态不允许删除
            if(0==privateMedia.getState()){
                VideoDetailsMenu videoDetailsMenu=new VideoDetailsMenu();
                videoDetailsMenu.setItemID(2);
                videoDetailsMenu.setTextColor("#FFFF7575");
                videoDetailsMenu.setItemName("删除");
                list.add(videoDetailsMenu);
            }
        }else{
            //已审核通过
            if(privateMedia.getState()>0){
                VideoDetailsMenu videoDetailsMenu=new VideoDetailsMenu();
                videoDetailsMenu.setItemID(0);
                videoDetailsMenu.setTextColor("#FF333333");
                videoDetailsMenu.setItemName("设为封面");
                list.add(videoDetailsMenu);
            }
            VideoDetailsMenu videoDetailsMenu=new VideoDetailsMenu();
            videoDetailsMenu.setItemID(2);
            videoDetailsMenu.setTextColor("#FFFF7575");
            videoDetailsMenu.setItemName("删除");
            list.add(videoDetailsMenu);
        }
        if(list.size()>0){
            SystemUtils.startVibrator(100);
            CommonMenuDialog commonMenuDialog =new CommonMenuDialog(PrivateMediaPhotoActivity.this);
            commonMenuDialog.setData(list);
            commonMenuDialog.setOnItemClickListener(new CommonMenuDialog.OnItemClickListener() {
                @Override
                public void onItemClick(int itemID, VideoDetailsMenu videoDetailsMenu) {
                    switch (itemID) {
                        //设为封面
                        case 0:
                            if(null!=mPresenter){
                                showProgressDialog("设置中，请稍后...",false);
                                mPresenter.setImageFront(privateMedia,position);
                            }
                            break;
                        //删除
                        case 2:
                            if(null!=mPresenter){
                                showProgressDialog("删除中，请稍后...",false);
                                mPresenter.deleteMediaFile(privateMedia,position);
                            }
                            break;
                    }
                }
            });
            commonMenuDialog.show();
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
                VideoDataUtils.getInstance().setFileType(Constant.MEDIA_TYPE_IMAGE);
                VideoDataUtils.getInstance().setIndex(-1);
                if(Constant.MEDIA_TYPE_IMAGE==privateMedia.getFile_type()){
                    List<PrivateMedia> imageData=new ArrayList<>();
                    imageData.add(mAdapter.getData().get(position));
                    VideoDataUtils.getInstance().setVideoData(imageData,position);
                    new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            VerticalImagePreviewActivity.start(PrivateMediaPhotoActivity.this,mHomeUserID,null);
                        }
                    }, SystemClock.uptimeMillis()+100);
                    return;
                }else if(Constant.MEDIA_TYPE_VIDEO==privateMedia.getFile_type()){
                    VideoDataUtils.getInstance().setVideoData(mAdapter.getData(),position);
                    //先关闭可能打开的视频预览界面
                    new android.os.Handler(Looper.getMainLooper()).postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            VerticalVideoPlayerAvtivity.start(PrivateMediaPhotoActivity.this,mHomeUserID, NetContants.getInstance().URL_FILE_LIST(),-1,position,mPage,0,null);
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
     * 选择照片
     */
    private void selectedMediaImage() {
//        Intent intent=new Intent(PrivateMediaPhotoActivity.this,MediaLocationImageListActivity.class);
//        intent.putExtra("max_count",30);//限制单次最大上传数量
//        startActivityForResult(intent,Constant.SELECT_IMAGE_REQUST);
        PhotoSelectedUtil.getInstance()
                .attachActivity(PrivateMediaPhotoActivity.this)
                .setCatScaleWidth(1)
                .setCatScaleHeight(1)
                .setCropMode(0)
                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                    @Override
                    public void onOutFile(File file) {
                        UploadFileToOSSManager.get(PrivateMediaPhotoActivity.this).addUploadListener(new OnUploadObjectListener() {
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
                                VideoApplication.getInstance().setMineRefresh(true);
                                mPage=1;
                                if(null!=mPresenter) mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_IMAGE,mPage);
                            }

                            @Override
                            public void onFail(int code, String errorMsg) {
                                ToastUtils.showCenterToast(errorMsg);
                            }
                        }).createAsyncUploadTask(file);
                    }

                    @Override
                    public void onError(int code, String errorMsg) {

                    }
                }).start();
    }

    /**
     * 加载私密相册、视频
     */
    private void loadData() {
        if(null!=mPresenter&&!mPresenter.isLoading()) {
            mPage++;
            mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_IMAGE,mPage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //照片
        if(requestCode==Constant.SELECT_IMAGE_REQUST&&resultCode==Constant.SELECT_IMAGE_RESULT){
            if(null!=data.getStringExtra("selected_images")){
                String images=data.getStringExtra("selected_images");
                ImageParams imageParams = new Gson().fromJson(images, ImageParams.class);
                List<ImageInfo> imags = imageParams.getImags();
                if(null!=imags&&imags.size()>0){
                    UploadFileToOSSManager.get(PrivateMediaPhotoActivity.this).showDetails(true).setContinuation(true).addUploadListener(new OnUploadObjectListener() {
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
                            if(null!=mPresenter) mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_IMAGE,mPage);
                        }

                        @Override
                        public void onFail(int code, String errorMsg) {
                            ToastUtils.showCenterToast(errorMsg);
                        }
                    }).createAsyncUploadTask(imags);
                }
            }else if(null!=data.getStringExtra("selected_image")){
                UploadFileToOSSManager.get(PrivateMediaPhotoActivity.this).addUploadListener(new OnUploadObjectListener() {
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
                        if(null!=mPresenter) mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_IMAGE,mPage);
                    }

                    @Override
                    public void onFail(int code, String errorMsg) {
                        ToastUtils.showCenterToast(errorMsg);
                    }
                }).createAsyncUploadTask(data.getStringExtra("selected_image"));
            }else if(null!=data)
            return;
        }
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        PhotoSelectedUtil.getInstance().onDestroy();
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
            mEmptyView.showMediaEmpty(Constant.MEDIA_TYPE_IMAGE, new DataChangeView.OnFuctionListener() {
                @Override
                public void onSubmit() {
                    selectedMediaImage();
                }
            });
        //第二人称关系
        }else{
            if(null!=mEmptyView) mEmptyView.showEmptyView(getResources().getString(R.string.media_image_empty2),R.drawable.ic_media_empty_photo);
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
    public void showModifyMediaFilePermissionResult(PrivateMedia media, int position, int code, String msg) {
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
                    if(null!=mPresenter&&!mPresenter.isLoading()){
                        mPage=0;
                        loadData();
                    }
                }
            }catch (RuntimeException e){

            }
        }
    }

    /**
     * 设置封面回执
     * @param code
     * @param msg
     */
    @Override
    public void showSetImageFrontResult(int code, String msg) {
        closeProgressDialog();
        ToastUtils.showCenterToast(msg);
        if(NetContants.API_RESULT_CODE==code){
            mPage=1;
            if(null!=mPresenter) mPresenter.getPrivateMedia(mHomeUserID,Constant.MEDIA_TYPE_IMAGE,mPage);
        }
    }
}