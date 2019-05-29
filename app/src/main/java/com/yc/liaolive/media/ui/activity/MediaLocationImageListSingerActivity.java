package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.ImageDirInfo;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityMediaImageListSingerBinding;
import com.yc.liaolive.media.adapter.ImageDirListAdapter;
import com.yc.liaolive.media.adapter.ImagesListAdapter;
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.msg.view.ListEmptyFooterView;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.PhotoScanUtils;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.StatusUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import java.io.File;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/19
 * 本地图片选择，单选操作，选择完成走裁剪
 */

public class MediaLocationImageListSingerActivity extends BaseActivity<ActivityMediaImageListSingerBinding>  {

    private static final String TAG = "MediaLocationImageListSingerActivity";
    private ImagesListAdapter mAdapter;
    private DataChangeView mDataChangeView;
    private ListPopupWindow mListPopupWindow;
    private ImageDirListAdapter mDirListAdapter;
    private int mFloderIndex =0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_image_list_singer);
        StatusUtils.setStatusTextColor1(true,MediaLocationImageListSingerActivity.this);//白色背景，黑色字体
        getLocationImages(null,true);
    }

    @Override
    public void initViews() {

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                    case R.id.btn_close:
                        onBackPressed();
                        break;
                    //顶部标题
                    case R.id.btn_title:
                        showDirMenus();
                        break;
                }
            }
        };

        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnTitle.setOnClickListener(onClickListener);

        bindingView.recylerView.setLayoutManager(new GridLayoutManager(MediaLocationImageListSingerActivity.this, 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recylerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1.5f)));
        mAdapter = new ImagesListAdapter(null,1);//不允许多选操作
        //设置空视图
        mDataChangeView = new DataChangeView(MediaLocationImageListSingerActivity.this);
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);
        mAdapter.showEmptyView(true);
        mAdapter.addFooterView(new ListEmptyFooterView(MediaLocationImageListSingerActivity.this));
        //条目点击事件
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    ImageInfo imageInfo = (ImageInfo) view.getTag();
                    if(1==imageInfo.getItemType()){
                        PhotoSelectedUtil.getInstance()
                                .attachActivity(MediaLocationImageListSingerActivity.this)
                                .setClipCircle(false)
                                .setCatScaleHeight(1)
                                .setCatScaleWidth(1)
                                .setSkipClip(false)//跳过裁剪
                                .setCropMode(1)//配置裁剪模式，1为新模式
                                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                                    @Override
                                    public void onOutFile(File file) {
                                        //结束选择,回到上层界面开始上传
                                        Intent intent=new Intent();
                                        intent.putExtra("selected_image",file.getAbsolutePath());
                                        setResult(Constant.SELECT_SINGER_IMAGE_RESULT,intent);
                                        finish();
                                    }

                                    @Override
                                    public void onError(int code, String errorMsg) {
                                    }
                                }).startTakePictrue();
                        return;
                    }
                    //选中具体图片
                    if(!TextUtils.isEmpty(imageInfo.getFilePath())){
                        //去裁剪
                        PhotoSelectedUtil.getInstance()
                                .attachActivity(MediaLocationImageListSingerActivity.this)
                                .setSkipClip(false)//跳过裁剪
                                .setCropMode(1)
                                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                                    @Override
                                    public void onOutFile(File file) {
                                        //结束选择,回到上层界面开始上传
                                        Intent intent=new Intent();
                                        intent.putExtra("selected_image",file.getAbsolutePath());
                                        setResult(Constant.SELECT_SINGER_IMAGE_RESULT,intent);
                                        finish();
                                    }

                                    @Override
                                    public void onError(int code, String errorMsg) {
                                    }
                                }).startCutPreview(imageInfo.getFilePath());
                    }
                }
            }
        });
        bindingView.recylerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {

    }

    /**
     * 获取本机所有照片
     * @param dirPath 目录，为null或者""则不进行分组封装
     * @param isGroup 是否分组
     * 注意子线程抛出
     */
    private void getLocationImages(String dirPath,boolean isGroup) {
        //扫描本地图片文件
        PhotoScanUtils.getInstance().setOnScanListener(new PhotoScanUtils.OnScanListener() {
            @Override
            public void onFail(int code, String errorMsg) {
            }

            /**
             * 分类文件夹处理完成
             * @param tabs
             */
            @Override
            public void onTabs(final List<ImageDirInfo> tabs) {
                if(!MediaLocationImageListSingerActivity.this.isFinishing()) runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=bindingView&&null!=tabs&&tabs.size()>0){
                            bindingView.tvTitle.setText(tabs.get(0).getDirName());
                            bindingView.icDirection.setImageResource(R.drawable.ic_image_pull);
                        }
                        mFloderIndex =0;
                        //文件目录
                        if(null==mDirListAdapter) mDirListAdapter = new ImageDirListAdapter(MediaLocationImageListSingerActivity.this,null);
                        mDirListAdapter.setNewData(tabs);
                    }
                });
            }

            /**
             * 相册加载完成
             * @param imageInfos
             */
            @Override
            public void onSuccess(final List<ImageInfo> imageInfos, final String dirPath) {
                if(!MediaLocationImageListSingerActivity.this.isFinishing()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null==dirPath){
                                ImageInfo imageInfo=new ImageInfo();
                                imageInfo.setItemType(1);
                                imageInfos.add(0,imageInfo);
                            }
                            if(null!=mAdapter) {
                                mAdapter.setNewData(null);
                                mAdapter.setNewData(imageInfos);
                            }
                            if(null!=imageInfos&&imageInfos.size()>0){
                                if(null!=mDataChangeView) mDataChangeView.stopLoading();
                            }else{
                                if(null!=mDataChangeView) mDataChangeView.showEmptyView(false);
                            }
                        }
                    });
                }
            }
        }).getLocationImages(dirPath,isGroup);
    }

    /**
     * 呼出文件夹目录
     */
    private void showDirMenus() {
        if(null!=mDirListAdapter&&null==mListPopupWindow){
            mListPopupWindow = new ListPopupWindow(MediaLocationImageListSingerActivity.this);
            mListPopupWindow.setAdapter(mDirListAdapter);
            mListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            if(mDirListAdapter.getCount()>6){
                mListPopupWindow.setHeight((ScreenUtils.getScreenHeight()/3)*2);
            }else{
                mListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            mListPopupWindow.setAnchorView(bindingView.llToolBar);
            mListPopupWindow.setModal(true);//响应触摸和硬件物理按键
            mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //优先切换至用户选中的目标
                    List<ImageDirInfo> data = mDirListAdapter.getData();
                    if(mFloderIndex!= position){
                        data.get(mFloderIndex).setSelected(false);
                        data.get(position).setSelected(true);
                        mDirListAdapter.notifyDataSetChanged();
                    }
                    mFloderIndex=position;
                    mListPopupWindow.dismiss();
                    ImageDirInfo imageDirInfo = data.get(position);
                    if(null!=bindingView) bindingView.tvTitle.setText(imageDirInfo.getDirName());
                    getLocationImages(imageDirInfo.getDirPath(),false);
                }
            });
            //消息监听
            mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1.0f);
                    if(null!=bindingView) bindingView.icDirection.setImageResource(R.drawable.ic_image_pull);
                }
            });
        }
        if(null==mListPopupWindow) return;
        bindingView.icDirection.setImageResource(R.drawable.ic_image_down);
        mListPopupWindow.show();
        //尝试定位至上次选中的位置
        ListView listView = mListPopupWindow.getListView();
        if(null!=listView) listView.setSelection(mFloderIndex);
        setBackgroundAlpha(0.5f);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onDestroy() {
        if(null!=mAdapter) mAdapter.setNewData(null);
        mAdapter=null;
        PhotoScanUtils.getInstance().onDestroy();
        super.onDestroy();
    }
}