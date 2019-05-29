package com.yc.liaolive.view.widget;

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

public class PayWebView  extends FrameLayout{

    private static final String TAG = "PayWebView";
    private WebView mWebView;
    private String mUrl;
    private String olderSn;
    private String auth_domain;

    public PayWebView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public PayWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,null);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_pay_web_view,this);
        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();
    }

    /**
     * 初始化
     */
    private void initWebView() {
        if(null==mWebView) return;
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
                    PayWebView.this.setTag(olderSn);
                } else if (url.toLowerCase().contains("platformapi/startapp")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.aliPay(url);
                    PayWebView.this.setTag(olderSn);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().contains("platformapi/startapp")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.aliPay(url);
                    PayWebView.this.setTag(olderSn);
                    return true;
                } else if (url.startsWith("weixin://")) {
                    if(null!=mOnFunctionListener) mOnFunctionListener.weXinPay(url);
                    PayWebView.this.setTag(olderSn);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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
        PayWebView.this.setTag(olderSn);
    }

    public String getOlderSn(){
        return ((String) PayWebView.this.getTag());
    }

    public void reset(){
        PayWebView.this.setTag(null);
        if(null!=mWebView) {
            mWebView.loadUrl("about:blank");
            mWebView.setVisibility(GONE);
        }
    }

    public void onDestroy(){
        if(null!=mWebView){
            PayWebView.this.setTag(null);
            mWebView.setVisibility(GONE);
            mWebView.removeAllViews();
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView=null;
        }
        mUrl=null;olderSn=null;
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
