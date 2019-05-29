package com.yc.liaolive.index.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.databinding.FragmentIndexGroupVideoBinding;
import com.yc.liaolive.index.manager.IndexFragController;
import com.yc.liaolive.ui.adapter.AppFragmentPagerAdapter;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/9/25
 * 视频、图片、ASMR视频、ASMR音频及其他模块的父容器
 */

public class IndexVideoGroupFragment extends BaseFragment <FragmentIndexGroupVideoBinding,RxBasePresenter>{

    private static final String TAG = "IndexVideoGroupFragment";
    private List<Fragment> mFragments;
    private IndexFragment mParentFragment;
    private int mIndexGroup;
    private int index;
    private String mTargetID;

    /**
     * @param groupFileType 文件容器类型 0：照片 1：小视频
     * @param indexGroup 父Prant所在的坐标
     * @return
     */
    public static Fragment newInstance(int indexGroup, int index, int groupFileType, String targetID) {
        IndexVideoGroupFragment fragment=new IndexVideoGroupFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("groupFileType",groupFileType);
        bundle.putInt("indexGroup",indexGroup);
        bundle.putInt("index",index);
        bundle.putString("targetID",targetID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mIndexGroup = arguments.getInt("indexGroup");
            index = arguments.getInt("index");
            //默认是视频容器
            mTargetID = arguments.getString("targetID","13");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            MobclickAgent.onPageStart("main_group_"+mTargetID);
            MobclickAgent.onEvent(getActivity(), "main_group_"+mTargetID);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(null!=mFragments && mFragments.size()>0){
                Fragment fragment = mFragments.get(0);
                if(fragment instanceof IndexVideoListFragment){
                    ((IndexVideoListFragment) fragment).showVisible();
                }
            }
            MobclickAgent.onPageStart("main_group_"+mTargetID);
            MobclickAgent.onEvent(getActivity(), "main_group_"+mTargetID);
        } else if (isResumed()) {
            MobclickAgent.onPageEnd("main_group_"+mTargetID);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            MobclickAgent.onPageEnd("main_group_"+mTargetID);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index_group_video;
    }

    @Override
    protected void initViews() {
        //子界面实例化
        mFragments = IndexFragController.getInstance().getSubFragments(mIndexGroup, index);
        List<String> titles = IndexFragController.getInstance().getSubTitles();
        AppFragmentPagerAdapter myAppFragmentPagerAdapter =new AppFragmentPagerAdapter(getChildFragmentManager(), mFragments,titles);
        bindingView.indexVideoPager.setAdapter(myAppFragmentPagerAdapter);
        bindingView.indexVideoPager.setOffscreenPageLimit(mFragments.size());
        bindingView.tabLayout.setTabMode(TabLayout.GRAVITY_CENTER);
        bindingView.tabLayout.setupWithViewPager(bindingView.indexVideoPager);
        int subDefaultIndex = IndexFragController.getInstance().getSubDefaultIndex();
        if(subDefaultIndex<=0){
            subDefaultIndex=0;
        }
        if(subDefaultIndex>(mFragments.size()-1)){
            subDefaultIndex=(mFragments.size()-1);
        }
        bindingView.indexVideoPager.setCurrentItem(subDefaultIndex);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentFragment = (IndexFragment) getParentFragment();
    }

    /**
     * 通知首页消费手势滑动事件
     * @param flag
     */
    public void showMainTabLayout(boolean flag) {
        if(null!=mParentFragment) mParentFragment.showMainTabLayout(flag);
    }

    @Override
    protected void fromMainUpdata() {
        super.fromMainUpdata();
        if(null==bindingView) return;
        if(null!=mFragments&&mFragments.size()>bindingView.indexVideoPager.getCurrentItem()){
            Fragment fragment = mFragments.get(bindingView.indexVideoPager.getCurrentItem());
            if(null!=fragment&&fragment instanceof IndexVideoListFragment){
                ((IndexVideoListFragment) fragment).fromMainUpdata();
            }
        }
    }
}