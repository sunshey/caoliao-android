package com.yc.liaolive.media.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * TinyHung@Outlook.com
 * 2018/8/20
 * 抢聊组件遮罩层
 */

public class ShadeView extends View{

    private int mRadius;
    private Paint mPaint = new Paint();

    public ShadeView(Context context) {
        this(context,null);
    }

    public ShadeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShadeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //禁用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        //新建图层
        int layerID = canvas.saveLayer(0, 0, width, height, mPaint, Canvas.ALL_SAVE_FLAG);

        //设置渐变的背景颜色
        Paint paint = new Paint();
        LinearGradient backGradient = new LinearGradient(0, 0, 0, height, new int[]{Color.parseColor("#FF181818"), Color.parseColor("#FF1B1B1C") ,Color.parseColor("#FF202021") }, null, Shader.TileMode.CLAMP);
        paint.setShader(backGradient);
//        canvas.drawColor(0xFF181818);
        canvas.drawRect(0, 0, width, height, paint);

        int canvasWidth = canvas.getWidth();
        mRadius = canvasWidth / 2;

        //正常绘制黄色的圆形
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setColor(0xFFFFFFFF);

        //todo 这里直接居中
        canvas.drawCircle(width/2, height/2, mRadius, mPaint);

        //最后将画笔去除Xfermode
        mPaint.setXfermode(null);

        //还原图层
        canvas.restoreToCount(layerID);
    }
}
