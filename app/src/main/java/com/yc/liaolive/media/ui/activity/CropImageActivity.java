package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityCropImageBinding;
import com.yc.liaolive.util.IOUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import java.io.File;
import java.io.FileOutputStream;

/**
 * TinyHung@Outlook.com
 * 2018/10/13
 * 单张照片预览和裁剪
 */

public class CropImageActivity extends BaseActivity<ActivityCropImageBinding> {

    private static final String TAG = "CropImageActivity";
    private String mOutputPath;
    private  final String IMAGE_DRR_PATH = "photo_image.jpg";//最终输出图片

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String inputPath = intent.getStringExtra("inputPath");
        mOutputPath = intent.getStringExtra("outputPath");
        boolean fixAspectRatio = intent.getBooleanExtra("fixAspectRatio", false);
        if(TextUtils.isEmpty(inputPath)){
            ToastUtils.showCenterToast("请传入有效图片路径！");
            return;
        }
        setContentView(R.layout.activity_crop_image);
        //配置是否固定比例
        bindingView.cropImageView.setFixedAspectRatio(fixAspectRatio);
        //设置图片
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputPath, options);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap source = BitmapFactory.decodeFile(inputPath, options);
        bindingView.cropImageView.setImageBitmap(source);
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_back:
                        onBackPressed();
                        break;
                    //完成裁剪
                    case R.id.btn_submit:
                        toCropImage();
                        break;
                }
            }
        };
        bindingView.btnBack.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
    }

    @Override
    public void initData() {

    }

    /**
     * 去裁剪
     */
    private void toCropImage() {
        if(TextUtils.isEmpty(mOutputPath)){
            mOutputPath= Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator+ "VideoLive"+ File.separator+ "Photo"+File.separator;
            File file=new File(mOutputPath);
            if(!file.exists()){
                file.mkdirs();
            }
            mOutputPath=mOutputPath+IMAGE_DRR_PATH;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mOutputPath);
            Bitmap bitmap = bindingView.cropImageView.getCroppedImage();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception e) {
        } finally {
            if(null!=fos){
                IOUtils.close(fos);
            }
            Intent intent=new Intent();
            intent.putExtra("outPath",mOutputPath);
            setResult(Constant.SELECT_CROP_IMAGE_RESULT,intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=bindingView) bindingView.cropImageView.setImageResource(0);
    }
}
