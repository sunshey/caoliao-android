package com.yc.liaolive.start.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.bean.BuildMessageInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityBuildManagerBinding;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.start.manager.VersionCheckManager;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.start.service.DownLoadService;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.videocall.manager.VideoCallManager;

import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/8/28
 * 版本更新提示
 */

public class BuildManagerActivity extends TopBaseActivity implements Observer {

    private UpdataApkInfo mUpdataInfo;
    private ActivityBuildManagerBinding bindingView;

    public static void start( UpdataApkInfo updataApkInfo) {
        Intent intent=new Intent(AppEngine.getApplication(), BuildManagerActivity.class);
        intent.putExtra("updata_info",updataApkInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppEngine.getApplication().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoCallManager.getInstance().setBebusying(true);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_build_manager);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//需要添加的语句
        initLayoutParams();
        setFinishOnTouchOutside(false);//点击外部禁止关闭
        mUpdataInfo = (UpdataApkInfo) getIntent().getSerializableExtra("updata_info");
        init();
        ApplicationManager.getInstance().addObserver(this);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mUpdataInfo = (UpdataApkInfo) intent.getSerializableExtra("updata_info");
        init();
    }

    private void init () {
        if(null == mUpdataInfo){
            ToastUtils.showCenterToast("参数错误");
            finish();
            return;
        }
        initViews();
    }

    /**
     * 组件初始化
     */
    private void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //取消更新
                    case R.id.btn_close:
                    case R.id.btn_cancel:
                        finish();
                        break;
                    //开始更新
                    case R.id.btn_next:
                        if (mUpdataInfo.isAlreadyDownload()) {
                            VersionCheckManager
                                    .getInstance().installAPK(BuildManagerActivity.this);
                        } else {
                            if(null != bindingView.btnNext.getTag()){
                                Integer tag = (Integer) bindingView.btnNext.getTag();
                                //开始下载
                                if(1==tag){
                                    startServer();
                                    //后台下载
                                }else if(2==tag){
                                    if(null!=mUpdataInfo&&1==mUpdataInfo.getCompel_update()){
                                        ToastUtils.showCenterToast("请等待下载完成后安装");
                                        return;
                                    }
                                    finish();
                                }
                            }
                            startServer();
                        }
                        break;
                }
            }
        };
        bindingView.btnNext.setTag(1);//默认是立即下载
        if (mUpdataInfo.isAlreadyDownload()) {
            bindingView.btnNext.setText("免下载安装");
        } else{
            bindingView.btnNext.setText("立即更新");
        }
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnCancel.setOnClickListener(onClickListener);
        bindingView.btnNext.setOnClickListener(onClickListener);
        //配置可滑动
        bindingView.tvTipsContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        if(null==mUpdataInfo) return;
        String content=TextUtils.isEmpty(mUpdataInfo.getUpdate_log())?getResources().getString(R.string.upload_tips):mUpdataInfo.getUpdate_log();
        bindingView.tvTipsContent.setText(content);
        bindingView.buildTitle.setText("版本更新：V"+mUpdataInfo.getVersion());
        bindingView.tvDownloadProgress.setText("0MB/"+mUpdataInfo.getSize()+"MB");

        if(1 == mUpdataInfo.getCompel_update()){
            //切换到强制更新模式
            bindingView.btnClose.setVisibility(View.GONE);
            bindingView.btnCancel.setVisibility(View.GONE);
        } else if (mUpdataInfo.isAlreadyDownload()) {
            //已经下载完成
            bindingView.btnCancel.setVisibility(View.VISIBLE);
            bindingView.btnClose.setVisibility(View.GONE);
        } else {
            bindingView.btnCancel.setVisibility(View.GONE);
            bindingView.btnClose.setVisibility(View.VISIBLE);
        }
        //更新状态为正在下载中
        if(VideoApplication.getInstance().isDownloadAPK()) startDownload();
    }

    /**
     * 开启下载服务
     */
    private void startServer() {
        if(null==mUpdataInfo) return;
        Intent service = new Intent(BuildManagerActivity.this, DownLoadService.class);
        service.putExtra("downloadurl", mUpdataInfo.getDown_url());
        startService(service);
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        if(null==bindingView) return;
        bindingView.tvTipsContent.setVisibility(View.GONE);
        bindingView.llDownloadView.setVisibility(View.VISIBLE);
        bindingView.btnNext.setText("后台下载");
        bindingView.btnNext.setTag(2);
        //强制更新后台下载不可用
        if(null!=mUpdataInfo&&1==mUpdataInfo.getCompel_update()){
            bindingView.btnNext.setVisibility(View.INVISIBLE);
            bindingView.btnNext.setClickable(false);
        }
    }

    /**
     * 还原下载状态
     */
    private void restoreDownload() {
        if(null==bindingView) return;
        bindingView.tvTipsContent.setVisibility(View.VISIBLE);
        bindingView.llDownloadView.setVisibility(View.GONE);
        bindingView.tvDownloadProgress.setText("0MB/"+mUpdataInfo.getSize()+"MB");
        bindingView.btnNext.setVisibility(View.VISIBLE);
        bindingView.btnNext.setClickable(true);
        bindingView.btnNext.setText("立即更新");
        bindingView.btnNext.setTag(1);
    }

    /**
     * 下载状态
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&& arg instanceof BuildMessageInfo){
            BuildMessageInfo info= (BuildMessageInfo) arg;
            //开始下载
            if(TextUtils.equals(Constant.BUILD_START,info.getCmd())){
                startDownload();
            //下载中
            }else if(TextUtils.equals(Constant.BUILD_DOWNLOAD,info.getCmd())){
                updateProgress(info);
            //拦截的正在下载中的事件
            }else if(TextUtils.equals(Constant.BUILD_DOWNLOADING,info.getCmd())){
                startDownload();
            //下载完成
            }else if(TextUtils.equals(Constant.BUILD_END,info.getCmd())){
                if(null!=bindingView) bindingView.tvDownloadTips.setText(getResources().getString(R.string.download_success));
                updateProgress(info);
                if(null!=mUpdataInfo&&null!=bindingView) {
                    //普通的更新
                    if (0 == mUpdataInfo.getCompel_update()){
                        finish();
                        return;
                    //强制更新
                    }else if(1 == mUpdataInfo.getCompel_update()){
                        bindingView.btnNext.setText("立即更新");
                        bindingView.btnNext.setVisibility(View.VISIBLE);
                        bindingView.btnNext.setClickable(true);
                        bindingView.btnNext.setTag(1);
                    }
                }
            //强制更新，不允许关闭本界面
            //下载失败
            }else if(TextUtils.equals(Constant.BUILD_ERROR,info.getCmd())){
                stopService(new Intent(BuildManagerActivity.this, DownLoadService.class));
                restoreDownload();
            }
        }
    }

    /**
     * 刷新下载进度条
     * @param info
     */
    private void updateProgress(BuildMessageInfo info) {
        try {
            if(null!=bindingView&&null!=info){
                int totalSize = info.getTotalSize();
                int downloadSize = info.getDownloadSize();
                bindingView.tvDownloadProgress.setText(Utils.getSize(downloadSize)+"/"+Utils.getSize(totalSize));
                int progress = (int) (downloadSize * 1.0f / totalSize * 100);
                bindingView.pbDownloadProgress.setProgress(progress);
            }
        }catch (RuntimeException e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoCallManager.getInstance().setBebusying(false);
        ApplicationManager.getInstance().removeObserver(this);
    }

    @Override
    public void onBackPressed() {
        //拦截强制更新
        if(null!=mUpdataInfo&&1==mUpdataInfo.getCompel_update()){
            return;
        }
        super.onBackPressed();
    }
}
