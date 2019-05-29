package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogIdentityDateSelectBinding;

/**
 * Created by wanglin  on 2018/7/7 16:55.
 */
public class AuthenticationDialog extends BaseDialog<DialogIdentityDateSelectBinding> {
    public AuthenticationDialog(@NonNull Activity context) {
        super(context);

        setContentView(R.layout.dialog_identity_date_select);
    }

    @Override
    public void initViews() {
        bindingView.tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelectDate();
                }
            }
        });
        bindingView.tvForever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelectForever();
                }
                dismiss();
            }
        });
    }


    private onSelectDateListener listener;

    public void setOnSelectDateListener(onSelectDateListener listener) {
        this.listener = listener;
    }

    public interface onSelectDateListener {
        void onSelectDate();

        void onSelectForever();
    }

}
