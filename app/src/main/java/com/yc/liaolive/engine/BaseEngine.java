package com.yc.liaolive.engine;

import android.content.Context;

import com.yc.liaolive.BuildConfig;
import com.yc.liaolive.user.manager.UserManager;

import java.util.Map;

/**
 * Created by wanglin  on 2018/7/6 21:22.
 */
public class BaseEngine {

    public Context mContext;
    protected boolean isrsa = true;
    protected boolean iszip = true;
    protected boolean isEncrypt = true;

    public BaseEngine(Context context) {
        this.mContext = context;
        if ("tice".equals(BuildConfig.BUILD_TYPE)) {
            isrsa = false;
            iszip = false;
            isEncrypt = false;
        }
    }

    public Map<String, String> getHeaders() {
        return UserManager.getInstance().getHeaders();
    }
}
