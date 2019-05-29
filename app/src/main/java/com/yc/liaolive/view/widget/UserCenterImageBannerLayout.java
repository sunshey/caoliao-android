package com.yc.liaolive.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.model.GlideImageLoader;
import com.yc.liaolive.util.ScreenUtils;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import java.util.List;
import java.util.ArrayList;

/**
 * TinyHung@Outlook.com
 * 2018/10/12
 * 用户中心头像Banner
 * 默认高度是屏幕的宽度
 */

public class UserCenterImageBannerLayout extends FrameLayout {

    private static final String TAG = "UserCenterImageBannerLayout";
    private Banner mBanner;
    List<PrivateMedia> mPrivateMedia;
    private LinearLayout mDotView;


    public UserCenterImageBannerLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public UserCenterImageBannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("WrongViewCast")
    private void init(Context context) {
        View.inflate(context, R.layout.view_user_center_banner_layout,this);
        mBanner = (Banner) findViewById(R.id.view_banner);
        ViewGroup.LayoutParams layoutParams = mBanner.getLayoutParams();
        layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height=ScreenUtils.getScreenWidth();
        mBanner.setLayoutParams(layoutParams);
        //确定遮罩层为高度的1/3
        findViewById(R.id.view_shad_layout).getLayoutParams().height=ScreenUtils.getScreenWidth()/3;

        mBanner.setBannerAnimation(Transformer.Default);
        mDotView = (LinearLayout) findViewById(R.id.view_dot_view);
        mBanner.setImageLoader(new GlideImageLoader()).setDelayTime(3800);
        mBanner.setOnPageChangeListener(onPageChangeListener);
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if(null!=mOnBannerClickListenr&&null!= mPrivateMedia && mPrivateMedia.size()>position){
                    mOnBannerClickListenr.onItemClick(mPrivateMedia.get(position),UserCenterImageBannerLayout.this,mPrivateMedia.size());
                }
            }
        });
    }

    private ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(null!=mDotView&&mDotView.getChildCount()<=0){
                return;
            }
            try {
                for (int i = 0; i < mPrivateMedia.size(); i++) {
                    mDotView.getChildAt(i).setEnabled(i != position);
                }
            }catch (RuntimeException e){

            }catch (Exception e){

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };


    /**
     * 初始化Banner
     * @param integers
     */
    private void initBanners(List<String> integers) {

        if(null==integers) return;
        try {
            mBanner.update(integers);
            if(null!=onPageChangeListener) onPageChangeListener.onPageSelected(0);
            addDots();
        } catch (IllegalStateException e){

        } catch (RuntimeException e){

        }catch (Exception e){

        }
    }

    /**
     * 更新Banner
     * @param privateMedias
     */
    public void setBanners(List<PrivateMedia> privateMedias) {
        if(null==privateMedias) return;
        if(null!=mPrivateMedia) mPrivateMedia.clear();
        this.mPrivateMedia=privateMedias;
        List<String> strings=new ArrayList<>();
        for (int i = 0; i < privateMedias.size(); i++) {
            strings.add(TextUtils.isEmpty(privateMedias.get(i).getFile_path())?privateMedias.get(i).getImg_path():privateMedias.get(i).getFile_path());
        }
        initBanners(strings);
    }

    public void setBannerHeight(int height){
        if(null!=mBanner) mBanner.getLayoutParams().height=ScreenUtils.getScreenWidth();
    }

    /**
     * 绘制页眉小圆点
     */
    private void addDots() {
        if (mPrivateMedia == null) {
            return;
        }
        if(null!=mDotView) mDotView.removeAllViews();
        int num= mPrivateMedia.size();
        if(num>1){
            int pxFor10Dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
            for (int i=0;i<num;i++) {
                View dot = new View(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pxFor10Dp, pxFor10Dp);
                layoutParams.setMargins(0, 0, pxFor10Dp, 0);
                dot.setLayoutParams(layoutParams);
                dot.setBackgroundResource(R.drawable.user_arl_dot_selector);
                mDotView.addView(dot);
            }
            if(null!=onPageChangeListener) onPageChangeListener.onPageSelected(0);
        }
    }

    public void onResume(){
        if(null!=mBanner) mBanner.startAutoPlay();
    }

    public void onPause(){
        if(null!=mBanner) mBanner.stopAutoPlay();
    }

    public void onDestroy(){
        if(null!=mBanner) mBanner.stopAutoPlay(); mBanner=null;
        if(null!=mDotView) mDotView.removeAllViews(); mDotView=null;
        if(null!= mPrivateMedia) mPrivateMedia.clear(); mPrivateMedia =null;
    }

    public interface OnBannerClickListenr{
        void onItemClick(PrivateMedia privateMedia,View view,int count);
    }

    private OnBannerClickListenr mOnBannerClickListenr;

    public void setOnBannerClickListenr(OnBannerClickListenr onBannerClickListenr) {
        mOnBannerClickListenr = onBannerClickListenr;
    }
}
