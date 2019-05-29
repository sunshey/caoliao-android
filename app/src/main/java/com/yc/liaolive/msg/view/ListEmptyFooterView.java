package com.yc.liaolive.msg.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.yc.liaolive.R;

/**
 * TinyHung@Outlook.com
 * 2018/5/25
 * list或者其他AbsListView的空占位底部
 */

public class ListEmptyFooterView extends LinearLayout {

    public ListEmptyFooterView(Context context) {
        super(context);
        init(context,null);
    }

    public ListEmptyFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.view_list_empty_footer_layout,this);
    }

    /**
     * 设定Empty高度
     * @param height
     */
    public void setEmptyViewHeight(int height){
        View emptyView = findViewById(R.id.view_empty_layout);
        emptyView.getLayoutParams().height=height;
    }

    public void showEmptyView(boolean flag) {
        findViewById(R.id.view_empty_line).setVisibility(flag?VISIBLE:GONE);
    }
}
