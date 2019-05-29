package com.yc.liaolive.interfaces;

import android.content.Context;
import android.view.View;
import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/11/13
 * Banner自定义处理
 */

public interface BannerLoaderInterface<T extends View> extends Serializable {

    void displayImage(Context context, Object path, T imageView);

    T createImageView(Context context);
}
