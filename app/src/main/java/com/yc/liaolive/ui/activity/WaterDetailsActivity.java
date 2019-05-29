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
import com.yc.liaolive.databinding.ActivityTotalWaterDetailsBinding;
import com.yc.liaolive.ui.fragment.WaterDetailsFragment;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.view.widget.CommentTitleView;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/7/1
 * 明细详情
 */

public class WaterDetailsActivity extends BaseActivity<ActivityTotalWaterDetailsBinding> {

    public static void start(Context context, String homeUserID) {
        Intent intent = new Intent(context, WaterDetailsActivity.class);
        intent.putExtra("homeUserID",homeUserID);
        context.startActivity(intent);
    }

    @Override
    public void initViews() {
        bindingView.titleView.setTitle("用户明细");
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(WaterDetailsFragment.newInstance(0));
        fragments.add(WaterDetailsFragment.newInstance(1));
        List<String> titles=new ArrayList<>();
        titles.add("今日明细");
        titles.add("总明细");
        AppFragmentPagerAdapter myAppFragmentPagerAdapter =new AppFragmentPagerAdapter(getSupportFragmentManager(),fragments,titles);
        bindingView.viewPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(2);
        bindingView.tabLayout.setTabMode(TabLayout.GRAVITY_CENTER);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.setCurrentItem(0);

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
        setContentView(R.layout.activity_total_water_details);
        if(null==getIntent().getStringExtra("homeUserID")){
            ToastUtils.showCenterToast("参数错误！");
            finish();
        }
    }
}
