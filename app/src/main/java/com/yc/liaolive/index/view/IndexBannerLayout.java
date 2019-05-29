package com.yc.liaolive.index.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.model.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/10/23
 * 广告组件
 */

public class IndexBannerLayout<T> extends FrameLayout {

    private Banner mBanner;

    public IndexBannerLayout(@NonNull Context context) {
        this(context,null);
    }

    public IndexBannerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_index_banner_layout,this);
        mBanner = findViewById(R.id.view_index_banner);
        mBanner.setBannerAnimation(Transformer.Default);
        mBanner.setImageLoader(new GlideImageLoader()).setDelayTime(3800);
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if(null!=mOnBannerItemClickListener){
                    mOnBannerItemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * 根据图片宽高适配组件宽高
     * @param width
     * @param height
     */
    public void setLayoutParams(int width,int height){
        if(null!=mBanner){
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mBanner.getLayoutParams();
            layoutParams.width=width;
            layoutParams.height=height;
            mBanner.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置数据
     * @param list
     */
    public void setData(List<T> list){
        if(null!=mBanner&&null!=list){
            try {
                mBanner.update(list);
            }catch (RuntimeException e){

            }
        }
    }

    public void onResume(){
        if(null!=mBanner) mBanner.startAutoPlay();
    }

    public void onPause(){
        if(null!=mBanner) mBanner.stopAutoPlay();
    }

    public interface OnBannerItemClickListener{
        void onItemClick(int index);
    }
    private OnBannerItemClickListener mOnBannerItemClickListener;

    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        mOnBannerItemClickListener = onBannerItemClickListener;
    }
}
