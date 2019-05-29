package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.bean.ServerListBean;
import com.yc.liaolive.databinding.DialogQuireServerLayoutBinding;
import com.yc.liaolive.ui.adapter.ServerListAdapter;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.util.Utils;

/**
 * TinyHung@Outlook.com
 * 2018/10/31
 * 客服问询
 */

public class QuireServerDialog extends BaseDialog<DialogQuireServerLayoutBinding> {

    public static QuireServerDialog getInstance(Activity context) {
        return new QuireServerDialog(context);
    }

    public QuireServerDialog(@NonNull Activity context) {
        super(context,R.style.CenterDialogAnimationStyle);
        setContentView(R.layout.dialog_quire_server_layout);
        Utils.setDialogWidth(this);
    }

    @Override
    public void initViews() {

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_close:
                    case R.id.btn_finlish:
                        QuireServerDialog.this.dismiss();
                        break;
                }
            }
        };
        bindingView.btnClose.setOnClickListener(onClickListener);
        bindingView.btnFinlish.setOnClickListener(onClickListener);
        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ServerListAdapter adapter;
        if(null!=UserManager.getInstance().getServer()){
            adapter = new ServerListAdapter(UserManager.getInstance().getServer().getServer_list());
        }else{
            adapter = new ServerListAdapter(null);
        }
        adapter.setOnCopyListener(new ServerListAdapter.OnCopyListener() {
            @Override
            public void onCopy(ServerListBean data) {
                if(null!=data){
                    Utils.copyString(getActivity(),data.getIdentify());
                    bindingView.llCopy.setVisibility(View.INVISIBLE);
                    bindingView.llCopyFinlish.setVisibility(View.VISIBLE);
                    bindingView.tvTips.setText("复制成功！");
                    bindingView.btnFinlish.setText("确定");
                }
            }
        });
        bindingView.recyclerView.setAdapter(adapter);
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public QuireServerDialog setTitle(String title){
        if(null!=bindingView){
            bindingView.tvTitle.setText(title);
        }
        return this;
    }

    /**
     * 是否允许按下返回键关闭弹窗
     * @param isCancelable
     * @return
     */
    public QuireServerDialog setDialogCancelable(boolean isCancelable){
        this.setCancelable(isCancelable);
        return this;
    }

    /**
     * 是否允许触摸边界关闭此弹窗
     * @param isCanceledOnTouchOutside
     * @return
     */
    public QuireServerDialog setDialogCanceledOnTouchOutside(boolean isCanceledOnTouchOutside){
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
        public void onStartUserCenter() {}
    }

    private OnSubmitClickListener mOnSubmitClickListener;

    public QuireServerDialog setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        mOnSubmitClickListener = onSubmitClickListener;
        return this;
    }
}
