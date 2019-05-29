package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogProgressLayoutBinding;
import com.yc.liaolive.util.CommonUtils;

/**
 * TinyHung@outlook.com
 * 2017/3/25 15:16
 * 加载进度条
 */

public class LoadingProgressView extends BaseDialog<DialogProgressLayoutBinding> {

    private boolean isBack = false;
    private AnimationDrawable mAnimationDrawable;

    public LoadingProgressView(Activity context) {
        super(context, R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_progress_layout);
        setCanceledOnTouchOutside(false);
        onSetCanceledOnTouchOutside(false);
    }

    @Override
    public void initViews() {
        mAnimationDrawable = (AnimationDrawable) bindingView.ivLoadingIcon.getDrawable();
    }

    /**
     * 显示弹窗的结果状态
     *
     * @param mode 动画模式，1：成功，2：失败
     * @param closeDelyaedTime 关闭自身的延时时长
     */
    public void showResult(int mode, String msg, int closeDelyaedTime) {
        if (null != mAnimationDrawable && mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        if (null != bindingView) {
            bindingView.ivLoadingIcon.setVisibility(View.GONE);
            bindingView.finlishView.setVisibility(View.VISIBLE);
            bindingView.tvMsgContent.setText(msg);
            bindingView.finlishView.setSuccessColor(CommonUtils.getColor(R.color.white));
            bindingView.finlishView.setmResultType(mode);//成功状态
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LoadingProgressView.this.isShowing()) {
                    LoadingProgressView.this.dismiss();
                }
            }
        }, closeDelyaedTime);
    }

    @Override
    public void show() {
        super.show();
        if (null != bindingView) {
            bindingView.finlishView.setVisibility(View.GONE);
            bindingView.ivLoadingIcon.setVisibility(View.VISIBLE);
            if (null != mAnimationDrawable && !mAnimationDrawable.isRunning())
                mAnimationDrawable.start();
        }
    }

    public void showMessage(String message) {
        super.show();
        if (null != bindingView) {
            bindingView.tvMsgContent.setText(message);
            bindingView.finlishView.setVisibility(View.GONE);
            bindingView.ivLoadingIcon.setVisibility(View.VISIBLE);
            if (null != mAnimationDrawable && !mAnimationDrawable.isRunning())
                mAnimationDrawable.start();
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (null != mAnimationDrawable && mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        mAnimationDrawable = null;
    }

    public interface OnDialogBackListener {
        void onBack();
    }

    private OnDialogBackListener mOnDialogBackListener;

    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        mOnDialogBackListener = onDialogBackListener;
    }

    /**
     * 将用户按下返回键时间传递出去
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isBack) {
                if (mOnDialogBackListener != null) {
                    mOnDialogBackListener.onBack();
                }
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置Load文字
     *
     * @param message
     */
    public void setMessage(String message) {
        if(null!=bindingView) bindingView.tvMsgContent.setText(message);
    }

    /**
     * 设置返回键是否可用
     */
    public void onSetCancelable(boolean isClose) {
        setCancelable(isClose);
    }

    /**
     * 设置点击空白处是否关闭弹窗
     */
    public void onSetCanceledOnTouchOutside(boolean isBack) {
        this.isBack = isBack;
        setCanceledOnTouchOutside(isBack);
    }
}
