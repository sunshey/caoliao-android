package com.yc.liaolive.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.databinding.ActivityGetIntegralBinding;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * Created by wanglin  on 2018/7/3 20:45.
 */
public class IntegralGetActivity extends BaseActivity<ActivityGetIntegralBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_integral);
    }

    @Override
    public void initViews() {
        bindingView.commentTitleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }
}
