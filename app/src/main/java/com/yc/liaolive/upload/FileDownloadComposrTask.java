package com.yc.liaolive.upload;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.ui.dialog.DownloadProgressDialog;
import com.yc.liaolive.util.attach.FileDownloadTask;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2017/12/9
 * 这个类负责下载整套流程，检查缓存，复制，下载
 */

public class FileDownloadComposrTask {

    private String mOutPath= Constant.IMAGE_PATH;//输出路径
    private DownloadProgressDialog mUploadProgressView;
    private final Activity mContext;
    private final String mFileNetPath;

    /**
     * 该构造函数默认下载至/YunLin/Video
     * @param context
     * @param fileNetPath
     */
    public FileDownloadComposrTask(Activity context, String fileNetPath) {
        if(context instanceof Activity){
            this.mContext=context;
            this.mFileNetPath=fileNetPath;
        }else {
            throw new IllegalStateException("Error! You must preset so Activity Context!");
        }
    }

    /**
     *
     * @param context
     * @param fileNetPath
     * @param outPath 输出路径
     */
    public FileDownloadComposrTask(Activity context, String fileNetPath, String outPath) {
        if(context instanceof Activity){
            this.mContext=context;
            this.mFileNetPath=fileNetPath;
            if(null!=outPath){
                this.mOutPath=outPath;
            }
        }else {
            throw new IllegalStateException("Error! You must preset so Activity Context!");
        }
    }



    public void start() {
        File filePath=new File(mOutPath);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        createDownload();
    }

    /**
     * 下载之前的准备工作
     */
    private void createDownload() {
        //文件地址可能不是.mp4结尾的，需要判断下 rexVideoPath(name);
        File finlishFilePath=new File(mOutPath, Utils.rexVideoPath(Utils.getFileName(mFileNetPath)));
        //先判断是否存在将要下载的视频文件
        if(finlishFilePath.exists()&&finlishFilePath.isFile()){
            ToastUtils.showCenterToast("已保存至本地"+finlishFilePath.getAbsolutePath());
        }else{
            //开始下载
            startDownloadTask();
        }
    }


    /**
     * 开始下载
     */
    private void startDownloadTask() {

        //下载视频类型文件
        new FileDownloadTask(FileDownloadTask.FILE_TYPE_VIDEO,mOutPath, new FileDownloadTask.OnDownloadListener() {
            @Override
            public void downloadStart() {
                showDownloadTips("下载完成");
            }

            @Override
            public void downloadProgress(int progress) {
                if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                    mUploadProgressView.setProgress(progress);
                }
            }

            @Override
            public void downloadFinlish(File file) {
                if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                    mUploadProgressView.setTipsMessage("已保存至本地");
                }
                clodeDownloadProgress();
                Uri localUri = Uri.parse("file://"+ file.getAbsolutePath());
                Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                localIntent.setData(localUri);
                if(null!=mContext){
                    mContext.sendBroadcast(localIntent);
                    ToastUtils.showCenterToast("已保存至本地");
                }
            }

            @Override
            public void downloadError(String errorMessage) {
                clodeDownloadProgress();
                Toast.makeText(mContext,errorMessage,Toast.LENGTH_LONG).show();
            }
        }).execute(mFileNetPath);
    }


    /**
     * 显示下载\合并 进度条
     */
    private void showDownloadTips(String message){
        if(null!=mContext){
            if(null==mUploadProgressView){
                mUploadProgressView = new DownloadProgressDialog(mContext);
                mUploadProgressView.setOnDialogBackListener(new DownloadProgressDialog.OnDialogBackListener() {
                    @Override
                    public void onBack() {
                        ToastUtils.showCenterToast("请等待保存至相册完成");
                    }
                });
                mUploadProgressView.setMax(100);
            }
            mUploadProgressView.setProgress(0);
            mUploadProgressView.setTipsMessage(message);
            if(!mUploadProgressView.isShowing()){
                mUploadProgressView.show();
            }
        }
    }

    /**
     * 关闭下载\合并 进度条
     */
    private void clodeDownloadProgress(){
        if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
            mUploadProgressView.dismiss();
            mUploadProgressView=null;
        }
    }
}
