package com.yc.liaolive.base;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * TinyHung@Outlook.com
 * 2017/3/24 9:12
 * 弹窗的统一父类,新特性下拉关闭，位于屏幕底部
 */

public abstract class BaseBottomSheetDialog<V extends ViewDataBinding> extends BottomSheetDialog {

    protected  V bindingView;

    public BaseBottomSheetDialog(@NonNull Activity context) {
        super(context);
    }

    public BaseBottomSheetDialog(@NonNull Activity context, int themeResId) {
        super(context,themeResId);
    }

    /**
     * 默认在屏幕底部
     * @param layoutResId
     */
    @Override
    public void setContentView(int layoutResId) {
        bindingView=DataBindingUtil.inflate(getLayoutInflater(),layoutResId,null,false);
        getWindow().setContentView(bindingView.getRoot());
        initViews();
    }

    protected void initLayoutPrams(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        attributes.height= FrameLayout.LayoutParams.WRAP_CONTENT;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= Gravity.BOTTOM;
    }

    /**
     * 指定位于屏幕的位置
     * @param layoutResId
     * @param dropLocation 抛锚位置
     */
    public void setContentView(int layoutResId,int dropLocation) {
        bindingView=DataBindingUtil.inflate(getLayoutInflater(),layoutResId,null,false);
        Window window = getWindow();
        window.setContentView(bindingView.getRoot());
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams attributes = window.getAttributes();//得到布局管理者
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//得到窗口管理者
        DisplayMetrics displayMetrics=new DisplayMetrics();//创建设备屏幕的管理者
        systemService.getDefaultDisplay().getMetrics(displayMetrics);//得到屏幕的宽高
        int hight= FrameLayout.LayoutParams.WRAP_CONTENT;//取出布局的高度
        attributes.height= hight;
        attributes.width= systemService.getDefaultDisplay().getWidth();
        attributes.gravity= dropLocation;
        initViews();
    }

    public abstract void initViews();

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
