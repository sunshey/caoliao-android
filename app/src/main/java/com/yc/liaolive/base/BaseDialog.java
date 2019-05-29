package com.yc.liaolive.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.content.Context;

/**
 * TinyHung@Outlook.com
 * 2017/3/24 9:12
 * 弹窗的统一父类
 */

public abstract class BaseDialog<V extends ViewDataBinding> extends AppCompatDialog {

    protected  V bindingView;
    protected Activity mActivity;

    public BaseDialog(@NonNull Activity context) {
        super(context);
        mActivity=context;
    }

    public BaseDialog(@NonNull Activity context,int themeResId) {
        super(context,themeResId);
        mActivity=context;
    }

    @Override
    public void setContentView(int layoutResId) {
        bindingView=DataBindingUtil.inflate(getLayoutInflater(),layoutResId,null,false);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(layoutParams);
        getWindow().setContentView(bindingView.getRoot());
        initViews();
    }
    public abstract void initViews();

    /**
     * 设置Dialog依附在屏幕中的位置
     */
    protected void initLayoutParams(int gravity) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= gravity;
    }


    /**
     * 设置Dialog依附在屏幕中的位置
     */
    protected void initLayoutMarginParams(int gravity) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= LinearLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        attributes.width= systemService.getDefaultDisplay().getWidth()-120;
        attributes.gravity= gravity;
    }



    protected Activity getActivity(){
        return mActivity;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mActivity=null;
    }
}
