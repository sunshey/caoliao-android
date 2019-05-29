package com.yc.liaolive.index.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.start.manager.StartManager;
import com.yc.liaolive.start.model.bean.ConfigBean;
import com.yc.liaolive.util.Utils;
import com.yc.liaolive.view.widget.CircleRadarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/8/18.
 * 首页的底部TAB
 * 调用者控制是否启用重复点击刷新功能
 */

public class MainTabItem extends FrameLayout {

    private static final String TAG = "MainTabItem";
    private boolean isRefresh;//是否支持重复点击刷新
    private int mCureenViewIndex=0;//当前显示的Index
    private List<MainTabView> mIndexTabViews;
    private boolean isRuning;
    private boolean isShowing;
    private boolean isHiding;
    private Vibrator mVibrator;
    private CircleRadarLayout mRadarLayout;//圆形雷达波动
    private int mainIndex = 0;
    private int messageIndex = -1;

    public MainTabItem(@NonNull Context context) {
        super(context);
    }

    public MainTabItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_main_table,this);
    }

    public void initViews() {
        mIndexTabViews = new ArrayList<>();
        View.OnClickListener onTabClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigBean.PageBean pageBean = (ConfigBean.PageBean) view.getTag();
                if (pageBean != null) {
                    int childViewIndex = view.getId();
                    //将再次点击事件拦截，用于处理刷新
                    if(isRefresh && mCureenViewIndex == childViewIndex&& null != mOnTabChangeListene){
                        try {
                            //触摸反馈
                            if(null==mVibrator) mVibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                            mVibrator.vibrate(Constant.VIBRATOR_MILLIS);
                        }catch (RuntimeException e){

                        }
                        mOnTabChangeListene.onRefresh(childViewIndex);
                        return;
                    }
                    MobclickAgent.onEvent(AppEngine.getApplication().getApplicationContext(),"main_tab_"+pageBean.getTarget_id());
                    if(null!= mIndexTabViews && 0 == pageBean.getType()){ //文本
                        mIndexTabViews.get(mCureenViewIndex).setTabSelected(false);
                        mIndexTabViews.get(childViewIndex).setTabSelected(true);
                        if(null!=mOnTabChangeListene){
                            mOnTabChangeListene.onChangeed(childViewIndex);
                        }
                        mCureenViewIndex = childViewIndex;
                    } else if ("3".equals(pageBean.getTarget_id())) { //1v1雷达
                        if (null!= mIndexTabViews) {
                            mIndexTabViews.get(mCureenViewIndex).setTabSelected(false);
                        }
                        mOnTabChangeListene.onTabVideo(mainIndex);
                        //小额贷
                    } else if("24".equals(pageBean.getTarget_id())){
                        if(!TextUtils.isEmpty(pageBean.getOpen_url())){
                            CaoliaoController.start(pageBean.getOpen_url(),true,null);
                        }
                    } else { //图片
                        if (null!= mIndexTabViews) {
                            mIndexTabViews.get(mCureenViewIndex).setTabSelected(false);
                        }
                        if(null!=mOnTabChangeListene){
                            mOnTabChangeListene.onChangeed(childViewIndex);
                        }
                        mCureenViewIndex = childViewIndex;
                    }
                }
            }
        };

        LinearLayout contentLy = findViewById(R.id.view_tab_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        contentLy.removeAllViews();
        if (StartManager.getInstance().getPageBeanList() != null
                && StartManager.getInstance().getPageBeanList().size() > 0) {
            contentLy.removeAllViews();
            List<ConfigBean.PageBean> pageBeanList = StartManager.getInstance().getPageBeanList();
            mIndexTabViews.clear();
            for (int i = 0; i < pageBeanList.size(); i ++) {
                ConfigBean.PageBean pageBean = pageBeanList.get(i);
                //	0文本 1图片
                if (0 == pageBean.getType()) {
                    MainTabView tabView = new MainTabView(getContext());
                    tabView.setTag(pageBean);
                    tabView.setId(i);
                    tabView.setTabContent(pageBean.getText(), pageBean.getIcon(), pageBean.getIcon_check());
                    tabView.setTabSelected(i == 0);
                    contentLy.addView(tabView, params);
                    mIndexTabViews.add(i, tabView);
                    tabView.setOnClickListener(onTabClickListener);
                } else if ("3".equals(pageBean.getTarget_id())) {
                    //1v1 雷达图标
                    mRadarLayout = new CircleRadarLayout(getContext());
                    mRadarLayout.setTag(pageBean);
                    mRadarLayout.setId(i);
                    mRadarLayout.setStyleColor(getResources().getColor(R.color.main_radar_color));
                    mRadarLayout.setIntervalDurtion(990);
                    mRadarLayout.setPlayDurtion(5000);
                    mRadarLayout.setMinAlpha(0.1f);
                    mRadarLayout.setIconView(Utils.dip2px(57), Utils.dip2px(57),
                            Utils.dip2px(3), R.drawable.ic_main_tab);
                    mRadarLayout.onStart(); //自动开始播放
                    contentLy.addView(mRadarLayout, new LinearLayout.LayoutParams(
                            Utils.dip2px(63), Utils.dip2px(63)));
                    mRadarLayout.setOnClickListener(onTabClickListener);
                    mRadarLayout.setTranslationY(-Utils.dip2px(23.7f));
                    mIndexTabViews.add(i, new MainTabView(getContext()));
                } else if (!TextUtils.isEmpty(pageBean.getImg_url())){
                    ImageView imageView = new ImageView(getContext());
                    imageView.setTag(pageBean);
                    imageView.setId(i);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(getContext())
                            .load(pageBean.getImg_url())
                            .error(R.drawable.ic_default_user_head)
                            .crossFade()//渐变
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)//缓存源资源和转换后的资源
                            .centerCrop()//中心点缩放
                            .skipMemoryCache(true)//跳过内存缓存
                            .into(imageView);
                    contentLy.addView(imageView, params);
                    imageView.setOnClickListener(onTabClickListener);
                    mIndexTabViews.add(i, new MainTabView(getContext()));
                }
                if ("1".equals(pageBean.getTarget_id())) {
                    mainIndex = i;
                } else if ("3".equals(pageBean.getTarget_id())) {
                    messageIndex = i;
                }
            }
        }
    }

    public void setIndex(int index){
        if(null!= mIndexTabViews){
            mIndexTabViews.get(mCureenViewIndex).setTabSelected(false);
            mIndexTabViews.get(index).setTabSelected(true);
        }
        mCureenViewIndex=index;
    }

    /**
     * 设置是否支持重复点击刷新功能
     * @param flag
     */
    public void setDoubleRefresh(boolean flag){
        this.isRefresh=flag;
    }

    /**
     * 设置选中的TAB
     * @param index
     */
    public void setCurrentIndex(int index){
        if(mCureenViewIndex == index) return;
        if(null!= mIndexTabViews && mIndexTabViews.size()>0){
            mIndexTabViews.get(mCureenViewIndex).setTabSelected(false);
            mIndexTabViews.get(index).setTabSelected(true);
        }
        mCureenViewIndex = index;
        if(null != mOnTabChangeListene){
            mOnTabChangeListene.onChangeed(index);
        }
    }

    /**
     * 设置消息数量
     * @param count
     */
    public void setMessageContent(int count) {
        if (null!= mIndexTabViews && messageIndex > -1 && messageIndex < mIndexTabViews.size()) {
            mIndexTabViews.get(messageIndex).setTabRedPoint(count);
        }
    }

    /**
     * 显示、隐藏底部菜单
     * @param isShow
     */
    public void showMainTabLayout(boolean isShow){
        if(isRuning) return;
        if(isShow){
            isHiding=false;
            if(isShowing) return;
            isShowing=true;
            isRuning=true;
            ObjectAnimator inAnimator = ObjectAnimator.ofFloat(this, "translationY", this.getHeight(), 0);
            inAnimator.setInterpolator(new LinearInterpolator());
            inAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isRuning=false;
                }
            });
            inAnimator.setDuration(300);
            inAnimator.start();
        }else{
            isShowing=false;
            if(isHiding) return;
            isHiding=true;
            isRuning=true;
            ObjectAnimator outAnimator = ObjectAnimator.ofFloat(this, "translationY", 0, this.getHeight());
            outAnimator.setDuration(300);
            outAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isRuning=false;
                }
            });
            outAnimator.setInterpolator(new LinearInterpolator());
            outAnimator.start();
        }
    }

    public void onResume(){
        if(null!=mRadarLayout) mRadarLayout.onStart();
    }

    public void onPause(){
        if(null!=mRadarLayout) mRadarLayout.onStop();
    }

    /**
     * 对应方法中调用
     */
    public void onDestroy() {
        if(null!=mRadarLayout) mRadarLayout.onStop();
        isShowing=false;isHiding=false;
    }

    public interface OnTabChangeListene{
        void onChangeed(int index);
        void onRefresh(int index);
        void onTabVideo(int index);
    }

    private OnTabChangeListene mOnTabChangeListene;

    public void setOnTabChangeListene(OnTabChangeListene onTabChangeListene) {
        mOnTabChangeListene = onTabChangeListene;
    }
}