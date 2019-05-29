package com.yc.liaolive.model;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.yc.liaolive.util.Logger;

/**
 * TinyHung@outlook.com
 * 2017/5/24 8:58
 * GridLayout 直播列表 两边宽度10DP，中间宽度15DP 适合有头部的
 */
public class GridSpacesItemHeaderDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = GridSpacesItemHeaderDecoration.class.getSimpleName();
    private int space;

    public GridSpacesItemHeaderDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //Banner
        if(parent.getChildLayoutPosition(view)==0){
            outRect.left=0;
            outRect.bottom=0;
            outRect.right=0;
            outRect.top=0;
        }else if(parent.getChildLayoutPosition(view)==1){
            outRect.left=space*2;
            outRect.bottom=space;
            outRect.right=space*3/2;
            if(1==parent.getChildLayoutPosition(view)){
                outRect.top=space*2;
            }else{
                outRect.top=space;
            }
        }else if(parent.getChildLayoutPosition(view)==2){
            outRect.right=space*2;
            outRect.bottom=space;
            outRect.left=space*3/2;
            if(2==parent.getChildLayoutPosition(view)){
                outRect.top=space*2;
            }else{
                outRect.top=space;
            }
        }else if (parent.getChildLayoutPosition(view) %2==0) {
            outRect.left=space*2;
            outRect.bottom=space;
            outRect.right=space*3/2;
            if(1==parent.getChildLayoutPosition(view)){
                outRect.top=space*2;
            }else{
                outRect.top=space;
            }
        }else {
            outRect.right=space*2;
            outRect.bottom=space;
            outRect.left=space*3/2;
            if(2==parent.getChildLayoutPosition(view)){
                outRect.top=space*2;
            }else{
                outRect.top=space;
            }
        }
    }
}
