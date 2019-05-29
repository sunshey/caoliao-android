package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.Gravity;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogQuirePaywayLayoutBinding;
import com.yc.liaolive.recharge.model.bean.PayConfigBean;
import com.yc.liaolive.recharge.view.PayChanlSelectedLayout;

/**
 * TinyHung@Outlook.com
 * 2017/10/22
 * 充值路径询问
 */

public class PayWayQuire extends BaseDialog<DialogQuirePaywayLayoutBinding> {

    private PayConfigBean payConfigBean;
    private static Activity mActivity;

    public static PayWayQuire getInstance(Activity activity) {
        mActivity = activity;
        return new PayWayQuire(activity);
    }

    public PayWayQuire(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_quire_payway_layout);
        initLayoutMarginParams(Gravity.CENTER);
    }

    @Override
    public void initViews() {
//        bindingView.viewPayChannel.setReset();
        bindingView.viewPayChannel.setOnPayChanlChangedListener(new PayChanlSelectedLayout.OnPayChanlChangedListener() {
            @Override
            public void onPayChanlChanged(int chanl) {
                if(null!=mOnPayChanlChangedListener){
                    PayWayQuire.this.dismiss();
                    mOnPayChanlChangedListener.onPayChanlChanged(chanl);
                }
            }
        });
    }


    /**
     * 设置支付列表
     */
    public PayWayQuire setPayConfigBean(PayConfigBean payConfigBean) {
        this.payConfigBean = payConfigBean;
        bindingView.viewPayChannel.setPayListConfig(payConfigBean, mActivity);
        return this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * 设置标题
     * @param title
     * @return
     */
    public PayWayQuire setTitleText(String title) {
        if(null!=bindingView) bindingView.tvTitle.setText(title);
        return this;
    }


    public interface OnPayChanlChangedListener{
        void onPayChanlChanged(int chanl);
    }

    private OnPayChanlChangedListener mOnPayChanlChangedListener;

    public PayWayQuire setOnPayChanlChangedListener(OnPayChanlChangedListener onPayChanlChangedListener) {
        mOnPayChanlChangedListener = onPayChanlChangedListener;
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public PayWayQuire setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public PayWayQuire setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
        this.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        return this;
    }
}
