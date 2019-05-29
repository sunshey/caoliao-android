package com.yc.liaolive.view.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

/**
 * @time 2017/2/27 12:52
 * @des $操作完成动画效果
 */
public class FinlishView extends View implements ValueAnimator.AnimatorUpdateListener{

    private Context mContext;
    /**
     * paint对象
     */
    private Paint mPaint;
    /**
     * Path和对应的空Path用来填充
     */
    private Path mPathCircle;
    private Path mPathCircleDst;
    private Path mPathRight;
    private Path mPathRightDst;
    private Path mPathWrong1;
    private Path mPathWrong2;
    private Path mPathWrong1Dst;
    private Path mPathWrong2Dst;
    /**
     * Path管理
     */
    private PathMeasure mPathMeasure;
    /**
     * 动画
     */
    private ValueAnimator mCircleAnimator;
    private ValueAnimator mRightAnimator;
    private ValueAnimator mWrong1Animator;
    private ValueAnimator mWrong2Animator;
    /**
     * 当前绘制进度占总Path长度百分比
     */
    private float mCirclePercent;
    private float mRightPercent;
    private float mWrong1Percent;
    private float mWrong2Percent;
    /**
     * 线宽
     */
    private int mLineWidth;
    /**
     * 正确动画 错误动画
     */
    public static final int RESULT_RIGHT = 1;
    public static final int RESULT_WRONG = 2;
    /**
     * 当前结果类型
     */
    private int mResultType = RESULT_WRONG;

    int SUCCESSFUL_COLOR=Color.rgb(0,203,13);//默认的成功颜色
    int FAIL_COLOR=Color.rgb(255,78,92);//失败颜色

    public FinlishView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public FinlishView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public FinlishView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mLineWidth = dp2px(3);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(SUCCESSFUL_COLOR);
        initPath();
    }

    public void initPath() {
        mPathCircle = new Path();
        mPathCircleDst = new Path();
        mPathRight = new Path();
        mPathRightDst = new Path();
        mPathWrong1 = new Path();
        mPathWrong2 = new Path();
        mPathWrong1Dst = new Path();
        mPathWrong2Dst = new Path();

        mPathMeasure = new PathMeasure();

        //实例化对象
        mCircleAnimator = ValueAnimator.ofFloat(0, 1);
        //设置时长为1000ms
        mCircleAnimator.setDuration(1200);
        //开始动画
        mCircleAnimator.start();
        //设置动画监听
        mCircleAnimator.addUpdateListener(this);

        mRightAnimator = ValueAnimator.ofFloat(0, 1);
        mRightAnimator.setDuration(600);
        mRightAnimator.addUpdateListener(this);

        mWrong1Animator = ValueAnimator.ofFloat(0, 1);
        mWrong1Animator.setDuration(600);
        mWrong1Animator.addUpdateListener(this);
        mWrong2Animator = ValueAnimator.ofFloat(0, 1);
        mWrong2Animator.setDuration(600);
        mWrong2Animator.addUpdateListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mResultType == RESULT_RIGHT) {
            mPaint.setColor(SUCCESSFUL_COLOR);
        } else {
            mPaint.setColor(FAIL_COLOR);
        }

        //画圆
        mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - mLineWidth, Path.Direction.CW);
        mPathMeasure.setPath(mPathCircle, false);
        mPathMeasure.getSegment(0, mCirclePercent * mPathMeasure.getLength(), mPathCircleDst, true);
        canvas.drawPath(mPathCircleDst, mPaint);
        if (mResultType == RESULT_RIGHT) {
            //画对勾
            mPathRight.moveTo(getWidth() / 4, getWidth() / 2);
            mPathRight.lineTo(getWidth() / 2, getWidth() / 4 * 3);
            mPathRight.lineTo(getWidth() / 4 * 3, getWidth() / 4);
            if (mCirclePercent == 1) {
                mPathMeasure.nextContour();
                mPathMeasure.setPath(mPathRight, false);
                mPathMeasure.getSegment(0, mRightPercent * mPathMeasure.getLength(), mPathRightDst, true);
                canvas.drawPath(mPathRightDst, mPaint);
            }
        } else {
            mPathWrong1.moveTo(getWidth() / 4 * 3, getWidth() / 4);
            mPathWrong1.lineTo(getWidth() / 4, getWidth() / 4 * 3);

            mPathWrong2.moveTo(getWidth() / 4, getWidth() / 4);
            mPathWrong2.lineTo(getWidth() / 4 * 3, getWidth() / 4 * 3);

            if (mCirclePercent == 1) {
                mPathMeasure.nextContour();
                mPathMeasure.setPath(mPathWrong1, false);
                mPathMeasure.getSegment(0, mWrong1Percent * mPathMeasure.getLength(), mPathWrong1Dst, true);
                canvas.drawPath(mPathWrong1Dst, mPaint);
            }
            if (mWrong1Percent == 1) {
                mPathMeasure.nextContour();
                mPathMeasure.setPath(mPathWrong2, false);
                mPathMeasure.getSegment(0, mWrong2Percent * mPathMeasure.getLength(), mPathWrong2Dst, true);
                canvas.drawPath(mPathWrong2Dst, mPaint);
            }
        }
    }



    private int dp2px(int dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (scale * dp + 0.5f);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //圆形动画
        if (animation.equals(mCircleAnimator)) {
            mCirclePercent = (float) animation.getAnimatedValue();
            invalidate();
            if (mCirclePercent == 1) {
                if (mResultType == RESULT_RIGHT)
                    mRightAnimator.start();
                else
                    mWrong1Animator.start();
            }
        }
        //正确时 对勾动画
        else if (animation.equals(mRightAnimator)) {
            mRightPercent = (float) animation.getAnimatedValue();
            invalidate();
        }
        //错误时 右侧动画
        else if (animation.equals(mWrong1Animator)) {
            mWrong1Percent = (float) animation.getAnimatedValue();
            invalidate();
            if (mWrong1Percent == 1) {
                mWrong2Animator.start();
            }
        }
        //错误时 左侧动画
        else if (animation.equals(mWrong2Animator)) {
            mWrong2Percent = (float) animation.getAnimatedValue();
            invalidate();
        }
    }

    public void setmResultType(int resultType) {
        this.mResultType = resultType;
        invalidate();
    }


    public int getResultType(){
        return mResultType;
    }
    /**
     * 固定写死了宽高，可重新手动调配
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dp2px(60), dp2px(60));
    }

    public void setFailColor(int failColor) {
        this.FAIL_COLOR = FAIL_COLOR;
    }


    public void setSuccessColor(int color) {
        this.SUCCESSFUL_COLOR=color;
    }
}
