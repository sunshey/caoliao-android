package com.video.player.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;

import java.util.Formatter;
import java.util.Locale;

/**
 * TinyHung@Outlook.com
 * 2019/4/9
 */

public class VideoUtils {

    private static VideoUtils mInstance;

    public static synchronized VideoUtils getInstance() {
        synchronized (VideoUtils.class) {
            if (null == mInstance) {
                mInstance = new VideoUtils();
            }
        }
        return mInstance;
    }

    /**
     * 时长格式化
     * @param timeMs
     * @return
     */
    public String stringForAudioTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 返回设备是否连接至WIFI网络
     * @param context context
     * @return if wifi is connected,return true
     */
    public boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 检查设备是否已连接至可用网络
     * @return
     */
    public boolean isCheckNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_MOBILE || type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * Get activity from context object
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity getContextForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return getContextForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    //设备屏幕宽度
    public int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    //设备屏幕高度
    public int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 将dp转换成px
     * @param dp
     * @return
     */
    public float dpToPx(Context context,float dp) {
        return dp * context.getApplicationContext().getResources().getDisplayMetrics().density;
    }

    public int dpToPxInt(Context context,float dp) {
        return (int) (dpToPx(context,dp) + 0.5f);
    }
}
