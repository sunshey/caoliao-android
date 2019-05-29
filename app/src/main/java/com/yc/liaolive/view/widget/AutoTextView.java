package com.yc.liaolive.view.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TinyHung@Outlook.com
 * 2017/8/30.
 * 首页自动切换文字的View
 */

public class AutoTextView  extends TextView{

    private String[] strings;
    private boolean isAuto=false;
    private int cureenIndex=0;//当前正在显示的文字数组元素下标
    private long mAutoDurtion=5*1000;//默认隔多少秒自动切换

    public AutoTextView(Context context) {
        super(context);
    }

    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    public  static Handler sHandler=new Handler();

    public void setData(String[] strings){
        if(null==strings||strings.length<=0) return;
        this.strings=strings;
    }

    public void setAutoDurtion(long durtion){
        this.mAutoDurtion=durtion;
    }
    /**
     * 自动切换显示文字
     */
    public void startAuto(){
        if(isAuto) return;
        if(null!=strings&&strings.length>0){
            if(null!=sHandler&&null!=goNextPageRunnable){
                sHandler.postDelayed(goNextPageRunnable, mAutoDurtion);
                isAuto=true;
            }
        }
    }

    /**
     * 停止自动轮播
     */
    public void stopAuto(){
        if(!isAuto) return;
        if(null!=sHandler&&null!=goNextPageRunnable){
            sHandler.removeCallbacks(goNextPageRunnable);
            isAuto=false;
        }
    }


    private Runnable goNextPageRunnable = new Runnable() {

        @Override
        public void run() {
            showText();
            sHandler.postDelayed(this, mAutoDurtion);
        }
    };

    /**
     * 显示文字的方法
     */
    private void showText() {
        if (null != strings && strings.length > 0) {
            if (cureenIndex >= strings.length) {
                cureenIndex = 0;//从头开始
            }
            this.setText(strings[cureenIndex]);
            cureenIndex++;
        }
    }

    /**
     * 返回当前正在显示的文字
     * @return
     */
    public String getKey() {
        return this.getText().toString();
    }
}

