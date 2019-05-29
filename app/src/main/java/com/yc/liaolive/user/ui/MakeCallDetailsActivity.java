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
import com.yc.liaolive.contants.NetContants;
import com.yc.liaolive.databinding.ActivityDiamondDetailsBinding;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import com.yc.liaolive.ui.adapter.CallNotesListAdapter;
import com.yc.liaolive.user.ui.fragment.CallNotesListFragment;
import com.yc.liaolive.util.StatusUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/1/25
 * 通话、预约详情
 */

public class MakeCallDetailsActivity extends BaseActivity<ActivityDiamondDetailsBinding>{

    public static final String TYPE_CALL="0";//通话
    public static final String TYPE_MAKE="1";//预约
    private String mShowIndex="0";
    private String activityType=TYPE_CALL;

    /**
     * @param context
     * @param activityType 0：通话 1：预约
     * @param showIndex 默认显示的界面位置
     */
    public static void start(Context context, String activityType,String showIndex) {
        Intent intent = new Intent(context, MakeCallDetailsActivity.class);
        intent.putExtra("activityType", activityType);
        intent.putExtra("showIndex", showIndex);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(null!=intent) {
            mShowIndex = intent.getStringExtra("showIndex");
            activityType = intent.getStringExtra("activityType");
            if(TextUtils.isEmpty(mShowIndex)){
                mShowIndex="0";
            }
        }

        setContentView(R.layout.activity_diamond_details);
        StatusUtils.setStatusTextColor1(true,this);
    }

    @Override
    public void initViews() {
        bindingView.tvTips.setVisibility(View.GONE);
        bindingView.statusBar19.setVisibility(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP ? View.VISIBLE : View.GONE);
        bindingView.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        List<Fragment> fragments=new ArrayList<>();
        List<String> titles=new ArrayList<>();
        if(activityType.equals(TYPE_CALL)){
            //通话
            titles.add("未接来电");
            titles.add("全部来电");
            fragments.add(CallNotesListFragment.newInstance(NetContants.getInstance().URL_GET_CALL_LET_LIST(),CallNotesListAdapter.ITEM_TYPE_LET,0,0));
            fragments.add(CallNotesListFragment.newInstance(NetContants.getInstance().URL_GET_CALL_LET_LIST(),CallNotesListAdapter.ITEM_TYPE_LET,1,-1));
        }else if(activityType.equals(TYPE_MAKE)){
            //预约
            titles.add("预约中");
            titles.add("全部");
            fragments.add(CallNotesListFragment.newInstance(NetContants.getInstance().URL_RESEVER_LIST(),CallNotesListAdapter.ITEM_TYPE_MAKE,0,0));
            fragments.add(CallNotesListFragment.newInstance(NetContants.getInstance().URL_RESEVER_LIST(), CallNotesListAdapter.ITEM_TYPE_MAKE,1,-1));
        }
        AppFragmentPagerAdapter myAppFragmentPagerAdapter =new AppFragmentPagerAdapter(getSupportFragmentManager(),fragments,titles);
        bindingView.viewPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(2);
        bindingView.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
        int parseInt = Integer.parseInt(mShowIndex);
        bindingView.viewPager.setCurrentItem(parseInt >=fragments.size()?fragments.size()-1: parseInt);
        bindingView.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void initData() { }

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