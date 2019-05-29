package com.yc.liaolive.media.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.media.adapter.VideoExtractAdapter;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.adapter.BaseQuickAdapter;
import com.yc.liaolive.bean.VideoEditInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityLocationVideoFrameBinding;
import com.yc.liaolive.util.DeviceUtils;
import com.yc.liaolive.util.ExtractFrameWorkThread;
import com.yc.liaolive.util.ExtractVideoInfoUtil;
import com.yc.liaolive.util.PictureUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 本地视频封面截取
 */

public class MediaLocationVideoFrameActivity extends BaseActivity<ActivityLocationVideoFrameBinding>{

    private long mVideoFrame;//视频的帧数，默认为第一帧
    private String mVideoPath;
    private ExtractVideoInfoUtil mExtractVideoInfoUtil;
    private ExtractFrameWorkThread mExtractFrameWorkThread;
    private final String OutPutFileDirPath = Environment.getExternalStorageDirectory() + "/VideoLive/Extract";
    private VideoExtractAdapter mAdapter;
    private int count=0;
    private int mPosition=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoPath = getIntent().getStringExtra("videoPath");
        if(TextUtils.isEmpty(mVideoPath)){
            ToastUtils.showCenterToast("参数错误！");
            finish();
            return;
        }
        mVideoFrame = getIntent().getLongExtra("videoFrame",0);
        setContentView(R.layout.activity_location_video_frame);
    }

    @Override
    public void initViews() {
        bindingView.titltView.showMoreTitle(true);
        bindingView.titltView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }
            @Override
            public void onMoreTitleClick(View v) {
                super.onMoreTitleClick(v);
                Intent intent=new Intent();
                intent.putExtra("newVideoFrame",mVideoFrame);
                setResult(Constant.MEDIA_CIDEO_CAT_RESULT,intent);
                finish();
            }
        });
        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(MediaLocationVideoFrameActivity.this,LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new VideoExtractAdapter(null);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(mPosition!=position){
                    mAdapter.getData().get(mPosition).setSelected(false);
                    mAdapter.notifyItemChanged(mPosition,"newVideoFrame");
                    mAdapter.getData().get(position).setSelected(true);
                    mAdapter.notifyItemChanged(position,"newVideoFrame");
                    setBigCover(mAdapter.getData().get(position));
                }
                mPosition=position;
            }
        });
        bindingView.recyclerView.setAdapter(mAdapter);
        bindingView.icBigCover.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
    }

    @Override
    public void initData() {
        mExtractVideoInfoUtil = new ExtractVideoInfoUtil(mVideoPath);
        long endPosition = Long.valueOf(mExtractVideoInfoUtil.getVideoLength());
        long startPosition = 0;
        int thumbnailsCount = 10;
        int extractW = (Utils.getScreenWidth()) / 4;
        int extractH = DeviceUtils.dip2px(this, 55);
        mExtractFrameWorkThread = new ExtractFrameWorkThread(extractW, extractH, mUIHandler, mVideoPath, OutPutFileDirPath, startPosition, endPosition, thumbnailsCount);
        mExtractFrameWorkThread.start();
    }

    /**
     * 更新大封面
     * @param data
     */
    private void setBigCover(VideoEditInfo data){
        if(null==data) return;
        this.mVideoFrame=data.getTime();
        if(null==bindingView) return;
        bindingView.icBigCover.reset();
        if (!MediaLocationVideoFrameActivity.this.isFinishing()) {
            Glide.with(MediaLocationVideoFrameActivity.this)
                    .load("file://" + data.getPath())
                    .placeholder(bindingView.icBigCover.getDrawable())
                    .error(R.drawable.ic_default_item_cover)
                    .dontAnimate()
                    .skipMemoryCache(true)
                    .into(bindingView.icBigCover);
        }
    }

    private final MainHandler mUIHandler = new MainHandler();

    private  class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ExtractFrameWorkThread.MSG_SAVE_SUCCESS) {
                VideoEditInfo info = (VideoEditInfo) msg.obj;
                if(0==count) {
                    info.setSelected(true);
                    setBigCover(info);
                }
                if(null!=mAdapter){
                    mAdapter.addData(info);
                }
                count++;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mExtractVideoInfoUtil) mExtractVideoInfoUtil.release();
        if (mExtractFrameWorkThread != null) {
            mExtractFrameWorkThread.stopExtract();
        }
        mUIHandler.removeCallbacksAndMessages(null);
        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
            PictureUtils.deleteFile(new File(OutPutFileDirPath));
        }
        count=0;mPosition=0;
    }
}
