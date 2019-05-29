package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/5/24 8:58
 * GridLayout 现行布局左右边框
 */
public class ItemBothEndsSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public ItemBothEndsSpacesItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left=space;
        outRect.bottom=0;
        outRect.right=space;
        outRect.top=0;
    }
}
