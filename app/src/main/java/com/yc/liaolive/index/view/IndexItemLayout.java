package com.yc.liaolive.index.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yc.liaolive.util.ScreenUtils;

/**
 * TinyHung@Outlook.com
 * 2018/9/1
 * 首页Item，解决Item对齐，边距被裁剪变形的问题
 */

public class IndexItemLayout extends FrameLayout {

    private static final String TAG = "IndexItemLayout";
    private int mTop;
    private int mToPxInt1;
    private int mFileType;
    private int mToPxInt2;

    public IndexItemLayout(Context context) {
        super(context);
        init(context);
    }

    public IndexItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mToPxInt1 = ScreenUtils.dpToPxInt(1.6f);
        mToPxInt2 = ScreenUtils.dpToPxInt(4f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if(mFileType == 1){
            layoutParams.leftMargin=mToPxInt2;
            layoutParams.rightMargin=mToPxInt2;
            layoutParams.topMargin=0;
            layoutParams.bottomMargin=0;
        }else{
            layoutParams.leftMargin=mToPxInt2;
            layoutParams.rightMargin=mToPxInt2;
            layoutParams.topMargin=0;
            layoutParams.bottomMargin=0;
        }
        this.setLayoutParams(layoutParams);
    }

    /**
     * 设置当前所在的位置
     * @param adapterPosition
     * 改变头两个Position的位置
     */
    public void setAdapterPosition(int adapterPosition) {
        if(0==adapterPosition||1==adapterPosition){
            this.mTop=ScreenUtils.dpToPxInt(1.6f);
        }else{
            this.mTop=0;
        }
    }

    public void setFileType(int fileType) {
        this.mFileType=fileType;
    }
}
