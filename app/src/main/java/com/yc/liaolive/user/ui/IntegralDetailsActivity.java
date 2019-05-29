package com.yc.liaolive.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.androidkun.xtablayout.XTabLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.bean.DiamondInfo;
import com.yc.liaolive.databinding.ActivityIntegralDetailsBinding;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.user.ui.fragment.DiamondDetailFragment;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.util.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 积分详情
 */

public class IntegralDetailsActivity extends BaseActivity<ActivityIntegralDetailsBinding>{

    public static final String TYPE_ID="typeId";
    private static final String TAG = "IntegralDetailsActivity";
    private int mIndex;
    private String mTypeID;
    private int mTopBarHeight;

    /**
     * @param context
     * @param typeId 3：积分 4：钻石
     */
    public static void start(Context context, String typeId) {
        Intent intent = new Intent(context, IntegralDetailsActivity.class);
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
        setContentView(R.layout.activity_integral_details);
    }

    /**
     * 0：全部 1：支出 2：收入
     */
    @Override
    public void initViews() {
        bindingView.toolBarTitle.setText(getResources().getString(R.string.my_transaction));
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

        XTabLayout tablayout = bindingView.tabLayout;
        for (int i = 0; i < myAppFragmentPagerAdapter.getCount(); i++) {
            XTabLayout.Tab tab = tablayout.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.tab_item);//给每一个tab设置view
            TextView textView = tab.getCustomView().findViewById(R.id.tab_text);
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                textView.setSelected(true);//第一个tab被选中
            }
            textView.setText(titles.get(i));//设置tab上的文字
        }
        tablayout.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tab_text).setSelected(true);
                bindingView.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tab_text).setSelected(false);
            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
            }

        });
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        bindingView.collapseToolbar.measure(width, width);
        mTopBarHeight = bindingView.collapseToolbar.getMeasuredHeight()-ScreenUtils.dpToPxInt(48f);
        bindingView.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int abs = Math.abs(verticalOffset);
                if (abs >= (appBarLayout.getTotalScrollRange() - Utils.dip2px(5)) || verticalOffset == 0) {
                    bindingView.toolBarTitle.setVisibility(View.VISIBLE);
                } else {
                    bindingView.toolBarTitle.setVisibility(View.GONE);
                }
                //选项卡底部影影，仅在完全停靠后生效
                if(abs>=mTopBarHeight){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bindingView.barTitleView.setElevation(ScreenUtils.dpToPxInt(3f));
                    }
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bindingView.barTitleView.setElevation(ScreenUtils.dpToPxInt(0f));
                    }
                }
            }
        });
    }

    @Override
    public void initData() {}

    /**
     * 积分详情
     * @param info
     */
    public void setDesp(DiamondInfo info) {
        if(null!=bindingView&&TextUtils.isEmpty(bindingView.tvIntegralNum.getText().toString())){
            bindingView.tvIntegralTitle.setVisibility(View.VISIBLE);
            bindingView.tvIntegralNum.setNumberWithAnim((int) info.getPoints());
        }
    }
}