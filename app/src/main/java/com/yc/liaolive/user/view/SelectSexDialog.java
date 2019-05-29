package com.yc.liaolive.user.view;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import com.kaikai.securityhttp.utils.LogUtil;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;
import com.yc.liaolive.databinding.DialogEditSexBinding;
import com.yc.liaolive.user.manager.UserManager;

/**
 * Created by wanglin  on 2018/7/5 20:41.
 */
public class SelectSexDialog extends BaseDialog<DialogEditSexBinding> {

    private int sexId = 0;//0 男 1 女
    private int count = 0;

    public SelectSexDialog(@NonNull Activity context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        getWindow().setWindowAnimations(R.style.CenterAnimation);
        setContentView(R.layout.dialog_edit_sex);
    }

    @Override
    public void initViews() {
        if (UserManager.getInstance().getSex() == 0) {
            bindingView.btnRadioSexMan.setChecked(true);
        } else {
            bindingView.btnRadioSexWomen.setChecked(true);
        }


        bindingView.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn_radio_sex_man) {//选择男
                    sexId = 0;
                } else if (checkedId == R.id.btn_radio_sex_women) {//选择女
                    sexId = 1;
                }
                count++;
                LogUtil.msg("count:  " + count);
            }
        });

        bindingView.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectSexDialog.this.dismiss();
            }
        });

        bindingView.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelectSext(sexId);
                }
                SelectSexDialog.this.dismiss();
            }
        });
    }

    private onSelectSexListener listener;

    public void setOnSelectSexListener(onSelectSexListener listener) {
        this.listener = listener;
    }

    public interface onSelectSexListener {
        void onSelectSext(int sexId);
    }
}
