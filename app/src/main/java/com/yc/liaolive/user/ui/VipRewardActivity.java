package com.yc.liaolive.user.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.base.TopBaseActivity;
import com.yc.liaolive.databinding.ActivityVipRewardBinding;
import com.yc.liaolive.recharge.model.bean.VipListItem;
import com.yc.liaolive.user.adapter.VipRewardListAdapter;
import com.yc.liaolive.view.widget.IndexGridLayoutManager;

import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2019/2/22
 * VIP奖励
 */

public class VipRewardActivity extends TopBaseActivity {

    private ActivityVipRewardBinding bindingView;
    private VipRewardListAdapter mAdapter;

    public static void start() {
        Intent intent=new Intent(AppEngine.getInstance().getApplication().getApplicationContext(), VipRewardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppEngine.getInstance().getApplication().getApplicationContext().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_vip_reward);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int layoutWidth = initLayoutParams();
        bindingView.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bindingView.recyclerView.setLayoutManager(new IndexGridLayoutManager(VipRewardActivity.this,4,IndexGridLayoutManager.VERTICAL,false));
        List<VipListItem> listCoin = AppEngine.getInstance().getListCoin();
//        List<VipListItem> itemList= DataFactory.createVipRewardList();
        mAdapter = new VipRewardListAdapter(listCoin,layoutWidth);
        bindingView.recyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onNewIntent(Intent intent) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mAdapter) mAdapter.setNewData(null);
        AppEngine.getInstance().setVipListCoin(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(1011, intent);
        finish();
    }
}