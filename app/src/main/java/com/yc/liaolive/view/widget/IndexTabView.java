package com.yc.liaolive.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/7/24
 * 首页单个Tab
 */

public class IndexTabView extends RelativeLayout {

    private ImageView mViewBtnIcon;
    private TextView mTextView;

    public IndexTabView(Context context) {
        super(context);
        init(context,null);
    }

    public IndexTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_index_tab_layout,this);
        mViewBtnIcon = (ImageView) findViewById(R.id.view_btn_icon);
        mTextView = (TextView) findViewById(R.id.view_btn_text);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndexTabView);
            Drawable drawable = typedArray.getDrawable(R.styleable.IndexTabView_indexTabIcon);
            String title = typedArray.getString(R.styleable.IndexTabView_indexTabText);
            int color = typedArray.getColor(R.styleable.IndexTabView_indexTabTextColor, getContext().getResources().getColor(R.color.black));
            int textSize = typedArray.getInt(R.styleable.IndexTabView_indexTabSize, 12);
            if(null!=drawable) mViewBtnIcon.setImageDrawable(drawable);
            mTextView.setText(title);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            mTextView.setTextColor(color);
            typedArray.recycle();
        }
    }

    /**
     * 设置是否选中
     * @param selected
     */
    public void setTabSelected(boolean selected){
        if(null!=mViewBtnIcon) mViewBtnIcon.setSelected(selected);
        if(null!=mTextView) mTextView.setSelected(selected);
    }
}
