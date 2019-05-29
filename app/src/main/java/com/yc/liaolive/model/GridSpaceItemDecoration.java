package com.yc.liaolive.model;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/4/13 13:56
 * 三列水平的网格布局,左右两边屏幕边缘为0，中间平均分配
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration{

    private static final String TAG = GridSpaceItemDecoration.class.getSimpleName();
    private final Context context;
    private final int leftMargin;
    private final int rightMargin;
    private final int topMargin;
    private final int bottomMargin;

    /**
     *
     * @param context
     * @param leftMargin 左间距
     * @param rightMargin 右间距
     * @param topMargin 顶部间距
     * @param bottomMargin 底部间距
     */

    public GridSpaceItemDecoration(Context context,int leftMargin,int rightMargin,int topMargin,int bottomMargin) {
        this.context=context;
        this.leftMargin=leftMargin;
        this.rightMargin=rightMargin;
        this.topMargin=topMargin;
        this.bottomMargin=bottomMargin;
    }
    int cureenIndexItem=0;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //不是第一个的格子都设置左，右，下边距
        outRect.left = leftMargin;
        outRect.bottom = bottomMargin;
        outRect.right = rightMargin;
        outRect.top= topMargin;

        //每行的第一个Item
        if (parent.getChildLayoutPosition(view) %3==0) {
            outRect.left=0;
        }
        //如果是第一行第一列第三个条目，将右边距设置为0
        if(parent.getChildLayoutPosition(view)==2){
            outRect.right= 0;
            cureenIndexItem=2;
        }
        //如果这次的条目减去上一次的条目等于3，那么将这个条目右边距设置为0dp
        if(3==parent.getChildLayoutPosition(view)-cureenIndexItem){
            outRect.right= 0;
            cureenIndexItem=parent.getChildLayoutPosition(view);
        }
    }
}
