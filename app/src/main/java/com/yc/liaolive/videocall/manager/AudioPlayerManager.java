package com.yc.liaolive.videocall.manager;

import android.media.MediaPlayer;
import com.yc.liaolive.AppEngine;
import com.yc.liaolive.R;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2019/1/22
 * 音频
 */

public class AudioPlayerManager {

    private static final String TAG = "AudioPlayerManager";
    private static AudioPlayerManager mInstance;
    private MediaPlayer mAudioPlayer;

    public static synchronized AudioPlayerManager getInstance(){
        synchronized (AudioPlayerManager.class){
            if(null==mInstance){
                mInstance=new AudioPlayerManager();
            }
        }
        return mInstance;
    }

    public void startPlayer(){
        stopPlayer();
        try {
            mAudioPlayer = MediaPlayer.create(AppEngine.getApplication().getApplicationContext(), R.raw.mack_call_sound);
            mAudioPlayer.setLooping(true);
            mAudioPlayer.start();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    public void startPlayer(int resPath){
        stopPlayer();
        try {
            mAudioPlayer = MediaPlayer.create(AppEngine.getApplication().getApplicationContext(), resPath);
            mAudioPlayer.setLooping(true);
            mAudioPlayer.start();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    public void stopPlayer(){
        if(null!=mAudioPlayer){
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer=null;
        }
    }

    public void onDestroy(){
        if(null!=mAudioPlayer){
            mAudioPlayer.stop();
            mAudioPlayer.release();
            mAudioPlayer=null;
        }
        mInstance=null;
    }
}
