package com.yc.liaolive.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @time 2016/10/24 14:35
 * @des $解决嵌套使用显示不全的控件
 */
public class WrapContentGrideView extends GridView {

    private boolean haveScrollbar = true;
    public WrapContentGrideView(Context context) {
        this(context, null);
    }
    public WrapContentGrideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WrapContentGrideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
     *
     * @param haveScrollbar
     */
    public void setHaveScrollbar(boolean haveScrollbar) {
        this.haveScrollbar = haveScrollbar;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!haveScrollbar) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
