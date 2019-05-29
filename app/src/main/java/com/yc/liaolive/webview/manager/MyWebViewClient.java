package com.yc.liaolive.webview.manager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.user.manager.BindMobileManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.webview.IWebPageView;
import com.yc.liaolive.webview.ui.WebViewActivity;
import com.yc.liaolive.webview.util.WebviewUtil;

import java.util.HashMap;

import rx.functions.Action1;

import static com.yc.liaolive.common.CaoliaoController.URI_TYPE;


/**
 * Created by jingbin on 2016/11/17.
 * 监听网页链接:
 * - 优酷视频直接跳到自带浏览器
 * - 根据标识:打电话、发短信、发邮件
 * - 进度条的显示
 * - 添加javascript监听
 */
public class MyWebViewClient extends WebViewClient {

    private IWebPageView    mIWebPageView;
    private WebViewActivity mActivity;
    private CookieManager   cookieManager;
    private String mStartUrl;

    public MyWebViewClient(IWebPageView mIWebPageView) {
        this.mIWebPageView = mIWebPageView;
        mActivity = (WebViewActivity) mIWebPageView;
        CookieSyncManager.createInstance(AppEngine.getApplication());
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

    }

    @Override public void onPageStarted(WebView view, final String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mStartUrl = url;
        //页面开始加载时写入cookie
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebviewUtil.getInstance()
                        .setCookie(cookieManager, url, AppEngine.getApplication());
            }
        }).start();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(!TextUtils.isEmpty(mStartUrl)&&mStartUrl.equals(url)){
            return loadUrl(view,url);
        }else{
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * @param view
     * @param url
     * @return
     */
    private boolean loadUrl(WebView view,String url) {
        // 优酷视频跳转浏览器播放
        if (url.startsWith("http://v.youku.com/")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.BROWSABLE");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            mActivity.startActivity(intent);
            return true;
            // 电话、短信、邮箱
        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        if (isOtherSchema(url)) {
            mIWebPageView.stopLoading();
            if (url.startsWith("file://")) {// 加载本地文件
                mIWebPageView.loadUrl(url);
            } else if (url.startsWith("caoliao://")) {// 加载caoliao协议
                startCaoliaoUrl(url);
            } else {
                jumpToOtherApps(url);
            }
            return true;
        }
        mIWebPageView.startProgress();
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mActivity.mProgress90) {
            mIWebPageView.hindProgressBar();
        } else {
            mActivity.mPageFinish = true;
        }
        if(Utils.isCheckNetwork(mActivity)){
            mIWebPageView.hindProgressBar();
        }
        // html加载完成之后，添加监听图片的点击js函数
        mIWebPageView.addImageClickListener();
        String title = view.getTitle();
        if (!TextUtils.isEmpty(title)) {
            mIWebPageView.setTitle(title);
        }
        super.onPageFinished(view, url);

    }

    // 视频全屏播放按返回页面被放大的问题
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (newScale - oldScale > 7) {
            view.setInitialScale((int) (oldScale / newScale * 100)); //异常放大，缩回去。
        }
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
            mActivity.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showCenterToast("跳转失败，请刷新后重试");
        }
    }

    /**
     * 跳转native页面
     * @param url
     */
    private void startCaoliaoUrl (String url) {
        HashMap hashMap =  CaoliaoController.parseUri(url);
        if ("4".equals(hashMap.get(URI_TYPE))) {
            BindMobileManager.getInstance().goBingMobile(false)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                mIWebPageView.reload();
                            }
                        }
                    });
        } else {
            CaoliaoController.start(url);
        }

    }
}
