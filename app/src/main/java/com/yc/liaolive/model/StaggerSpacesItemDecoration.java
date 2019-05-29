package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TinyHung@outlook.com
 * 2017/5/24 8:58
 * GridLayout 两列列表间距
 */
public class StaggerSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = StaggerSpacesItemDecoration.class.getSimpleName();
    private int space;

    public StaggerSpacesItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //左边
        if (parent.getChildLayoutPosition(view) %2==0) {
            outRect.left=(space+space);
            outRect.bottom=space;
            outRect.right=space;
            if(0==parent.getChildLayoutPosition(view)){
                outRect.top=(space+space);
            }else{
                outRect.top=space;
            }
        }else{
            outRect.right=(space+space);
            outRect.bottom=space;
            outRect.left=space;
            if(1==parent.getChildLayoutPosition(view)){
                outRect.top=(space+space);
            }else{
                outRect.top=space;
            }
        }

    }
}
