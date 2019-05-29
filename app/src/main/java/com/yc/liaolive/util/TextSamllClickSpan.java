package com.yc.liaolive.util;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/6/27 15:49
 * 这是一个处理包含 #XXXX#关键字 的点击事件
 */
public class TextSamllClickSpan extends ClickableSpan {

    private static final String TAG = TextSamllClickSpan.class.getSimpleName();
    private final int color;//字体颜色
    private final int mScreenWidth;


    public TextSamllClickSpan(int color){
        this.color=color;
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    public TextSamllClickSpan(){
        this.color= Color.parseColor("#FCB840");
        mScreenWidth = ScreenUtils.getScreenWidth();
    }


    /**
     * 设置颜色
     * @param ds
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
        int screenDensity = ScreenUtils.getScreenDensity();
        float textSize=30f;
        if(screenDensity>480){
            textSize=38;
        }else if(screenDensity<=480&&screenDensity>400){
            textSize=36;
        }else if(screenDensity<=400&&screenDensity>320){
            textSize=33;
        }else if(screenDensity<=320&&screenDensity>280){
            textSize=25;
        }else if(screenDensity<=280&&screenDensity>220){
            textSize=17;
        }else{
            textSize=20;
        }
        // 5.2寸1080分辨率手机像素密度为480， 字体大小建议44-43 f大小,每11.156789个密度占一个字体大小
        ds.setTextSize(textSize);//根据屏幕密度来算出字体大小
        ds.setUnderlineText(false);
    }

    /**
     * 点击事件的监听
     * @param widget
     */
    @Override
    public void onClick(View widget) {

    }
}
