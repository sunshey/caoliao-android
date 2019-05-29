package com.yc.liaolive.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.BasePagerBinding;
import com.yc.liaolive.interfaces.SnackBarListener;
import com.yc.liaolive.util.ToastUtils;

/**
 * TinyHung@Outlook.com
 * 2017/9/11
 * 片段界面的Pager基类
 */

public abstract class BasePager <T extends ViewDataBinding>{

    protected static final String TAG = "BasePager";
    protected T bindingView;
    private BasePagerBinding baseBindingView;
    protected  Activity mContext;
    protected   int mCurrentPosition;
    protected boolean isVisible;//当前是否处于可见状态

    public BasePager(Activity context){
        this.mContext=context;
    }

    /**
     * 设置LayoutID
     * @param layoutID
     */
    public void setContentView(int layoutID){
        if(null!=mContext){
            //父View
            baseBindingView = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.base_pager, null, false);
            //子View
            bindingView = DataBindingUtil.inflate(mContext.getLayoutInflater(),layoutID, (ViewGroup) baseBindingView.getRoot().getParent(), false);
            //父内容容器
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            bindingView.getRoot().setLayoutParams(params);
            baseBindingView.viewContent.addView(bindingView.getRoot());//添加至父容器
            initViews();
            initData();
        }
    }

    public View getView() {
        return baseBindingView.getRoot();
    }


    public Activity getContext() {
        return mContext;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }


    public abstract void initViews();

    public abstract void initData();

    protected void onCreate(){

    }

    protected void onStart(){
        setVisible(true);
    }

    protected void onResume(){
        setVisible(true);
    }

    protected void onPause(){
        setVisible(false);
    }

    public void onStop(){
        setVisible(false);
    }

    protected void onBackPressed(){
//        if(null!=mContext) mContext.onBackPressed();
    }

    protected void onDestroy(){
        setVisible(false);
        mContext=null;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    protected View findViewById(int resID){
        if(null!=baseBindingView){
            return baseBindingView.getRoot().findViewById(resID);
        }
        return null;
    }
    /**
     * 失败吐司
     * @param action
     * @param snackBarListener
     * @param message
     */
    protected void showErrorToast(String action, SnackBarListener snackBarListener, String message){
        if(null!=mContext&&!mContext.isFinishing()){
            ToastUtils.showSnackebarStateToast(mContext.getWindow().getDecorView(),action,snackBarListener, R.drawable.snack_bar_error_white, Constant.SNACKBAR_ERROR,message);
        }
    }
}
