package com.yc.liaolive.ui.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseDialog;

/**
 * Created by wanglin  on 2018/7/6 15:32.
 */
public class ReportUserDialog  extends BaseDialog{
    public ReportUserDialog(@NonNull Activity context) {
        super(context);
        setContentView(R.layout.dialog_report);
    }

    @Override
    public void initViews() {

    }
}
