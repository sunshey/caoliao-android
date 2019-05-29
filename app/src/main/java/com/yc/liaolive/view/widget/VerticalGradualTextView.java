package com.yc.liaolive.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.util.AttributeSet;
/**
 * TinyHung@Outlook.com
 * 2018/6/15
 * 垂直渐变颜色的TextView
 */

public class VerticalGradualTextView extends android.support.v7.widget.AppCompatTextView {

    private LinearGradient mShaderVertical;

    public VerticalGradualTextView(Context context) {
        super(context);
    }

    public VerticalGradualTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    /**
     * 字体描边
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        /**
//         x0 渐变起点坐标x位置
//         y0 渐变起点坐标y位置
//         x1 渐变起终点坐标x位置
//         y1 渐变起终点坐标y位置
//         color0 渐变颜色起始色
//         color1 渐变颜色终止色
//         Shader.TileMode tile 平铺方式
//         */
//        if(null==mShaderVertical) mShaderVertical = new LinearGradient(0, getHeight()/4, 0, getHeight(), Color.parseColor("#FFEAB0"),  Color.parseColor("#B99F62"), Shader.TileMode.CLAMP);
//        // 复制原来TextViewg画笔中的一些参数
//        TextPaint paint = getPaint();
//        //设置垂直的渐变颜色
//        paint.setShader(mShaderVertical);
//        String text = getText().toString();
//        //设置字体的水平间距
////        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
////            strokePaint.setLetterSpacing(0.1f);
////        }
//        //在文本底层画出带描边的文本
//        canvas.drawText(text, 0, getBaseline(), paint);
        super.onDraw(canvas);
    }
}
