package com.yc.liaolive.webview.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CommentTitleView;
import com.yc.liaolive.webview.IWebPageView;
import com.yc.liaolive.webview.manager.CLJavascriptInterface;
import com.yc.liaolive.webview.manager.FullscreenHolder;
import com.yc.liaolive.webview.manager.MyWebChromeClient;
import com.yc.liaolive.webview.manager.MyWebViewClient;
import com.yc.loanbox.view.AndroidInterface;
import com.yc.loanboxsdk.LoanboxSDK;
import java.util.Map;

/**
 * 网页可以处理:
 * 点击相应控件:拨打电话、发送短信、发送邮件、上传图片、播放视频
 * 进度条、返回网页上一层、显示网页标题
 */

public class WebViewActivity extends AppCompatActivity implements IWebPageView, CLJavascriptInterface.OnJsListener {

    private static WebViewActivity mInstance;
    private static final String TAG = "WebViewActivity";
    private SwipeRefreshLayout refreshLayout;
    // 进度条是否加载到90%
    public boolean mProgress90;
    // 网页是否加载完成
    public boolean mPageFinish;
    // 加载视频相关
    private MyWebChromeClient mWebChromeClient;
    // title
    private String mTitle;
    // 网页链接
    private String mUrl;
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private FrameLayout mVideoFullView;
    private CommentTitleView titleView;

    private int mProgress=0;//当前已位移进度
    private int mToMaxProgress=0;//位移进度目标最大值
    private int mMaxProgress=100;//进度条最大值

    public static WebViewActivity get() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initViews();
        initWebView();
        if(TextUtils.isEmpty(mUrl)){
            ToastUtils.showCenterToast("网址错误！");
            finish();
            return;
        }
        mInstance=this;
        MobclickAgent.onEvent(WebViewActivity.this,Utils.formatHostUrl(mUrl));
        Map<String,String> paramsMap = Utils.getParamsExtra(mUrl);
        if(!TextUtils.isEmpty(paramsMap.get("agent_id"))){
            LoanboxSDK loanboxSDK = LoanboxSDK.defaultLoanboxSDK();
            loanboxSDK.setChannelId(paramsMap.get("agent_id"));
            MobclickAgent.onEvent(WebViewActivity.this,"channel_id_"+paramsMap.get("agent_id"));
            loanboxSDK.init(WebViewActivity.this);
        }
        startProgressToMax(90);
        //post路由日志
        LogApi simpli=new LogApi();
        simpli.setRequstTime(System.currentTimeMillis());
        String host = Utils.formatHostUrl(mUrl);
        simpli.setRequstUrl(host);
        ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
        actionLogInfo.setData(simpli);
        UserManager.getInstance().postHostState(NetContants.POST_ACTION_TYPE_WEB_HOST,actionLogInfo,null);
        MobclickAgent.onEvent(this,host);
        mWebView.loadUrl(mUrl);
    }

    private void initViews() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        mUrl = intent.getStringExtra("url");
        boolean titleForbidden = intent.getBooleanExtra("titleForbidden", false);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
        mWebView = (WebView) findViewById(R.id.webview_detail);
        mVideoFullView = (FrameLayout) findViewById(R.id.video_fullView);
        titleView = (CommentTitleView) findViewById(R.id.title_view);
        titleView.showTitle(titleForbidden?View.GONE:View.VISIBLE);
        setTitle(mTitle);
        titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                onBackPressed();
            }
        });
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });
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
        if(null!=mProgressBar) mProgressBar.setVisibility(View.VISIBLE);
        WebSettings ws =  mWebView.getSettings();
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

        mWebChromeClient = new MyWebChromeClient(this);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(new MyWebViewClient(this));
        // 与js交互
        ThreadLocal<CLJavascriptInterface> mJavascriptInterface = new ThreadLocal<>();
        CLJavascriptInterface clJavascriptInterface = new CLJavascriptInterface();
        clJavascriptInterface.setOnJsListener(this);
        mWebView.addJavascriptInterface(clJavascriptInterface, "injectedObject");
        mWebView.addJavascriptInterface(new AndroidInterface(WebViewActivity.this, ""), "android");
    }

    @Override
    public void hindProgressBar() {
        startProgressToMax(100);
    }

    @Override
    public void startProgress() {
        startProgressToMax(90);
    }

    @Override
    public void showWebView() {
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindWebView() {
        mWebView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void fullViewAddView(View view) {
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        mVideoFullView = new FullscreenHolder(WebViewActivity.this);
        mVideoFullView.addView(view);
        decor.addView(mVideoFullView);
    }

    @Override
    public void showVideoFullView() {
        mVideoFullView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindVideoFullView() {
        mVideoFullView.setVisibility(View.GONE);
    }

    @Override public void stopLoading() {
        mWebView.stopLoading();
    }

    @Override public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void reload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    @Override
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            titleView.setTitle(title);
        }
    }

    @Override
    public void progressChanged(int newProgress) {
        if (mProgress90) {
            int progress = newProgress * 100;
            if (progress > 900) {
                if(null!=mProgressBar) mProgressBar.setProgress(progress);
                if (progress == 1000) {
                    if(null!=mProgressBar) mProgressBar.setVisibility(View.GONE);
                }
            }
        }
        if (newProgress > 90) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                //  "objs[i].onclick=function(){alert(this.getAttribute(\"has_link\"));}" +
                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"has_link\"));}" +
                "}" +
                "})()");

        // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        mWebView.loadUrl("javascript:(function(){" +
                "var objs =document.getElementsByTagName(\"a\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){" +
                "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                "}" +
                "})()");
    }

    /**
     * 进度条 假装加载到90%
     */
    public void startProgress90() {
        for (int i = 0; i < 900; i++) {
            final int progress = i + 1;
            mProgressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(null!=mProgressBar) mProgressBar.setProgress(progress);
                    if (progress == 900) {
                        mProgress90 = true;
                        if (mPageFinish) {
                            startProgress90to100();
                        }
                    }
                }
            }, (i + 1) * 2);
        }
    }

    /**
     * 进度条 加载到100%
     */
    public void startProgress90to100() {
        for (int i = 900; i <= 1000; i++) {
            final int progress = i + 1;
            if(null!=mProgressBar) mProgressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                   if(null!=mProgressBar) mProgressBar.setProgress(progress);
                    if (progress == 1000) {
                        if(null!=mProgressBar) mProgressBar.setVisibility(View.GONE);
                    }
                }
            }, (i + 1) * 2);
        }
    }


    public FrameLayout getVideoFullView() {
        return mVideoFullView;
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 上传图片之后的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE) {
            mWebChromeClient.mUploadMessage(intent, resultCode);
        } else if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient.mUploadMessageForAndroid5(intent, resultCode);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        mWebView.onResume();
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        mWebView.resumeTimers();
        // 设置为横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshLayout.setRefreshing(false);
        mVideoFullView.removeAllViews();
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
        }

        mWebChromeClient=null;mVideoFullView=null;mProgressBar=null;mWebView=null;
        Runtime.getRuntime().gc();
    }

    /**
     * 打开网页:
     * @param mContext 上下文
     * @param mUrl     要加载的网页url
     * @param mTitle   title
     */
    public static void loadUrl(Context mContext, String mUrl, String mTitle) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("url",mUrl);
        intent.putExtra("title",mTitle);
        mContext.startActivity(intent);
    }

    /**
     * 打开网页:
     * @param mContext 上下文
     * @param mUrl     要加载的网页url
     * @param mTitle   title
     * @param titleForbidden   标题栏是否被禁用
     */
    public static void loadUrl(Context mContext, String mUrl, String mTitle,boolean titleForbidden ) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("url",mUrl);
        intent.putExtra("title",mTitle);
        intent.putExtra("titleForbidden",titleForbidden);
        mContext.startActivity(intent);
    }

    /**
     * Js调用app方法处理
     * @param eventName
     * @param data
     */
    @Override public void setJsContent(String eventName, String data) {
        if (eventName.equals("closeWebview")) {
            this.finish();
        } else if (eventName.equals("refresh")) {
            refreshLayout.setEnabled(data.equals("1"));
        }
    }

    /**
     * 拦截返回和菜单事件
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
        if(TextUtils.isEmpty(mUrl)){
            if(null!=mWebView) mWebView.loadUrl("about:blank");
            finish();
            return;
        }
//        if(AppManager.getInstance().existBlackList(mUrl)){
//            if(null!=mWebView) mWebView.loadUrl("about:blank");
//            finish();
//            return;
//        }
        //全屏播放退出全屏
        if (mWebChromeClient.inCustomView()) {
            hideCustomView();
            return;
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        if(null!=mWebView) mWebView.loadUrl("about:blank");
        finish();
    }
}