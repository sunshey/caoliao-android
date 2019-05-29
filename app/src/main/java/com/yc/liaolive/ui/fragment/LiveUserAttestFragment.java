package com.yc.liaolive.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.FragmentAttestUserBinding;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.manager.ApplicationManager;
import com.yc.liaolive.user.ui.UserAuthenticationActivity;
import java.util.Observable;
import java.util.Observer;

/**
 * TinyHung@Outlook.com
 * 2018/5/24
 * 用户实名认证
 */

public class LiveUserAttestFragment extends BaseFragment <FragmentAttestUserBinding,RxBasePresenter> implements Observer {

    @Override
    protected void initViews() {
        bindingView.btnAttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是0，继续，其他状态不可用
                if(UserManager.getInstance().isUncertified()){
                    startActivity(new Intent(getActivity(),UserAuthenticationActivity.class));
                }
            }
        });
        bindingView.tvAttestTitle.setText("成为主播");
        bindingView.tvAttestDesp.setText("让你的时间为他人创造更多价值");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplicationManager.getInstance().addObserver(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_attest_user;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().removeObserver(this);
    }

    /**
     * 认证成功，关闭自己
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if(null!=arg&&arg instanceof String&&"authentication".equalsIgnoreCase((String) arg)){
            if(null!=getActivity()){
                getActivity().finish();
            }
        }
    }
}
