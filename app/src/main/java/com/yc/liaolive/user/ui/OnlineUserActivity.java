package com.yc.liaolive.user.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseActivity;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.databinding.ActivityOnlineUserBinding;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CommentTitleView;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线用户列表界面
 * Created by yangxueqin on 18/12/14.
 */

public class OnlineUserActivity extends BaseActivity<ActivityOnlineUserBinding> {

    private ViewPager viewPager;

    private XTabLayout mTabLayout;

    private String[] tabTitle = new String[]{"在线用户", "在线VIP用户"};

    /**
     * 类型。0在线用户 1在线VIP用户
     */
    private int[] type = new int[]{0, 1};

    private int POSITION = 0;//默认选中的tab

    private UserFragmentAdapter adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_user);
        String type = getIntent().getStringExtra("show_type");
        POSITION = TextUtils.isEmpty(type) ? 0 : Integer.parseInt(type);
        EventBus.getDefault().register(this);
    }

    @Override public void initViews() {
        bindingView.titleView.setTitle("用户列表");
        viewPager = bindingView.viewPager;
        mTabLayout = bindingView.mTabLayout;
        adpter = new UserFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adpter);
        viewPager.setOffscreenPageLimit(type.length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                POSITION = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setCurrentItem(POSITION);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setTabMode(XTabLayout.MODE_FIXED);
        mTabLayout.getTabAt(0).setCustomView(getTabView(tabTitle[0], 0));
        mTabLayout.getTabAt(1).setCustomView(getTabView(tabTitle[1], 1));
        mTabLayout.addOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(XTabLayout.Tab tab) {
                changeTabSelect(tab);
            }

            @Override public void onTabUnselected(XTabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            @Override public void onTabReselected(XTabLayout.Tab tab) {

            }
        });

        bindingView.titleView.setOnTitleClickListener(new CommentTitleView.OnTitleClickListener() {
            @Override
            public void onBack(View v) {
                super.onBack(v);
                onBackPressed();
            }
        });
    }

    private View getTabView(String title, int position) {
        TextView textView = new TextView(getContext());
        textView.setWidth(Utils.dip2px(80));
        textView.setHeight(Utils.dip2px(28));
        textView.setTextSize(11);
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);

        //确定是左边还是右边
        textView.setBackgroundResource(0 == position ?
                R.drawable.online_user_left_selector : R.drawable.online_user_right_selector);
        //确定默认选中的项,默认显示用户打开界面显示的那一项
        if(position == POSITION){
            textView.setSelected(true);
            textView.setTextColor(Color.parseColor("#ffffff"));
        }else{
            textView.setSelected(false);
            textView.setTextColor(Color.parseColor("#ff7575"));
        }
        return textView;
    }

    private void changeTabSelect(XTabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view != null) {
            view.setSelected(true);
            ((TextView)view).setTextColor(Color.parseColor("#ffffff"));
        }
    }

    private void changeTabNormal(XTabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view != null) {
            view.setSelected(false);
            ((TextView)view).setTextColor(Color.parseColor("#ff7575"));
        }
    }

    @Override public void initData() {

    }

    class UserFragmentAdapter extends FragmentStatePagerAdapter {
        private List<BaseFragment> fragments;

        public UserFragmentAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            for (int t : type) {
                BaseFragment fragment = new OnlineUserFragment().newInstance(t);
                fragments.add(fragment);
            }
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
