package com.yc.liaolive.media.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.bean.PrivateMedia;
import com.yc.liaolive.interfaces.PerfectClickListener;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@Outlook.com
 * 2018/9/14
 * 私密、公开等照片预览控件
 */

public class PrivateTouchImageView extends RelativeLayout {

    private static final String TAG = "PrivateTouchImageView";
    private View mLlTipsView;
    private PinchImageView mPinchImageView;
    private PrivateMedia mImageInfo;

    public PrivateTouchImageView(Context context) {
        super(context);
        init(context);
    }

    public PrivateTouchImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.view_private_touch_image,this);
        mPinchImageView = (PinchImageView) findViewById(R.id.view_image);
        mPinchImageView.setOnDoubleClickListener(new PinchImageView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick() {
                if(null!=mOnFunctionListener) mOnFunctionListener.onDoubleClick();
            }
        });
        mPinchImageView.setOnClickListener(new PerfectClickListener(350) {
            @Override
            protected void onNoDoubleClick(View v) {
                if(null!=mOnFunctionListener) mOnFunctionListener.onClick();
            }
        });
    }

    /**
     * 绑定信息
     * @param imageInfo
     */
    public void setImageData(PrivateMedia imageInfo) {
        if(null==imageInfo) return;
        this.mImageInfo=imageInfo;
        mImageInfo.setIs_private(0);//默认全部公开
        setImage(mImageInfo.getFile_path());
    }


    /**
     * 初始预览
     */
    public void initPreview() {
//        if(null==mImageInfo) return;
//        setImage(mImageInfo.getFile_path());
//        if(null!=mPinchImageView) mPinchImageView.setVisibility(VISIBLE);
        //自己看自己的
//        if(TextUtils.equals(mImageInfo.getUserid(),UserManager.getInstance().getUserId())){
//            Logger.d(TAG,"initPreview---自己看自己的");
//            AnimationUtil.goneTransparentView(mBlurImageView);
//            if(null!=mPinchImageView) mPinchImageView.setVisibility(VISIBLE);
//            return;
//        }
        //如果是公开的
//        if(0==mImageInfo.getIs_private()){
//            Logger.d(TAG,"initPreview---公开的");
//            AnimationUtil.goneTransparentView(mBlurImageView);
//            if(null!=mPinchImageView) mPinchImageView.setVisibility(VISIBLE);
//            return;
//        }
        //查看私密照片
//        if(null!=mBlurImageView) mBlurImageView.setVisibility(VISIBLE);
//        showTipsView();
    }

//    private void showTipsView() {
//        if(null!=mLlTipsView&&mLlTipsView.getVisibility()==VISIBLE) return;
//        //私密图层
//        mLlTipsView = findViewById(R.id.ll_tips_layout);
//        mLlTipsView.setVisibility(View.VISIBLE);
//        TextView tvTips = (TextView) findViewById(R.id.tv_tips);
//        ImageView icTips = (ImageView) findViewById(R.id.ic_tips);
//        icTips.setImageResource(R.drawable.ic_private_media_preview_touch);
//        tvTips.setText(getResources().getString(R.string.media_preview_tips));
//    }

    /**
     * 还原
     */
    public void reset() {
        if(null!=mPinchImageView) {
            mPinchImageView.reset();
        }
    }
    /**
     * 设置正常的照片
     */
    private void setImage(String url){
        //公开的照片就用封面
        if(null!=mPinchImageView) Glide.with(getContext()).
                load(url)
                .error(R.drawable.ic_default_live_icon)
                .placeholder(R.drawable.ic_default_live_icon)
                .dontAnimate()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                .skipMemoryCache(true)
                .into(mPinchImageView);
    }

//    private void hideTouchTips() {
//        if(null!=mLlTipsView&&mLlTipsView.getVisibility()==VISIBLE){
//            TextView tvTips = (TextView) findViewById(R.id.tv_tips);
//            ImageView icTips = (ImageView) findViewById(R.id.ic_tips);
//            icTips.setImageResource(0);
//            tvTips.setText("");
//            mLlTipsView.setVisibility(View.GONE);
//            mLlTipsView=null;
//        }
//    }

    private float downX;//首次按下位置
    private float downY;//首次按下位置

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(null!=mImageInfo){
//            //如果用户未成功购买多媒体文件，尝试购买
//            if(TextUtils.isEmpty(mImageInfo.getFile_path())){
//                if(event.getAction()==MotionEvent.ACTION_DOWN){
//                    if(null!=mOnFunctionListener) mOnFunctionListener.buyMediaFile();
//                }
//                return true;
//            }
//            switch (event.getAction()) {
//                //按下，禁止用户上下左右滑动
//                case MotionEvent.ACTION_DOWN:
//                    downX=event.getX();
//                    downY=event.getY();
//                    //查看私密的照片
//                    if(!TextUtils.equals(mImageInfo.getUserid(),UserManager.getInstance().getUserId())&&1==mImageInfo.getIs_private()){
//                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_YES);
//                        hideTouchTips();
//                        AnimationUtil.goneTransparentView(mBlurImageView);
//                        if(null!=mPinchImageView) mPinchImageView.setVisibility(VISIBLE);
//                    }
//                    break;
//                //手指移动中，达到移动距离阈值 允许用户上下左右滑动,并立即还原封面显示
//                case MotionEvent.ACTION_MOVE:
//                    float moveX=event.getX();
//                    float moveY=event.getY();
//                    Logger.d(TAG,"downX:"+downX+",downY:"+downY+",moveX:"+moveX+",moveY:"+moveY+",absX:"+Math.abs(moveX-downX)+",absY:"+Math.abs(moveY-downY));
//                    if(Math.abs(moveX-downX)>=180||Math.abs(moveY-downY)>=180){
//                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_NO);
//                        //还原封面
//                        if(null!=mBlurImageView&&mBlurImageView.getVisibility()!=VISIBLE) mBlurImageView.setVisibility(VISIBLE);
//                        if(null!=mPinchImageView&&mPinchImageView.getVisibility()!=GONE) mPinchImageView.setVisibility(GONE);
//                    }
//                    break;
//                //松手，允许用户上下左右滑动
//                case MotionEvent.ACTION_UP:
//                    downX=0;
//                    downY=0;
//                    if(!TextUtils.equals(mImageInfo.getUserid(),UserManager.getInstance().getUserId())&&1==mImageInfo.getIs_private()){
//                        ApplicationManager.getInstance().observerUpdata(Constant.OBSERVER_TOUCH_DISPATCHTOUCH_NO);
//                        if(null!=mBlurImageView) mBlurImageView.setVisibility(VISIBLE);
//                        if(null!=mPinchImageView) mPinchImageView.setVisibility(GONE);
//                    }
//                    break;
//            }
//        }
//        return true;
//    }


    public interface OnFunctionListener{
        void onClick();
        void onDoubleClick();
        void buyMediaFile();
    }
    private OnFunctionListener mOnFunctionListener;

    public void setOnFunctionListener(OnFunctionListener onFunctionListener) {
        mOnFunctionListener = onFunctionListener;
    }
}
