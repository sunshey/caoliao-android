package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogFirstChargeBinding;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/7/20
 * 首充
 */

public class FirstChargeDialog extends BaseDialog<DialogFirstChargeBinding> {

    private static final String TAG = "FirstChargeDialog";
    public int mMode;


    public static FirstChargeDialog getInstance(Activity activity) {
        return new FirstChargeDialog(activity,0);
    }

    /**
     *
     * @param activity
     * @param mode 0: 充值 1：已完成
     * @return
     */
    public static FirstChargeDialog getInstance(Activity activity,int mode) {
        return new FirstChargeDialog(activity,mode);
    }

    public FirstChargeDialog(@NonNull Activity context,int mode) {
        super(context, R.style.CenterDialogAnimationStyle);
        this.mMode=mode;
        setContentView(R.layout.dialog_first_charge);
        Utils.setDialogWidth(this);
        setCanceledOnTouchOutside(false);//禁止
    }

    @Override
    public void initViews() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //支付宝支付
                    case R.id.btn_zhifubao:
                        FirstChargeDialog.this.dismiss();
                        if(null!=mOnSelectedListener) mOnSelectedListener.onSelected(0);
                        break;
                    //微信支付
                    case R.id.btn_weixin:
                        if(null!=mOnSelectedListener) mOnSelectedListener.onSelected(1);
                        FirstChargeDialog.this.dismiss();
                        break;
                    //关闭
                    case R.id.btn_close:
                        FirstChargeDialog.this.dismiss();
                        break;

                }
            }
        };
        bindingView.btnWeixin.setOnClickListener(onClickListener);
        bindingView.btnZhifubao.setOnClickListener(onClickListener);
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.llRechgreView.setVisibility(mMode==0?View.VISIBLE:View.INVISIBLE);
        bindingView.viewTvFinlish.setVisibility(mMode==1?View.VISIBLE:View.INVISIBLE);
    }

    public interface OnSelectedListener{
        void onSelected(int payType);
    }

    private OnSelectedListener mOnSelectedListener;


    public FirstChargeDialog setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
        return this;
    }
}

