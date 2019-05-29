package com.yc.liaolive.view.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * TinyHung@Outlook.com
 * 2017/8/30.
 * 双层View拖动滑动View
 */

public class DragLayout extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private ViewGroup menuView;
    private ViewGroup mainView;
    private int mWidth;
    private int mHeight;
    private int maxDragRange;

    public DragLayout(Context context) {
        this(context,null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, callback);
    }
    //定义拖拽状态
    private enum DragState{
        OPEN,DRAGGING,CLOSE
    }
    //定义当前状态,也就是默认状态
    private DragState currentState=DragState.CLOSE;
    //保存上一次状态
    private DragState preState=DragState.CLOSE;
    //1、定义接口  View身上有这样OnStateChangedListener的监听方法，不要使用
    public interface OnDragStateChangedListener{
        public void onOpen();
        public void onDragging(float percent);
        public void onClose();
    }

    private OnDragStateChangedListener onDragStateChangedListener;
    public void setOnDragStateChangedListener(OnDragStateChangedListener onDragStateChangedListener) {
        this.onDragStateChangedListener = onDragStateChangedListener;
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        /**
         * 尝试捕获一个view  尝试拖拽一个view
         * @param child     被拖拽的view
         * @param pointerId     多点触碰，第一个手指按下的点pointerId=0， 后面的依次++
         * @return      返回值决定了孩子能否被拖拽, 如果返回false，后面的回调方法基本上都不调用了
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }
        /**
         * View已经被捕获了
         * @param capturedChild  被拖拽的iew
         * @param activePointerId      多点触碰，第一个手指按下的点pointerId=0， 后面的依次++
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }
        /**
         *  限制View水平方向的位置    相当于告诉ViewDragHelper下一次滑动到哪个位置
         * @param child   正在被拖拽的孩子
         * @param left    水平方向距离左边的距离  child.getLeft()+dx
         * @param dx       水平方向的偏移量
         * @return      返回值决定了View被拖拽的位置
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            int oldLeft = child.getLeft();
////            Log.i("test", "child = [" + child.getTag() + "], left = [" + left + "], dx = [" + dx + "], oldLeft = [" + oldLeft + "]");
            if (child==mainView){
                //限制主界面的拖拽范围
                left=fixLeftRange(left);
            }
            return left;
        }

        /**
         * 当View的位置发生改变的时候被调用
         * @param changedView      被拖拽的view
         * @param left  距离屏幕左边的距离   这个left跟changedView有关
         * @param top   距离屏幕上边的距离
         * @param dx    水平方向的偏移量
         * @param dy    垂直方向的偏移量
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView==menuView){
                //强行固定菜单界面不动
                menuView.offsetLeftAndRight(-dx);
                //获取mainView的oldLeft
                int oldLeft = mainView.getLeft();
                int newLeft=oldLeft+dx;
                //修正水平方向拖拽的范围
                newLeft=fixLeftRange(newLeft);
                dx=newLeft-oldLeft;
                //移动主界面
                mainView.offsetLeftAndRight(dx);//移动的方法一
//                mainView.layout(newLeft,0,newLeft+mWidth,mHeight);//移动的方法二
            }
            //滑动的百分比  left要改成
            float percent=mainView.getLeft()*1.0f/maxDragRange;
            //执行一些列的伴随动画
            executeAnimation(percent);
            //3、执行接口回调
            executeListener(percent);
        }
        /**
         *当释放View的时候被调用
         * @param releasedChild     被释放的View
         * @param xvel      水平方向的速度
         * @param yvel      垂直方向的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
//            Log.i("test", "releasedChild = [" + releasedChild.getTag() + "], xvel = [" + xvel + "], yvel = [" + yvel + "]");
            if (xvel==0&&mainView.getLeft()>maxDragRange*0.5){
                //打开
                open();
            }else if (xvel>0){
                open();
            }else{
                close();//关闭
            }
        }
    };
    //执行接口回调的方法
    private void executeListener(float percent) {
        //更当前状态之前先保存上一次状态
        preState=currentState;
        //更当前状态
        currentState=updateCurrentState(percent);
        if (onDragStateChangedListener!=null){
            if (currentState==DragState.OPEN && preState!=currentState){
                //执行打开的回调方法
                onDragStateChangedListener.onOpen();
            }else if (currentState==DragState.CLOSE && preState!=currentState){
                //执行关闭的回调方法
                onDragStateChangedListener.onClose();
            }else{
                //执行正在拖拽的回调方法
                onDragStateChangedListener.onDragging(percent);
            }
        }
    }
    //更当前状态的方法
    private DragState updateCurrentState(float percent) {
        if (percent==1.0f){
            return DragState.OPEN;
        }else if (percent==0f){
            return DragState.CLOSE;
        }
        return DragState.DRAGGING;
    }

    //执行一些列的伴随动画的方法
    private void executeAnimation(float percent) {
//        Log.i("test","percent="+percent);
        //主界面
            //缩放动画  percent 0.0--->1.0    Scale  1.0f-->0.8f
//        Float evaluateFloat = CommonUtils.evaluateFloat(percent, 1.0f, 0.8f);
//        ViewCompat.setScaleX(mainView,evaluateFloat);
//        ViewCompat.setScaleY(mainView, evaluateFloat);
//        //菜单界面
//            //缩放动画  percent 0.0f--->1.0f    Scale  0.6f-->1.0f
//        evaluateFloat= CommonUtils.evaluateFloat(percent,0.6f,1.0f);
//        ViewCompat.setScaleX(menuView, evaluateFloat);
//        ViewCompat.setScaleY(menuView, evaluateFloat);
//            //平移动画  percent 0.0f--->1.0f    Translation  -menuView.getWidth()*0.5f-->0f
//        evaluateFloat=CommonUtils.evaluateFloat(percent,-menuView.getWidth()*0.5f,0f);
//        ViewCompat.setTranslationX(menuView, evaluateFloat);
//            //透明度动画  percent 0.0f--->1.0f    Alpha  0.3f-->1.0f
//        evaluateFloat=CommonUtils.evaluateFloat(percent,0.3f,1.0f);
//        ViewCompat.setAlpha(menuView,evaluateFloat);
//        //背景亮度  注意点：必须要设置背景图片
//        int evaluateArgb = CommonUtils.evaluateArgb(percent, Color.BLACK, Color.TRANSPARENT);
//        getBackground().setColorFilter(evaluateArgb, PorterDuff.Mode.SRC_OVER);
    }

    //关闭菜单
    private void close() {
//        mainView.layout(0,0,mWidth,mHeight);
        if (viewDragHelper.smoothSlideViewTo(mainView,0,0)){
//            invalidate();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    //打开菜单
    private void open() {
//        mainView.layout(maxDragRange,0,maxDragRange+mWidth,mHeight);
        //返回值决定了要不要执行动画，如果是true表示要执行，那么就发起动画
       if (viewDragHelper.smoothSlideViewTo(mainView,maxDragRange,0)){
           //执行动画
//           invalidate();
           ViewCompat.postInvalidateOnAnimation(this);
       }
    }
    //计算每次滑动的距离
    @Override
    public void computeScroll() {
        super.computeScroll();
        //一帧一帧的执行动画，知道动画结束了就返回false
        if (viewDragHelper.continueSettling(true)){
            //重绘
//            invalidate();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    //限制主界面的拖拽范围
    private int fixLeftRange(int left) {
        if (left>maxDragRange){
            left= (int) maxDragRange;
        }else if (left<0){
            left=0;
        }
        return left;
    }

    //2、把事件交给ViewDragHelper去拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }
    //3、把事件交给ViewDragHelper去处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
    //测量完成之后会被调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //view的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //最大拖拽范围
        maxDragRange = (int) (mWidth * 0.6f);
    }

    //当布局加载完成之后被调
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //健壮性判断
        if (getChildCount()!=2){
            throw new IllegalStateException("you must have only two children");
        }
        if (!(getChildAt(0) instanceof ViewGroup)||!(getChildAt(1) instanceof ViewGroup)){
            throw new IllegalArgumentException("your child must be instance of ViewGroup");
        }
        //获取菜单和主界面View
        menuView = (ViewGroup) getChildAt(0);
        mainView = (ViewGroup) getChildAt(1);
    }
}
