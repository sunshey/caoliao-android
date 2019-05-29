
package com.yc.liaolive.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * 缓存图片
 */

public class ImageCache {
	
	private ImageCache() {
		//最大使用容量为堆内存的1/8
		cache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 7)) {
              @Override
              protected int sizeOf(String key, Bitmap value) {
                  return value.getRowBytes() * value.getHeight();
              }
          };
	}

	private static ImageCache imageCache = null;

	public static synchronized ImageCache getInstance() {
		if (imageCache == null) {
			imageCache = new ImageCache();
		}
		return imageCache;

	}
	private LruCache<String, Bitmap> cache = null;
	
	/**
	 * 讲Bitmap缓存起来
	 * @param key
	 * @param value
	 * @return
	 */
	public Bitmap put(String key, Bitmap value){
		if(null==cache){
			cache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}
			};
		}
		return cache.put(key, value);
	}
	
	/**
	 * 根据路径获取Bitmap
	 * @param key
	 * @return
	 */
	public Bitmap get(String key){
		if(TextUtils.isEmpty(key)) return null;
        if(null!=cache&&cache.size()>0){
            return cache.get(key);
        }
        return null;
	}

	/**
	 * 释放掉所有的缓存
	 */
	public void recyler() {
		if(null!=cache){
			cache.evictAll();//清除缓存
		}
	}
}
