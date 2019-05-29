package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Create by yangxueqin on 2019/1/10
 * 竖向线性布局中间间距
 */
public class ItemMiddleSpaceDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public ItemMiddleSpaceDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right=0;
        outRect.bottom=0;
        outRect.left=0;
        if(0==parent.getChildLayoutPosition(view)){
            outRect.top=0;
        }else{
            outRect.top=space;
        }
    }
}
