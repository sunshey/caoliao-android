package com.yc.liaolive.videocall.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogQuireAnchorLayoutBinding;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/9/27
 * 预约主播
 */

public class QuireAnchorDialog extends BaseDialog<DialogQuireAnchorLayoutBinding> {

    public static QuireAnchorDialog getInstance(Activity context) {
        return new QuireAnchorDialog(context);
    }

    public QuireAnchorDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_quire_anchor_layout);
        Utils.setDialogWidth(this);
    }

    @Override
    public void initViews() {

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_close:
                        QuireAnchorDialog.this.dismiss();
                        break;
                    case R.id.btn_submit:
                        if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onSubmit();
                        QuireAnchorDialog.this.dismiss();
                        break;
                    case R.id.user_icon:
                        QuireAnchorDialog.this.dismiss();
                        if(null!=mOnSubmitClickListener) mOnSubmitClickListener.onStartUserCenter();
                        break;
                }
            }
        };
        bindingView.userIcon.setOnClickListener(onClickListener);
        bindingView.btnSubmit.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
    }

    /**
     * 设置附属的信息
     * @param submitText 确定按钮文字
     * @param subTitle 副标题文字
     */
    public QuireAnchorDialog setTipsData( String submitText, String subTitle){
        if(null!=bindingView){
            bindingView.btnSubmit.setText(submitText);
            bindingView.tvSubTitle.setText(subTitle);
        }
        return this;
    }

    /**
     * 设置title
     * @param spanned
     * @return
     */
    public QuireAnchorDialog setTitle(Spanned spanned){
        if(null!=bindingView){
            bindingView.tvTitle.setText(spanned);
        }
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public QuireAnchorDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public QuireAnchorDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.setCanceledOnTouchOutside(false);
        return this;
    }

    /**
     * 设置主播头像
     * @param avatar
     * @return
     */
    public QuireAnchorDialog setAuthorAvatar(String avatar) {
        if(null!=bindingView) Glide.with(getContext())
                .load(avatar)
                .placeholder(bindingView.userIcon.getDrawable())
                .error(R.drawable.ic_default_user_head)
                .crossFade()//渐变
                .animate(R.anim.item_alpha_in)//加载中动画
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(bindingView.userIcon);
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
        public void onStartUserCenter() {}
    }

    private OnSubmitClickListener mOnSubmitClickListener;

    public QuireAnchorDialog setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
        return this;
    }
}
