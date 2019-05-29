package com.yc.liaolive.model;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/11/13
 * Banner--ImageView-Loader
 */

public class BannerImageLoader implements BannerViewInterface<ImageView> {

    private int radius=0;//圆角

    public BannerImageLoader(int radius){
        this.radius=radius;
    }

    public BannerImageLoader(){}

    @Override
    public void displayView(Context context, Object path, ImageView view) {
        if(null!=view){
            if(path instanceof String){
                String urlPath= (String) path;
                if(urlPath.endsWith(".GIF")||urlPath.endsWith(".gif")){
                    Glide.with(context)
                            .load(urlPath)
                            .asGif()
                            .fitCenter()
                            .placeholder(R.drawable.ic_default_item_cover)
                            .error(R.drawable.ic_default_item_cover)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GifDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(final GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    if(null!=resource){
                                        view.setImageDrawable(resource);
                                    }
                                    return false;
                                }
                            })
                            .into(view);
                }else{
                    Glide.with(context)
                            .load(path)
                            .error(R.drawable.ic_default_live_icon)
                            .placeholder(R.drawable.ic_default_live_icon)
                            .dontAnimate()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(view);
                }
            }else{
                Glide.with(context)
                        .load(path)
                        .error(R.drawable.ic_default_live_icon)
                        .placeholder(R.drawable.ic_default_live_icon)
                        .dontAnimate()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(view);
            }
        }
    }

    @Override
    public ImageView createImageView(Context context) {
        return new ImageView(context);
    }
}