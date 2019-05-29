package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.yc.liaolive.R;
import com.yc.liaolive.model.GlideCircleTransform;

/**
 * TinyHung@Outlook.com
 * 2018/11/7
 * 主播直播前的编辑界面
 */

public class LivePublishEditLayout extends FrameLayout {

    public LivePublishEditLayout(@NonNull Context context) {
        this(context,null);
    }

    public LivePublishEditLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_live_publish_layout,this);
    }
    public void init(String frontCover){
        setClickable(true);
        setBackgroundColor(getResources().getColor(R.color.black));
        getBackground().setAlpha(50);
        ImageView cover = (ImageView) findViewById(R.id.btn_front_cover);

        Glide.with(getContext())
                .load(frontCover)
                .error(R.drawable.ic_default_user_head)
                .centerCrop()
                .skipMemoryCache(true)//跳过内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(cover);

        OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                switch (v.getId()) {
                    case R.id.btn_publish:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onStartPublish();
                        break;
                    case R.id.view_btn_close:
                        if(null!=mOnFunctionListener) mOnFunctionListener.onBack();
                        break;
                }
            }
        };
        findViewById(R.id.btn_publish).setOnClickListener(onClickListener);
        findViewById(R.id.view_btn_close).setOnClickListener(onClickListener);
    }

    public void onDestroy(){
        ImageView cover = (ImageView) findViewById(R.id.btn_front_cover);
        if(null!=cover) cover.setImageResource(0);
        setBackgroundColor(0);
    }

    public interface OnFunctionListener{
        void onBack();
        void onStartPublish();
    }

    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
