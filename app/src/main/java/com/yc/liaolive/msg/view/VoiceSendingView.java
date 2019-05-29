package com.yc.liaolive.msg.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.liaolive.R;


/**
 * 发送语音提示控件
 */
public class VoiceSendingView extends RelativeLayout {

    private AnimationDrawable frameAnimation;
    private ImageView img;
    private TextView desc;

    private boolean showRecording = false;  //显示录制还是取消 true录制 false取消

    public VoiceSendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.voice_sending, this);
        img = (ImageView)findViewById(R.id.microphone);
        img.setBackgroundResource(R.drawable.anim_voice);
//        frameAnimation = (AnimationDrawable) img.getBackground();
        desc = findViewById(R.id.voice_sending_desc);
    }

    public void showRecording(){
        if (!showRecording) {
            showRecording = true;
            img.setBackgroundResource(R.drawable.anim_voice);
            frameAnimation = (AnimationDrawable) img.getBackground();
            frameAnimation.start();
            desc.setBackground(null);
            desc.setText(getResources().getString(R.string.chat_up_finger));
        }
    }

    public void showCancel(){
        if (showRecording) {
            showRecording = false;
            if (frameAnimation != null ) frameAnimation.stop();
            img.setBackgroundResource(R.drawable.icon_voice_cancle);
            desc.setBackground(getResources().getDrawable(R.drawable.bg_voice_cancel_tvbg));
            desc.setText(getResources().getString(R.string.chat_voice_cancel));
        }
    }

    public void release(){
        showRecording = false;
        if (frameAnimation != null ) frameAnimation.stop();
    }
}
