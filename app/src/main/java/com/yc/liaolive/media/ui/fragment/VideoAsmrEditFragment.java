package com.yc.liaolive.media.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.UploadObjectInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentVideoAmsrEditBinding;
import com.yc.liaolive.interfaces.OnUploadObjectListener;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.manager.UploadFileToOSSManager;
import com.yc.liaolive.media.ui.activity.MediaLocationVideoFrameActivity;
import com.yc.liaolive.util.ExtractVideoInfoUtil;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.PictureUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2019/3/28
 * ASMR视频上传-描述
 */

public class VideoAsmrEditFragment extends BaseFragment<FragmentVideoAmsrEditBinding,RxBasePresenter> {

    private static final String TAG = "VideoAsmrEditFragment";
    private String mFilePath;
    private long mVideoFrame;//视频的帧数，默认为第一帧
    private Animation mInputAnimation;
    private ExtractVideoInfoUtil mExtractVideoInfoUtil;
    //最大描述输入长度
    public int content_charMaxNum=50;
    //临时的封面存储目录
    private final String OutPutFileDirPath = Environment.getExternalStorageDirectory() + "/VideoLive/TempExtract";

    public static VideoAsmrEditFragment getInstance(String filePath) {
        VideoAsmrEditFragment fragment=new VideoAsmrEditFragment();
        Bundle bundle=new Bundle();
        bundle.putString("filePath", filePath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mFilePath = arguments.getString("filePath",null);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_amsr_edit;
    }

    @Override
    protected void initViews() {
        bindingView.tvTextNum.setText(String.format(getString(R.string.nick_count),0 , content_charMaxNum));
        bindingView.tvUploadTips.setText(Html.fromHtml("<font color='#FF7575'>*</font> "+getResources().getString(R.string.upload_audio_tips)));
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //更换封面
                    case R.id.video_cover:
                        Intent intent=new Intent(getActivity(),MediaLocationVideoFrameActivity.class);
                        intent.putExtra("videoFrame",mVideoFrame);
                        intent.putExtra("videoPath",mFilePath);
                        startActivityForResult(intent, Constant.MEDIA_CIDEO_CAT_REQUST);
                        break;
                    //立即上传
                    case R.id.btn_upload:
                        upload();
                        break;
                }
            }
        };
        bindingView.videoCover.setOnClickListener(onClickListener);
        bindingView.btnUpload.setOnClickListener(onClickListener);
        //监听输入框文字
        bindingView.etInput.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)&&charSequence.length()>0){
                    bindingView.tvTextNum.setText(charSequence.length()+"/"+content_charMaxNum);
                    bindingView.btnUpload.setBackgroundResource(R.drawable.bg_comment_button_true);
                    if(charSequence.length()>content_charMaxNum){
                        bindingView.etInput.setText(Utils.subString(charSequence.toString(),content_charMaxNum));
                        bindingView.etInput.setSelection( bindingView.etInput.getText().toString().length());
                        ToastUtils.showCenterToast("描述文字不得超过"+content_charMaxNum+"个字符");
                    }
                }else{
                    bindingView.tvTextNum.setText("0/"+content_charMaxNum);
                    bindingView.btnUpload.setBackgroundResource(R.drawable.bg_comment_button_false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        mExtractVideoInfoUtil = new ExtractVideoInfoUtil(mFilePath);
        setVideoCover();
    }

    /**
     * 设置封面
     */
    private void setVideoCover() {
        if(null!=mExtractVideoInfoUtil&&null!=bindingView){
            String path = mExtractVideoInfoUtil.extractFrames(OutPutFileDirPath,mVideoFrame);
            Glide.with(this)
                    .load("file://" + path)
                    .error(R.drawable.ic_default_live_min_icon)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(bindingView.videoCover);
        }
    }

    /**
     * 上传
     */
    private void upload() {
        String content = bindingView.etInput.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            ToastUtils.showCenterToast("请输入视频描述内容");
            if(null!=mInputAnimation) bindingView.etInput.startAnimation(mInputAnimation);
            return;
        }
        File file=new File(mFilePath);
        if(file.exists()){
            UploadObjectInfo uploadObjectInfo=new UploadObjectInfo();
            uploadObjectInfo.setFilePath(mFilePath);
            uploadObjectInfo.setVideoDesp(content);
            uploadObjectInfo.setVideoFrame(mVideoFrame);
            UploadFileToOSSManager.get(getActivity()).addUploadListener(new OnUploadObjectListener() {
                @Override
                public void onStart() {
                    Logger.d(TAG,"onStart-->");
                }

                @Override
                public void onProgress(long progress) {
                }

                @Override
                public void onSuccess(UploadObjectInfo data, String msg) {
                    Logger.d(TAG,"onSuccess-->data:"+data+",msg:"+msg);
                    ToastUtils.showCenterToast(msg);
                    //通知视频选择界面关闭自己
                    ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_CLOSE_LOCATION_VIDEO_ACTIVITY);
                    getActivity().finish();
                }

                @Override
                public void onFail(int code, String errorMsg) {
                    Logger.d(TAG,"onFail-->code:"+code+",errorMsg:"+errorMsg);
                    ToastUtils.showCenterToast(errorMsg);

                }
            }).createAsyncUploadTaskToAsmr(uploadObjectInfo);
        }else{
            ToastUtils.showCenterToast("文件地址不正确，请重刷新列表重试");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG,"onActivityResult-->requestCode:"+requestCode+",resultCode:"+resultCode);
        //选取封面回执
        if(requestCode==Constant.MEDIA_CIDEO_CAT_REQUST&&resultCode==Constant.MEDIA_CIDEO_CAT_RESULT){
            mVideoFrame = data.getLongExtra("newVideoFrame", 0);
            setVideoCover();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mInputAnimation){
            mInputAnimation.cancel();
            mInputAnimation=null;
        }
        if(null!=mExtractVideoInfoUtil) mExtractVideoInfoUtil.release();
        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
            PictureUtils.deleteFile(new File(OutPutFileDirPath));
        }
    }
}