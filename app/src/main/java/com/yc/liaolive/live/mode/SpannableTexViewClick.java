package com.yc.liaolive.live.mode;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/5/20
 */
public class SpannableTexViewClick extends ClickableSpan {

    private final int color;//字体颜色
    private boolean isUnderline;//是否显示下划线

    public SpannableTexViewClick(int color){
        this.color=color;
    }

    public SpannableTexViewClick(int color,boolean isUnderline){
        this.color=color;
        this.isUnderline=isUnderline;
    }


    public SpannableTexViewClick(){
        this.color= Color.parseColor("#FCB840");
    }

    /**
     * 设置颜色
     * @param ds
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
        ds.setUnderlineText(isUnderline);//是否禁用下划线
    }

    /**
     * 点击事件的监听
     * @param widget
     */
    @Override
    public void onClick(View widget) {

    }
}
