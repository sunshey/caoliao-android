package com.yc.liaolive.recharge.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.yc.liaolive.R;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2018/9/10
 * 网页支付
 */

public class WebPayView extends FrameLayout{

    private static final String TAG = "PayWebView";
    private WebView mWebView;
    private String mUrl;
    private String olderSn;
    private String auth_domain;
    private ProgressBar mProgressBar;
    private int mProgress=0;//当前已位移进度
    private int mToMaxProgress=0;//位移进度目标最大值
    private int mMaxProgress=100;//进度条最大值


    public WebPayView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public WebPayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,null);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_pay_web_view,this);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setVisibility(VISIBLE);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
        initWebView();
    }

    /**
     * 进度条 假装加载到maxProgress
     * @param maxProgress
     */
    public void startProgressToMax(int maxProgress) {
        if(null!=mProgressBar){
            //如果是直接到达最大数
            if(maxProgress>=mMaxProgress){
                mProgressBar.removeCallbacks(progressRunnable);
                mProgressBar.setProgress(maxProgress);
                mProgressBar.setVisibility(View.INVISIBLE);
                mToMaxProgress=maxProgress;
                return;
            }
            //缓慢加载至期望进度
            mProgressBar.setVisibility(View.VISIBLE);
            mProgress=0;
            mToMaxProgress=maxProgress;
            mProgressBar.postDelayed(progressRunnable,90);
        }
    }

    /**
     * 时间到 移除自己
     */
    private Runnable progressRunnable=new Runnable() {
        @Override
        public void run() {
            if(null!=mProgressBar){
                mProgress+=5;
                mProgressBar.setProgress(mProgress);
                if(mProgress>=mMaxProgress){
                    mProgressBar.setProgress(100);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mProgressBar.removeCallbacks(progressRunnable);
                    return;
                }
                if(mProgress<mToMaxProgress){
                    mProgressBar.postDelayed(progressRunnable,90);
                }
            }
        }
    };

    /**
     * 初始化
     */
    private void initWebView() {
        if(null==mWebView) return;
        if(null!=mProgressBar) mProgressBar.setVisibility(View.VISIBLE);
        WebSettings ws =  mWebView.getSettings();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(false);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(false);
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
        mWebView.setInitialScale(1);
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
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith("weixin://")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.weXinPay(url);
                    WebPayView.this.setTag(olderSn);
                } else if (url.toLowerCase().contains("platformapi/startapp")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.aliPay(url);
                    WebPayView.this.setTag(olderSn);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains("platformapi/startapp")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.aliPay(url);
                    WebPayView.this.setTag(olderSn);
                    return true;
                } else if (url.startsWith("weixin://")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.weXinPay(url);
                    WebPayView.this.setTag(olderSn);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                startProgressToMax(100);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtils.showCenterToast(description);
                reset();
            }
        });
    }

    /**
     * 开始调用支付
     * @param olderSn 订单号
     * @param payurl 支付协议地址
     * @param auth_domain 微信H5支付授权域名
     */
    public void starPlay(String olderSn,String payurl, String auth_domain) {
        startProgressToMax(90);
        this.mUrl = payurl;
        this.olderSn = olderSn;
        this.auth_domain = auth_domain;
        if (!TextUtils.isEmpty(auth_domain)) {
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Referer", auth_domain);
            if(null!=mWebView) mWebView.loadUrl(payurl, extraHeaders);
        } else {
            if(null!=mWebView) mWebView.loadUrl(payurl);
        }
    }

    public void setOlderSn(String olderSn){
        this.olderSn=olderSn;
        WebPayView.this.setTag(olderSn);
    }

    public String getOlderSn(){
        return ((String) WebPayView.this.getTag());
    }

    public void reset(){
        WebPayView.this.setTag(null);
        if(null!=mWebView) {
            mWebView.loadUrl("about:blank");
            mWebView.setVisibility(GONE);
        }
    }

    public boolean backPress(){
        if(null!=mWebView&&mWebView.canGoBack()){
             mWebView.goBack();
             return true;
        }
        reset();
        return false;
    }

    public void onDestroy(){
        if(null!=mWebView){
            WebPayView.this.setTag(null);
            mWebView.setVisibility(GONE);
            mWebView.removeAllViews();
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView=null;
        }
        mUrl=null;olderSn=null;
    }

    public void showWebView() {
        if(null!=mWebView){
            mWebView.setVisibility(VISIBLE);
        }
    }

    public interface OnFunctionListener{
        void weXinPay(String url);
        void aliPay(String url);
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
