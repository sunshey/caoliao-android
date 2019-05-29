package com.yc.liaolive.view.gold;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import com.yc.liaolive.R;
import java.util.ArrayList;

/**
 * 类功能描述：</br>
 *红包金币仿雨滴下落效果
 * @author yuyahao
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 */
public class FlakeView extends View {

    Bitmap droid;
    int numFlakes = 0;
    ArrayList<Flake> flakes = new ArrayList<Flake>(); // List of current flakes
    public ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    long startTime, prevTime; // Used to track elapsed time for animations and fps
    int frames = 0;     // Used to track frames per second
    Paint textPaint;    // Used for rendering fps text
    float fps = 0;      // frames per second
    Matrix m = new Matrix(); // Matrix used to translate/rotate each flake during rendering
    String fpsString = "";
    String numFlakesString = "";
    //一个轮回中有多少个金币掉落
    private int quantity=58;

    /**
     * 利用属性动画进行改变每一个小金币的属性值
     * 这里是将每一个金币看作为一个类
     * 在Java中万物皆对象，一个金币就是一个对象，
     * 拥有自己bitmap，宽高，大小，还有自己的坐标
     * the animator
     */
    public FlakeView(Context context) {
        super(context);
        if(null!=context&&null!=animator){
            droid = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_gift_money);
            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(34);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator arg0) {
                    long nowTime = System.currentTimeMillis();
                    float secs = (float) (nowTime - prevTime) / 100f;
                    prevTime = nowTime;
                    for (int i = 0; i < numFlakes; ++i) {
                        Flake flake = flakes.get(i);
                        flake.y += (flake.speed * secs);
                        if (flake.y > getHeight()) {
                            // If a flake falls off the bottom, send it back to the top
                            flake.y = 0 - flake.height;
                        }
                        flake.rotation = flake.rotation + (flake.rotationSpeed * secs);
                    }
                    // Force a redraw to see the flakes in their new positions and orientations
                    invalidate();
                }
            });
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(3000);
        }
    }



    private void setNumFlakes(int quantity) {
        numFlakes = quantity;
        numFlakesString = "numFlakes: " + numFlakes;
    }

    /**
     *增加每一个小金币属性
     */
    public void addFlakes(int quantity) {
        this.quantity=quantity;
        for (int i = 0; i < quantity; ++i) {
            flakes.add(Flake.createFlake(getWidth(), droid,getContext()));
        }
        setNumFlakes(numFlakes + quantity);
    }

    /**
     * 减去指定数量的金币，其他的金币属性保持不变
     */
    void subtractFlakes(int quantity) {
        for (int i = 0; i < quantity; ++i) {
            int index = numFlakes - i - 1;
            flakes.remove(index);
        }
        setNumFlakes(numFlakes - quantity);
    }

    /**
     * nSizeChanged()实在布局发生变化时的回调函数，间接回去调用onMeasure, onLayout函数重新布局
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Reset list of droidflakes, then restart it with 8 flakes
        flakes.clear();
        numFlakes = 0;
        addFlakes(quantity);
        // Cancel animator in case it was already running
        if(null!=animator) animator.cancel();
        // Set up fps tracking and start the animation
        startTime = System.currentTimeMillis();
        prevTime = startTime;
        frames = 0;
        if(null!=animator) animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < numFlakes; ++i) {
            Flake flake = flakes.get(i);
            m.setTranslate(-flake.width / 2, -flake.height / 2);
            m.postRotate(flake.rotation);
            m.postTranslate(flake.width / 2 + flake.x, flake.height / 2 + flake.y);
            canvas.drawBitmap(flake.bitmap, m, null);
        }
        ++frames;
        long nowTime = System.currentTimeMillis();
        long deltaTime = nowTime - startTime;
        if (deltaTime > 1000) {
            float secs = (float) deltaTime / 1000f;
            fps = (float) frames / secs;
            startTime = nowTime;
            frames = 0;
        }
    }
    /**
     * 生命周期 pause
     */
    public void pause() {
        if(null!=animator) animator.cancel();
    }
    /**
     * 生命周期 resume
     */
    public void resume() {
        if(null!=animator) animator.start();
    }

    public void onDestroy(){
        if(null!=animator) animator.cancel(); animator=null;
    }
}
