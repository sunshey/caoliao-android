package com.yc.liaolive.msg.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/5/25
 * list或者其他AbsListView的底部
 */

public class ListViewFooterView extends LinearLayout {

    private TextView mTextView;

    public ListViewFooterView(Context context) {
        super(context);
        init(context,null);
    }

    public ListViewFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_list_msg_footer_layout,this);
        mTextView = (TextView) findViewById(R.id.view_content);
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MsgFooterView);
            String string = typedArray.getString(R.styleable.MsgFooterView_msgFooterContent);
            int color = typedArray.getColor(R.styleable.MsgFooterView_msgFooterContentColor, getContext().getResources().getColor(R.color.common_empty));
            mTextView.setText(string);
            mTextView.setTextColor(color);
            typedArray.recycle();
        }
    }

    public void setContent(String content){
        if(null!=mTextView) mTextView.setText(content);
    }

    public void setContentColor(int color){
        if(null!=mTextView) mTextView.setTextColor(color);
    }
}
