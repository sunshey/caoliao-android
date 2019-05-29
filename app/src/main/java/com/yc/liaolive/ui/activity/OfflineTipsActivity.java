package com.yc.liaolive.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.yc.liaolive.R;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.live.manager.OfflineManager;

/**
 * TinyHung@outlook.com
 * 2017/5/28
 * 互踢、下线处理
 */

public class OfflineTipsActivity extends TopBaseActivity implements View.OnClickListener {

    public static final int ACTION_RESET_LOGIN=1;//强制重新登录
    public static final int ACTION_CLEAN_LOGIN=2;//取消登录
    private int action=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        setFinishOnTouchOutside(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//需要添加的语句
        initLayoutParams();
        findViewById(R.id.tv_submit).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_submit:
                action=ACTION_RESET_LOGIN;
                break;
            case R.id.tv_cancel:
                action=ACTION_CLEAN_LOGIN;
                break;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //禁用返回键
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OfflineManager.getInstance().getOfflineSubject().onNext(action);
        OfflineManager.getInstance().getOfflineSubject().onCompleted();
    }
}