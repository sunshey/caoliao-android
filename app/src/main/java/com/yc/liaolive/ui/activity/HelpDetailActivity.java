package com.yc.liaolive.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.HelpInfo;
import com.yc.liaolive.databinding.ActivityHelpDetailBinding;
import com.yc.liaolive.ui.contract.HelpDetailContract;
import com.yc.liaolive.ui.presenter.HelpDetailPresenter;
import com.yc.liaolive.view.layout.DataChangeView;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wanglin  on 2018/7/5 15:40.
 */
public class HelpDetailActivity extends BaseActivity<ActivityHelpDetailBinding> implements HelpDetailContract.View {
    private String id;
    private HelpDetailPresenter mPresenter;

    public static void start(Context context, String id, String title) {
        Intent intent = new Intent(context, HelpDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_detail);
        id = getIntent().getStringExtra("id");
        bindingView.stateView.showLoadingView();
        bindingView.stateView.setOnRefreshListener(new DataChangeView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bindingView.stateView.showLoadingView();
                mPresenter.getHelpInfoDetail(id);
            }
        });
        mPresenter = new HelpDetailPresenter(this);
        mPresenter.attachView(this);
        mPresenter.getHelpInfoDetail(id);
    }

    @Override
    public void initViews() {
        bindingView.commentTitleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void showHelpDetail(HelpInfo data) {
        if(null!=bindingView){
            bindingView.stateView.stopLoading();
            bindingView.llContainer.setVisibility(View.VISIBLE);
            bindingView.tvTitle.setText(data.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
            bindingView.tvTime.setText(sdf.format(new Date(data.getAddtime() * 1000)));
            bindingView.tvContent.setText(Html.fromHtml(data.getContent()));
//        bindingView.tvContent.setText(data.getContent());
            initWebView(data.getContent());
        }
    }

    @Override
    public void showNoData() {
        if(null!=bindingView) bindingView.stateView.showNoData();
    }

    @Override
    public void showNoNet() {
        if(null!=bindingView) bindingView.stateView.showErrorView();
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    private void initWebView(String data) {
        final WebSettings webSettings = bindingView.webView.getSettings();
        bindingView.webView.setBackgroundColor(Color.TRANSPARENT);
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 //优先使用缓存:
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setBlockNetworkImage(true);//设置是否加载网络图片 true 为不加载 false 为加载
        bindingView.webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
        bindingView.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webSettings.setBlockNetworkImage(false);
            }
        });
    }

    @Override
    public void hide() {

    }

    @Override
    public void showLoading() {

    }
}
