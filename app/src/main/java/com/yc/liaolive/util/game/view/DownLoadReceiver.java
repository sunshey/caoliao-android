package com.yc.liaolive.util.game.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.File;

public class DownLoadReceiver extends BroadcastReceiver {
    long mTaskId;
    String apkName = "";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        SharedPreferences sPreferences = context.getSharedPreferences( "xw", 0);
        mTaskId = sPreferences.getLong("taskid", 0);
        apkName = sPreferences.getString("apkname", "");
        if(myDwonloadID!= -1){
            mTaskId = myDwonloadID;
        }
        if (mTaskId == myDwonloadID) {
            checkDownloadStatus(context);
        }
    }

    //下载到本地后执行安装
    protected void installAPK(Context context, File file) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && sdkVersion >= 24) {
            uri = FileProvider.getUriForFile(context, mContext.getApplicationContext().getPackageName() + ".fileProvider", file);//newfile
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            uri = Uri.parse("file://" + file.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释

        context.startActivity(intent);
    }

    DownloadManager downloadManager;

    //检查下载状态
    private void checkDownloadStatus(Context context) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            apkName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.i("DownLoadService", ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.i("DownLoadService", ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.i("DownLoadService", ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i("DownLoadService", ">>>下载完成");
                    //下载完成安装APK
//                  String downloadPath = Environment.getExternalStor+ageDirectory() + "/51xianwan/" + apkName;
                    String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "51xianwan" + File.separator + apkName;

                    installAPK(context, new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.i("DownLoadService", ">>>下载失败");
                    break;
            }
        }
    }

}
