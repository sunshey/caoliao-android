package com.yc.liaolive.util.attach;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.ui.dialog.DownloadProgressDialog;
import com.yc.liaolive.util.FileUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import java.io.File;

/**
 * TinyHung@Outlook.com
 * 2017/12/9
 * 这个类负责下载整套流程，检查缓存，复制，下载，添加水印
 */

public class VideoDownloadComposrTask {

    private DownloadProgressDialog mUploadProgressView;
    private final Activity mContext;
    private final String mFileNetPath;

    public VideoDownloadComposrTask(Activity context, String fileNetPath) {
        if(context instanceof Activity){
            this.mContext=context;
            this.mFileNetPath=fileNetPath;
        }else {
            throw new IllegalStateException("Error! You must preset so Activity Context!");
        }
    }

    public void start() {
        File file=new File(Constant.DOWNLOAD_PATH);//XinQu/File/Download/目录下
        if(!file.exists()){
            file.mkdirs();
        }
        createDownload();
    }
    /**
     * 下载之前的准备工作
     */
    private void createDownload() {
        //文件地址可能不是.mp4结尾的，需要判断下 rexVideoPath(name);
        File outPath=new File(Constant.DOWNLOAD_PATH, Utils.rexVideoPath(Utils.getFileName(mFileNetPath)));
        //先判断下载目录中是否存在该视频
        if(outPath.exists()&&outPath.isFile()){
            if(null!=mContext) ToastUtils.showFinlishToast((AppCompatActivity)mContext,null,null,"已保存至本地:"+Constant.DOWNLOAD_WATERMARK_VIDEO_PATH);
            closeDownloadProgress();
        }else{
            //开始下载
            startDownloadTask();
        }
    }

    /**
     * 开始复制文件到目标文件夹下
     * @param resouceFilePath 源文件的绝对地址
     * @param outFilePath 要复制到所在目录的相对路径
     */

    private void startCopyFile(String resouceFilePath, String outFilePath) {
        if(TextUtils.isEmpty(resouceFilePath))return;
        if(TextUtils.isEmpty(outFilePath))return;
        File outPath=new File(outFilePath);
        if(!outPath.exists()){
            outPath.mkdirs();
        }

        File fileOutPath=new File(outFilePath,Utils.getFileName(mFileNetPath));
        new CopyFileTask(fileOutPath.getAbsolutePath()).execute(resouceFilePath);
    }

    /**
     * 复制文件
     */
    private class CopyFileTask extends AsyncTask<String,Void,Boolean> {

        private final String mOutFilePath;

        public CopyFileTask(String outFilePath) {
            this.mOutFilePath=outFilePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDownloadTips("保存至本地中..");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if(null!=params&&params.length>0){
                return FileUtils.copyFile(params[0], mOutFilePath);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(null!=aBoolean&&aBoolean){
                File file=new File(mOutFilePath);
                if(file.exists()&&file.isFile()){
                    //复制完成，合成水印
                }else{
                    startDownloadTask();
                }
            }else{
                startDownloadTask();
            }
        }
    }

    /**
     * 开始下载
     */
    private void startDownloadTask() {

        File file=new File(Constant.DOWNLOAD_PATH,Utils.getFileName(mFileNetPath));
        if(null!=file&&file.exists()){
            FileUtils.deleteFile(file);
        }
        //下载视频类型文件
        new FileDownloadTask(FileDownloadTask.FILE_TYPE_VIDEO,Constant.DOWNLOAD_PATH, new FileDownloadTask.OnDownloadListener() {
            @Override
            public void downloadStart() {

                showDownloadTips("视频下载中...");
            }

            @Override
            public void downloadProgress(int progress) {
                if(null!=mUploadProgressView&&mUploadProgressView.isShowing()){
                    mUploadProgressView.setProgress(progress);
                }
            }

            @Override
            public void downloadFinlish(File file) {
                closeDownloadProgress();
                Uri localUri = Uri.parse("file://"+ file);
                Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                localIntent.setData(localUri);
                mContext.sendBroadcast(localIntent);
                if(null!=mContext) ToastUtils.showFinlishToast((AppCompatActivity)mContext,null,null,"已保存至本地:"+Constant.DOWNLOAD_PATH);
            }

            @Override
            public void downloadError(String errorMessage) {
                closeDownloadProgress();
                ToastUtils.showCenterToast(errorMessage);
            }
        }).execute(mFileNetPath);
    }

    /**
     * 显示下载\合并 进度条
     */
    private void showDownloadTips(String message){
        if(null!=mContext&&!mContext.isFinishing()){
            if(null==mUploadProgressView){
                mUploadProgressView = new DownloadProgressDialog(mContext);
                mUploadProgressView.setOnDialogBackListener(new DownloadProgressDialog.OnDialogBackListener() {
                    @Override
                    public void onBack() {
                        ToastUtils.showCenterToast("请等待保存至相册完成!");
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
    private void closeDownloadProgress(){
        if(null!=mContext&&!mContext.isFinishing()&&null!=mUploadProgressView&&mUploadProgressView.isShowing()){
            mUploadProgressView.dismiss();
            mUploadProgressView=null;
        }
    }
}
