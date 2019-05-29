package com.yc.liaolive.media.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.util.MusicUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.FileInfos;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityLocationAudioEditBinding;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.manager.UploadFileToOSSManager;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.PhotoSelectedUtil;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.VideoUtils;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 音频上传描述
 */

public class MediaLocationAudioEditActivity extends BaseActivity<ActivityLocationAudioEditBinding> {

    private static final String TAG = "MediaLocationAudioEditActivity";
    private String mAudioPath;
    private String mMd5ByFile;
    private int content_charMaxNum=10;//描述内容长度上限
    private Animation mInputAnimation;

    public static void start(Context context, String videoPath) {
        Intent intent=new Intent(context,MediaLocationAudioEditActivity.class);
        intent.putExtra("audioPath",videoPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioPath = getIntent().getStringExtra("audioPath");
        if(TextUtils.isEmpty(mAudioPath)){
            ToastUtils.showCenterToast("参数错误！");
            finish();
            return;
        }
        setContentView(R.layout.activity_location_audio_edit);
        mInputAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
    }

    @Override
    public void initViews() {
        bindingView.titltView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }
        });
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //更换封面
                    case R.id.tv_audio_tisp:
                    case R.id.ic_audio_image:
                        changedAudioCover();
                        break;
                    //更换音频源
                    case R.id.tv_audio_change:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,Constant.SELECT_AUDIO_REQUST2);
                        break;
                    //提交上传
                    case R.id.btn_submit:
                        upload();
                        break;
                }
            }
        };
        bindingView.icAudioImage.setOnClickListener(onClickListener);
        bindingView.tvAudioTisp.setOnClickListener(onClickListener);
        bindingView.tvAudioChange.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
    }

    @Override
    public void initData() {
        //监听输入框文字
        bindingView.etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.bt_app_style_bg_selector);
                }else{
                    bindingView.btnSubmit.setBackgroundResource(R.drawable.bg_comment_button_false);
                }
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>content_charMaxNum){
                    bindingView.etInput.setText(Utils.subString(charSequence.toString(),content_charMaxNum));
                    bindingView.etInput.setSelection( bindingView.etInput.getText().toString().length());
                    ToastUtils.showCenterToast("描述文字不得超过"+content_charMaxNum+"个字符");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        bindingView.tvUploadTips.setText(Html.fromHtml("<font color='#FF7575'>*</font> "+getResources().getString(R.string.upload_audio_tips)));
        bindingView.tvTitlePath.setText(Html.fromHtml("<font color='#FF7575'>*</font> ASMR声音"));
        bindingView.tvTitleImage.setText(Html.fromHtml("<font color='#FF7575'>*</font> ASMR声音封面"));
        bindingView.tvTitleDesp.setText(Html.fromHtml("<font color='#FF7575'>*</font> ASMR声音介绍"));
        updataAudioData();
    }

    /**
     * 设置封面
     * @param audioCoverPath
     */
    private void setVideoCover(String audioCoverPath) {
        bindingView.tvAudioTisp.setVisibility(View.INVISIBLE);
        bindingView.icAudioImage.setVisibility(View.VISIBLE);
        Glide.with(MediaLocationAudioEditActivity.this)
                .load("file://" + audioCoverPath)
                .asBitmap()
                .placeholder(R.drawable.ic_default_live_min_icon)
                .error(R.drawable.ic_default_live_min_icon)
                .dontAnimate()
                .skipMemoryCache(true)
                .centerCrop()
                .into(new BitmapImageViewTarget(bindingView.icAudioImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                    }
                });
    }

    /**
     * 音频封面选择
     */
    private void changedAudioCover() {
        PhotoSelectedUtil.getInstance()
                .attachActivity(MediaLocationAudioEditActivity.this)
                .setCatScaleWidth(1)
                .setCatScaleHeight(1)
                .setCropMode(0)
                .setOutFileName(mMd5ByFile+".jpg")
                .setImageCutStyle(ClipImageActivity.THEME_STYLE_HIGH)
                .setOnSelectedPhotoOutListener(new PhotoSelectedUtil.OnSelectedPhotoOutListener() {
                    @Override
                    public void onOutFile(File file) {
                        //用户选中了封面后立即上传至OSS，MD5需要与音频ID保持一致
                        if(null!=file){
                            Logger.d(TAG,"file:"+file.getAbsolutePath());
                            setVideoCover(file.getAbsolutePath());
                            UploadObjectInfo uploadObjectInfo=new UploadObjectInfo();
                            //MD5文件
                            uploadObjectInfo.setFileMd5(mMd5ByFile);
                            uploadObjectInfo.setFilePath(file.getAbsolutePath());
                            uploadObjectInfo.setFileName(Utils.getFileName(file.getAbsolutePath()));
                            //确定存储在OOS文件目录名称
                            uploadObjectInfo.setUploadFileFolder(Constant.OOS_DIR_ASMR_IMAGE);
                            //确定文件类型，这里批量是图片类型，AUDIO音频封面文件Type:-1
                            uploadObjectInfo.setFileSourceType(Constant.OSS_FILE_TYPE_ASMR_COVER);
                            //获取文件基本信息
                            FileInfos fileInfo = VideoUtils.getVideoInfo(uploadObjectInfo.getFilePath(), uploadObjectInfo.getFileSourceType());
                            if (null != fileInfo) {
                                uploadObjectInfo.setFileWidth(fileInfo.getFileWidth());
                                uploadObjectInfo.setFileHeight(fileInfo.getFileHeight());
                                uploadObjectInfo.setVideoDurtion(fileInfo.getVideoDurtion());
                                uploadObjectInfo.setFileSize(fileInfo.getFileSize());
                            }
                            UploadFileToOSSManager.get(null).addUploadListener(new OnUploadObjectListener() {
                                @Override
                                public void onStart() {
                                    Logger.d(TAG,"onStart-->");
                                }

                                @Override
                                public void onProgress(long progress) {
                                }

                                @Override
                                public void onSuccess(UploadObjectInfo data, String msg) {
                                    Logger.d(TAG,"onSuccess-->msg："+msg);
                                }

                                @Override
                                public void onFail(int code, String errorMsg) {
                                    Logger.d(TAG,"onFail-->code:"+code+",errorMsg:"+errorMsg);
                                    ToastUtils.showCenterToast(errorMsg);
                                }
                            }).startcreateAsyncUploadTask(uploadObjectInfo);
                        }
                    }

                    @Override
                    public void onError(int code, String errorMsg) {

                    }
                }).startSystem();
    }

    /**
     * 上传
     */
    private void upload() {
        if(!TextUtils.isEmpty(mAudioPath)){
            String content = bindingView.etInput.getText().toString().trim();
            if(TextUtils.isEmpty(content)){
                ToastUtils.showCenterToast("请输入视频描述内容");
                if(null!=mInputAnimation) bindingView.etInput.startAnimation(mInputAnimation);
                return;
            }
            File file=new File(mAudioPath);
            if(file.exists()){
                UploadObjectInfo uploadObjectInfo=new UploadObjectInfo();
                //MD5文件
                uploadObjectInfo.setFileMd5(mMd5ByFile);
                uploadObjectInfo.setVideoDesp(content);
                uploadObjectInfo.setFilePath(file.getAbsolutePath());
                uploadObjectInfo.setFileName(mMd5ByFile+"."+Utils.getFilePostName(file.getAbsolutePath()));
                //确定存储在OOS文件目录名称
                uploadObjectInfo.setUploadFileFolder(Constant.OOS_DIR_ASMR_AUDIO);
                //确定文件类型，这里批量是图片类型，AUDIO音频文件Type:-1
                uploadObjectInfo.setFileSourceType(Constant.OSS_FILE_TYPE_ASMR_AUDIO);
                uploadObjectInfo.setFileSize(file.length()/1024);
                UploadFileToOSSManager.get(MediaLocationAudioEditActivity.this).addUploadListener(new OnUploadObjectListener() {
                    @Override
                    public void onStart() {
                        Logger.d(TAG,"onStart-AUDIO-->");
                    }

                    @Override
                    public void onProgress(long progress) {
                    }

                    @Override
                    public void onSuccess(UploadObjectInfo data, String msg) {
                        Logger.d(TAG,"onSuccess-AUDIO-->msg："+msg);
                        ToastUtils.showCenterToast(msg);
                        finish();
                    }

                    @Override
                    public void onFail(int code, String errorMsg) {
                        Logger.d(TAG,"onFail-AUDIO-->code:"+code+",errorMsg:"+errorMsg);
                        ToastUtils.showCenterToast(errorMsg);
                    }
                }).startcreateAsyncUploadTask(uploadObjectInfo);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG,"onActivityResult-->:requestCode:"+requestCode+",resultCode:"+resultCode);
        //切换音频
        if(requestCode==Constant.SELECT_AUDIO_REQUST2&&null!=data&&null!=data.getData()){
            if(null!=data.getData()){
                String realPathFromURI = MusicUtils.getInstance().getPathFromURI(MediaLocationAudioEditActivity.this,data.getData());
                Logger.d(TAG,"FILE_PATH:"+realPathFromURI);
                if(!TextUtils.isEmpty(realPathFromURI)){
                    this.mAudioPath =realPathFromURI;
                    updataAudioData();
                }
            }
        }
        PhotoSelectedUtil.getInstance().onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PhotoSelectedUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updataAudioData() {
        if(!TextUtils.isEmpty(mAudioPath)){
            File file=new File(mAudioPath);
            bindingView.tvAudioName.setText(file.getName());
            try {
                mMd5ByFile = FileUtils.getMd5ByFile(new File(mAudioPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mMd5ByFile="file"+String.valueOf(System.currentTimeMillis());
            }
            Logger.d(TAG,"FILE_MD5:"+mMd5ByFile);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PhotoSelectedUtil.getInstance().onDestroy();
        if(null!=mInputAnimation){
            mInputAnimation.cancel();
            mInputAnimation=null;
        }
    }
}