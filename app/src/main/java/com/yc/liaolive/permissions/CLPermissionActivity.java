package com.yc.liaolive.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

/**
 * 处理权限的activity
 * Created by yangxueqin on 18/12/14.
 */
@TargetApi(Build.VERSION_CODES.M)
public class CLPermissionActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String[] permissions = intent.getStringArrayExtra("permissions");
        if (intent.getBooleanExtra("isFullScreen", false)) {
            setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        }
        requestPermissions(permissions, 42);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    shouldShowRequestPermissionRationale[i] =
                            shouldShowRequestPermissionRationale(permissions[i]);
                } else {
                    shouldShowRequestPermissionRationale[i] = true;
                }
            }
            RXPermissionManager.getInstance(this)
                    .onRequestPermissionsResult(requestCode, permissions, grantResults,
                            shouldShowRequestPermissionRationale);
            finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}