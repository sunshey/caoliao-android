package com.yc.liaolive.videocall.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogQuireVideoLayoutBinding;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/9/29
 * 视频模块对话框
 */

public class QuireVideoDialog extends BaseDialog<DialogQuireVideoLayoutBinding> {

    public static QuireVideoDialog getInstance(Activity context) {
        return new QuireVideoDialog(context);
    }

    public QuireVideoDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_quire_video_layout);
        Utils.setDialogWidth(this);
    }

    @Override
    public void initViews() {
        bindingView.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onClose();
                QuireVideoDialog.this.dismiss();
            }
        });
        bindingView.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onSubmit();
                QuireVideoDialog.this.dismiss();
            }
        });
    }


    /**
     * 设置附属的信息
     * @param title 标题
     * @param content 内容
     * @param submitText 确定按钮文字
     */
    public QuireVideoDialog setTipsData(String title, String content,String submitText){
        if(null!=bindingView){
            bindingView.tvTitle.setText(title);
            bindingView.btnSubmit.setText(submitText);
            bindingView.tvContent.setVisibility(TextUtils.isEmpty(content)?View.GONE:View.VISIBLE);
            bindingView.tvContent.setText(content);
        }
        return this;
    }

    public QuireVideoDialog setTipsData(Spanned title, String submitText){
        if(null!=bindingView){
            bindingView.tvContent.setVisibility(View.GONE);
            bindingView.icMakeSuccessVip.setVisibility(View.VISIBLE);
            bindingView.icMakeSuccessVip.setImageResource(R.drawable.ic_make_success_vip);
            bindingView.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            bindingView.tvTitle.setText(title);
            bindingView.btnSubmit.setText(submitText);
        }
        return this;
    }



    /**
     * 是否显示关闭按钮
     * @param flag
     * @return
     */
    public QuireVideoDialog showCloseBtn(boolean flag){
        if(null!=bindingView){
            bindingView.btnClose.setVisibility(flag?View.VISIBLE:View.GONE);
        }
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public QuireVideoDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public QuireVideoDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
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
        public void onClose(){}
        public void onDissmiss(){}
    }

    private OnSubmitClickListener mOnSubmitClickListener;

    public QuireVideoDialog setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
        return this;
    }
}
