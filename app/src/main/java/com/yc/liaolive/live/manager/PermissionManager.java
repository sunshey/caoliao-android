package com.yc.liaolive.live.manager;

import android.content.Intent;

/**
 * TinyHung@Outlook.com
 * 2019/3/1
 * 一个简单的权限申请类
 */

public class PermissionManager {

    private static PermissionManager mInstance;

    public static synchronized PermissionManager getInstance() {
        synchronized (PermissionManager.class) {
            if (null == mInstance) {
                mInstance = new PermissionManager();
            }
        }
        return mInstance;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
