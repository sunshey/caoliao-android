package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.bean.RechargeFillInfo;
import com.yc.liaolive.databinding.DialogTodayWaterLayoutBinding;
import com.yc.liaolive.live.listener.OnSpannableUserClickListener;
import com.yc.liaolive.live.util.LiveChatUserGradleSpan;
import com.yc.liaolive.ui.contract.WaterDetailContract;
import com.yc.liaolive.ui.presenter.WaterDetailPresenter;
import com.yc.liaolive.user.ui.DiamondDetailsActivity;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/6/13
 * 今日明细
 */

public class TodayWaterDialog extends BaseDialog<DialogTodayWaterLayoutBinding> implements WaterDetailContract.View {

    private String mHomtUserID;
    private WaterDetailPresenter mPresenter;

    public static TodayWaterDialog newInstance(Activity context,String homtUserID) {
        TodayWaterDialog dialog=new TodayWaterDialog(context,homtUserID);
        return dialog;
    }

    public TodayWaterDialog(@NonNull Activity context,String homeUserID) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_today_water_layout);
        this.mHomtUserID=homeUserID;
        Utils.setDialogWidth(this);
        mPresenter = new WaterDetailPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserWaterDetsils(mHomtUserID);
    }

    @Override
    public void initViews() {
        bindingView.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodayWaterDialog.this.dismiss();
            }
        });
        String text="查看明细";
        SpannableString spannableString = LiveChatUserGradleSpan.stringFormat(text, "查看明细",bindingView.tvSubTitle, Color.parseColor("#00A0E9"),true,new OnSpannableUserClickListener() {
            @Override
            public void onClick(String userID) {
                DiamondDetailsActivity.start(getContext(),"4");
            }
        });
        bindingView.tvSubTitle.setText(spannableString);
    }

    /**
     * 设置附属的信息
     * @param datas item内容数量
     */
    public void setItemData(long[] datas){
        if(null!=bindingView&& datas.length==3){
            bindingView.tvContent1.setText(Html.fromHtml("今日获得：<font color='#FA4D77'>"+datas[0]+"</font>"));
            bindingView.tvContent2.setText(Html.fromHtml("今日充值：<font color='#FA4D77'>"+datas[1]+"</font>"));
            bindingView.tvContent3.setText(Html.fromHtml("今日送出：<font color='#FA4D77'>"+datas[2]+"</font>"));
        }
    }

    public TodayWaterDialog setContentTitle(String title) {
        if(null!=bindingView) bindingView.tvTitle.setText(title);
        return this;
    }


    @Override
    public void showErrorView() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showUserDetsilsResult(RechargeFillInfo info) {
        if(null!=bindingView){
            try {
                bindingView.tvSubTitle.setVisibility(View.VISIBLE);
                setItemData(new long[]{info.getAccept_total(),info.getCharge_total(),info.getGiving_total()});
            }catch (Exception e){
            }
        }
    }

    @Override
    public void showUserDetsilsError(int code, String msg) {

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(null!=mPresenter) mPresenter.detachView(); mPresenter=null;
        mHomtUserID=null;
    }

    public interface OnSubmitClickListener{
        void onSubmit();
    }

    private OnSubmitClickListener mOnSubmitClickListener;

    public TodayWaterDialog setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
        return this;
    }
}
