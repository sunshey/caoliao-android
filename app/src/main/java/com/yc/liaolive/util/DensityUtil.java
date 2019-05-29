package com.yc.liaolive.util;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.yc.liaolive.VideoApplication;

/**
 * TinyHung@outlook.com
 * 2017/5/20 10:52
 * 分辨率相关
 */

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = VideoApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = VideoApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 设置某个View的margin
     *
     * @param view   需要设置的view
     * @param isDp   需要设置的数值是否为DP
     * @param left   左边距
     * @param right  右边距
     * @param top    上边距
     * @param bottom 下边距
     * @return
     */
    public static ViewGroup.LayoutParams setViewMargin(View view, boolean isDp, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        }

        int leftPx = left;
        int rightPx = right;
        int topPx = top;
        int bottomPx = bottom;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }

        //根据DP与PX转换计算值
        if (isDp) {
            leftPx = dp2px(left);
            rightPx = dp2px(right);
            topPx = dp2px(top);
            bottomPx = dp2px(bottom);
        }
        //设置margin
        marginParams.setMargins(leftPx, topPx, rightPx, bottomPx);
        view.setLayoutParams(marginParams);
        view.requestLayout();
        return marginParams;
    }


    /**
     * sp转px
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,getDisplayMetrics(context));
    }


    /**
     * px转sp
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2sp(Context context, float pxVal){
        return (int) (pxVal / getDisplayMetrics(context).scaledDensity + 0.5f);
    }


    /**
     * 获取DisplayMetrics
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }
    /**
     * 获取屏幕的密度
     */

    public static DisplayMetrics getScreenSize(Activity act) {
        DisplayMetrics metric = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;  // 屏幕高度（像素
        return metric;
    }

    /**
     * 获取屏幕的高
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = getScreenSize(activity);
        int height = displayMetrics.heightPixels;  // 屏幕高度（像素
        return height;
    }

    /**
     * 获取屏幕的宽
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = getScreenSize(activity);
        int width = displayMetrics.widthPixels;  // 屏幕高度（像素
        return width;
    }
}