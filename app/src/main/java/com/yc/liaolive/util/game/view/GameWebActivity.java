package com.yc.liaolive.util.game.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.ActionLogInfo;
import com.yc.liaolive.bean.LogApi;
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityGameWebBinding;
import com.yc.liaolive.manager.ApkManager;
import com.yc.liaolive.start.manager.AppManager;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.util.game.SystemUtil;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.io.File;

/**
 * 闪玩 跳转组件6
 */
public class GameWebActivity extends BaseActivity<ActivityGameWebBinding> {

    private static final String TAG = "GameWebActivity";
    private static final int REQUEST_PHONE_STATE = 100;
    private static final int REQUEST_INSTALL_STATE = 101;
    private static final int REQUEST_WRITE_STORAGE = 102;
    private static final int REQUEST_INSTALL_UNKNOW_SOURCE = 1001;
    private String downloadPath;
    private String apkName;
    private boolean isFirst = true;
    private Context mContext;
    private String mGameUrl;
    private WebView mWebView;

    @Override
    public void initViews() {
        mWebView = (WebView) findViewById(R.id.webview);
        String title = getIntent().getStringExtra("title");
        if(!TextUtils.isEmpty(title)){
            bindingView.titleView.setTitle(title);
        }
        bindingView.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新刷新页面
                bindingView.webview.loadUrl(  bindingView.webview.getUrl());
            }
        });
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                if (bindingView.webview.canGoBack()) {
                    bindingView.webview.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void initData() {}

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen(true);
        super.onCreate(savedInstanceState);
        mGameUrl = getIntent().getStringExtra("url");
        if(TextUtils.isEmpty(mGameUrl)){
            Toast.makeText(this,"参数错误",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_game_web);//MResource.getIdByName(getApplication(), "layout", "activity_xw_ad_list")
        mContext = this;
        //post路由日志
        LogApi simpli=new LogApi();
        simpli.setRequstTime(System.currentTimeMillis());
        simpli.setRequstUrl(Utils.formatHostUrl(mGameUrl));
        ActionLogInfo<LogApi> actionLogInfo=new ActionLogInfo();
        actionLogInfo.setData(simpli);
        UserManager.getInstance().postHostState(NetContants.POST_ACTION_TYPE_WEB_HOST,actionLogInfo,null);

        initWebView();
        openUrl(mGameUrl);
        onCallPermission();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = bindingView.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setDomStorageEnabled(true);  //开启DOM Storage功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        bindingView.webview.addJavascriptInterface(GameWebActivity.this, "android");
    }

    private void openUrl(String gameUrl) {
        WebChromeClient webchromeclient = new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                Toast.makeText(GameWebActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();

                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if(null!=bindingView) bindingView.titleView.setTitle(title);//闲玩
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //隐藏进度条
                    bindingView.swipeContainer.setRefreshing(false);
                } else {
                    if (!bindingView.swipeContainer.isRefreshing())
                        bindingView.swipeContainer.setRefreshing(true);
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        bindingView.webview.setWebChromeClient(webchromeclient);
        bindingView.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Build.VERSION.SDK_INT < 26) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(null!=bindingView) bindingView.titleView.setTitle(view.getTitle());//闲玩
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        });
        bindingView.webview.loadUrl(gameUrl);//"https://h5.51xianwan.com/try/try_list_plus.aspx?ptype=2" + "&deviceid=" + imei + "&appid=" + appid + "&appsign=" + appsign + "&keycode=" + keycode

    }

    // 定义JS需要调用的方法 被JS调用的方法必须加入@JavascriptInterface注解
    /**
     * 检测是否安装APP
     * @param packageName 包名
     */
    @JavascriptInterface
    public void CheckInstall(String packageName) {
        boolean installedApp = ApkManager.getInstance().isInstalledApp(GameWebActivity.this, packageName);
        if (installedApp) {
            bindingView.webview.post(new Runnable() {
                @Override
                public void run() {
                    bindingView.webview.loadUrl("javascript:CheckInstall_Return(1)");
                }
            });
        } else {
            bindingView.webview.post(new Runnable() {
                @Override
                public void run() {
                    bindingView.webview.loadUrl("javascript:CheckInstall_Return(0)");
                }
            });
        }
    }

    /**
     * 打开 App
     * @param packageName 包名
     */
    @JavascriptInterface
    public void OpenAPP(String packageName) {
        startAppByPackageName(packageName);
    }

    /**
     * 下载 并安装 App
     * @param url 下载地址
     */
    @JavascriptInterface
    public void InstallAPP(String url) {
        int last = url.lastIndexOf("/") + 1;
        apkName = url.substring(last);
        if (!apkName.contains(".apk")) {
            if (apkName.length() > 10) {
                apkName = apkName.substring(apkName.length() - 10);
            }
            apkName += ".apk";
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            checkDownloadStatus(url, apkName);
        }else {
            openAppDetails();
//            Toast.makeText(this,"你还未获取存储权限，设置后再来吧",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 网页游戏
     * @param url 网址
     */
    @JavascriptInterface
    public void Browser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(getApplicationContext(), "没有匹配的程序", Toast.LENGTH_SHORT).show();
        }
//        startActivity(intent);
    }

    /**
     * 检查 下载状态
     * @param url
     * @param apkName
     */
    private void checkDownloadStatus(final String url, String apkName) {
        boolean isLoading = false;
        DownloadManager.Query query = new DownloadManager.Query();
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
//        c.moveToFirst();
        while (c.moveToNext()) {
            String LoadingUrl = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
            if (url.equals(LoadingUrl)) {
                isLoading = true;
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        Log.i("DownLoadService", ">>>下载暂停");
                    case DownloadManager.STATUS_PENDING:
                        Log.i("DownLoadService", ">>>下载延迟");
                    case DownloadManager.STATUS_RUNNING:
                        long bytes_downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        long bytes_total = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        int progress = (int) (bytes_downloaded * 100 / bytes_total);
                        Toast.makeText(GameWebActivity.this, "正在下载，已完成" + progress + "%", Toast.LENGTH_SHORT).show();
                        Log.i("DownLoadService", ">>>正在下载");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Log.i("DownLoadService", ">>>下载完成");
                        //下载完成安装APK
                        downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "51xianwan" + File.separator + apkName;
                        File file = new File(downloadPath);
                        if (file.exists()) {
                            int sdkVersion = this.getApplicationInfo().targetSdkVersion;
                            if (sdkVersion >= 26 && Build.VERSION.SDK_INT >= 26) {
                                boolean b = false;
                                b = getPackageManager().canRequestPackageInstalls();
                                if (b) {
                                    installAPK(new File(downloadPath), apkName);
                                } else {
                                    ActivityCompat.requestPermissions(GameWebActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_INSTALL_STATE);
                                }
                            } else {
                                installAPK(new File(downloadPath), apkName);
                            }
                        } else {
                            isLoading = false;
                        }
                        break;
                    case DownloadManager.STATUS_FAILED:
                        Log.i("DownLoadService", ">>>下载失败");
                        break;
                }

                break;
            }

        }
        c.close();

        if (!isLoading) {
            if (!canDownloadState()) {
                jumpSetting();
                return;
            }

            SystemUtil.NetState netState = SystemUtil.getNetWorkType(mContext.getApplicationContext());
            if (netState == SystemUtil.NetState.NET_NO) {
                Toast.makeText(mContext, "现在还没有网哦！", Toast.LENGTH_SHORT).show();
            } else if (netState == SystemUtil.NetState.NET_MOBILE) {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(mContext);
                normalDialog.setTitle("温馨提醒");
                normalDialog.setMessage("您现在使用的是非WiFi流量,是否继续?");
                normalDialog.setPositiveButton("继续下载",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                                DownLoadService.startActionFoo(GameWebActivity.this, url);
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                normalDialog.show();
            } else {
                downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "51xianwan" + File.separator + apkName;
                File file = new File(downloadPath);
                Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                DownLoadService.startActionFoo(GameWebActivity.this, url);
            }
        }

    }

    /**
     * 安装App
     * @param file
     * @param apkName
     */
    protected void installAPK(File file, String apkName) {
        if (file == null || !file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        int sdkVersion = this.getApplicationInfo().targetSdkVersion;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && sdkVersion >= 24) {
            // 即是在清单文件中配置的authorities
            uri = FileProvider.getUriForFile(GameWebActivity.this, getApplication().getPackageName() + ".fileProvider", file);//newfile
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            uri = Uri.parse("file://" + file.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释

        startActivity(intent);
    }

    /**
     * 注意这个是8.0新API
     * 进入设置界面 打开未知来源安装
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, REQUEST_INSTALL_UNKNOW_SOURCE);
    }

    /**
     * 启动apk
     * @param packagename
     */
    private void startAppByPackageName(String packagename) {
        if (TextUtils.isEmpty(packagename)) {
            return;
        }
        PackageManager packageManager = getPackageManager();
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        Intent intent = packageManager.getLaunchIntentForPackage(packagename);
        if (intent != null) {
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, "手机还未安装该应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查SD卡读写权限
     */
    public void onCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //判断当前系统的SDK版本是否大于23
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) { //如果当前申请的权限没有授权
                //第一次请求权限的时候返回false,第二次shouldShowRequestPermissionRationale返回true
                //如果用户选择了“不再提醒”永远返回false。
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {

                    Toast.makeText(this, "Please grant the permission this time", Toast.LENGTH_LONG).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(GameWebActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            } else {//已经授权了就走这条分支
                Log.i("wei", "onClick granted");
            }
        }
    }

    /**
     * 检查系统下载器是否可用
     */
    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {

                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 跳转到设置界面
     */
    private void jumpSetting() {
        String packageName = "com.android.providers.downloads";
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            //e.printStackTrace();
            //Open the generic Apps page:
            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            bindingView.webview.loadUrl(bindingView.webview.getUrl());
        } else {
            isFirst = false;
        }
    }

    /**
     * 加个获取权限的监听
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String imeil = TelephonyMgr.getDeviceId();
                } else {
                    Toast.makeText(this, "你不给权限我就不好干事了啦", Toast.LENGTH_SHORT).show();
                }
                openUrl(mGameUrl);
                onCallPermission();
                break;
            }
            case REQUEST_INSTALL_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installAPK(new File(downloadPath), apkName);
                } else {
                    if (Build.VERSION.SDK_INT >= 26) {
                        startInstallPermissionSettingActivity();
                    }
                }
                break;
            }
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
//                    Toast.makeText(this, "你不给权限我就不好干事了啦", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你还未获取存储权限哦，现在去获取？");    // 取消后可到 “应用信息 -> 权限” 中授予"
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 拦截返回和菜单事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&  event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(TextUtils.isEmpty(mGameUrl)){
            if(null!=bindingView) bindingView.webview.loadUrl("about:blank");
            finish();
            return;
        }
        if(null==bindingView){
            finish();
            return;
        }
        if(AppManager.getInstance().existBlackList(mGameUrl)){
            bindingView.webview.loadUrl("about:blank");
            finish();
            return;
        }
        if (bindingView.webview.canGoBack()) {
            bindingView.webview.goBack();
            return;
        }
        bindingView.webview.loadUrl("about:blank");
        finish();
    }

    @Override
    public void onDestroy() {
        if (bindingView != null) {
            bindingView.webview.destroy();
        }
        mWebView= null;
        mContext = null;
        super.onDestroy();
    }

    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    private void checkIsAndroidO(String filePath, String apkName) {

        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = false;
            b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                installAPK(new File(filePath), apkName);
            } else {
                ActivityCompat.requestPermissions(GameWebActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_INSTALL_STATE);
            }
        } else {
            installAPK(new File(filePath), apkName);
        }
    }
}