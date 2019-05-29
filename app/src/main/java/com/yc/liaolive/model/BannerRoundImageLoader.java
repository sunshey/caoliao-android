package com.yc.liaolive.model;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ScreenUtils;
import com.yc.liaolive.view.widget.RoundImageView;

/**
 * TinyHung@Outlook.com
 * 2018/2/14
 * Banner--圆角--ImageView-Loader 不适用于gif,暂时没解决问题 2019-02-28
 */

public class BannerRoundImageLoader implements BannerViewInterface<RoundImageView> {

    private float radius=5;//圆角

    public BannerRoundImageLoader(float radius){
        this.radius= radius;
    }

    public BannerRoundImageLoader(){}

    @Override
    public void displayView(Context context, Object path, RoundImageView view) {
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
                            .asBitmap()
                            .error(R.drawable.ic_default_live_icon)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(new BitmapImageViewTarget(view) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }
            }else{
                Glide.with(context)
                        .load(path)
                        .asBitmap()
                        .error(R.drawable.ic_default_live_icon)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(new BitmapImageViewTarget(view) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                            }
                        });
            }
        }
    }

    @Override
    public RoundImageView createImageView(Context context) {
        RoundImageView roundImageView = new RoundImageView(context);
        roundImageView.setRoundRadius(ScreenUtils.dpToPxInt(radius));
        return roundImageView;
    }
}

