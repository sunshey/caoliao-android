package com.yc.liaolive.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yc.liaolive.VideoApplication;

import java.lang.reflect.Method;


/**
 * @author TinyHung@Outlook.com
 * @version 1.0
 * @des ${TODO}
 */
public class InputTools {


    /**
     * 强制隐藏软件盘
     * @param context
     */
    public static void hideKeyBoard(Activity context) {
        InputMethodManager systemService = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null!=systemService){
            systemService.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(),0);
        }
    }

    /**
     * 强制打开软件盘
     * @param context
     */
    public static void showKeyBoard(Activity context) {
        InputMethodManager systemService = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null!=systemService){
            systemService.showSoftInputFromInputMethod(context.getWindow().getDecorView().getWindowToken(),0);
        }
    }



    /**
     * 打卡软键盘
     *
     * @param mEditText
     */
    public static void openKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    /**
     * 关闭软键盘
     *
     * @param mEditText
     */
    public static void closeKeybord(EditText mEditText)
    {
        InputMethodManager imm = (InputMethodManager) VideoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 检查设备是否具备底部虚拟按键功能
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static void solveNavigationBar(Window window) {
        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT>=19){
            uiOptions |= 0x00001000;
        }else{
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
}
