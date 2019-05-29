package com.yc.liaolive.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.databinding.ActivityTableListBinding;
import com.yc.liaolive.ui.fragment.IntegralTopListFragment;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/22
 * 积分榜单
 */

public class IntegralTopListActivity extends BaseActivity<ActivityTableListBinding> {

    public static void start(Context context,String homeUserID) {
        Intent intent = new Intent(context, IntegralTopListActivity.class);
        intent.putExtra("to_userid",homeUserID);
        context.startActivity(intent);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setTitle("亲密度");
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(IntegralTopListFragment.newInstance(getIntent().getStringExtra("to_userid"),1));
        fragments.add(IntegralTopListFragment.newInstance(getIntent().getStringExtra("to_userid"),0));
        List<String> titles=new ArrayList<>();
        titles.add("日榜");
        titles.add("总榜");
        AppFragmentPagerAdapter myAppFragmentPagerAdapter =new AppFragmentPagerAdapter(getSupportFragmentManager(),fragments,titles);
        bindingView.viewPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(2);
        bindingView.tabLayout.setTabMode(TabLayout.GRAVITY_CENTER);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.setCurrentItem(1);

        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                onBackPressed();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);
        if(null==getIntent().getStringExtra("to_userid")){
            ToastUtils.showCenterToast("参数错误！");
            finish();
        }
    }
}
