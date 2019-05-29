package com.yc.liaolive.index.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.just.agentweb.AgentWeb;
import com.kk.utils.LogUtil;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.ActivityWebviewBinding;
import com.yc.liaolive.util.Utils;
import com.yc.loanbox.view.AndroidInterface;
import com.yc.loanboxsdk.LoanboxSDK;
import java.util.Map;

/**
 * TinyHung@Outlook.com
 * 2019/3/4
 *
 */

public class IndexWebViewFragment extends BaseFragment<ActivityWebviewBinding,RxBasePresenter> {

    public final static String TAG = "IndexWebViewFragment";
    private AgentWeb mAgentWeb;
    private String mUrl = "";
    private String title="";
    private String id="";
    private FrameLayout mWebView;
    private boolean isRefresh=true;

    public static IndexWebViewFragment newInstance(String hostUrl,String title,int index) {
        IndexWebViewFragment fragment=new IndexWebViewFragment();
        Bundle bundle=new Bundle();
        bundle.putString("url",hostUrl);
        bundle.putString("title",title);
        bundle.putInt("index",index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mUrl = arguments.getString("url");
            title = arguments.getString("title");
        }
    }

    @Override
    protected void initViews() {}

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment parentFragment = getParentFragment();
        if(null!=parentFragment){
            bindingView.statusBar.setVisibility(View.GONE);
        }
        mWebView = (FrameLayout) getView().findViewById(R.id.webview);
        Map<String,String> paramsMap = Utils.getParamsExtra(mUrl);
        LoanboxSDK loanboxSDK = LoanboxSDK.defaultLoanboxSDK();
        loanboxSDK.setChannelId(paramsMap.get("agent_id"));
        loanboxSDK.init(getActivity());
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if(isRefresh&&null!=bindingView){
            openUrl();
            isRefresh=false;
        }
    }

    private void openUrl() {
        if (!TextUtils.isEmpty(mUrl)) {
            LogUtil.msg(mUrl);
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(mWebView, new FrameLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator(ContextCompat.getColor(getActivity(), R.color.colorAccent))
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
            mAgentWeb.getWebCreator().getWebView().addJavascriptInterface(new AndroidInterface(getActivity(), id), "android");
        }
    }

    @Override
    public void fromMainUpdata() {
        super.fromMainUpdata();
        if(!TextUtils.isEmpty(mUrl)){
            openUrl();
        }
    }
}