package com.yc.liaolive.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.databinding.ActivityNotecaseBinding;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.view.widget.CommentTitleView;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 我的钱包
 */

public class NotecaseActivity extends BaseActivity<ActivityNotecaseBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notecase);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                onBackPressed();
            }
        });
        bindingView.tvDiamond.setText(String.valueOf(UserManager.getInstance().getDiamonds()));
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_recharge:
                        VipActivity.start(NotecaseActivity.this,0);
                        break;
                    case R.id.btn_diamond_details:
                        DiamondDetailsActivity.start(NotecaseActivity.this,"4");
                        break;
                }
            }
        };
        bindingView.btnRecharge.setOnClickListener(onClickListener);
        bindingView.btnDiamondDetails.setOnClickListener(onClickListener);
    }

    @Override
    public void initData() {
        UserManager.getInstance().getFullUserData(UserManager.getInstance().getUserId(), UserManager.getInstance().getUserId(), new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=bindingView){
                    bindingView.tvDiamond.setText(String.valueOf(UserManager.getInstance().getDiamonds()));
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
    }
}