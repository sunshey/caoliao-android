package com.yc.liaolive.model;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;
import com.yc.liaolive.manager.ApplicationManager;

/**
 * TinyHung@outlook.com
 * 2017/6/16 10:33
 * 配置Glide加载图片质量和缓存大小及Glide缓存目录---SD卡
 */
public class GlideCache implements GlideModule {

    private static final String TAG = GlideCache.class.getSimpleName();

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

        int cacheSize = 100*1000*1000;

        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);//内存缓存大小
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);//内存Bitmap大小

        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565)
                .setMemoryCache(new LruResourceCache(customMemoryCacheSize))
                .setBitmapPool( new LruBitmapPool( customBitmapPoolSize ))
                .setDiskCache(new DiskLruCacheFactory(ApplicationManager.getInstance().getCacheExample().getCachePathAbsolutePath(), cacheSize));//设置缓存位置
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
