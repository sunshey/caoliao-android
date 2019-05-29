package com.yc.liaolive.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2019/2/27
 */

public class ApkManager {

    private static final String TAG = "ApkManager";
    private static ApkManager mInstance;

    /**
     * 单例初始化
     *
     * @return
     */
    public static synchronized ApkManager getInstance() {
        synchronized (ApkManager.class) {
            if (null == mInstance) {
                mInstance = new ApkManager();
            }
        }
        return mInstance;
    }

    /**
     * 检测设备有没有安装此应用
     * @param context
     * @param packageName
     * @return
     */
    public boolean isInstalledApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed ;
        try {
            pm.getPackageInfo(packageName, 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }
}
