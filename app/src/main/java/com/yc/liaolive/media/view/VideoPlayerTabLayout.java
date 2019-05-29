package com.yc.liaolive.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/9/26
 * 播放器界面TAB
 */

public class VideoPlayerTabLayout  extends LinearLayout{

    public ImageView mBtnIcon;
    private TextView mBtnTitle;
    private AnimationDrawable mAnimationDrawable;

    public VideoPlayerTabLayout(Context context) {
        super(context);
        init(context,null);
    }

    public VideoPlayerTabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_video_player_tab_layout,this);
        mBtnIcon = (ImageView) findViewById(R.id.view_btn_icon);
        mBtnTitle = (TextView) findViewById(R.id.view_btn_title);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoPlayerTabLayout);
            Drawable icon = typedArray.getDrawable(R.styleable.VideoPlayerTabLayout_playerTabIcon);
            int textColor = typedArray.getColor(R.styleable.VideoPlayerTabLayout_playerTabTextColor, Color.parseColor("#FFFFFF"));
            String title = typedArray.getString(R.styleable.VideoPlayerTabLayout_playerTabText);
            int pixelSize = typedArray.getDimensionPixelSize(R.styleable.VideoPlayerTabLayout_playerTabTextSize, 12);
            if(null!=icon) mBtnIcon.setImageDrawable(icon);
            mBtnTitle.setTextColor(textColor);
            mBtnTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,pixelSize);
            mBtnTitle.setText(title);
            typedArray.recycle();
        }
    }

    public void setTitle(String title){
        if(null!=mBtnTitle)mBtnTitle.setText(title);
    }

    public void setTextColor(int color){
        if(null!=mBtnTitle) mBtnTitle.setTextColor(color);
    }

    public void setTitleSize(int sp){
        if(null!=mBtnTitle) mBtnTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
    }

    public void setIcon(int resID){
        if(null!=mBtnIcon) mBtnIcon.setImageResource(resID);
    }

    public void onDestroy(){
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        mAnimationDrawable=null;
    }
}
