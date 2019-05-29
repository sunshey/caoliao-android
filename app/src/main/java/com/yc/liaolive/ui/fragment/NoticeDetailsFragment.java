package com.yc.liaolive.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.bean.HomeNoticeInfo;
import com.yc.liaolive.databinding.FragmentNoticeDetailsBinding;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.contract.PublicNoticeContract;
import com.yc.liaolive.ui.presenter.PublicNoticPresenter;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2018/7/12
 * 通告详情
 */

public class NoticeDetailsFragment extends BaseFragment<FragmentNoticeDetailsBinding,PublicNoticPresenter> implements PublicNoticeContract.View {

    private static final String TAG = "NoticeDetailsFragment";

    private String mNoticeID;
    private ContentFragmentActivity mActivity;
    private int mReTopBarHeight;//头部的高度

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ContentFragmentActivity) context;
    }

    public static NoticeDetailsFragment getInstance(String noticeID){
        NoticeDetailsFragment fragment=new NoticeDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putString("noticeID",noticeID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mNoticeID = arguments.getString("noticeID");
        }
    }

    @Override
    protected void initViews() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        bindingView.collapseToolbar.measure(width, width);
        mReTopBarHeight = bindingView.collapseToolbar.getMeasuredHeight();
        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(null!=mActivity){
                    int abs = Math.abs(verticalOffset);
                    float scale = (float) abs / mReTopBarHeight;
                    float alpha = (scale * 255);
                    mActivity.setTitleAlpha(alpha);
                }
            }
        });
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        if(null!=mPresenter){
            showLoadingView();
            mPresenter.getNoticeDetails(mNoticeID);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice_details;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter=new PublicNoticPresenter();
        mPresenter.attachView(this);
        showLoadingView();
        mPresenter.getNoticeDetails(mNoticeID);
    }

    private void initWebView(String data) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        bindingView.collapseToolbar.measure(width, width);
        mReTopBarHeight = bindingView.collapseToolbar.getMeasuredHeight();

        final WebSettings webSettings = bindingView.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//        bindingView.webView.addJavascriptInterface(new CLJavascriptInterface(), "HTML");
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 //优先使用缓存:
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setBlockNetworkImage(true);//设置是否加载网络图片 true 为不加载 false 为加载
        bindingView.webView.setBackgroundColor(Color.parseColor("#F2F2F2")); // 设置背景色
        bindingView.webView.getBackground().setAlpha(255); // 设置填充透明度 范围：0-255
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
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showPublicNotices(List<HomeNoticeInfo> data) {

    }

    @Override
    public void showPublicNoticeEmpty() {

    }

    @Override
    public void showPublicNoticeError(int code, String errorMsg) {
        ToastUtils.showCenterToast(errorMsg);
    }

    @Override
    public void showNoticeDetails(HomeNoticeInfo data) {
        showContentView();
        bindingView.tvTitle.setText(data.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        bindingView.tvTime.setText(sdf.format(new Date(data.getAddtime()*1000)));
//        bindingView.tvContent.setText(Html.fromHtml(data.getContent()));
        initWebView(data.getContent());
    }

    @Override
    public void showNoticeDetailError(int code, String errorMsg) {
        showLoadingErrorView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity=null;
    }
}
