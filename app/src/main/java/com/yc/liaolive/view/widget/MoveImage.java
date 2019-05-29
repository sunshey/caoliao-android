package com.yc.liaolive.view.widget;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;


/**
 * TinyHung@Outlook.com
 * 2018/7/23
 */

public class MoveImage extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener{

    private boolean mInit;
    private boolean mIsCanMove;
    private float mLastX;
    private float mLastY;
    private Matrix mScaleMatrix;
    private int mMoveSlop;

    public MoveImage(android.content.Context context) {
        super(context);
        init(context);
    }

    public MoveImage(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(android.content.Context context) {
        mScaleMatrix = new Matrix();
        mMoveSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnTouchListener(this);
        mIsCanMove = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (!mInit) {
            Drawable d = getDrawable();
            if (d != null) {
                float scale = 1.0f;

                int dw = d.getIntrinsicWidth();
                int dh = d.getIntrinsicHeight();

                int width = getWidth();
                int height = getHeight();

                if (dw > width && dh < height) {
                    scale = width * 1.0f / dw;
                } else if (dw < width && dh > height) {
                    scale = height * 1.0f / dh;
                } else if (dw < width && dh < height) {
                    scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
                } else if (dw > width && dh > height) {
                    scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
                } else {
                    scale = 1.0f;
                }

                int centerX = (width - dw) / 2;
                int centerY = (height - dh) / 2;
                mScaleMatrix.postTranslate(centerX, centerY);
                scale = scale / 2.0f;
                mScaleMatrix.postScale(scale, scale, width / 2, height / 2);
                setImageMatrix(mScaleMatrix);
            }
        }
        mInit = true;
    }

    private RectF getMatrixRectF() {
        RectF rectf = new RectF();
        final Matrix matrix = mScaleMatrix;
        Drawable d = getDrawable();
        if (d != null) {
            rectf.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectf);
        }
        return rectf;
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mMoveSlop;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mIsCanMove = rectF.contains(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsCanMove) {
                    float x = event.getX();
                    float y = event.getY();
                    float deltaX = x - mLastX;
                    float deltaY = y - mLastY;

                    if (isMoveAction(deltaX, deltaY)) {
                        checkBorderWhenMove(deltaX, deltaY, rectF);
                        setImageMatrix(mScaleMatrix);
                    }

                    mLastX = x;
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsCanMove = false;
                break;
        }
        return true;
    }

    private void checkBorderWhenMove(float deltaX, float deltaY, RectF rectF) {
        int width = getWidth();
        int height = getHeight();

        if (rectF.left + deltaX < 0) {
            deltaX = -rectF.left;
        } else if (rectF.right + deltaX > width) {
            deltaX = width - rectF.right;
        }
        if (rectF.top + deltaY < 0) {
            deltaY = -rectF.top;
        } else if (rectF.bottom + deltaY > height) {
            deltaY = height - rectF.bottom;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }
}
