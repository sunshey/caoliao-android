package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogQuireLayoutBinding;
import com.yc.liaolive.model.GlideCircleTransform;

/**
 * TinyHung@Outlook.com
 * 2017/9/15.
 * 询问对话框
 */

public class QuireDialog extends BaseDialog<DialogQuireLayoutBinding> {

    private boolean btnClickDismiss = true;

    public static QuireDialog getInstance(Activity activity) {
        return new QuireDialog(activity);
    }

    public QuireDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_quire_layout);
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_submit:
                        if (btnClickDismiss) {
                            QuireDialog.this.dismiss();
                        }
                        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onConsent();
                        break;
                    case R.id.tv_cancel:
                        if (btnClickDismiss) {
                            QuireDialog.this.dismiss();
                        }
                        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onRefuse();
                        break;
                    case R.id.btn_close:
                        if (btnClickDismiss) {
                            QuireDialog.this.dismiss();
                        }
                        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onCloseDialog();
                        break;
                }
            }
        };
        bindingView.tvSubmit.setOnClickListener(onClickListener);
        bindingView.tvCancel.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mOnQueraConsentListener) mOnQueraConsentListener.onDissmiss();
    }

    /**
     * 设置标题
     * @param title
     * @return
     */
    public  QuireDialog setTitleText(String title) {
        if(null!=bindingView) bindingView.tvTitle.setText(title);
        return this;
    }

    /**
     * 设置确定按钮文字内容
     * @param submitTitle
     * @return
     */
    public QuireDialog setSubmitTitleText(String submitTitle) {
        if(null!=bindingView) bindingView.tvSubmit.setText(submitTitle);
        return this;
    }

    /**
     * 设置取消按钮是否显示
     * @param visible
     * @return
     */
    public QuireDialog setCancelTitleVisible (int visible) {
        if(null!=bindingView){
            if (visible == View.GONE) {
                bindingView.tvSubmit.setBackgroundResource(R.drawable.bt_save_btn_selector);
            }
            bindingView.tvCancel.setVisibility(visible);
            bindingView.btnLine.setVisibility(visible);
        }
        return this;
    }

    /**
     * 设置取消文字按钮
     * @param cancelTitleText
     * @return
     */
    public  QuireDialog setCancelTitleText(String cancelTitleText) {
        if(null!=bindingView) bindingView.tvCancel.setText(cancelTitleText);
        return this;
    }
    /**
     * 设置提示内容
     * @param content
     * @return
     */
    public  QuireDialog setContentText(String content) {
        if(null!=bindingView) bindingView.tvContent.setText(Html.fromHtml(content));
        return this;
    }

    /**
     * 设置副提示内容
     * @param subContent
     * @return
     */
    public QuireDialog setSubContentText(String subContent) {
        if(null!=bindingView) {
            bindingView.tvSubContent.setVisibility(TextUtils.isEmpty(subContent)?View.GONE:View.VISIBLE);
            bindingView.tvSubContent.setText(Html.fromHtml(subContent));
        }
        return this;
    }


    /**
     * 设置标题文字颜色
     * @param color
     * @return
     */
    public  QuireDialog setTitleTextColor(int color) {
        if(null!=bindingView) bindingView.tvTitle.setTextColor(color);
        return this;
    }


    /**
     * 设置确定按钮文字颜色
     * @param color
     * @return
     */
    public QuireDialog setSubmitTitleTextColor(int color) {
        if(null!=bindingView) bindingView.tvSubmit.setTextColor(color);
        return this;
    }

    /**
     * 设置取消文字颜色
     * @param color
     * @return
     */
    public  QuireDialog setCancelTitleTextColor(int color) {
        if(null!=bindingView) bindingView.tvCancel.setTextColor(color);
        return this;
    }

    /**
     * 设置提示内容文字颜色
     * @param color
     * @return
     */
    public  QuireDialog setContentTextColor(int color) {
        if(null!=bindingView) bindingView.tvContent.setTextColor(color);
        return this;
    }

    /**
     * 是否显示圆形图片
     * @param flag
     * @return
     */
    public QuireDialog showHeadView(boolean flag){
        if(null!=bindingView) bindingView.ivIcon.setVisibility(flag?View.VISIBLE:View.GONE);
        return this;
    }

    /**
     * 是否显示关闭按钮
     * @param flag
     */
    public QuireDialog showCloseBtn(boolean flag){
        if(null!=bindingView) bindingView.btnClose.setVisibility(flag?View.VISIBLE:View.GONE);
        return this;
    }

    /**
     * 设置圆形图片地址URL
     * @param coverUrl
     * @return
     */
    public QuireDialog setHeadCover(String coverUrl){
        if(null!=bindingView){
            Glide.with(getContext())
                    .load(coverUrl)
                    .error(R.drawable.ic_default_user_head)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideCircleTransform(getContext()))
                    .into(bindingView.ivIcon);
            if(null!=bindingView) bindingView.ivIcon.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 点击确认、取消按钮时是否自动关闭弹窗
     * @param dismiss 是否自动关闭
     * @return
     */
    public QuireDialog setBtnClickDismiss(boolean dismiss){
        this.btnClickDismiss = dismiss;
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public QuireDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public QuireDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        return this;
    }

    public QuireDialog showTitle(boolean flag) {
        if(null!=bindingView) bindingView.tvTitle.setVisibility(flag?View.VISIBLE:View.GONE);
        return this;
    }

    public QuireDialog setContentTextStyle(int style) {
        if(null!=bindingView) bindingView.tvContent.setTypeface(Typeface.defaultFromStyle(style));
        return null;
    }

    public abstract static class OnQueraConsentListener{
        public void onConsent(){}
        public void onRefuse(){}
        public void onDissmiss(){}
        public void onCloseDialog(){}
    }
    private OnQueraConsentListener mOnQueraConsentListener;

    public QuireDialog setOnQueraConsentListener(OnQueraConsentListener onQueraConsentListener) {
        mOnQueraConsentListener = onQueraConsentListener;
        return this;
    }
}
