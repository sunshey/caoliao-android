package com.yc.liaolive.index.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.model.BannerImageLoader;
import com.yc.liaolive.view.widget.AutoBannerLayout;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2018/11/13
 * 广告组件
 */

public class IndexBannerView extends FrameLayout {

    private AutoBannerLayout mBanner;

    public IndexBannerView(@NonNull Context context) {
        this(context,null);
    }

    public IndexBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_index_banners_layout,this);
        mBanner = findViewById(R.id.view_index_banner);
        mBanner.setImageLoader(new BannerImageLoader()).setOnItemClickListener(new AutoBannerLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
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
            LayoutParams layoutParams = (LayoutParams) mBanner.getLayoutParams();
            layoutParams.width=width;
            layoutParams.height=height;
            mBanner.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置数据
     * @param list
     */
    public void setData(List<?> list){
        if(null!=mBanner) mBanner.setData(list).start();
    }

    public void onResume(){
        if(null!=mBanner) mBanner.onResume();
    }

    public void onPause(){
        if(null!=mBanner) mBanner.onPause();
    }

    public interface OnBannerItemClickListener{
        void onItemClick(int index);
    }
    private OnBannerItemClickListener mOnBannerItemClickListener;

    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        mOnBannerItemClickListener = onBannerItemClickListener;
    }
}
