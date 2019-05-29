package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.google.gson.Gson;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.ImageDirInfo;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.bean.ImageObserverEvent;
import com.yc.liaolive.bean.ImageParams;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityMediaImageListBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.adapter.ImageDirListAdapter;
import com.yc.liaolive.media.adapter.ImagesListAdapter;
import com.yc.liaolive.model.RecyclerViewSpacesItem;
import com.yc.liaolive.msg.view.ListEmptyFooterView;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.PhotoScanUtils;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.StatusUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.layout.DataChangeView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/9/19
 * 本地图片批量选择操作
 */

public class MediaLocationImageListActivity  extends BaseActivity<ActivityMediaImageListBinding> implements Observer {

    private static final String TAG = "MediaLocationImageListActivity";
    private ImagesListAdapter mAdapter;
    private ListPopupWindow mListPopupWindow;
    private ImageDirListAdapter mDirListAdapter;
    private DataChangeView mDataChangeView;
    private int mFloderIndex =0;
    private int mMaxCount;//最大限制一次性可选择数量,为0表示不限制
    private int mCurrentCount;//现在已经选中了多少个

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxCount = getIntent().getIntExtra("max_count", 0);
        setContentView(R.layout.activity_media_image_list);
        StatusUtils.setStatusTextColor1(true,MediaLocationImageListActivity.this);//白色背景，黑色字体
        ApplicationManager.getInstance().addObserver(this);
        getLocationImages(null,true);
    }

    @Override
    public void initViews() {
        bindingView.recylerView.setLayoutManager(new GridLayoutManager(MediaLocationImageListActivity.this, 3, LinearLayoutManager.VERTICAL, false));
        bindingView.recylerView.addItemDecoration(new RecyclerViewSpacesItem(ScreenUtils.dpToPxInt(1.5f)));
        mAdapter = new ImagesListAdapter(null,0);
        //设置空视图
        mDataChangeView = new DataChangeView(MediaLocationImageListActivity.this);
        mDataChangeView.showLoadingView();
        mAdapter.setEmptyView(mDataChangeView);
        mAdapter.showEmptyView(true);
        mAdapter.addFooterView(new ListEmptyFooterView(MediaLocationImageListActivity.this));
        //选中事件回调
        mAdapter.setOnSelectedListener(new ImagesListAdapter.OnSelectedListener() {
            @Override
            public void onSelectedChanged(View itemView,View view,ImageInfo weiXinVideo, int position) {
                if(null!=view.getTag()){
                    ImageInfo imageInfo = (ImageInfo) view.getTag();
                    if(0!=mMaxCount&&!imageInfo.isSelector()&&mCurrentCount>=mMaxCount){
                        ToastUtils.showCenterToast("一次最多只能选择"+mMaxCount+"张照片上传");
                        return;
                    }
                    //避免item闪烁，直接更新局部控件状态
                    imageInfo.setSelector(!imageInfo.isSelector());

                    View emptyView = itemView.findViewById(R.id.item_empty);
                    emptyView.setVisibility(imageInfo.isSelector()?View.VISIBLE:View.GONE);
                    ImageView selectedState = (ImageView) itemView.findViewById(R.id.btn_selected_state);
                    selectedState.setImageResource(imageInfo.isSelector()?R.drawable.ic_image_selected_true:R.drawable.ic_image_selected_false);
                    view.setTag(imageInfo);
                    itemView.setTag(imageInfo);
                    AnimationUtil.playTextCountAnimation2(selectedState);
//                    mAdapter.notifyItemChanged(position,"upload_image");//只是局部刷新
                    //改变已选中的数量
                    changedSelected();
                }
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null!=view.getTag()){
                    ImageInfo imageInfo = (ImageInfo) view.getTag();
                    if(1==imageInfo.getItemType()){
                        PhotoSelectedUtil.getInstance()
                                .attachActivity(MediaLocationImageListActivity.this)
                                .setClipCircle(false)
                                .setSkipClip(true)//跳过裁剪
                                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                                    @Override
                                    public void onOutFile(File file) {
                                        //结束选择,回到上层界面开始上传
                                        Intent intent=new Intent();
                                        intent.putExtra("selected_image",file.getAbsolutePath());
                                        setResult(Constant.SELECT_IMAGE_RESULT,intent);
                                        finish();
                                    }

                                    @Override
                                    public void onError(int code, String errorMsg) {
                                    }
                                }).startTakePictrue();
                        return;
                    }
                    VideoApplication.getInstance().setImages(mAdapter.getData());
                    Intent intent=new Intent(MediaLocationImageListActivity.this,MediaLocationImageListPreviewActivity.class);
                    intent.putExtra("index",position);
                    intent.putExtra("max_count",mMaxCount);
                    startActivityForResult(intent,Constant.PREVIRE_IMAGE_REQUST);
                }
            }
        });
        bindingView.recylerView.setAdapter(mAdapter);

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                    case R.id.btn_close:
                        finish();
                        break;
                    //顶部标题
                    case R.id.btn_title:
                        showDirMenus();
                        break;
                    //完成
                    case R.id.btn_success:
                        if(bindingView.tvSuccess.getText().equals("请选择")) return;
                        btnSuccess(false);
                        break;
                    //预览
                    case R.id.btn_preview:
                        btnSuccess(true);
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnTitle.setOnClickListener(onClickListener);
        bindingView.btnSuccess.setOnClickListener(onClickListener);
        bindingView.btnPreview.setOnClickListener(onClickListener);
        bindingView.reBottomBar.getBackground().setAlpha(210);
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
                if(!MediaLocationImageListActivity.this.isFinishing()) runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=bindingView&&null!=tabs&&tabs.size()>0){
                            bindingView.tvTitle.setText(tabs.get(0).getDirName());
                            bindingView.icDirection.setImageResource(R.drawable.ic_image_pull);
                        }
                        mFloderIndex =0;
                        //文件目录
                        if(null==mDirListAdapter) mDirListAdapter = new ImageDirListAdapter(MediaLocationImageListActivity.this,null);
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
                if(!MediaLocationImageListActivity.this.isFinishing()){
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
                            //还原已选中的状态显示
                            if(null!=bindingView){
                                bindingView.tvSuccess.setText("请选择");
                                bindingView.tvSuccess.setTextColor(getResources().getColor(R.color.colorTextG3));
                                bindingView.tvCount.setText("");
                                bindingView.tvCount.setVisibility(View.GONE);
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
     * 结束选择
     * @param isPreview 是否前往预览
     */
    private void btnSuccess(boolean isPreview) {
        List<ImageInfo> newImageInfos=new ArrayList<>();
        List<ImageInfo> data = mAdapter.getData();
        for (ImageInfo item : data) {
            if(item.isSelector()){
                item.setSelecte(1);
                newImageInfos.add(item);
            }
        }
        //前往预览
        if(isPreview){
            if(newImageInfos.size()<=0){
                ToastUtils.showCenterToast("请先选择图片");
                return;
            }
            VideoApplication.getInstance().setImages(newImageInfos);
            Intent intent=new Intent(MediaLocationImageListActivity.this,MediaLocationImageListPreviewActivity.class);
            intent.putExtra("index",0);
            intent.putExtra("max_count",mMaxCount);
            startActivityForResult(intent,Constant.PREVIRE_IMAGE_REQUST);
            return;
        }
        //结束选择,回到上层界面开始上传
        Intent intent = new Intent();
        ImageParams imageParams = new ImageParams();
        imageParams.setImags(newImageInfos);
        String images = new Gson().toJson(imageParams);
        intent.putExtra("selected_images",images);
        setResult(Constant.SELECT_IMAGE_RESULT,intent);
        finish();
    }

    /**
     * 改变选中的状态
     */
    private void changedSelected() {
        List<ImageInfo> data = mAdapter.getData();
        int count=0;
        for (ImageInfo datum : data) {
            if(datum.isSelector()){
                count++;
            }
        }
        mCurrentCount=count;
        bindingView.tvCount.setVisibility(count>0?View.VISIBLE:View.GONE);
        String unReadStr = String.valueOf(count);
        bindingView.tvCount.setText(unReadStr+"/"+mMaxCount);
        AnimationUtil.playTextCountAnimation2(bindingView.tvCount);
        bindingView.tvSuccess.setText(count>0?"完成":"请选择");
        bindingView.tvSuccess.setTextColor(count>0?getResources().getColor(R.color.app_style):getResources().getColor(R.color.colorTextG3));
    }


    /**
     * 呼出文件夹目录
     */
    private void showDirMenus() {
        if(null!=mDirListAdapter&&null==mListPopupWindow){
            mListPopupWindow = new ListPopupWindow(MediaLocationImageListActivity.this);
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
        //如果在预览界面点击的完成，直接回去上传
        if(requestCode==Constant.PREVIRE_IMAGE_REQUST&&resultCode==Constant.PREVIRE_IMAGE_RESULT){
                if(null!=data.getStringExtra("preview_imags")){
                    Intent intent=new Intent();
                    intent.putExtra("selected_images",data.getStringExtra("preview_imags"));
                    setResult(Constant.SELECT_IMAGE_RESULT,intent);
                    finish();
                }
            return;
        }
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onDestroy() {
        if(null!=mListPopupWindow) mListPopupWindow.dismiss();mListPopupWindow=null;
        if(null!=mAdapter) mAdapter.setNewData(null);
        mAdapter=null;
        ApplicationManager.getInstance().removeObserver(this);
        PhotoScanUtils.getInstance().onDestroy();
        mCurrentCount=0;mMaxCount=0;
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg && arg instanceof ImageObserverEvent){
            ImageObserverEvent imageObserverEvent= (ImageObserverEvent) arg;
            int position = imageObserverEvent.getPosition();
            ImageInfo imageInfo = imageObserverEvent.getImageInfo();
            if(null!=mAdapter&&null!=imageInfo){
                if(null!=mAdapter) {
                    List<ImageInfo> data = mAdapter.getData();
                    if(null!=data&&data.size()>position){
                        mAdapter.notifyItemChanged(position,"upload_image");
                        //防止Item不可见，直接修改值
                        data.get(position).setSelector(imageInfo.isSelector());
                    }
                    //改变已选中的数量
                    changedSelected();
                }
            }
        }
    }
}