package com.yc.liaolive.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2019/3/1
 */

public class TextActivity extends TopBaseActivity{

    private static final String TAG = "TextActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requstPermissions();
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
    }
}
