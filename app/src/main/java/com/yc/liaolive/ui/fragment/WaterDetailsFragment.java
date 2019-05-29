package com.yc.liaolive.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.FragmentWaterDetailsBinding;

/**
 * TinyHung@Outlook.com
 * 2018/7/1
 * 明细详情
 */

public class WaterDetailsFragment extends BaseFragment<FragmentWaterDetailsBinding,RxBasePresenter> {

    private int mWaterType;//0:今日 1：全部

    public static WaterDetailsFragment newInstance(int waterType) {
        WaterDetailsFragment fragment=new WaterDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("waterType",waterType);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mWaterType = arguments.getInt("waterType");
        }
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_water_details;
    }

}
