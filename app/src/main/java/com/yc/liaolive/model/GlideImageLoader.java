package com.yc.liaolive.model;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.youth.banner.loader.ImageLoader;

/**
 * 轮播图的加载构造器
 */

public class GlideImageLoader extends ImageLoader {

    private static final String TAG = "GlideImageLoader";
    private int radius;

    public GlideImageLoader(int radius){
        this.radius=radius;
    }

    public GlideImageLoader(){

    }

    @Override
    public void displayImage(Context context, Object url, ImageView imageView) {
        if(null!=imageView){
            imageView.setImageResource(0);
            Glide.with(context)
                    .load(url)
                    .thumbnail(0.1f)
                    .placeholder(imageView.getDrawable())
                    .error(R.drawable.bg_live_transit)
                    .dontAnimate()
                    .skipMemoryCache(true)//跳过内存缓存
                    .transform(new GlideRoundTransform(context,radius))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .into(imageView);
        }
    }
}
