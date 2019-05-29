package com.yc.liaolive.start.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.start.model.bean.UpdataApkInfo;
import com.yc.liaolive.start.service.DownLoadService;
import com.yc.liaolive.start.ui.BuildManagerActivity;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;

import java.io.File;

/**
 * 版本检测
 * Created by yangxueqin on 2018/11/19.
 */

public class VersionCheckManager {

    private static final String TAG = "VersionCheckManager";

    private static VersionCheckManager manager;

    private UpdataApkInfo info;

    private String APK_NAME = "huayan.apk";

    private boolean isMainInit = false;
    private boolean showUpdataView = false;

    public static VersionCheckManager getInstance () {
        if (manager == null) {
            manager = new VersionCheckManager();
        }
        return manager;
    }

    /**
     * 检测升级处理
     * @param info 升级信息
     * @param isClick 是否是个人中心手动点击升级检测
     */
    public void checkVersion (UpdataApkInfo info, boolean isClick) {
        if (info == null) {
            return;
        }
        this.info = info;
        if (info.getVersion_code() <= Utils.getVersionCode()) {
            return;
        }
        if (1 == info.getCompel_update()) { //强制升级，直接弹出下载弹窗，不处理自动下载
            startUpdateDialog(canDirectlyInstallAPK(info.getVersion()));
        } else if (isClick) {
            //手动点击版本检测，校验本地是否已经存在新的安装包
            startUpdateDialog(canDirectlyInstallAPK(info.getVersion()));
        } else if (0 == info.getWifi_auto_down() &&
                Utils.getNetworkType() == NetContants.NETWORK_STATE_WIFI) {
            //wifi下下载完成后提示升级
            wifiAutoUpdate();
        } else {
            startUpdateDialog(canDirectlyInstallAPK(info.getVersion()));
        }
    }

    /**
     * wifi 自动下载
     */
    private void wifiAutoUpdate() {
        if (canDirectlyInstallAPK(info.getVersion())) {
            startUpdateDialog(true);
            return;
        } else {
            //下载
            Intent service = new Intent(AppEngine.getApplication(), DownLoadService.class);
            service.putExtra("downloadurl", info.getDown_url());
            service.putExtra("isWifiAuto", true);
            AppEngine.getApplication().startService(service);
        }
    }

    public String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 是否 可以直接安装 apk 已经下载了
     *
     * @param newAppVersion 最新版本号，验证下载apk是否是最新版本；
     * @return
     */
    private boolean canDirectlyInstallAPK(String newAppVersion) {
        boolean needInstallApk = false;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), APK_NAME);
        if (file.exists()) {
            String apk_path = file.getAbsolutePath();// 下载apk绝对路径
            try {
                PackageManager pm = AppEngine.getApplication().getPackageManager();
                PackageInfo packageInfo =
                        pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);

                /** 得到包名 */
                String packageName = packageInfo.packageName;
                /** apk的版本名称 String */
                String versionName = packageInfo.versionName;
                /** apk的版本号码 int */
                int versionCode = packageInfo.versionCode;
                if (AppEngine.getApplication().getPackageName().equals(packageName) && // 包名相同
                        newAppVersion.equals(versionName)) {// 下载apk版本是最新版本
                    int verCode = Utils.getVersionCode();
                    if (verCode != -1 && versionCode > verCode) {// 下载apk版本高于目前版本，说明提示安装
                        needInstallApk = true;
                    } else { // 下载apk版本低于或等同于目前版本，说明已安装到最新，无需更新
                        deleteErrorFile(); //说明包已经过时 删除
                    }
                } else if (newAppVersion.equals(versionName)) { //包名不同，但是是最新包（更换了包名）
                    needInstallApk = true;
                } else {
                    deleteErrorFile(); //说明包已经过时 删除
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return needInstallApk;
    }

    public void deleteErrorFile() {
        File f = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), APK_NAME);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 跳转升级dialog
     * @param downloaded
     */
    public void startUpdateDialog(boolean downloaded) {
        info.setAlreadyDownload(downloaded);
        if (isMainInit) {
            BuildManagerActivity.start(info);
            showUpdataView = false;
        } else {
            ApplicationManager
                    .getInstance().getCacheExample().put("updata_apk_info", info, Constant.CACHE_TIME);
            showUpdataView = true;
        }
    }

    /**
     * 安装apk
     * @param context
     */
    public void installAPK(Context context) {
        if (null == context) {
            return;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APK_NAME);
        if(file.exists()){
            openFile(file, context);
        }else{
            ToastUtils.showCenterToast("下载失败，正前往应用市场更新");
            Uri uri = Uri.parse("market://details?id=" + AppEngine.getApplication().getPackageName());
            Intent intent0 = new Intent(Intent.ACTION_VIEW, uri);
            intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppEngine.getApplication().startActivity(intent0);
        }
    }

    /**
     *重点在这里
     */
    public void openFile(File var0, Context var1) {
        if(null==var0) return;
        Intent intent = new Intent();
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri uriForFile = FileProvider.getUriForFile(var1,
                    var1.getApplicationContext().getPackageName() + ".apkprovider", var0);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
            //            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        }else{
            intent.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }
        try {
            var1.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            ToastUtils.showCenterToast("没有找到打开此类文件的程序");
        }
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    public String getAPK_NAME() {
        return APK_NAME;
    }

    public void setMainInit(boolean mainInit) {
        isMainInit = mainInit;
    }

    public boolean isShowUpdataView() {
        return showUpdataView;
    }
}
