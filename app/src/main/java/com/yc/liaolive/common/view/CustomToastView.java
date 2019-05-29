package com.yc.liaolive.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.util.DeviceUtils;
import com.yc.liaolive.util.Utils;

/**
 * 自定义Toast提示
 * Created by Yangxueqin on 2018/11/29.
 */

public class CustomToastView {

    public static final int LENGTH_LONG = 3500;

    public static final int LENGTH_SHORT = 2000;

    private static Context mContext;

    private View toastView;

    private static CustomToastView mCustomToastView;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mWLayoutParams;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private static boolean hasAuthorFloatWin; //是否开启浮窗权限

    public static CustomToastView getInstance() {
        mContext = AppEngine.getApplication();
        hasAuthorFloatWin = DeviceUtils.hasAuthorFloatWin();
        if (mCustomToastView == null) {
            synchronized (CustomToastView.class) {
                if (null == mCustomToastView) {
                    mCustomToastView = new CustomToastView();
                }
            }
        }
        return mCustomToastView;
    }

    private CustomToastView() {
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        if (null == mWLayoutParams) {
            initWindowParam();
        }
    }

    /**
     * 初始化WindowManager显示样式
     */
    private void initWindowParam() {
        mWLayoutParams = new WindowManager.LayoutParams();
        mWLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER;
        mWLayoutParams.format = PixelFormat.TRANSLUCENT;
        //Google Android7.1.1版本（25）对TYPE TOAST进行管控，修改说明见官网https://android.googlesource.com/platform/frameworks/base/+/dc24f93%5E%21/#F6
        //使用时会报android.view.WindowManager$BadTokenException: Unable to add window -- window android.view.ViewRootImpl$W@363f7b1 has already been added
        //的错，具体意思是Toast已经被添加到窗口中，所以25及以上我们把TYPE_TOAST换成TYPE_PHONE
        if (android.os.Build.VERSION.SDK_INT < 25) {
            mWLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mWLayoutParams.packageName = mContext.getPackageName();
        mWLayoutParams.windowAnimations = android.R.style.Animation_Toast;
        mWLayoutParams.y = mContext.getResources().getDisplayMetrics().heightPixels / 5;
        //        mWindowParams.windowAnimations 可以设置toast弹出的显示动画
    }

    /**
     * 初始化默认弹出的TextView样式
     *
     * @return 默认的toastView（为TextView）
     */
    private View getDefaultToastView() {
        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTextColor(Color.WHITE);
        textView.setFocusable(false);
        textView.setClickable(false);
        textView.setFocusableInTouchMode(false);
        textView.setBackgroundResource(R.drawable.tv_bg_toast_center_shape);
        textView.setLineSpacing(Utils.dip2px(2), 1f);
        textView.setPadding(
                Utils.dip2px(20),
                Utils.dip2px(10),
                Utils.dip2px(20),
                Utils.dip2px(10)
        );

        textView.setMaxWidth((int) (Utils.getScreenWidth() * 0.8));
        return textView;
    }

    private void removeView() {
        if (toastView != null && toastView.getParent() != null && hasAuthorFloatWin) {
            mWindowManager.removeView(toastView);
            mHandler.removeCallbacks(timerRunnable);
        }
    }

    /**
     * 弹出默认Toast
     *
     * @param textString toast文案
     * @param duration toast显示时间
     */
    public void showToast(String textString, int duration) {
        removeView();
        //如果toastView为空或者上一个toastView不是默认TextView的，重新生成
        toastView = getDefaultToastView();
        if (toastView instanceof TextView) {
            ((TextView) toastView).setText(textString);
        }
        show(duration, Gravity.NO_GRAVITY);
    }

    /**
     * 弹出中间默认Toast
     *
     * @param textString toast文案 资源id
     * @param duration toast显示时间
     */
    public void showCenterToast(int textString, int duration) {
        showCenterToast(mContext.getString(textString), duration);
    }

    /**
     * 弹出中间默认Toast
     *
     * @param textString toast文案
     * @param duration toast显示时间
     */
    public void showCenterToast(String textString, int duration) {
        removeView();
        //如果toastView为空或者上一个toastView不是默认TextView的，重新生成
        toastView = getDefaultToastView();
        if (toastView instanceof TextView) {
            ((TextView) toastView).setText(textString);
        }
        mWLayoutParams.y = 0;
        show(duration, Gravity.CENTER);
    }

    /**
     * 弹出默认Toast
     *
     * @param textString toast文案 资源id
     * @param duration toast显示时间
     */
    public void showToast(int textString, int duration) {
        showToast(mContext.getString(textString), duration);
    }

    /**
     * 弹出自定义View的Toast
     * 传递View时注意如果使用页面activity创建的View，可能会出现内存溢出
     *
     * @param view toast内容
     * @param duration toast显示时间
     */
    public void showToast(View view, int duration) {
        removeView();
        toastView = view;
        show(duration, Gravity.NO_GRAVITY);
    }

    /**
     * 显示toast
     * 为WindowManager添加要展示的内容并设置显示时间后移除
     *
     * @param duration 显示时长
     */
    private void show(int duration, int gravity) {
        if (hasAuthorFloatWin) {
            removeView();
            try {
                mWindowManager.addView(toastView, mWLayoutParams);
                mHandler.postDelayed(timerRunnable, duration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showSystemToast(duration, gravity);
        }

    }

    /**
     * 使用系统Toast弹出提示
     *
     * @param duration
     */
    private void showSystemToast(final int duration, int gravity) {
        try {
            ToastCompat toast = new ToastCompat(mContext);
            toast.setView(toastView);
            toast.setDuration(duration);
            if (gravity != Gravity.NO_GRAVITY) {
                toast.setGravity(gravity, 0, 0);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为WindowManager移除toast显示内容
     */
    private final Runnable timerRunnable = new Runnable() {
        @Override public void run() {
            removeView();
        }
    };

}
