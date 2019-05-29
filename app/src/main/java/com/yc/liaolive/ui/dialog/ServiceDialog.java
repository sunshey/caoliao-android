package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogServiceBinding;
import com.yc.liaolive.util.ToastUtils;


/**
 * Created by wanglin  on 2018/7/4 20:05.
 */
public class ServiceDialog extends BaseDialog<DialogServiceBinding> {

    private String qq;

    public ServiceDialog(@NonNull Activity context, String QQ) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        this.qq = QQ;
        getWindow().setWindowAnimations(R.style.CenterAnimation);
        setContentView(R.layout.dialog_service);
    }

    @Override
    public void initViews() {

        bindingView.tvQq.setText(String.format(getContext().getString(R.string.qq), qq));

        bindingView.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceDialog.this.dismiss();
            }
        });

        bindingView.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String urlQQ = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlQQ)));
                } catch (Exception e) {
                    ToastUtils.showCenterToast("打开QQ失败");
                }

                ServiceDialog.this.dismiss();
            }
        });
    }


}
