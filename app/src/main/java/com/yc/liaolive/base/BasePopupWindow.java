package com.yc.liaolive.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * TinyHung@outlook.com
 * 2017/3/28 16:40
 * Popup父类
 */
public abstract class BasePopupWindow<DV extends ViewDataBinding> extends PopupWindow{

    protected Activity context;
    protected DV bindingView;

    public BasePopupWindow(Activity context){
        bindingView = DataBindingUtil.inflate(context.getLayoutInflater(), setLayoutID(), null, false);
        setContentView(bindingView.getRoot());
        this.context= context;
        setLayoutPrams();
        initData();
        initViews();
    }

    /**
     * 设置宽高和样式
     */
    private void setLayoutPrams() {
//        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setAnimationStyle(setAnimationStyle());//设置动画
    }

    /**
     * 设置动画
     * @return
     */
    public abstract int setAnimationStyle();
    public abstract int  setLayoutID();
    public abstract void  initViews();
    public abstract void initData();

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        backgroundAlpha(0.6f);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(1.0f);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp =context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().setAttributes(lp);
    }
}
