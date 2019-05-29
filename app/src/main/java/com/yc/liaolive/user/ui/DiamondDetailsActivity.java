package com.yc.liaolive.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.databinding.ActivityDiamondDetailsBinding;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.user.ui.fragment.DiamondDetailFragment;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.StatusUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 钻石详情
 */

public class DiamondDetailsActivity extends BaseActivity<ActivityDiamondDetailsBinding>{

    public static final String TYPE_ID="typeId";
    private static final String TAG = "DiamondDetailsActivity";
    private int mIndex;
    private String mTypeID;

    /**
     * @param context
     * @param typeId 3：积分 4：钻石
     */
    public static void start(Context context, String typeId) {
        Intent intent = new Intent(context, DiamondDetailsActivity.class);
        intent.putExtra(TYPE_ID, typeId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(null!=intent) {
            mTypeID = intent.getStringExtra(TYPE_ID);
            mIndex = intent.getIntExtra("index", 0);
        }
        setContentView(R.layout.activity_diamond_details);
        StatusUtils.setStatusTextColor1(true,this);
    }

    /**
     * 0：全部 1：支出 2：收入
     */
    @Override
    public void initViews() {
        bindingView.statusBar19.setVisibility(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP ? View.VISIBLE : View.GONE);
        bindingView.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(DiamondDetailFragment.newInstance(0,mTypeID));
        fragments.add(DiamondDetailFragment.newInstance(1,mTypeID));
        fragments.add(DiamondDetailFragment.newInstance(2,mTypeID));
        List<String> titles=new ArrayList<>();
        titles.add("全部");
        titles.add("支出");
        titles.add("收入");
        AppFragmentPagerAdapter myAppFragmentPagerAdapter =new AppFragmentPagerAdapter(getSupportFragmentManager(),fragments,titles);
        bindingView.viewPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(3);
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.setCurrentItem(mIndex>=fragments.size()?0:mIndex);
    }

    @Override
    public void initData() {}

    /**
     * 提示描述
     * @param desp
     */
    public void setDesp(String desp){
        if(null!=bindingView) {
            bindingView.tvTips.setText(desp);
            bindingView.tvTips.setVisibility(TextUtils.isEmpty(desp)?View.GONE:View.VISIBLE);
        }
    }
}