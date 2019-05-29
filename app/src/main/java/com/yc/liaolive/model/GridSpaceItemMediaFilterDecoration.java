package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/4/13 13:56
 * 四列垂直的网格布局,左右两边屏幕边缘为中间的双倍
 */
public class GridSpaceItemMediaFilterDecoration extends RecyclerView.ItemDecoration{

    private final int margin;


    public GridSpaceItemMediaFilterDecoration(int margin) {
        this.margin=margin;
    }

    int cureenIndexItem=0;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //不是第一个的格子都设置左，右，下边距
        outRect.left = margin;
        outRect.bottom = margin;
        outRect.right = margin;
        outRect.top= margin;

        //每行的第一个Item
        if (parent.getChildLayoutPosition(view) %4==0) {
            outRect.left+=margin;
        }
        //第一排的顶部全部双倍
        if(parent.getChildLayoutPosition(view)==0|parent.getChildLayoutPosition(view)==1|parent.getChildLayoutPosition(view)==2|parent.getChildLayoutPosition(view)==3){
            outRect.top+=margin;
        }
        //第一排第三个
        if(parent.getChildLayoutPosition(view)==3){
            outRect.right+=margin;
            cureenIndexItem=3;
        }
        //如果这次的条目减去上一次的条目等于3，那么将这个条目右边距设置为双倍
        if(4==parent.getChildLayoutPosition(view)-cureenIndexItem){
            outRect.right+=margin;
            cureenIndexItem=parent.getChildLayoutPosition(view);
        }
    }
}
