package com.yc.liaolive.user.ui;

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
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityAttachFirendBinding;
import com.yc.liaolive.user.ui.fragment.AttachFirendListFragment;
import com.yc.liaolive.util.StatusUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/6/12
 * 用户相关的人物关系
 */

public class AttachFirendActivity extends BaseActivity<ActivityAttachFirendBinding>{

    private int mIndex;
    /**
     * 和用户相关的分发
     * @param context
     * @param index 显示的Index
     * @param userID
     */
    public static void start(Context context,int index,String userID){
        Intent intent=new Intent(context,AttachFirendActivity.class);
        intent.putExtra("index",index);
        intent.putExtra("userID",userID);
        context.startActivity(intent);
    }

    @Override
    public void initViews() {
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(AttachFirendListFragment.newInstance(getIntent().getStringExtra("userID"), NetContants.getInstance().URL_FOLLOW(),0));
        fragments.add(AttachFirendListFragment.newInstance(getIntent().getStringExtra("userID"),NetContants.getInstance().URL_FANS(),1));
        List<String> titles=new ArrayList<>();
        titles.add("关注");
        titles.add("粉丝");
//        titles.add("好友");
        AppFragmentPagerAdapter myAppFragmentPagerAdapter =new AppFragmentPagerAdapter(getSupportFragmentManager(),fragments,titles);
        bindingView.viewPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(2);
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        bindingView.viewPager.setCurrentItem(mIndex>=fragments.size()?0:mIndex);
        bindingView.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(null!=intent) {
            mIndex = intent.getIntExtra("index", 0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_firend);
        StatusUtils.setStatusTextColor1(true,this);
    }

    /**
     * 返回当前显示的界面
     * @return
     */
    public int getCurrentItem() {
        return bindingView.viewPager.getCurrentItem();
    }
}