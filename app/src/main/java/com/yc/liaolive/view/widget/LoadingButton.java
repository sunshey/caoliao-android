package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/3/3.
 * 一个局部Loading的Submit组件
 * 用户可自定义的属性：背景颜色，处理中背景颜色，文字：内容、颜色、大小。处理中src
 */

public class LoadingButton extends FrameLayout {

    private AnimationDrawable mAnimationDrawable;
    private boolean isSubmiting;//是否正在提交中
    private TextView mTv_content;
    private LinearLayout mBtn_submit;
    private Drawable mBackgroundBlock;//当按钮不可点击的时候颜色
    private Drawable mBackground;//正常的背景颜色
    private ImageView mIc_loading;
    private final RectF roundRect = new RectF();
    private float rect_adius = 10;
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();
    private String mContentBlockText="处理中...";//处理中文本内容
    private String mText="提交";//默认的文本内容


    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context,attrs);
    }

    private void initViews(Context context,AttributeSet attrs) {
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        zonePaint.setColor(Color.WHITE);
        //
        float density = getResources().getDisplayMetrics().density;
        rect_adius = rect_adius * density;
        View.inflate(context, R.layout.view_loading_submit_layout,this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton);
        Resources resources = context.getResources();
        //获取用户自定义的属性值
        Drawable background = typedArray.getDrawable(R.styleable.LoadingButton_loadBackground);
        Drawable backgroundBlock = typedArray.getDrawable(R.styleable.LoadingButton_loadBackgroundBlock);
        mBackgroundBlock =backgroundBlock;

        int textColor = typedArray.getColor(R.styleable.LoadingButton_loadButtonTextColor, resources.getColor(R.color.white));
        float textSize = typedArray.getDimension(R.styleable.LoadingButton_loadTextSize, 15);
        String contentText = typedArray.getString(R.styleable.LoadingButton_loadButtonText);
        mContentBlockText = typedArray.getString(R.styleable.LoadingButton_loadButtonBolckText);
        Drawable loadIcon = typedArray.getDrawable(R.styleable.LoadingButton_loadIconSrc);
        mBtn_submit = (LinearLayout) findViewById(R.id.ll_submit);
        mIc_loading = (ImageView) findViewById(R.id.ic_loading);
        mTv_content = (TextView) findViewById(R.id.tv_content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtn_submit.setBackground(background);
        }
        mBackground =background;
        this.mText=contentText;
        mTv_content.setText(contentText);
        mTv_content.setTextColor(textColor);
        mTv_content.setTextSize(textSize);

        try {
            if(null!=loadIcon){
                mIc_loading.setImageDrawable(loadIcon);
            }else{
                mIc_loading.setImageResource(R.drawable.progress_anim);
            }
            mAnimationDrawable = (AnimationDrawable) mIc_loading.getDrawable();
        }catch (Exception e){

        }

        typedArray.recycle();

        mBtn_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交
                if(isSubmiting) return;
                if(null!=mOnClickSubmitListener){
                    mOnClickSubmitListener.onSubmit(v);
                }
            }
        });
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int w = getWidth();
        int h = getHeight();
        roundRect.set(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(roundRect, rect_adius, rect_adius, zonePaint);
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        canvas.restore();
    }

    public void setBroadius(float adius) {
        rect_adius = adius;
        invalidate();
    }

    /**
     * 设置Button背景颜色
     * @param background
     */
    public void setBackGround(Drawable background){
        mBackground =background;
        if(null!=mBtn_submit){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBtn_submit.setBackground(mBackground);
            }
        }
    }

    /**
     * 设置Button锁定背景颜色
     * @param background
     */
    public void setButtonBackgroundBlock(Drawable background){
        this.mBackgroundBlock =background;
    }

    /**
     * 设置Button文字内容
     * @param content
     */
    public void setButtonContent(String content){
        if(null!=mTv_content){
            mTv_content.setText(content);
        }
    }

    /**
     * 设置Button文字颜色
     * @param color
     */
    public void setButtonTextColor(int color){
        if(null!=mTv_content){
            mTv_content.setTextColor(color);
        }
    }

    /**
     * 设置Button文字大小
     * @param size
     */
    public void setButtonTextSize(float size){
        if(null!=mTv_content){
            mTv_content.setTextSize(size);
        }
    }

    /**
     * 设置ButtonLoading SRC
     * @param src
     */
    public void setButtonLoadSrc(int src){
        if(null!=mIc_loading){
            mIc_loading.setBackgroundResource(src);
        }
    }

    public void setText(String text){
        this.mText=text;
        if(null!=mTv_content) mTv_content.setText(mText);
    }

    public void setBlockText(String text){
        this.mContentBlockText=text;
        if(null!=mTv_content) mTv_content.setText(mContentBlockText);
    }


    /**
     * 开始播放提交中动画
     */
    public void startSubmitAnimation() {
        if(null!=mBtn_submit){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBtn_submit.setBackground(mBackgroundBlock);
            }
        }
        if(null!=mTv_content) mTv_content.setText(mContentBlockText);
        if(null!=mIc_loading&&mIc_loading.getVisibility()!=VISIBLE) mIc_loading.setVisibility(VISIBLE);
        if(null!=mAnimationDrawable&&!mAnimationDrawable.isRunning()) mAnimationDrawable.start();
        isSubmiting=true;
    }

    /**
     * 结束播放提交中动画
     */
    public void stopSubmitAnimation() {
        if(null!=mBtn_submit) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBtn_submit.setBackground(mBackground);
        }
        if(null!=mTv_content) mTv_content.setText(mText);
        if(null!=mAnimationDrawable&&mAnimationDrawable.isRunning()) mAnimationDrawable.stop();
        if(null!=mIc_loading&&mIc_loading.getVisibility()!=GONE) mIc_loading.setVisibility(GONE);
        isSubmiting=false;
    }


    public interface OnClickSubmitListener{
        void onSubmit(View view);
    }

    private OnClickSubmitListener mOnClickSubmitListener;

    public void setOnClickSubmitListener(OnClickSubmitListener onClickSubmitListener) {
        mOnClickSubmitListener = onClickSubmitListener;
    }
}
