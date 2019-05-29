package com.yc.liaolive.util;

import android.content.Context;
import java.lang.reflect.Method;

/**
 * TinyHung@Outlook.com
 * 2017/8/23.
 * 反射获取Context
 */

public class ContextUtil {

    private static Context CONTEXT_INSTANCE;

    public ContextUtil() {

    }

    public static Context getContext() {
        if(CONTEXT_INSTANCE == null) {
            synchronized(ContextUtil.class) {
                if(CONTEXT_INSTANCE == null) {
                    try {
                        Class e = Class.forName("android.app.ActivityThread");
                        Method method = e.getMethod("currentActivityThread", new Class[0]);
                        Object currentActivityThread = method.invoke(e, new Object[0]);
                        Method method2 = currentActivityThread.getClass().getMethod("getApplication", new Class[0]);
                        CONTEXT_INSTANCE = (Context)method2.invoke(currentActivityThread, new Object[0]);
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }
                }
            }
        }
        return CONTEXT_INSTANCE;
    }
}
