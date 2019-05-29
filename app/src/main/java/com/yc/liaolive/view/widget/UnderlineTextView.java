package com.yc.liaolive.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * Created by wanglin  on 2018/7/2 11:49.
 */
public class UnderlineTextView extends android.support.v7.widget.AppCompatTextView {


    public UnderlineTextView(Context context) {
        super(context);
        setUnderLine();
    }

    public UnderlineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUnderLine();
    }

    public UnderlineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUnderLine();
    }

    private void setUnderLine() {
        TextPaint paint = getPaint();
        paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);

    }


}
