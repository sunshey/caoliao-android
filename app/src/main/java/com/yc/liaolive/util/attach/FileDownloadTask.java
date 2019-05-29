package com.yc.liaolive.util.attach;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TinyHung@Outlook.com
 * 2017/12/6.
 * 通用文件下载器
 */

public class FileDownloadTask extends AsyncTask<String,Integer,File> {

    public static final int FILE_TYPE_DEFAULT =0;
    public static final int FILE_TYPE_VIDEO =1;
    private int mDownloadFileType=FILE_TYPE_DEFAULT;
    private  String OUT_PATH = Constant.IMAGE_PATH;
    private boolean isDownload=true;//是否继续下载
    private int laterate = 0;//当前已读字节

    public interface OnDownloadListener{
        void downloadStart();
        void downloadProgress(int progress);
        void downloadFinlish(File file);
        void downloadError(String errorMessage);
    }
    private OnDownloadListener mOnDownloadListener;

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
    }


    public FileDownloadTask(int downloadFileType,String fileOutPath,OnDownloadListener onDownloadListener){
        if(!TextUtils.isEmpty(fileOutPath)){
            OUT_PATH=fileOutPath;
        }
        this.mOnDownloadListener=onDownloadListener;
        this.mDownloadFileType=downloadFileType;
    }

    public void stopDownload(boolean flag){
        this.isDownload=flag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(null!=mOnDownloadListener){
            mOnDownloadListener.downloadStart();
        }
    }

    @Override
    protected File doInBackground(String... params) {
        if(null==params) return null;
        if(params.length<=0)return null;

        File file=new File(OUT_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        String name= FileUtils.getFileName(params[0]);
        //下载的是视频文件，在这里将要下载的文件名称命名为带.mp4后缀的文件名称，方便创建添加水印任务
        if(mDownloadFileType==FILE_TYPE_VIDEO){
            if(!name.endsWith(".mp4")||name.endsWith(".MP4")){
                name=name+".mp4";
            }
        }
        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(900000);
            conn.setConnectTimeout(900000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200){
                int length = conn.getContentLength();
                int count = 0;
                File outPutPath = new File(file.getAbsolutePath());
                if (!outPutPath.exists()) {
                    outPutPath.mkdirs();
                }
                File apkDownloadPath = new File(outPutPath, name);
                InputStream in = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(apkDownloadPath);
                byte[] buffer = new byte[1024];
                do {
                    int numread = in.read(buffer);
                    count += numread;
                    int progress = (int) (((float) count / length) * 100);// 得到当前进度
                    if (progress >= laterate + 1) {// 只有当前进度比上一次进度大于等于1，才可以更新进度
                        laterate = progress;
                        this.publishProgress(progress);
                    }
                    if (numread <= 0) {//下载完毕
                        break;
                    }
                    os.write(buffer, 0, numread);
                } while (isDownload);
                in.close();
                os.close();
                return apkDownloadPath;
            }else{
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.toString();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(null!=mOnDownloadListener){
            mOnDownloadListener.downloadProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if(null!=file&&file.exists()&&file.isFile()){
            if(null!=mOnDownloadListener){
                mOnDownloadListener.downloadFinlish(file);
            }
        }else{
            if(null!=mOnDownloadListener){
                mOnDownloadListener.downloadError("下载失败");
            }
        }
    }
}
