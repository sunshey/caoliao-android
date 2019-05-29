package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.nanchen.compresshelper.CompressHelper;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.ImageInfo;
import com.yc.liaolive.bean.ImageObserverEvent;
import com.yc.liaolive.bean.ImageParams;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityPreviewLocationImageListBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.media.view.PinchImageView;
import com.yc.liaolive.media.view.PinchImageViewPager;
import com.yc.liaolive.util.AnimationUtil;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.ToastUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017-06-09 19:21
 * 本地图片批量预览
 */
public class MediaLocationImageListPreviewActivity extends BaseActivity <ActivityPreviewLocationImageListBinding>{

    private static final String TAG = "MediaLocationImagePreviewActivity";
    private int mIndex;
    private List<ImageInfo> mImags;
    private int mMaxCount;//最大限制一次性可选择数量,为0表示不限制
    private int mCurrentCount;//现在已经选中了多少个
    private String mCompressOutPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaxCount = getIntent().getIntExtra("max_count", 0);
        setContentView(R.layout.activity_preview_location_image_list);
        Intent intent = getIntent();
        mIndex = intent.getIntExtra("index",0);
        mImags = VideoApplication.getInstance().getImages();
        //临时存放压缩文件的路径
        mCompressOutPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "CaoLiaoTemp" + File.separator;
        showImage();
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                        finish();
                        break;
                    case R.id.btn_success:
                        onSuccess();
                        break;
                    case R.id.btn_selected_state:

                        changedSelectdState(bindingView.viewPager.getCurrentItem(),true);
                        break;
                    case R.id.tv_title:
                        if(null!=bindingView) bindingView.viewPager.setCurrentItem(0);
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnSuccess.setOnClickListener(onClickListener);
        bindingView.btnSelectedState.setOnClickListener(onClickListener);
        bindingView.tvTitle.setOnClickListener(onClickListener);
    }


    /**
     * 直接完成去上传
     */
    private void onSuccess() {
        //点击完成，将当前预览的一张强制置为选中状态
        if(null!=mImags&&mImags.size()>0){
            int currentItem = bindingView.viewPager.getCurrentItem();
            if(mImags.size()>currentItem){
                mImags.get(currentItem).setSelector(true);
            }
            Intent intent=new Intent();
            List<ImageInfo> newImageInfos=new ArrayList<>();
            for (int i = 0; i < mImags.size(); i++) {
                if(mImags.get(i).isSelector()){
                    newImageInfos.add(mImags.get(i));
                }
            }
            ImageParams imageParams=new ImageParams();
            imageParams.setImags(newImageInfos);
            String images = new Gson().toJson(imageParams);
            intent.putExtra("preview_imags",images);
            setResult(Constant.PREVIRE_IMAGE_RESULT,intent);
            finish();
        }
    }

    @Override
    public void initData() {

    }

    /**
     * 显示图片
     */
    private void showImage() {
        bindingView.viewPager.setAdapter(new ImagePagerAdapter());
        bindingView.viewPager.setOnPageChangeListener(new PinchImageViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bindingView.tvTitle.setText((position+1)+"/"+mImags.size());
                if(null!=mImags&&mImags.size()>position){
                    ImageInfo imageInfo = mImags.get(position);
                    bindingView.btnSelectedState.setImageResource(imageInfo.isSelector()?R.drawable.ic_image_selected_true:R.drawable.ic_image_selected_false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bindingView.viewPager.setOffscreenPageLimit(1);
        bindingView.viewPager.setCurrentItem(mIndex);
        bindingView.tvTitle.setText((mIndex+1)+"/"+mImags.size());
        changedSelectdState(mIndex,false);
    }

    /**
     * 改变当前图片的选中状态
     * @param index 谁的状态需要改变
     * @param isNotify 是否通知其他界面需要更新最新的选中状态
     */
    private void changedSelectdState(int index,boolean isNotify) {
        if(null!=mImags&&mImags.size()>0){
            ImageInfo imageInfo = mImags.get(index);
            //用户触发了操作
            if(isNotify){
                if(0!=mMaxCount&&!imageInfo.isSelector()&&mCurrentCount>=mMaxCount){
                    ToastUtils.showCenterToast("一次最多只能选择"+mMaxCount+"张照片上传");
                    return;
                }
                imageInfo.setSelector(!imageInfo.isSelector());
            }
            bindingView.btnSelectedState.setImageResource(imageInfo.isSelector()?R.drawable.ic_image_selected_true:R.drawable.ic_image_selected_false);
            int count=0;
            for (ImageInfo datum : mImags) {
                if(datum.isSelector()){
                    count++;
                }
            }
            mCurrentCount=count;
            bindingView.tvCount.setTag(count);
            bindingView.tvCount.setVisibility(count>0?View.VISIBLE:View.GONE);
            String unReadStr = String.valueOf(count);
            bindingView.tvCount.setText(unReadStr+"/"+mMaxCount);
            AnimationUtil.playTextCountAnimation2(bindingView.btnSelectedState);
            AnimationUtil.playTextCountAnimation2(bindingView.tvCount);
            if(isNotify){
                //通知外面界面，有对象被选中
                ImageObserverEvent imageObserverEvent=new ImageObserverEvent();
                imageObserverEvent.setPosition(imageInfo.getPosition());
                imageObserverEvent.setImageInfo(imageInfo);
                ApplicationManager.getInstance().observerUpdata(imageObserverEvent);
            }
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImags == null ? 0 : mImags.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final PinchImageView piv=new PinchImageView(MediaLocationImageListPreviewActivity.this);
            container.addView(piv);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    File newFile=null;
                    String url=mImags.get(position).getFilePath();
                    try {
                        newFile= new CompressHelper.Builder(VideoApplication.getInstance().getApplicationContext())
                                .setQuality(90)//压缩质量
                                .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                                .setDestinationDirectoryPath(mCompressOutPath)
                                .build()
                                .compressToFile(new File(url));
                    }catch (RuntimeException e){

                    }finally {
                        //使用压缩后的图片路径节约内存
                        if(null!=newFile) url=newFile.getAbsolutePath();
                        final String finalUrl = url;
                        if(!MediaLocationImageListPreviewActivity.this.isFinishing()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(null!=container){
                                        Glide.with(container.getContext())
                                                .load(finalUrl)
                                                .error(R.drawable.ic_error)
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(piv);
                                        bindingView.viewPager.setMainPinchImageView(piv);
                                    }
                                }
                            });
                        }
                    }
                }
            }.start();
            return piv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onDestroy() {
        if(null!=bindingView) bindingView.viewPager.removeAllViews();
        mCurrentCount=0;
        if(null!=mCompressOutPath) FileUtils.deleteFile(mCompressOutPath);
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.screen_zoom_out);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
