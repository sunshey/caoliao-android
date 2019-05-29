package com.yc.liaolive.media.ui.fragment;

import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.FragmentVideoLiveBinding;

/**
 * TinyHung@Outlook.com
 * 2018/5/25
 * 首页-抢聊
 * onVisible 一级界面切换延时处理提升速度
 * onResume 二级界面切换应立即销毁播放器
 */

public class IndexTestVideoFragment extends BaseFragment<FragmentVideoLiveBinding,RxBasePresenter> {

    @Override
    protected void initViews() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_test;
    }
}
