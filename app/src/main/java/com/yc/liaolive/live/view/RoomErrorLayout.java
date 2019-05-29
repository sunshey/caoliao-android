package com.yc.liaolive.live.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.live.listener.OnExceptionListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * TinyHung@Outlook.com
 * 2019/1/8
 * 直播间异常情况界面交互
 */

public class RoomErrorLayout extends FrameLayout {
    //其他异常情况
    private View mErrorRootView;
    //主播前后台切换发生了变化
    private ImageView mEmptyFrontCover;
    private ImageView mEmptyFrontIcon;
    private TextView mViewEmptyTips;
    private String mFrontcover;
    private OnExceptionListener mExceptionListener;

    public RoomErrorLayout(@NonNull Context context) {
        this(context,null);
    }

    public RoomErrorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_live_room_error_layout,this);
        //主播切换至后台
        mErrorRootView = findViewById(R.id.view_error_root);
        //主播封面
        mEmptyFrontCover = findViewById(R.id.view_empty_front_cover);
        //icon
        mEmptyFrontIcon = findViewById(R.id.view_empty_front_icon);
        mViewEmptyTips = findViewById(R.id.view_empty_tips);
    }

    /**
     * 主播端APP前后台切换发生了变化
     * @param foregroundState 0：至前台 1：至后台
     */
    public synchronized void switchAppState(int foregroundState) {
        if(null== mErrorRootView) return;
        if(0==foregroundState){
            if(mErrorRootView.getVisibility()==GONE) return;
            mErrorRootView.setVisibility(GONE);
            mViewEmptyTips.setText("");
            mEmptyFrontCover.setImageResource(0);
            mEmptyFrontIcon.setImageResource(0);
        }else{
            if(mErrorRootView.getVisibility()==VISIBLE) return;
            mErrorRootView.setVisibility(VISIBLE);
            mEmptyFrontIcon.setImageResource(R.drawable.ic_leave);
            //封面
            Glide.with(getContext()).load(mFrontcover)
                    .error(R.drawable.bg_live_transit)
                    .crossFade()//渐变
                    .animate(R.anim.item_alpha_in)//加载中动画
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .centerCrop()//中心点缩放
                    .skipMemoryCache(true)//跳过内存缓存
                    .bitmapTransform(new BlurTransformation(getContext(), 25))
                    .into(mEmptyFrontCover);
            mViewEmptyTips.setText(1==foregroundState?"主播暂时离开,请稍等片刻...":"主播还在路上...");
        }
    }

    /**
     * 显示拉流失败状态
     */
    public void showPullError(int resID,String tips) {
        if(null== mErrorRootView ||null== mViewEmptyTips) return;
        mErrorRootView.setVisibility(VISIBLE);
        mErrorRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!= mExceptionListener) mExceptionListener.onTautology();
            }
        });
        mEmptyFrontIcon.setImageResource(resID);
        mViewEmptyTips.setText(tips);
    }

    /**
     * 重置
     */
    public void errorStateReset() {
        if(null!=mErrorRootView){
            mErrorRootView.setOnClickListener(null);
            mErrorRootView.setVisibility(GONE);
        }
        if(null!=mEmptyFrontIcon) mEmptyFrontIcon.setImageResource(0);
        if(null!=mViewEmptyTips) mViewEmptyTips.setText("");
        if(null!=mEmptyFrontCover) mEmptyFrontCover.setImageResource(0);
    }

    /**
     * 绑定主播
     * @param frontcover
     */
    public void setAnchorAvatar(String frontcover) {
        this.mFrontcover=frontcover;
    }

    public void setOnExceptionListener(OnExceptionListener listsner) {
        this.mExceptionListener = listsner;
    }
}
