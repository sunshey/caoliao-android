package com.yc.liaolive.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.base.back.SwipeBackActivityBase;
import com.yc.liaolive.base.back.SwipeBackActivityHelper;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.ActivityBaseBinding;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.util.HotCityManager;
import com.yc.liaolive.util.ToastUtils;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.layout.SwipeBackLayout;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/3/19 14:51
 * 所有Activity的父类
 */

public abstract  class BaseActivity<SV extends ViewDataBinding> extends TopBaseActivity implements SwipeBackActivityBase {

    private static final String TAG = "BaseActivity";
    // 布局view
    protected SV bindingView;

    private SwipeBackActivityHelper mHelper;
    private boolean isTransparent;//是否全透明


    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    protected Context getContext(){
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        swipeBackLayout.setScrollThresHold(0.8f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 是否全透明
     */
    protected void setStatusBar(boolean flag) {
        this.isTransparent=flag;
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        ActivityBaseBinding baseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false);
        bindingView = DataBindingUtil.inflate(getLayoutInflater(), layoutResID, null, false);
        //content
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bindingView.getRoot().setLayoutParams(params);
        FrameLayout mContainer = (FrameLayout) baseBinding.getRoot().findViewById(R.id.container);
        mContainer.addView(bindingView.getRoot());
        getWindow().setContentView(baseBinding.getRoot());
        initViews();
        initData();
    }

    public abstract void initViews();
    public abstract void initData();

    protected void setPosition(String name) {}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(null!=mHelper) mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        if(null!=mHelper){
            View v = super.findViewById(id);
            if (v == null && mHelper != null)
                return mHelper.findViewById(id);
            return v;
        }
        return null;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        if(null!=mHelper){
            return mHelper.getSwipeBackLayout();
        }
        return null;
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    protected void updateUserSex() {

    }

    /**
     * 切换Fragment
     * @param id
     * @param fragment
     */
    public void replaceFragment(@IdRes int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(id, fragment).commitAllowingStateLoss();
    }

    /**
     * 切换Fragment
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    /**
     * 切换Fragment
     * @param fragment
     * @param title
     */

    public void replaceFragment(Fragment fragment,String title) {
        setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    /**
     * 改变当前Activity的透明度
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = bgAlpha;
        getWindow().setAttributes(layoutParams);
    }

    /**
     * 城市选择
     */
    protected void showCityList() {
        List<HotCity> hotCityList = new ArrayList<>();
        hotCityList.add(new HotCity("北京", "北京", "101010100"));
        hotCityList.add(new HotCity("上海", "上海", "101020100"));
        hotCityList.add(new HotCity("广州", "广东", "101280101"));
        hotCityList.add(new HotCity("深圳", "广东", "101280601"));
        hotCityList.add(new HotCity("武汉", "湖北", "101210401"));
        hotCityList.add(new HotCity("重庆", "重庆", "101210801"));
        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())//此方法必须调用
                .enableAnimation(true)    //启用动画效果
                .setAnimationStyle(R.style.DefaultCityPickerAnimation)    //自定义动画
                .setLocatedCity(new LocatedCity(UserManager.getInstance().getPosition(), UserManager.getInstance().getProvince(), ""))  //APP自身已定位的城市，默认为null（定位失败）
                .setHotCities(hotCityList)    //指定热门城市
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        if (data != null) {
                            HotCity city = new HotCity(data.getName(), data.getProvince(), "");
                            HotCityManager.saveCity(city);
                            setPosition(data.getName());
                            updataUserInfo(Constant.MODITUTY_KEY_POSITION,data.getName());
                        }
                    }

                    @Override
                    public void onLocate() {
                    }
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 更新用户信息
     * @param paramsKey
     * @param content
     */
    protected void updataUserInfo(final String paramsKey, String content) {

        UserManager.getInstance().modityUserData(paramsKey, content, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                VideoApplication.getInstance().setMineRefresh(true);//首页需要刷新了
                if(null!=object && object instanceof String){
                    try {
                        if(TextUtils.equals(Constant.MODITUTY_KEY_SEX,paramsKey)){
                            UserManager.getInstance().setSex(Integer.parseInt((String) object));
                            updateUserSex();
                        }else if(TextUtils.equals(Constant.MODITUTY_KEY_HEIGHT,paramsKey)){
                            UserManager.getInstance().setHeight((String) object);
                        }else if(TextUtils.equals(Constant.MODITUTY_KEY_WEIGHT,paramsKey)){
                            UserManager.getInstance().setWeight((String) object);
                        }else if(TextUtils.equals(Constant.MODITUTY_KEY_STAR,paramsKey)){
                            UserManager.getInstance().setStar((String) object);
                        }else if(TextUtils.equals(Constant.MODITUTY_KEY_POSITION,paramsKey)){
                            UserManager.getInstance().setPosition((String) object);
                        }
                    }catch (RuntimeException e){

                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                ToastUtils.showCenterToast(errorMsg);
            }
        });
    }
}
