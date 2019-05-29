package com.yc.liaolive.recharge.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.yc.liaolive.R;
import com.yc.liaolive.recharge.model.bean.PayConfigBean;
import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/10/16
 * 支付渠道选择
 */

public class PayChanlSelectedLayout extends RadioGroup{

    private static final String TAG = "PayChanlSelectedLayout";
    private int mChannel = 1;//0：支付宝 1：微信
    private PayConfigBean payConfigBean;
    private PayConfigBean.ZhifubaoTipsBean alipayTips;
    private Activity mActivity;
    private boolean isResponseListenser = true; //是否响应监听

    public PayChanlSelectedLayout(Context context) {
        this(context,null);
    }

    public PayChanlSelectedLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        this.setDividerDrawable(getResources().getDrawable(R.drawable.driver_line));
        this.setDividerDrawable(null);
        this.setBackground(null);

        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if ("wxpay".equals(payConfigBean.getPay_list().get(checkedId).getItem())) {
                    //0未启用 1启用 #state字段废弃
                    if (alipayTips != null &&!TextUtils.isEmpty(alipayTips.getTxt()) && isResponseListenser) {
                        showAlipayTipsDialog(alipayTips.getTxt(), group);
                    } else {
                        setPayChannel(1);
                    }
                } else if ("alipay".equals(payConfigBean.getPay_list().get(checkedId).getItem())) {
                    setPayChannel(0);
                } else {
                    setPayChannel(-1);
                }
                if (!isResponseListenser) {
                    isResponseListenser = true;
                }
            }
        });
    }

    private void showAlipayTipsDialog (String content, final RadioGroup group) {
        QuireDialog.getInstance(mActivity).setTitleText("温馨提示")
                .showCloseBtn(false)
                .setContentText(content)
                .setCancelTitleText("放弃优惠")
                .setSubmitTitleText("使用优惠")
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        super.onConsent();
                        //弹窗消失，选中支付宝支付
                        isResponseListenser = false;
                        ((RadioButton)group.findViewWithTag("alipay")).setChecked(true);
                    }

                    @Override
                    public void onRefuse() {
                        super.onRefuse();
                        //弹窗消失，选中微信支付
                        setPayChannel(1);
                    }
                }).setDialogCancelable(false)
                .setDialogCanceledOnTouchOutside(false).show();
    }

    public void setPayListConfig (PayConfigBean payConfigBean, Activity mActivity) {
        if (payConfigBean == null || mActivity == null || mActivity.isFinishing()) {
            return;
        }
        removeAllViews();
        this.mActivity = mActivity;
        this.payConfigBean = payConfigBean;
        if (payConfigBean.getZhifubao_tips() != null && payConfigBean.getZhifubao_tips().size() > 0) {
            this.alipayTips = payConfigBean.getZhifubao_tips().get(0);
        }
        String defaultItem = payConfigBean.getDefault_pay();
        int left15 = Utils.dip2px(16);
        int padding12 = Utils.dip2px(12);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(64));
        for (int i = 0; i < payConfigBean.getPay_list().size(); i ++) {
            PayConfigBean.PayConfigItemBean itemBean = payConfigBean.getPay_list().get(i);
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setId(i);
            radioButton.setTag(itemBean.getItem());
            radioButton.setButtonDrawable(null);
            radioButton.setBackground(null);
            if(!TextUtils.isEmpty(itemBean.getDescription())){
                radioButton.setText(Html.fromHtml("<font>"+itemBean.getPay_title()+"<br></font><font color='#FF6666'><small>"+itemBean.getDescription()+"</small></font>"));
            }else{
                radioButton.setText(itemBean.getPay_title());
            }
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            radioButton.setTextColor(getResources().getColor(R.color.colorContent));
            radioButton.setGravity(Gravity.CENTER_VERTICAL);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(getIcon(itemBean.getItem())), null ,
                    getResources().getDrawable(R.drawable.vip_pay_selector), null);
            radioButton.setCompoundDrawablePadding(padding12);
            radioButton.setPadding(left15, padding12, left15, padding12);
            if(!TextUtils.isEmpty(defaultItem)&&defaultItem.equals(itemBean.getItem())){
                isResponseListenser = false;
                radioButton.setChecked(true);
                //默认选中
                if ("wxpay".equals(defaultItem)) {
                    setPayChannel(1);
                } else if ("alipay".equals(defaultItem)) {
                    setPayChannel(0);
                }
            }

            this.addView(radioButton, params);
        }
    }

    private int getIcon (String item) {
        int res;
        switch (item) {
            case "wxpay":
                res = R.drawable.ic_pay_wx;
                break;
            case "alipay":
                res = R.drawable.ic_pay_zfb;
                break;
            default:
                res = R.drawable.ic_pay_zfb;
                break;
        }
        return res;
    }

    public void setPayChannel(int chanl){
        this.mChannel =chanl;
        if(null != mOnPayChanlChangedListener) mOnPayChanlChangedListener.onPayChanlChanged(chanl);
    }

    public int getPayChannel() {
        return mChannel;
    }

    public interface OnPayChanlChangedListener{
        void onPayChanlChanged(int chanl);
    }

    private OnPayChanlChangedListener mOnPayChanlChangedListener;

    public void setOnPayChanlChangedListener(OnPayChanlChangedListener onPayChanlChangedListener) {
        mOnPayChanlChangedListener = onPayChanlChangedListener;
    }

    public void onDestry(){
        removeAllViews();
    }
}
