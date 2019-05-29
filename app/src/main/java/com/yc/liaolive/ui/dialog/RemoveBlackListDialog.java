package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogRemoveBlacklistBinding;


/**
 * Created by wanglin  on 2018/7/4 20:05.
 */
public class RemoveBlackListDialog extends BaseDialog<DialogRemoveBlacklistBinding> {

    private String mNickName;

    public RemoveBlackListDialog(@NonNull Activity context, String nickName) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        this.mNickName = nickName;
        setContentView(R.layout.dialog_remove_blacklist);
    }

    @Override
    public void initViews() {

        bindingView.tvRemoveContent.setText(String.format(getContext().getString(R.string.remove_blacklist), mNickName));
        bindingView.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveBlackListDialog.this.dismiss();
            }
        });

        bindingView.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirm();
                }
                RemoveBlackListDialog.this.dismiss();
            }
        });
    }


    private onConfirmListener listener;

    public void setOnConfirmListener(onConfirmListener listener) {
        this.listener = listener;
    }

    public interface onConfirmListener {
        void onConfirm();
    }
}
