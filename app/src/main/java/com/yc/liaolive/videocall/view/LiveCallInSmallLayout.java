package com.yc.liaolive.videocall.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.model.GlideCircleTransform;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.view.widget.CircleRadarLayout;

/**
 * TinyHung@Outlook.com
 * 2018/12/10
 * 视频通话 呼入 迷你版
 */

public class LiveCallInSmallLayout extends FrameLayout {

    private static final String TAG = "LiveCallInSmallLayout";
    private Vibrator mVibrator;//来电震动
    private MediaPlayer mPlayer;
    private CircleRadarLayout mBtnAccpet;
    private CircleRadarLayout mBtnReject;

    public LiveCallInSmallLayout(@NonNull Context context) {
        this(context,null);
    }

    public LiveCallInSmallLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_call_in_min_layout,this);
        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.view_call_tips).getVisibility()==VISIBLE){
                    findViewById(R.id.view_call_tips).setVisibility(GONE);
                    return;
                }
                switch (v.getId()) {
                    case R.id.btn_accept:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onAcceptCall();
                        break;
                    case R.id.btn_reject:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onRejectCall();
                        break;
                }
            }
        };
        mBtnAccpet = (CircleRadarLayout) findViewById(R.id.btn_accept);
        mBtnReject = (CircleRadarLayout) findViewById(R.id.btn_reject);
        mBtnAccpet.setOnClickListener(onClickListener);
        mBtnReject.setOnClickListener(onClickListener);
    }

    /**
     * 开始响应来电状态
     */
    public void onStart(){
        mVibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] patter = {2000,1000};
        mVibrator.vibrate(patter, 0);
        //直接修改系统音量为最大音量
        try {
            mPlayer = MediaPlayer.create(getContext(), R.raw.mack_call_sound);
            mPlayer.setLooping(true);
            mPlayer.start();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    /**
     * 结束来电状态
     */
    public void onStop(){
        if(null!=mBtnAccpet) mBtnAccpet.onStop();
        if(null!=mBtnReject) mBtnReject.onStop();
        if(null!=mVibrator) mVibrator.cancel();
        if(null!=mPlayer){
            mPlayer.stop();
            mPlayer.release();
            mPlayer=null;
        }
    }

    public void setHeadImg(String headImg){
        ImageView imageView = (ImageView) findViewById(R.id.view_user_head);
        //主播头像
        Glide.with(getContext())
                .load(headImg)
                .error(R.drawable.ic_default_user_head)
                .dontAnimate()
                .centerCrop()//中心点缩放
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(imageView);
    }

    public void setNickname(String nickname){
        ((TextView) findViewById(R.id.view_user_name)).setText(nickname);
    }

    public void setDesc(String desc){
        ((TextView) findViewById(R.id.view_tv_desc)).setText(desc);
    }

    public void setTips(String tips){
        ((TextView) findViewById(R.id.view_tv_tips)).setText(tips);
    }

    public void setTips(Spanned tips){
        ((TextView) findViewById(R.id.view_tv_tips)).setText(tips);
    }

    public void showCallTipsView(boolean flag){
        findViewById(R.id.view_call_tips).setVisibility(flag?VISIBLE:GONE);
    }

    public void setNickNameTextSize(int size) {
        ((TextView) findViewById(R.id.view_user_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }


    public abstract static class OnFunctionListener{
        //接听
        public void onAcceptCall(){}
        //挂断
        public void onRejectCall(){}
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }

    public void onDestroy(){
        onStop();
        mOnFunctionListener=null;mVibrator=null;mBtnAccpet=null;mBtnReject=null;
    }
}
