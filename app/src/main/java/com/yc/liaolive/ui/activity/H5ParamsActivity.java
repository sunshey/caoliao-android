package com.yc.liaolive.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.SharedPreferencesUtil;
import com.yc.liaolive.webview.ui.WebViewActivity;

/**
 * TinyHung@Outlook.com
 * 2017/12/1.
 * 接收网页参数跳转至视频播放详情界面
 */

public class H5ParamsActivity extends AppCompatActivity {

    private static final String TAG = "H5ParamsActivity";

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (null == intent || null == intent.getData() || null == intent.getData().getHost()){
            finish();
            return;
        }
        Uri uri = intent.getData();
        try {
            if("huayanzhima".equals(uri.getHost())){
                try {
                    String url = uri.toString();
                    StringBuilder stringBuilder=new StringBuilder(Constant.CONTENT_AGREEMENT_AUTHENTI);
                    stringBuilder.append("?");
                    String result = url.substring(stringBuilder.toString().length(),url.length());
                    if(null!= WebViewActivity.get()){
                        WebViewActivity.get().finish();
                    }
                    SharedPreferencesUtil.getInstance().putString(Constant.SP_ZHIMA_AUTHENTI_RESULT,result);
                    finish();
                }catch (RuntimeException e){
                    finish();
                }
            }else{
                CaoliaoController.start(uri.toString());
                finish();
            }
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}