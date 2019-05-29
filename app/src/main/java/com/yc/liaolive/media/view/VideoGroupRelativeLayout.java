package com.yc.liaolive.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.yc.liaolive.R;
import com.yc.liaolive.interfaces.OnAnimationListener;
import com.yc.liaolive.view.widget.LikeView;

/**
 * TinyHung@outlook.com
 * 2017/7/7 9:16
 * 拦截屏幕的双击事件以播放双击点赞动画，分发单击和左右滑动事件
 */

public class VideoGroupRelativeLayout extends RelativeLayout implements View.OnTouchListener {

    private static final String TAG = VideoGroupRelativeLayout.class.getSimpleName();
    private final Context context;
    private LikeView mLikeView;
    private boolean isPriceAnimationPlaying=false;//是否正在播放动画
    private GestureDetector mGestureDetector;


    public VideoGroupRelativeLayout(Context context) {
        this(context,null);
    }

    public VideoGroupRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoGroupRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        View.inflate(context, R.layout.video_group_layout,this);
        mGestureDetector = new GestureDetector(context,new TouchOnGestureListener());
        mLikeView = (LikeView) findViewById(R.id.like_view);
        this.setOnTouchListener(this);
        mGestureDetector.setIsLongpressEnabled(true);
        this.setFocusable(true);
        this.setLongClickable(true);
    }


    /**
     * 外界传值，是否支持点赞动画
     * @param isPrice
     */
    public void setIsPrice(boolean isPrice){
        this.isPriceAnimationPlaying =isPrice;
    }

    /**
     * 将手势处理事件交给GestureDetector
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(null!=mGestureDetector) mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 拦截触摸屏幕的所有事件
     */
    private  class TouchOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG,"onSingleTapUp");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d(TAG,"onLongPress");
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.d(TAG,"onScroll---distanceX="+distanceX+",distanceY="+distanceY);
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG,"onFling"+",velocityX="+velocityX+",velocityY="+velocityY);
            //右滑动
            if(velocityX>0){
                if(null!=mOnDoubleClickListener){
                    mOnDoubleClickListener.onRightSwipe();
                }
            //左滑动
            }else if(velocityX<0){
                if(null!=mOnDoubleClickListener){
                    mOnDoubleClickListener.onLeftSwipe();
                }
            }
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG,"onShowPress");
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG,"onDown");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG,"onDoubleTap");
            if(null!=mOnDoubleClickListener){
                mOnDoubleClickListener.onDoubleClick();
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d(TAG,"onDoubleTapEvent");
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG,"onSingleTapConfirmed");
            if(null!=mOnDoubleClickListener){
                mOnDoubleClickListener.onSingleClick();
            }
            return false;
        }
    }

    /**
     * 初始不显示点赞动画
     */
    public void setImageVisibility(){
        if(null!=mLikeView) mLikeView.setVisibility(GONE);
    }

    /**
     * 播放点赞动画
     */
    public void startPriceAnimation() {
        if(isPriceAnimationPlaying){
            return;
        }
        if(null!=mLikeView){
            mLikeView.startViewMotion(new OnAnimationListener() {
                @Override
                public void onStart() {
                    isPriceAnimationPlaying=true;
                    if(null!=mLikeView){
                        mLikeView.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onStop() {
                    isPriceAnimationPlaying=false;
                    if(null!=mLikeView){
                        mLikeView.setVisibility(GONE);
                    }
                }
            });
        }

    }

    /**
     * 对外接口
     */
    public interface OnDoubleClickListener{
        void onDoubleClick();//双击事件
        void onSingleClick();//单击事件
        void onLeftSwipe();//向左滑
        void onRightSwipe();//向右滑
    }

    private OnDoubleClickListener mOnDoubleClickListener;

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        mOnDoubleClickListener = onDoubleClickListener;
    }
}
