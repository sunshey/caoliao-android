package com.yc.liaolive.webview.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.just.agentweb.AgentWeb;
import com.kk.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.databinding.ActivityWebviewBinding;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.Utils;
import com.yc.loanbox.helper.AndroidBug5497Workaround;
import com.yc.loanbox.view.AndroidInterface;
import com.yc.loanboxsdk.LoanboxSDK;
import java.util.Map;

/**
 * Loan类
 */
public class WebLoanActivity extends com.yc.liaolive.base.BaseActivity<ActivityWebviewBinding> {

    public final static String TAG = "WebLoanActivity";
    private AgentWeb mAgentWeb;
    private String mUrl = "";
    private FrameLayout mWebView;
    private String id="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getIntent().getStringExtra("url");
        setContentView(R.layout.activity_webview);
    }

    @Override
    public void initViews() {
        bindingView.viewBottomLayout.getLayoutParams().height=0;
        bindingView.statusBar.setBackgroundColor(Color.parseColor("#999999"));
        mWebView = (FrameLayout) findViewById(R.id.webview);
        AndroidBug5497Workaround.assistActivity(this);
        Map<String,String> paramsMap = Utils.getParamsExtra(mUrl);
        LoanboxSDK loanboxSDK = LoanboxSDK.defaultLoanboxSDK();
        loanboxSDK.setChannelId(paramsMap.get("agent_id"));
        MobclickAgent.onEvent(WebLoanActivity.this,"channel_id_"+paramsMap.get("agent_id"));
        loanboxSDK.init(WebLoanActivity.this);
        if (!TextUtils.isEmpty(mUrl)) {
            LogUtil.msg(mUrl);
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(mWebView, new FrameLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator(ContextCompat.getColor(WebLoanActivity.this, R.color.colorAccent))
                    .setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageCommitVisible(WebView view, String url) {}

                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                        }
                    })
                    .createAgentWeb()
                    .ready()
                    .go(this.mUrl);
            mAgentWeb.getWebCreator().getWebView().addJavascriptInterface(new AndroidInterface(WebLoanActivity.this, id), "android");
        }
    }

    @Override
    public void initData() {}

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return false;
        }
        if (!mAgentWeb.back()) {
            finish();
        }
        return true;
    }
}