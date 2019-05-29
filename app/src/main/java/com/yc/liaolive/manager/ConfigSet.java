package com.yc.liaolive.manager;

import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.util.SharedPreferencesUtil;

/**
 * TinyHung@outlook.com
 * 2017/5/26 14:09
 * 设置中心配置
 */
public class ConfigSet {

    public static boolean IS_DEBUG=true;
    private static ConfigSet mConfigSet;
    private boolean isHWCodecEnabled;//是否开启硬编
    private boolean isAudioOpenWindown;//音频播放是否开启悬浮窗


    public static synchronized ConfigSet getInstance(){
        synchronized (ConfigSet.class){
            if(null == mConfigSet){
                mConfigSet = new ConfigSet();
            }
        }
        return mConfigSet;
    }

    public void setDebug(boolean flag) {
        IS_DEBUG=flag;
    }


    public boolean isHWCodecEnabled() {
        return isHWCodecEnabled;
    }

    /**
     * 开启、关闭直播硬编
     * @param HWCodecEnabled
     */
    public void setHWCodecEnabled(boolean HWCodecEnabled) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_HWCODEC_ENABLED,HWCodecEnabled);
        isHWCodecEnabled = HWCodecEnabled;
    }

    public boolean isAudioOpenWindown() {
        return isAudioOpenWindown;
    }

    public void setAudioOpenWindown(boolean audioOpenWindown) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.SETTING_AUDIO_WINDOWN,audioOpenWindown);
        isAudioOpenWindown = audioOpenWindown;
    }

    /**
     * 初始设置状态
     */
    public void init() {
        isHWCodecEnabled = SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_HWCODEC_ENABLED,true);
        isAudioOpenWindown = SharedPreferencesUtil.getInstance().getBoolean(Constant.SETTING_AUDIO_WINDOWN,true);
    }
}
