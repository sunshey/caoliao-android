package com.yc.liaolive.model;

import android.content.Context;
import android.view.View;
import java.io.Serializable;

/**
 * TinyHung@Outlook.com
 * 2018/11/13
 * Banner--View,自定义布局实现此接口,指定泛型类型
 */

public interface BannerViewInterface<T extends View> extends Serializable {

    String TAG="BannerViewInterface";

    void displayView(Context context, Object path, T view);

    T createImageView(Context context);
}
