package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/8/23
 * 自定义边角View
 * 兼容5.0以下系统，替代CradView
 */

public class CornerItemLayout extends FrameLayout {

    private Path mPath;
    private RectF mRect;
    private int mRadiusX;//X轴半径
    private int mRadiusY;//Y轴半径
    private int mRadius;//优先使用全角半径

    public CornerItemLayout(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public CornerItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPath=new Path();
        mRect=new RectF();
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CornerItemLayout);
            mRadiusX = typedArray.getInt(R.styleable.CornerItemLayout_itemLayoutCornerRadiusX, 0);
            mRadiusY = typedArray.getInt(R.styleable.CornerItemLayout_itemLayoutCornerRadiusY, 0);
            mRadius = typedArray.getInt(R.styleable.CornerItemLayout_itemLayoutCornerRadius, 0);
            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(null==mPath) return;
        mPath.reset();
        mRect.set(0, 0, w, h);
        mPath.addRoundRect(mRect ,mRadius>0?mRadius:mRadiusX , mRadius>0?mRadius:mRadiusY , Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if(null==mPath) canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
