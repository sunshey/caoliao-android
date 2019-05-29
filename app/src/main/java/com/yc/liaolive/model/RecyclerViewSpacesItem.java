package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 这是一个给RecyclerView Item设置边距的类
 * @version 1.0
 * @outhor TingHung@Outlook.com
 * @time 2016-09-09 21:43
 */
public class RecyclerViewSpacesItem extends RecyclerView.ItemDecoration {

    private int space;

    public RecyclerViewSpacesItem(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;
    }
}
