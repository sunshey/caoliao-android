package com.yc.liaolive.start.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.bean.BuildMessageInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.start.manager.VersionCheckManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TinyHung@Outlook.com
 * 2017/12/1
 * 版本更新文件下载及安装
 */

public class DownLoadService extends Service {

    private static final String TAG = "DownLoadService";
    private DownloadManager manager;
    private DownloadCompleteReceiver receiver;
    private String url;
    private boolean isDownload=false;
    private long mEnqueueID;
    private Timer mTimer;
    private Handler mHandler;
    private int mTotalSizeBytes=-1;
    private boolean isRegisterReceiver;//是否已注册广播
    private boolean isWifiAuto = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isDownload){
            BuildMessageInfo buildMessageInfo=new BuildMessageInfo();
            buildMessageInfo.setCmd(Constant.BUILD_DOWNLOADING);
            ApplicationManager.getInstance().observerUpdata(buildMessageInfo);
            VideoApplication.getInstance().setDownloadAPK(true);
            return Service.START_NOT_STICKY;
        }
        url = intent.getStringExtra("downloadurl");
        isWifiAuto = intent.getBooleanExtra("isWifiAuto", false);
        if(TextUtils.isEmpty(url)){
            url="http://a.tn990.com/app.apk";
        }
        try{
            initDownManager();
        }catch (RuntimeException e){
            BuildMessageInfo buildMessageInfoError=new BuildMessageInfo();
            //更新下载失败状态
            buildMessageInfoError.setCmd(Constant.BUILD_ERROR);
            VideoApplication.getInstance().setDownloadAPK(false);
            ApplicationManager.getInstance().observerUpdata(buildMessageInfoError);
            e.printStackTrace();
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent0 = new Intent(Intent.ACTION_VIEW, uri);
                intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent0);
            } catch (Exception ex) {
                ToastUtils.showCenterToast("下载失败");
            }
        }
        return Service.START_NOT_STICKY;
    }

    /**
     * 初始化下载
     */
    private void initDownManager() {
        VersionCheckManager.getInstance().deleteErrorFile();//删除重复文件，防止安装失败
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        down.setAllowedOverRoaming(false);
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
//        down.setMimeType(mimeString);
        down.setMimeType("application/vnd.android.package-archive");
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        down.setVisibleInDownloadsUi(true);
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                VersionCheckManager.getInstance().getAPK_NAME());
        down.setTitle(getResources().getString(R.string.app_name)+" 下载中...");
        //返回一个任务ID，用来获取任务进度
        mEnqueueID = manager.enqueue(down);
        //更新下载中状态
        BuildMessageInfo buildMessageInfo=new BuildMessageInfo();
        buildMessageInfo.setCmd(Constant.BUILD_START);
        VideoApplication.getInstance().setDownloadAPK(true);
        ApplicationManager.getInstance().observerUpdata(buildMessageInfo);
        isDownload=true;
        mHandler = new Handler();
        //开启定时任务查询下载状态
        startQueryProgress();
        if(!isRegisterReceiver) {
            isRegisterReceiver=true;
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    /**
     * 开始定时查询下载状态
     */
    private void startQueryProgress() {
        mTimer = new Timer();
        mTimer.schedule(queryDownloadTask,0,100);//开启定时任务
    }

    private void stopQueryProgress(){
        if(null!=mTimer) mTimer.cancel(); mTimer=null;
    }


    private TimerTask queryDownloadTask=new TimerTask() {
        @Override
        public void run() {
            queryProgress();
        }
    };

    /**
     * 查询下载进度
     */
    private void queryProgress() {
        if(null!=manager&&0!=mEnqueueID){
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(mEnqueueID);
            Cursor cursor = null;
            try {
                cursor = manager.query(query);
                if (cursor != null && cursor.moveToFirst()) {
                    //已经下载文件大小
                    final int bytesDownloadSoFarIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    //下载文件的总大小
                    if(-1==mTotalSizeBytes){
                        mTotalSizeBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    }
                    if(null!=mHandler){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                BuildMessageInfo buildMessageInfo=new BuildMessageInfo();
                                buildMessageInfo.setCmd(Constant.BUILD_DOWNLOAD);
                                buildMessageInfo.setTotalSize(mTotalSizeBytes);
                                buildMessageInfo.setDownloadSize(bytesDownloadSoFarIndex);
                                VideoApplication.getInstance().setDownloadAPK(true);
                                ApplicationManager.getInstance().observerUpdata(buildMessageInfo);
                            }
                        });
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * 监听的广播
     */
    private class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                isDownload=false;
                VideoApplication.getInstance().setDownloadAPK(false);
                BuildMessageInfo buildMessageInfo=new BuildMessageInfo();
                //更新下载完成状态
                buildMessageInfo.setCmd(Constant.BUILD_END);
                buildMessageInfo.setTotalSize(mTotalSizeBytes);
                buildMessageInfo.setDownloadSize(mTotalSizeBytes);
                ApplicationManager.getInstance().observerUpdata(buildMessageInfo);
                stopQueryProgress();
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if(manager.getUriForDownloadedFile(downId)!=null){
                    if (isWifiAuto) {
                        //wifi下自动下载，下载完成后弹出升级提示框
                        VersionCheckManager.getInstance().startUpdateDialog(true);
                    } else {
                        VersionCheckManager.getInstance().installAPK(context);
//                                VersionCheckManager.getInstance().getRealFilePath(context,manager.getUriForDownloadedFile(downId)));
                    }

                }else{
                    BuildMessageInfo buildMessageInfoError=new BuildMessageInfo();
                    //更新下载失败状态
                    buildMessageInfoError.setCmd(Constant.BUILD_ERROR);
                    ApplicationManager.getInstance().observerUpdata(buildMessageInfoError);
                    ToastUtils.showCenterToast("下载失败");
                }
                DownLoadService.this.stopSelf();
            }
        }

    }

    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            f.delete();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isRegisterReceiver&&null!=receiver){
            isRegisterReceiver=false;
            try {
                unregisterReceiver(receiver);
            }catch (RuntimeException e){
            }
        }
        stopQueryProgress();
        if(null!=mHandler) mHandler.removeMessages(0);
        mTotalSizeBytes=-1;
    }
}