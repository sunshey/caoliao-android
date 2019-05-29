package com.yc.liaolive.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.interfaces.SnackBarListener;
import com.yc.liaolive.ui.activity.ContentFragmentActivity;
import com.yc.liaolive.ui.dialog.LoadingProgressView;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.refresh.LoadingIndicatorView;

/**
 * TinyHung@outlook.com
 * 2017/3/17 15:46
 * 替代Dialog的解决方案Fragment
 */

public abstract class BaseDialogFragment<VS extends ViewDataBinding,P extends RxBasePresenter> extends DialogFragment {

    private static final String TAG = "BaseDialogFragment";
    // 子布局view
    protected VS bindingView;
    protected P mPresenter;
    protected LoadingProgressView mLoadingProgressedView;
    //这两个属性优先isAutoHeight
    private int fragmentMarginHeight=0;//距离顶部的距离，子类可定制决定
    private Boolean isAutoHeight=false;//根布局是否自适应高度
    private int mGravity=Gravity.BOTTOM;


    public void setAutoHeight(Boolean autoHeight) {
        isAutoHeight = autoHeight;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }


    protected void setFragmentMarginHeight(int fragmentMarginHeight) {
        this.fragmentMarginHeight = fragmentMarginHeight;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(mGravity);
        View ll = inflater.inflate(R.layout.fragment_base, (ViewGroup) window.findViewById(R.id.content),false);
        bindingView = DataBindingUtil.inflate(getActivity().getLayoutInflater(), getLayoutId(), null, false);

        if (bindingView != null && bindingView.getRoot() != null) {
            int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            bindingView.getRoot().measure(width,width);
        }

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//透明
        //宽度缩减，高度自适应
        if(null!=isAutoHeight&&isAutoHeight){
            window.setLayout(ScreenUtils.getScreenWidth()-190,FrameLayout.LayoutParams.WRAP_CONTENT);//这2行,和上面的一样,注意顺序就行;
            window.setWindowAnimations(R.style.CenterAnimation);//从小到大
        //宽度随屏幕，高度根据用户指定
        }else if(null!=isAutoHeight&&!isAutoHeight){
            window.setLayout(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight()-ScreenUtils.dpToPxInt(fragmentMarginHeight));//这2行,和上面的一样,注意顺序就行;
            window.setWindowAnimations(R.style.ButtomAnimation);//从下而上
        //宽度随屏幕，高度位置
        }else{
            window.setLayout(ScreenUtils.getScreenWidth(),FrameLayout.LayoutParams.WRAP_CONTENT);//高度自适应
            window.setWindowAnimations(R.style.ButtomAnimation);//从小到大
        }

        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount=0.0f;
        window.setAttributes(attributes);
        if(null!=bindingView){
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
        LoadingIndicatorView loadingIndicatorView = getView().findViewById(R.id.base_loading_view);
        loadingIndicatorView.hide();//默认是不显示加载中的
        initViews();
    }

    protected abstract void initViews();
    public abstract int getLayoutId();

    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        getActivity().startActivity(intent);
    }

    /**
     * 根据Targe打开新的界面
     * @param title
     * @param fragmentTarge
     */
    protected void startTargetActivity(int fragmentTarge,String title,String authorID,int authorType,String topicID) {
        Intent intent=new Intent(getActivity(), ContentFragmentActivity.class);
        intent.putExtra(Constant.KEY_FRAGMENT_TYPE,fragmentTarge);
        intent.putExtra(Constant.KEY_TITLE,title);
        intent.putExtra(Constant.KEY_AUTHOR_ID,authorID);
        intent.putExtra(Constant.KEY_AUTHOR_TYPE,authorType);
        intent.putExtra(Constant.KEY_VIDEO_TOPIC_ID,topicID);
        getActivity().startActivity(intent);
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onInvisible() {}
    protected void onVisible() {}

    protected <T extends View> T getView(int id) {
        if(null==getView()) return null;
        return (T) getView().findViewById(id);
    }

    /**
     * 设置进度框文字
     * @param message
     */
    protected void setProgressDialogMessage(String message){
        if(null!=mLoadingProgressedView&&!getActivity().isFinishing()&&mLoadingProgressedView.isShowing()){
            mLoadingProgressedView.setMessage(message);
        }
    }


    /**
     * 显示进度框
     * @param message
     * @param isProgress
     */
    protected void showProgressDialog(String message,boolean isProgress){
        if(null!=getActivity()&&!getActivity().isFinishing()){
            if(null==mLoadingProgressedView){
                mLoadingProgressedView = new LoadingProgressView(getActivity());
            }
            mLoadingProgressedView.setMessage(message);
            mLoadingProgressedView.show();
        }
    }

    /**
     * 关闭进度框
     */
    protected void closeProgressDialog(){
        if(null!=mLoadingProgressedView&&!getActivity().isFinishing()&&mLoadingProgressedView.isShowing()){
            mLoadingProgressedView.dismiss();
        }
        mLoadingProgressedView=null;
    }

    /**
     * 失败吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=getActivity()){
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
        }
    }

    /**
     * 统一的网络设置入口
     */
    protected void showNetWorkTips(){

        showErrorToast("网络设置", new SnackBarListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);//直接进入网络设置
                startActivity(intent);
            }
        }, "没有可用的网络链接");
    }

    /**
     * 成功吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showFinlishToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=getActivity()){
            ToastUtils.showSnackebarStateToast(getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT),action,snackBarListener, R.drawable.snack_bar_done_white, Constant.SNACKBAR_DONE,message);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mPresenter) mPresenter.detachView();
        Runtime.getRuntime().gc();
    }
}
