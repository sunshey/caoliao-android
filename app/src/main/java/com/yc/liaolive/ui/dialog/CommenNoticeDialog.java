package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogNoticeLayoutBinding;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/6/13
 * 通用通知
 */

public class CommenNoticeDialog extends BaseDialog<DialogNoticeLayoutBinding> {

    public static CommenNoticeDialog getInstance(Activity context) {
        return new CommenNoticeDialog(context);
    }

    public CommenNoticeDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_notice_layout);
        Utils.setDialogWidth(this);
    }

    @Override
    public void initViews() {
        bindingView.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommenNoticeDialog.this.dismiss();
            }
        });
        bindingView.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onSubmit();
                CommenNoticeDialog.this.dismiss();
            }
        });
    }


    /**
     * 设置附属的信息
     * @param title 标题
     * @param desp 提示内容
     * @param submitText 确定按钮文字
     */
    public CommenNoticeDialog setTipsData(String title,String desp,String submitText){
        if(null!=bindingView){
            bindingView.tvTitle.setText(title);
            bindingView.tvSubtitle.setText(desp);
            bindingView.btnSubmit.setText(submitText);
        }
        return this;
    }

    public CommenNoticeDialog setSubSubmitTitle(String subSubmitText) {
        if(null!=bindingView){
            bindingView.tvSubtitle.setText(subSubmitText);
        }
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public CommenNoticeDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public CommenNoticeDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.setCanceledOnTouchOutside(false);
        return this;
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onDissmiss();
    }


    public abstract static class OnSubmitClickListener{
        public void onSubmit(){}
        public void onDissmiss(){}
    }

    private OnSubmitClickListener mOnSubmitClickListener;

    public CommenNoticeDialog setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
        return this;
    }
}
