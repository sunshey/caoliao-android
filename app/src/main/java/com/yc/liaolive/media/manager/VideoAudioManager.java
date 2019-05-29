package com.yc.liaolive.media.manager;

import android.content.Context;
import android.media.AudioManager;

/**
 * TinyHung@Outlook.com
 * 2019/3/13
 */

public class VideoAudioManager implements AudioManager.OnAudioFocusChangeListener {

    private static VideoAudioManager mInstance;
    private AudioManager mAudioManager;

    public static synchronized VideoAudioManager getInstance() {
        synchronized (VideoAudioManager.class) {
            if (null == mInstance) {
                mInstance = new VideoAudioManager();
            }
        }
        return mInstance;
    }


    public VideoAudioManager getAudioManager(Context context) {
        if(null==mAudioManager){
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        return mInstance;
    }

    /**
     * 占据焦点
     */
    public void requestAudioFocus(){
        if(null!=mAudioManager){
            mAudioManager.requestAudioFocus(this,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    /**
     * 释放焦点
     */
    public void releaseAudioFocus() {
        if(null!=mAudioManager){
            mAudioManager.abandonAudioFocus(this);
            mAudioManager=null;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }
}
