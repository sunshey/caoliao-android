package com.yc.liaolive.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.interfaces.SnackBarListener;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.util.LogRecordUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;

/**
 * TinyHung@outlook.com
 * 2018/4/12 15:06
 * 所有Fragment 片段基类，适合于子类不需要父类处理界面显示状态的片段
 */

public abstract class BaseFragment<VS extends ViewDataBinding, P extends RxBasePresenter> extends Fragment {

    // 子布局view
    protected VS bindingView;
    protected P mPresenter;
    protected LoadingProgressView mLoadingProgressedView;
    private View mLoadingStateView;
    private LoadingIndicatorView mLoadingView;

    protected abstract void initViews();
    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_base, null);
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), getLayoutId(), null, false);
        if (null != bindingView) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bindingView.getRoot().setLayoutParams(params);
            FrameLayout contentView = (FrameLayout) ll.findViewById(R.id.content_view);
            contentView.addView(bindingView.getRoot());
        }
        return ll;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingStateView = getView().findViewById(R.id.base_loading_state);
        mLoadingStateView.findViewById(R.id.base_load_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        //加载中
        mLoadingView = getView().findViewById(R.id.base_loading_view);
        mLoadingView.hide();//默认是不显示加载中的
        initViews();
    }

    /**
     * 刷新，子类需要刷新 请复写
     */
    protected void onRefresh() {}

    /**
     * 切换当前显示的Fragment
     * @param fragment
     */
    public void addReplaceFragment(Fragment fragment) {
        try {
            android.support.v4.app.FragmentManager supportFragmentManager = getChildFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment, "FRAGMENT");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onInvisible() {
    }

    protected void onVisible() {
    }

    protected void fromMainUpdata() {
    }

    protected <T extends View> T getView(int id) {
        if (null == getView()) return null;
        return (T) getView().findViewById(id);
    }


    protected void showLoadingView() {
        showLoadingView(null);
    }

    /**
     * 显示加载中
     */
    protected void showLoadingView(String message) {
        if (null != mLoadingStateView) {
            mLoadingStateView.setClickable(true);
            mLoadingStateView.setVisibility(View.VISIBLE);
            ImageView loadingView = (ImageView) mLoadingStateView.findViewById(R.id.base_load_icon);
            loadingView.setImageResource(0);
            ((TextView) mLoadingStateView.findViewById(R.id.base_load_content)).setText("");
        }
        if(null!=mLoadingView&&mLoadingView.getVisibility()!=View.VISIBLE) mLoadingView.smoothToShow();
    }

    /**
     * 显示界面内容
     */
    protected void showContentView() {
        if (null != mLoadingStateView) {
            mLoadingStateView.setVisibility(View.GONE);
            mLoadingStateView.setClickable(false);
            ImageView loadingView = (ImageView) mLoadingStateView.findViewById(R.id.base_load_icon);
            loadingView.setImageResource(0);
            ((TextView) mLoadingStateView.findViewById(R.id.base_load_content)).setText("");
        }
        if(null!=mLoadingView&&mLoadingView.getVisibility()!=View.GONE) mLoadingView.smoothToHide();
    }


    /**
     * 显示加载失败
     */
    protected void showLoadingErrorView() {
        if(null!=mLoadingView&&mLoadingView.getVisibility()!=View.GONE) mLoadingView.smoothToHide();
        if (null != mLoadingStateView) {
            mLoadingStateView.setClickable(true);
            mLoadingStateView.setVisibility(View.VISIBLE);
            ImageView loadingView = (ImageView) mLoadingStateView.findViewById(R.id.base_load_icon);
            loadingView.setImageResource(R.drawable.ic_net_error);
            ((TextView) mLoadingStateView.findViewById(R.id.base_load_content)).setText("加载失败，轻触重试");
        }
    }


    /**
     * 显示进度框
     *
     * @param message
     */
    protected void showProgressDialog(String message) {
        if (null != getActivity() && !getActivity().isFinishing()) {
            if (null == mLoadingProgressedView) {
                mLoadingProgressedView = new LoadingProgressView(getActivity());
            }
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    /**
     * 关闭进度框
     */
    protected void closeProgressDialog() {
        if (null != mLoadingProgressedView && mLoadingProgressedView.isShowing() && !getActivity().isFinishing()) {
            mLoadingProgressedView.dismiss();
        }
        mLoadingProgressedView = null;
    }

    /**
     * 失败吐司
     *
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showErrorToast(String action, SnackBarListener snackBarListener, String message) {
        if (null != getActivity()) {
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().getDecorView(), action, snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR, message);
        }
    }

    /**
     * 成功吐司
     *
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showFinlishToast(String action, SnackBarListener snackBarListener, String message) {
        if (null != getActivity()) {
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT), action, snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE, message);
        }
    }

    /**
     * 统一的网络设置入口
     */
    protected void showNetWorkTips() {
        showErrorToast("网络设置", new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                startActivity(intent);
            }
        }, "没有可用的网络链接");
    }

    /**
     * H5微信支付
     * @param url
     */
    public void openWxpay(String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
            String content="H5微信支付唤起微信客户端失败，可能原因：未安装微信客户端、系统未识别到Scheme头协议，errorCode:"+0+",errorMsg:"+e.getMessage()+",appSign:"+appSign;
            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
        }
    }

    /**
     * H5支付宝支付
     * @param url
     */
    public void openAlipay(String url) {
        try {
            Intent intent;
            intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setComponent(null);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            String appSign = LogRecordUtils.getInstance().getAppSignToMd5(AppEngine.getApplication().getApplicationContext());
            String content="H5支付宝支付唤起支付宝客户端失败，可能原因：未安支付宝客户端、系统未识别到Scheme头协议，errorCode:"+0+",errorMsg:"+e.getMessage()+",appSign:"+appSign;
            LogRecordUtils.getInstance().postSystemErrorMessage(LogRecordUtils.LEVE_PAY,content,appSign);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mLoadingView) mLoadingView.hide();
        if (null != mPresenter) mPresenter.detachView();
        Runtime.getRuntime().gc();
    }
}
