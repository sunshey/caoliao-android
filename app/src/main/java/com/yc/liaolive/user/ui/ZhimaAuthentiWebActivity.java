package com.yc.liaolive.user.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityWebAuthentiBinding;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.webview.manager.CLJavascriptInterface;

/**
 * TinyHung@Outlook.com
 * 2019/1/26
 * 开始芝麻认证
 */

public class ZhimaAuthentiWebActivity extends BaseActivity<ActivityWebAuthentiBinding> implements CLJavascriptInterface.OnJsListener {

    private static final String TAG = "ZhimaAuthentiWebActivity";
    private int mProgress=0;//当前已位移进度
    private int mToMaxProgress=0;//位移进度目标最大值
    private int mMaxProgress=100;//进度条最大值
    private static ZhimaAuthentiWebActivity mInstance;

    public static void start(Context context, String url, String title) {
        Intent intent=new Intent(context,ZhimaAuthentiWebActivity.class);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static ZhimaAuthentiWebActivity get() {
        return mInstance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_authenti);
        String url = getIntent().getStringExtra("url");
        if(TextUtils.isEmpty(url)){
            ToastUtils.showCenterToast("请给定URL");
            finish();
        }
        mInstance=this;
        bindingView.titleView.setTitle(getIntent().getStringExtra("title"));
        startProgressToMax(90);
        initWebView();
        bindingView.webView.loadUrl(url);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void initData() {}

    /**
     * 初始化
     */
    private void initWebView() {
        WebSettings ws = bindingView.webView.getSettings();
        bindingView.webView.resumeTimers();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        if(Utils.isCheckNetwork()){
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else{
            ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        // 缩放比例 1
        bindingView.webView.setInitialScale(1);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        ws.setTextZoom(100);
        // 与js交互
        ThreadLocal<CLJavascriptInterface> mJavascriptInterface = new ThreadLocal<>();
        CLJavascriptInterface clJavascriptInterface = new CLJavascriptInterface();
        clJavascriptInterface.setOnJsListener(this);
        bindingView.webView.addJavascriptInterface(clJavascriptInterface, "injectedObject");

        // 与js交互
        bindingView.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
       
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url=request.getUrl().toString();
                //如果是电话、短信之类
                if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ignored) {
                    }
                    return true;
                }
                if (isOtherSchema(url)) {
                    bindingView.webView.stopLoading();
                    if (url.startsWith("file://")) {// 加载本地文件
                        bindingView.webView.loadUrl(url);
                    } else if (url.startsWith("huayan://")) {// 加载caoliao协议
                        if(url.contains("huayanzhima:result")){
                            try {
                                StringBuilder stringBuilder=new StringBuilder(Constant.CONTENT_AGREEMENT_AUTHENTI);
                                stringBuilder.append("?");
                                String result = url.substring(stringBuilder.toString().length(),url.length());
                                finish();
                                ZhimaAuthentiResultActivity.start(AppEngine.getApplication().getApplicationContext(),result);
                            }catch (RuntimeException e){

                            }
                        }
                    } else {
                        jumpToOtherApps(url);
                    }
                    return true;
                }
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(null!=view&&!TextUtils.isEmpty(view.getTitle())){
                    if(null!=bindingView) bindingView.titleView.setTitle(view.getTitle());
                }
                startProgressToMax(100);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });
    }

    /**
     * 判断是否是其他意图，
     *
     * @param url 跳转的url地址
     * @return 是否是其他schema头, 即判断url地址是不是http的.
     */
    private boolean isOtherSchema(String url) {
        Uri uri = Uri.parse(url);
        String schema = uri.getScheme();
        return schema != null && !schema.contains("http");
    }

    /**
     * 跳转其他第三方app
     *
     * @param jumpurl 跳转第三方的url地址
     */
    private void jumpToOtherApps(String jumpurl) {
        try {
            Uri uri = Uri.parse(jumpurl);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showCenterToast("跳转失败，请刷新后重试");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null!=bindingView){
            bindingView.webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=bindingView){
            bindingView.webView.onPause();
        }
    }

    /**
     * 进度条 假装加载到maxProgress
     * @param maxProgress
     */
    public void startProgressToMax(int maxProgress) {
        if(null!=bindingView){
            //如果是直接到达最大数
            if(maxProgress>=mMaxProgress){
                bindingView.pbProgress.removeCallbacks(progressRunnable);
                bindingView.pbProgress.setProgress(maxProgress);
                bindingView.pbProgress.setVisibility(View.INVISIBLE);
                mToMaxProgress=maxProgress;
                return;
            }
            //缓慢加载至期望进度
            bindingView.pbProgress.setVisibility(View.VISIBLE);
            mProgress=0;
            mToMaxProgress=maxProgress;
            bindingView.pbProgress.postDelayed(progressRunnable,90);
        }
    }

    /**
     * 时间到 移除自己
     */
    private Runnable progressRunnable=new Runnable() {
        @Override
        public void run() {
            mProgress+=5;
            bindingView.pbProgress.setProgress(mProgress);
            if(mProgress>=mMaxProgress){
                bindingView.pbProgress.setProgress(100);
                bindingView.pbProgress.setVisibility(View.INVISIBLE);
                bindingView.pbProgress.removeCallbacks(progressRunnable);
                return;
            }
            if(mProgress<mToMaxProgress){
                bindingView.pbProgress.postDelayed(progressRunnable,90);
            }
        }
    };


    /**
     * 拦截返回
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(null!=bindingView&&bindingView.webView.canGoBack()){
            bindingView.webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=bindingView){
            bindingView.pbProgress.removeCallbacks(progressRunnable);
            ViewGroup parent = (ViewGroup) bindingView.webView.getParent();
            if (parent != null) {
                parent.removeView(bindingView.webView);
            }
            bindingView.webView.loadUrl("about:blank");
            bindingView.webView.clearCache(true);
            bindingView.webView.clearHistory();
            bindingView.webView.removeAllViews();
            bindingView.webView.stopLoading();
            bindingView.webView.setWebChromeClient(null);
            bindingView.webView.setWebViewClient(null);
            bindingView.webView.destroy();
        }
        mInstance=null;
        Runtime.getRuntime().gc();
    }

    @Override
    public void setJsContent(String eventName, String data) {
        if (eventName.equals("closeWebview")) {
            this.finish();
        }
    }
}